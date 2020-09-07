package entities;

import org.joml.Vector3f;

public class Sun extends Light {

	private final float radius = 1000f;
	private float currentAngle = 0;
	
	public Sun(Vector3f position, Vector3f color) {
		super(position, color);
	}
	
	public void update() {
		currentAngle += 0.1;
		float y = radius * (float)Math.sin(Math.toRadians(currentAngle));
		float x = radius * (float)Math.cos(Math.toRadians(currentAngle));
		float a = x / (float)Math.sqrt(2);
		
		super.setPosition(a, y, a);
	}

}
