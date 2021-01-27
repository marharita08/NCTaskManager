package ua.edu.sumdu.j2se.rozghon.tasks.view;

import org.apache.log4j.Logger;
import ua.edu.sumdu.j2se.rozghon.tasks.controller.Controller;
import ua.edu.sumdu.j2se.rozghon.tasks.model.AbstractTaskList;
import ua.edu.sumdu.j2se.rozghon.tasks.model.Task;
import ua.edu.sumdu.j2se.rozghon.tasks.model.Tasks;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public class MainForm extends JFrame {
    private JTable table;
    private JComboBox<String> comboBox;
    private JMenuItem deleteTask;
    private JMenuItem deleteAllTasks;
    private JMenuItem editTask;
    private JButton calendar;
    private JButton showTasks;
    private static final Logger log = Logger.getLogger(MainForm.class);
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public MainForm() {
        super("Task Manager");
        createMenu();
        createContent();
        fillContent();
        setSize(500, 300);
        setIconImage(Toolkit.getDefaultToolkit().getImage(
                "src/main/resources/task.png"));
        setVisible(true);
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            log.info("Exit");
            System.exit(0);
        }
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        //create menu items
        JMenuItem addTask = new JMenuItem("Add task");
        addTask.addActionListener(new Controller.AddTask());
        editTask = new JMenuItem("Edit task");
        editTask.addActionListener(new Controller.EditTask());
        deleteTask = new JMenuItem("Delete task");
        deleteTask.addActionListener(new Controller.DeleteTask());
        deleteAllTasks = new JMenuItem("Delete all tasks");
        deleteAllTasks.addActionListener(new Controller.DeleteAllAction());
        JMenuItem mail = new JMenuItem("Mail settings");
        mail.addActionListener(new Controller.MailAction());
        //add menu items to menu
        menu.add(addTask);
        menu.add(editTask);
        menu.add(deleteTask);
        menu.add(deleteAllTasks);
        menu.add(mail);
        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    private void createContent() {
        DefaultTableModel model = new DefaultTableModel();
        table = new JTable(model); //create table
        //create buttons
        calendar = new JButton("Calendar");
        calendar.addActionListener(new Controller.CalendarAction());
        showTasks = new JButton("Show All Tasks");
        showTasks.addActionListener(new Controller.ShowTaskAction());
        //create comboBox
        comboBox = new JComboBox<>();
        comboBox.setEditable(true);
        comboBox.addItem("week");
        comboBox.addItem("month");
        comboBox.addItem("year");
    }

    private void fillContent() {
        Box contents = new Box(BoxLayout.Y_AXIS);
        contents.add(new JScrollPane(table));
        JPanel panel = new JPanel();
        panel.add(calendar);
        panel.add(comboBox);
        panel.add(showTasks);
        contents.add(panel);
        setContentPane(contents);
    }

    public void enableButtons() {
        showTasks.setEnabled(true);
        calendar.setEnabled(true);
        deleteTask.setEnabled(true);
        editTask.setEnabled(true);
        deleteAllTasks.setEnabled(true);
    }

    public static void disableButtons() {
        showTasks.setEnabled(false);
        calendar.setEnabled(false);
        editTask.setEnabled(false);
        deleteTask.setEnabled(false);
        deleteAllTasks.setEnabled(false);
    }

    public void showTasks(AbstractTaskList taskList) {
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
                double interval =
                        (double) ((Task) task).getRepeatInterval() / 86400;
                String unit = " days";
                if (interval < 1) {
                    interval =
                            (double) ((Task) task).getRepeatInterval() / 3600;
                    unit = " hours";
                }
                if (interval < 1) {
                    interval =
                            (double) ((Task) task).getRepeatInterval() / 60;
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

    public void showCalendar(AbstractTaskList taskList) {
        //fill table with data about active tasks in date-tasks format
        SortedMap<LocalDateTime, Set<Task>> map;
        switch (comboBox.getSelectedIndex()) {
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

    public boolean confirmation() {
        return JOptionPane.showConfirmDialog(
                null,
                "Do you confirm deletion of all tasks?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
