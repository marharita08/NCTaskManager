package ua.edu.sumdu.j2se.rozghon.tasks.view;

import org.apache.log4j.Logger;
import ua.edu.sumdu.j2se.rozghon.tasks.controller.Controller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class MainForm extends JFrame {
    private JTable table;
    private JComboBox<String> comboBox;
    private static JMenuItem deleteTask;
    private static JMenuItem deleteAllTasks;
    private static JMenuItem editTask;
    private static JButton calendar;
    private static JButton showTasks;
    private static final Logger log = Logger.getLogger(MainForm.class);

    public MainForm(){
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

    private void createMenu(){
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        //create menu items
        JMenuItem addTask = new JMenuItem("Add task");
        addTask.addActionListener(new AddTask());
        editTask = new JMenuItem("Edit task");
        editTask.addActionListener(new EditTask());
        deleteTask = new JMenuItem("Delete task");
        deleteTask.addActionListener( new DeleteTask());
        deleteAllTasks = new JMenuItem("Delete all tasks");
        deleteAllTasks.addActionListener(new DeleteAllAction());
        JMenuItem mail = new JMenuItem("Mail settings");
        mail.addActionListener(new MailAction());
        //add menu items to menu
        menu.add(addTask);
        menu.add(editTask);
        menu.add(deleteTask);
        menu.add(deleteAllTasks);
        menu.add(mail);
        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    private void createContent(){
        DefaultTableModel model = new DefaultTableModel();
        table = new JTable(model); //create table
        //create buttons
        calendar = new JButton("Calendar");
        calendar.addActionListener(new CalendarAction());
        showTasks = new JButton("Show All Tasks");
        showTasks.addActionListener(new ShowTaskAction());
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

    public static void enableButtons () {
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

    public class DeleteTask implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            new DeleteTaskForm();
        }
    }

    public class AddTask implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            new AddTaskForm();
        }
    }

    public class EditTask implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            new EditTaskForm();
        }
    }

    public class ShowTaskAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            Controller.showTasks(table);
        }
    }

    public class CalendarAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            Controller.showCalendar(table, comboBox);
        }
    }

    public class MailAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            new MailSettings();
        }
    }

    public class DeleteAllAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            int result = JOptionPane.showConfirmDialog(
                    null,
                    "Do you confirm deletion of all tasks?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                Controller.deleteAll();
            }
        }
    }
}
