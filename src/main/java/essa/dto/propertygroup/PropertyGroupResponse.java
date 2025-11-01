package essa.dto.propertygroup;

import essa.entity.PropertyGroup;
import io.smallrye.common.constraint.NotNull;

public class PropertyGroupResponse {
    private final Long id;
    private final String name;

    public PropertyGroupResponse(
            @NotNull Long id,
            @NotNull String name
    ) {
        this.id = id;
        this.name = name;
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
    public static PropertyGroupResponse fromEntity(@NotNull PropertyGroup group) {
        return new PropertyGroupResponse(
                group.getId(),
                group.getName()
        );
    }
}
