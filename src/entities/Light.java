package entities;

import org.joml.Vector3f;

public class Light {

	private Vector3f position;
	private Vector3f color;
	private Vector3f attenuation = new Vector3f(1, 0, 0);
	
	public Light(Vector3f position, Vector3f color) {
		this.position = position;
		this.color = color;
	}
	public Light(Vector3f position, Vector3f color, Vector3f attenuation) {
		this(position, color);
		this.attenuation = attenuation;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}
	public void setPosition(float XPos, float YPos, float ZPos) {
		this.position.x = XPos;
		this.position.y = YPos;
		this.position.z = ZPos;
	}
	public void increasePosition(float dxPos, float dyPos, float dzPos) {
		this.position.x += dxPos;
		this.position.y += dyPos;
		this.position.z += dzPos;
	}
	public void setColor(Vector3f color) {
		this.color = color;
	}
	public void setColor(float R, float G, float B) {
		this.color.x = R;
		this.color.y = G;
		this.color.z = B;
	}
	public void setAttenuation(Vector3f attenuation) {
		this.attenuation = attenuation;
	}
	//TODO setAttenuation(float X? , float Y?, float Z?) jak to nazwac ?
	
	public Vector3f getPosition() {
		return position;
	}
	public Vector3f getColor() {
		return color;
	}
	public Vector3f getAttenuation() {
		return attenuation;
	}
}
