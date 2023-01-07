
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
uniform sampler2D u_texture;   //diffuse map
uniform sampler2D u_lightmap;
uniform vec2 resolution;
uniform float transparent;

void main() {
	vec2 pos = vec2(gl_FragCoord.x,resolution.y - gl_FragCoord.y)/ resolution.xy;
	vec4 DiffuseColor = texture2D(u_texture, vTexCoord0);
	vec4 LightMap = texture2D(u_lightmap,pos);
	vec3 DefaultColor = vec3(0,0,0);
	float FinalAlpha = DiffuseColor.a;
	if (transparent > 0) {
		DefaultColor = vec3(0.3,0.3,0.3);
		FinalAlpha *= 0.3;
	}
	vec3 FinalColor = DiffuseColor.rgb*max(LightMap.rgb,DefaultColor);
	gl_FragColor = vColor*vec4(FinalColor,DiffuseColor.a);
}