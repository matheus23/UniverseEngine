package org.universeengine.util.cam;

import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.universeengine.display.UniLoop;

public class UniCamera {

	public static float smooth = 0.2f;

	/** User-Variables */
	private float x;
	private float y;
	private float z;
	private float rotx;
	private float roty;
	private float rotz;
	private float speed;
	private float rotSpeed;

	/** "Performance"-Variables */
	private float mov;
	private float movside;

	private UniLoop loop;
	private boolean mouseReset;

	public UniCamera(UniLoop l) {
		speed = 0.1f;
		rotSpeed = 1f;
		x = 0f;
		y = 0f;
		z = 10f;
		rotx = 0;
		roty = 0;
		rotz = 0;
		loop = l;
		mouseReset = true;
		Mouse.setCursorPosition(loop.display.getSize().width / 2,
				loop.display.getSize().height / 2);
	}

	public void update() {
		if (!Mouse.isInsideWindow() && Mouse.isGrabbed()) {
			Mouse.setCursorPosition(loop.display.getSize().width / 2,
					loop.display.getSize().height / 2);
			mouseReset = true;
		}
		calcMove();
		calcRotation();
		mouseReset = false;
	}

	private void calcMove() {
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)
				|| Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			y += speed;
		} if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)
				|| Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			y -= speed;
		} if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			roty += rotSpeed;
		} if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			roty -= rotSpeed;
		}

		mov = (float) (roty * Math.PI / 180);
		movside = (float) ((roty + 90f) * Math.PI / 180);

		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			/** Relative movement forwards */
			x -= speed * Math.sin(mov);
			z -= speed * Math.cos(mov);
		} if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			/** Relative movement backwards */
			x += speed * Math.sin(mov);
			z += speed * Math.cos(mov);
		} if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			/** Relative movement to the right */
			x += speed * Math.sin(movside);
			z += speed * Math.cos(movside);
		} if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			/** Relative movement to the left */
			x -= speed * Math.sin(movside);
			z -= speed * Math.cos(movside);
		}
	}

	private void calcRotation() {
		if (!mouseReset) {
			roty -= Mouse.getDX() * smooth;
			rotx += Mouse.getDY() * smooth;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
			rotz += 1.0f;
		} if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
			rotz -= 1.0f;
		} 
		if (rotx < -90f) {
			rotx = -90f;
		} if (rotx > 90f) {
			rotx = 90f;
		}
	}

	public void apply() {
		glRotatef(rotz, 0f, 0f, -1f);
		glRotatef(rotx, -1f, 0f, 0f);
		glRotatef(roty, 0f, -1f, 0f);
		glTranslatef(-x, -y, -z);
	}

	public void move(float dx, float dy, float dz) {
		x += dx;
		y += dy;
		z += dz;
	}

	public void rotate(float rx, float ry, float rz) {
		rotx += rx;
		roty += ry;
		rotz += rz;
	}

	public void setPos(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setRot(float rotx, float roty, float rotz) {
		this.rotx = rotx;
		this.roty = roty;
		this.rotz = rotz;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public float getRotX() {
		return rotx;
	}

	public float getRotY() {
		return roty;
	}

	public float getRotZ() {
		return rotz;
	}

}
