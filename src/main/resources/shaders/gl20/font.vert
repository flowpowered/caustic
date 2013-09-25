// $shader_type: vertex

// $attrib_layout: position = 0
// $attrib_layout: textureCoords = 1

#version 120

attribute vec2 position;
attribute vec2 textureCoords;

varying vec2 textureUV;

uniform mat4 modelMatrix;
uniform vec2 glyphOffset;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main() {
    textureUV = textureCoords;

    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position + glyphOffset, 0, 1);
}
