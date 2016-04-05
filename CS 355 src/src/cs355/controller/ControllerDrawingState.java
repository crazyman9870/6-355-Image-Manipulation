package cs355.controller;

import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import cs355.GUIFunctions;
import cs355.model.drawing.Circle;
import cs355.model.drawing.Ellipse;
import cs355.model.drawing.Line;
import cs355.model.drawing.Model;
import cs355.model.drawing.Rectangle;
import cs355.model.drawing.Shape;
import cs355.model.drawing.Square;
import cs355.model.drawing.Triangle;

public class ControllerDrawingState implements IControllerState {

	private Point2D.Double mouseDragStart;
	private ArrayList<Point2D> triangleCoordinates;
	
	public ControllerDrawingState() {
		this.mouseDragStart = null;
		this.triangleCoordinates = new ArrayList<>();
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		if(Model.instance().getCurrentShape() == Shape.type.TRIANGLE) {
			Point2D.Double point = new Point2D.Double(arg0.getX(), arg0.getY());
			AffineTransform viewToWorld = Controller.instance().viewToWorld();
			viewToWorld.transform(point, point);
			this.triangleCoordinates.add(point);
			
			if (this.triangleCoordinates.size() == 3) 
			{
				Point2D.Double point1 = new Point2D.Double(this.triangleCoordinates.get(0).getX(), this.triangleCoordinates.get(0).getY());
				Point2D.Double point2 = new Point2D.Double(this.triangleCoordinates.get(1).getX(), this.triangleCoordinates.get(1).getY());
				Point2D.Double point3 = new Point2D.Double(this.triangleCoordinates.get(2).getX(), this.triangleCoordinates.get(2).getY());
								
				Point2D.Double center = new Point2D.Double(Controller.instance().calculateCenterTriangle(point1.getX(), point2.getX(), point3.getX()),
						Controller.instance().calculateCenterTriangle(point1.getY(), point2.getY(), point3.getY()));
				
				Triangle triangle = new Triangle(Model.instance().getColor(), center, point1, point2, point3);
				Model.instance().addShape(triangle);
				this.triangleCoordinates.clear();
				Model.instance().changeMade();
			}
		}
		else {
			this.mouseDragStart = new Point2D.Double(arg0.getX(), arg0.getY());
			AffineTransform viewToWorld = Controller.instance().viewToWorld();
			viewToWorld.transform(this.mouseDragStart, this.mouseDragStart);
			switch(Model.instance().getCurrentShape()) {
			case LINE:
				Model.instance().addShape(new Line(Model.instance().getColor(),
						new Point2D.Double(this.mouseDragStart.getX(), this.mouseDragStart.getY()),
						new Point2D.Double(this.mouseDragStart.getX(), this.mouseDragStart.getY())));
				break;
			case SQUARE: 
				Model.instance().addShape(new Square(Model.instance().getColor(),
						new Point2D.Double(this.mouseDragStart.getX(), this.mouseDragStart.getY()), 0));
				break;
			case RECTANGLE: 
				Model.instance().addShape(new Rectangle(Model.instance().getColor(), 
						new Point2D.Double(this.mouseDragStart.getX(), this.mouseDragStart.getY()),0, 0));
				break;
			case CIRCLE: 
				Model.instance().addShape(new Circle(Model.instance().getColor(),
						new Point2D.Double(this.mouseDragStart.getX(), this.mouseDragStart.getY()),0));
				break;
			case ELLIPSE: 
				Model.instance().addShape(new Ellipse(Model.instance().getColor(),
						new Point2D.Double(this.mouseDragStart.getX(), this.mouseDragStart.getY()),0, 0));
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		if(Model.instance().getCurrentShape() != Shape.type.TRIANGLE) {
			Shape currentShape = Model.instance().getLastShape();
			Point2D.Double movingPoint = new Point2D.Double(arg0.getX(), arg0.getY());
			AffineTransform viewToWorld = Controller.instance().viewToWorld();
			viewToWorld.transform(movingPoint, movingPoint);
			switch(currentShape.getShapeType()) {
			case LINE:
				handleActiveLine(movingPoint);
				break;
			case SQUARE:
				handleActiveSquare(movingPoint);
				break;
			case RECTANGLE:
				handleActiveRectangle(movingPoint);
				break;
			case CIRCLE:
				handleActiveCircle(movingPoint);
				break;
			case ELLIPSE:
				handleActiveEllipse(movingPoint);
				break;
			case TRIANGLE:
				break;
			case NONE:
				break;
			default:
				break;
			}
			GUIFunctions.refresh();
		}
	}

	@Override
	public stateType getType() {
		return stateType.DRAWING;
	}
	
	/* Shape Handlers */
	
	private void handleActiveLine(Point2D.Double pt) {
		
		Line line = (Line) Model.instance().getLastShape();
		line.setEnd(new Point2D.Double(pt.getX(), pt.getY()));
		
		Model.instance().setLastShape(line);
	}
	
	private void handleActiveSquare(Point2D.Double pt)	{
		
		Square square = (Square) Model.instance().getLastShape();
		//if the cursor is moving below the upper left corner
		if(pt.getY() > mouseDragStart.y)
		{
			//if the cursor is moving to the bottom right quad
			if(pt.getX() > mouseDragStart.x)
			{
				double lengthX = pt.getX() - mouseDragStart.x;
				double lengthY = pt.getY() - mouseDragStart.y;
				double newcorner = Math.min(lengthX, lengthY);
				
				square.setCenter(new Point2D.Double(mouseDragStart.x + newcorner/2, mouseDragStart.y + newcorner/2));
				square.setSize(newcorner);
			}

			//if the cursor is moving to the bottom left quad
			if(pt.getX() < mouseDragStart.x)
			{
				double lengthX = mouseDragStart.x - pt.getX();
				double lengthY = pt.getY() - mouseDragStart.y;
				double newcorner = Math.min(lengthX, lengthY);
				
				square.setCenter(new Point2D.Double(mouseDragStart.x - newcorner/2, mouseDragStart.y + newcorner/2));
				square.setSize(newcorner);
			}
		}

		//if the cursor is moving above the upper left corner
		if(pt.getY() < mouseDragStart.y)
		{
			//if the cursor is moving to the upper right quad
			if(pt.getX() > mouseDragStart.x)
			{
				double lengthX = pt.getX() - mouseDragStart.x;
				double lengthY = mouseDragStart.y - pt.getY();
				double newcorner = Math.min(lengthX, lengthY);
				
				//change to set center of some sort 
				square.setCenter(new Point2D.Double(mouseDragStart.x + newcorner/2, mouseDragStart.y  - newcorner/2));
				square.setSize(newcorner);
			}

			//if the cursor is moving to the upper left quad
			if(pt.getX() < mouseDragStart.x)
			{
				double lengthX = mouseDragStart.x - pt.getX();
				double lengthY = mouseDragStart.y - pt.getY();
				double newcorner = Math.min(lengthX, lengthY);
				
				square.setCenter(new Point2D.Double(mouseDragStart.x - newcorner/2, mouseDragStart.y - newcorner/2));
				square.setSize(newcorner);
			}
		}
		Model.instance().setLastShape(square);
	}
	
	private void handleActiveRectangle(Point2D.Double pt) {
		
		Rectangle rectangle = (Rectangle) Model.instance().getLastShape();
		//if the cursor is moving below the upper left corner
		if(pt.getY() > mouseDragStart.y)
		{
			//if the cursor is moving to the bottom right quad
			if(pt.getX() > mouseDragStart.x)
			{
				double lengthX = pt.getX() - mouseDragStart.x;
				double lengthY = pt.getY() - mouseDragStart.y;
				
				rectangle.setCenter(new Point2D.Double(mouseDragStart.x + lengthX/2, mouseDragStart.y + lengthY/2));
				rectangle.setHeight(lengthY);
				rectangle.setWidth(lengthX);
			}

			//if the cursor is moving to the bottom left quad
			if(pt.getX() < mouseDragStart.x)
			{
				double lengthX = mouseDragStart.x - pt.getX();
				double lengthY = pt.getY() - mouseDragStart.y;
				
				rectangle.setCenter(new Point2D.Double(mouseDragStart.x - lengthX/2, mouseDragStart.y + lengthY/2));
				rectangle.setHeight(lengthY);
				rectangle.setWidth(lengthX);
			}
		}

		//if the cursor is moving above the upper left corner
		if(pt.getY() < mouseDragStart.y)
		{
			//if the cursor is moving to the upper right quad
			if(pt.getX() > mouseDragStart.x)
			{
				double lengthX = pt.getX() - mouseDragStart.x;
				double lengthY = mouseDragStart.y - pt.getY();
				
				rectangle.setCenter(new Point2D.Double(mouseDragStart.x + lengthX/2, mouseDragStart.y  - lengthY/2));
				rectangle.setHeight(lengthY);
				rectangle.setWidth(lengthX);
			}

			//if the cursor is moving to the upper left quad
			if(pt.getX() < mouseDragStart.x)
			{
				double lengthX = mouseDragStart.x - pt.getX();
				double lengthY = mouseDragStart.y - pt.getY();
				
				rectangle.setCenter(new Point2D.Double(mouseDragStart.x - lengthX/2, mouseDragStart.y - lengthY/2));
				rectangle.setHeight(lengthY);
				rectangle.setWidth(lengthX);
			}
		}
		Model.instance().setLastShape(rectangle);
	}
	
	private void handleActiveCircle(Point2D.Double pt) {
		
		Circle circle = (Circle) Model.instance().getLastShape();
		//if the cursor is moving below the upper left corner
		if(pt.getY() > mouseDragStart.y)
		{
			//if the cursor is moving to the bottom right quad
			if(pt.getX() > mouseDragStart.x)
			{
				double lengthX = pt.getX() - mouseDragStart.x;
				double lengthY = pt.getY() - mouseDragStart.y;
				double newcorner = Math.min(lengthX, lengthY);
				
				circle.setCenter(new Point2D.Double(mouseDragStart.x + newcorner/2, mouseDragStart.y + newcorner/2));
				circle.setRadius(newcorner / 2);
			}

			//if the cursor is moving to the bottom left quad
			if(pt.getX() < mouseDragStart.x)
			{
				double lengthX = mouseDragStart.x - pt.getX();
				double lengthY = pt.getY() - mouseDragStart.y;
				double newcorner = Math.min(lengthX, lengthY);
				
				circle.setCenter(new Point2D.Double(mouseDragStart.x - newcorner/2, mouseDragStart.y + newcorner/2));
				circle.setRadius(newcorner / 2);
			}
		}

		//if the cursor is moving above the upper left corner
		if(pt.getY() < mouseDragStart.y)
		{
			//if the cursor is moving to the upper right quad
			if(pt.getX() > mouseDragStart.x)
			{
				double lengthX = pt.getX() - mouseDragStart.x;
				double lengthY = mouseDragStart.y - pt.getY();
				double newcorner = Math.min(lengthX, lengthY);
				
				circle.setCenter(new Point2D.Double(mouseDragStart.x + newcorner/2, mouseDragStart.y  - newcorner/2));
				circle.setRadius(newcorner / 2);
			}

			//if the cursor is moving to the upper left quad
			if(pt.getX() < mouseDragStart.x)
			{
				double lengthX = mouseDragStart.x - pt.getX();
				double lengthY = mouseDragStart.y - pt.getY();
				double newcorner = Math.min(lengthX, lengthY);
				
				circle.setCenter(new Point2D.Double(mouseDragStart.x - newcorner/2, mouseDragStart.y - newcorner/2));
				circle.setRadius(newcorner / 2);
			}
		}
		Model.instance().setLastShape(circle);
	}
	
	private void handleActiveEllipse(Point2D.Double pt) {
		
		Ellipse ellipse = (Ellipse) Model.instance().getLastShape();
		//if the cursor is moving below the upper left corner
		if(pt.getY() > mouseDragStart.y)
		{
			//if the cursor is moving to the bottom right quad
			if(pt.getX() > mouseDragStart.x)
			{
				double lengthX = pt.getX() - mouseDragStart.x;
				double lengthY = pt.getY() - mouseDragStart.y;
				
				ellipse.setCenter(new Point2D.Double(mouseDragStart.x + lengthX/2, mouseDragStart.y + lengthY/2));
				ellipse.setWidth(lengthX);
				ellipse.setHeight(lengthY);
			}

			//if the cursor is moving to the bottom left quad
			if(pt.getX() < mouseDragStart.x)
			{
				double lengthX = mouseDragStart.x - pt.getX();
				double lengthY = pt.getY() - mouseDragStart.y;
				
				ellipse.setCenter(new Point2D.Double(mouseDragStart.x - lengthX/2, mouseDragStart.y + lengthY/2));
				ellipse.setWidth(lengthX);
				ellipse.setHeight(lengthY);
			}
		}

		//if the cursor is moving above the upper left corner
		if(pt.getY() < mouseDragStart.y)
		{
			//if the cursor is moving to the upper right quad
			if(pt.getX() > mouseDragStart.x)
			{
				double lengthX = pt.getX() - mouseDragStart.x;
				double lengthY = mouseDragStart.y - pt.getY();
				
				ellipse.setCenter(new Point2D.Double(mouseDragStart.x + lengthX/2, mouseDragStart.y  - lengthY/2));
				ellipse.setWidth(lengthX);
				ellipse.setHeight(lengthY);
			}

			//if the cursor is moving to the upper left quad
			if(pt.getX() < mouseDragStart.x)
			{
				double lengthX = mouseDragStart.x - pt.getX();
				double lengthY = mouseDragStart.y - pt.getY();
				
				ellipse.setCenter(new Point2D.Double(mouseDragStart.x - lengthX/2, mouseDragStart.y - lengthY/2));
				ellipse.setWidth(lengthX);
				ellipse.setHeight(lengthY);
			}
		}
		Model.instance().setLastShape(ellipse);
	}

}