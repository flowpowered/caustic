#version 120

attribute vec2 position;
attribute vec2 textureCoords;

varying vec2 textureUV;

uniform mat4 modelMatrix;
uniform float glyphOffset;
uniform mat4 cameraMatrix;
uniform mat4 projectionMatrix;

void main() {
    textureUV = textureCoords;

    gl_Position = projectionMatrix * cameraMatrix * modelMatrix * vec4(position + vec2(glyphOffset, 0), 0, 1);
}
