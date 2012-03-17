package org.universeengine.opengl.vertex;

public interface UniVertex {
	
	public static final int FLOAT = 0;
	public static final int INT = 1;
	
	public static final int X = 0;
	public static final int Y = 1;
	public static final int Z = 2;
	
	public static final int NX = 0;
	public static final int NY = 1;
	public static final int NZ = 2;
	
	public static final int U = 0;
	public static final int V = 1;
	
	public static final int R = 0;
	public static final int G = 1;
	public static final int B = 2;
	public static final int A = 3;
	
	public int getBandwidth();
	public int getType();
	public void render();
	
	public float[] get();

}
