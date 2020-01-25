#version 460 core

layout(location = 0) in vec3 a_Position;
layout(location = 1) in vec4 a_Color;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

out vec2 v_Test;
out vec4 v_Color;

void main()
{
    v_Test = a_Position.xy;
    v_Color = a_Color;
    gl_Position = projection * view * model * vec4(a_Position, 1.0f);
}