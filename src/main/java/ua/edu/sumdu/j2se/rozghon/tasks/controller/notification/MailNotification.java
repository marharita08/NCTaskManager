package ua.edu.sumdu.j2se.rozghon.tasks.controller.notification;

import org.apache.log4j.Logger;
import ua.edu.sumdu.j2se.rozghon.tasks.model.Task;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailNotification implements Notification {
    private static final Logger log = Logger.getLogger(MailNotification.class);
    private static String username;
    private static String password;
    private static Properties properties;
    private static boolean active;

    public MailNotification() {
        File file = new File("src/main/resources/mail.properties.txt");
        properties = new Properties();
        try {
            //read properties from file
            properties.load(new FileReader(file));
            username = properties.getProperty("username");
            password = properties.getProperty("password");
        } catch (IOException e) {
            log.error(e);
        }
    }

    /**
     * Method for sending mail notification.
     * @param set tasks for which user has to get notification
     */
    public void sendNotification(Set<Task> set) {
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            //add mail of recipient
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(properties.getProperty("recipient")));
            message.setSubject("Notification"); //add heading of message
            //add text of message
            StringBuilder stringBuilder = new StringBuilder();
            int i = 0;
            for (Task task : set) {
                stringBuilder.append(task.getTitle());
                if (i < set.size() - 1) {
                    stringBuilder.append(", ");
                }
                i++;
            }
            String text = stringBuilder.toString();
            message.setText(text);
            Transport.send(message); //send message
            log.info("sent notification");
        } catch (MessagingException e) {
            log.error(e);
        } catch (NullPointerException e) {
            log.error("File with mail properties don't have needed information "
                    + e);
        }
    }

    /**
     * Method checks activity of mail notifications.
     * @return true if mail notifications are active
     */
    public static boolean isActive() {
        return active;
    }

    /**
     * Method for setting mail notifications activity.
     * @param act activity of mail notifications
     */
    public static void setActive(boolean act) {
        active = act;
    }
}
