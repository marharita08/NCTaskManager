package ua.edu.sumdu.j2se.rozghon.tasks.controller;

import org.apache.log4j.Logger;
import ua.edu.sumdu.j2se.rozghon.tasks.controller.notification.MailNotification;
import ua.edu.sumdu.j2se.rozghon.tasks.controller.notification.NotificationManager;
import ua.edu.sumdu.j2se.rozghon.tasks.model.*;
import ua.edu.sumdu.j2se.rozghon.tasks.view.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

public class Controller {
    private static AbstractTaskList taskList; //buffer
    private static final Logger log = Logger.getLogger(Controller.class);
    private static NotificationManager notificationManager;
    private static MainForm mainForm;
    private static AddTaskForm addTaskForm;
    private static DeleteTaskForm deleteTaskForm;
    private static EditTaskForm editTaskForm;

    public static void readData(MainForm form) {
        mainForm = form;
        taskList = new ArrayTaskList();
        log.info("Read list from file to buffer");
        TaskIO.readText(taskList,
                new File("src/main/resources/data.txt")); //read data
        if (taskList.size() == 0) {
            //disable all buttons apart 'Add task' on MainForm
            mainForm.disableButtons();
            log.info("List is empty");
        }
    }

    public static void createNotificationManager() {
        File file = new File("src/main/resources/mail.properties.txt");
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(file));
        } catch (IOException e) {
            log.error(e);
        }
        MailNotification.setActive(properties.getProperty("recipient") != null);
        notificationManager = new NotificationManager(taskList);
        notificationManager.setDaemon(true);
        notificationManager.start();
    }

    public static AbstractTaskList getTaskList() {
        return taskList;
    }

    public static class DeleteTask implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (deleteTaskForm == null) {
                deleteTaskForm = new DeleteTaskForm(taskList);
            }
        }
    }

    public static void closeDeleteForm() {
        deleteTaskForm = null;
    }

    public static class AddTask implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (addTaskForm == null) {
                addTaskForm = new AddTaskForm();
            }
        }
    }

    public static void closeAddForm() {
        addTaskForm = null;
    }

    public static class EditTask implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (editTaskForm == null) {
                editTaskForm = new EditTaskForm(taskList);
            }
        }
    }

    public static class MailAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            new MailSettings();
        }
    }

    public static class ShowTaskAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            mainForm.showTasks(taskList);
        }
    }

    public static class EditAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            //fill fields with parameters of chosen task
            editTaskForm.fillEditFields(taskList);
        }
    }

    public static void closeEditForm() {
        editTaskForm = null;
    }

    public static class CalendarAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            mainForm.showCalendar(taskList);
        }
    }

    public static class DeleteAllAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (mainForm.confirmation()) {
                taskList = new ArrayTaskList();
                //write list to the file
                TaskIO.writeText(taskList,
                        new File("src/main/resources/data.txt"));
                log.info("All tasks was deleted from list");
                log.info("List is empty");
                notificationManager.setTaskList(taskList);
                mainForm.disableButtons();
            }
        }
    }

    public static class RepeatedActionAdd implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                //show fields for repetitive task
                addTaskForm.showRepetitiveFields();
            } else {
                //hide fields for repetitive task
                addTaskForm.hideRepetitiveFields();
            }
        }
    }

    public static class RepeatedActionEdit implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                //show fields for repetitive task
                editTaskForm.showRepetitiveFields();
            } else {
                //hide fields for repetitive task
                editTaskForm.hideRepetitiveFields();
            }
        }
    }

    public static class AddSave implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String title = addTaskForm.getTaskTitle();
            if (title.equals("")) {
                addTaskForm.exceptionMessages(
                        new IllegalArgumentException("Fill field for title."));
            } else {
                int max = 0;
                int i = 0;
                for (Object task1 : taskList) {
                    String title1 = ((Task) task1).getTitle();
                    if (Pattern.matches("^" + title + " [0-9]$", title1)) {
                        i = Integer.parseInt(title1, title1.length() - 1,
                                title1.length(), 10) + 1;
                    } else if (Pattern.matches(
                            "^" + title + " [0-9]{2}$", title1)) {
                        i = Integer.parseInt(((Task) task1).getTitle(),
                                title1.length() - 2,
                                title1.length(), 10) + 1;
                    } else if (title.equals(title1)) {
                        i = 1;
                    }
                    if (i > max) {
                        max = i;
                    }
                }
                if (max > 0) {
                    //add index if task with set title already exist
                    title = title + " " + max;
                }
                Task task = null;
                if (addTaskForm.repetitive()) {
                    LocalDateTime start = addTaskForm.getStartTime();
                    LocalDateTime end = addTaskForm.getEndTime();
                    int interval = addTaskForm.getInterval();
                    //create repetitive task
                    if (start != null && end != null) {
                        try {
                            task = new Task(title, start, end, interval);
                        } catch (Exception exception) {
                            addTaskForm.exceptionMessages(exception);
                        }
                    }
                } else {
                    LocalDateTime time = addTaskForm.getTime();
                    if (time != null) {
                        try {
                            //create non repetitive task
                            task = new Task(title, time);
                        } catch (Exception exception) {
                            addTaskForm.exceptionMessages(exception);
                        }
                    }
                }
                if (task != null) {
                    task.setActive(addTaskForm.active());
                    taskList.add(task); //add created task to the list
                    //write list to the file
                    TaskIO.writeText(taskList,
                            new File("src/main/resources/data.txt"));
                    if (taskList.size() == 1) {
                        //if list was empty enable all buttons on MainForm
                        mainForm.enableButtons();
                    }
                    addTaskForm.message();
                    log.info("Task was added to list");
                    notificationManager.setTaskList(taskList);
                    addTaskForm.dispose();
                    addTaskForm = null;
                }
            }
        }
    }

    public static class Delete implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String title = deleteTaskForm.getTaskTitle();
            for (Object task:taskList) {
                if (((Task) task).getTitle().equals(title)) {
                    taskList.remove((Task) task); //delete chosen task from list
                    //write list to the file
                    TaskIO.writeText(taskList,
                            new File("src/main/resources/data.txt"));
                    log.info("Task was deleted from list");
                    notificationManager.setTaskList(taskList);
                    deleteTaskForm.message();
                    deleteTaskForm.dispose();
                    if (taskList.size() == 0) {
                        mainForm.disableButtons();
                        log.info("List is empty");
                    }
                    deleteTaskForm = null;
                    break;
                }
            }
        }
    }

    public static class EditSave implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            //read data from fields
            Task task = taskList.getTask(editTaskForm.getTitleIndex());
            String title = editTaskForm.getTaskTitle();
            if (title.equals("")) {
                editTaskForm.exceptionMessages(new IllegalArgumentException(
                        "Fill field for title."));
            } else {
                int max = 0;
                int i = 0;
                for (Object task1 : taskList) {
                    if(!task.equals(task1)) {
                        String title1 = ((Task) task1).getTitle();
                        if (Pattern.matches("^" + title + " [0-9]$", title1)) {
                            i = Integer.parseInt(title1, title1.length() - 1,
                                    title1.length(), 10) + 1;
                        } else if (Pattern.matches(
                                "^" + title + " [0-9]{2}$", title1)) {
                            i = Integer.parseInt(((Task) task1).getTitle(),
                                    title1.length() - 2,
                                    title1.length(), 10) + 1;
                        } else if (title.equals(title1)) {
                            i = 1;
                        }
                        if (i > max) {
                            max = i;
                        }
                    }
                }
                if (max > 0) {
                    //add index if task with set title already exist
                    title = title + " " + max;
                }
                if (editTaskForm.repetitive()) {
                    LocalDateTime start = editTaskForm.getStartTime();
                    LocalDateTime end = editTaskForm.getEndTime();
                    int interval = editTaskForm.getInterval();
                    //create repetitive task
                    if (start != null && end != null) {
                        try {
                            task.setTime(start, end, interval);
                        } catch (Exception exception) {
                            editTaskForm.exceptionMessages(exception);
                        }
                    }
                } else {
                    LocalDateTime time = editTaskForm.getTime();
                    if (time != null) {
                        try {
                            task.setTime(time);
                        } catch (Exception exception) {
                            editTaskForm.exceptionMessages(exception);
                        }
                    }
                }
                if (task != null) {
                    task.setTitle(title);
                    task.setActive(editTaskForm.active());
                    //write list to the file
                    TaskIO.writeText(taskList,
                            new File("src/main/resources/data.txt"));
                    editTaskForm.message();
                    log.info("Task was edited");
                    notificationManager.setTaskList(taskList);
                    editTaskForm.dispose();
                    editTaskForm = null;
                }
            }
        }
    }
}
