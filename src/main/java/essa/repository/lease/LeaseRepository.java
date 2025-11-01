package essa.repository.lease;

import essa.entity.Lease;
import essa.enums.LeaseStatus;
import io.smallrye.common.constraint.NotNull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class LeaseRepository {

    @PersistenceContext
    EntityManager em;

    /**
     * Find lease by id.
     *
     * @param id id
     * @return Lease
     */
    public Lease findById(@NotNull Long id) {
        return em.find(Lease.class, id);
    }

    /**
     * Persist lease.
     *
     * @param lease lease
     */
    public void persist(@NotNull Lease lease) {
        em.persist(lease);
    }

    /**
     * Update lease.
     *
     * @param lease lease
     * @return Lease
     */
    public Lease update(@NotNull Lease lease) {
        return em.merge(lease);
    }

    /**
     * Delete lease.
     */
    public void delete(@NotNull Lease lease) {
        em.remove(lease);
    }

    /**
     * List leases by property.
     *
     * @param propertyId property id
     * @return list of leases
     */
    public List<Lease> listByProperty(@NotNull Long propertyId) {
        return em.createQuery(
                        "SELECT lease FROM Lease lease WHERE lease.property.id = :propertyId", Lease.class)
                .setParameter("propertyId", propertyId)
                .getResultList();
    }

    /**
     * List leases by tenant.
     *
     * @param tenantId tenant id
     * @return list of leases
     */
    public List<Lease> listByTenant(@NotNull Long tenantId) {
        return em.createQuery(
                        "SELECT lease FROM Lease lease WHERE lease.tenant.id = :tenantId", Lease.class)
                .setParameter("tenantId", tenantId)
                .getResultList();
    }

    /**
     * List leases by landlord
     *
     * @param landlordId landlord id
     * @return list of leases
     */
    public List<Lease> listByLandlord(@NotNull Long landlordId) {
        return em.createQuery(
                        "SELECT lease FROM Lease lease WHERE lease.property.user.id = :landlordId", Lease.class)
                .setParameter("landlordId", landlordId)
                .getResultList();
    }

    /**
     * Check for any overlapping ACTIVE or UPCOMING leases on the same property.
     *
     * @param propertyId property id
     * @param start      start date
     * @param end        end date
     * @return boolean
     */
    public boolean hasOverlap(@NotNull Long propertyId, @NotNull LocalDate start, @NotNull LocalDate end) {
        Long count = em.createQuery(
                        "SELECT COUNT(lease) FROM Lease lease " +
                                "WHERE lease.property.id = :propertyId " +
                                "  AND lease.status IN (:status) " +
                                "  AND lease.startDate <= :end " +
                                "  AND lease.endDate   >= :start", Long.class)
                .setParameter("propertyId", propertyId)
                .setParameter("status", List.of(LeaseStatus.ACTIVE, LeaseStatus.UPCOMING))
                .setParameter("start", start)
                .setParameter("end", end)
                .getSingleResult();
        return count > 0;
    }

    /**
     * Batch transition: UPCOMING to ACTIVE when startDate ≤ now
     *
     * @param now current timestamp
     */
    public int transitionUpcomingToActive(@NotNull LocalDateTime now) {
        return em.createQuery(
                        "UPDATE Lease lease SET lease.status = :active " +
                                "WHERE lease.status = :upcoming AND lease.startDate <= :now")
                .setParameter("active", LeaseStatus.ACTIVE)
                .setParameter("upcoming", LeaseStatus.UPCOMING)
                .setParameter("now", now)
                .executeUpdate();
    }

    /**
     * Batch transition: ACTIVE to EXPIRED when endDate ≤ now
     *
     * @param now current timestamp
     */
    public int transitionActiveToExpired(@NotNull LocalDateTime now) {
        return em.createQuery(
                        "UPDATE Lease lease SET lease.status = :expired " +
                                "WHERE lease.status = :active AND lease.endDate <= :now")
                .setParameter("expired", LeaseStatus.EXPIRED)
                .setParameter("active", LeaseStatus.ACTIVE)
                .setParameter("now", now)
                .executeUpdate();
    }
}
