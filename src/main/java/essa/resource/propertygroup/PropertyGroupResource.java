package essa.resource.propertygroup;


import essa.dto.property.PropertyResponse;
import essa.dto.propertygroup.PropertyGroupCreateRequest;
import essa.dto.propertygroup.PropertyGroupResponse;
import essa.dto.propertygroup.PropertyGroupUpdateRequest;
import essa.service.propertygroup.PropertyGroupService;
import essa.service.user.UserService;
import io.smallrye.common.constraint.NotNull;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Path("/property-groups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PropertyGroupResource {

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
    @Path("/{userId}")
    @NotNull
    public List<PropertyGroupResponse> getUserPropertyGroups(@PathParam("userId") Long userId) {
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
    public List<PropertyResponse> getPropertiesOfGroup(@PathParam("id") @NotNull Long propertyGroupId) {
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
    public PropertyGroupResponse getById(@PathParam("id") @NotNull Long id) {
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
    public PropertyGroupResponse create(@NotNull @Valid PropertyGroupCreateRequest propertyGroupCreateRequest) {
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
    public PropertyGroupResponse update(@NotNull @Valid PropertyGroupUpdateRequest propertyGroupUpdateRequest) {
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
    public void delete(@PathParam("id") @NotNull Long id) {
        Long userId = userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId();
        propertyGroupService.deletePropertyGroup(id, userId);
    }
}
