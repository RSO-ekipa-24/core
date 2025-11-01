package essa.dto.tag;

import essa.entity.Tag;
import io.smallrye.common.constraint.NotNull;

public class TagResponse {
    private final String name;
    private final String color;
    private final Long id;

    public TagResponse(
            @NotNull Long id,
            @NotNull String name,
            @NotNull String color
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

    @NotNull
    public static TagResponse fromEntity(@NotNull Tag tag) {
        return new TagResponse(tag.getId(), tag.getName(), tag.getColor());
    }
}