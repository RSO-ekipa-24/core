package essa.entity;

import essa.enums.LeaseStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import io.smallrye.common.constraint.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "lease")
public class Lease extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "property_id_fk", nullable = false)
    private Property property;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tenant_user_id_fk", nullable = false)
    private User tenant;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @NotNull
    @Future
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private LeaseStatus status;

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
    public User getTenant() {
        return tenant;
    }

    public void setTenant(@NotNull User tenant) {
        this.tenant = tenant;
    }

    @NotNull
    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(@NotNull LocalDateTime startDate) {
        this.startDate = startDate;
    }

    @NotNull
    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(@NotNull LocalDateTime endDate) {
        this.endDate = endDate;
    }

    @NotNull
    public LeaseStatus getStatus() {
        return status;
    }

    public void setStatus(@NotNull LeaseStatus status) {
        this.status = status;
    }
}
