package essa.dto.tag;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import io.smallrye.common.constraint.NotNull;
import io.smallrye.common.constraint.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TagUpdateRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private final String name;

    @NotBlank(message = "Color is required")
    @Pattern(regexp = "^#[0-9a-fA-F]{6}$", message = "Color must be in hex format (#RRGGBB)")
    private final String color;

    @NotNull()
    private final Long id;

    private Long userId;

    @JsonbCreator
    public TagUpdateRequest(
            @JsonbProperty("id") @NotNull Long id,
            @JsonbProperty("name") @NotNull String name,
            @JsonbProperty("color") @NotNull String color
    ) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    @NotNull
    public Long getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getColor() {
        return color;
    }

    @Nullable
    public Long getUserId() {
        return userId;
    }

    public void setUserId(@NotNull Long userId) {
        this.userId = userId;
    }
}