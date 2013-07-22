#version 330

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;

out vec3 modelPosition;
out vec3 modelNormal;
out vec3 viewDirection;

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
