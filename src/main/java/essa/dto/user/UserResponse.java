package essa.dto.user;

import essa.entity.User;
import io.smallrye.common.constraint.NotNull;

public class UserResponse {
    private final String username;
    private final String email;

    public UserResponse(@NotNull String username, @NotNull String email) {
        this.username = username;
        this.email = email;
    }

    @NotNull
    public String getUsername() {
        return username;
    }

    @NotNull
    public String getEmail() {
        return email;
    }

    @NotNull
    public static UserResponse fromEntity(@NotNull User user) {
        return new UserResponse(user.getUsername(), user.getEmail());
    }
}
