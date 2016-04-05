package cs355.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Random;

import cs355.GUIFunctions;
import cs355.controller.Controller;
import cs355.controller.IControllerState;
import cs355.model.drawing.*;
import cs355.model.scene.HouseModel;
import cs355.model.scene.Instance;
import cs355.model.scene.Line3D;
import cs355.model.scene.Point3D;
import cs355.model.scene.SceneModel;

public class View implements ViewRefresher {
	
	Random rand = new Random();

	@Override
	public void update(Observable arg0, Object arg1) {
		GUIFunctions.refresh();
	}

	@Override
	public void refreshView(Graphics2D g2d) {
		ArrayList<Shape> shapes = (ArrayList<Shape>) Model.instance().getShapes();
		int selectedShapeIndex = Model.instance().getSelectedShapeIndex();
		for(int i = 0; i < shapes.size(); i++) {
			Shape currentShape = shapes.get(i);
			
			//Sets the color for the graphics object
			g2d.setColor(currentShape.getColor());

			// changes the coordinates from object->world->view
			g2d.setTransform(Controller.instance().objectToView(currentShape));
			//Draw the object
			g2d.fill(shapeFactory(currentShape, g2d, false)); //Uses the factory to determine the current shape to set the fill.
			g2d.draw(shapeFactory(currentShape, g2d, selectedShapeIndex == i)); //Uses the factory to determine the current shape to draw the image
			g2d.setColor(currentShape.getColor());
		}
		
		if(Controller.instance().getState() == IControllerState.stateType.THREED) {
			render3D(g2d);
		}
	}
	
	//Use a factory to determine what type is being dealt with
	public java.awt.Shape shapeFactory(Shape currentShape, Graphics2D g2d, boolean selected) {
		
		if(currentShape.getShapeType() == Shape.type.LINE) {
			g2d.setTransform(new AffineTransform());
			Line line = (Line)currentShape;
			Point2D.Double start = Controller.instance().worldPointToViewPoint(line.getCenter());
			Point2D.Double end = Controller.instance().worldPointToViewPoint(line.getEnd());
			if(selected) {
				g2d.setColor(new Color(255, 131, 0));
				g2d.drawOval((int)(start.getX() - 6),(int)(start.getY() - 6), 11, 11); //center
				g2d.drawOval((int)(end.getX() - 6), (int)(end.getY() - 6), 11, 11); //end
				g2d.setColor(currentShape.getColor());
			}
			return new Line2D.Double(start.x, start.y, end.x, end.y);
		}

		if(currentShape.getShapeType() == Shape.type.SQUARE) {
			//create a Square from Rectangle2D object and return it
			double sideLength = ((Square) currentShape).getSize();
			if(selected) {
				g2d.setColor(new Color(255, 131, 0));
				double zoom = Controller.instance().getZoom();
				
				//draw circle handle	
				g2d.setTransform(new AffineTransform()); //make sure sure no transforms are goin on
				Point2D.Double point = new Point2D.Double(0, -(sideLength/2) - (12/zoom));
				AffineTransform objToView = Controller.instance().objectToView(currentShape);
				objToView.transform(point, point);
				g2d.drawOval((int)(point.getX()-6), (int)(point.getY()-6), 11, 11); //center
				
				//draw bounding box
				g2d.setTransform(Controller.instance().objectToView(currentShape)); //object->world->view
				g2d.setStroke(new BasicStroke((float) (1/zoom)));
				g2d.drawRect((int)-sideLength/2,(int)-sideLength/2,(int)sideLength,(int)sideLength);
			}
			return new Rectangle2D.Double(-sideLength/2, -sideLength/2, sideLength, sideLength);
		}
		
		if(currentShape.getShapeType() == Shape.type.RECTANGLE)	{
			//create a Rectangle2D object and return it
			double width = ((Rectangle) currentShape).getWidth();
			double height = ((Rectangle) currentShape).getHeight();
			if(selected) {
				g2d.setColor(new Color(255, 131, 0));
				
				double zoom = Controller.instance().getZoom();

				g2d.setTransform(new AffineTransform()); //make sure sure no transforms are goin on
				Point2D.Double point = new Point2D.Double(0, -(height/2) - (12/zoom));
				AffineTransform objToView = Controller.instance().objectToView(currentShape);
				objToView.transform(point, point);
				g2d.drawOval((int)(point.getX()-6), (int)(point.getY()-6), 11, 11); //center

				g2d.setTransform(Controller.instance().objectToView(currentShape)); //object->world->view
				g2d.setStroke(new BasicStroke((float) (1/zoom)));
				g2d.drawRect((int)-width/2,(int)-height/2,(int)width,(int)height);
			}
			return new Rectangle2D.Double(-width/2, -height/2, width, height);
		}
		
		if(currentShape.getShapeType() == Shape.type.CIRCLE) {
			//create a Circle2D object and return it
			double diameter = ((Circle) currentShape).getRadius() * 2;
			if(selected) {
				g2d.setColor(new Color(255, 131, 0));

				double zoom = Controller.instance().getZoom();
				
				//draw circle handle
				g2d.setTransform(new AffineTransform()); //make sure sure no transforms are goin on
				Point2D.Double point = new Point2D.Double(0, -(diameter/2) - (12/zoom));
				AffineTransform objToView = Controller.instance().objectToView(currentShape);
				objToView.transform(point, point);
				g2d.drawOval((int)(point.getX()-6), (int)(point.getY()-6), 11, 11); //center
				
				//draw bounding box
				g2d.setTransform(Controller.instance().objectToView(currentShape)); //object->world->view
				g2d.setStroke(new BasicStroke((float) (1/zoom)));
				g2d.drawRect((int)-diameter/2,(int)-diameter/2,(int)diameter,(int)diameter);
				
				g2d.setColor(currentShape.getColor());
			}
			return new Ellipse2D.Double(-diameter/2, -diameter/2, diameter, diameter);
		}
		
		if(currentShape.getShapeType() == Shape.type.ELLIPSE) {
			//create a Ellipse2D object and return it
			double width = ((Ellipse) currentShape).getWidth();
			double height = ((Ellipse) currentShape).getHeight();
			if(selected) {
				g2d.setColor(new Color(255, 131, 0));

				double zoom = Controller.instance().getZoom();
				
				//draw circle handle
				g2d.setTransform(new AffineTransform()); //make sure sure no transforms are goin on
				Point2D.Double point = new Point2D.Double(0, -(height/2) - (12/zoom));
				AffineTransform objToWorld = Controller.instance().objectToView(currentShape);
				objToWorld.transform(point, point);
				g2d.drawOval((int)(point.getX()-6), (int)(point.getY()-6), 11, 11); //center
				
				//draw bounding box
				g2d.setTransform(Controller.instance().objectToView(currentShape)); //object->world->view
				g2d.setStroke(new BasicStroke((float) (1/zoom)));
				g2d.drawRect((int)-width/2,(int)-height/2,(int)width,(int)height);
				
				g2d.setColor(currentShape.getColor());
			}
			return new Ellipse2D.Double(-width/2, -height/2, width, height);
		}
		
		if(currentShape.getShapeType() == Shape.type.TRIANGLE) {
			//create a triangle from a Polygon and return it
			int[] x = new int[3];
			int[] y = new int[3];
			
			x[0] = (int) (((Triangle) currentShape).getA().x - ((Triangle) currentShape).getCenter().x);
			x[1] = (int) (((Triangle) currentShape).getB().x - ((Triangle) currentShape).getCenter().x);
			x[2] = (int) (((Triangle) currentShape).getC().x - ((Triangle) currentShape).getCenter().x);
			
			y[0] = (int) (((Triangle) currentShape).getA().y - ((Triangle) currentShape).getCenter().y);
			y[1] = (int) (((Triangle) currentShape).getB().y - ((Triangle) currentShape).getCenter().y);
			y[2] = (int) (((Triangle) currentShape).getC().y - ((Triangle) currentShape).getCenter().y);
			
			Polygon tri = new Polygon();
			tri.addPoint(x[0], y[0]);
			tri.addPoint(x[1], y[1]);
			tri.addPoint(x[2], y[2]);
			
			if(selected) {
				g2d.setColor(new Color(255, 131, 0));

				double zoom = Controller.instance().getZoom();
				
				//draw circle handle
				g2d.setTransform(new AffineTransform()); //make sure sure no transforms are goin on
				Point2D.Double point = new Point2D.Double();
				
				if(y[0] <= y[1] && y[0] <= y[2]) {
					point.x = x[0];
					point.y = y[0] - (12/zoom);
				}
				else if(y[1] <= y[0] && y[1] <= y[2]) {
					point.x = x[1];
					point.y = y[1] - (12/zoom);
				}
				else if(y[2] <= y[1] && y[2] <= y[0]) {
					point.x = x[2];
					point.y = y[2] - (12/zoom);
				}
				
				AffineTransform objToView = Controller.instance().objectToView(currentShape);
				objToView.transform(point, point);
				g2d.drawOval((int)(point.getX()-6), (int)(point.getY()-6), 11, 11); //center
				
				//draw bounding box
				g2d.setTransform(Controller.instance().objectToView(currentShape)); //object->world->view
				g2d.setStroke(new BasicStroke((float) (1/zoom)));
				g2d.drawPolygon(x, y, 3);
			}
			
			return tri;
		}
		
		return null;
	}
	
	public void render3D(Graphics2D g2d) {

		ArrayList<Instance> theHood = SceneModel.instance().instances();
//		System.out.println("OBJECTS LIST SIZE = " + theHood.size());
		g2d.setTransform(Controller.instance().worldToView());
		for(Instance inst : theHood) {
//			g2d.setTransform(Controller.instance().objectToView3D(inst));
			g2d.setColor(inst.getColor());
			List<Line3D> list = inst.getModel().getLines();
//			System.out.println("LINE LIST SIZE = " + list.size());
			for(Line3D l : list) {

				double[] startCoord = Controller.instance().threeDWorldToClip(l.start, inst);
				double[] endCoord = Controller.instance().threeDWorldToClip(l.end, inst);

				if (!Controller.instance().clipTest(startCoord, endCoord)) {
//					System.out.println("CLIP PASSED");
					Point3D start = Controller.instance().clipToScreen(new Point3D(startCoord[0] / startCoord[3], startCoord[1] / startCoord[3], startCoord[2] / startCoord[3]));
					Point3D end = Controller.instance().clipToScreen(new Point3D(endCoord[0] / endCoord[3], endCoord[1] / endCoord[3], endCoord[2] / endCoord[3]));

					g2d.drawLine((int) Math.round(start.x), (int) Math.round(start.y), (int) Math.round(end.x), (int) Math.round(end.y));
				}
			}
		}
	}
}