#version 460 core

layout(location = 0) in vec2 pos;
layout(location = 1) in vec2 texcoords;
layout(location = 2) in mat4 matrix;

out vec2 TC;

void main(){
    TC = texcoords;
    gl_Position = matrix * vec4(pos, 1.0f, 1.0f);
}