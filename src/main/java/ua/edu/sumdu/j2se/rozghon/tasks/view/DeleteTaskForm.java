package ua.edu.sumdu.j2se.rozghon.tasks.view;

import ua.edu.sumdu.j2se.rozghon.tasks.controller.Controller;
import ua.edu.sumdu.j2se.rozghon.tasks.model.AbstractTaskList;
import ua.edu.sumdu.j2se.rozghon.tasks.model.Task;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class DeleteTaskForm extends JFrame {
    private JComboBox<String> comboBox;

    public DeleteTaskForm(AbstractTaskList taskList) {
        super("Delete Task");
        fillContent(taskList);
        setIconImage(Toolkit.getDefaultToolkit().getImage(
                "src/main/resources/task.png"));
        setSize(300, 220);
        setVisible(true);
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            dispose();
            Controller.closeDeleteForm();
        }
    }

    private void fillContent(AbstractTaskList taskList) {
        JPanel panel = new JPanel();
        //create comboBox with task list to choose task for deletion
        comboBox = new JComboBox<>();
        comboBox.setEditable(true);
        for (Object task:taskList) {
            //fill comboBox with task titles
            comboBox.addItem(((Task) task).getTitle());
        }
        panel.add(comboBox);
        //create button 'Delete'
        JButton delete = new JButton("Delete");
        delete.addActionListener(new Controller.Delete());
        panel.add(delete);
        Box contents = new Box(BoxLayout.Y_AXIS);
        contents.add(panel);
        setContentPane(contents);
    }

    public String getTaskTitle() {
        return String.valueOf(comboBox.getSelectedItem());
    }

    public void message() {
        JOptionPane.showMessageDialog(null,
                "Task was deleted successfully.",
                "Message", JOptionPane.INFORMATION_MESSAGE);
    }
}
