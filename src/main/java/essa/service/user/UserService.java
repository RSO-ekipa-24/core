package essa.service.user;

import essa.dto.user.UserCreateRequest;
import essa.dto.user.UserInternalResponse;
import essa.dto.user.UserResponse;
import essa.entity.User;
import essa.exception.EntityNotFoundException;
import essa.keycloak.KeycloakAdminProvider;
import essa.keycloak.KeycloakUserResponse;
import essa.repository.user.UserRepository;
import io.smallrye.common.constraint.NotNull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import essa.messaging.UserCreatedEvent;
import essa.messaging.UserEventPublisher;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserService {

    @Inject
    KeycloakAdminProvider keycloakAdminProvider;

    @Inject
    UserRepository userRepository;

    @Inject
    UserEventPublisher userEventPublisher;

    @Inject
    @ConfigProperty(name = "oidc-client.realm")
    String realm;

    /**
     * Get all users.
     */
    @NotNull
    public List<UserResponse> getAllUsers() {
        return userRepository.listAll().stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get user by keycloak id.
     */
    @NotNull
    public UserResponse getUserByKeycloakId(@NotNull String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId);
        if (user == null) {
            throw new EntityNotFoundException("User not found.");
        }
        return UserResponse.fromEntity(user);
    }

    /**
     * Get user entity by keycloak id.
     */
    @NotNull
    public User getUserEntityByKeycloakId(@NotNull String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId);
        if (user == null) {
            throw new EntityNotFoundException("User not found.");
        }
        return user;
    }


    /**
     * Get user by keycloak id internally.
     */
    @NotNull
    public UserInternalResponse getUserByKeycloakIdInternal(@NotNull String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId);
        if (user == null) {
            throw new EntityNotFoundException("User not found.");
        }
        return new UserInternalResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getKeycloakId()
        );
    }

    /**
     * Create user.
     */
    @Transactional
    @NotNull
    public UserResponse createUser(@NotNull UserCreateRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setKeycloakId(request.getKeycloakId());
        user.setEmail(request.getEmail());
        userRepository.persist(user);

        return UserResponse.fromEntity(user);
    }

    /**
     * Delete user.
     */
    @Transactional
    @NotNull
    public void deleteUser(@NotNull Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new EntityNotFoundException("User not found.");
        }
        userRepository.delete(user);
    }

    /**
     * Get keycloak user by username.
     */
    @NotNull
    public KeycloakUserResponse getKeycloakUser(String username) throws NotFoundException {
        try (Keycloak kc = keycloakAdminProvider.getKeycloakClient()) {
            return kc.realms()
                    .realm(realm)
                    .users()
                    .search(username, true)
                    .stream()
                    .findFirst()
                    .map(this::mapKeycloakUser)
                    .orElseThrow(() -> new NotFoundException(
                            "No user with username " + username + " exists in Authentication Service"));
        }
    }

    /**
     * Map keycloak user representation to keycloak user response.
     */
    @NotNull
    private KeycloakUserResponse mapKeycloakUser(@NotNull UserRepresentation userRepresentation) {
        return new KeycloakUserResponse(
                userRepresentation.getUsername(),
                userRepresentation.getFirstName(),
                userRepresentation.getLastName(),
                userRepresentation.getEmail()
        );
    }

    /**
     * Publishes user created.
     *
     * @param userResponse user
     */
    public void publishUserCreated(@NotNull UserResponse userResponse) {
        userEventPublisher.publishUserCreated(new UserCreatedEvent(
                userResponse.getUsername(),
                userResponse.getEmail()
        ));
    }

}
