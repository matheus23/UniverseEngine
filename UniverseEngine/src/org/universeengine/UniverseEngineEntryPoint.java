package org.universeengine;

public interface UniverseEngineEntryPoint {

	public void start();

	public void tick();
	public void render();

	public void displayUpdate();

	public void pause();
	public void resume();

	public void onResize(int oldWidth, int oldHeight, int newWidth, int newHeight);
	public boolean isCloseRequested();

	public void end();

}
