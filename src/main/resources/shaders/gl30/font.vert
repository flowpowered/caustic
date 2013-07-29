#version 330

layout(location = 0) in vec2 position;
layout(location = 1) in vec2 textureCoords;

out vec2 textureUV;

uniform mat4 modelMatrix;
uniform float glyphOffset;
uniform mat4 cameraMatrix;
uniform mat4 projectionMatrix;

void main() {
    textureUV = textureCoords;

    gl_Position = projectionMatrix * cameraMatrix * modelMatrix * vec4(position + vec2(glyphOffset, 0), 0, 1);
}
