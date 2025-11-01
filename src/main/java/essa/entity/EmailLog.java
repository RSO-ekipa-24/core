package essa.entity;

import jakarta.persistence.*;
import io.smallrye.common.constraint.NotNull;

@Entity
@Table(name = "email_log")
public class EmailLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "to_address", nullable = false, length = 255)
    public String to;

    @Column(nullable = false, length = 255)
    public String subject;

    @Column(columnDefinition = "TEXT", nullable = false)
    public String body;

    @NotNull
    public String getTo() {
        return to;
    }

    public void setTo(@NotNull String to) {
        this.to = to;
    }

    @NotNull
    public String getSubject() {
        return subject;
    }

    public void setSubject(@NotNull String subject) {
        this.subject = subject;
    }

    @NotNull
    public String getBody() {
        return body;
    }

    public void setBody(@NotNull String body) {
        this.body = body;
    }

}
