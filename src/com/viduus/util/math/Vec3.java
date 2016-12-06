package com.viduus.util.math;

public class Vec3 {

	public static Vec3 add(Vec3 a, Vec3 b){
		Vec3 answer = new Vec3(a.x + b.x, a.y + b.y, a.z + b.z);
		return answer;
	}
	// Multiplies the vectors and returns a vector
	public static Vec3 crossProduct(Vec3 a, Vec3 b){
		Vec3 answer = new Vec3((a.y * b.z - a.z * b.y),(a.z * b.x - a.x * b.z),(a.x * b.y - a.y * b.x));
		return answer;
	}
	// Multiplies the vectors and returns a scalar quantity
	public static float dotProduct(Vec3 a, Vec3 b){
		return (a.x * b.x) + (a.y + b.y) + (a.z + b.z);
	}
	
	/**
	 * TODO
	 * @param subtract
	 * @return
	 */
	public static Vec3 normalize(Vec3 v) {
		float length = v.getLength();
		return new Vec3( v.x/length, v.y/length, v.z/length );
	}
	/**
	 * TODO
	 * @param eye
	 * @param center
	 * @return
	 */
	public static Vec3 subtract(Vec3 v1, Vec3 v2) {
		return new Vec3(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
	}
	public float x;
	
	public float y;

	public float z;
	
	public Vec3(){
		x = 0;
		y = 0;
		z = 0;
	}
	
	public Vec3(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void add(Vec3 b){
		x += b.x;
		y += b.y;
		z += b.z;
	}
	
	public void crossProduct(Vec3 b){
		x = y * b.z - z * b.y;
		y = z * b.x - x * b.z;
		z = x * b.y - y * b.x;
	}
	
	public float dotProduct(Vec3 b){
		return (x * b.x) + (y + b.y) + (z + b.z);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vec3 other = (Vec3) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
			return false;
		return true;
	}
	public float getLength() {
		float len_sq = x*x + y*y + z*z;
		if( len_sq == 1 ){
			return 1;
		}else{
			return (float) Math.sqrt(len_sq);
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		result = prime * result + Float.floatToIntBits(z);
		return result;
	}
	
	public Vec3 normalize() {
		return normalize(this);
	}
	
	public void reset(){
		x = 0;
		y = 0;
		z = 0;
	}
	
	@Override
	public String toString() {
		return "Vec3 [x=" + x + ", y=" + y + ", z=" + z + "]";
	}
	
	/**
	 * TODO
	 * @param direction
	 * @return
	 */
	public static Vec3 negate(Vec3 v) {
		return new Vec3(-v.x, -v.y, -v.z);
	}
	/**
	 * TODO
	 * @param location
	 * @param mouse_location
	 * @return
	 */
	public static float distanceSquared(Vec3 v1, Vec3 v2) {
		float dx = v1.x-v2.x;
		float dy = v1.y-v2.y;
		float dz = v1.z-v2.z;
		return dx*dx + dy*dy + dz*dz;
	}
}
