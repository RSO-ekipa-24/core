package essa.repository.propertygroup;


import essa.entity.PropertyGroup;
import io.smallrye.common.constraint.NotNull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class PropertyGroupRepository {

    @PersistenceContext
    EntityManager em;

    /**
     * Find property group by id.
     *
     * @param id id
     * @return PropertyGroup
     */
    @NotNull
    public PropertyGroup findById(@NotNull Long id) {
        return em.find(PropertyGroup.class, id);
    }

    /**
     * Persist property group.
     *
     * @param propertyGroup property group
     */
    public void persist(@NotNull PropertyGroup propertyGroup) {
        em.persist(propertyGroup);
    }

    /**
     * Update property group.
     *
     * @param propertyGroup property group
     * @return PropertyGroup
     */
    @NotNull
    public PropertyGroup update(@NotNull PropertyGroup propertyGroup) {
        return em.merge(propertyGroup);
    }

    /**
     * Delete property group.
     *
     */
    public void delete(@NotNull PropertyGroup propertyGroup) {
        em.remove(propertyGroup);
    }
}
