package essa.service.lease;

import essa.repository.lease.LeaseRepository;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;

@ApplicationScoped
public class LeaseStatusUpdater {

    private static final Logger LOGGER = Logger.getLogger(LeaseStatusUpdater.class);

    @Inject
    LeaseRepository leaseRepository;

    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void refreshStatuses() {
        LocalDateTime now = LocalDateTime.now();

        int updatedUp = leaseRepository.transitionUpcomingToActive(now);
        int updatedExp = leaseRepository.transitionActiveToExpired(now);

        LOGGER.infof(
                "LeaseStatusUpdater: %d leases transitioned from UPCOMING to ACTIVE, %d leases moved from ACTIVE to EXPIRED at %s",
                updatedUp, updatedExp, now
        );
    }
}
