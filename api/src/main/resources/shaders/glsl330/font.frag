// $shader_type: fragment

// $texture_layout: diffuse = 0

#version 330

in vec2 textureUV;

out vec4 outputColor;

uniform sampler2D diffuse;
uniform vec4 fontColor;

void main() {
    float color = texture(diffuse, textureUV).r;

    if (color <= 0) {
        discard;
    }

    outputColor = color * fontColor;
}
