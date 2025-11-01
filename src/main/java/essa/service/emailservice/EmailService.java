package essa.service.emailservice;

import essa.entity.EmailLog;
import essa.repository.emaillog.EmailLogRepository;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;

@ApplicationScoped
public class EmailService {

    private static final Logger LOGGER = Logger.getLogger(EmailService.class);

    @Inject
    Mailer mailer;

    @Inject
    EmailLogRepository emailLogRepository;

    public void sendLeaseInvitationEmail(
            String recipientEmail,
            String propertyName,
            String token,
            LocalDateTime leaseStartsAt,
            LocalDateTime leaseEndsAt,
            LocalDateTime expiresAt
    ) {
        String subject = "You've been invited to lease a property";

        String html = """
                <div style="font-family:Arial,Helvetica,sans-serif;line-height:1.5">
                  <p>Hello,</p>
                  <p>You have been invited to lease the property <strong>%s</strong>.</p>
                  <p>
                    <strong>Lease window:</strong><br/>
                    Starts: %s<br/>
                    Ends: %s
                  </p>
                  <p>Please click the following link to accept the invitation:</p>
                  <p>
                    <a href="https://your-app.com/invite/%s"
                       style="display:inline-block;padding:10px 16px;background:#2563eb;text-decoration:none;border-radius:6px">
                       Accept Invitation
                    </a>
                  </p>
                  <p style="margin-top:16px">
                    This invitation will expire on <strong>%s</strong>.
                  </p>
                  <p>Best regards,<br/>Your Team</p>
                </div>
                """.formatted(
                propertyName,
                leaseStartsAt,
                leaseEndsAt,
                token,
                expiresAt
        );

        // save email log
        EmailLog emailLog = new EmailLog();
        emailLog.setTo(recipientEmail);
        emailLog.setSubject(subject);
        emailLog.setBody(html);
        emailLogRepository.persist(emailLog);

        // send email
        mailer.send(Mail.withHtml(recipientEmail, subject, html));
        LOGGER.infof("Invitation email queued to %s for property '%s'", recipientEmail, propertyName);
    }

}
