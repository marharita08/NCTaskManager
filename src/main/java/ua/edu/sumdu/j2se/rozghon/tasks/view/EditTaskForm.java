package ua.edu.sumdu.j2se.rozghon.tasks.view;

import org.apache.log4j.Logger;
import ua.edu.sumdu.j2se.rozghon.tasks.controller.Controller;
import ua.edu.sumdu.j2se.rozghon.tasks.model.AbstractTaskList;
import ua.edu.sumdu.j2se.rozghon.tasks.model.Task;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class EditTaskForm extends JFrame {
    private JPanel listPanel;
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
    private JComboBox<String> comboBox;
    private JComboBox<String> unit;
    private static final Logger log = Logger.getLogger(EditTaskForm.class);
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public EditTaskForm(AbstractTaskList taskList) {
        super("Edit Task");
        createContent(taskList);
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
            Controller.closeEditForm();
        }
    }

    private void createContent(AbstractTaskList taskList) {
        listPanel = new JPanel();
        //create comboBox with task list to choose task for edition
        comboBox = new JComboBox<>();
        comboBox.setEditable(true);
        for (Object task:taskList) {
            //fill comboBox with task titles
            comboBox.addItem(((Task) task).getTitle());
        }
        JButton edit = new JButton("Edit");
        edit.addActionListener(new Controller.EditAction());
        listPanel.add(comboBox);
        listPanel.add(edit);
        //add fields for input tasks parameters
        titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Title:");
        titleField = new JTextField(15);
        titlePanel.add(titleLabel);
        titlePanel.add(titleField);
        titlePanel.setVisible(false);
        checkPanel = new JPanel();
        active = new JCheckBox("active");
        repetitive = new JCheckBox("repetitive");
        checkPanel.add(active);
        checkPanel.add(repetitive);
        repetitive.addItemListener(new Controller.RepeatedActionEdit());
        checkPanel.setVisible(false);
        timePanel = new JPanel();
        JLabel timeLabel = new JLabel("Time:");
        timeField = new JTextField(15);
        timePanel.add(timeLabel);
        timePanel.add(timeField);
        timePanel.setVisible(false);
        startPanel = new JPanel();
        JLabel startLabel = new JLabel("Start time:");
        startField = new JTextField(15);
        startPanel.add(startLabel);
        startPanel.add(startField);
        startPanel.setVisible(false);
        endPanel = new JPanel();
        JLabel endLabel = new JLabel("End time:");
        endField = new JTextField(15);
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
        save.addActionListener(new Controller.EditSave());
        checkPanel.add(save);
    }

    private void addToolTips() {
        titleField.setToolTipText("This field is "
                + "for input title of editing task");
        timeField.setToolTipText("This field is for input time "
                + "of editing task in format 'yyyy-MM-dd HH:mm'.");
        startField.setToolTipText("This field is for input start time"
                + " of editing task in format 'yyyy-MM-dd HH:mm'.");
        endField.setToolTipText("This field is for input "
                + "end time of editing task in format 'yyyy-MM-dd HH:mm'.");
        intervalField.setToolTipText("This field is for "
                + "input interval of editing task.");
    }

    private void fillContent() {
        Box contents = new Box(BoxLayout.Y_AXIS);
        contents.add(listPanel);
        contents.add(titlePanel);
        contents.add(timePanel);
        contents.add(startPanel);
        contents.add(endPanel);
        contents.add(intervalPanel);
        contents.add(checkPanel);
        setContentPane(contents);
    }

    public void fillEditFields(AbstractTaskList taskList) {
        String title = String.valueOf(comboBox.getSelectedItem());
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
                    endField.setText(
                            task.getTime().plusDays(1).format(formatter));
                }
                //show fields
                listPanel.setVisible(false);
                titlePanel.setVisible(true);
                checkPanel.setVisible(true);
                if (repetitive.isSelected()) {
                    startPanel.setVisible(true);
                    endPanel.setVisible(true);
                    intervalPanel.setVisible(true);
                } else {
                    timePanel.setVisible(true);
                }
                return;
            }
        }
        JOptionPane.showMessageDialog(null,
                "Chosen task already does not exist",
                "No such task.", JOptionPane.ERROR_MESSAGE);
        log.error("No such task");
        dispose();
        Controller.closeEditForm();
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

    public String getTaskTitle(){
        return titleField.getText();
    }

    public int getTitleIndex() {
        return comboBox.getSelectedIndex();
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

    public boolean active() {
        return active.isSelected();
    }

    public boolean repetitive() {
        return repetitive.isSelected();
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

    public void message() {
        JOptionPane.showMessageDialog(null,
                "Task was edited successfully.",
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
