package ua.edu.sumdu.j2se.rozghon.tasks.view;

import org.apache.log4j.Logger;
import ua.edu.sumdu.j2se.rozghon.tasks.controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AddTaskForm extends JFrame {
    private JPanel titlePanel;
    private JPanel checkPanel;
    private JPanel timePanel;
    private JPanel startPanel;
    private JPanel endPanel;
    private JPanel intervalPanel;
    private JTextField titleField;
    private JCheckBox active;
    private JCheckBox repetitive;
    private JTextField timeField;
    private JTextField startField;
    private JTextField endField;
    private JTextField intervalField;
    private JComboBox<String> unit;
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Logger log = Logger.getLogger(AddTaskForm.class);

    public AddTaskForm() {
        super("Add Task");
        createContent();
        addToolTips();
        fillContent();
        setIconImage(Toolkit.getDefaultToolkit().getImage(
                "src/main/resources/task.png"));
        setSize(300, 220);
        setVisible(true);
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            dispose();
            Controller.getInstance().closeAddForm();
        }
    }

    private void createContent() {
        //add fields for input tasks parameters
        titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Title:");
        titleField = new JTextField(15);
        titlePanel.add(titleLabel);
        titlePanel.add(titleField);
        checkPanel = new JPanel();
        active = new JCheckBox("active");
        repetitive = new JCheckBox("repetitive");
        checkPanel.add(active);
        checkPanel.add(repetitive);
        repetitive.addItemListener(new Controller.RepeatedActionAdd());
        timePanel = new JPanel();
        JLabel timeLabel = new JLabel("Time:");
        timeField = new JTextField(LocalDateTime.now().format(formatter));
        timePanel.add(timeLabel);
        timePanel.add(timeField);
        startPanel = new JPanel();
        JLabel startLabel = new JLabel("Start time:");
        startField = new JTextField(LocalDateTime.now().format(formatter));
        startPanel.add(startLabel);
        startPanel.add(startField);
        startPanel.setVisible(false);
        endPanel = new JPanel();
        JLabel endLabel = new JLabel("End time:");
        endField = new JTextField(
                LocalDateTime.now().plusDays(1).format(formatter));
        endPanel.add(endLabel);
        endPanel.add(endField);
        endPanel.setVisible(false);
        intervalPanel = new JPanel();
        JLabel intervalLabel = new JLabel("Interval:");
        intervalField = new JTextField(6);
        unit = new JComboBox<>();
        unit.setEditable(true);
        unit.addItem("minutes");
        unit.addItem("hours");
        unit.addItem("days");
        intervalPanel.add(intervalLabel);
        intervalPanel.add(intervalField);
        intervalPanel.add(unit);
        intervalPanel.setVisible(false);
        //add button for saving
        JButton save = new JButton("Save");
        save.addActionListener(new Controller.AddSave());
        checkPanel.add(save);
    }

    private void addToolTips() {
        titleField.setToolTipText(
                "This field is for input title of adding task");
        timeField.setToolTipText("This field is for input time "
                + "of adding task in format 'yyyy-MM-dd HH:mm'.");
        startField.setToolTipText("This field is for input start time "
                + "of adding task in format 'yyyy-MM-dd HH:mm'.");
        endField.setToolTipText("This field is for input end time"
                + " of adding task in format 'yyyy-MM-dd HH:mm'.");
        intervalField.setToolTipText("This field is for input"
                + " interval of adding task.");
    }

    private void fillContent() {
        Box contents = new Box(BoxLayout.Y_AXIS);
        contents.add(titlePanel);
        contents.add(timePanel);
        contents.add(startPanel);
        contents.add(endPanel);
        contents.add(intervalPanel);
        contents.add(checkPanel);
        setContentPane(contents);
    }

    public String getTaskTitle() {
        return titleField.getText();
    }

    public LocalDateTime getTime() {
        try {
            return LocalDateTime.parse(
                    timeField.getText(), formatter);
        } catch (DateTimeParseException exception) {
            exceptionMessages(exception);
            return null;
        }
    }

    public LocalDateTime getStartTime() {
        try {
            return LocalDateTime.parse(
                    startField.getText(), formatter);
        } catch (DateTimeParseException exception) {
            exceptionMessages(exception);
            return null;
        }
    }

    public LocalDateTime getEndTime() {
        try {
            return LocalDateTime.parse(
                    endField.getText(), formatter);
        } catch (DateTimeParseException exception) {
            exceptionMessages(exception);
            return null;
        }
    }

    public int getInterval() {
        try {
            switch (unit.getSelectedIndex()) {
                case 0:
                    return (int) (Double.parseDouble(
                            intervalField.getText()) * 60);
                case 2:
                    return (int) (Double.parseDouble(
                            intervalField.getText()) * 86400);
                default:
                    return (int) (Double.parseDouble(
                            intervalField.getText()) * 3600);
            }
        } catch (NumberFormatException e) {
            exceptionMessages(e);
            return 0;
        }
    }

    public boolean active() {
        return active.isSelected();
    }

    public boolean repetitive() {
        return repetitive.isSelected();
    }

    public void showRepetitiveFields() {
        //show fields for repetitive task
        timePanel.setVisible(false);
        startPanel.setVisible(true);
        endPanel.setVisible(true);
        intervalPanel.setVisible(true);
    }

    public void hideRepetitiveFields() {
        //hide fields for repetitive task
        timePanel.setVisible(true);
        startPanel.setVisible(false);
        endPanel.setVisible(false);
        intervalPanel.setVisible(false);
    }

    public void message() {
        JOptionPane.showMessageDialog(null,
                "Task was added successfully.",
                "Message", JOptionPane.INFORMATION_MESSAGE);
    }

    public void exceptionMessages(Exception exception) {
        if (exception instanceof DateTimeParseException) {
            JOptionPane.showMessageDialog(null,
                    "Fill field for time with format 'yyyy-MM-dd HH:mm'.",
                    "Incorrect time format", JOptionPane.ERROR_MESSAGE);
            log.error("Incorrect time format. " + exception);
        } else if (exception instanceof NumberFormatException) {
            JOptionPane.showMessageDialog(null,
                    "Interval should be a number.",
                    "Incorrect interval format", JOptionPane.ERROR_MESSAGE);
            log.error("Incorrect interval format." + exception);
        } else {
            JOptionPane.showMessageDialog(null,
                    exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            log.error(exception);
        }
    }
}
