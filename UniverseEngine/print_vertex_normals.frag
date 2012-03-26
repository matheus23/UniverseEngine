#version 120

varying vec3 vertex_normal;

void main(void) {
	gl_FragColor = vec4(vertex_normal, 1.0);
}