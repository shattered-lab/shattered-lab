package com.shatteredpixel.shatteredpixeldungeon.audio;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;

import java.util.Arrays;
import java.util.Locale;

public final class Earshot {

    private Earshot(){}

    public enum Space { TIGHT, ROOM, LARGE, HUGE }
    public enum Tail { NONE, ROOM, HALL, CAVERN }
    public enum Relation { CLEAR, DOOR, WALL }

    public static final int RADIUS = 16;
    static final int SIDE = 2 * RADIUS + 1, CELLS = SIDE * SIDE;
    static final double MAX_AGE = 0.5;
    static final int DETOUR = 6;
    static final int VAST = 700;
    static final float TRIM_DB = 0f;

    static final byte OPEN_CELL = 0, DOOR_CELL = 1, WALL_CELL = 2;
    static final byte[] CLASS = new byte[256];
    static final float[] ABSORB = new float[256];
    static final boolean[] WATER = new boolean[256];
    static {
        for (int t : new int[]{ Terrain.WALL, Terrain.WALL_DECO, Terrain.SECRET_DOOR,
                Terrain.BOOKSHELF}) CLASS[t] = WALL_CELL;
        for (int t : new int[]{ Terrain.DOOR, Terrain.LOCKED_DOOR, Terrain.HERO_LKD_DR, Terrain.CRYSTAL_DOOR,
                Terrain.BARRICADE, Terrain.LOCKED_EXIT }) CLASS[t] = DOOR_CELL;

        for (int t : new int[]{ Terrain.HIGH_GRASS, Terrain.FURROWED_GRASS }) ABSORB[t] = 0.7f;
        ABSORB[Terrain.CHASM] = 0.5f;
        ABSORB[Terrain.GRASS] = 0.35f;
        WATER[Terrain.WATER] = true;
    }

    interface Clock {
        double now();
    }

    static Clock clock = () -> System.nanoTime() / 1e9;

    private static final Object LOCK = new Object();
    private static volatile Snapshot last;

    private static final int[] terrain = new int[CELLS];
    private static final byte[] kind = new byte[CELLS];
    private static final int[] dist = new int[CELLS];
    private static final int[] queue = new int[CELLS];
    private static final int[] doorsIn = new int[CELLS], doorsOut = new int[CELLS];
    private static final int[] spaceDoors = new int[CELLS], doorDist = new int[CELLS];
    private static final short[] doorVia = new short[CELLS];

    private static final int[] DX = { -1, 0, 1, -1, 1, -1, 0, 1 };
    private static final int[] DY = { -1, -1, -1, 0, 0, 1, 1, 1 };
    private static final int[] SIDES = { 1, 3, 4, 6 };
    private static final byte UNREACHED = (byte) (3 << 6);

    public static Snapshot of( Level level, int hero ){
        if (level == null || hero < 0 || hero >= level.length()) return null;
        double now = clock.now();
        Snapshot s = last;
        if (s != null && s.fresh( level, hero, now )) return s;
        synchronized (LOCK){
            s = last;
            if (s != null && s.fresh( level, hero, now )) return s;
            s = work( level, hero, now );
            last = s;
            return s;
        }
    }

    private static boolean isRockLevel( Level level ){
        String t = level.tilesTex();
        if (t == null) return false;
        return t.equals( Assets.Environment.TILES_CAVES )
                || t.equals( Assets.Environment.TILES_CAVES_CRYSTAL )
                || t.equals( Assets.Environment.TILES_CAVES_GNOLL );
    }

    private static float colour( Level level ){
        String t = level.tilesTex();
        if (t == null) return 0f;
        if (t.equals( Assets.Environment.TILES_SEWERS )
                || t.equals( Assets.Environment.TILES_CITY )
                || t.equals( Assets.Environment.TILES_HALLS )
                || isRockLevel( level )){
            return 1f;
        }
        return 0f;
    }

    private static byte classOf( int t ){
        return t >= 0 && t < 256 ? CLASS[t] : WALL_CELL;
    }

    static Relation line( int[] map, int width, int from, int to ){
        if (from == to) return Relation.CLEAR;
        int x = from % width, y = from / width, x1 = to % width, y1 = to / width;
        int dx = Math.abs( x1 - x ), dy = -Math.abs( y1 - y ), sx = x < x1 ? 1 : -1, sy = y < y1 ? 1 : -1;
        int err = dx + dy;
        Relation worst = Relation.CLEAR;
        while (true){
            int e2 = 2 * err;
            if (e2 >= dy){ err += dy; x += sx; }
            if (e2 <= dx){ err += dx; y += sy; }
            if (x == x1 && y == y1) return worst;
            byte c = classOf( map[x + y * width] );
            if (c == WALL_CELL) return Relation.WALL;
            if (c == DOOR_CELL) worst = Relation.DOOR;
        }
    }

    private static float pan( float dx ){
        float max = SpatialSound.maxPan();
        return Math.max( -max, Math.min( max, dx / SpatialSound.panCells() ) );
    }

    private static byte cellInfo( int doors, int path ){
        return (byte) ((doors << 6) | Math.min( path, 63 ));
    }

    private static Snapshot work( Level level, int hero, double now ){
        int[] map = level.map;
        int w = level.width(), h = level.height();
        if (map == null || w <= 0 || h <= 0 || map.length < w * h || hero >= w * h) return null;
        int hx = hero % w, hy = hero / w, ox = hx - RADIUS, oy = hy - RADIUS;

        for (int wy = 0; wy < SIDE; wy++){
            int y = oy + wy;
            for (int wx = 0; wx < SIDE; wx++){
                int x = ox + wx, i = wx + wy * SIDE;
                terrain[i] = x < 0 || y < 0 || x >= w || y >= h ? -1 : map[x + y * w];
                kind[i] = classOf( terrain[i] );
            }
        }

        byte[] info = new byte[CELLS];
        short[] via = new short[CELLS];
        Arrays.fill( info, UNREACHED );
        Arrays.fill( via, (short) -1 );
        Arrays.fill( dist, -1 );
        int centre = RADIUS + RADIUS * SIDE;
        int[] in = doorsIn, out = doorsOut;
        int nIn = 0, nSpaceDoors = 0;
        for (int layer = 0; layer <= 2; layer++){
            int head = 0, tail = 0, s = 0, nOut = 0;
            if (layer == 0){
                dist[centre] = 0;
                info[centre] = 0;
                queue[tail++] = centre;
            }
            while (head < tail || s < nIn){
                int c = s < nIn && (head >= tail || dist[in[s]] <= dist[queue[head]]) ? in[s++] : queue[head++];
                int cx = c % SIDE, cy = c / SIDE, d = dist[c] + 1;
                for (int k = 0; k < 8; k++){
                    int nx = cx + DX[k], ny = cy + DY[k];
                    if (nx < 0 || ny < 0 || nx >= SIDE || ny >= SIDE) continue;
                    int n = nx + ny * SIDE;
                    byte nk = kind[n];
                    if (dist[n] >= 0 || nk == WALL_CELL || (nk == DOOR_CELL && layer == 2)) continue;
                    if (DX[k] != 0 && DY[k] != 0 && kind[nx + cy * SIDE] != OPEN_CELL && kind[cx + ny * SIDE] != OPEN_CELL) continue;
                    dist[n] = d;
                    if (nk == DOOR_CELL){
                        info[n] = cellInfo( layer + 1, d );
                        via[n] = layer == 0 ? (short) n : via[c];
                        out[nOut++] = n;
                    } else {
                        info[n] = cellInfo( layer, d );
                        via[n] = via[c];
                        queue[tail++] = n;
                    }
                }
            }
            if (layer == 0){
                System.arraycopy( out, 0, spaceDoors, 0, nOut );
                nSpaceDoors = nOut;
            }
            int[] t = in;
            in = out;
            out = t;
            nIn = nOut;
        }

        int area = 0, faces = 0, soft = 0, wet = 0;
        float soak = 0f;
        for (int i = 0; i < CELLS; i++){
            if (dist[i] < 0 || (info[i] & UNREACHED) != 0) continue;
            area++;
            int t = terrain[i], x = i % SIDE, y = i / SIDE;
            float a = t >= 0 && t < 256 ? ABSORB[t] : 0f;
            soak += a;
            if (t >= 0 && t < 256 && WATER[t]) wet++;
            for (int k : SIDES){
                int nx = x + DX[k], ny = y + DY[k];
                if (nx < 0 || ny < 0 || nx >= SIDE || ny >= SIDE) continue;
                int n = nx + ny * SIDE;
                if (kind[n] == OPEN_CELL) continue;
                faces++;
                if (terrain[n] == Terrain.BOOKSHELF) soft++;
            }
        }
        throughDoors( info, via, nSpaceDoors );

        float softShare = faces == 0 ? 0f : soft / (float) faces;
        float absorb = area == 0 ? 0f : soak / area;
        float water = area == 0 ? 0f : wet / (float) area;
        float thin = faces == 0 ? Float.POSITIVE_INFINITY : area / (float) faces;

        boolean rock = isRockLevel( level );
        Space space;
        Tail tail;
        float base;
        if (area < 20 || thin < 0.9f){
            space = Space.TIGHT;
            tail = Tail.ROOM;
            base = -12f;
        } else if (area < 70){
            space = Space.ROOM;
            tail = rock ? Tail.HALL : Tail.ROOM;
            base = rock ? -12f : -11f;
        } else if (area < 160){
            space = Space.LARGE;
            tail = Tail.HALL;
            base = -11f;
        } else {
            space = Space.HUGE;
            tail = rock || area >= VAST ? Tail.CAVERN : Tail.HALL;
            base = tail == Tail.CAVERN ? -10f : -8f;
        }
        float db = 0f;
        if (tail != Tail.NONE){
            db = base + colour( level ) - 6f * softShare - 8f * absorb + (water >= 0.3f ? 1f : 0f) + TRIM_DB;
            db = Math.max( -24f, Math.min( -6f, db ) );
            if (db < -20f) tail = Tail.NONE;
        }

        float[] echo = (space == Space.HUGE && rock) ? caveEcho( map, w, h, hx, hy, absorb ) : null;

        return new Snapshot( level, hero, now, map, w, h, ox, oy, info, via, kind.clone(),
                space, tail, db, rock, false, echo, area, faces, softShare, absorb, water );
    }

    private static void throughDoors( byte[] info, short[] via, int nDoors ){
        boolean longWay = false;
        for (int i = 0; i < CELLS && !longWay; i++) longWay = (info[i] & UNREACHED) == 0 && (info[i] & 63) - straight( i ) >= DETOUR;
        if (!longWay || nDoors == 0) return;
        Arrays.fill( doorDist, -1 );
        int head = 0, tail = 0, s = 0;
        while (head < tail || s < nDoors){
            int c;
            if (s < nDoors && (head >= tail || dist[spaceDoors[s]] <= doorDist[queue[head]])){
                c = spaceDoors[s++];
                doorDist[c] = dist[c];
                doorVia[c] = (short) c;
            } else {
                c = queue[head++];
            }
            int cx = c % SIDE, cy = c / SIDE, d = doorDist[c] + 1;
            for (int k = 0; k < 8; k++){
                int nx = cx + DX[k], ny = cy + DY[k];
                if (nx < 0 || ny < 0 || nx >= SIDE || ny >= SIDE) continue;
                int n = nx + ny * SIDE;
                if (kind[n] != OPEN_CELL || doorDist[n] >= 0) continue;
                if (DX[k] != 0 && DY[k] != 0 && kind[nx + cy * SIDE] != OPEN_CELL && kind[cx + ny * SIDE] != OPEN_CELL) continue;
                doorDist[n] = d;
                doorVia[n] = doorVia[c];
                queue[tail++] = n;
            }
        }
        for (int i = 0; i < CELLS; i++){
            if ((info[i] & UNREACHED) != 0 || doorDist[i] < 0 || doorDist[i] >= dist[i] || (info[i] & 63) - straight( i ) < DETOUR) continue;
            info[i] = cellInfo( 1, doorDist[i] );
            via[i] = doorVia[i];
        }
    }

    private static int straight( int i ){
        return Math.max( Math.abs( i % SIDE - RADIUS ), Math.abs( i / SIDE - RADIUS ) );
    }

    private static void rays( int[] map, int w, int h, int hx, int hy, int reach, int[] hit, float[] len ){
        for (int k = 0; k < 16; k++){
            double a = k * Math.PI / 8, cos = Math.cos( a ), sin = Math.sin( a );
            hit[k] = -1;
            len[k] = 0f;
            for (int step = 1; step <= 2 * reach; step++){
                int x = (int) Math.round( hx + 0.5 * step * cos ), y = (int) Math.round( hy + 0.5 * step * sin );
                if (x < 0 || y < 0 || x >= w || y >= h) break;
                int c = x + y * w;
                if (classOf( map[c] ) != OPEN_CELL){
                    hit[k] = c;
                    len[k] = (float) Math.hypot( x - hx, y - hy );
                    break;
                }
            }
        }
    }

    private static int face( boolean[] in, float[] len ){
        int start = 0;
        for (int k = 0; k < 16; k++) if (!in[k]) start = (k + 1) % 16;
        float best = Float.MAX_VALUE;
        int bestRay = -1;
        int k = 0;
        while (k < 16){
            int run = 0, near = -1;
            while (k + run < 16 && in[(start + k + run) % 16]){
                int r = (start + k + run) % 16;
                if (near < 0 || len[r] < len[near]) near = r;
                run++;
            }
            if (run >= 2 && len[near] >= 11f && len[near] < best){
                best = len[near];
                bestRay = near;
            }
            k += Math.max( run, 1 );
        }
        return bestRay;
    }

    private static float[] caveEcho( int[] map, int w, int h, int hx, int hy, float absorb ){
        int[] hit = new int[16];
        float[] len = new float[16];
        rays( map, w, h, hx, hy, 24, hit, len );
        boolean[] far = new boolean[16];
        for (int k = 0; k < 16; k++) far[k] = hit[k] >= 0 && len[k] >= 11f;
        int ray = face( far, len );
        if (ray < 0) return null;
        return new float[]{
                Math.max( 0.10f, Math.min( 0.25f, 0.00875f * len[ray] ) ),
                0.16f * (1f - 0.6f * absorb),
                0.5f * pan( hit[ray] % w - hx ) };
    }

    public static final class Snapshot {
        public final Space space;
        public final Tail tail;
        public final float level;
        public final boolean rock, openAir;
        public final float echoDelay, echoLevel, echoPan;
        public final int area, faces;
        public final float soft, absorb, water;

        private final Level owner;
        private final int hero;
        private final double made;
        private final int[] map;
        private final int width, height, ox, oy;
        private final byte[] info, kind;
        private final short[] via;

        private Snapshot( Level owner, int hero, double made, int[] map, int width, int height, int ox, int oy,
                          byte[] info, short[] via, byte[] kind, Space space, Tail tail, float level, boolean rock,
                          boolean openAir, float[] echo, int area, int faces, float soft, float absorb, float water ){
            this.owner = owner; this.hero = hero; this.made = made; this.map = map;
            this.width = width; this.height = height; this.ox = ox; this.oy = oy;
            this.info = info; this.via = via; this.kind = kind;
            this.space = space; this.tail = tail; this.level = level; this.rock = rock; this.openAir = openAir;
            this.echoDelay = echo == null ? 0f : echo[0];
            this.echoLevel = echo == null ? 0f : echo[1];
            this.echoPan = echo == null ? 0f : echo[2];
            this.area = area; this.faces = faces; this.soft = soft; this.absorb = absorb; this.water = water;
        }

        boolean fresh( Level level, int hero, double now ){
            return owner == level && this.hero == hero && now >= made && now - made < MAX_AGE;
        }

        public Relation relation( int cell ){
            if (cell < 0 || cell >= width * height) return Relation.CLEAR;
            int i = window( cell );
            if (i < 0) return beyond( cell );
            i = judged( i );
            return i < 0 ? Relation.WALL : judge( i );
        }

        public int door( int cell ){
            if (cell < 0 || cell >= width * height) return -1;
            int i = window( cell );
            if (i < 0) return -1;
            i = judged( i );
            if (i < 0 || (info[i] >> 6 & 3) != 1 || via[i] < 0) return -1;
            return ox + via[i] % SIDE + (oy + via[i] / SIDE) * width;
        }

        private int window( int cell ){
            int x = cell % width - ox, y = cell / width - oy;
            return x < 0 || y < 0 || x >= SIDE || y >= SIDE ? -1 : x + y * SIDE;
        }

        private int judged( int i ){
            if (kind[i] == OPEN_CELL || i == RADIUS + RADIUS * SIDE) return i;
            int best = -1;
            int x = i % SIDE, y = i / SIDE;
            for (int k = 0; k < 8; k++){
                int nx = x + DX[k], ny = y + DY[k];
                if (nx < 0 || ny < 0 || nx >= SIDE || ny >= SIDE) continue;
                int n = nx + ny * SIDE;
                if (info[n] == UNREACHED) continue;
                if (best < 0 || judge( n ).ordinal() < judge( best ).ordinal()
                        || (judge( n ) == judge( best ) && (info[n] & 63) < (info[best] & 63))) best = n;
            }
            return best;
        }

        private Relation judge( int i ){
            int doors = info[i] >> 6 & 3;
            if (doors == 0) return (info[i] & 63) - straight( i ) < DETOUR ? Relation.CLEAR : Relation.WALL;
            return doors == 1 ? Relation.DOOR : Relation.WALL;
        }

        private Relation beyond( int cell ){
            Relation a = line( map, width, cell, hero ), b = line( map, width, hero, cell );
            return a.ordinal() <= b.ordinal() ? a : b;
        }
    }
}