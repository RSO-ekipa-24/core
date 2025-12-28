package essa.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class UserEventPublisher {

    @Inject
    @Channel("user-created-out")
    Emitter<UserCreatedEvent> emitter;

    public void publishUserCreated(UserCreatedEvent event) {
        emitter.send(event);
    }
}
