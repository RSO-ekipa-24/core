package essa.dto.property;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import essa.validation.propertygroupidvalidator.ValidPropertyGroupId;
import io.smallrye.common.constraint.NotNull;
import io.smallrye.common.constraint.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class PropertyCreateRequest {

    @NotBlank(message = "Name is required.")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters.")
    private final String name;

    @Size(min = 3, max = 500, message = "Description must be between 3 and 500 characters.")
    private final String description;

    @ValidPropertyGroupId
    private final Long propertyGroupId;

    //TODO add validators that tag exists for user
    @jakarta.validation.constraints.NotNull(message = "Tag list is required.")
    private final List<Long> tagIds;

    private Long userId;

    @JsonbCreator
    public PropertyCreateRequest(
            @JsonbProperty("name") @NotNull String name,
            @JsonbProperty("description") @Nullable String description,
            @JsonbProperty("tags") @NotNull List<Long> tagIds,
            @JsonbProperty("propertyGroupId") @Nullable Long propertyGroupId
    ) {
        this.name = name;
        this.description = description;
        this.tagIds = tagIds;
        this.propertyGroupId = propertyGroupId;
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
    public List<Long> getTagIds() {
        return tagIds;
    }

    @Nullable
    public Long getPropertyGroupId() {
        return propertyGroupId;
    }

    @Nullable
    public Long getUserId() {
        return userId;
    }

    public void setUserId(@NotNull Long userId) {
        this.userId = userId;
    }
}
