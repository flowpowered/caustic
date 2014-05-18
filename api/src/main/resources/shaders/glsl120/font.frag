// $shader_type: fragment

// $texture_layout: diffuse = 0

#version 120

varying vec2 textureUV;

uniform sampler2D diffuse;
uniform vec4 fontColor;

void main() {
    float color = texture2D(diffuse, textureUV).r;

    if (color <= 0) {
        discard;
    }

    gl_FragColor = color * fontColor;
}
