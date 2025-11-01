package essa.repository.tags;

import essa.entity.Tag;
import io.smallrye.common.constraint.NotNull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class TagRepository {
    @PersistenceContext
    EntityManager em;

    /**
     * Find tag by id.
     *
     * @param id id
     * @return Tag
     */
    public Tag findById(@NotNull Long id) {
        return em.find(Tag.class, id);
    }

    /**
     * Find a tag by its name and owner.
     *
     * @param userId the owner’s user id
     * @param name   the tag name
     * @return the Tag or null if none found
     */
    public Tag findByNameAndUser(@NotNull Long userId, @NotNull String name) {
        try {
            return em.createQuery(
                            "SELECT t FROM Tag t WHERE t.user.id = :userId AND t.name = :name",
                            Tag.class)
                    .setParameter("userId", userId)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Select tags by user.
     *
     * @param userId user id
     * @return list of tags
     */
    public List<Tag> listByUser(@NotNull Long userId) {
        return em.createQuery(
                        "SELECT t FROM Tag t WHERE t.user.id = :id", Tag.class)
                .setParameter("id", userId)
                .getResultList();
    }

    /**
     * Persist tag.
     *
     * @param tag tag
     */
    public void persist(@NotNull Tag tag) {
        em.persist(tag);
    }

    /**
     * Delete tag.
     */
    public void delete(@NotNull Tag tag) {
        em.remove(tag);
    }
}
