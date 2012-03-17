package org.universeengine.util.input;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

public class UniInput {
	
	private boolean[] pressed = new boolean[Keyboard.KEYBOARD_SIZE];
	private List<UniInputListener> listeners = new ArrayList<UniInputListener>(1);
	
	public UniInput(UniInputListener listener) {
		listeners.add(listener);
	}
	
	public void addInputListener(UniInputListener listener) {
		listeners.add(listener);
	}
	
	public void update() {
		for (int i = 0; i < Keyboard.KEYBOARD_SIZE; i++) {
			if (Keyboard.isKeyDown(i)) {
				if (!pressed[i]) {
					listenersPress(i);
				}
				pressed[i] = true;
			} else {
				if (pressed[i]) {
					listenersRelease(i);
				}
				pressed[i] = false;
			}
		}
	}
	
	public void listenersPress(int key) {
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).keyPressed(key);
		}
	}
	
	public void listenersRelease(int key) {
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).keyReleased(key);
		}
	}
	
	public boolean isDown(int key) {
		return Keyboard.isKeyDown(key);
	}

}
