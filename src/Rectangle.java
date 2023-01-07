
public class Rectangle {
	protected float x,y,w,h;

	public Rectangle() {
		set(0,0,0,0);
	}
	
	public Rectangle (float x,float y,float w,float h){
		float x1,y1,w1,h1;
		x1 = Math.min(x, x+w);
		y1 = Math.min(y, y+h);
		w1 = Math.abs(w);
		h1 = Math.abs(h);
		this.x=x1;
		this.y=y1;
		this.w=w1;
		this.h=h1;
	}
	
	public Rectangle(Rectangle rect)
	{
		set(rect);
	}
	
	public float get_x(){ return x;	}
	public float get_y(){return y;}
	public float get_w(){return w;}
	public float get_h(){return h;}

	public float x(){ return x;	}
	public float y(){return y;}
	public float w(){return w;}
	public float h(){return h;}
	
	public void set_x(float x){ this.x = x;	}
	public void set_y(float y){this.y = y;}
	public void set_w(float w){this.w = w;}
	public void set_h(float h){this.h = h;}
	
	public void x(float x){ this.x = x;	}
	public void y(float y){this.y = y;}
	public void w(float w){this.w = w;}
	public void h(float h){this.h = h;}
	
	public void set (float x,float y,float w,float h){
		float x1,y1,w1,h1;
		x1 = Math.min(x, x+w);
		y1 = Math.min(y, y+h);
		w1 = Math.abs(w);
		h1 = Math.abs(h);
		this.x=x1;
		this.y=y1;
		this.w=w1;
		this.h=h1;
	}
	
	public void set(Rectangle rect)
	{
		if(rect == null)
			return;
		set(rect.x,rect.y,rect.w,rect.h);
	}
	
	public static boolean check_rect_intersection(Rectangle a,Rectangle b) {
		if(Math.abs(a.x+a.w/2-(b.x+b.w/2))<(a.w+b.w)/2 &&(Math.abs(a.y+a.h/2-(b.y+b.h/2))<(a.h+b.h)/2))
		{
			return true;
		}
		return false;
	}
	
	public float center_x() {
		return x+w/2;
	}
	
	public float center_y() {
		return y+h/2;
	}
	
	public void set_center(float cx,float cy) {
		x=cx-w/2;
		y=cy-h/2;
	}
	
	
//	
	public static boolean check_rect_intersection_weak(Rectangle a,Rectangle b) {
//		System.out.print("checking a: "+ a.x+" "+a.y+" "+(a.x+a.w)+" "+(a.y+a.h));
//		System.out.print("checking b: "+ b.x+" "+b.y+" "+(b.x+b.w)+" "+(b.y+b.h));
//		System.out.print("checking condition: "+ (a.x < b.x && a.x+a.w > b.x)+" "+(a.x > b.x && a.x+ a.w < b.x+b.w)+" "+(a.x < b.x+b.w && a.x+a.w > b.x+b.w));
//		
		boolean xcheck = ((a.x < b.x && a.x+a.w > b.x) || (a.x > b.x && a.x+ a.w < b.x+b.w) || (a.x < b.x+b.w && a.x+a.w > b.x+b.w));
		boolean ycheck = ((a.y < b.y && a.y+a.h > b.y) || (a.y > b.y && a.y+ a.h < b.y+b.h) || (a.y < b.y+b.h && a.y+a.h > b.y+b.h));
		
		if ((a.x < b.x && a.x+a.w > b.x || (a.x > b.x && a.x+ a.w < b.x+b.w) || (a.x < b.x+b.w && a.x+a.w > b.x+b.w))
		&& (a.y < b.y && a.y+a.h > b.y || (a.y > b.y && a.y+ a.h < b.y+b.h) || (a.y < b.y+b.h && a.y+a.h > b.y+b.h)))
		{
			return true;
		}
		return false;
	}
	
	public static boolean check_point_intersection(Rectangle r,Vector v)
	{
		if((v.getX() >=r.x && v.getX() <=r.x+r.w) &&(v.getY()>=r.y && v.getY()<= r.y+r.h))
		{
			return true;
		}
		return false;
	}
}
