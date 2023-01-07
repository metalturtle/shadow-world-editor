
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
uniform sampler2D u_normals;   //normal map
uniform sampler2D u_lightmap;

//values used for shading algorithm...
uniform vec2 Resolution;         //resolution of screen
//uniform vec3 LightPos[8];           //light position, normalized
//uniform vec2 CameraPosition;
uniform vec3 LightPos[8];
uniform float LightActive[8];
//uniform LOWP vec4 LightColor[8];
uniform LOWP vec4 LightColor;    //light RGBA -- alpha is intensity
uniform LOWP vec4 AmbientColor;  //ambient RGBA -- alpha is intensity 
uniform vec3 Falloff;            //attenuation coefficients

float PHI = 1.61803398874989484820459;  // Φ = Golden Ratio   

float random(vec2 p){return fract(cos(dot(p,vec2(23.14069263277926,2.665144142690225)))*12345.6789);}

void main() {
	vec4 DiffuseColor = texture2D(u_texture, vTexCoord0);
	vec3 NormalMap = texture2D(u_normals, vTexCoord0).rgb;
	vec4 LightMap = texture2D(u_lightmap,vTexCoord0);
	int i = 0;
	vec3 LightDir=vec3(0,0,0),N,L,Diffuse,Ambient,Intensity,FinalColor=vec3(0);
	Ambient = AmbientColor.rgb * AmbientColor.a;
	
	float D=0,Attenuation,gray,lightval;
	
	//vec3 ambientvec = vec3(random(vec2(CameraPosition.x+gl_FragCoord.x,CameraPosition.y+gl_FragCoord.y)),random(vec2(CameraPosition.x+gl_FragCoord.x,CameraPosition.y+gl_FragCoord.y)),1);
	//ambientvec = normalize(ambientvec);
	//vec3 AmbientLight = (AmbientColor.rgb * AmbientColor.a)* max(dot(N, ambientvec), 0.0);
	//bvec3 gt = EqualTo (LightMap, vec3(0));
	
//	if(LightMap == vec3(0))
//	{
//		gl_FragColor = vColor*vec4(DiffuseColor.rgb*AmbientLight.rgb, 1.0);
//		return;
//	}

	for (i=0;i<8;i++)
	{
		if (LightActive[i] == 0)
			continue;
		
		LightDir = vec3(LightPos[i].xy - (gl_FragCoord.xy / Resolution.xy), LightPos[i].z);
		LightDir.x *= Resolution.x / Resolution.y;
		D = length(LightDir);
		L = normalize(LightDir);
		N = normalize(NormalMap * 2.0 - 1.0);
		
		Diffuse = (LightColor.rgb * LightColor.a) * max(dot(N, L), 0.0);
		
		Attenuation = 1.0 / ( Falloff.x + (Falloff.y*D) + (Falloff.z*D*D) );
		Intensity = Diffuse*Attenuation;
		
		FinalColor += DiffuseColor.rgb*Intensity;

	}

	FinalColor = FinalColor*LightMap.rgb;
	//vec3 ambientvec = vec3(0,0,1);
	
	//FinalColor = FinalColor.rgb;
	//vec3 FinalColor =0.1*DiffuseColor.rgb+DiffuseColor.rgb*ShadowMap.rgb*Intensity;
	//FinalColor += 0.5*DiffuseColor.rgb;
	gl_FragColor = vColor*vec4(FinalColor, 1.0);
}
