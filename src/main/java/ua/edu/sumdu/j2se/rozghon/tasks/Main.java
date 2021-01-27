package ua.edu.sumdu.j2se.rozghon.tasks;

public class Main {

	private static final Logger log = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		log.info("Start");
		MainForm mainForm = new MainForm(); //create MainForm
		Controller.readData(mainForm); //read data from file to buffer
		Controller.createNotificationManager();
	}
}
