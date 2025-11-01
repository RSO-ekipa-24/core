package essa.dto.tag;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import io.smallrye.common.constraint.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TagCreateRequest {

    @NotBlank(message = "Name is required.")
    @Size(min = 3, max = 50, message = "Tag must be between 3 and 50 characters.")
    private final String name;

    @NotBlank(message = "Color is required.")
    @Pattern(regexp = "^#[0-9a-fA-F]{6}$", message = "Color must be in hex format (#RRGGBB)")
    private final String color;

    @JsonbCreator
    public TagCreateRequest(
            @JsonbProperty("name") @NotNull String name,
            @JsonbProperty("color") @NotNull String color
    ) {
        this.name = name;
        this.color = color;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getColor() {
        return color;
    }
}
