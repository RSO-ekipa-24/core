package essa.resource.lease;

import essa.dto.lease.LeaseCreateRequest;
import essa.dto.lease.LeaseResponse;
import essa.dto.leaseinvitation.LeaseInvitationResponse;
import essa.entity.User;
import essa.service.lease.LeaseService;
import essa.service.user.UserService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Path("/leases")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LeaseResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    LeaseService leaseService;

    @Inject
    UserService userService;

    /**
     * Get lease by id.
     *
     * @param id id
     * @return LeaseResponse
     */
    @GET
    @Path("/{id}")
    public LeaseResponse getById(@PathParam("id") Long id) {
        Long userId = userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId();
        return leaseService.getLeaseById(id, userId);
    }

    /**
     * Terminate lease.
     *
     * @param id id
     * @return LeaseResponse
     */
    @PUT
    @Path("/{id}/terminate")
    public LeaseResponse terminate(@PathParam("id") Long id) {
        Long userId = userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId();
        return leaseService.terminateLease(id, userId);
    }

    /**
     * Get leases with current user as landlord.
     *
     * @return List<LeaseResponse>
     */
    @GET
    @Path("/landlord")
    public List<LeaseResponse> byLandlord() {
        Long userId = userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId();
        return leaseService.listByLandlord(userId);
    }

    /**
     * Get leases with current user as tenant.
     *
     * @return List<LeaseResponse>
     */
    @GET
    @Path("/tenant")
    public List<LeaseResponse> byTenant() {
        Long userId = userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId();
        return leaseService.listByTenant(userId);
    }

    /**
     * Invite user to a lease.
     *
     * @param leaseCreateRequest lease create request
     * @return LeaseInvitationResponse
     */
    @POST
    public LeaseInvitationResponse invite(@Valid LeaseCreateRequest leaseCreateRequest) {
        Long landlordId = userService.getUserByKeycloakIdInternal(jwt.getSubject()).getId();
        leaseCreateRequest.setUserId(landlordId);
        return leaseService.invite(leaseCreateRequest);
    }

    /**
     * Accept lease invitation.
     *
     * @param token token
     * @return LeaseResponse
     */
    @POST
    @Path("/accept/{token}")
    public LeaseResponse accept(
            @PathParam("token") String token
    ) {
        User tenant = userService.getUserEntityByKeycloakId(jwt.getSubject());
        return leaseService.accept(token, tenant);
    }
}
