#version 330

smooth in vec3 modelPosition;
smooth in vec3 modelNormal;
smooth in vec3 viewDirection;

out vec4 outputColor;

uniform vec4 modelColor;
uniform vec3 lightPosition;
uniform float lightAttenuation;
uniform float diffuseIntensity;
uniform float specularIntensity;
uniform float ambientIntensity;

void main() {
    vec3 lightDifference = lightPosition - modelPosition;
    float lightDistance = length(lightDifference);
    vec3 lightDirection = lightDifference / lightDistance;
    float distanceIntensity = 1 / (1 + lightAttenuation * lightDistance);

    vec4 diffuse = modelColor * distanceIntensity *
        clamp(dot(modelNormal, lightDirection), 0, 1);

    vec4 specular = modelColor * distanceIntensity *
        pow(clamp(dot(reflect(-lightDirection, modelNormal), viewDirection), 0, 1), 2);

    vec4 ambient = modelColor;

    outputColor =
        diffuse * diffuseIntensity +
        specular * specularIntensity +
        ambient * ambientIntensity;
}
