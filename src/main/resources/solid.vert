#version 330

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;

smooth out vec3 modelPosition;
smooth out vec3 modelNormal;
smooth out vec3 viewDirection;

uniform mat4 modelMatrix;
uniform mat4 cameraMatrix;
uniform mat4 projectionMatrix;

void main() {
    modelPosition = vec3(modelMatrix * vec4(position, 1));
    modelNormal = mat3(modelMatrix) * normal;
    viewDirection = normalize(vec3(inverse(cameraMatrix) * vec4(0, 0, 0, 1)) - modelPosition);

    if (dot(modelNormal, viewDirection) < 0) {
        modelNormal = -modelNormal;
    }

    gl_Position = projectionMatrix * cameraMatrix * vec4(modelPosition, 1);
}
