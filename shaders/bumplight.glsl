
#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif
//attributes from vertex shader
varying LOWP vec4 vColor;
varying vec2 vTexCoord0;

//our texture samplers
uniform sampler2D u_light;   //diffuse map
uniform sampler2D u_normals;   //normal map


void main() {
	
	vec4 LightMap = texture2D(u_light,vTexCoord0);
	vec3 NormalMap = texture2D(u_normals,vTexCoord0).rgb;
	
	vec3 N = normalize(NormalMap * 2.0 - 1.0);
	vec3 L = normalize(LightMap.rgb/255.0);
	vec3 Diffuse = (LightMap.rgb * LightMap.a) * max(N*L, 0.0)+0.5;
	vec3 FinalColor = DiffuseColor.rgb * Diffuse;
	gl_FragColor = vColor * vec4(FinalColor, DiffuseColor.a);
}
