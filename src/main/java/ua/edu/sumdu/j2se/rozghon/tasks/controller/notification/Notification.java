package ua.edu.sumdu.j2se.rozghon.tasks.controller.notification;

import ua.edu.sumdu.j2se.rozghon.tasks.model.Task;

import java.util.Set;

public interface Notification {
    void sendNotification(Set<Task> set);
}
