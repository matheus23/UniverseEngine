#version 120
 
uniform sampler2D texture;
uniform vec2 shift;
 
const int gaussRadius = 11;
const float gaussFilter[gaussRadius] = float[gaussRadius](
	0.0402, 0.0623, 0.0877, 0.1120, 0.1297, 0.1362, 0.1297, 0.1120, 0.0877, 0.0623, 0.0402
);
 
void main() {
	vec2 texCoord = gl_TexCoord[0].xy - float(int(gaussRadius/2)) * shift;
	vec3 color = vec3(0.0, 0.0, 0.0); 
	for (int i=0; i<gaussRadius; ++i) { 
		color += gaussFilter[i] * texture2D(texture, texCoord).xyz;
		texCoord += shift;
	}
	gl_FragColor = vec4(color,1.0);
}