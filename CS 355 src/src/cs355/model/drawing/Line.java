package cs355.model.drawing;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import cs355.controller.Controller;

/**
 * Add your line code here. You can add fields, but you cannot
 * change the ones that already exist. This includes the names!
 */
public class Line extends Shape {

	// The ending point of the line.
	private Point2D.Double end;

	/**
	 * Basic constructor that sets all fields.
	 * @param color the color for the new shape.
	 * @param start the starting point.
	 * @param end the ending point.
	 */
	public Line(Color color, Point2D.Double start, Point2D.Double end) {

		// Initialize the superclass.
		super(color, start);
		super.setShapeType(Shape.type.LINE);

		// Set the field.
		this.end = end;
	}
	
	/**
	 * Getter for this Line's ending point.
	 * @return the ending point as a Java point.
	 */
	public Point2D.Double getEnd() {
		return end;
	}

	/**
	 * Setter for this Line's ending point.
	 * @param end the new ending point for the Line.
	 */
	public void setEnd(Point2D.Double end) {
		this.end = end;
	}

	/**
	 * Add your code to do an intersection test
	 * here. You <i>will</i> need the tolerance.
	 * @param pt = the point to test against.
	 * @param tolerance = the allowable tolerance.
	 * @return true if pt is in the shape,
	 *		   false otherwise.
	 */
	@Override
	public boolean pointInShape(Point2D.Double pt, double tolerance) {
		AffineTransform worldToView = Controller.instance().worldToView();
		Point2D.Double centerView = new Point2D.Double(center.getX(), center.getY());
		Point2D.Double endView = new Point2D.Double(end.getX(), end.getY());
		worldToView.transform(centerView, centerView);
		worldToView.transform(endView, endView);
		
		double x0 = pt.getX();
		double y0 = pt.getY();
		double x1 = centerView.getX();
		double y1 = centerView.getY();
		double x2 = endView.getX();
		double y2 = endView.getY();
		
		double slope = (y1-y2)/(x2-x1);
		double tangentSlope = -1/slope;
		double degree = Math.atan(tangentSlope);
		
		y0 += Math.sin(degree);
		x0 += Math.cos(degree);
		
//		double zoom = Controller.instance().getZoom();
		if(x0 <= (Math.max(x1, x2)+tolerance) && x0 >= (Math.min(x1, x2)-tolerance)
				&& y0 <= (Math.max(y1, y2)+tolerance) && y0 >= (Math.min(y1, y2)-tolerance)) {
//			System.out.println("Line Selected");
			return true;
		}
		return false;
	}
	
	public boolean pointNearCenter(Point2D.Double pt, double tolerance) {
		if(Math.abs(center.getX() - pt.getX()) <= tolerance && Math.abs(center.getY() - pt.getY()) <= tolerance)
			return true;
		return false;
	}
	
	public boolean pointNearEnd(Point2D.Double pt, double tolerance) {
		if(Math.abs(end.getX() - pt.getX()) <= tolerance && Math.abs(end.getY() - pt.getY()) <= tolerance)
			return true;
		return false;
	}
}
