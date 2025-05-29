package util;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EmailUtil {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static String USERNAME;
    private static String PASSWORD;

    public static void sendVerificationEmail(String recipientEmail, String verificationCode) {
        // Set up email properties
        Properties props = new Properties();
        try (InputStream input = EmailUtil.class.getClassLoader().getResourceAsStream("mail.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find mail.properties");
                return;
            }
            props.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        USERNAME = props.getProperty("mail.username");
        PASSWORD = props.getProperty("mail.password");

        // Create a session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            // Create email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Email Verification");
            message.setText("Your verification code is: " + verificationCode);

            // Send email
            Transport.send(message);
            System.out.println("Verification email sent successfully.");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
