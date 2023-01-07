import java.io.Serializable;

public class Vector implements Serializable{
  
  private float x;
  private float y;
  
  public Vector(float x, float y) {
    this.x = x;
    this.y = y;
  }
  
  public float getX() {
    return x;
  }
  
  public float getY() {
    return y;
  }
  
  public void setXY(float x, float y) {
    this.x = x;
    this.y = y;
  }
  
  public void set(Vector vec)
  {
	  this.x = vec.x;
	  this.y = vec.y;
  }
  
  public float length() {
    return (float)Math.sqrt(lengthSquared()); 
  }
  
  public float lengthSquared() {
    return this.dot(this);
  }
  
  public float dot(Vector v2) {
    return x * v2.x + y * v2.y;
  }
  
  public Vector add(Vector v2) {
    setXY(x + v2.x, y + v2.y);
    return this;
  }
  
  public Vector substract(Vector v2) {
    setXY(x - v2.x, y - v2.y);
    return this;
  }
  
  public Vector multiply(float constant) {
    setXY(x * constant, y * constant);
    return this;
  }
  
  public Vector unitVector() {
    float length = length();
    return this.multiply(1 / length);
  }
  
  public Vector normal() {
    setXY(-y, x);
    return this;
  }
  
  public float bearing() {
    return (float) Math.atan2(y, x);
  }
  
  public static Vector copyOf(Vector vector) {
    return new Vector(vector.x, vector.y);
  }
  
  public void SetAngle(float angle) {
	  setXY((float)Math.cos(Math.toRadians(angle)),(float) Math.sin(Math.toRadians(angle)));
  }
  
  public Vector SetRadian(float radian) {
	  setXY((float)Math.cos(-radian),(float) Math.sin(-radian));
	  return this;
  }
  
  public static float GetAngle(Vector a)
  {
	  float angle;
	  angle = (float)Math.atan2(a.getY(),a.getX());
	  angle = (float)Math.toDegrees(angle);
	  return angle;
	  
  }
  public static float distance(Vector a,Vector b)
  {
	  float x = a.getX()-b.getX();
	  float y = a.getY()-b.getY();
	  return (float)Math.sqrt(x*x+y*y);
  }
  
  public static Vector normalize(Vector a)
  {
	  float v = a.length();
	  return new Vector(a.getX()/v,a.getY()/v);
	  
  }
}
