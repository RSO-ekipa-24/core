package essa.dto.user;

import io.smallrye.common.constraint.NotNull;

public class UserInternalResponse {
    private final Long id;
    private final String keycloakId;
    private final String username;
    private final String email;

    public UserInternalResponse(
            @NotNull Long id,
            @NotNull String keycloakId,
            @NotNull String username,
            @NotNull String email
    ) {
        this.id = id;
        this.keycloakId = keycloakId;
        this.username = username;
        this.email = email;
    }

    @NotNull
    public Long getId() {
        return id;
    }

    @NotNull
    public String getKeycloakId() {
        return keycloakId;
    }

    @NotNull
    public String getUsername() {
        return username;
    }

    @NotNull
    public String getEmail() {
        return email;
    }

}
