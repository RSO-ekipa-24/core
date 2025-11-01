package essa.dto.leaseinvitation;

import io.smallrye.common.constraint.NotNull;

import java.time.LocalDateTime;

public class LeaseInvitationResponse {
    private final Long id;
    private final Long propertyId;
    private final String invitedEmail;
    private final String status;
    private final LocalDateTime expiresAt;

    public LeaseInvitationResponse(
            @NotNull Long id,
            @NotNull Long propertyId,
            @NotNull String invitedEmail,
            @NotNull String status,
            @NotNull LocalDateTime expiresAt
    ) {
        this.id = id;
        this.propertyId = propertyId;
        this.invitedEmail = invitedEmail;
        this.status = status;
        this.expiresAt = expiresAt;
    }

    @NotNull
    public Long getId() {
        return id;
    }

    @NotNull
    public Long getPropertyId() {
        return propertyId;
    }

    @NotNull
    public String getInvitedEmail() {
        return invitedEmail;
    }

    @NotNull
    public String getStatus() {
        return status;
    }

    @NotNull
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
}
