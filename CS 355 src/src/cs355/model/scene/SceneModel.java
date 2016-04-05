package cs355.model.scene;

public class SceneModel extends CS355Scene {

	//Use a singleton so that the model can be accessed by the view when repainting
	private static SceneModel _instance;
	
	private float pitch, roll;

	//If the model had not been initialized, it will be.
	public static SceneModel instance() {
		if (_instance == null) 
			_instance = new SceneModel();
		return _instance;
	}
	
	// Constructor(s)
	public SceneModel()	{
		this.setCameraPosition(new Point3D(0.0d, 0.0d, 0.0d));
		this.setCameraRotation(180.0);
		this.pitch = 0.0f;
		this.roll = 0.0f;
	}
	
	public SceneModel(Point3D location)	{
		this.setCameraPosition(location);
		this.setCameraRotation(180.0);
		this.pitch = 0.0f;
		this.roll = 0.0f;
	}
	
	// Methods

	public float getYaw() {
		return (float) Math.toRadians(this.getCameraRotation());
	}

	public void yaw(float yaw) {
		this.setCameraRotation((float)this.getCameraRotation() + yaw);
	}

	public void setYaw(float yaw) {
		this.setCameraRotation(yaw);
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
		Point3D cam = this.getCameraPosition();
		cam.y += distance;
		this.setCameraPosition(cam);
	}

	// moves the camera forward relative to its current rotation
	public void moveForward(float distance)	{
		Point3D cam = this.getCameraPosition();
		cam.x -= distance * (float) Math.sin(Math.toRadians(this.getCameraRotation()));
		cam.z += distance * (float) Math.cos(Math.toRadians(this.getCameraRotation()));
		this.setCameraPosition(cam);
	}

	// moves the camera backward relative to its current rotation
	public void moveBackward(float distance) {
		Point3D cam = this.getCameraPosition();
		cam.x += distance * (float) Math.sin(Math.toRadians(this.getCameraRotation()));
		cam.z -= distance * (float) Math.cos(Math.toRadians(this.getCameraRotation()));
		this.setCameraPosition(cam);
	}

	// strafe the camera left relative to its current rotation
	public void strafeLeft(float distance) {
		Point3D cam = this.getCameraPosition();
		cam.x -= distance * (float) Math.sin(Math.toRadians(this.getCameraRotation() - 90));
		cam.z += distance * (float) Math.cos(Math.toRadians(this.getCameraRotation() - 90));
		this.setCameraPosition(cam);
	}

	// strafe the camera right relative to its current rotation
	public void strafeRight(float distance)	{
		Point3D cam = this.getCameraPosition();
		cam.x -= distance * (float) Math.sin(Math.toRadians(this.getCameraRotation() + 90));
		cam.z += distance * (float) Math.cos(Math.toRadians(this.getCameraRotation() + 90));
		this.setCameraPosition(cam);
	}
	
	// combined left/right strafe method. strafe's left/right relative to camera's current rotation
	public void strafe(float distance) {
		int negate = distance >= 0 ? -1 : 1;
		float offset = 90 * negate;

		Point3D cam = this.getCameraPosition();
		cam.x += negate * distance * (float) Math.sin(Math.toRadians(this.getCameraRotation() + offset));
		cam.z += -negate * distance * (float) Math.cos(Math.toRadians(this.getCameraRotation() + offset));
		this.setCameraPosition(cam);
	}
}
