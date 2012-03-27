#version 120

varying vec3 position;

const float val = 5.0;

void main(void) {
	gl_FragColor = vec4(position.x / val, position.y / val, position.z / val, 1.0);
}