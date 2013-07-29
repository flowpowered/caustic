#version 330

in vec2 textureUV;

out vec4 outputColor;

uniform sampler2D diffuse;
uniform vec4 fontColor;

void main() {
    vec4 color = texture(diffuse, textureUV);

    if (color.a <= 0) {
        discard;
    }

    outputColor = color * fontColor;
}
