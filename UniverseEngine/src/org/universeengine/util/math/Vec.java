package org.universeengine.util.math;

public class Vec {
	
	public float[] data;
	
	public Vec(int size) {
		if (size > 4 || size < 1) {
			throw new IllegalArgumentException("Unnsupported data length: " + size);
		}
		data = new float[size];
	}
	
	public Vec(float... data) {
		if (data.length > 4) {
			throw new IllegalArgumentException("Unnsupported data length: " + data.length);
		}
		this.data = data;
	}
	
	/* Getters */
	public float x() {
		return data[0];
	}
	
	public float y() {
		return data[1];
	}
	
	public float z() {
		return data[2];
	}
	
	public float w() {
		return data[3];
	}
	
	/* Color-Getters */
	public float r() {
		return x();
	}
	
	public float g() {
		return y();
	}
	
	public float b() {
		return z();
	}
	
	public float a() {
		return w();
	}
	
	/* Vec-Getters */
	public Vec xy() {
		return new Vec(data[0], data[1]);
	}
	
	public Vec xyz() {
		return new Vec(data[0], data[1], data[2]);
	}
	
	public Vec xyzw() {
		return new Vec(data[0], data[1], data[2], data[3]);
	}
	
	/* Setters */
	public void x(float x) {
		data[0] = x;
	}
	
	public void y(float y) {
		data[1] = y;
	}
	
	public void z(float z) {
		data[2] = z;
	}
	
	public void w(float w) {
		data[3] = w;
	}
	
	public void set(float... data) {
		if (data.length != this.data.length) {
			throw new IllegalArgumentException("The given \"data\" Array's length is not equal to the size of this Vector!");
		}
		for (int i = 0; i < data.length; i++) {
			this.data[i] = data[i];
		}
	}
	
	public void clamp(float f) {
		for (int i = 0; i < data.length; i++) {
			while(data[i] > f) {
				data[i] -= f;
			}
		}
	}
	
	public float length() {
		if (data.length == 1) {
			return Math.abs(x());
		}
		float l = 0;
		for (int i = 0; i < data.length; i++) {
			l += data[i]*data[i];
		}
		return (float) Math.sqrt(l);
	}
	
	public void normalize() {
		div(this, length());
	}
	
	public void add(Vec v1, Vec v2) {
		int num = Math.min(Math.min(v1.data.length, v2.data.length), data.length);
		for (int i = 0; i < num; i++) {
			data[i] = v1.data[i] + v2.data[i];
		}
	}
	
	public void sub(Vec v1, Vec v2) {
		int num = Math.min(Math.min(v1.data.length, v2.data.length), data.length);
		for (int i = 0; i < num; i++) {
			data[i] = v1.data[i] - v2.data[i];
		}
	}
	
	public void mult(Vec v1, Vec v2) {
		int num = Math.min(Math.min(v1.data.length, v2.data.length), data.length);
		for (int i = 0; i < num; i++) {
			data[i] = v1.data[i] * v2.data[i];
		}
	}
	
	public void div(Vec v1, Vec v2) {
		int num = Math.min(Math.min(v1.data.length, v2.data.length), data.length);
		for (int i = 0; i < num; i++) {
			data[i] = v1.data[i] / v2.data[i];
		}
	}
	
	public void cross(Vec v1, Vec v2) {
		if (v1.data.length != 3 || v2.data.length != 3) {
			throw new IllegalArgumentException(String.format("Unsupported Data-length for computing cross-product: v1.data.lengt: %d, v2.data.length: %d", v2.data.length, v2.data.length));
		}
		data[0] = v1.data[1] * v2.data[2] - v1.data[2] * v2.data[1];
		data[1] = v1.data[2] * v2.data[0] - v1.data[0] * v2.data[2];
		data[2] = v1.data[0] * v2.data[1] - v1.data[1] * v2.data[0];
	}
	
	public String toString() {
		StringBuffer dataStr = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			if (i == data.length-1) {
				dataStr.append(data[i]);
			} else {
				dataStr.append(data[i] + ", ");
			}
		}
		return String.format("Vec%d: [%s]", data.length, dataStr.toString());
	}
	
	public static void add(Vec v, float f) {
		for (int i = 0; i < v.data.length; i++) {
			v.data[i] += f;
		}
	}
	
	public static void sub(Vec v, float f) {
		for (int i = 0; i < v.data.length; i++) {
			v.data[i] -= f;
		}
	}
	
	public static void mult(Vec v, float f) {
		for (int i = 0; i < v.data.length; i++) {
			v.data[i] *= f;
		}
	}
	
	public static void div(Vec v, float f) {
		for (int i = 0; i < v.data.length; i++) {
			v.data[i] /= f;
		}
	}
	
	public static Vec addVec(Vec v1, Vec v2) {
		int num = Math.min(v1.data.length, v2.data.length);
		Vec vec = new Vec(num);
		for (int i = 0; i < num; i++) {
			vec.data[i] = v1.data[i] + v2.data[i];
		}
		return vec;
	}
	
	public static Vec subVec(Vec v1, Vec v2) {
		int num = Math.min(v1.data.length, v2.data.length);
		Vec vec = new Vec(num);
		for (int i = 0; i < num; i++) {
			vec.data[i] = v1.data[i] - v2.data[i];
		}
		return vec;
	}
	
	public static Vec multVec(Vec v1, Vec v2) {
		int num = Math.min(v1.data.length, v2.data.length);
		Vec vec = new Vec(num);
		for (int i = 0; i < num; i++) {
			vec.data[i] = v1.data[i] * v2.data[i];
		}
		return vec;
	}
	
	public static Vec divVec(Vec v1, Vec v2) {
		int num = Math.min(v1.data.length, v2.data.length);
		Vec vec = new Vec(num);
		for (int i = 0; i < num; i++) {
			vec.data[i] = v1.data[i] / v2.data[i];
		}
		return vec;
	}
	
	public static float dot(Vec v1, Vec v2) {
		int num = Math.min(v1.data.length, v2.data.length);
		float f = 0;
		for (int i = 0; i < num; i++) {
			f += v1.data[i] * v2.data[i];
		}
		return f;
	}
	
	public static Vec crossVec(Vec v1, Vec v2) {
		if (v1.data.length != 3 || v2.data.length != 3) {
			throw new IllegalArgumentException(String.format("Unsupported Data-length for computing cross-product: v1.data.lengt: %d, v2.data.length: %d", v2.data.length, v2.data.length));
		}
		return new Vec(
				v1.data[1] * v2.data[2] - v1.data[2] * v2.data[1],
				v1.data[2] * v2.data[0] - v1.data[0] * v2.data[2],
				v1.data[0] * v2.data[1] - v1.data[1] * v2.data[0]);
	}
	
	public static void main(String[] args) {
		
		System.out.println("---------------------");
		
		Vec vec = new Vec(1f, 4.1f, 5.2f, 0.3f);
		for (float f : vec.data) {
			System.out.println(f);
		}
		vec.clamp(1f);
		System.out.println(vec.toString());
		
		System.out.println("---------------------");
		
		Vec vec1 = new Vec(2f, 5f, 1.2f, 3.56782f);
		Vec vec2 = new Vec(5f, 3f);
		System.out.println("Vec1: " + vec1.toString());
		System.out.println("Vec2: " + vec2.toString());
		System.out.println("Sub result: " + subVec(vec1, vec2));
		
		System.out.println("---------------------");
		
		Vec dest = new Vec(4);
		System.out.println("Dest: " + dest);
		dest.mult(vec1, vec2);
		System.out.println("Dest after multiplicating Vec1, Vec2: " + dest);
		
		System.out.println("---------------------");
		
		Vec vec3 = new Vec(1366f, 768f, 30f, 40f);
		System.out.println("vec3: " + vec3);
		System.out.println("Length of vec3: " + vec3.length());
		System.out.println("Normalized:");
		vec3.normalize();
		System.out.println("vec3: " + vec3);
		System.out.println("vec3's normalized length: " + vec3.length());
		
		System.out.println("---------------------");
		
		Vec v1 = new Vec(1f, 4f, 10f);
		Vec v2 = new Vec(2f, -5f, 3f);
		System.out.println("Cross Product from: ");
		System.out.println("v1: " + v1);
		System.out.println("v2: " + v2);
		System.out.println("Cross vector: " + crossVec(v1, v2));
		
		System.out.println("---------------------");
		
		Vec vecCross = crossVec(v1, v2);
		System.out.println("Dot Product from v1 and v2: " + dot(v1, v2));
		
		System.out.println("v1 dot vecCross: " + dot(v1, vecCross));
		System.out.println("v2 dot vecCross: " + dot(v2, vecCross));
		
		System.out.println("---------------------");
		
		Vec v3 = new Vec(-3f);
		System.out.printf("Length of v3 %s: %f\n", v3, v3.length());
		
	}
	
}
