package ua.edu.sumdu.j2se.rozghon.tasks.controller.notification;

import ua.edu.sumdu.j2se.rozghon.tasks.model.Task;

import java.util.Set;

public interface Notification {
    /**
     * Method for sending notification.
     * @param set tasks for which user has to get notification
     */
    void sendNotification(Set<Task> set);
}
