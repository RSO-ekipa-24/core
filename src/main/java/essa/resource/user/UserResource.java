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
import io.smallrye.common.constraint.NotNull;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

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
    public List<PropertyResponse> getUserProperties() {
        return propertyService.getUserProperties(userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId());
    }

    //keycloak external endpoints for testing under this TODO REMOVE
    @GET
    @Path("/keycloak/{username}")
    @NotNull
    public KeycloakUserResponse getKeycloakUser(@PathParam("username") @NotNull String username) {
        return userService.getKeycloakUser(username);
    }

    @GET
    @Path("/me")
    public UserResponse getCurrentUser() {
        String userId = jwt.getSubject();
        try (Keycloak keycloak = keycloakAdminProvider.getKeycloakClient()) {
            UserRepresentation user = keycloak.realm("quarkus")
                    .users().get(userId).toRepresentation();
            return new UserResponse(user.getUsername(), user.getEmail());
        }
    }
}
