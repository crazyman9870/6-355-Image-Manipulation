package cs355;

import cs355.model.scene.Point3D;

public class Camera {
	// Variables
	private Point3D location;
	private float yaw, pitch, roll;

	// Constructor(s)
	public Camera()	{
		this.location = new Point3D(0.0d, 0.0d, 0.0d);
		this.yaw = 0.0f;
		this.pitch = 0.0f;
		this.roll = 0.0f;
	}
	
	public Camera(Point3D location)	{
		this.location = location;
		this.yaw = 0.0f;
		this.pitch = 0.0f;
		this.roll = 0.0f;
	}

	// Methods

	public Point3D getLocation() {
		return this.location;
	}

	public void setLocation(Point3D location) {
		this.location = location;
	}

	public float getYaw() {
		return (float) Math.toRadians(this.yaw);
	}

	public void yaw(float yaw) {
		this.yaw += yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return this.pitch;
	}

	public void pitch(float pitch) {
		this.pitch += pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getRoll() {
		return this.roll;
	}

	public void roll(float roll) {
		this.roll += roll;
	}

	public void setRoll(float roll)	{
		this.roll = roll;
	}

	// moves the camera up or down ignoring current rotation
	public void changeAltitude(float distance) {
		this.location.y += distance;
	}

	// moves the camera forward relative to its current rotation (yaw)
	public void moveForward(float distance)	{
		this.location.x -= distance * (float) Math.sin(Math.toRadians(yaw));
		this.location.z += distance * (float) Math.cos(Math.toRadians(yaw));
	}

	// moves the camera backward relative to its current rotation (yaw)
	public void moveBackward(float distance) {
		this.location.x += distance * (float) Math.sin(Math.toRadians(yaw));
		this.location.z -= distance * (float) Math.cos(Math.toRadians(yaw));
	}

	// strafe the camera left relative to its current rotation (yaw)
	public void strafeLeft(float distance) {
		this.location.x -= distance * (float) Math.sin(Math.toRadians(yaw - 90));
		this.location.z += distance * (float) Math.cos(Math.toRadians(yaw - 90));
	}

	// strafe the camera right relative to its current rotation (yaw)
	public void strafeRight(float distance)	{
		this.location.x -= distance * (float) Math.sin(Math.toRadians(yaw + 90));
		this.location.z += distance * (float) Math.cos(Math.toRadians(yaw + 90));
	}
	
	// combined left/right strafe method. strafe's left/right relative to camera's current rotation (yaw)
	public void strafe(float distance) {
		int negate = distance >= 0 ? -1 : 1;
		float offset = 90 * negate;

		this.location.x += negate * distance * (float) Math.sin(Math.toRadians(yaw + offset));
		this.location.z += -negate * distance * (float) Math.cos(Math.toRadians(yaw + offset));
	}
}
