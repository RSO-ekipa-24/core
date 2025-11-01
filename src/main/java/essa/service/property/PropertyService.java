package essa.service.property;

import essa.dto.property.PropertyCreateRequest;
import essa.dto.property.PropertyResponse;
import essa.dto.property.PropertyUpdateRequest;
import essa.entity.Property;
import essa.entity.PropertyGroup;
import essa.entity.Tag;
import essa.entity.User;
import essa.exception.EntityNotFoundException;
import essa.repository.property.PropertyRepository;
import essa.repository.propertygroup.PropertyGroupRepository;
import essa.repository.tags.TagRepository;
import essa.repository.user.UserRepository;
import io.smallrye.common.constraint.NotNull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class PropertyService {

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    PropertyGroupRepository propertyGroupRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    TagRepository tagRepository;


    /**
     * Get all properties.
     *
     * @return List<PropertyResponse>
     */
    @NotNull
    public List<PropertyResponse> getUserProperties(@NotNull Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found.");
        }
        return user.getProperties().stream()
                .map(PropertyResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get property by id.
     *
     * @param id id
     * @return PropertyResponse
     */
    @NotNull
    public PropertyResponse getPropertyById(@NotNull Long id, @NotNull Long userId) {
        Property property = propertyRepository.findById(id);
        if (property == null || !Objects.equals(property.getUser().getId(), userId))
            throw new EntityNotFoundException("Property with id " + id + " not found.");
        return PropertyResponse.fromEntity(property);
    }

    /**
     * Get properties by tag
     *
     * @param userId  user id
     * @param tagName tag id
     * @return list of properties
     */
    public List<PropertyResponse> getByTag(@NotNull Long userId, @NotNull String tagName) {
        Tag tag = tagRepository.findByNameAndUser(userId, tagName);
        if (tag == null) {
            throw new EntityNotFoundException("Tag not found");
        }

        return tag.getProperties().stream()
                .map(PropertyResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Create property.
     *
     * @param request request
     * @return PropertyResponse
     */
    @Transactional
    @NotNull
    public PropertyResponse createProperty(@NotNull PropertyCreateRequest request) {
        Property property = new Property();
        property.setName(request.getName());
        property.setDescription(request.getDescription());

        Set<Tag> tags = resolveUserTags(request.getUserId(), request.getTagIds());
        for (Tag t : tags) {
            property.addTag(t);
        }

        User user = userRepository.findById(request.getUserId());
        if (user == null) throw new EntityNotFoundException("User not found.");
        property.setUser(user);

        if (request.getPropertyGroupId() != null) {
            PropertyGroup propertyGroup = propertyGroupRepository.findById(request.getPropertyGroupId());
            if (propertyGroup == null) throw new EntityNotFoundException("Property group not found.");
            property.setPropertyGroup(propertyGroup);
        }

        propertyRepository.persist(property);
        return PropertyResponse.fromEntity(property);
    }

    /**
     * Helper function to resolve all user tags once.
     *
     * @param userId       user id
     * @param requestedIds requested tag ids
     * @return set of tags
     */
    private Set<Tag> resolveUserTags(Long userId, List<Long> requestedIds) {
        List<Tag> userTags = tagRepository.listByUser(userId);

        Map<Long, Tag> tagMap = userTags.stream()
                .collect(Collectors.toMap(Tag::getId, Function.identity()));

        List<Long> ids = requestedIds != null
                ? requestedIds
                : Collections.emptyList();

        Set<Tag> result = new HashSet<>();
        for (Long id : ids) {
            Tag t = tagMap.get(id);
            if (t == null) {
                throw new EntityNotFoundException(
                        "Tag not found: " + id + "."
                );
            }
            result.add(t);
        }
        return result;
    }


    /**
     * Update property.
     *
     * @param request request
     * @return PropertyResponse
     */
    @Transactional
    @NotNull
    public PropertyResponse updateProperty(@NotNull PropertyUpdateRequest request) {
        Property property = propertyRepository.findById(request.getId());
        if (property == null || !Objects.equals(property.getUser().getId(), request.getUserId()))
            throw new EntityNotFoundException("Property with id " + request.getId() + " not found.");

        property.setName(request.getName());
        property.setDescription(request.getDescription());

        if (request.getPropertyGroupId() != null) {
            PropertyGroup propertyGroup = propertyGroupRepository.findById(request.getPropertyGroupId());
            if (propertyGroup == null || !Objects.equals(propertyGroup.getUser().getId(), request.getUserId())) {
                throw new EntityNotFoundException("Property group not found: " + request.getPropertyGroupId());
            }
            property.setPropertyGroup(propertyGroup);
        } else {
            property.setPropertyGroup(null);
        }


        Set<Tag> tags = resolveUserTags(request.getUserId(), request.getTagIds());
        syncTags(property, tags);

        property = propertyRepository.update(property);
        return PropertyResponse.fromEntity(property);
    }

    /**
     * Helper function to sync tags.
     *
     * @param property property
     * @param newTags  new tags
     */
    private void syncTags(Property property, Set<Tag> newTags) {
        List<Tag> toRemove = new ArrayList<>();
        for (Tag existing : property.getTags()) {
            if (!newTags.contains(existing)) {
                toRemove.add(existing);
            }
        }

        for (Tag rem : toRemove) {
            property.removeTag(rem);
        }

        for (Tag t : newTags) {
            if (!property.getTags().contains(t)) {
                property.addTag(t);
            }
        }
    }

    /**
     * Delete property.
     *
     * @param id id
     */
    @Transactional
    @NotNull
    public void deleteProperty(@NotNull Long id, @NotNull Long userId) {
        Property property = propertyRepository.findById(id);
        if (property == null || !Objects.equals(property.getUser().getId(), userId))
            throw new EntityNotFoundException("Property with id " + id + " not found.");
        propertyRepository.delete(property);
    }
}
