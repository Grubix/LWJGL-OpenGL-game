#version 440

in vec2 position;

in mat4 modelViewMatrix;
in vec4 textureOffsets;
in float blendFactor;

out vec2 textureCoords1;
out vec2 textureCoords2;
out float pass_blendFactor;

uniform mat4 projectionMatrix;
uniform float numberOfAtlasRows;

void main(void){

	vec2 textureCoords = position + vec2(0.5, 0.5);
	textureCoords.y = 1.0 - textureCoords.y;
	textureCoords /= numberOfAtlasRows;

	textureCoords1 = textureCoords + textureOffsets.xy;
	textureCoords2 = textureCoords + textureOffsets.zw;
	pass_blendFactor = blendFactor;

	gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 0.0, 1.0);

}
