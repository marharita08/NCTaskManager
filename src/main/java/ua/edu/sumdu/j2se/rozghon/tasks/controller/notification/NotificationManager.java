package ua.edu.sumdu.j2se.rozghon.tasks.controller.notification;

import org.apache.log4j.Logger;
import ua.edu.sumdu.j2se.rozghon.tasks.model.AbstractTaskList;
import ua.edu.sumdu.j2se.rozghon.tasks.model.Task;
import ua.edu.sumdu.j2se.rozghon.tasks.model.Tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.SortedMap;

public class NotificationManager extends Thread {
    private static final Logger log =
            Logger.getLogger(NotificationManager.class);
    private AbstractTaskList taskList;
    private boolean timeout;

    public NotificationManager(AbstractTaskList taskList) {
        this.taskList = taskList;
    }

    public void setTaskList(AbstractTaskList taskList) {
        //refresh taskList
        this.taskList = taskList;
        timeout = false;
        synchronized (this) {
            //break waiting
            notify();
        }
    }

    public void run() {
        for (;;) {
            LocalDateTime from = LocalDateTime.now();
            LocalDateTime to = LocalDateTime.now().plusHours(1);
            //read tasks for the next hour
            SortedMap<LocalDateTime, Set<Task>> map =
                    Tasks.calendar(taskList, from, to);
            if (map.isEmpty()) {
                try {
                    log.info("Map is empty. Wait hour");
                    synchronized (this) {
                        //if no tasks are detected for the next hour
                        //wait an hour
                        wait(3600000);
                    }
                } catch (Exception e) {
                    log.error(e);
                }
            } else {
                Duration duration =
                        Duration.between(LocalDateTime.now(), map.firstKey());
                try {
                    log.info("wait " + duration.toMinutes() + " minutes");
                    timeout = true;
                    synchronized (this) {
                        //if tasks are detected
                        //wait for execution time of the first of it
                        wait(duration.toMillis());
                    }
                    if (timeout) {
                        //if the execution time has come, send notification
                        Notification screenNotification =
                                new ScreenNotification();
                        screenNotification.sendNotification(
                                map.get(map.firstKey()));
                        if (MailNotification.isActive()) {
                            //if mail notification is active, send it
                            MailNotification mailNotification =
                                    new MailNotification();
                            mailNotification.sendNotification(
                                    map.get(map.firstKey()));
                        }
                    }
                } catch (InterruptedException e) {
                    log.error(e);
                }
            }
        }
    }
}
