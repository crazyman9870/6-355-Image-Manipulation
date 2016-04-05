package cs355.controller;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.Iterator;

import cs355.GUIFunctions;
import cs355.controller.IControllerState.stateType;
import cs355.model.drawing.*;
import cs355.model.scene.Instance;
import cs355.model.scene.Point3D;
import cs355.model.scene.SceneModel;
import cs355.solution.CS355;

public class Controller implements CS355Controller {

	private static Controller _instance;
	
	public static final double ZOOMIN = 2.0;
	public static final double ZOOMOUT = .5;
	public static final double ZOOMMIN = .25;
	public static final double ZOOMMAX = 4.0;
	private static final float nearPlane = 1.0f;
	private static final float farPlane = 250.0f;
	private final float movement = 1.2f;
	
	private double zoom;
	private double scrollerSize;
	private boolean updating;
	private Point2D.Double viewCenter;
	private IControllerState state;
	
	private Point3D cameraHome;
	private double rotationHome;

	//If the model had not been initialized, it will be.
	public static Controller instance() {
		if (_instance == null) 
			_instance = new Controller();
		return _instance;
	}
	
	private Controller() {
		this.zoom = 1.0;
		this.scrollerSize = 512;
		this.updating = false;
		this.viewCenter = new Point2D.Double(0,0);
		this.state = new ControllerNothingState();
		SceneModel.instance().setCameraPosition(new Point3D(0f, 1.5f, 25f));
		this.cameraHome = new Point3D(0f, 1.5f, 25f);
		this.rotationHome = SceneModel.instance().getYaw();
	}
	
	/* Mouse Events */

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		state.mousePressed(arg0);
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		state.mouseDragged(arg0);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		state.mouseReleased(arg0);
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	
	/* Button Events */
	
	@Override
	public void colorButtonHit(Color c) {
		Model.instance().setColor(c);
		GUIFunctions.changeSelectedColor(c);
	}

	@Override
	public void lineButtonHit() {
		Model.instance().setSelectedShapeIndex(-1);
		Model.instance().setCurrentShape(Shape.type.LINE);
		this.state = new ControllerDrawingState();
		GUIFunctions.refresh();
	}

	@Override
	public void squareButtonHit() {
		Model.instance().setSelectedShapeIndex(-1);
		Model.instance().setCurrentShape(Shape.type.SQUARE);
		this.state = new ControllerDrawingState();
		GUIFunctions.refresh();
	}

	@Override
	public void rectangleButtonHit() {
		Model.instance().setSelectedShapeIndex(-1);
		Model.instance().setCurrentShape(Shape.type.RECTANGLE);
		this.state = new ControllerDrawingState();
		GUIFunctions.refresh();
	}

	@Override
	public void circleButtonHit() {
		Model.instance().setSelectedShapeIndex(-1);
		Model.instance().setCurrentShape(Shape.type.CIRCLE);
		this.state = new ControllerDrawingState();
		GUIFunctions.refresh();
	}

	@Override
	public void ellipseButtonHit() {
		Model.instance().setSelectedShapeIndex(-1);
		Model.instance().setCurrentShape(Shape.type.ELLIPSE);
		this.state = new ControllerDrawingState();
		GUIFunctions.refresh();
	}

	@Override
	public void triangleButtonHit() {
		Model.instance().setSelectedShapeIndex(-1);
		Model.instance().setCurrentShape(Shape.type.TRIANGLE);
		this.state = new ControllerDrawingState();
		GUIFunctions.refresh();
	}
	
	@Override
	public void selectButtonHit() {
		this.state = new ControllerSelectState();
		GUIFunctions.refresh();
	}

	@Override
	public void zoomInButtonHit() {
		this.setZoom(ZOOMIN);
	}

	@Override
	public void zoomOutButtonHit() {
		this.setZoom(ZOOMOUT);
	}
	
	@Override
	public void hScrollbarChanged(int value) {
		viewCenter.x = value + scrollerSize / 2.0;
		if(!updating)
			Model.instance().changeMade();
	}

	@Override
	public void vScrollbarChanged(int value) {
		viewCenter.y = value + scrollerSize / 2.0;
		if(!updating)
			Model.instance().changeMade();
	}

	@Override
	public void toggle3DModelDisplay() {
//		System.out.println("3D BUTTON");
		this.state = new Controller3DState();
		Model.instance().changeMade();
		
	}

	@Override
	public void keyPressed(Iterator<Integer> iterator) {
		
		if(this.state.getType() != IControllerState.stateType.THREED)
			return;
		
		while (iterator.hasNext()) {
			
			switch(iterator.next())	{
				case KeyEvent.VK_W:
					SceneModel.instance().moveForward(this.movement);
					break;
					
				case KeyEvent.VK_A:
					SceneModel.instance().strafe(-this.movement);
					break;
				
				case KeyEvent.VK_S:
					SceneModel.instance().moveBackward(this.movement);
					break;
				
				case KeyEvent.VK_D:
					SceneModel.instance().strafe(this.movement);
					break;
				
				case KeyEvent.VK_Q:
					SceneModel.instance().yaw(this.movement);
					break;
				
				case KeyEvent.VK_E:
					SceneModel.instance().yaw(-this.movement);
					break;
				
				case KeyEvent.VK_R:
					SceneModel.instance().changeAltitude(this.movement);
					break;
				
				case KeyEvent.VK_F:
					SceneModel.instance().changeAltitude(-this.movement);
					break;
				
				case KeyEvent.VK_H:
					SceneModel.instance().setCameraPosition(cameraHome);
					SceneModel.instance().setCameraRotation(Math.toDegrees(rotationHome));
					break;
			}
		}
		
		GUIFunctions.refresh();
	}
	
	/* Menu Buttons */

	@Override
	public void saveDrawing(File file) {
		Model.instance().save(file);
	}

	@Override
	public void openDrawing(File file) {
		Model.instance().open(file);
		GUIFunctions.refresh();
	}

	@Override
	public void openScene(File file) {
		SceneModel.instance().open(file);
		this.cameraHome = SceneModel.instance().getCameraPosition();
		this.rotationHome = SceneModel.instance().getYaw();
		GUIFunctions.refresh();
	}

	@Override
	public void doMoveForward() {
		if(state.getType() == stateType.SELECT) {
			int currentShapeIndex = ((ControllerSelectState)state).getCurrentShapeIndex();
			if(currentShapeIndex != -1) {
				Model.instance().moveForward(currentShapeIndex);
				currentShapeIndex = Model.instance().getSelectedShapeIndex();
				((ControllerSelectState)state).setCurrentShapeIndex(currentShapeIndex);
			}
		}
	}

	@Override
	public void doMoveBackward() {
		if(state.getType() == stateType.SELECT) {
			int currentShapeIndex = ((ControllerSelectState)state).getCurrentShapeIndex();
			if(currentShapeIndex != -1) {
				Model.instance().moveBackward(currentShapeIndex);
				currentShapeIndex = Model.instance().getSelectedShapeIndex();
				((ControllerSelectState)state).setCurrentShapeIndex(currentShapeIndex);
			}
		}
	}

	@Override
	public void doSendToFront() {
		if(state.getType() == stateType.SELECT) {
			int currentShapeIndex = ((ControllerSelectState)state).getCurrentShapeIndex();
			if(currentShapeIndex != -1) {
				Model.instance().moveToFront(currentShapeIndex);
				currentShapeIndex = Model.instance().getSelectedShapeIndex();
				((ControllerSelectState)state).setCurrentShapeIndex(currentShapeIndex);
			}
		}
	}

	@Override
	public void doSendtoBack() {
		if(state.getType() == stateType.SELECT) {
			int currentShapeIndex = ((ControllerSelectState)state).getCurrentShapeIndex();
			if(currentShapeIndex != -1) {
				Model.instance().moveToBack(currentShapeIndex);
				currentShapeIndex = Model.instance().getSelectedShapeIndex();
				((ControllerSelectState)state).setCurrentShapeIndex(currentShapeIndex);
			}
		}
	}
	
	@Override
	public void doDeleteShape() {
		if(state.getType() == stateType.SELECT) {
			int currentShapeIndex = ((ControllerSelectState)state).getCurrentShapeIndex();
			if(currentShapeIndex != -1) {
				Model.instance().deleteShape(currentShapeIndex);
				currentShapeIndex = Model.instance().getSelectedShapeIndex();
				((ControllerSelectState)state).setCurrentShapeIndex(currentShapeIndex);
				Model.instance().changeMade();
			}
		}
	}

	/* Implement Later */
	
	@Override
	public void openImage(File file) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveImage(File file) {
		// TODO Auto-generated method stub

	}

	@Override
	public void toggleBackgroundDisplay() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doEdgeDetection() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSharpen() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doMedianBlur() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doUniformBlur() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doGrayscale() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doChangeContrast(int contrastAmountNum) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doChangeBrightness(int brightnessAmountNum) {
		// TODO Auto-generated method stub

	}
	
	/* Transforms - 2D Objects */
	
	public AffineTransform objectToWorld(Shape shape) {
		AffineTransform transform = new AffineTransform();
		//Translation
		transform.concatenate(new AffineTransform(1.0, 0, 0, 1.0, shape.getCenter().getX(), shape.getCenter().getY()));
		//Rotation
		transform.concatenate(new AffineTransform(Math.cos(shape.getRotation()), Math.sin(shape.getRotation()), -Math.sin(shape.getRotation()), Math.cos(shape.getRotation()), 0, 0));
		return transform;
	}
	
	public AffineTransform worldToView() {
		AffineTransform transform = new AffineTransform();
		//Scale
        transform.concatenate(new AffineTransform(zoom, 0, 0, zoom, 0, 0));
        //Translation
		transform.concatenate(new AffineTransform(1.0, 0, 0, 1.0, -viewCenter.getX() + 256*(1/zoom), -viewCenter.getY() + 256*(1/zoom)));
		return transform;
	}

	public AffineTransform objectToView(Shape shape) {
		AffineTransform transform = new AffineTransform();
		// World to View
        transform.concatenate(worldToView());
		// Object to World
		transform.concatenate(objectToWorld(shape));
		return transform;
	}
	
	public AffineTransform viewToWorld() {
		AffineTransform transform = new AffineTransform();
		//Translation
        transform.concatenate(new AffineTransform(1.0, 0, 0, 1.0, -(-viewCenter.getX() + 256*(1/zoom)), -(-viewCenter.getY() + 256*(1/zoom))));
        //Scale
        transform.concatenate(new AffineTransform(1/zoom, 0, 0, 1/zoom, 0, 0)); 
		return transform;
	}
	
	public AffineTransform worldToObject(Shape shape) {
		AffineTransform transform = new AffineTransform();
		//Rotation
		transform.concatenate(new AffineTransform(Math.cos(shape.getRotation()), -Math.sin(shape.getRotation()), Math.sin(shape.getRotation()), Math.cos(shape.getRotation()), 0.0, 0.0));
		//Translation
		transform.concatenate(new AffineTransform(1.0, 0.0, 0.0, 1.0, -shape.getCenter().getX(), -shape.getCenter().getY()));
		return transform;
	}
	
	public AffineTransform viewToObject(Shape shape) {
		AffineTransform transform = new AffineTransform();
		// World to object
		transform.concatenate(worldToObject(shape));
		// View to world
        transform.concatenate(viewToWorld());
		return transform;
	}
	
	public Point2D.Double viewPointToWorldPoint(MouseEvent arg0) {
		int x = arg0.getX();
		int y = arg0.getY();
		Point2D.Double point = new Point2D.Double((double)x, (double)y);
		return viewPointToWorldPoint(point);
	}
	
	public Point2D.Double viewPointToWorldPoint(Point2D.Double point) {
		Point2D.Double pointCopy = new Point2D.Double(point.getX(), point.getY());
		AffineTransform transform = new AffineTransform();
		transform.concatenate(new AffineTransform(1.0, 0, 0, 1.0, -(viewCenter.getX() + 256*(1/zoom)), -(viewCenter.getY() + 256*(1/zoom)))); //t
        transform.concatenate(new AffineTransform(1/zoom, 0, 0, 1/zoom, 0, 0));
        transform.transform(pointCopy, pointCopy);
        return pointCopy;
	}
	
	public Point2D.Double worldPointToViewPoint(Point2D.Double point) {
		Point2D.Double pointCopy = new Point2D.Double(point.getX(), point.getY());
		AffineTransform transform = new AffineTransform();
		transform.concatenate(new AffineTransform(zoom, 0, 0, zoom, 0, 0)); //scale
		transform.concatenate(new AffineTransform(1.0, 0, 0, 1.0, -viewCenter.getX() + 256*(1/zoom), -viewCenter.getY() + 256*(1/zoom))); //t
        transform.transform(pointCopy, pointCopy);
        return pointCopy;
	}
	
	public Point2D.Double objectPointToViewPoint(Shape shape, Point2D.Double point) {
		Point2D.Double pointCopy = new Point2D.Double(point.getX(), point.getY());
		AffineTransform transform = objectToView(shape);
        transform.transform(pointCopy, pointCopy);
        return pointCopy;
	}
	
	/* Transforms - 3D Objects */
	
	public AffineTransform objectToWorld3D(Instance inst) {
		AffineTransform transform = new AffineTransform();
		//Translation
		transform.concatenate(new AffineTransform(1.0, 0, 0, 1.0, inst.getPosition().x, inst.getPosition().y));
		//Rotation
		transform.concatenate(new AffineTransform(Math.cos(inst.getRotAngle()), Math.sin(inst.getRotAngle()), -Math.sin(inst.getRotAngle()), Math.cos(inst.getRotAngle()), 0, 0));
		return transform;
	}
	
	public AffineTransform objectToView3D(Instance inst) {
		AffineTransform transform = new AffineTransform();
		// World to View
        transform.concatenate(worldToView());
		// Object to World
		transform.concatenate(objectToWorld3D(inst));
		return transform;
	}
	
	public double[] threeDWorldToClip(Point3D point, Instance inst) {
		//TODO
		float theta = (float) (SceneModel.instance().getYaw());
		double c_x = SceneModel.instance().getCameraPosition().x + inst.getPosition().x;
		double c_y = SceneModel.instance().getCameraPosition().y + inst.getPosition().y;
		double c_z = SceneModel.instance().getCameraPosition().z + inst.getPosition().z;
		double e = (farPlane + nearPlane) / (farPlane - nearPlane);
		double f = (-2 * nearPlane * farPlane) / (farPlane - nearPlane);

		//World to camera translate
		double x1 = point.x - c_x;
		double y1 = point.y - c_y;
		double z1 = point.z - c_z;
		
		//World to camera rotate
		double x2 = x1 * Math.cos(theta) + z1 * Math.sin(theta);
		double z2 = -x1 * Math.sin(theta) + z1 * Math.cos(theta);

		//Camera to Clip
		double x = x2 * Math.sqrt(3) + Math.sqrt(3);
		double y = y1 * Math.sqrt(3);
		double z = f + e * z2;		
		double bigW = (-c_z * Math.cos(theta) + point.z * Math.cos(theta) + c_x * Math.sin(theta) - point.x * Math.sin(theta));
		
		double[] result = {x, y, z, bigW};

		return result;
	}

	public Point3D clipToScreen(Point3D point) {
		double x = (1024 + (1024 * point.x));
		double y = (1024 - (1024 * point.y));
		return new Point3D(x, y, 1);
	}

	public boolean clipTest(double[] start, double[] end) {
		
		double startX = start[0];
		double startY = start[1];
		double startZ = start[2];
		double startW = start[3];

		double endX = end[0];
		double endY = end[1];
		double endZ = end[2];
		double endW = end[3];

		if ((startX > startW && endX > endW) || (startX < -startW && endX < -endW)) return true;

		if ((startY > startW && endY > endW) || (startY < -startW && endY < -endW)) return true;

		if ((startZ > startW && endZ > endW)) return true;

		if (startZ <= -startW || endZ <= -endW) return true;

		return false;
	}
	
	/* Zoom Adjustment */
	public double getZoom() {
		return zoom;
	}
	
	private void setZoom(double value){

		// find screen width before zoom changes
		int prevWidth = (int) (CS355.SCROLLSTART/zoom);
		
		zoom *= value;
		if(zoom < ZOOMMIN) zoom = ZOOMMIN;
		if(zoom > ZOOMMAX) zoom = ZOOMMAX;
		
		scrollerSize = (CS355.SCROLLSTART/zoom);

		//calculate the new top left of the view
		Point2D.Double newTopLeft = new Point2D.Double(viewCenter.x - scrollerSize/2, viewCenter.y - scrollerSize/2);
		if(newTopLeft.x < 0) newTopLeft.x = 0;
		if(newTopLeft.y < 0) newTopLeft.y = 0;
		if(newTopLeft.x + scrollerSize > CS355.SCREENSIZE) newTopLeft.x = CS355.SCREENSIZE - scrollerSize;
		if(newTopLeft.y + scrollerSize > CS355.SCREENSIZE) newTopLeft.y = CS355.SCREENSIZE - scrollerSize;
		
		updating = true;
		
		if(prevWidth == CS355.SCREENSIZE) {
			GUIFunctions.setHScrollBarKnob((int) scrollerSize);
			GUIFunctions.setVScrollBarKnob((int) scrollerSize);
		}
		GUIFunctions.setHScrollBarPosit((int) newTopLeft.x);
		GUIFunctions.setVScrollBarPosit((int) newTopLeft.y);
		GUIFunctions.setHScrollBarKnob((int) scrollerSize);
		GUIFunctions.setVScrollBarKnob((int) scrollerSize);

		GUIFunctions.setZoomText(zoom);
		Model.instance().changeMade();
		
		this.updating = false;
	}
	
	/* Misc Functions */
	
	public double calculateCenterTriangle(double coord1, double coord2, double coord3) {
		return ((coord1 + coord2 + coord3) / 3);
	}
	
	public IControllerState.stateType getState() {
		return this.state.getType();
	}
}
