package essa.dto.user;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import io.smallrye.common.constraint.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserCreateRequest {

    @NotBlank(message = "Username is required.")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters.")
    private final String username;

    @NotBlank(message = "Email is required.")
    @Size(min = 10, max = 100, message = "Email must be between 10 and 100 characters.")
    private final String email;

    @NotBlank(message = "Keycloak id is required.")
    @Size(min = 3, max = 100, message = "Keycloak id must be between 3 and 50 characters.")
    private final String keycloakId;

    @JsonbCreator
    public UserCreateRequest(
            @JsonbProperty("username") @NotNull String username,
            @JsonbProperty("email") @NotNull String email,
            @JsonbProperty("keycloakId") @NotNull String keycloakId
    ) {
        this.username = username;
        this.email = email;
        this.keycloakId = keycloakId;
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
    public String getKeycloakId() {
        return keycloakId;
    }

}
