package essa.service.tag;

import essa.dto.tag.TagCreateRequest;
import essa.dto.tag.TagResponse;
import essa.dto.tag.TagUpdateRequest;
import essa.entity.Tag;
import essa.entity.User;
import essa.exception.EntityNotFoundException;
import essa.repository.tags.TagRepository;
import essa.repository.user.UserRepository;
import io.smallrye.common.constraint.NotNull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationScoped
public class TagService {

    @Inject
    TagRepository tagRepository;

    @Inject
    UserRepository userRepository;

    /**
     * Create tag.
     *
     * @param userId  id of the user creating the tag
     * @param request request
     * @return TagResponse
     */
    @Transactional
    public TagResponse create(@NotNull Long userId, @NotNull TagCreateRequest request) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found.");
        }

        Tag tag = new Tag();
        tag.setName(request.getName());
        tag.setUser(user);
        tag.setColor(request.getColor());
        tagRepository.persist(tag);
        return TagResponse.fromEntity(tag);
    }

    /**
     * Update tag.
     *
     * @param request update request
     * @return updated TagResponse
     */
    @Transactional
    public TagResponse update(TagUpdateRequest request) {
        Tag tag = tagRepository.findById(request.getId());
        if (tag == null || !Objects.equals(tag.getUser().getId(), request.getUserId())) {
            throw new EntityNotFoundException("Tag not found");
        }

        tag.setName(request.getName());
        tag.setColor(request.getColor());
        tagRepository.persist(tag);
        return TagResponse.fromEntity(tag);
    }

    /**
     * List tags by user.
     *
     * @param userId user id
     * @return list of tags
     */
    public List<TagResponse> list(@NotNull Long userId) {
        return tagRepository.listByUser(userId).stream()
                .map(TagResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Delete tag.
     *
     * @param id     tag id
     * @param userId id of the user requesting deletion
     */
    @Transactional
    public void delete(@NotNull Long id, @NotNull Long userId) {
        Tag tag = tagRepository.findById(id);
        if (tag == null || !Objects.equals(tag.getUser().getId(), userId)) {
            throw new EntityNotFoundException("Tag not found.");
        }
        tagRepository.delete(tag);
    }
}