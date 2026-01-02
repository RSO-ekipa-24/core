package essa.messaging;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class UserEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserEventPublisher.class);

    @Inject
    @Channel("user-created-out")
    MutinyEmitter<UserCreatedEvent> emitter;

    @Retry(maxRetries = 3, delay = 200)
    @CircuitBreaker(requestVolumeThreshold = 5, delay = 5000)
    @Timeout(500)
    @Fallback(fallbackMethod = "fallbackPublishUserCreated")
    public Uni<Void> publishUserCreated(UserCreatedEvent event) {
        return emitter.send(event);
    }

    Uni<Void> fallbackPublishUserCreated(UserCreatedEvent event) {
        LOGGER.warn("UserCreatedEvent NOT published (fallback). username={}, email={}",
                event.getUsername(), event.getEmail());
        return Uni.createFrom().voidItem();
    }
}
