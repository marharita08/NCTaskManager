package ua.edu.sumdu.j2se.rozghon.tasks.view;

import org.apache.log4j.Logger;
import ua.edu.sumdu.j2se.rozghon.tasks.controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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

    public EditTaskForm() {
        super("Edit Task");
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
        listPanel = new JPanel();
        //create comboBox with task list to choose task for edition
        comboBox = new JComboBox<>();
        comboBox.setEditable(true);
        Controller.fillComboBox(comboBox);
        JButton edit = new JButton("Edit");
        edit.addActionListener(new EditAction());
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
        repetitive.addItemListener(new RepeatedAction());
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
        unit = new JComboBox<String>();
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
        save.addActionListener(new EditSave());
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

    public class EditAction implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            //fill fields with parameters of chosen task
            if (Controller.fillEditFields(titleField, active,
                    repetitive, timeField, startField,
                    endField, intervalField, comboBox, unit)) {
                //if fields was fill successfully, show they
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
            } else {
                JOptionPane.showMessageDialog(null,
                        "Chosen task already does not exist",
                        "No such task.", JOptionPane.ERROR_MESSAGE);
                log.error("No such task");
                dispose();
            }
        }
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

    public class EditSave implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (Controller.editTask(comboBox, titleField, active, repetitive,
                    timeField, startField, endField, intervalField, unit)) {
                dispose(); //close form if task was edited successfully
            }
        }
    }
}
