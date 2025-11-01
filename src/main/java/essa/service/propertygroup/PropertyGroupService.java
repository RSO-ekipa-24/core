package essa.service.propertygroup;

import essa.dto.property.PropertyResponse;
import essa.dto.propertygroup.PropertyGroupCreateRequest;
import essa.dto.propertygroup.PropertyGroupResponse;
import essa.dto.propertygroup.PropertyGroupUpdateRequest;
import essa.entity.PropertyGroup;
import essa.entity.User;
import essa.exception.EntityNotFoundException;
import essa.repository.propertygroup.PropertyGroupRepository;
import essa.repository.user.UserRepository;
import io.smallrye.common.constraint.NotNull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationScoped
public class PropertyGroupService {

    @Inject
    PropertyGroupRepository propertyGroupRepository;

    @Inject
    UserRepository userRepository;

    /**
     * Get all property groups.
     *
     * @return List<PropertyGroupResponse>
     */
    @NotNull
    public List<PropertyGroupResponse> getUserPropertyGroups(@NotNull Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found.");
        }
        return user.getPropertyGroups().stream()
                .map(PropertyGroupResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Returns properties belonging to a group.
     *
     * @param groupId group id
     * @return List<PropertyResponse>
     */
    @NotNull
    public List<PropertyResponse> getPropertiesOfGroup(@NotNull Long groupId, @NotNull Long userId) {
        PropertyGroup group = propertyGroupRepository.findById(groupId);
        if (group == null || !group.getUser().getId().equals(userId)) {
            throw new EntityNotFoundException("Property group not found.");
        }

        return group.getProperties().stream()
                .map(PropertyResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get property group by id.
     *
     * @param id id
     * @return PropertyGroupResponse
     */
    @NotNull
    public PropertyGroupResponse getPropertyGroupById(@NotNull Long id, @NotNull Long userId) {
        PropertyGroup propertyGroup = propertyGroupRepository.findById(id);
        if (propertyGroup == null || !Objects.equals(propertyGroup.getUser().getId(), userId))
            throw new EntityNotFoundException("Property group with id " + id + " not found.");
        return PropertyGroupResponse.fromEntity(propertyGroup);
    }

    /**
     * Create property group.
     *
     * @param request request
     * @return PropertyGroupResponse
     */
    @Transactional
    @NotNull
    public PropertyGroupResponse createPropertyGroup(@NotNull PropertyGroupCreateRequest request) {
        PropertyGroup propertyGroup = new PropertyGroup();
        propertyGroup.setName(request.getName());

        User user = userRepository.findById(request.getUserId());
        if (user == null) throw new EntityNotFoundException("User not found.");
        propertyGroup.setUser(user);

        propertyGroupRepository.persist(propertyGroup);
        return PropertyGroupResponse.fromEntity(propertyGroup);
    }

    /**
     * Update property group.
     *
     * @param request request
     * @return PropertyGroupResponse
     */
    @Transactional
    @NotNull
    public PropertyGroupResponse updatePropertyGroup(@NotNull PropertyGroupUpdateRequest request) {
        PropertyGroup propertyGroup = propertyGroupRepository.findById(request.getId());
        if (propertyGroup == null || !Objects.equals(propertyGroup.getUser().getId(), request.getUserId()))
            throw new EntityNotFoundException("Property group with id " + request.getId() + " not found.");

        propertyGroup.setName(request.getName());

        propertyGroup = propertyGroupRepository.update(propertyGroup);
        return PropertyGroupResponse.fromEntity(propertyGroup);
    }

    /**
     * Delete property group.
     *
     * @param id id
     */
    @Transactional
    public void deletePropertyGroup(@NotNull Long id, @NotNull Long userId) {
        PropertyGroup propertyGroup = propertyGroupRepository.findById(id);
        if (propertyGroup == null || !Objects.equals(propertyGroup.getUser().getId(), userId))
            throw new EntityNotFoundException("Property group with id " + id + " not found.");
        propertyGroupRepository.delete(propertyGroup);
    }
}