package ua.edu.sumdu.j2se.rozghon.tasks;

import ua.edu.sumdu.j2se.rozghon.tasks.view.MainForm;
import ua.edu.sumdu.j2se.rozghon.tasks.controller.Controller;

import org.apache.log4j.Logger;

public class Main {
	private static final Logger log = Logger.getLogger(Main.class);

	/**
	 * Application entry point.
	 * Method creates main form and controller.
	 * @param args don't used
	 */
	public static void main(String[] args) {
		log.info("Start");
		MainForm mainForm = new MainForm(); //create MainForm
		Controller controller = Controller.getInstance();
		controller.setMainForm(mainForm);
	}
}
