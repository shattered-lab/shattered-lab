/*
 * Shattered Lab
 * Switches for the optional effects, all off by default:
 * 3D sound and echo (Settings, Audio), dynamic light and shader effects (Settings, Display).
 * Sound: audio/SpatialSound and audio/RoomAcoustics from Warped Pixel Dungeon by gabriwar.
 * Light and shaders: com.watabou.noosa.PostFx and scenes/SceneFx, after Reforged Pixel Dungeon by DenySilver.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks;

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.audio.RoomAcoustics;
import com.shatteredpixel.shatteredpixeldungeon.audio.SpatialSound;
import com.watabou.noosa.PostFx;

public class HackFx {

	private static final String KEY_SOUND   = "shd_fx_sound";
	private static final String KEY_LIGHT   = "shd_fx_light";
	private static final String KEY_SHADERS = "shd_fx_shaders";

	public static boolean sound(){
		return SPDSettings.getBoolean( KEY_SOUND, false );
	}

	public static boolean light(){
		return SPDSettings.getBoolean( KEY_LIGHT, false );
	}

	public static boolean shaders(){
		return SPDSettings.getBoolean( KEY_SHADERS, false );
	}

	//at boot: only what is switched on gets loaded or created
	public static void apply(){
		if (sound()) {
			SpatialSound.on = true;
			RoomAcoustics.enable( true );
		}
		PostFx.light( light() );
		PostFx.shaders( shaders() );
	}

	public static void sound( boolean value ){
		SPDSettings.put( KEY_SOUND, value );
		SpatialSound.on = value;
		RoomAcoustics.enable( value );
		if (value) RoomAcoustics.preview();
	}

	public static void light( boolean value ){
		SPDSettings.put( KEY_LIGHT, value );
		PostFx.light( value );
	}

	public static void shaders( boolean value ){
		SPDSettings.put( KEY_SHADERS, value );
		PostFx.shaders( value );
	}
}
