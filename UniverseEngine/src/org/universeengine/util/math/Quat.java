package org.universeengine.util.math;

public class Quat extends Vec {
	
	public Quat(float f0, float f1, float f2, float f3) {
		super(f0, f1, f2, f3);
	}
	
	 public void set(float f0, float f1, float f2, float f3) {
			data[0] = f0;
			data[1] = f1;
			data[2] = f2;
			data[3] = f3;
	 }
	 
	 public String toString() {
		 return String.format("Quat: [%f, %f, %f, %f]", data[0], data[1], data[2], data[3]);
	 }
	 
	 public void add(Quat q1, Quat q2) {
		 set(
				 q1.data[0] + q2.data[0],
				 q1.data[1] + q2.data[1],
				 q1.data[2] + q2.data[2],
				 q1.data[3] + q2.data[3]);
	 }
	 
	 public void sub(Quat q1, Quat q2) {
		 set(
				 q1.data[0] - q2.data[0],
				 q1.data[1] - q2.data[1],
				 q1.data[2] - q2.data[2],
				 q1.data[3] - q2.data[3]);
	 }
	 
	 public void mult(Quat q1, Quat q2) {
		 set(
				 q1.data[0] + q2.data[0] - q1.data[1] * q2.data[1] - q1.data[2] * q2.data[2] - q1.data[3] * q2.data[3],
				 q1.data[0] - q2.data[1] + q1.data[1] * q2.data[0] + q1.data[2] * q2.data[3] - q1.data[3] * q2.data[2],
				 q1.data[0] - q2.data[2] - q1.data[1] * q2.data[3] + q1.data[2] * q2.data[0] + q1.data[3] * q2.data[1],
				 q1.data[0] - q2.data[3] + q1.data[1] * q2.data[2] - q1.data[2] * q2.data[1] + q1.data[3] * q2.data[0]);
	 }
	 
	 public void div(Quat q1, Quat q2) {
		 set(
				 (q1.data[0] + q2.data[0] - q1.data[1] * q2.data[1] - q1.data[2] * q2.data[2] - q1.data[3] * q2.data[3])
				 / q1.data[0] * q1.data[0] + q1.data[1] * q1.data[1] + q1.data[2] * q1.data[2] + q1.data[3] * q1.data[3],
				 
				 (q1.data[0] - q2.data[1] + q1.data[1] * q2.data[0] + q1.data[2] * q2.data[3] - q1.data[3] * q2.data[2])
				 / q1.data[0] * q1.data[0] + q1.data[1] * q1.data[1] + q1.data[2] * q1.data[2] + q1.data[3] * q1.data[3],
				 
				 (q1.data[0] - q2.data[2] - q1.data[1] * q2.data[3] + q1.data[2] * q2.data[0] + q1.data[3] * q2.data[1])
				 / q1.data[0] * q1.data[0] + q1.data[1] * q1.data[1] + q1.data[2] * q1.data[2] + q1.data[3] * q1.data[3],
				 
				 (q1.data[0] - q2.data[3] + q1.data[1] * q2.data[2] - q1.data[2] * q2.data[1] + q1.data[3] * q2.data[0])
				 / q1.data[0] * q1.data[0] + q1.data[1] * q1.data[1] + q1.data[2] * q1.data[2] + q1.data[3] * q1.data[3]);
	 }
	 
	 public void rotVec(Vec v) {
		 Vec temp = new Vec(
			 (1 - 2 * sq(data[2]) - 2 * sq(data[3])) * 2 * (data[1] * data[2] + data[0] * data[3]) * 2 * (data[1] * data[3] - data[0] * data[2]),
			 2 * (data[1] * data[2] - data[0] * data[3]) * (1 - 2 * sq(data[2]) - 2 * sq(data[3])) * 2 * (data[2] * data[3] + data[0] * data[1]),
			 2 * (data[1] * data[3] + data[0] * data[2]) * 2 * (data[2] * data[3] - data[0] * data[1]) * (1 - 2 * sq(data[2]) - 2 * sq(data[3])));
		 v.mult(temp, v);
	 }
	 
	 private static float sq(float f) {
		 return f*f;
	 }
	 
	 public static Quat addQuat(Quat q1, Quat q2) {
		 return new Quat(
				 q1.data[0] + q2.data[0],
				 q1.data[1] + q2.data[1],
				 q1.data[2] + q2.data[2],
				 q1.data[3] + q2.data[3]);
	 }
	 
	 public static Quat subQuat(Quat q1, Quat q2) {
		 return new Quat(
				 q1.data[0] - q2.data[0],
				 q1.data[1] - q2.data[1],
				 q1.data[2] - q2.data[2],
				 q1.data[3] - q2.data[3]);
	 }
	 
	 public static Quat multQuat(Quat q1, Quat q2) {
		 return new Quat(
				 q1.data[0] + q2.data[0] - q1.data[1] * q2.data[1] - q1.data[2] * q2.data[2] - q1.data[3] * q2.data[3],
				 q1.data[0] - q2.data[1] + q1.data[1] * q2.data[0] + q1.data[2] * q2.data[3] - q1.data[3] * q2.data[2],
				 q1.data[0] - q2.data[2] - q1.data[1] * q2.data[3] + q1.data[2] * q2.data[0] + q1.data[3] * q2.data[1],
				 q1.data[0] - q2.data[3] + q1.data[1] * q2.data[2] - q1.data[2] * q2.data[1] + q1.data[3] * q2.data[0]);
	 }
	 
	 public static Quat divQuat(Quat q1, Quat q2) {
		 return new Quat(
				 (q1.data[0] + q2.data[0] - q1.data[1] * q2.data[1] - q1.data[2] * q2.data[2] - q1.data[3] * q2.data[3])
				 / q1.data[0] * q1.data[0] + q1.data[1] * q1.data[1] + q1.data[2] * q1.data[2] + q1.data[3] * q1.data[3],
				 
				 (q1.data[0] - q2.data[1] + q1.data[1] * q2.data[0] + q1.data[2] * q2.data[3] - q1.data[3] * q2.data[2])
				 / q1.data[0] * q1.data[0] + q1.data[1] * q1.data[1] + q1.data[2] * q1.data[2] + q1.data[3] * q1.data[3],
				 
				 (q1.data[0] - q2.data[2] - q1.data[1] * q2.data[3] + q1.data[2] * q2.data[0] + q1.data[3] * q2.data[1])
				 / q1.data[0] * q1.data[0] + q1.data[1] * q1.data[1] + q1.data[2] * q1.data[2] + q1.data[3] * q1.data[3],
				 
				 (q1.data[0] - q2.data[3] + q1.data[1] * q2.data[2] - q1.data[2] * q2.data[1] + q1.data[3] * q2.data[0])
				 / q1.data[0] * q1.data[0] + q1.data[1] * q1.data[1] + q1.data[2] * q1.data[2] + q1.data[3] * q1.data[3]);
	 }
	 
	 public static Vec rotVec(Vec v, Quat q) {
		 Vec temp = new Vec(
			 (1 - 2 * sq(q.data[2]) - 2 * sq(q.data[3])) * 2 * (q.data[1] * q.data[2] + q.data[0] * q.data[3]) * 2 * (q.data[1] * q.data[3] - q.data[0] * q.data[2]),
			 2 * (q.data[1] * q.data[2] - q.data[0] * q.data[3]) * (1 - 2 * sq(q.data[2]) - 2 * sq(q.data[3])) * 2 * (q.data[2] * q.data[3] + q.data[0] * q.data[1]),
			 2 * (q.data[1] * q.data[3] + q.data[0] * q.data[2]) * 2 * (q.data[2] * q.data[3] - q.data[0] * q.data[1]) * (1 - 2 * sq(q.data[2]) - 2 * sq(q.data[3])));
		 return multVec(temp, v);
	 }
	 
	 public static void main(String[] args) {
		 Quat q1 = new Quat(1f, 4f, 3f, 7f);
		 Quat q2 = new Quat(3f, 2f, 10f, -4f);
		 System.out.println("Everything unnormalized:");
		 System.out.println("q1: " + q1 + " (length: " + q1.length() + ")");
		 System.out.println("q2: " + q2 + " (length: " + q2.length() + ")");
		 Quat qAdd = addQuat(q1, q2);
		 Quat qSub = subQuat(q1, q2);
		 Quat qMult = multQuat(q1, q2);
		 Quat qDiv = divQuat(q1, q2);
		 System.out.println("q1 + q2: " + qAdd + " (length: " + qAdd.length() + ")");
		 System.out.println("q1 - q2: " + qSub + " (length: " + qSub.length() + ")");
		 System.out.println("q1 * q2: " + qMult + " (length: " + qMult.length() + ")");
		 System.out.println("q1 / q2: " + qDiv + " (length: " + qDiv.length() + ")");
		 q1.normalize();
		 q2.normalize();
		 System.out.println("After normalization:");
		 System.out.println("q1: " + q1 + " (length: " + q1.length() + ")");
		 System.out.println("q2: " + q2 + " (length: " + q2.length() + ")");
		 qAdd = addQuat(q1, q2);
		 qSub = subQuat(q1, q2);
		 qMult = multQuat(q1, q2);
		 qDiv = divQuat(q1, q2);
		 System.out.println("q1 + q2: " + qAdd + " (length: " + qAdd.length() + ")");
		 System.out.println("q1 - q2: " + qSub + " (length: " + qSub.length() + ")");
		 System.out.println("q1 * q2: " + qMult + " (length: " + qMult.length() + ")");
		 System.out.println("q1 / q2: " + qDiv + " (length: " + qDiv.length() + ")");
		 System.out.println("Normalization after each computation:");
		 qAdd.normalize();
		 qSub.normalize();
		 qMult.normalize();
		 qDiv.normalize();
		 System.out.println("q1 + q2: " + qAdd + " (length: " + qAdd.length() + ")");
		 System.out.println("q1 - q2: " + qSub + " (length: " + qSub.length() + ")");
		 System.out.println("q1 * q2: " + qMult + " (length: " + qMult.length() + ")");
		 System.out.println("q1 / q2: " + qDiv + " (length: " + qDiv.length() + ")");
	 }
	 
}
