package cs355.model.drawing;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cs355.GUIFunctions;
import cs355.controller.Controller;
import cs355.model.scene.HouseModel;

public class Model extends CS355Drawing {

	//Use a singleton so that the model can be accessed by the view when repainting
	private static Model _instance;
	
	private Shape.type currentShape;
	private Color selectedColor;
	private int selectedShapeIndex;
	private ArrayList<Shape> shapes;
	private ArrayList<HouseModel> hood;


	//If the model had not been initialized, it will be.
	public static Model instance() {
		if (_instance == null) 
			_instance = new Model();
		return _instance;
	}
	
	private Model() {
		currentShape = Shape.type.NONE;
		selectedColor = Color.WHITE;
		selectedShapeIndex = -1;
		shapes = new ArrayList<Shape>();
		hood = new ArrayList<>();
		inTheHood();
	}
	
	//Notifies the observers
	public void notifyObservers() {
		super.notifyObservers();
	}
	
	public void setColor(Color c) {	
		selectedColor = c;
		if(selectedShapeIndex != -1)
			shapes.get(selectedShapeIndex).setColor(c);
		changeMade();
	}
	
	public Color getColor()	{
		return selectedColor;
	}
		
	public int getSelectedShapeIndex() {
		return selectedShapeIndex;
	}

	public void setSelectedShapeIndex(int selectedShapeIndex) {
		this.selectedShapeIndex = selectedShapeIndex;
		changeMade();
	}
	
	public int selectShape(Point2D.Double pt, double tolerance) {
		for(int i = shapes.size() - 1; i >= 0; i--) {
			Shape s = shapes.get(i);
			Point2D.Double ptCopy = new Point2D.Double(pt.getX(), pt.getY());

			if(s.getShapeType() != Shape.type.LINE) {
				// changes the coordinates from view->world->object
				AffineTransform viewToObject = Controller.instance().viewToObject(s);
				viewToObject.transform(ptCopy, ptCopy);
			}
//			else {
				//changes the coordinates from view->world
//				AffineTransform viewToWorld = Controller.instance().viewToWorld();
//				viewToWorld.transform(ptCopy, ptCopy);
//			}
			if(s.pointInShape(ptCopy, tolerance)) {
				selectedShapeIndex = i;
				selectedColor = s.getColor();
				GUIFunctions.changeSelectedColor(selectedColor);
				changeMade();
				return selectedShapeIndex;
			}
		}
		selectedShapeIndex = -1;
		changeMade();
		return selectedShapeIndex;
	}
	
	@Override
	public Shape getShape(int index) {
		return shapes.get(index);
	}
	
	public void setShapeByIndex(int index, Shape newShape) {
		shapes.remove(index);
		shapes.add(index, newShape);
	}

	@Override
	public int addShape(Shape s) {
		shapes.add(s);
//		System.out.println(shapes.size());
		return shapes.size();
	}

	@Override
	public void deleteShape(int index) {
		if(shapes.size() <= index || index < 0)
			return;
		
		shapes.remove(index);
		selectedShapeIndex = -1;
	}

	@Override
	public void moveToFront(int index) {
		if(shapes.size() <= index || index < 0)
			return;
		
		Shape s = shapes.get(index);
		shapes.remove(index);
		shapes.add(s);
		selectedShapeIndex = shapes.size() - 1;
	}

	@Override
	public void moveToBack(int index) {
		if(shapes.size() <= index || index < 0)
			return;
		
		Shape s = shapes.get(index);
		shapes.remove(index);
		shapes.add(0, s);
		selectedShapeIndex = 0;
	}

	@Override
	public void moveForward(int index) {
		if(shapes.size() - 1 <= index || index < 0)
			return;
		
		Shape s = shapes.get(index);
		shapes.remove(index);
		shapes.add(index + 1, s);
		selectedShapeIndex = index + 1;
	}

	@Override
	public void moveBackward(int index) {
		if(shapes.size() <= index || index <= 0)
			return;
		
		Shape s = shapes.get(index);
		shapes.remove(index);
		shapes.add(index - 1, s);
		selectedShapeIndex = index - 1; 
	}

	@Override
	public List<Shape> getShapes() {
		return shapes;
	}

	@Override
	public List<Shape> getShapesReversed() {
		ArrayList<Shape> copy = new ArrayList<>();
		Collections.reverse(copy);
		return null;
	}

	@Override
	public void setShapes(List<Shape> shapes) {
		this.shapes = (ArrayList<Shape>) shapes;
	}

	public Shape.type getCurrentShape() {
		return currentShape;
	}

	public void setCurrentShape(Shape.type currentMode) {
		this.currentShape = currentMode;
	}

	public Color getSelectedColor() {
		return selectedColor;
	}

	public void setSelectedColor(Color selectedColor) {
		this.selectedColor = selectedColor;
	}

	public void setShapes(ArrayList<Shape> shapes) {
		this.shapes = shapes;
	}
	
	public Shape getLastShape()
	{	return shapes.get(shapes.size() - 1);	}
	
	public void setLastShape(Shape newShape) {	
		shapes.remove(shapes.size() - 1);
		shapes.add(newShape);
	}
	
	public void deleteLastShape() {
		shapes.remove(shapes.size()-1);
	}
	
	public void changeMade() {
		setChanged();
		notifyObservers();
	}
	
	private void inTheHood() {
		hood.add(new HouseModel());
	}

	public ArrayList<HouseModel> getHood() {
		return hood;
	}

	public void setHood(ArrayList<HouseModel> hood) {
		this.hood = hood;
	}
	
	public HouseModel getCribByIndex(int i) {
		return hood.get(i);
	}
}
