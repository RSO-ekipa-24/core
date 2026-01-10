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

@Path("/properties")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "BearerAuth")
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
     * @return List<PropertyResponse>
     */
    @GET
    @Path("/all")
    @NotNull
    @Operation(
            summary = "Get all properties",
            description = "Returns all properties in the system for public browsing."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "List of all properties",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PropertyResponse.class, type = SchemaType.ARRAY),
                            examples = @ExampleObject(
                                    name = "AllPropertiesList",
                                    value = "[{\"id\":1,\"name\":\"Sunset Villa\",\"description\":\"Beachfront property\",\"tags\":[],\"propertyGroupId\":1}]"
                            )
                    )
            ),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public List<PropertyResponse> getAll() {
        return propertyService.getAllProperties();
    }

    /**
     * Get properties for user.
     *
     * @param userId user id
     * @return List<PropertyResponse>
     */
    @GET
    @Path("/{userId}")
    @NotNull
    @Operation(
            summary = "Get all properties for a user",
            description = "Returns all properties owned by the given user."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "List of properties",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PropertyResponse.class, type = SchemaType.ARRAY),
                            examples = @ExampleObject(
                                    name = "PropertiesList",
                                    value = "[{\"id\":10,\"name\":\"My apartment\",\"description\":\"City center flat\",\"tags\":[{\"id\":1,\"name\":\"Apartment\",\"color\":\"#FFAA00\"},{\"id\":2,\"name\":\"Rent\",\"color\":\"#00AAFF\"}],\"propertyGroupId\":5}]"
                            )
                    )
            ),
            @APIResponse(responseCode = "401", description = "Missing/invalid token"),
            @APIResponse(responseCode = "403", description = "Forbidden"),
            @APIResponse(responseCode = "404", description = "User not found"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public List<PropertyResponse> getUserProperties(
            @Parameter(description = "User id", example = "1", required = true)
            @PathParam("userId") Long userId
    ) {
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
    @Operation(
            summary = "Get properties by tag",
            description = "Returns properties for the authenticated user filtered by tag name."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "List of properties",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PropertyResponse.class, type = SchemaType.ARRAY),
                            examples = @ExampleObject(
                                    name = "PropertiesByTag",
                                    value = "[{\"id\":10,\"name\":\"My apartment\",\"description\":\"City center flat\",\"tags\":[{\"id\":1,\"name\":\"Apartment\",\"color\":\"#FFAA00\"}],\"propertyGroupId\":5}]"
                            )
                    )
            ),
            @APIResponse(responseCode = "400", description = "Invalid tag name"),
            @APIResponse(responseCode = "401", description = "Missing/invalid token"),
            @APIResponse(responseCode = "403", description = "Forbidden"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public List<PropertyResponse> getPropertiesByTag(
            @Parameter(description = "Tag name", example = "Apartment", required = true)
            @PathParam("tagName") String tagName
    ) {
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
    @Path("/property/{id}")
    @NotNull
    @Operation(
            summary = "Get property by id",
            description = "Returns a single property by id for the authenticated user."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Property",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PropertyResponse.class),
                            examples = @ExampleObject(
                                    name = "Property",
                                    value = "{\"id\":10,\"name\":\"My apartment\",\"description\":\"City center flat\",\"tags\":[{\"id\":1,\"name\":\"Apartment\",\"color\":\"#FFAA00\"},{\"id\":2,\"name\":\"Rent\",\"color\":\"#00AAFF\"}],\"propertyGroupId\":5}"
                            )
                    )
            ),
            @APIResponse(responseCode = "401", description = "Missing/invalid token"),
            @APIResponse(responseCode = "403", description = "Forbidden"),
            @APIResponse(responseCode = "404", description = "Property not found"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public PropertyResponse getById(
            @Parameter(description = "Property id", example = "10", required = true)
            @PathParam("id") @NotNull Long id
    ) {
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
    @Operation(
            summary = "Create property",
            description = "Creates a new property for the authenticated user."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Created property",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PropertyResponse.class),
                            examples = @ExampleObject(
                                    name = "CreatedProperty",
                                    value = "{\"id\":10,\"name\":\"My apartment\",\"description\":\"City center flat\",\"tags\":[{\"id\":1,\"name\":\"Apartment\",\"color\":\"#FFAA00\"},{\"id\":2,\"name\":\"Rent\",\"color\":\"#00AAFF\"}],\"propertyGroupId\":5}"
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Validation error (e.g., name/description length, missing tag list, invalid property group id)"
            ),
            @APIResponse(responseCode = "401", description = "Missing/invalid token"),
            @APIResponse(responseCode = "403", description = "Forbidden"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public PropertyResponse create(
            @NotNull
            @Valid
            @Parameter(
                    description = "Property create payload",
                    required = true,
                    examples = @ExampleObject(
                            name = "CreateRequest",
                            value = "{\"name\":\"My apartment\",\"description\":\"City center flat\",\"tags\":[1,2],\"propertyGroupId\":5}"
                    )
            )
            PropertyCreateRequest propertyCreateRequest
    ) {
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
    @Operation(
            summary = "Update property",
            description = "Updates an existing property for the authenticated user."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Updated property",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PropertyResponse.class),
                            examples = @ExampleObject(
                                    name = "UpdatedProperty",
                                    value = "{\"id\":10,\"name\":\"My apartment (updated)\",\"description\":\"City center flat\",\"tags\":[{\"id\":1,\"name\":\"Apartment\",\"color\":\"#FFAA00\"},{\"id\":2,\"name\":\"Rent\",\"color\":\"#00AAFF\"}],\"propertyGroupId\":5}"
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Validation error (e.g., invalid property id, missing tag list, name/description length)"
            ),
            @APIResponse(responseCode = "401", description = "Missing/invalid token"),
            @APIResponse(responseCode = "403", description = "Forbidden"),
            @APIResponse(responseCode = "404", description = "Property not found"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public PropertyResponse update(
            @NotNull
            @Valid
            @Parameter(
                    description = "Property update payload",
                    required = true,
                    examples = @ExampleObject(
                            name = "UpdateRequest",
                            value = "{\"id\":10,\"name\":\"My apartment (updated)\",\"description\":\"City center flat\",\"tags\":[1,2],\"propertyGroupId\":5}"
                    )
            )
            PropertyUpdateRequest propertyUpdateRequest
    ) {
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
    @Operation(
            summary = "Delete property",
            description = "Deletes a property by id for the authenticated user."
    )
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Deleted"),
            @APIResponse(responseCode = "401", description = "Missing/invalid token"),
            @APIResponse(responseCode = "403", description = "Forbidden"),
            @APIResponse(responseCode = "404", description = "Property not found"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public void delete(
            @Parameter(description = "Property id", example = "10", required = true)
            @PathParam("id") @NotNull Long id
    ) {
        Long userId = userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId();
        propertyService.deleteProperty(id, userId);
    }
}