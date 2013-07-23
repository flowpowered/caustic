#version 330

in vec3 modelPosition;
in vec3 modelNormal;
in vec3 viewDirection;

out vec4 outputColor;

uniform vec3 lightPosition;
uniform float diffuseIntensity;
uniform float specularIntensity;
uniform float ambientIntensity;

void main() {
    vec3 lightDirection = normalize(lightPosition - modelPosition);
    vec4 modelColor = vec4(0.9, 0.1, 0.1, 1);

    vec4 diffuse = modelColor * clamp(dot(modelNormal, lightDirection), 0, 1);
    vec4 specular = modelColor * pow(clamp(dot(reflect(-lightDirection, modelNormal), viewDirection), 0, 1), 2);
    vec4 ambient = modelColor;

    outputColor = diffuse * diffuseIntensity + specular * specularIntensity + ambient * ambientIntensity;
}
