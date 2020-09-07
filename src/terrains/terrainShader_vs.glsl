#version 400 core
const int MAX_LIGHTS = 4;

in vec3 vertices;
in vec2 textureCoords;
in vec3 normals;

out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector[MAX_LIGHTS];
out vec3 toCameraVector;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[MAX_LIGHTS];

uniform float density = 0.05;
uniform float gradient = 8;

uniform vec4 clipPlane;

void main(void) {

	vec4 worldPosition = transformationMatrix * vec4(vertices,1.0);
	gl_ClipDistance[0] = dot(worldPosition, clipPlane);
	
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCam;	
	pass_textureCoords = textureCoords;
	
	surfaceNormal = (transformationMatrix * vec4(normals,0.0)).xyz;
	for(int i=0 ; i<MAX_LIGHTS ; i++) {
		toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}
	toCameraVector = (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz - worldPosition.xyz;
	
	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow((distance*density),gradient));
	visibility = clamp(visibility, 0.0, 1.0);
}
