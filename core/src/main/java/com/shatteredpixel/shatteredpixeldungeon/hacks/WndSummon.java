/*
 * Shattered Lab
 * Bestiary: summon any regular or rare monster next to the hero.
 * Bosses need their own arenas, so use the lift for them.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.PhantomPiranha;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfSirensSong;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class WndSummon extends WndPick {

	private final ArrayList<Class<? extends Mob>> classes;

	public WndSummon(){
		this(build());
	}

	private WndSummon(Object[] built){
		super(Messages.get(WndSummon.class, "title"), (ArrayList<WndPick.Entry>) built[0]);
		this.classes = (ArrayList<Class<? extends Mob>>) built[1];
	}

	static Object[] build(){
		ArrayList<WndPick.Entry> entries = new ArrayList<>();
		ArrayList<Class<? extends Mob>> classes = new ArrayList<>();

		entries.add(new WndPick.Entry(Messages.get(WndSummon.class, "cat_dummy")));
		classes.add(null);
		addMob(TrainingDummy.class, entries, classes);

		Bestiary[] cats = {Bestiary.REGIONAL, Bestiary.UNIVERSAL, Bestiary.RARE};
		for (Bestiary cat : cats){
			entries.add(new WndPick.Entry(cat.title()));
			classes.add(null);
			for (Class<?> c : cat.entities()){
				if (!Mob.class.isAssignableFrom(c)) continue;
				if (c == Piranha.class || c == PhantomPiranha.class) continue; //die at once without water
				//NPCs (e.g. the sentry from the sentry room) are invulnerable, and a sentry outside its room also crashes the game
				if (NPC.class.isAssignableFrom(c)) continue;
				addMob((Class<? extends Mob>) c, entries, classes);
			}
		}
		return new Object[]{entries, classes};
	}

	//all classes that can be summoned (for tests)
	public static ArrayList<Class<? extends Mob>> summonable(){
		ArrayList<Class<? extends Mob>> out = new ArrayList<>();
		for (Class<? extends Mob> c : (ArrayList<Class<? extends Mob>>) build()[1]){
			if (c != null) out.add(c);
		}
		return out;
	}

	private static void addMob(Class<? extends Mob> c, ArrayList<WndPick.Entry> entries,
							   ArrayList<Class<? extends Mob>> classes){
		Mob m = Reflection.newInstance(c);
		if (m == null) return;
		Image icon = null;
		try {
			CharSprite s = Reflection.newInstance(m.spriteClass);
			if (s != null) icon = s;
		} catch (Exception e) {
			icon = null;
		}
		entries.add(new WndPick.Entry(icon, Messages.titleCase(m.name())));
		classes.add(c);
	}

	@Override
	protected void onPick(int index) {
		final Class<? extends Mob> c = classes.get(index);
		if (c == null) return;
		Mob sample = Reflection.newInstance(c);
		Image icon = null;
		try {
			icon = Reflection.newInstance(sample.spriteClass);
		} catch (Exception e) {
			icon = null;
		}
		GameScene.show(new WndOptions(icon, Messages.titleCase(sample.name()),
				Messages.get(WndSummon.class, "how"),
				Messages.get(WndSummon.class, "enemy"),
				Messages.get(WndSummon.class, "enemy5"),
				Messages.get(WndSummon.class, "ally")){
			@Override
			protected void onSelect(int i) {
				//close the bestiary list too, otherwise it covers the map and the cell can't be tapped
				WndSummon.this.hide();
				if (i == 0) pickCell(c, false, 1);
				else if (i == 1) pickCell(c, false, 5);
				else if (i == 2) pickCell(c, true, 1);
			}
		});
	}

	//the player picks the cell to summon into
	public static void pickCell(final Class<? extends Mob> cls, final boolean ally, final int count){
		GameScene.selectCell(new com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector.Listener() {
			@Override
			public void onSelect(Integer cell) {
				if (cell == null) return;
				spawn(cls, ally, count, cell);
			}

			@Override
			public String prompt() {
				return Messages.get(WndSummon.class, "where");
			}
		});
	}

	public static void spawn(Class<? extends Mob> cls, boolean ally, int count){
		spawn(cls, ally, count, Dungeon.hero.pos);
	}

	public static void spawn(Class<? extends Mob> cls, boolean ally, int count, int center){
		Hero hero = Dungeon.hero;
		HackCheats.markUsed();
		int w = Dungeon.level.width();
		int placed = 0;
		for (int r = 0; r <= 4 && placed < count; r++){
			for (int dy = -r; dy <= r && placed < count; dy++){
				for (int dx = -r; dx <= r && placed < count; dx++){
					if (Math.max(Math.abs(dx), Math.abs(dy)) != r) continue;
					int x = center % w + dx, y = center / w + dy;
					if (x < 1 || y < 1 || x >= w - 1 || y >= Dungeon.level.height() - 1) continue;
					int cell = y * w + x;
					Mob m = Reflection.newInstance(cls);
					if (m == null) return;
					if (!Dungeon.level.passable[cell] || Dungeon.level.solid[cell]
							|| Actor.findChar(cell) != null
							|| (Char.hasProp(m, Char.Property.LARGE) && !Dungeon.level.openSpace[cell])){
						continue;
					}
					//statues (and trap guardians) get their weapon from the level generator; without it the game crashes on attack
					if (m instanceof Statue) ((Statue) m).createWeapon(false);
					m.pos = cell;
					if (ally){
						Buff.affect(m, ScrollOfSirensSong.Enthralled.class);
					} else if (!(m instanceof TrainingDummy)){
						m.state = m.HUNTING;
					}
					GameScene.add(m);
					Dungeon.level.occupyCell(m);
					if (!ally && !(m instanceof TrainingDummy)) m.aggro(hero);
					CellEmitter.get(cell).burst(Speck.factory(Speck.LIGHT), 4);
					placed++;
				}
			}
		}
		if (placed == 0){
			GLog.w(Messages.get(WndSummon.class, "no_space"));
		}
	}
}
