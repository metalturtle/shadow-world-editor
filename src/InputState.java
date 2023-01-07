

public class InputState 
{
	public static boolean keyboard[] = new boolean[256];
	public static Vector mouse = new Vector(0,0);
	public static boolean left_click, right_click;
	
	static public void map_keyboard(boolean keys[])
	{
		for(int i=0;i<256;i++)
			keyboard[i] = keys[i];
	}
	
	static public void map_mouse(float x, float y)
	{
//		mouse.setXY(x,y);
	}
	
	static public void map_input(boolean keys[],float x,float y)
	{
		map_keyboard(keys);
		map_mouse(x,y);
	}
}
