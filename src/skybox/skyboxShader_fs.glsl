#version 400 core

in vec3 textureCoords;
out vec4 out_Color;

uniform samplerCube cubeMap1;
uniform samplerCube cubeMap2;
uniform float blendFactor;
uniform vec3 fogColor;

const float lowerLimit = 0.0;
const float upperLimit = 30.0;

void main(void){
    vec4 tex_1 = texture(cubeMap1, textureCoords);
    vec4 tex_2 = texture(cubeMap2, textureCoords);
    vec4 finalColor = mix(tex_1, tex_2, blendFactor);
    
    float factor = (textureCoords.y - lowerLimit) / (upperLimit - lowerLimit);
	factor = clamp(factor, 0.0, 1.0);
	
	out_Color = mix(vec4(fogColor, 1.0), finalColor, factor);
}