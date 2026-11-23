/*
 * Shattered Lab
 * Water with reflections, used while shader effects are on (PostFx).
 * Based on the water reflection in Reforged Pixel Dungeon by DenySilver (GPLv3).
 */

package com.watabou.noosa;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.watabou.glscripts.Script;
import com.watabou.glwrap.Uniform;

public class WaterScript extends NoosaScriptNoLighting {

	//the last one made, so the post effects can reset its camera without creating it
	public static WaterScript instance;

	public Uniform uReflection;
	public Uniform uTime;
	public Uniform uScreenSize;
	public Uniform uTileUV;

	public static WaterScript get() {
		return Script.use( WaterScript.class );
	}

	public WaterScript() {
		super();
		uReflection = uniform( "u_reflection" );
		uTime       = uniform( "u_time" );
		uScreenSize = uniform( "u_screenSize" );
		uTileUV     = uniform( "u_tileUV" );
		instance = this;
	}

	//binds last frame's reflections to unit 1 and sets the wave uniforms; call after get()
	public void prepare() {
		com.badlogic.gdx.graphics.Texture refl = PostFx.reflectionTexture();
		if (refl != null) {
			refl.bind( 1 );
		}
		Gdx.gl.glActiveTexture( GL20.GL_TEXTURE0 );
		uReflection.value1i( 1 );

		Camera camera = Camera.main;
		float zoom = camera != null ? camera.zoom : 1f;
		uTileUV.value1f( (16f * zoom) / (float) Gdx.graphics.getHeight() );
		uTime.value1f( Game.timeTotal % 1000f );
		uScreenSize.value2f( (float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight() );
	}

	@Override
	protected String shader() {
		return SHADER;
	}

	private static final String SHADER =
			"uniform mat4 uCamera;\n" +
			"uniform mat4 uModel;\n" +
			"attribute vec4 aXYZW;\n" +
			"attribute vec2 aUV;\n" +
			"varying vec2 vUV;\n" +
			"void main() {\n" +
			"  gl_Position = uCamera * uModel * aXYZW;\n" +
			"  vUV = aUV;\n" +
			"}\n" +

			//this symbol separates the vertex and fragment shaders (see Script.compile)
			"//\n" +

			"#ifdef GL_ES\n" +
			"  precision mediump float;\n" +
			"#endif\n" +
			"varying vec2 vUV;\n" +
			"uniform sampler2D uTex;\n" +
			"uniform sampler2D u_reflection;\n" +
			"uniform float u_time;\n" +
			"uniform vec2 u_screenSize;\n" +
			"uniform float u_tileUV;\n" +
			"void main() {\n" +
			"  vec4 waterTile = texture2D( uTex, vUV );\n" +
			"  vec2 screenUV = gl_FragCoord.xy / u_screenSize;\n" +
			"  vec2 tileCoord = screenUV / max( u_tileUV, 0.0001 );\n" +
			"  vec2 reflectUV = screenUV;\n" +
			"  reflectUV.x += sin( u_time * 2.0 + tileCoord.y * 2.5 ) * ( u_tileUV * 0.03 );\n" +
			"  reflectUV.y += cos( u_time * 2.5 + tileCoord.x * 2.5 ) * ( u_tileUV * 0.05 );\n" +
			"  vec4 reflectColor = texture2D( u_reflection, reflectUV );\n" +
			"  vec3 tinted = reflectColor.rgb * vec3( 0.65, 0.8, 1.1 );\n" +
			"  gl_FragColor = vec4( mix( waterTile.rgb, tinted, 0.6 * reflectColor.a * waterTile.a ), waterTile.a );\n" +
			"}\n";
}
