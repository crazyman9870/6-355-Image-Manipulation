package cs355.solution;

import java.awt.Color;

import cs355.GUIFunctions;
import cs355.controller.Controller;
import cs355.model.drawing.Model;
import cs355.model.scene.SceneModel;
import cs355.view.View;

/**
 * This is the main class. The program starts here.
 * Make you add code below to initialize your model,
 * view, and controller and give them to the app.
 */
public class CS355 {

	public static final int SCREENSIZE = 2048;
	public static final int SCROLLSTART = 512;
	public static final int STARTINGPOSITION = 1024 - SCROLLSTART/2;
	/**
	 * This is where it starts.
	 * @param args = the command line arguments
	 */
	public static void main(String[] args) {

		Controller controller = Controller.instance();
		View view = new View();
		Model.instance().addObserver(view);
//		SceneModel.instance().addObserver(view);
		SceneModel.instance();

		GUIFunctions.createCS355Frame(controller, view);
		GUIFunctions.changeSelectedColor(Color.WHITE);

		GUIFunctions.setHScrollBarMax(SCREENSIZE);
		GUIFunctions.setVScrollBarMax(SCREENSIZE);

		GUIFunctions.setVScrollBarPosit(STARTINGPOSITION);
		GUIFunctions.setHScrollBarPosit(STARTINGPOSITION);

		GUIFunctions.setHScrollBarKnob(SCROLLSTART);
		GUIFunctions.setVScrollBarKnob(SCROLLSTART);
		
		GUIFunctions.refresh();
	}
}
