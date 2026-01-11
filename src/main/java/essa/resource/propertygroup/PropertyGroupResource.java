package essa.resource.propertygroup;

import essa.dto.property.PropertyResponse;
import essa.dto.propertygroup.PropertyGroupCreateRequest;
import essa.dto.propertygroup.PropertyGroupResponse;
import essa.dto.propertygroup.PropertyGroupUpdateRequest;
import essa.service.propertygroup.PropertyGroupService;
import essa.service.user.UserService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.smallrye.common.constraint.NotNull;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
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

import java.util.List;

@Path("/property-groups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "BearerAuth")
public class PropertyGroupResource {

    private static final String METRIC_PREFIX = "essa.propertyGroups";

    private final PropertyGroupService propertyGroupService;
    private final UserService userService;

    @Inject
    JsonWebToken jwt;

    public PropertyGroupResource(PropertyGroupService propertyGroupService, UserService userService) {
        this.propertyGroupService = propertyGroupService;
        this.userService = userService;
    }

    /**
     * Get all property groups of a user.
     *
     * @param userId user id
     * @return @return List<PropertyGroupResponse>
     */
    @GET
    @Path("/for-user/{userId}")
    @NotNull
    @Timed(value = METRIC_PREFIX + ".getUserPropertyGroups.time", description = "Time spent returning property groups for a user")
    @Counted(value = METRIC_PREFIX + ".getUserPropertyGroups.count", description = "Number of calls to get property groups for a user")
    @Operation(
            summary = "Get all property groups for a user",
            description = "Returns all property groups owned by the given user."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "List of property groups",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PropertyGroupResponse.class, type = SchemaType.ARRAY)
                    )
            ),
            @APIResponse(responseCode = "401", description = "Missing/invalid token"),
            @APIResponse(responseCode = "403", description = "Forbidden"),
            @APIResponse(responseCode = "404", description = "User not found"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public List<PropertyGroupResponse> getUserPropertyGroups(
            @Parameter(description = "User id", example = "1", required = true)
            @PathParam("userId") Long userId
    ) {
        return propertyGroupService.getUserPropertyGroups(userId);
    }

    /**
     * Get all properties belonging to a group.
     *
     * @param propertyGroupId property group id
     * @return List<PropertyResponse>
     */
    @GET
    @Path("/{id}/properties")
    @NotNull
    @Timed(value = METRIC_PREFIX + ".getPropertiesOfGroup.time", description = "Time spent returning properties of a group")
    @Counted(value = METRIC_PREFIX + ".getPropertiesOfGroup.count", description = "Number of calls to get properties of a group")
    @Operation(
            summary = "Get properties of a property group",
            description = "Returns all properties belonging to the given property group for the authenticated user."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "List of properties",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PropertyResponse.class, type = SchemaType.ARRAY)
                    )
            ),
            @APIResponse(responseCode = "401", description = "Missing/invalid token"),
            @APIResponse(responseCode = "403", description = "Forbidden"),
            @APIResponse(responseCode = "404", description = "Property group not found"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public List<PropertyResponse> getPropertiesOfGroup(
            @Parameter(description = "Property group id", example = "5", required = true)
            @PathParam("id") @NotNull Long propertyGroupId
    ) {
        Long userId = userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId();
        return propertyGroupService.getPropertiesOfGroup(propertyGroupId, userId);
    }

    /**
     * Get property group by id.
     *
     * @param id id
     * @return PropertyGroupResponse
     */
    @GET
    @Path("/{id}")
    @NotNull
    @Timed(value = METRIC_PREFIX + ".getById.time", description = "Time spent returning property group by id")
    @Counted(value = METRIC_PREFIX + ".getById.count", description = "Number of calls to get property group by id")
    @Operation(
            summary = "Get property group by id",
            description = "Returns a single property group by id for the authenticated user."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Property group",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PropertyGroupResponse.class)
                    )
            ),
            @APIResponse(responseCode = "401", description = "Missing/invalid token"),
            @APIResponse(responseCode = "403", description = "Forbidden"),
            @APIResponse(responseCode = "404", description = "Property group not found"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public PropertyGroupResponse getById(
            @Parameter(description = "Property group id", example = "5", required = true)
            @PathParam("id") @NotNull Long id
    ) {
        Long userId = userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId();
        return propertyGroupService.getPropertyGroupById(id, userId);
    }

    /**
     * Create property group.
     *
     * @param propertyGroupCreateRequest property group create request
     * @return PropertyGroupResponse
     */
    @POST
    @NotNull
    @Timed(value = METRIC_PREFIX + ".create.time", description = "Time spent creating a property group")
    @Counted(value = METRIC_PREFIX + ".create.count", description = "Number of calls to create a property group")
    @Operation(
            summary = "Create property group",
            description = "Creates a new property group for the authenticated user."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Created property group",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PropertyGroupResponse.class),
                            examples = @ExampleObject(
                                    name = "CreatedPropertyGroup",
                                    value = "{\"id\":5,\"name\":\"Favorites\"}"
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Validation error (e.g., name too short, missing required fields)"
            ),
            @APIResponse(responseCode = "401", description = "Missing/invalid token"),
            @APIResponse(responseCode = "403", description = "Forbidden"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public PropertyGroupResponse create(
            @NotNull
            @Valid
            @Parameter(
                    description = "Property group create payload",
                    required = true,
                    examples = @ExampleObject(
                            name = "CreateRequest",
                            value = "{\"name\":\"Favorites\"}"
                    )
            )
            PropertyGroupCreateRequest propertyGroupCreateRequest
    ) {
        propertyGroupCreateRequest.setUserId(userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId());
        return propertyGroupService.createPropertyGroup(propertyGroupCreateRequest);
    }

    /**
     * Update property group.
     *
     * @param propertyGroupUpdateRequest property group update request
     * @return PropertyGroupResponse
     */
    @PUT
    @NotNull
    @Timed(value = METRIC_PREFIX + ".update.time", description = "Time spent updating a property group")
    @Counted(value = METRIC_PREFIX + ".update.count", description = "Number of calls to update a property group")
    @Operation(
            summary = "Update property group",
            description = "Updates an existing property group for the authenticated user."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Updated property group",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PropertyGroupResponse.class),
                            examples = @ExampleObject(
                                    name = "UpdatedPropertyGroup",
                                    value = "{\"id\":5,\"name\":\"Favorites (updated)\"}"
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Validation error (e.g., name too short, invalid property group id)"
            ),
            @APIResponse(responseCode = "401", description = "Missing/invalid token"),
            @APIResponse(responseCode = "403", description = "Forbidden"),
            @APIResponse(responseCode = "404", description = "Property group not found"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public PropertyGroupResponse update(
            @NotNull
            @Valid
            @Parameter(
                    description = "Property group update payload",
                    required = true,
                    examples = @ExampleObject(
                            name = "UpdateRequest",
                            value = "{\"id\":5,\"name\":\"Favorites (updated)\"}"
                    )
            )
            PropertyGroupUpdateRequest propertyGroupUpdateRequest
    ) {
        propertyGroupUpdateRequest.setUserId(userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId());
        return propertyGroupService.updatePropertyGroup(propertyGroupUpdateRequest);
    }

    /**
     * Delete property group by id.
     *
     * @param id id
     */
    @DELETE
    @Path("/{id}")
    @Timed(value = METRIC_PREFIX + ".delete.time", description = "Time spent deleting a property group")
    @Counted(value = METRIC_PREFIX + ".delete.count", description = "Number of calls to delete a property group")
    @Operation(
            summary = "Delete property group",
            description = "Deletes a property group by id for the authenticated user."
    )
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Deleted"),
            @APIResponse(responseCode = "401", description = "Missing/invalid token"),
            @APIResponse(responseCode = "403", description = "Forbidden"),
            @APIResponse(responseCode = "404", description = "Property group not found"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public void delete(
            @Parameter(description = "Property group id", example = "5", required = true)
            @PathParam("id") @NotNull Long id
    ) {
        Long userId = userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId();
        propertyGroupService.deletePropertyGroup(id, userId);
    }
}