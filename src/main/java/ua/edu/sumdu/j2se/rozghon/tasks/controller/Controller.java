package ua.edu.sumdu.j2se.rozghon.tasks.controller;

import org.apache.log4j.Logger;
import ua.edu.sumdu.j2se.rozghon.tasks.controller.notification.MailNotification;
import ua.edu.sumdu.j2se.rozghon.tasks.controller.notification.NotificationManager;
import ua.edu.sumdu.j2se.rozghon.tasks.model.*;
import ua.edu.sumdu.j2se.rozghon.tasks.view.MainForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

public class Controller {
    private static AbstractTaskList taskList; //buffer
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Logger log = Logger.getLogger(Controller.class);
    private static NotificationManager notificationManager;

    public static void readData() {
        taskList = new ArrayTaskList();
        log.info("Read list from file to buffer");
        TaskIO.readText(taskList, new File("src/main/resources/data.txt")); //read data
        if (taskList.size() == 0) {
            JOptionPane.showMessageDialog(null,
                    "Your task list is empty.",
                    "Message", JOptionPane.INFORMATION_MESSAGE);
            //disable all buttons apart 'Add task' on MainForm
            MainForm.disableButtons();
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
        MailNotification.setActive(properties.getProperty("to") != null);
        notificationManager = new NotificationManager(taskList);
        notificationManager.setDaemon(true);
        notificationManager.start();
    }

    public static void fillComboBox(JComboBox<String> comboBox) {
        for (Object task:taskList) {
            //fill comboBox with task titles
            comboBox.addItem(((Task) task).getTitle());
        }
    }

    public static boolean fillEditFields(JTextField titleField,
                                         JCheckBox active,
                                         JCheckBox repetitive,
                                         JTextField timeField,
                                         JTextField startField,
                                         JTextField endField,
                                         JTextField intervalField,
                                         JComboBox<String> titleList,
                                         JComboBox<String> unit) {
        String title = String.valueOf(titleList.getSelectedItem());
        for (Object task1:taskList) {
            if (((Task) task1).getTitle().equals(title)) {
                //fill fields for edition with data about chosen task
                Task task = (Task) task1;
                titleField.setText(task.getTitle());
                active.setSelected(task.isActive());
                repetitive.setSelected(task.isRepeated());
                if (task.isRepeated()) {
                    startField.setText(task.getStartTime().format(formatter));
                    endField.setText(task.getEndTime().format(formatter));
                    double interval = (double) task.getRepeatInterval() / 86400;
                    unit.setSelectedIndex(2);
                    if (interval < 1) {
                        interval = (double) task.getRepeatInterval() / 3600;
                        unit.setSelectedIndex(1);
                    }
                    if (interval < 1) {
                        interval = (double) task.getRepeatInterval() / 60;
                        unit.setSelectedIndex(0);
                    }
                    intervalField.setText(String.valueOf(interval));
                    timeField.setText(task.getTime().format(formatter));
                } else {
                    timeField.setText(task.getTime().format(formatter));
                    startField.setText(task.getTime().format(formatter));
                    endField.setText(task.getTime().plusDays(1).format(formatter));
                }
                return true;
            }
        }
        return false;
    }

    public static void showTasks(JTable table) {
        //fill table with data about all tasks from list
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        model.setColumnCount(0);
        model.addColumn("Parameter");
        model.addColumn("Value");
        for (Object task: taskList) {
            model.addRow(new Object[]{"title", ((Task) task).getTitle()});
            model.addRow(new Object[]{"active",
                    ((Task) task).isActive() ? "+" : "-"});
            if (((Task) task).isRepeated()) {
                model.addRow(new Object[]{"start time",
                        ((Task) task).getStartTime().format(formatter)});
                model.addRow(new Object[]{"end time",
                        ((Task) task).getEndTime().format(formatter)});
                double interval = (double) ((Task) task).getRepeatInterval() / 86400;
                String unit = " days";
                if (interval < 1) {
                    interval = (double) ((Task) task).getRepeatInterval() / 3600;
                    unit = " hours";
                }
                if (interval < 1) {
                    interval = (double) ((Task) task).getRepeatInterval() / 60;
                    unit = " minutes";
                }
                model.addRow(new Object[]{"interval",
                        interval + unit});
            } else {
                model.addRow(new Object[]{"time",
                        ((Task) task).getTime().format(formatter)});
            }
            model.addRow(new Object[]{" ", " "});
        }
    }

    public static void showCalendar(JTable table, JComboBox<String> period) {
        //fill table with data about active tasks in date-tasks format
        SortedMap<LocalDateTime, Set<Task>> map;
        switch (period.getSelectedIndex()) {
            case 1:
                map = Tasks.calendar(taskList, LocalDateTime.now(),
                        LocalDateTime.now().plusDays(30));
                break;
            case 2:
                map = Tasks.calendar(taskList, LocalDateTime.now(),
                        LocalDateTime.now().plusDays(365));
                break;
            default:
                map = Tasks.calendar(taskList, LocalDateTime.now(),
                        LocalDateTime.now().plusDays(7));
                break;
        }
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        model.setColumnCount(0);
        model.addColumn("Date");
        model.addColumn("Task");
        for (Map.Entry<LocalDateTime, Set<Task>> entry : map.entrySet()) {
            Set<Task> set = entry.getValue();
            String text = "";
            int j = 0;
            for (Task task:set) {
                text = text + task.getTitle();
                if (j < set.size() - 1) {
                    text = text + ", ";
                }
                j++;
            }
            model.addRow(new Object[]{entry.getKey().format(formatter), text});
        }
    }

    static public boolean addTask(JTextField titleField, JCheckBox activeField,
                                  JCheckBox repetitive, JTextField timeField,
                                  JTextField startTime, JTextField endTime,
                                  JTextField intervalField, JComboBox<String> unit) {
        //add repetitive task
        //read data from AddForm
        String title = titleField.getText();
        try {
            if (title.equals("")) {
                titleMessage();
                return false;
            }
            int max = 0;
            int i = 0;
            for (Object task1 : taskList) {
                String title1 = ((Task) task1).getTitle();
                if (Pattern.matches("^" + title + " [0-9]$", title1)) {
                    i = Integer.parseInt(title1, title1.length() - 1,
                            title1.length(), 10) + 1;
                } else if (Pattern.matches("^" + title + " [0-9]{2}$", title1)) {
                    i = Integer.parseInt(((Task) task1).getTitle(), title1.length() - 2,
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
            Task task;
            if (repetitive.isSelected()) {
                LocalDateTime start =
                        LocalDateTime.parse(startTime.getText(), formatter);
                LocalDateTime end =
                        LocalDateTime.parse(endTime.getText(), formatter);
                int interval;
                switch (unit.getSelectedIndex()){
                    case 0:
                        interval = (int) (Double.parseDouble(
                                intervalField.getText()) * 60);
                        break;
                    case 2:
                        interval = (int) (Double.parseDouble(
                                intervalField.getText()) * 86400);
                        break;
                    default:
                        interval = (int) (Double.parseDouble(
                                intervalField.getText()) * 3600);
                        break;
                }
                //create repetitive task
                task = new Task(title, start, end, interval);
            } else {
                LocalDateTime time = LocalDateTime.parse(
                        timeField.getText(), formatter);
                task = new Task(title, time); //create non repetitive task
            }
            task.setActive(activeField.isSelected());
            taskList.add(task); //add created task to the list
            TaskIO.writeText(taskList,
                    new File("src/main/resources/data.txt")); //write list to the file
            JOptionPane.showMessageDialog(null,
                    "Task was added successfully.",
                    "Message", JOptionPane.INFORMATION_MESSAGE);
            if (taskList.size() == 1) {
                //if list was empty enable all buttons on MainForm
                MainForm.enableButtons();
            }
            log.info("Task was added to list");
            notificationManager.setTaskList(taskList);
            return true;
        } catch (DateTimeParseException exception) {
            dateMessage(exception);
            return false;
        } catch (NumberFormatException exception) {
            intervalMessage(exception);
            return false;
        } catch (IllegalArgumentException exception){
            datesMessage(exception);
            return false;
        }
    }

    public static void deleteTask(JComboBox<String> titleList) {
        String title = String.valueOf(titleList.getSelectedItem());
        int i = 0;
        for (Object task:taskList) {
            if (((Task) task).getTitle().equals(title)) {
                taskList.remove((Task) task); //delete chosen task from list
                TaskIO.writeText(taskList,
                        new File("src/main/resources/data.txt")); //write list to the file
                JOptionPane.showMessageDialog(null,
                            "Task was deleted successfully.",
                        "Message", JOptionPane.INFORMATION_MESSAGE);
                log.info("Task was deleted from list");
                notificationManager.setTaskList(taskList);
                if (taskList.size() == 0) {
                    MainForm.disableButtons();
                    log.info("List is empty");
                }
                i++;
                break;
            }
        }
        if (i == 0) {
            JOptionPane.showMessageDialog(null,
                     "Chosen task already does not exist",
                    "No such task.", JOptionPane.ERROR_MESSAGE);
            log.error("No such task");
        }
    }

    static public boolean editTask(JComboBox<String> titleList, JTextField titleField,
                                   JCheckBox activeField,
                                   JCheckBox repetitive, JTextField timeField,
                                   JTextField startTime, JTextField endTime,
                                   JTextField intervalField, JComboBox<String> unit) {
        //read data from fields
        Task task = taskList.getTask(titleList.getSelectedIndex());
        String title = titleField.getText();
        try {
            if (title.equals("")) {
                titleMessage();
                return false;
            }
            int max = 0;
            int i = 0;
            for (Object task1 : taskList) {
                if (!task.equals(task1)) {
                    String title1 = ((Task) task1).getTitle();
                    if (Pattern.matches("^" + title + " [0-9]$", title1)) {
                        i = Integer.parseInt(title1, title1.length() - 1,
                                title1.length(), 10) + 1;
                    } else if (Pattern.matches("^" + title + " [0-9]{2}$", title1)) {
                        i = Integer.parseInt(((Task) task1).getTitle(), title1.length() - 2,
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
            if (repetitive.isSelected()) {
                LocalDateTime start =
                        LocalDateTime.parse(startTime.getText(), formatter);
                LocalDateTime end =
                        LocalDateTime.parse(endTime.getText(), formatter);
                int interval;
                switch (unit.getSelectedIndex()){
                    case 0:
                        interval = (int) (Double.parseDouble(
                                intervalField.getText()) * 60);
                        break;
                    case 2:
                        interval = (int) (Double.parseDouble(
                                intervalField.getText()) * 86400);
                        break;
                    default:
                        interval = (int) (Double.parseDouble(
                                intervalField.getText()) * 3600);
                        break;
                }
                task.setTime(start, end, interval);
            } else {
                LocalDateTime time = LocalDateTime.parse(
                        timeField.getText(), formatter);
                task.setTime(time); //create non repetitive task
            }
            task.setTitle(title);
            task.setActive(activeField.isSelected());
            TaskIO.writeText(taskList,
                    new File("src/main/resources/data.txt")); //write list to the file
            JOptionPane.showMessageDialog(null,
                    "Task was edited successfully.",
                    "Message", JOptionPane.INFORMATION_MESSAGE);
            log.info("Task was edited");
            notificationManager.setTaskList(taskList);
            return true;
        } catch (DateTimeParseException exception) {
            dateMessage(exception);
            return false;
        } catch (NumberFormatException exception) {
            intervalMessage(exception);
            return false;
        } catch (IllegalArgumentException exception) {
            datesMessage(exception);
            return false;
        }
    }

    public static void deleteAll() {
        taskList = new ArrayTaskList();
        TaskIO.writeText(taskList,
                new File("src/main/resources/data.txt")); //write list to the file
        log.info("All tasks was deleted from list");
        log.info("List is empty");
        notificationManager.setTaskList(taskList);
        MainForm.disableButtons();
    }

    private static void titleMessage() {
        JOptionPane.showMessageDialog(null,
                "Fill field for title.",
                "Empty title field", JOptionPane.ERROR_MESSAGE);
        log.error("Attempt to add task with empty title");
    }

    private static void intervalMessage(NumberFormatException exception) {
        JOptionPane.showMessageDialog(null,
                "Interval should be a number.",
                "Incorrect interval format", JOptionPane.ERROR_MESSAGE);
        log.error("Incorrect interval format." + exception);
    }

    private static void datesMessage(Exception exception) {
        JOptionPane.showMessageDialog(null,
                exception,
                "Incorrect dates or interval", JOptionPane.ERROR_MESSAGE);
        log.error(exception);
    }

    private static void dateMessage(DateTimeParseException exception) {
        JOptionPane.showMessageDialog(null,
                "Fill field for time with format 'yyyy-MM-dd HH:mm'.",
                "Incorrect time format", JOptionPane.ERROR_MESSAGE);
        log.error("Incorrect time format. " + exception);
    }
}
