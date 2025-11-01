package essa.entity;

import essa.enums.InvitationStatus;
import io.smallrye.common.constraint.NotNull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lease_invitation")
public class LeaseInvitation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "property_id_fk", nullable = false)
    private Property property;

    @Column(name = "invited_email", nullable = false, length = 255)
    private String invitedEmail;

    @ManyToOne
    @JoinColumn(name = "invited_user_id_fk")
    private User invitedUser;

    @Column(nullable = false, unique = true, length = 100)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvitationStatus status;

    @Column(name = "lease_starts_at", nullable = false)
    private LocalDateTime leaseStartsAt;

    @Column(name = "lease_ends_at", nullable = false)
    private LocalDateTime leaseEndsAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @NotNull
    public Long getId() {
        return id;
    }

    @NotNull
    public Property getProperty() {
        return property;
    }

    public void setProperty(@NotNull Property property) {
        this.property = property;
    }

    @NotNull
    public String getInvitedEmail() {
        return invitedEmail;
    }

    public void setInvitedEmail(@NotNull String invitedEmail) {
        this.invitedEmail = invitedEmail;
    }

    @NotNull
    public User getInvitedUser() {
        return invitedUser;
    }

    public void setInvitedUser(@NotNull User invitedUser) {
        this.invitedUser = invitedUser;
    }

    @NotNull
    public String getToken() {
        return token;
    }

    public void setToken(@NotNull String token) {
        this.token = token;
    }

    @NotNull
    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(@NotNull InvitationStatus status) {
        this.status = status;
    }

    @NotNull
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(@NotNull LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    @NotNull
    public LocalDateTime getLeaseEndsAt() {
        return leaseEndsAt;
    }

    public void setLeaseEndsAt(@NotNull LocalDateTime leaseEndsAt) {
        this.leaseEndsAt = leaseEndsAt;
    }

    @NotNull
    public LocalDateTime getLeaseStartsAt() {
        return leaseStartsAt;
    }

    public void setLeaseStartsAt(@NotNull LocalDateTime leaseStartsAt) {
        this.leaseStartsAt = leaseStartsAt;
    }

    @Nullable
    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(@Nullable LocalDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }
}
