package essa.dto.propertygroup;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import essa.validation.propertygroupidvalidator.ValidPropertyGroupId;
import io.smallrye.common.constraint.NotNull;
import io.smallrye.common.constraint.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PropertyGroupUpdateRequest {

    @NotBlank(message = "Name is required.")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters.")
    private final String name;

    @jakarta.validation.constraints.NotNull(message = "Id is required.")
    @ValidPropertyGroupId()
    private final Long id;

    private Long userId;

    @JsonbCreator
    public PropertyGroupUpdateRequest(
            @JsonbProperty("id") @NotNull Long id,
            @JsonbProperty("name") @NotNull String name
    ) {
        this.name = name;
        this.id = id;
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
    public Long getUserId() {
        return userId;
    }

    public void setUserId(@NotNull Long userId) {
        this.userId = userId;
    }
}
