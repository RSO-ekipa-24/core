package essa.dto.lease;

import essa.dto.property.PropertyResponse;
import essa.dto.user.UserResponse;
import essa.entity.Lease;
import essa.enums.LeaseStatus;
import io.smallrye.common.constraint.NotNull;

import java.time.LocalDateTime;

public class LeaseResponse {
    private final Long id;
    private final PropertyResponse property;
    private final UserResponse tenant;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final LeaseStatus status;

    public LeaseResponse(
            @NotNull Long id,
            @NotNull PropertyResponse property,
            @NotNull UserResponse tenant,
            @NotNull LocalDateTime startDate,
            @NotNull LocalDateTime endDate,
            @NotNull LeaseStatus status
    ) {
        this.id = id;
        this.property = property;
        this.tenant = tenant;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    @NotNull
    public Long getId() {
        return id;
    }

    @NotNull
    public PropertyResponse getProperty() {
        return property;
    }

    @NotNull
    public UserResponse getTenant() {
        return tenant;
    }

    @NotNull
    public LocalDateTime getStartDate() {
        return startDate;
    }

    @NotNull
    public LocalDateTime getEndDate() {
        return endDate;
    }

    @NotNull
    public LeaseStatus getStatus() {
        return status;
    }

    @NotNull
    public static LeaseResponse fromEntity(@NotNull Lease lease) {
        PropertyResponse propertyDto = PropertyResponse.fromEntity(lease.getProperty());
        UserResponse userResponse = UserResponse.fromEntity(lease.getTenant());
        LocalDateTime start = lease.getStartDate();
        LocalDateTime end = lease.getEndDate();
        LeaseStatus statusEnum = LeaseStatus.valueOf(lease.getStatus().name());
        return new LeaseResponse(lease.getId(), propertyDto, userResponse, start, end, statusEnum);
    }
}
