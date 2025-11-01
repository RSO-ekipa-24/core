package essa.service.lease;

import essa.dto.lease.LeaseCreateRequest;
import essa.dto.lease.LeaseResponse;
import essa.dto.leaseinvitation.LeaseInvitationResponse;
import essa.entity.Lease;
import essa.entity.LeaseInvitation;
import essa.entity.Property;
import essa.entity.User;
import essa.enums.InvitationStatus;
import essa.enums.LeaseStatus;
import essa.exception.EntityNotFoundException;
import essa.repository.lease.LeaseRepository;
import essa.repository.leaseinvitation.LeaseInvitationRepository;
import essa.repository.property.PropertyRepository;
import essa.repository.user.UserRepository;
import essa.service.emailservice.EmailService;
import io.smallrye.common.constraint.NotNull;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class LeaseService {

    @Inject
    LeaseRepository leaseRepository;
    @Inject
    LeaseInvitationRepository leaseInvitationRepository;
    @Inject
    PropertyRepository propertyRepository;
    @Inject
    UserRepository userRepository;
    @Inject
    EmailService emailService;

    /**
     * Get lease by id.
     *
     * @param id     id
     * @param userId user id
     * @return lease response
     */
    public LeaseResponse getLeaseById(@NotNull Long id, @NotNull Long userId) {
        Lease lease = leaseRepository.findById(id);
        if (lease == null) throw new EntityNotFoundException("Lease not found.");

        Long landlordId = lease.getProperty().getUser().getId();
        if (!userId.equals(landlordId) && !userId.equals(lease.getTenant().getId()))
            throw new EntityNotFoundException("Lease not found.");

        return LeaseResponse.fromEntity(lease);
    }

    /**
     * List leases where user is landlord.
     *
     * @param landlordId landlord id
     * @return list of leases
     */
    public List<LeaseResponse> listByLandlord(@NotNull Long landlordId) {
        return leaseRepository.listByLandlord(landlordId).stream()
                .map(LeaseResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * List leases where user is tenant.
     *
     * @param tenantId tenant id
     * @return list of leases
     */
    public List<LeaseResponse> listByTenant(@NotNull Long tenantId) {
        return leaseRepository.listByTenant(tenantId).stream()
                .map(LeaseResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public LeaseInvitationResponse invite(@NotNull LeaseCreateRequest leaseCreateRequest) {
        Property property = propertyRepository.findById(leaseCreateRequest.getPropertyId());
        if (property == null) {
            throw new EntityNotFoundException("Property not found.");
        }
        if (!property.getUser().getId().equals(leaseCreateRequest.getUserId())) {
            throw new EntityNotFoundException("Property not found.");
        }

        LeaseInvitation leaseInvitation = new LeaseInvitation();
        leaseInvitation.setProperty(property);
        leaseInvitation.setInvitedEmail(leaseCreateRequest.getTenantEmail());
        leaseInvitation.setToken(UUID.randomUUID().toString());
        leaseInvitation.setStatus(InvitationStatus.PENDING);
        leaseInvitation.setLeaseStartsAt(leaseCreateRequest.getStartDate());
        leaseInvitation.setLeaseEndsAt(leaseCreateRequest.getEndDate());
        leaseInvitation.setExpiresAt(LocalDateTime.now().plusDays(7));

        // wire up existing user if present
        User existingUser = null;
        try {
            existingUser = userRepository.findByEmail(leaseCreateRequest.getTenantEmail());
            leaseInvitation.setInvitedUser(existingUser);
        } catch (EntityNotFoundException ignored) {
            // leave invitedUser null
        }

        leaseInvitationRepository.persist(leaseInvitation);

        // notification if they exist
        if (existingUser != null) {
            // send notification, email otherwise
        } else {
            emailService.sendLeaseInvitationEmail(
                    leaseCreateRequest.getTenantEmail(),
                    property.getName(),
                    leaseInvitation.getToken(),
                    leaseCreateRequest.getStartDate(),
                    leaseCreateRequest.getEndDate(),
                    leaseInvitation.getExpiresAt()
            );
        }

        return new LeaseInvitationResponse(
                leaseInvitation.getId(),
                property.getId(),
                leaseInvitation.getInvitedEmail(),
                leaseInvitation.getStatus().name(),
                leaseInvitation.getExpiresAt()
        );
    }

    @Transactional
    public LeaseResponse accept(@NotNull String token, @NotNull User tenant) {
        LeaseInvitation leaseInvitation = leaseInvitationRepository.findByToken(token);
        if (leaseInvitation == null
                || leaseInvitation.getStatus() != InvitationStatus.PENDING
                || leaseInvitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired invitation.");
        }

        if (leaseInvitation.getInvitedUser() != null) {
            if (!leaseInvitation.getInvitedUser().getId().equals(tenant.getId())) {
                throw new EntityNotFoundException("Invitation not found.");
            }
        }

        leaseInvitation.setInvitedUser(tenant);
        leaseInvitation.setStatus(InvitationStatus.ACCEPTED);
        leaseInvitation.setAcceptedAt(LocalDateTime.now());
        leaseInvitationRepository.update(leaseInvitation);

        Lease lease = new Lease();
        lease.setProperty(leaseInvitation.getProperty());
        lease.setTenant(tenant);
        lease.setStartDate(leaseInvitation.getLeaseStartsAt());
        lease.setEndDate(leaseInvitation.getLeaseEndsAt());

        LocalDateTime now = LocalDateTime.now();
        LeaseStatus initialStatus = lease.getStartDate().isAfter(now)
                ? LeaseStatus.UPCOMING
                : LeaseStatus.ACTIVE;
        lease.setStatus(initialStatus);

        leaseRepository.persist(lease);

        // send notification

        return LeaseResponse.fromEntity(lease);
    }

    @Transactional
    public LeaseResponse terminateLease(@NotNull Long leaseId, @NotNull Long userId) {
        Lease lease = leaseRepository.findById(leaseId);
        if (lease == null) throw new EntityNotFoundException("Lease not found.");
        Long landlordId = lease.getProperty().getUser().getId();

        if (!userId.equals(landlordId) && !userId.equals(lease.getTenant().getId()))
            throw new EntityNotFoundException("Lease not found.");

        lease.setStatus(LeaseStatus.TERMINATED);
        lease.setEndDate(LocalDateTime.now());
        leaseRepository.update(lease);
        return LeaseResponse.fromEntity(lease);
    }

}