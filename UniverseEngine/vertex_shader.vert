#version 120

varying vec3 position;

const float val = 0.8;

void main(void) {
	vec4 v = vec4(gl_Vertex.xy, gl_Vertex.z / val, gl_Vertex.w);
	gl_Position = gl_ModelViewProjectionMatrix * v;
	position = gl_Vertex.xyz;
}