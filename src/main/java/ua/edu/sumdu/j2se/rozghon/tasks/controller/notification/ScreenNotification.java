package ua.edu.sumdu.j2se.rozghon.tasks.controller.notification;

import org.apache.log4j.Logger;
import ua.edu.sumdu.j2se.rozghon.tasks.model.Task;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class ScreenNotification implements Notification {
    private static final Logger log =
            Logger.getLogger(ScreenNotification.class);
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void sendNotification(Set<Task> set) {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            //add icon
            TrayIcon trayIcon = new TrayIcon(
                    Toolkit.getDefaultToolkit().getImage("src/main/resources/task.png"));
            trayIcon.setImageAutoSize(true);
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                log.error(e);
            }
            //create text of message
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
            trayIcon.setToolTip("Notification: " + text); //add toolTip
            System.out.println(LocalDateTime.now().format(formatter) + " Notification: " + text);
            Toolkit.getDefaultToolkit().beep(); //add sound
            //send notification
            trayIcon.displayMessage("Notification", text,
                    TrayIcon.MessageType.INFO);
            log.info("Sent notification");
        } else {
            log.info("SystemTray is not supported");
        }
    }
}
