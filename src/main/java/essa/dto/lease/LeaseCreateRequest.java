package essa.dto.lease;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import io.smallrye.common.constraint.NotNull;
import io.smallrye.common.constraint.Nullable;


import java.time.LocalDateTime;

public class LeaseCreateRequest {

    //TODO add validators
    private final Long propertyId;
    private final String tenantEmail;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private Long userId;

    @JsonbCreator
    public LeaseCreateRequest(
            @JsonbProperty("propertyId") @NotNull Long propertyId,
            @JsonbProperty("tenantEmail") @NotNull String   tenantEmail,
            @JsonbProperty("startDate") @NotNull LocalDateTime startDate,
            @JsonbProperty("endDate") @NotNull LocalDateTime endDate
    ) {
        this.propertyId = propertyId;
        this.tenantEmail = tenantEmail;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @NotNull
    public Long getPropertyId() {
        return propertyId;
    }

    @NotNull
    public String getTenantEmail() {
        return tenantEmail;
    }

    @NotNull
    public LocalDateTime getStartDate() {
        return startDate;
    }

    @NotNull
    public LocalDateTime getEndDate() {
        return endDate;
    }

    @Nullable
    public Long getUserId() {
        return userId;
    }

    public void setUserId(@NotNull Long userId) {
        this.userId = userId;
    }
}
