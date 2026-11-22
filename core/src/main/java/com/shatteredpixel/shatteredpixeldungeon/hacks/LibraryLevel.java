/*
 * Shattered Lab
 * Floor 0: a library with every item in the game.
 *
 * Layout (top view):
 *   +-------------------------------------+
 *   |        MOD SECTION (full width)     |
 *   +------------+-------+----------------+
 *   | artifacts  |       | stones/seeds   |
 *   | wands      | hall  | alchemy        |
 *   | weapons    | (hero | POTIONS        |
 *   | thrown/arm | here) | scrolls        |
 *   | enchants   |       | food and bombs |
 *   |            |       | glyphs         |
 *   +------------+-------+----------------+
 *   | misc       | down  | quest          |
 *   +------------+-------+----------------+
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Alchemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.SurfaceScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WellWater;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.watabou.utils.Reflection;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class LibraryLevel extends Level {

	{
		color1 = 0x4b6636;
		color2 = 0xf2f2f2;
		viewDistance = 14;
	}

	// ===== geometry =====
	public static final int W = 39;
	public static final int H = 62;

	private static final int SIDE_W = 14;          //inner width of a side room
	private static final int LEFT_X = 1;           //left rooms: x 1..14
	private static final int HALL_X = 16;          //hall: x 16..22
	private static final int HALL_W = 7;
	private static final int RIGHT_X = 24;         //right rooms: x 24..37
	private static final int TOP_Y = 1;            //mod section: y 1..4
	private static final int TOP_H = 4;
	private static final int MID_Y = 6;            //side rooms: y 6..34
	private static final int MID_H = 29;
	private static final int BOT_Y = 36;           //bottom rooms: y 36..40
	private static final int BOT_H = 5;
	private static final int ARENA_Y = 42;         //arena: y 42..50, full width
	private static final int ARENA_H = 9;
	//dummies and the bestiary lectern on the arena
	private static final int[][] DUMMIES = {{5, 45}, {11, 47}, {27, 47}, {33, 45}};
	private static final int LECTERN_X = 15, LECTERN_Y = 43;
	private static final int PLAY_Y = 52;          //trap range, garden and wells: y 52..60
	private static final int PLAY_H = 9;
	private static final int TRAPS_W = 22;         //trap range: x 1..22

	public static final int SPAWN = (MID_Y + 14) * W + HALL_X + HALL_W/2;  //hall center, stairs up
	public static final int EXIT_CELL = (ARENA_Y + ARENA_H - 1) * W + HALL_X + HALL_W/2;

	// ===== shelves =====
	public static class Shelf {
		public final LibraryCatalog.Maker maker;
		public final String sig;
		public final Class<? extends Item> cls;
		public final boolean blocked;

		Shelf(LibraryCatalog.Maker maker){
			this.maker = maker;
			Item sample = maker.make();
			this.cls = sample.getClass();
			this.sig = signature(sample);
			this.blocked = Challenges.isItemBlocked(sample);
		}
	}

	private static class Label {
		final String key;
		final float x, y; //in cells
		final int color;
		final int align; //-1: x = left edge, 0: x = center, 1: x = right edge
		Label(String key, float x, float y, int color, int align){
			this.key = key; this.x = x; this.y = y; this.color = color; this.align = align;
		}
	}

	private LinkedHashMap<Integer, Shelf> shelves = new LinkedHashMap<>();
	private LinkedHashMap<Integer, Class<?>> trapSpots = new LinkedHashMap<>();
	private LinkedHashMap<Integer, Class<?>> gardenSpots = new LinkedHashMap<>();
	private LinkedHashMap<Integer, Class<?>> wellSpots = new LinkedHashMap<>();
	private ArrayList<Label> labels = new ArrayList<>();
	private int[] originalMap;

	public static String signature(Item item){
		String s = item.getClass().getName();
		if (item instanceof Weapon && ((Weapon) item).enchantment != null){
			s += "|" + ((Weapon) item).enchantment.getClass().getName();
		}
		if (item instanceof Armor && ((Armor) item).glyph != null){
			s += "|" + ((Armor) item).glyph.getClass().getName();
		}
		return s;
	}

	//for the self-test
	public java.util.Map<Integer, Class<?>> testTraps(){ return trapSpots; }
	public java.util.Map<Integer, Class<?>> testGarden(){ return gardenSpots; }
	public java.util.Map<Integer, Class<?>> testWells(){ return wellSpots; }

	public Shelf shelfAt(int cell){
		return shelves.get(cell);
	}

	//true if the cell holds the display copy itself (not something the player dropped on it)
	public boolean isShelfItem(int cell, Item item){
		Shelf s = shelves.get(cell);
		return s != null && item != null && s.sig.equals(signature(item));
	}

	// ===== music and tiles =====

	@Override
	public void playLevelMusic() {
		Music.INSTANCE.play(Assets.Music.THEME_1, true);
	}

	@Override
	public String tilesTex() {
		return Assets.Environment.TILES_CITY;
	}

	@Override
	public String waterTex() {
		return Assets.Environment.WATER_CITY;
	}

	// ===== building =====

	@Override
	protected boolean build() {
		setSize(W, H);
		originalMap = paint(map);

		LevelTransition entry = new LevelTransition(this, SPAWN, LevelTransition.Type.SURFACE);
		transitions.add(entry);
		LevelTransition exit = new LevelTransition(this, EXIT_CELL, LevelTransition.Type.REGULAR_EXIT);
		transitions.add(exit);

		feeling = Feeling.NONE;
		return true;
	}

	//draws the map into target and fills shelves/labels; returns a copy of the map
	private int[] paint(int[] target){
		shelves = new LinkedHashMap<>();
		labels = new ArrayList<>();
		trapSpots = new LinkedHashMap<>();
		gardenSpots = new LinkedHashMap<>();
		wellSpots = new LinkedHashMap<>();

		Arrays.fill(target, Terrain.WALL);

		//mod section on top
		fill(target, 1, TOP_Y, W-2, TOP_H, Terrain.EMPTY_SP);
		placeSection(LibraryCatalog.mod(), 1, TOP_Y, W-2, TOP_H, 0xFF66FF, 0);

		//hall
		fill(target, HALL_X, MID_Y-1, HALL_W, MID_H+3+BOT_H, Terrain.EMPTY);
		fill(target, HALL_X+2, MID_Y-1, 3, MID_H+3+BOT_H, Terrain.EMPTY_SP);

		//arena
		fill(target, 1, ARENA_Y, W-2, ARENA_H, Terrain.EMPTY);
		fill(target, HALL_X+2, ARENA_Y, 3, ARENA_H, Terrain.EMPTY_SP);
		target[LECTERN_Y*W + LECTERN_X] = Terrain.PEDESTAL;
		shelves.put(LECTERN_Y*W + LECTERN_X, new Shelf(LibraryCatalog.of(
				com.shatteredpixel.shatteredpixeldungeon.hacks.items.BestiaryBook.class)));
		for (int x = 3; x < W-3; x += 6){
			if (Math.abs(x - (HALL_X + HALL_W/2)) <= 3) continue;
			target[(ARENA_Y + ARENA_H - 1)*W + x] = Terrain.STATUE_SP;
		}
		labels.add(new Label("sec_arena", HALL_X + HALL_W/2f, ARENA_Y - 1, 0xFF8866, 0));

		//trap range: every trap type on every other cell, so you can walk between them
		fill(target, 1, PLAY_Y, TRAPS_W, PLAY_H, Terrain.EMPTY_SP);
		target[(PLAY_Y-1)*W + 12] = Terrain.DOOR;
		ArrayList<Class<?>> trapClasses = new ArrayList<>(
				com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary.TRAP.entities());
		int ti = 0;
		for (int ty = PLAY_Y + 2; ty <= PLAY_Y + 6 && ti < trapClasses.size(); ty += 2){
			for (int tx = 2; tx <= TRAPS_W && ti < trapClasses.size(); tx += 2){
				int cell = ty*W + tx;
				target[cell] = Terrain.TRAP;
				trapSpots.put(cell, trapClasses.get(ti++));
			}
		}
		labels.add(new Label("sec_traps", 1 + TRAPS_W/2f, PLAY_Y - 1, 0xFF6666, 0));

		//garden and wells
		fill(target, RIGHT_X, PLAY_Y, SIDE_W, PLAY_H, Terrain.EMPTY);
		target[(PLAY_Y-1)*W + 30] = Terrain.DOOR;
		Class<?>[] seeds = com.shatteredpixel.shatteredpixeldungeon.items.Generator.Category.SEED.classes;
		int si = 0;
		for (int gy = PLAY_Y + 2; gy <= PLAY_Y + 4 && si < seeds.length; gy += 2){
			for (int gx = RIGHT_X + 1; gx < RIGHT_X + SIDE_W && si < seeds.length; gx += 2){
				int cell = gy*W + gx;
				target[cell] = Terrain.GRASS;
				gardenSpots.put(cell, seeds[si++]);
			}
		}
		int w1 = (PLAY_Y + 7)*W + RIGHT_X + 3, w2 = (PLAY_Y + 7)*W + RIGHT_X + 10;
		target[w1] = Terrain.WELL;
		target[w2] = Terrain.WELL;
		wellSpots.put(w1, com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfHealth.class);
		wellSpots.put(w2, com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfAwareness.class);
		labels.add(new Label("sec_garden", RIGHT_X + SIDE_W/2f, PLAY_Y - 1, 0x88FF88, 0));

		//side rooms
		LibraryCatalog.Section[] left = {
				LibraryCatalog.artifacts(), LibraryCatalog.wandsRings(), LibraryCatalog.melee(),
				LibraryCatalog.missilesArmor(), LibraryCatalog.enchants()};
		LibraryCatalog.Section[] right = {
				LibraryCatalog.stonesSeeds(), LibraryCatalog.alchemy(), LibraryCatalog.potions(),
				LibraryCatalog.scrolls(), LibraryCatalog.foodBombs(), LibraryCatalog.glyphs()};
		column(target, left, LEFT_X, HALL_X-1);
		int alchemyTop = column(target, right, RIGHT_X, HALL_X+HALL_W);

		//alchemy pot in the alchemy room (bottom right corner)
		int potX = RIGHT_X + SIDE_W - 1;
		int potY = alchemyTop + 3;
		target[potY*W + potX] = Terrain.ALCHEMY;
		potCell = potY*W + potX;

		//bottom rooms
		fill(target, LEFT_X, BOT_Y, SIDE_W, BOT_H, Terrain.EMPTY_SP);
		target[(BOT_Y+1)*W + HALL_X-1] = Terrain.DOOR;
		doorSign("sec_misc", BOT_Y+1, true);
		placeSection(LibraryCatalog.misc(), LEFT_X, BOT_Y, SIDE_W, BOT_H, 0xFFFF44, 1);
		fill(target, RIGHT_X, BOT_Y, SIDE_W, BOT_H, Terrain.EMPTY_SP);
		target[(BOT_Y+1)*W + HALL_X+HALL_W] = Terrain.DOOR;
		doorSign("sec_quest", BOT_Y+1, false);
		placeSection(LibraryCatalog.quest(), RIGHT_X, BOT_Y, SIDE_W, BOT_H, 0xFFFF44, -1);

		//walls between rooms are bookshelves
		for (int i = 0; i < target.length; i++){
			int x = i % W, y = i / W;
			if (target[i] == Terrain.WALL && x > 0 && x < W-1 && y > 0 && y < H-1){
				target[i] = Terrain.BOOKSHELF;
			}
		}

		//statues along the hall (not blocking the doors)
		for (int y = MID_Y+2; y < MID_Y+MID_H-1; y += 6){
			if (Math.abs(y - SPAWN / W) <= 1) continue;
			for (int x : new int[]{HALL_X, HALL_X+HALL_W-1}){
				int wallX = x == HALL_X ? x-1 : x+1;
				boolean nearDoor = false;
				for (int dy = -1; dy <= 1; dy++){
					if (target[(y+dy)*W + wallX] == Terrain.DOOR) nearDoor = true;
				}
				if (!nearDoor) target[y*W + x] = Terrain.STATUE_SP;
			}
		}

		//stairs
		target[SPAWN] = Terrain.ENTRANCE_SP;
		target[EXIT_CELL] = Terrain.EXIT;

		labels.add(new Label("up", SPAWN % W + 0.5f, SPAWN / W + 1.0f, 0xAAFFAA, 0));
		labels.add(new Label("down", EXIT_CELL % W + 0.5f, EXIT_CELL / W - 1.2f, 0xAAFFAA, 0));

		return target.clone();
	}

	private int potCell = -1;

	//builds a column of rooms; returns the top of the alchemy room (for the pot), if any
	private int column(int[] target, LibraryCatalog.Section[] sections, int x, int doorX){
		int needed = 0;
		for (LibraryCatalog.Section s : sections) needed += roomHeight(s);
		needed += sections.length - 1;
		int extra = MID_H - needed;

		int y = MID_Y;
		int alchemyTop = -1;
		for (int i = 0; i < sections.length; i++){
			LibraryCatalog.Section s = sections[i];
			int h = roomHeight(s);
			//extra rows go to the last rooms
			if (extra > 0 && i >= sections.length - extra){
				h++;
			}
			if (i == sections.length-1){
				h = MID_Y + MID_H - y;
			}
			fill(target, x, y, SIDE_W, h, Terrain.EMPTY_SP);
			target[(y+1)*W + doorX] = Terrain.DOOR;
			doorSign(s.key, y+1, x == LEFT_X);
			if (s.key.equals("sec_alchemy")) alchemyTop = y;
			placeSection(s, x, y, SIDE_W, h, 0xFFFF44, x == LEFT_X ? 1 : -1);
			y += h + 1;
		}
		return alchemyTop;
	}

	//short sign in the hall right by the door: "< WEAPONS" on the left, "POTIONS >" on the right
	private void doorSign(String key, int row, boolean leftSide){
		if (leftSide){
			labels.add(new Label("door_" + key, HALL_X + 0.15f, row, 0xEEDDAA, -1));
		} else {
			labels.add(new Label("door_" + key, HALL_X + HALL_W - 0.15f, row, 0xEEDDAA, 1));
		}
	}

	private static int roomHeight(LibraryCatalog.Section s){
		return Math.max(3, s.rows.size()*2 - 1);
	}

	//item rows on every other line (item row, aisle, item row...)
	//room label - a sign centered on the bookshelf wall above it
	private void placeSection(LibraryCatalog.Section s, int x, int y, int w, int h, int color, int align){
		int row = 0;
		for (ArrayList<LibraryCatalog.Maker> items : s.rows){
			int ry = y + row*2;
			if (ry >= y + h) break;
			int start = x + Math.max(0, (w - items.size()) / 2);
			for (int i = 0; i < items.size() && i < w; i++){
				shelves.put(ry*W + start + i, new Shelf(items.get(i)));
			}
			row++;
		}
		labels.add(new Label(s.key, x + w/2f, y - 1, color, 0));
	}

	private static void fill(int[] target, int x, int y, int w, int h, int value){
		for (int j = y; j < y + h; j++){
			for (int i = x; i < x + w; i++){
				target[j*W + i] = value;
			}
		}
	}

	@Override
	public void create() {
		super.create();
		//the whole library is visible at once
		for (int i = 0; i < length(); i++){
			visited[i] = true;
			mapped[i] = true;
		}
		if (potCell != -1){
			Blob.seed(potCell, 1, Alchemy.class, this);
		}
		restockPlayground();
	}

	//arms traps, plants seeds, fills wells
	private void restockPlayground(){
		boolean inScene = Dungeon.level == this;
		for (Map.Entry<Integer, Class<?>> e : trapSpots.entrySet()){
			int cell = e.getKey();
			Trap t = traps.get(cell);
			if (t == null || t.getClass() != e.getValue()){
				t = (Trap) Reflection.newInstance(e.getValue());
				if (t == null) continue;
				t.set(cell);
				traps.put(cell, t);
			}
			t.active = true;
			t.reveal();
			if (map[cell] != Terrain.TRAP && Actor.findChar(cell) == null){
				set(cell, Terrain.TRAP, this);
				if (inScene) GameScene.updateMap(cell);
			}
		}
		for (Map.Entry<Integer, Class<?>> e : gardenSpots.entrySet()){
			int cell = e.getKey();
			if (plants.get(cell) != null || Actor.findChar(cell) != null || heaps.get(cell) != null) continue;
			Plant.Seed seed = (Plant.Seed) Reflection.newInstance(e.getValue());
			if (seed == null) continue;
			if (inScene){
				plant(seed, cell);
			} else {
				plants.put(cell, seed.couch(cell, this));
			}
		}
		for (Map.Entry<Integer, Class<?>> e : wellSpots.entrySet()){
			int cell = e.getKey();
			if (map[cell] == Terrain.EMPTY_WELL || blobs.get(e.getValue()) == null
					|| blobs.get(e.getValue()).cur == null || blobs.get(e.getValue()).cur[cell] <= 0){
				if (map[cell] != Terrain.WELL){
					set(cell, Terrain.WELL, this);
					if (inScene) GameScene.updateMap(cell);
				}
				WellWater.seed(cell, 1, (Class<? extends WellWater>) e.getValue(), this);
			}
		}
	}

	@Override
	public Mob createMob() {
		return null;
	}

	//no monsters spawn in the library (e.g. during ascension)
	@Override
	public boolean spawnMob(int disLimit) {
		return false;
	}

	@Override
	protected void createMobs() {
		respawnDummies();
	}

	//dummies come back if they get destroyed anyway
	private void respawnDummies(){
		for (int[] d : DUMMIES){
			int cell = d[1]*W + d[0];
			boolean found = false;
			for (Mob m : mobs){
				if (m instanceof TrainingDummy && m.pos == cell) { found = true; break; }
			}
			if (found || Actor.findChar(cell) != null) continue;
			TrainingDummy dummy = new TrainingDummy();
			dummy.pos = cell;
			if (Dungeon.level == this){
				GameScene.add(dummy);
			} else {
				mobs.add(dummy);
			}
		}
	}

	//tapping a shelf: the bestiary opens summon, anything else opens the "take" window
	public static void onShelfTap(Heap heap, Shelf shelf){
		if (heap.peek() instanceof com.shatteredpixel.shatteredpixeldungeon.hacks.items.BestiaryBook){
			final LibraryCatalog.Maker maker = shelf.maker;
			Item book = heap.peek();
			GameScene.show(new com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions(
					new com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite(book),
					Messages.titleCase(book.name()), book.desc(),
					Messages.get(LibraryLevel.class, "lectern_summon"),
					Messages.get(LibraryLevel.class, "lectern_take")){
				@Override
				protected void onSelect(int index) {
					if (index == 0) GameScene.show(new WndSummon());
					else if (index == 1) WndLibraryItem.take(maker, 1);
				}
			});
		} else {
			GameScene.show(new WndLibraryItem(heap, shelf));
		}
	}

	@Override
	protected void createItems() {
		for (Map.Entry<Integer, Shelf> e : shelves.entrySet()){
			restockShelf(e.getKey(), e.getValue());
		}
	}

	private void restockShelf(int cell, Shelf shelf){
		if (shelf.blocked) return;
		Heap heap = heaps.get(cell);
		if (heap != null){
			for (Item i : heap.items){
				if (shelf.sig.equals(signature(i))) return;
			}
		}
		Item item = shelf.maker.make();
		if (item == null) return;
		Heap h = drop(item, cell);
		h.seen = true;
	}

	public void restockAll(){
		for (Map.Entry<Integer, Shelf> e : shelves.entrySet()){
			restockShelf(e.getKey(), e.getValue());
		}
		repairShelves();
		respawnDummies();
		restockPlayground();
	}

	//burnt bookshelves and doors repair themselves
	private void repairShelves(){
		if (originalMap == null) return;
		Blob fire = blobs.get(Fire.class);
		for (int i = 0; i < length(); i++){
			int orig = originalMap[i];
			if ((orig == Terrain.BOOKSHELF || orig == Terrain.DOOR || orig == Terrain.STATUE_SP)
					&& map[i] != orig && map[i] != Terrain.OPEN_DOOR
					&& (fire == null || fire.cur == null || fire.cur[i] <= 0)
					&& Actor.findChar(i) == null && heaps.get(i) == null){
				set(i, orig, this);
				if (Dungeon.level == this) GameScene.updateMap(i);
			}
		}
	}

	//library keeper: every few turns puts items back on the shelves
	public static class Keeper extends Actor {
		{
			actPriority = BUFF_PRIO;
		}

		@Override
		protected boolean act() {
			if (Dungeon.level instanceof LibraryLevel){
				((LibraryLevel) Dungeon.level).restockAll();
			}
			spend(5f);
			return true;
		}
	}

	@Override
	public Actor addRespawner() {
		Keeper k = new Keeper();
		Actor.addDelayed(k, 1f);
		return k;
	}

	@Override
	public int randomRespawnCell( Char ch ) {
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int i : PathFinder.NEIGHBOURS8){
			int cell = SPAWN + i;
			if (passable[cell] && Actor.findChar(cell) == null){
				candidates.add(cell);
			}
		}
		return candidates.isEmpty() ? -1 : Random.element(candidates);
	}

	// ===== transitions =====

	@Override
	public boolean activateTransition(Hero hero, LevelTransition transition) {
		if (transition.type == LevelTransition.Type.SURFACE){
			if (hero.belongings.getItem( Amulet.class ) == null) {
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show( new WndMessage( Messages.get(Hero.class, "leave") ) );
					}
				});
				return false;
			} else {
				Statistics.ascended = true;
				Game.switchScene(SurfaceScene.class, new Game.SceneChangeCallback() {
					@Override
					public void beforeCreate() {
					}

					@Override
					public void afterCreate() {
						Badges.validateHappyEnd();
						Dungeon.win( Amulet.class );
						Dungeon.deleteGame( GamesInProgress.curSlot, true );
						Badges.saveGlobal();
					}
				});
				return true;
			}
		}
		return super.activateTransition(hero, transition);
	}

	// ===== section labels =====

	@Override
	public Group addVisuals() {
		super.addVisuals();
		for (Label l : labels){
			RenderedTextBlock t = new RenderedTextBlock(6 * PixelScene.defaultZoom);
			t.text(Messages.get(LibraryLevel.class, l.key));
			t.zoom(1f / PixelScene.defaultZoom);
			t.hardlight(l.color);
			float tw = t.width(), th = t.height();
			float left = l.align == 0 ? l.x * 16f - tw / 2f
					: (l.align > 0 ? l.x * 16f - tw : l.x * 16f);
			float top = l.y * 16f + (16f - th) / 2f;

			ColorBlock frame = new ColorBlock(tw + 6, th + 4, 0xFF8A6A3A);
			frame.x = left - 3; frame.y = top - 2;
			visuals.add(frame);
			ColorBlock plate = new ColorBlock(tw + 4, th + 2, 0xFF221A12);
			plate.x = left - 2; plate.y = top - 1;
			visuals.add(plate);

			t.setPos(left, top);
			visuals.add(t);
		}
		return visuals;
	}

	@Override
	public String tileName(int tile) {
		switch (tile) {
			case Terrain.BOOKSHELF:
				return Messages.get(LibraryLevel.class, "shelf_name");
			default:
				return super.tileName(tile);
		}
	}

	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.BOOKSHELF:
				return Messages.get(LibraryLevel.class, "shelf_desc");
			case Terrain.ENTRANCE_SP:
				return Messages.get(LibraryLevel.class, "entrance_desc");
			default:
				return super.tileDesc(tile);
		}
	}

	// ===== saving =====

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		//the layout is deterministic, so just rebuild the shelf list
		int[] scratch = new int[W*H];
		originalMap = paint(scratch);
		//save from an old library version of another size - disable shelves and repairs
		if (width != W || height != H){
			originalMap = null;
			shelves = new LinkedHashMap<>();
			labels = new ArrayList<>();
			trapSpots = new LinkedHashMap<>();
			gardenSpots = new LinkedHashMap<>();
			wellSpots = new LinkedHashMap<>();
		}
	}
}
