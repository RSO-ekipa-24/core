package essa.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCreatedEvent {
    public String username;
    public String email;

    public UserCreatedEvent() {}

    public UserCreatedEvent(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
