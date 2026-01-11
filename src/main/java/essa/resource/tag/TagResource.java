package essa.resource.tag;

import essa.dto.tag.TagCreateRequest;
import essa.dto.tag.TagResponse;
import essa.dto.tag.TagUpdateRequest;
import essa.service.tag.TagService;
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

@Path("/tags")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecurityRequirement(name = "BearerAuth")
public class TagResource {

    private static final String METRIC_PREFIX = "essa.tags";

    @Inject
    TagService tagService;

    @Inject
    UserService userService;

    @Inject
    JsonWebToken jwt;

    /**
     * Get all tags of a user.
     *
     * @return List<TagResponse>
     */
    @GET
    @Timed(value = METRIC_PREFIX + ".list.time", description = "Time spent listing tags")
    @Counted(value = METRIC_PREFIX + ".list.count", description = "Number of calls to list tags")
    @Operation(
            summary = "List tags",
            description = "Returns all tags for the authenticated user."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "List of tags",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TagResponse.class, type = SchemaType.ARRAY)
                    )
            ),
            @APIResponse(responseCode = "401", description = "Missing/invalid token"),
            @APIResponse(responseCode = "403", description = "Forbidden"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public List<TagResponse> list() {
        Long userId = userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId();
        return tagService.list(userId);
    }

    /**
     * Create a tag.
     *
     * @param tagCreateRequest tag create request
     * @return TagResponse
     */
    @POST
    @Timed(value = METRIC_PREFIX + ".create.time", description = "Time spent creating a tag")
    @Counted(value = METRIC_PREFIX + ".create.count", description = "Number of calls to create a tag")
    @Operation(
            summary = "Create tag",
            description = "Creates a new tag for the authenticated user."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Created tag",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TagResponse.class),
                            examples = @ExampleObject(
                                    name = "CreatedTag",
                                    value = "{\"id\":1,\"name\":\"Apartment\",\"color\":\"#FFAA00\"}"
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Validation error (e.g., name too short, color not in hex format)"
            ),
            @APIResponse(responseCode = "401", description = "Missing/invalid token"),
            @APIResponse(responseCode = "403", description = "Forbidden"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public TagResponse create(
            @Valid
            @Parameter(
                    description = "Tag create payload",
                    required = true,
                    examples = @ExampleObject(
                            name = "CreateRequest",
                            value = "{\"name\":\"Apartment\",\"color\":\"#FFAA00\"}"
                    )
            )
            TagCreateRequest tagCreateRequest
    ) {
        Long userId = userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId();
        return tagService.create(userId, tagCreateRequest);
    }

    /**
     * Update a tag.
     *
     * @param tagUpdateRequest tag update request
     * @return TagResponse
     */
    @PUT
    @Timed(value = METRIC_PREFIX + ".update.time", description = "Time spent updating a tag")
    @Counted(value = METRIC_PREFIX + ".update.count", description = "Number of calls to update a tag")
    @Operation(
            summary = "Update tag",
            description = "Updates an existing tag for the authenticated user."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Updated tag",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TagResponse.class),
                            examples = @ExampleObject(
                                    name = "UpdatedTag",
                                    value = "{\"id\":1,\"name\":\"Apartment (updated)\",\"color\":\"#00AAFF\"}"
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Validation error (e.g., name too short, color not in hex format)"
            ),
            @APIResponse(responseCode = "401", description = "Missing/invalid token"),
            @APIResponse(responseCode = "403", description = "Forbidden"),
            @APIResponse(responseCode = "404", description = "Tag not found"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public TagResponse update(
            @NotNull
            @Valid
            @Parameter(
                    description = "Tag update payload",
                    required = true,
                    examples = @ExampleObject(
                            name = "UpdateRequest",
                            value = "{\"id\":1,\"name\":\"Apartment (updated)\",\"color\":\"#00AAFF\"}"
                    )
            )
            TagUpdateRequest tagUpdateRequest
    ) {
        tagUpdateRequest.setUserId(userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId());
        return tagService.update(tagUpdateRequest);
    }

    /**
     * Delete tag.
     *
     * @param id id
     */
    @DELETE
    @Path("/{id}")
    @Timed(value = METRIC_PREFIX + ".delete.time", description = "Time spent deleting a tag")
    @Counted(value = METRIC_PREFIX + ".delete.count", description = "Number of calls to delete a tag")
    @Operation(
            summary = "Delete tag",
            description = "Deletes a tag by id for the authenticated user."
    )
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Deleted"),
            @APIResponse(responseCode = "401", description = "Missing/invalid token"),
            @APIResponse(responseCode = "403", description = "Forbidden"),
            @APIResponse(responseCode = "404", description = "Tag not found"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    public void delete(
            @Parameter(description = "Tag id", example = "1", required = true)
            @PathParam("id") Long id
    ) {
        Long userId = userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId();
        tagService.delete(id, userId);
    }
}