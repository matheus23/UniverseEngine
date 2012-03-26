#version 120

varying vec3 vertex_normal;

void main() {
	vertex_normal = gl_Normal;
	gl_Position = ftransform();
}