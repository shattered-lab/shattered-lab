/*
 * Shattered Lab
 * The game scene's side of the optional post effects (PostFx, Settings, Display):
 * dynamic light sources, reflections for the water, glowing objects and screen colour.
 * Nothing here runs while both switches are off.
 *
 * Based on the lighting in Reforged Pixel Dungeon by DenySilver
 * (GPLv3, https://github.com/DenySilver/reforged-pixel-dungeon).
 */

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blizzard;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ConfusionGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Inferno;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfAwareness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.ShadowClone;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalGuardian;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalSpire;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalWisp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM100;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM200;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM300;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Pylon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TormentedSpirit;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.PrismaticImage;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.LightGrid;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CeremonialCandle;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicalFireRoom;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.PostFx;
import com.watabou.noosa.Scene;
import com.watabou.noosa.Visual;

import java.util.ArrayList;

class SceneFx {

	// ------------------------------------------------------------------ world and interface

	//the world is everything drawn through the map camera, the interface is the rest
	static void drawPart( Scene scene, boolean world ){
		synchronized (scene) {
			ArrayList<Gizmo> members = scene.members;
			for (int i = 0; i < members.size(); i++) {
				Gizmo g = members.get( i );
				if (g == null || !g.exists || !g.isVisible()) continue;
				Camera c = g.camera();
				boolean inWorld = c == null || c == Camera.main;
				if (inWorld == world) g.draw();
			}
		}
	}

	// ------------------------------------------------------------------ glow

	private final ArrayList<Gizmo> bloom = new ArrayList<>();

	void addBloom( Gizmo g ){
		if (g == null || !PostFx.shaders) return;
		synchronized (bloom) {
			if (!bloom.contains( g )) bloom.add( g );
		}
	}

	private Gizmo[] bloomSnapshot(){
		synchronized (bloom) {
			for (int i = bloom.size() - 1; i >= 0; i--) {
				Gizmo g = bloom.get( i );
				if (g == null || !g.exists || !g.alive) bloom.remove( i );
			}
			return bloom.toArray( new Gizmo[0] );
		}
	}

	void drawBloom(){
		for (Gizmo g : bloomSnapshot()) {
			if (g.visible) g.draw();
		}
	}

	// ------------------------------------------------------------------ reflections

	//every character sprite again, upside down a little lower and softened, for the water to show
	void drawReflections( Group mobs ){
		if (mobs == null) return;
		synchronized (mobs) {
			for (int i = 0; i < mobs.members.size(); i++) {
				Gizmo g = mobs.members.get( i );
				if (!(g instanceof Visual) || !g.exists || !g.visible) continue;
				Visual v = (Visual) g;
				float sy = v.scale.y, x = v.x, y = v.y, am = v.am;
				float refY = y + DungeonTilemap.SIZE * 1.9f;
				v.scale.y = -sy;
				v.am = am * 0.6f;
				v.x = x - 1; v.y = refY; v.draw();
				v.x = x + 1; v.draw();
				v.x = x; v.y = refY - 1; v.draw();
				v.y = refY + 1; v.draw();
				v.y = refY; v.draw();
				v.scale.y = sy;
				v.x = x;
				v.y = y;
				v.am = am;
			}
		}
	}

	// ------------------------------------------------------------------ light

	private LightGrid grid;
	private Image gridImage;

	void drawLights( Group mobs ){
		Level level = Dungeon.level;
		if (level == null) return;
		if (grid == null || grid.width != level.width() || grid.height != level.height()) {
			if (grid != null) grid.dispose();
			grid = new LightGrid( level.width(), level.height() );
			gridImage = new Image( grid.texture() );
			gridImage.scale.set( DungeonTilemap.SIZE, DungeonTilemap.SIZE );
		}
		grid.clear( 0.05f, 0.07f, 0.12f );
		try {
			collectLights( level, mobs );
		} catch (RuntimeException e) {
			//the actor thread may change the level meanwhile: this frame keeps the lights it got
		}
		grid.upload();
		gridImage.draw();
	}

	void destroy(){
		if (grid != null) grid.dispose();
		grid = null;
		synchronized (bloom) {
			bloom.clear();
		}
	}

	private void collectLights( Level level, Group mobs ){
		int w = level.width();
		boolean[] fov = level.heroFOV;
		float t = Game.timeTotal;

		//the hero's own light
		Hero hero = Dungeon.hero;
		if (hero != null && hero.sprite != null) {
			float hx = (hero.sprite.x + hero.sprite.width / 2f) / DungeonTilemap.SIZE;
			float hy = (hero.sprite.y + hero.sprite.height / 2f) / DungeonTilemap.SIZE;
			grid.addLight( hx, hy, 0xFFD98C, 8.5f + (float)Math.sin( t * 6f ) * 0.25f, 0.01f );
		}

		//torches on prison walls
		if (level instanceof PrisonLevel || level instanceof PrisonBossLevel) {
			for (int cell = 0; cell < level.length(); cell++) {
				if (fov[cell] && level.map[cell] == Terrain.WALL_DECO) {
					grid.addLight( cell % w + 0.5f, cell / w + 0.5f, 0xFFD98C, 4.5f + (float)Math.sin( t * 7f + cell ) * 0.2f, 0.3f );
				}
			}
		}

		//ritual candles
		for (Heap heap : level.heaps.valueList()) {
			if (heap == null || heap.type != Heap.Type.HEAP || heap.pos < 0 || heap.pos >= fov.length || !fov[heap.pos]) continue;
			for (Item item : heap.items) {
				if (item instanceof CeremonialCandle && item.glowing() != null) {
					grid.addLight( heap.pos % w + 0.5f, heap.pos / w + 0.5f, 0x26EA35, 3f + (float)Math.sin( t * 8f + heap.pos ) * 0.15f, 0.45f );
					break;
				}
			}
		}

		//glowing characters, and burning ones
		if (mobs != null) {
			Gizmo[] sprites;
			synchronized (mobs) {
				sprites = mobs.members.toArray( new Gizmo[0] );
			}
			for (Gizmo g : sprites) {
				if (!(g instanceof CharSprite)) continue;
				CharSprite s = (CharSprite) g;
				Char ch = s.ch;
				if (!(ch instanceof Mob) || !ch.isAlive() || ch.pos < 0 || ch.pos >= fov.length || !fov[ch.pos]) continue;
				float mx = (s.x + s.width / 2f) / DungeonTilemap.SIZE;
				float my = (s.y + s.height / 2f) / DungeonTilemap.SIZE;
				if (mobLight( (Mob) ch, s, mx, my, t )) {
					//a white core, so a glowing monster stays bright in its own coloured light
					grid.addLight( mx, my, 0xFFFFFF, 1.6f, 0.5f );
				} else if (ch.buff( Burning.class ) != null) {
					grid.addLight( mx, my, 0xF3682B, 4.5f, 0.8f );
				}
			}
		}

		//light from effects: beams and lightning
		for (Gizmo g : bloomSnapshot()) {
			if (!g.visible || g instanceof BlobEmitter) continue;
			if (g instanceof Beam) {
				segmentLight( (Visual) g, 0xAA87FF, 1f, 0.32f, level );
			} else if (g instanceof Lightning) {
				lightningLight( (Group) g, level );
			}
		}

		//fire, electricity, frost and magic in the air
		for (Blob blob : level.blobs.values().toArray( new Blob[0] )) {
			if (blob == null || blob.cur == null || blob.volume <= 0) continue;
			int color; float radius, decay;
			if (blob instanceof Fire)                                { color = 0xF3682B; radius = 4.5f; decay = 0.8f; }
			else if (blob instanceof Inferno)                        { color = 0xFF3300; radius = 6f;   decay = 0.95f; }
			else if (blob instanceof SacrificialFire)                { color = 0x3C69FB; radius = 5f;   decay = 0.85f; }
			else if (blob instanceof MagicalFireRoom.EternalFire)    { color = 0x33FF4D; radius = 4.5f; decay = 0.8f; }
			else if (blob instanceof Electricity)                    { color = 0xADD5F3; radius = 5f;   decay = 0.9f; }
			else if (blob instanceof WaterOfAwareness)               { color = 0x33CCFF; radius = 3.5f; decay = 0.6f; }
			else if (blob instanceof Freezing || blob instanceof Blizzard) { color = 0x6CB6ED; radius = 4f; decay = 0.5f; }
			else if (blob instanceof ConfusionGas)                   { color = 0xE64DE6; radius = 3.5f; decay = 0.4f; }
			else continue;
			int[] cur = blob.cur;
			int n = Math.min( cur.length, fov.length );
			for (int cell = 0; cell < n; cell++) {
				if (cur[cell] > 0 && fov[cell]) {
					grid.addLight( cell % w + 0.5f, cell / w + 0.5f, color, radius, decay );
				}
			}
		}
	}

	private void segmentLight( Visual v, int color, float radius, float intensity, Level level ){
		float alpha = v.alpha();
		if (alpha <= 0.01f) return;
		float sx = (v.x + v.origin.x) / DungeonTilemap.SIZE, sy = (v.y + v.origin.y) / DungeonTilemap.SIZE;
		double rad = Math.toRadians( v.angle );
		float len = v.width() / DungeonTilemap.SIZE;
		float dx = (float)Math.cos( rad ) * len, dy = (float)Math.sin( rad ) * len;
		int steps = Math.max( 1, Math.round( (float)Math.sqrt( dx * dx + dy * dy ) ) );
		float k = intensity * alpha * alpha;
		boolean[] fov = level.heroFOV;
		for (int s = 0; s <= steps; s++) {
			float lx = sx + dx * s / steps, ly = sy + dy * s / steps;
			int cell = (int)lx + (int)ly * level.width();
			if (cell >= 0 && cell < fov.length && fov[cell]) grid.addLight( lx, ly, color, radius, k );
		}
	}

	private void lightningLight( Group group, Level level ){
		Gizmo[] parts = group.members.toArray( new Gizmo[0] );
		for (Gizmo m : parts) {
			if (m == null || !m.exists || !m.alive || !m.visible) continue;
			if (m instanceof Group) {
				for (Gizmo arc : ((Group) m).members.toArray( new Gizmo[0] )) {
					if (arc instanceof Visual && arc.exists && arc.alive && arc.visible) {
						segmentLight( (Visual) arc, 0xADD5F3, 1f, 0.3f, level );
						break;
					}
				}
			} else if (m instanceof Visual) {
				segmentLight( (Visual) m, 0xADD5F3, 2f, 0.3f, level );
			}
		}
	}

	private boolean mobLight( Mob mob, CharSprite s, float x, float y, float t ){
		if (mob instanceof Elemental.NewbornFireElemental)   { grid.addLight( x, y, 0x3BFD62, 4.2f, 0.6f );  return true; }
		if (mob instanceof Elemental.FireElemental)          { grid.addLight( x, y, 0xFF7319, 4f, 0.55f );   return true; }
		if (mob instanceof Elemental.FrostElemental)         { grid.addLight( x, y, 0x4DCCFF, 3.5f, 0.5f );  return true; }
		if (mob instanceof Elemental.ShockElemental)         { grid.addLight( x, y, 0xADD5F3, 4f, 0.55f );   return true; }
		if (mob instanceof Elemental.ChaosElemental)         { rainbow( x, y, 4.2f, 0.55f, t ); return true; }
		if (mob instanceof WandOfWarding.Ward)               { grid.addLight( x, y, 0xE214F8, 3.5f, 0.5f );  return true; }
		if (mob instanceof Ghost || mob instanceof DriedRose.GhostHero) { grid.addLight( x, y, 0xD9EBFF, 3.5f, 0.45f ); return true; }
		if (mob instanceof TormentedSpirit)                  { grid.addLight( x, y, 0xFF2626, 3.2f, 0.5f );  return true; }
		if (mob instanceof Wraith || mob instanceof ShadowClone.ShadowAlly) { grid.addLight( x, y, 0x7319BF, 3f, 0.45f ); return true; }
		if (mob instanceof PrismaticImage)                   { rainbow( x, y, 3.5f, 0.5f, t + 1.5f ); return true; }
		if (mob instanceof SpiritHawk.HawkAlly)              { grid.addLight( x, y, 0xFFF2BF, 4f, 0.55f );   return true; }
		if (mob instanceof DM100 || mob instanceof DM200)    { grid.addLight( x, y, 0xFFF2BF, 3.4f, 0.45f ); return true; }
		if (mob instanceof DM300 || mob instanceof Pylon)    { grid.addLight( x, y, 0xFFF2BF, 4.5f, 0.55f ); return true; }
		if (mob instanceof CrystalWisp || mob instanceof CrystalGuardian || mob instanceof CrystalSpire) {
			String variant = s.getClass().getSimpleName();
			int color = variant.equals( "Green" ) ? 0x33FF73 : variant.equals( "Red" ) ? 0xFF7A33 : 0x33CCFF;
			boolean spire = mob instanceof CrystalSpire;
			grid.addLight( x, y, color, spire ? 5.5f : 3.2f, spire ? 0.65f : 0.45f );
			return true;
		}
		return false;
	}

	private void rainbow( float x, float y, float radius, float intensity, float phase ){
		float a = phase * 2.5f;
		grid.addLight( x, y, 0.5f + 0.5f * (float)Math.sin( a ), 0.5f + 0.5f * (float)Math.sin( a + 2.094f ),
				0.5f + 0.5f * (float)Math.sin( a + 4.188f ), radius, intensity );
	}

	// ------------------------------------------------------------------ screen colour

	void updateScreen( float delta ){
		PostFx.updateFlashes( delta );
		Hero hero = Dungeon.hero;
		if (hero == null || !hero.isAlive() || hero.HT <= 0) return;

		float lerp = Math.min( 1f, delta * 3.5f );
		float hp = (float) hero.HP / hero.HT;

		float noise = hp < 0.3f ? (0.3f - hp) / 0.3f * 0.25f : 0f;

		boolean invisible = hero.invisible > 0 || hero.buff( Invisibility.class ) != null;
		boolean mindVision = hero.buff( MindVision.class ) != null;
		//shader effects make colours a little richer; mind vision a bit more, with a slight lens
		//(much softer than the original 2.2 and 0.04: in this mod it can last the whole run)
		float saturation = 1.1f, tint = 0f, fisheye = mindVision ? 0.015f : 0f;
		if (invisible) {
			saturation = 0.5f;
			tint = 0.5f;
			PostFx.tintR = 0.3f; PostFx.tintG = 0.6f; PostFx.tintB = 1.2f;
		} else if (mindVision) {
			saturation = 1.4f;
		} else if (hp < 0.2f) {
			saturation = 1.1f * hp / 0.2f;
		}

		boolean poisoned = hero.buff( Poison.class ) != null;
		float chromatic = poisoned ? 0.035f : 0f;
		AscensionChallenge curse = hero.buff( AscensionChallenge.class );
		if (curse != null) chromatic += curse.shdStacks() / 8f * 0.08f;

		PostFx.noise += (noise - PostFx.noise) * lerp;
		PostFx.saturation += (saturation - PostFx.saturation) * lerp;
		PostFx.tintAmount += (tint - PostFx.tintAmount) * lerp;
		PostFx.fisheye += (fisheye - PostFx.fisheye) * lerp;
		PostFx.chromatic += (chromatic - PostFx.chromatic) * lerp;
		PostFx.distortion += ((poisoned ? 0.002f : 0f) - PostFx.distortion) * lerp;
		PostFx.vignette += (0f - PostFx.vignette) * lerp;
	}
}
