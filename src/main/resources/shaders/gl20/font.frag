#version 120

varying vec2 textureUV;

uniform sampler2D diffuse;
uniform vec4 fontColor;

void main() {
    vec4 color = texture2D(diffuse, textureUV);

    if (color.a <= 0) {
        discard;
    }

    gl_FragColor = color * fontColor;
}
