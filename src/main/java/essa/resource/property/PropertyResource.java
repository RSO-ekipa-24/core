package essa.resource.property;

import essa.dto.property.PropertyCreateRequest;
import essa.dto.property.PropertyResponse;
import essa.dto.property.PropertyUpdateRequest;
import essa.service.property.PropertyService;
import essa.service.user.UserService;
import io.smallrye.common.constraint.NotNull;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Path("/properties")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PropertyResource {

    private final PropertyService propertyService;
    private final UserService userService;

    @Inject
    JsonWebToken jwt;

    public PropertyResource(PropertyService propertyService, UserService userService) {
        this.propertyService = propertyService;
        this.userService = userService;
    }

    /**
     * Get all properties.
     *
     * @param userId user id
     * @return List<PropertyResponse>
     */
    @GET
    @Path("/{userId}")
    @NotNull
    public List<PropertyResponse> getUserProperties(@PathParam("userId") Long userId) {
        return propertyService.getUserProperties(userId);
    }

    /**
     * Get all properties by tag id.
     *
     * @param tagName tag name
     * @return List<PropertyResponse>
     */
    @GET
    @Path("/tag/{tagName}")
    public List<PropertyResponse> getPropertiesByTag(@PathParam("tagName") String tagName) {
        Long userId = userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId();
        return propertyService.getByTag(userId, tagName);
    }

    /**
     * Get property by id.
     *
     * @param id id
     * @return PropertyResponse
     */
    @GET
    @Path("/{id}")
    @NotNull
    public PropertyResponse getById(@PathParam("id") @NotNull Long id) {
        Long userId = userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId();
        return propertyService.getPropertyById(id, userId);
    }

    /**
     * Create property.
     *
     * @param propertyCreateRequest property create request
     * @return PropertyResponse
     */
    @POST
    @NotNull
    public PropertyResponse create(@NotNull @Valid PropertyCreateRequest propertyCreateRequest) {
        propertyCreateRequest.setUserId(userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId());
        return propertyService.createProperty(propertyCreateRequest);
    }

    /**
     * Update property.
     *
     * @param propertyUpdateRequest property update request
     * @return PropertyResponse
     */
    @PUT
    @NotNull
    public PropertyResponse update(@NotNull @Valid PropertyUpdateRequest propertyUpdateRequest) {
        propertyUpdateRequest.setUserId(userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId());
        return propertyService.updateProperty(propertyUpdateRequest);
    }

    /**
     * Delete property by id.
     *
     * @param id id
     */
    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") @NotNull Long id) {
        Long userId = userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId();
        propertyService.deleteProperty(id, userId);
    }
}
