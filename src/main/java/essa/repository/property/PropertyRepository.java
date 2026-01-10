package essa.repository.property;

import essa.entity.Property;
import io.smallrye.common.constraint.NotNull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class PropertyRepository {

    @PersistenceContext
    EntityManager em;

    /**
     * Find all properties.
     *
     * @return List<Property>
     */
    @NotNull
    public List<Property> listAll() {
        return em.createQuery("SELECT p FROM Property p", Property.class).getResultList();
    }

    /**
     * Find property by id.
     *
     * @param id id
     * @return Property
     */
    @NotNull
    public Property findById(@NotNull Long id) {
        return em.find(Property.class, id);
    }

    /**
     * Persist property.
     *
     * @param property property
     */
    public void persist(@NotNull Property property) {
        em.persist(property);
    }

    /**
     * Update property.
     *
     * @param property property
     * @return Property
     */
    @NotNull
    public Property update(@NotNull Property property) {
        return em.merge(property);
    }

    /**
     * Delete property.
     *
     */
    public void delete(@NotNull Property property) {
            em.remove(property);
    }
}