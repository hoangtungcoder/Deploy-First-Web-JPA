package murach.util;

import java.util.Properties;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Sends email through Gmail SMTP. The class name is retained to match the
 * helper class used by the Chapter 14 exercise.
 */
public final class MailUtilLocal {

    private MailUtilLocal() {
    }

    public static void sendMail(
            String to,
            String from,
            String subject,
            String body,
            boolean bodyIsHTML
    ) throws MessagingException {
        String username = getRequiredEnvironmentVariable("SMTP_USERNAME");
        String password = getRequiredEnvironmentVariable("SMTP_PASSWORD");

        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.starttls.required", "true");

        Session session = Session.getInstance(
                properties,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication
                    getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                username, password
                        );
                    }
                }
        );
        session.setDebug(Boolean.parseBoolean(
                System.getenv("MAIL_DEBUG")
        ));

        MimeMessage message = new MimeMessage(session);
        message.setSubject(subject, "UTF-8");

        if (bodyIsHTML) {
            message.setContent(body, "text/html; charset=UTF-8");
        } else {
            message.setText(body, "UTF-8");
        }

        String sender = from == null || from.trim().isEmpty()
                ? username
                : from.trim();
        Address fromAddress = new InternetAddress(sender);
        Address toAddress = new InternetAddress(to);
        message.setFrom(fromAddress);
        message.setRecipient(Message.RecipientType.TO, toAddress);

        Transport.send(message);
    }

    private static String getRequiredEnvironmentVariable(String name)
            throws MessagingException {
        String value = System.getenv(name);

        if (value == null || value.trim().isEmpty()) {
            throw new MessagingException(
                    "Missing required environment variable: " + name
            );
        }

        return value.trim();
    }
}
