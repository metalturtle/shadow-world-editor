
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

void main() {
	vec2 pos = vec2(gl_FragCoord.x,resolution.y - gl_FragCoord.y)/ resolution.xy;
	vec4 DiffuseColor = texture2D(u_texture, vTexCoord0);
	vec4 LightMap = texture2D(u_lightmap,pos);
	// vec4 temp = vColor*vec4(DiffuseColor.rgb*LightMap.rgb,DiffuseColor.a*LightMap.a);
	gl_FragColor = vColor*vec4(vTexCoord0.x,vTexCoord0.y,0,0)*0+vColor*DiffuseColor*LightMap+vec4(pos,0,0)*0;
}