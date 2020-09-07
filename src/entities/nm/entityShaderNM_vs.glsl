#version 400 core
const int MAX_LIGHTS = 4;

in vec3 vertices;
in vec2 textureCoords;
in vec3 normals;
in vec3 tangent;

out vec2 pass_textureCoords;
out vec3 toLightVector[MAX_LIGHTS];
out vec3 toCameraVector;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPositionEyeSpace[MAX_LIGHTS];

uniform float numberOfAtlasRows;
uniform vec2 textureOffset;

uniform float density = 0.05;
uniform float gradient = 6;

uniform vec4 clipPlane;

void main(void) {

	vec4 worldPosition = transformationMatrix * vec4(vertices,1.0);
	gl_ClipDistance[0] = dot(worldPosition, clipPlane);
	
	mat4 modelViewMatrix = viewMatrix * transformationMatrix;
	vec4 positionRelativeToCam = modelViewMatrix * vec4(vertices,1.0);
	gl_Position = projectionMatrix * positionRelativeToCam;
	pass_textureCoords = textureCoords/numberOfAtlasRows + textureOffset;
	
	vec3 surfaceNormal = (modelViewMatrix * vec4(normals,0.0)).xyz;
	
	vec3 norm = normalize(surfaceNormal);
	vec3 tang = normalize((modelViewMatrix * vec4(tangent, 0.0)).xyz);
	vec3 bitang = normalize(cross(norm, tang));
	mat3 toTangentSpace = mat3(
		tang.x, bitang.x, norm.x,
		tang.y, bitang.y, norm.y,
		tang.z, bitang.z, norm.z
	);
	
	for(int i=0 ; i<MAX_LIGHTS ; i++) {
		toLightVector[i] = toTangentSpace * (lightPositionEyeSpace[i] - positionRelativeToCam.xyz);
	}
	
	toCameraVector = toTangentSpace * (-positionRelativeToCam.xyz);

	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow((distance*density),gradient));
	visibility = clamp(visibility, 0.0, 1.0);
}
