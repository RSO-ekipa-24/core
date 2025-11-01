package essa.dto.propertygroup;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import io.smallrye.common.constraint.NotNull;
import io.smallrye.common.constraint.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PropertyGroupCreateRequest {

    @NotBlank(message = "Name is required.")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters.")
    private final String name;

    private Long userId;

    @JsonbCreator
    public PropertyGroupCreateRequest(
            @JsonbProperty("name") @NotNull String name
    ) {
        this.name = name;
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
