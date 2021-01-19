package ua.edu.sumdu.j2se.rozghon.tasks.view;

import org.apache.log4j.Logger;
import ua.edu.sumdu.j2se.rozghon.tasks.controller.notification.MailNotification;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

public class MailSettings extends JFrame {
    private static final Logger log = Logger.getLogger(MailSettings.class);
    private JTextField textField;
    private File file;
    private Properties properties;

    MailSettings() {
        super("Mail settings");
        JPanel panel1 = new JPanel();
        JLabel label = new JLabel("Current mail for notification:");
        //add field for input mail
        textField = new JTextField(20);
        file = new File("src/main/resources/mail.properties.txt");
        properties = new Properties();
        //read properties from file
        try {
            properties.load(new FileReader(file));
            fillTextField();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "File with mail properties not found",
                    "File not found.", JOptionPane.ERROR_MESSAGE);
            log.error(e);
        }
        panel1.add(label);
        panel1.add(textField);
        JPanel panel2 = new JPanel();
        //add button for saving
        JButton save = new JButton("Save");
        save.addActionListener(new SaveAction());
        panel2.add(save);
        Box contents = new Box(BoxLayout.Y_AXIS);
        contents.add(panel1);
        contents.add(panel2);
        setContentPane(contents);
        setIconImage(Toolkit.getDefaultToolkit().getImage(
                "src/main/resources/mail.png"));
        setSize(400, 100);
        setVisible(true);
    }

    private void fillTextField() {
        String recipient = properties.getProperty("recipient");
        if (recipient != null) {
            //if mail was added fill field with current mail
            textField.setText(recipient);
        }
    }

    public class SaveAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            //read address from textField
		    String recipient = textField.getText();
		    if (!recipient.equals("")) {
		        //if textField is not empty check address
		        if (checkAddress(recipient)) {
		            //if address is valid add it to properties
                    properties.put("recipient", recipient);
                    JOptionPane.showMessageDialog(null,
                            "New mail is saved.",
                            "Message", JOptionPane.INFORMATION_MESSAGE);
                    log.info("New mail address is saved.");
                    //activate mail notifications
                    MailNotification.setActive(true);
                    try {
                        //write properties to file
                        properties.store(new FileWriter(file), null);
                    } catch (IOException exception) {
                        log.error(exception);
                    }
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Input valid email address",
                            "Invalid Address.", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                //if textField is empty remove address
		        properties.remove("recipient");
                JOptionPane.showMessageDialog(null,
                        "Mail was deleted.",
                        "Message", JOptionPane.INFORMATION_MESSAGE);
                log.info("Mail address was deleted.");
                //deactivate mail notification
                MailNotification.setActive(false);
                try {
                    properties.store(new FileWriter(file), null);
                } catch (IOException exception) {
                    log.error(exception);
                }
                dispose();
            }
        }
    }

    public boolean checkAddress(String recipient) {
        String pattern =
                "^(([a-zA-Z0-9])+[\\.-])*([a-zA-Z0-9])+"
                       + "@(([a-zA-Z0-9])+[\\.-])+([a-zA-Z0-9])+$";
        return Pattern.matches(pattern, recipient);
    }
}
