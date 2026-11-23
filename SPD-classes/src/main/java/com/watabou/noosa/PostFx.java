/*
 * Shattered Lab
 * Optional post effects for the game scene: dynamic light and shader effects
 * (glow, water reflections, screen effects). Both are off by default.
 *
 * Based on the lighting and post processing in Reforged Pixel Dungeon by DenySilver
 * (GPLv3, https://github.com/DenySilver/reforged-pixel-dungeon), reworked so that nothing
 * is created while both switches are off, and any GL error turns the effects off
 * instead of crashing the game.
 */

package com.watabou.noosa;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.watabou.glscripts.Script;
import com.watabou.glwrap.Blending;
import com.watabou.glwrap.Quad;

public class PostFx {

	//the switches, set from the settings at boot and when changed
	public static volatile boolean light = false;
	public static volatile boolean shaders = false;

	//set after an error: the effects stay off until a switch is changed again
	private static boolean failed = false;

	//frames drawn with the effects (the test stand checks it)
	public static int frames = 0;

	public static void light( boolean value ){
		light = value;
		failed = false;
		if (!light && !shaders) release();
	}

	public static void shaders( boolean value ){
		shaders = value;
		failed = false;
		if (!light && !shaders) release();
	}

	public static boolean active(){
		return (light || shaders) && !failed;
	}

	//shader effects are on and ready to draw (the water reflection asks this every frame)
	public static boolean shadersReady(){
		return shaders && !failed && reflection != null;
	}

	//screen colour, set by the game scene every frame while shader effects are on
	public static float saturation = 1f;
	public static float tintR = 1f, tintG = 1f, tintB = 1f, tintAmount = 0f;
	public static float noise = 0f;
	public static float vignette = 0f, vignetteR = 0f;
	public static float chromatic = 0f;
	public static float distortion = 0f;
	public static float fisheye = 0f;
	public static float bloom = 1.5f;

	//short flashes, they fade out by themselves
	public static float starvingFlash = 0f;
	public static float teleportFlash = 0f;

	public static void triggerStarvingFlash(){
		starvingFlash = 0.85f;
	}

	public static void triggerTeleport(){
		teleportFlash = 0.16f;
	}

	public static void updateFlashes( float delta ){
		if (starvingFlash > 0f) starvingFlash = Math.max( 0f, starvingFlash - delta * 2f );
		if (teleportFlash > 0f) teleportFlash = Math.max( 0f, teleportFlash - delta * 0.18f );
	}

	// ------------------------------------------------------------------ GL objects

	private static FrameBuffer world, base, lightMap, reflection, bloomMask, ping, pong;
	private static ShaderProgram lightmapShader, thresholdShader, blurShader, finalShader;
	private static SpriteBatch batch;
	private static int builtW = -1, builtH = -1;
	private static boolean builtShaders = false;

	//the reflection texture for the water shader, null while shader effects are off
	public static Texture reflectionTexture(){
		return shadersReady() ? reflection.getColorBufferTexture() : null;
	}

	//drops every GL object: on a resize, when both switches go off, and after an error
	public static synchronized void release(){
		boolean had = batch != null || world != null || lightmapShader != null;
		FrameBuffer[] all = { world, base, lightMap, reflection, bloomMask, ping, pong };
		for (FrameBuffer f : all) {
			if (f != null) try { f.dispose(); } catch (Exception ignored) {}
		}
		world = base = lightMap = reflection = bloomMask = ping = pong = null;
		ShaderProgram[] progs = { lightmapShader, thresholdShader, blurShader, finalShader };
		for (ShaderProgram p : progs) {
			if (p != null) try { p.dispose(); } catch (Exception ignored) {}
		}
		lightmapShader = thresholdShader = blurShader = finalShader = null;
		if (batch != null) try { batch.dispose(); } catch (Exception ignored) {}
		batch = null;
		builtW = builtH = -1;
		builtShaders = false;
		//disposing unbinds libGDX's buffers and program, and with them noosa's quad indices:
		//without these, the next normal frame draws from no index buffer and the GPU driver crashes
		if (had) {
			try { restoreNoosa(); } catch (Throwable ignored) {}
		}
	}

	private static FrameBuffer fbo( int w, int h ){
		FrameBuffer f = new FrameBuffer( Pixmap.Format.RGBA8888, Math.max( 1, w ), Math.max( 1, h ), false );
		f.getColorBufferTexture().setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );
		f.getColorBufferTexture().setWrap( Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge );
		return f;
	}

	private static ShaderProgram shader( String frag ){
		ShaderProgram p = new ShaderProgram( Gdx.files.internal( "shaders/post.vert" ), Gdx.files.internal( "shaders/" + frag ) );
		if (!p.isCompiled()) {
			String log = p.getLog();
			p.dispose();
			throw new RuntimeException( "post effect shader " + frag + " failed: " + log );
		}
		return p;
	}

	private static void build( int w, int h ){
		if (w != builtW || h != builtH || shaders != builtShaders) {
			release();
			ShaderProgram.pedantic = false;
			batch = new SpriteBatch( 4 );
			batch.disableBlending();
			batch.getProjectionMatrix().setToOrtho2D( 0, 0, w, h );
			lightmapShader = shader( "lightmap.frag" );
			world = fbo( w, h );
			lightMap = fbo( w / 2, h / 2 );
			if (shaders) {
				thresholdShader = shader( "threshold.frag" );
				blurShader = shader( "blur.frag" );
				finalShader = shader( "final.frag" );
				base = fbo( w, h );
				reflection = fbo( w / 2, h / 2 );
				bloomMask = fbo( w / 2, h / 2 );
				ping = fbo( w / 2, h / 2 );
				pong = fbo( w / 2, h / 2 );
			}
			builtW = w;
			builtH = h;
			builtShaders = shaders;
			//creating framebuffers binds their textures and buffers behind noosa's back
			restoreNoosa();
		}
	}

	// ------------------------------------------------------------------ drawing

	//noosa caches its program, camera and texture: forget them after libGDX drew something
	private static void restoreNoosa(){
		if (Gdx.gl30 != null) Gdx.gl30.glBindVertexArray( 0 );
		Gdx.gl.glBindBuffer( GL20.GL_ARRAY_BUFFER, 0 );
		Quad.bindIndices();
		Gdx.gl.glActiveTexture( GL20.GL_TEXTURE0 );
		com.watabou.glwrap.Texture.clear();
		Script.unuse();
		Blending.useDefault();
		resetCameras();
	}

	private static void resetCameras(){
		NoosaScript.get().resetCamera();
		NoosaScriptNoLighting.get().resetCamera();
		if (WaterScript.instance != null) WaterScript.instance.resetCamera();
		Script.unuse();
		Gdx.gl.glDisable( GL20.GL_SCISSOR_TEST );
	}

	private static void begin( FrameBuffer f, float r, float g, float b, float a ){
		f.begin();
		resetCameras();
		Gdx.gl.glClearColor( r, g, b, a );
		Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );
	}

	private static void end( FrameBuffer f ){
		f.end();
		Gdx.gl.glClearColor( 0, 0, 0, 1 );
	}

	//draws a texture over the whole target through a shader (null: plain copy)
	private static void blit( Texture tex, ShaderProgram p, int w, int h ){
		batch.setShader( p );
		batch.begin();
		batch.draw( tex, 0, 0, w, h, 0, 0, 1, 1 );
		batch.end();
	}

	private static void blurUniforms( boolean horizontal, int w, int h ){
		float[] weights = { 0.05f, 0.09f, 0.12f, 0.15f, 0.18f, 0.15f, 0.12f, 0.09f, 0.05f };
		float[] offsets = new float[18];
		float texel = 2.5f / (horizontal ? w : h);
		for (int i = 0; i < 9; i++) {
			offsets[i*2]   = horizontal ? (i - 4) * texel : 0;
			offsets[i*2+1] = horizontal ? 0 : (i - 4) * texel;
		}
		blurShader.setUniform1fv( "u_weights", weights, 0, 9 );
		blurShader.setUniform2fv( "u_offsets", offsets, 0, 18 );
	}

	/**
	 * Draws the scene with the effects. Returns false (having drawn nothing) when the effects
	 * cannot run, so the caller draws the scene as usual.
	 */
	public static synchronized boolean render( Scene scene ){
		if (!active()) return false;
		int w = Gdx.graphics.getWidth(), h = Gdx.graphics.getHeight();
		if (w <= 0 || h <= 0) return false;

		try {
			build( w, h );

			//reflections first: the water in the world pass samples them
			if (shaders) {
				begin( reflection, 0, 0, 0, 0 );
				scene.drawReflections();
				end( reflection );
			}

			//the world, as the game draws it
			begin( world, 0, 0, 0, 1 );
			scene.drawWorld();
			end( world );

			if (light) {
				begin( lightMap, 0.05f, 0.07f, 0.12f, 1f );
				Gdx.gl.glEnable( GL20.GL_BLEND );
				Gdx.gl.glBlendFunc( GL20.GL_ONE, GL20.GL_ONE );
				scene.drawLightSources();
				Blending.useDefault();
				end( lightMap );
			}

			if (shaders) {
				begin( bloomMask, 0, 0, 0, 1 );
				scene.drawBloomObjects();
				end( bloomMask );
			}

			restoreNoosa();

			if (light) {
				//world times light, straight to the screen or into the base for the screen pass
				if (shaders) begin( base, 0, 0, 0, 1 );
				else {
					Gdx.gl.glClearColor( 0, 0, 0, 1 );
					Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );
				}
				lightMap.getColorBufferTexture().bind( 1 );
				Gdx.gl.glActiveTexture( GL20.GL_TEXTURE0 );
				batch.setShader( lightmapShader );
				batch.begin();
				lightmapShader.setUniformi( "u_lightmap", 1 );
				batch.draw( world.getColorBufferTexture(), 0, 0, w, h, 0, 0, 1, 1 );
				batch.end();
				if (shaders) end( base );
			}

			if (shaders) {
				FrameBuffer src = light ? base : world;

				//glow: the bright parts of glowing objects, blurred
				ping.begin();
				blit( bloomMask.getColorBufferTexture(), thresholdShader, w, h );
				ping.end();

				pong.begin();
				batch.setShader( blurShader );
				batch.begin();
				blurUniforms( true, w, h );
				batch.draw( ping.getColorBufferTexture(), 0, 0, w, h, 0, 0, 1, 1 );
				batch.end();
				pong.end();

				ping.begin();
				batch.setShader( blurShader );
				batch.begin();
				blurUniforms( false, w, h );
				batch.draw( pong.getColorBufferTexture(), 0, 0, w, h, 0, 0, 1, 1 );
				batch.end();
				ping.end();

				//the screen pass: world (lit), glow and screen colour
				Gdx.gl.glClearColor( 0, 0, 0, 1 );
				Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );
				ping.getColorBufferTexture().bind( 1 );
				Gdx.gl.glActiveTexture( GL20.GL_TEXTURE0 );
				batch.setShader( finalShader );
				batch.begin();
				finalShader.setUniformi( "u_bloom", 1 );
				finalShader.setUniformf( "u_bloomIntensity", bloom );
				finalShader.setUniformf( "u_fisheye", fisheye );
				finalShader.setUniformf( "u_distortion", distortion );
				finalShader.setUniformf( "u_time", Game.timeTotal % 1000f );
				finalShader.setUniformf( "u_chromatic", chromatic + teleportFlash );
				finalShader.setUniformf( "u_saturation", saturation );
				finalShader.setUniformf( "u_tint", tintR, tintG, tintB );
				finalShader.setUniformf( "u_tintAmount", tintAmount );
				finalShader.setUniformf( "u_noise", noise );
				finalShader.setUniformf( "u_seed", Game.timeTotal % 100f );
				finalShader.setUniformf( "u_vignette", Math.max( vignette, starvingFlash ) );
				finalShader.setUniformf( "u_vignetteColor", vignette > 0f ? vignetteR : 0f, 0f, 0f );
				batch.draw( src.getColorBufferTexture(), 0, 0, w, h, 0, 0, 1, 1 );
				batch.end();
			}

			restoreNoosa();
			scene.drawUI();
			Gdx.gl.glDisable( GL20.GL_SCISSOR_TEST );
			frames++;
			return true;

		} catch (Throwable t) {
			//never crash for an optional effect: turn the effects off and draw as usual
			failed = true;
			try { FrameBuffer.unbind(); } catch (Throwable ignored) {}
			try { Gdx.gl.glViewport( 0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight() ); } catch (Throwable ignored) {}
			try { restoreNoosa(); } catch (Throwable ignored) {}
			release();
			Game.reportException( new RuntimeException( "post effects turned off after an error", t ) );
			Gdx.gl.glClearColor( 0, 0, 0, 1 );
			Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );
			scene.draw();
			return true;
		}
	}
}
