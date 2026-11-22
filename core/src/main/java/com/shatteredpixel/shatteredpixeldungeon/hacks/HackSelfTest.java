/*
 * Shattered Lab
 * Self-test of mod features. Runs only if the save folder has a "shd_selftest" file
 * (normal players are not affected). Results go to the log with tag SHDTEST.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks;

import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.hacks.items.BagOfHolding;
import com.shatteredpixel.shatteredpixeldungeon.hacks.items.ScrollOfPolymorph;
import com.shatteredpixel.shatteredpixeldungeon.hacks.items.WandOfEverything;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.AlarmTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public class HackSelfTest {

	public static final String FLAG = "shd_selftest";
	private static boolean checked = false;

	//called every frame from the grimoire button; fires once
	public static void maybeRun(){
		if (checked || Dungeon.hero == null || !Dungeon.hero.ready) return;
		checked = true;
		if (!FileUtils.fileExists(FLAG)) return;
		FileUtils.deleteFile(FLAG);
		try {
			run();
		} catch (Throwable e){
			log("CRASH " + e);
			for (StackTraceElement el : e.getStackTrace()) log("    at " + el);
		}
	}

	private static int pass, fail;

	//for the headless test rig
	public static int passed(){ return pass; }
	public static int failed(){ return fail; }

	private static final StringBuilder out = new StringBuilder();

	private static void log(String s){
		out.append(s).append('\n');
		try { Gdx.app.error("SHDTEST", s); } catch (Throwable ignored) {}
		try { FileUtils.getFileHandle("shd_selftest.log").writeString(s + "\n", true); } catch (Throwable ignored) {}
	}

	private interface Test { String run() throws Exception; }

	private static void t(String name, Test test){
		try {
			String detail = test.run();
			pass++;
			log("PASS " + name + (detail != null ? " (" + detail + ")" : ""));
		} catch (Throwable e){
			fail++;
			log("FAIL " + name + ": " + e);
			StackTraceElement[] st = e.getStackTrace();
			for (int i = 0; i < Math.min(6, st.length); i++) log("    at " + st[i]);
		}
	}

	private static void check(boolean cond, String what) throws Exception {
		if (!cond) throw new Exception("check failed: " + what);
	}

	private static HashSet<Mob> mobsNow(){
		return new HashSet<>(Dungeon.level.mobs);
	}

	private static void removeNew(HashSet<Mob> before){
		for (Mob m : Dungeon.level.mobs.toArray(new Mob[0])){
			if (!before.contains(m)){
				m.destroy();
				if (m.sprite != null) m.sprite.killAndErase();
			}
		}
	}

	public static void run(){
		pass = fail = 0;
		final Hero hero = Dungeon.hero;
		log("START depth=" + Dungeon.depth + " class=" + hero.heroClass);

		t("catalog", new Test(){ public String run() throws Exception {
			int n = 0;
			for (LibraryCatalog.Section s : LibraryCatalog.allSections())
				for (ArrayList<LibraryCatalog.Maker> row : s.rows)
					for (LibraryCatalog.Maker m : row){ check(m.make() != null, s.key); n++; }
			return n + " items";
		}});

		t("kits", new Test(){ public String run() throws Exception {
			int n = 0;
			for (String k : WndKits.KITS){ ArrayList<Item> l = WndKits.kit(k); check(!l.isEmpty(), k); n += l.size(); }
			return WndKits.KITS.length + " kits, " + n + " items";
		}});

		t("workshop+mega", new Test(){ public String run() throws Exception {
			Sword s = new Sword(); s.identify();
			WndWorkshop.setLevel(s, s.level() + 10);
			check(s.level() == 10, "lvl10=" + s.level());
			s.degrade();
			check(s.level() == 9, "degrade=" + s.level());
			return "ok";
		}});

		t("wand_of_everything", new Test(){ public String run() throws Exception {
			WandOfEverything w = new WandOfEverything(); w.identify();
			for (Class<?> c : Generator.Category.WAND.classes){
				w.setMode((Class<? extends Wand>) c);
				w.collisionProperties(hero.pos + 1);
				check(w.name() != null && w.desc() != null && w.statsDesc() != null, c.getSimpleName());
			}
			return Generator.Category.WAND.classes.length + " modes";
		}});

		t("summon_all", new Test(){ public String run() throws Exception {
			Object[] built = WndSummon.build();
			ArrayList<Class<? extends Mob>> classes = (ArrayList<Class<? extends Mob>>) built[1];
			int n = 0;
			for (Class<? extends Mob> c : classes){
				if (c == null) continue;
				HashSet<Mob> before = mobsNow();
				WndSummon.spawn(c, false, 1);
				WndSummon.spawn(c, true, 1);
				removeNew(before);
				n++;
			}
			return n + " mob types";
		}});

		t("polymorph", new Test(){ public String run() throws Exception {
			HashSet<Mob> before = mobsNow();
			WndSummon.spawn(Rat.class, false, 2);
			int count = ScrollOfPolymorph.transformVisible(hero);
			boolean sheep = false;
			for (Mob m : Dungeon.level.mobs) if (m instanceof Sheep && !before.contains(m)) sheep = true;
			removeNew(before);
			check(count >= 1 && sheep, "count=" + count);
			return count + " turned";
		}});

		t("kill_all", new Test(){ public String run() throws Exception {
			HashSet<Mob> before = mobsNow();
			WndSummon.spawn(Rat.class, false, 3);
			WndFloor.killAll();
			int left = 0;
			for (Mob m : Dungeon.level.mobs) if (m instanceof Rat && m.isAlive()) left++;
			removeNew(before);
			check(left == 0, "rats left=" + left);
			return "ok";
		}});

		t("freeze", new Test(){ public String run() throws Exception {
			HashSet<Mob> before = mobsNow();
			WndSummon.spawn(Rat.class, false, 1);
			WndFloor.freeze();
			boolean frozen = false;
			for (Mob m : Dungeon.level.mobs) if (m instanceof Rat && m.buff(Paralysis.class) != null) frozen = true;
			removeNew(before);
			check(frozen, "paralysis");
			return "ok";
		}});

		t("secrets+xray", new Test(){ public String run() throws Exception {
			WndFloor.revealSecrets();
			WndFloor.xray();
			return "ok";
		}});

		t("dummy_stats", new Test(){ public String run() throws Exception {
			HashSet<Mob> before = mobsNow();
			WndSummon.spawn(TrainingDummy.class, false, 1);
			TrainingDummy d = null;
			for (Mob m : Dungeon.level.mobs) if (m instanceof TrainingDummy && !before.contains(m)) d = (TrainingDummy) m;
			check(d != null, "spawned");
			d.damage(7, hero);
			check(d.isAlive() && d.HP == d.HT, "full hp");
			String desc = d.description();
			removeNew(before);
			check(desc.contains("7"), "stats in desc");
			return "ok";
		}});

		t("bag_of_holding", new Test(){ public String run() throws Exception {
			BagOfHolding bag = hero.belongings.getItem(BagOfHolding.class);
			check(bag != null, "has bag");
			ArrayList<Item> added = new ArrayList<>();
			int guard = 0;
			while (hero.belongings.backpack.items.size() < hero.belongings.backpack.capacity() && guard++ < 40){
				Dagger d = new Dagger(); d.identify(); check(d.collect(), "fill"); added.add(d);
			}
			Dagger extra = new Dagger(); extra.identify();
			check(extra.collect(), "extra collect");
			added.add(extra);
			boolean inBag = bag.items.contains(extra);
			for (Item i : added){
				if (bag.items.contains(i)) i.detach(bag); else i.detach(hero.belongings.backpack);
			}
			check(inBag, "overflow went to bag");
			return "filled " + (added.size() - 1);
		}});

		t("two_bags_no_crash", new Test(){ public String run() throws Exception {
			BagOfHolding second = new BagOfHolding();
			check(second.collect(), "second bag");
			ArrayList<Item> added = new ArrayList<>();
			int guard = 0;
			while (hero.belongings.backpack.items.size() < hero.belongings.backpack.capacity() && guard++ < 40){
				Dagger d = new Dagger(); d.identify(); d.collect(); added.add(d);
			}
			Dagger extra = new Dagger(); extra.identify();
			check(extra.collect(), "collect with two bags");
			added.add(extra);
			for (Item i : added){
				for (com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag b : hero.belongings.getBags()){
					if (b.items.contains(i)) { i.detach(b); break; }
				}
			}
			second.detach(hero.belongings.backpack);
			return "ok";
		}});

		t("summon_at_cell", new Test(){ public String run() throws Exception {
			HashSet<Mob> before = mobsNow();
			int target = -1;
			for (int i : com.watabou.utils.PathFinder.NEIGHBOURS8){
				int c = hero.pos + 2*i;
				if (c > 0 && c < Dungeon.level.length() && Dungeon.level.passable[c] && Actor.findChar(c) == null){ target = c; break; }
			}
			check(target != -1, "free cell");
			WndSummon.spawn(Rat.class, false, 1, target);
			boolean ok = false;
			for (Mob m : Dungeon.level.mobs) if (!before.contains(m) && m.pos == target) ok = true;
			removeNew(before);
			check(ok, "spawned at chosen cell");
			return "ok";
		}});

		t("class_change", new Test(){ public String run() throws Exception {
			HeroClass orig = hero.heroClass;
			for (HeroClass c : HeroClass.values()){
				WndHeroEditor.setClass(c);
				check(hero.heroClass == c, c.name());
			}
			WndHeroEditor.setClass(orig);
			check(hero.heroClass == orig, "restored");
			WndTalentsCheat.respec(hero);
			return HeroClass.values().length + " classes";
		}});

		if (Dungeon.level instanceof LibraryLevel){
			final LibraryLevel lib = (LibraryLevel) Dungeon.level;

			t("traps_rearm", new Test(){ public String run() throws Exception {
				int cell = -1;
				for (Map.Entry<Integer, Class<?>> e : lib.testTraps().entrySet()) if (e.getValue() == AlarmTrap.class) cell = e.getKey();
				check(cell != -1, "alarm trap");
				Trap trap = lib.traps.get(cell);
				trap.trigger();
				check(!trap.active || lib.map[cell] != Terrain.TRAP, "triggered");
				lib.restockAll();
				Trap t2 = lib.traps.get(cell);
				check(t2 != null && t2.active && lib.map[cell] == Terrain.TRAP, "rearmed");
				return lib.testTraps().size() + " traps";
			}});

			t("garden_replant", new Test(){ public String run() throws Exception {
				int cell = lib.testGarden().keySet().iterator().next();
				lib.uproot(cell);
				check(lib.plants.get(cell) == null, "uprooted");
				lib.restockAll();
				check(lib.plants.get(cell) != null, "replanted");
				return lib.testGarden().size() + " plants";
			}});

			t("well_refill", new Test(){ public String run() throws Exception {
				Map.Entry<Integer, Class<?>> e = lib.testWells().entrySet().iterator().next();
				int cell = e.getKey();
				Level.set(cell, Terrain.EMPTY_WELL, lib);
				Blob b = lib.blobs.get(e.getValue());
				if (b != null) b.clear(cell);
				lib.restockAll();
				check(lib.map[cell] == Terrain.WELL, "terrain");
				Blob b2 = lib.blobs.get(e.getValue());
				check(b2 != null && b2.cur[cell] > 0, "water");
				GameScene.updateMap(cell);
				return "ok";
			}});
		}

		t("snapshots_folder", new Test(){ public String run() throws Exception {
			return FileUtils.dirExists(com.shatteredpixel.shatteredpixeldungeon.GamesInProgress.gameFolder(
					com.shatteredpixel.shatteredpixeldungeon.GamesInProgress.curSlot)) ? "game folder ok" : "no game folder";
		}});

		log("DONE pass=" + pass + " fail=" + fail);
	}
}
