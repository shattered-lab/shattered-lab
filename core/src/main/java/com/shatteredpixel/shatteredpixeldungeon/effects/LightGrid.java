/*
 * Shattered Lab
 * One light value per map cell for the dynamic light (Settings, Display), uploaded as a small
 * texture that the game scene stretches over the map.
 * Based on LightGrid in Reforged Pixel Dungeon by DenySilver (GPLv3).
 */

package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class LightGrid {

	public final int width, height;
	private final float[] r, g, b;
	//kept in the texture cache, so it comes back by itself after the GL context is lost
	private final SmartTexture texture;

	public LightGrid( int width, int height ){
		this.width = width;
		this.height = height;
		r = new float[width * height];
		g = new float[width * height];
		b = new float[width * height];
		texture = TextureCache.create( this, width, height );
	}

	public SmartTexture texture(){
		return texture;
	}

	public void dispose(){
		TextureCache.remove( this );
	}

	public void clear( float ar, float ag, float ab ){
		Arrays.fill( r, ar );
		Arrays.fill( g, ag );
		Arrays.fill( b, ab );
	}

	public void addLight( float x, float y, int rgb, float radius, float decay ){
		addLight( x, y, ((rgb >> 16) & 0xFF) / 255f, ((rgb >> 8) & 0xFF) / 255f, (rgb & 0xFF) / 255f, radius, decay );
	}

	//a light at (x, y) in cells, fading with distance and stopped by walls
	public void addLight( float x, float y, float cr, float cg, float cb, float radius, float decay ){
		Level level = Dungeon.level;
		if (level == null || level.width() != width || level.height() != height) return;
		int minX = Math.max( 0, (int)(x - radius) ), maxX = Math.min( width - 1, (int)(x + radius) );
		int minY = Math.max( 0, (int)(y - radius) ), maxY = Math.min( height - 1, (int)(y + radius) );
		float radSq = radius * radius;
		float cutoff = 1f / (1f + decay * radSq);
		for (int cx = minX; cx <= maxX; cx++) {
			for (int cy = minY; cy <= maxY; cy++) {
				float dx = cx - x, dy = cy - y;
				float distSq = dx * dx + dy * dy;
				if (distSq > radSq || !lineOfSight( (int)x, (int)y, cx, cy, level )) continue;
				float att = 1f / (1f + decay * distSq);
				float k = Math.max( 0f, (att - cutoff) / (1f - cutoff) );
				int i = cx + cy * width;
				r[i] += cr * k;
				g[i] += cg * k;
				b[i] += cb * k;
			}
		}
	}

	private boolean lineOfSight( int x0, int y0, int x1, int y1, Level level ){
		boolean[] blocking = level.losBlocking;
		int dx = Math.abs( x1 - x0 ), dy = Math.abs( y1 - y0 );
		int sx = x0 < x1 ? 1 : -1, sy = y0 < y1 ? 1 : -1;
		int err = dx - dy;
		int x = x0, y = y0;
		while (true) {
			if (x == x1 && y == y1) return true;
			if (x < 0 || x >= width || y < 0 || y >= height) return false;
			if ((x != x0 || y != y0) && blocking[x + y * width]) return false;
			int e2 = 2 * err;
			if (e2 > -dy) { err -= dy; x += sx; }
			if (e2 < dx) { err += dx; y += sy; }
		}
	}

	//writes the light into the texture, each cell scaled down so its brightest channel is at most 1
	public void upload(){
		ByteBuffer px = texture.bitmap.getPixels();
		px.position( 0 );
		for (int i = 0; i < r.length; i++) {
			float cr = r[i], cg = g[i], cb = b[i];
			float max = Math.max( cr, Math.max( cg, cb ) );
			if (max > 1f) { cr /= max; cg /= max; cb /= max; }
			px.put( (byte)(int)(cr * 255f) );
			px.put( (byte)(int)(cg * 255f) );
			px.put( (byte)(int)(cb * 255f) );
			px.put( (byte)255 );
		}
		px.position( 0 );
		texture.bitmap( texture.bitmap );
	}
}
