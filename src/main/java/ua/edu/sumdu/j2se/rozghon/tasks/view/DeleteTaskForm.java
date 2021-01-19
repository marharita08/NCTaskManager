package ua.edu.sumdu.j2se.rozghon.tasks.view;

import ua.edu.sumdu.j2se.rozghon.tasks.controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeleteTaskForm extends JFrame {
    private JComboBox<String> comboBox;

    public DeleteTaskForm() {
        super("Delete Task");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        fillContent();
        setIconImage(Toolkit.getDefaultToolkit().getImage(
                "src/main/resources/task.png"));
        setSize(300, 220);
        setVisible(true);
    }

    private void fillContent() {
        JPanel panel = new JPanel();
        //create comboBox with task list to choose task for deletion
        comboBox = new JComboBox<>();
        comboBox.setEditable(true);
        Controller.fillComboBox(comboBox);
        panel.add(comboBox);
        //create button 'Delete'
        JButton delete = new JButton("Delete");
        delete.addActionListener(new Delete());
        panel.add(delete);
        Box contents = new Box(BoxLayout.Y_AXIS);
        contents.add(panel);
        setContentPane(contents);
    }

    public class Delete implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Controller.deleteTask(comboBox);
            dispose(); //close form after deletion
        }
    }
}
