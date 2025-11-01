package essa.repository.user;

import essa.entity.User;
import essa.exception.EntityNotFoundException;
import io.smallrye.common.constraint.NotNull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class UserRepository {

    @PersistenceContext
    EntityManager em;

    /**
     * List all users.
     *
     * @return List<User>
     */
    public List<User> listAll() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    /**
     * Find user by id.
     *
     * @param id id
     * @return User
     */
    @NotNull
    public User findById(@NotNull Long id) {
        return em.find(User.class, id);
    }

    /**
     * Find user by keycloak id.
     *
     * @param keycloakId keycloak id
     * @return User
     */
    @NotNull
    public User findByKeycloakId(@NotNull String keycloakId) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.keycloakId = :keycloakId", User.class)
                    .setParameter("keycloakId", keycloakId)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new EntityNotFoundException("User with keycloakId " + keycloakId + " not found.");
        }
    }

    /**
     * Find user by email.
     *
     * @param email user email
     * @return User
     * @throws EntityNotFoundException if no user with that email exists
     */
    @NotNull
    public User findByEmail(@NotNull String email) {
        try {
            return em.createQuery(
                            "SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new EntityNotFoundException("User with email " + email + " not found.");
        }
    }

    /**
     * Persist user.
     *
     * @param user user
     */
    public void persist(@NotNull User user) {
        em.persist(user);
    }

    /**
     * Delete user.
     */
    public void delete(@NotNull User user) {
        em.remove(user);
    }

    /**
     * Check if user exists by username.
     *
     * @param username username
     * @return boolean
     */
    public boolean existsByUsername(@NotNull String username) {
        long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }
}