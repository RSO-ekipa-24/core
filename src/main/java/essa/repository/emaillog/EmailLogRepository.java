package essa.repository.emaillog;

import essa.entity.EmailLog;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class EmailLogRepository {

    @PersistenceContext
    EntityManager em;

    /**
     * Persist email log.
     *
     * @param emailLog email log
     */
    public void persist(EmailLog emailLog) {
        em.persist(emailLog);
    }
}
