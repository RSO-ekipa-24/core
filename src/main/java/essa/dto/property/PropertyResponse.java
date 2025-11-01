package essa.dto.property;

import essa.dto.tag.TagResponse;
import essa.entity.Property;
import io.smallrye.common.constraint.NotNull;
import io.smallrye.common.constraint.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class PropertyResponse {
    private final Long id;
    private final String name;
    private final String description;
    private final List<TagResponse> tags;
    private final Long propertyGroupId;

    public PropertyResponse(
            @NotNull Long id,
            @NotNull String name,
            @Nullable String description,
            @NotNull List<TagResponse> tags,
            @Nullable Long propertyGroupId
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.propertyGroupId = propertyGroupId;
    }

    @NotNull
    public Long getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    @NotNull
    public List<TagResponse> getTags() {
        return tags;
    }

    @Nullable
    public Long getPropertyGroupId() {
        return propertyGroupId;
    }

    @NotNull
    public static PropertyResponse fromEntity(@NotNull Property property) {
        List<TagResponse> tagResponses = property.getTags().stream()
                .map(TagResponse::fromEntity)
                .collect(Collectors.toList());
        Long groupId = property.getPropertyGroup() != null
                ? property.getPropertyGroup().getId()
                : null;
        return new PropertyResponse(
                property.getId(),
                property.getName(),
                property.getDescription(),
                tagResponses,
                groupId
        );
    }
}
