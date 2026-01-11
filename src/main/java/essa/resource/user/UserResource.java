package essa.resource.user;

import essa.dto.property.PropertyResponse;
import essa.dto.propertygroup.PropertyGroupResponse;
import essa.dto.user.UserCreateRequest;
import essa.dto.user.UserResponse;
import essa.keycloak.KeycloakAdminProvider;
import essa.keycloak.KeycloakUserResponse;
import essa.service.property.PropertyService;
import essa.service.propertygroup.PropertyGroupService;
import essa.service.user.UserService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.smallrye.common.constraint.NotNull;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "BearerAuth")
public class UserResource {

    private static final String METRIC_PREFIX = "essa.users";

    @Inject
    UserService userService;

    @Inject
    PropertyService propertyService;

    @Inject
    PropertyGroupService propertyGroupService;

    @Inject
    KeycloakAdminProvider keycloakAdminProvider;

    @Inject
    @ConfigProperty(name = "oidc-client.realm")
    String realm;

    @Inject
    JsonWebToken jwt;

    /**
     * Get user.
     *
     * @return List<UserResponse>
     */
    @GET
    @NotNull
    @Timed(value = METRIC_PREFIX + ".get.time", description = "Time spent returning authenticated user")
    @Counted(value = METRIC_PREFIX + ".get.count", description = "Number of calls to get authenticated user")
    @Operation(
            summary = "Get authenticated user",
            description = "Returns the user for the provided access token (subject)."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "User",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class),
                            examples = @ExampleObject(
                                    name = "User",
                                    value = "{\"username\":\"john\",\"email\":\"john@example.com\"}"
                            )
                    )
            ),
            @APIResponse(responseCode = "401", description = "Missing/invalid token"),
            @APIResponse(responseCode = "403", description = "Forbidden"),
            @APIResponse(responseCode = "404", description = "User not found"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public UserResponse get() {
        return userService.getUserByKeycloakId(jwt.getSubject());
    }

    /**
     * Authenticates/registers user and always returns UserResponse if successful
     *
     * @return UserResponse for authenticated users
     * @throws WebApplicationException if authentication fails
     */
    @POST
    @Path("/authenticate")
    @NotNull
    @Timed(value = METRIC_PREFIX + ".authenticate.time", description = "Time spent authenticating/registering user")
    @Counted(value = METRIC_PREFIX + ".authenticate.count", description = "Number of calls to authenticate/register user")
    @Operation(
            summary = "Authenticate/register user",
            description = "Registers the user in the application database if missing and returns the user. Uses token claims (subject, preferred_username, email)."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Authenticated/registered user",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class),
                            examples = @ExampleObject(
                                    name = "User",
                                    value = "{\"username\":\"john\",\"email\":\"john@example.com\"}"
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "401",
                    description = "Invalid token: missing subject/username/email"
            ),
            @APIResponse(responseCode = "403", description = "Forbidden"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public UserResponse authenticateUser() {
        // Get Keycloak ID (subject) from token
        String keycloakId = jwt.getSubject();
        if (keycloakId == null || keycloakId.isEmpty()) {
            throw new WebApplicationException("Invalid token: missing subject",
                    Response.Status.UNAUTHORIZED);
        }

        // Get username from token claims
        String username = jwt.getClaim("preferred_username");
        if (username == null || username.isEmpty()) {
            throw new WebApplicationException("Token missing username",
                    Response.Status.UNAUTHORIZED);
        }

        // Get email from token claims
        String email = jwt.getClaim("email");
        if (email == null || email.isEmpty()) {
            throw new WebApplicationException("Token missing username",
                    Response.Status.UNAUTHORIZED);
        }

        // Check if user already exists in your system
        try {
            UserResponse existingUser = userService.getUserByKeycloakId(keycloakId);
            if (existingUser != null) {
                return existingUser;
            }
        } catch (Exception e) {

        }

        // Create new user with info from token
        @Valid UserCreateRequest request = new UserCreateRequest(
                username,
                email,
                keycloakId
        );

        UserResponse userResponse = userService.createUser(request);
        userService.publishUserCreated(userResponse);

        return userResponse;
    }

    /**
     * Delete user by id (SHOULD BE CALLED BEFORE DELETING ON KEYCLOAK).
     */
    @DELETE
    @Timed(value = METRIC_PREFIX + ".delete.time", description = "Time spent deleting authenticated user")
    @Counted(value = METRIC_PREFIX + ".delete.count", description = "Number of calls to delete authenticated user")
    @Operation(
            summary = "Delete authenticated user",
            description = "Deletes the authenticated user from the application database (should be called before deleting the user in Keycloak)."
    )
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Deleted"),
            @APIResponse(responseCode = "401", description = "Missing/invalid token"),
            @APIResponse(responseCode = "403", description = "Forbidden"),
            @APIResponse(responseCode = "404", description = "User not found"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public void delete() {
        userService.deleteUser(userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId());
    }

    /**
     * Get all property groups belonging to a user.
     *
     * @return List<PropertyGroupResponse>
     */
    @GET
    @Path("/property-groups")
    @NotNull
    @Timed(value = METRIC_PREFIX + ".getUserPropertyGroups.time", description = "Time spent returning authenticated user's property groups")
    @Counted(value = METRIC_PREFIX + ".getUserPropertyGroups.count", description = "Number of calls to get authenticated user's property groups")
    @Operation(
            summary = "Get authenticated user's property groups",
            description = "Returns all property groups for the authenticated user."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "List of property groups",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PropertyGroupResponse.class, type = SchemaType.ARRAY),
                            examples = @ExampleObject(
                                    name = "PropertyGroups",
                                    value = "[{\"id\":5,\"name\":\"Favorites\"}]"
                            )
                    )
            ),
            @APIResponse(responseCode = "401", description = "Missing/invalid token"),
            @APIResponse(responseCode = "403", description = "Forbidden"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public List<PropertyGroupResponse> getUserPropertyGroups() {
        return propertyGroupService.getUserPropertyGroups(userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId());
    }

    /**
     * Get all properties belonging to a user.
     *
     * @return List<PropertyResponse>
     */
    @GET
    @Path("/properties")
    @NotNull
    @Timed(value = METRIC_PREFIX + ".getUserProperties.time", description = "Time spent returning authenticated user's properties")
    @Counted(value = METRIC_PREFIX + ".getUserProperties.count", description = "Number of calls to get authenticated user's properties")
    @Operation(
            summary = "Get authenticated user's properties",
            description = "Returns all properties for the authenticated user."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "List of properties",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PropertyResponse.class, type = SchemaType.ARRAY),
                            examples = @ExampleObject(
                                    name = "Properties",
                                    value = "[{\"id\":10,\"name\":\"My apartment\",\"description\":\"City center flat\",\"tags\":[{\"id\":1,\"name\":\"Apartment\",\"color\":\"#FFAA00\"},{\"id\":2,\"name\":\"Rent\",\"color\":\"#00AAFF\"}],\"propertyGroupId\":5}]"
                            )
                    )
            ),
            @APIResponse(responseCode = "401", description = "Missing/invalid token"),
            @APIResponse(responseCode = "403", description = "Forbidden"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public List<PropertyResponse> getUserProperties() {
        return propertyService.getUserProperties(userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId());
    }

    //keycloak external endpoints for testing under this TODO REMOVE
    @GET
    @Path("/keycloak/{username}")
    @NotNull
    @Timed(value = METRIC_PREFIX + ".getKeycloakUser.time", description = "Time spent fetching Keycloak user by username (testing)")
    @Counted(value = METRIC_PREFIX + ".getKeycloakUser.count", description = "Number of calls to fetch Keycloak user by username (testing)")
    @Operation(
            summary = "Get Keycloak user by username (testing)",
            description = "Testing endpoint that queries Keycloak for a user by username."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Keycloak user",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = KeycloakUserResponse.class)
                    )
            ),
            @APIResponse(responseCode = "401", description = "Missing/invalid token"),
            @APIResponse(responseCode = "403", description = "Forbidden"),
            @APIResponse(responseCode = "404", description = "User not found"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public KeycloakUserResponse getKeycloakUser(
            @Parameter(description = "Keycloak username", example = "john", required = true)
            @PathParam("username") @NotNull String username
    ) {
        return userService.getKeycloakUser(username);
    }

    @GET
    @Path("/me")
    @Timed(value = METRIC_PREFIX + ".getCurrentUser.time", description = "Time spent returning current Keycloak user via admin client")
    @Counted(value = METRIC_PREFIX + ".getCurrentUser.count", description = "Number of calls to return current Keycloak user via admin client")
    @Operation(
            summary = "Get current Keycloak user (admin client)",
            description = "Returns the current user from Keycloak using the Keycloak admin client."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "User",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class),
                            examples = @ExampleObject(
                                    name = "User",
                                    value = "{\"username\":\"john\",\"email\":\"john@example.com\"}"
                            )
                    )
            ),
            @APIResponse(responseCode = "401", description = "Missing/invalid token"),
            @APIResponse(responseCode = "403", description = "Forbidden"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public UserResponse getCurrentUser() {
        String userId = jwt.getSubject();
        try (Keycloak keycloak = keycloakAdminProvider.getKeycloakClient()) {
            UserRepresentation user = keycloak.realm("quarkus")
                    .users().get(userId).toRepresentation();
            return new UserResponse(user.getUsername(), user.getEmail());
        }
    }
}