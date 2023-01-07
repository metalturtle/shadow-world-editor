

public class GameCamera extends Rectangle
{
//	GameActorHandler actorhandler;

	byte type;
	float speed, acceleration, lastDistance,maxSpeed;
	Vector jerkVector;
	boolean jerk;
	float ZOOM;
	float rw,rh;
	
	GameCamera(float x,float y, float w,float h)
	{
		super(x,y, w, h);
		rw=w;
		rh=h;
		this.maxSpeed = 5;
		this.acceleration = 0.5f;
		jerkVector = new Vector(0,0);
		this.ZOOM = 1;
	}
	
	public GameCamera(GameCamera cam) {
		super(cam);
//		this.set(cam);
		this.maxSpeed = 5;
		this.acceleration = 0.5f;
		jerkVector = new Vector(0,0);
		this.ZOOM = 1;
	}
	
	public float get_screen_val(float world_val) {
		return (world_val/ZOOM);
	}
	
	public void set_zoom (float zoom) {
		this.ZOOM = zoom;
		this.w = ZOOM*rw;
		this.h = ZOOM*rh;
	}
	
	public float get_zoom() {
		return this.ZOOM;
	}
	
	public void set_type (byte type) {
		this.type = type;
	}
}
