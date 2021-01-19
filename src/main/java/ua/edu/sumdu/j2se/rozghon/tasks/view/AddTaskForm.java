package ua.edu.sumdu.j2se.rozghon.tasks.view;

import ua.edu.sumdu.j2se.rozghon.tasks.controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public AddTaskForm() {
        super("Add Task");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        createContent();
        addToolTips();
        fillContent();
        setIconImage(Toolkit.getDefaultToolkit().getImage(
                "src/main/resources/task.png"));
        setSize(300, 220);
        setVisible(true);
    }

    private void createContent() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
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
        repetitive.addItemListener(new RepeatedAction());
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
        save.addActionListener(new AddSave());
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

    public class RepeatedAction implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                //show fields for repetitive task
                timePanel.setVisible(false);
                startPanel.setVisible(true);
                endPanel.setVisible(true);
                intervalPanel.setVisible(true);
            } else {
                //hide fields for repetitive task
                timePanel.setVisible(true);
                startPanel.setVisible(false);
                endPanel.setVisible(false);
                intervalPanel.setVisible(false);
            }
        }
    }

    public class AddSave implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (Controller.addTask(titleField, active, repetitive,
                    timeField, startField, endField, intervalField, unit)) {
                dispose(); //close form if task was added successfully
            }
        }
    }
}
