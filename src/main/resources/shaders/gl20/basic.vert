#version 120

attribute vec3 position;
attribute vec3 normal;

varying vec3 modelPosition;
varying vec3 modelNormal;
varying vec3 viewDirection;

uniform mat4 modelMatrix;
uniform mat4 cameraMatrix;
uniform mat4 projectionMatrix;

void main() {
    modelPosition = vec3(modelMatrix * vec4(position, 1));
    modelNormal = mat3(modelMatrix) * normal;
    vec3 cameraPosition = -cameraMatrix[3].xyz * mat3(cameraMatrix);
    viewDirection = normalize(cameraPosition - modelPosition);

    if (dot(modelNormal, viewDirection) < 0) {
        modelNormal = -modelNormal;
    }

    gl_Position = projectionMatrix * cameraMatrix * vec4(modelPosition, 1);
}
