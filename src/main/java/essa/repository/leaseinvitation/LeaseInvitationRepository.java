package essa.repository.leaseinvitation;

import essa.entity.LeaseInvitation;
import essa.enums.InvitationStatus;
import io.smallrye.common.constraint.NotNull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class LeaseInvitationRepository {

    @PersistenceContext
    EntityManager em;

    /**
     * Persist leaseInvitation.
     *
     * @param leaseInvitation lease invitation
     */
    public void persist(LeaseInvitation leaseInvitation) {
        em.persist(leaseInvitation);
    }

    /**
     * Find lease invitation by token.
     *
     * @param token token
     * @return LeaseInvitation
     */
    public LeaseInvitation findByToken(@NotNull String token) {
        return em.createQuery(
                        "SELECT leaseInvitation FROM LeaseInvitation leaseInvitation WHERE leaseInvitation.token = :token", LeaseInvitation.class)
                .setParameter("token", token)
                .getSingleResult();
    }

    /**
     * Find pending lease invitation by email.
     *
     * @param email email
     * @return LeaseInvitation
     */
    public List<LeaseInvitation> findPendingByEmail(@NotNull String email) {
        return em.createQuery(
                        "SELECT leaseInvitation FROM LeaseInvitation leaseInvitation " +
                                " WHERE leaseInvitation.invitedEmail = :email" +
                                "   AND leaseInvitation.status = :status", LeaseInvitation.class)
                .setParameter("email", email)
                .setParameter("status", InvitationStatus.PENDING)
                .getResultList();
    }

    /**
     * Update lease invitation.
     *
     * @param leaseInvitation lease invitation
     * @return LeaseInvitation
     */
    public LeaseInvitation update(@NotNull LeaseInvitation leaseInvitation) {
        return em.merge(leaseInvitation);
    }
}
