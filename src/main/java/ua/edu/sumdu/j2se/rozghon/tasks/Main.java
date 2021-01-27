package ua.edu.sumdu.j2se.rozghon.tasks;

public class Main {

	private static final Logger log = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		log.info("Start");
		MainForm mainForm = new MainForm(); //create MainForm
		Controller controller = Controller.getInstance();
		controller.setMainForm(mainForm);
	}
}
