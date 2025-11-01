package essa.resource.tag;

import essa.dto.tag.TagCreateRequest;
import essa.dto.tag.TagResponse;
import essa.dto.tag.TagUpdateRequest;
import essa.service.tag.TagService;
import essa.service.user.UserService;
import io.smallrye.common.constraint.NotNull;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Path("/tags")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TagResource {
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
    public TagResponse create(@Valid TagCreateRequest tagCreateRequest) {
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
    public TagResponse update(@NotNull @Valid TagUpdateRequest tagUpdateRequest) {
        tagUpdateRequest.setUserId(userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId());
        return tagService.update(tagUpdateRequest);
    }

    /**
     * Delete tag.
     * @param id id
     */
    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") Long id) {
        Long userId = userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId();
        tagService.delete(id, userId);
    }
}