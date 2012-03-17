package org.universeengine;

public interface UniverseEngineEnterPoint {
	
	public void start();
	
	public void tick();
	public void render();
	
	public void pause();
	public void resume();
	
	public void onResize(int oldWidth, int oldHeight);
	
	public void end();

}
