/*
 * Shattered Lab
 * Floor and time: kill all, secrets and traps, x-ray, freeze enemies, skip turns.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Awareness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Ghoul;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;

public class WndFloor extends Window {

	private static final int BTN_H = 20;
	private static final int GAP = 2;

	private final int width;
	private float pos;

	public WndFloor(){
		super();
		width = PixelScene.landscape() ? 150 : 120;

		RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(this, "title"), 9);
		title.hardlight(TITLE_COLOR);
		title.setPos((width - title.width())/2f, 2);
		add(title);
		pos = title.bottom() + 4;

		button(Icons.get(Icons.SKULL), "kill", new Runnable(){ public void run(){ killAll(); }});
		button(Icons.get(Icons.MAGNIFY), "secrets", new Runnable(){ public void run(){ revealSecrets(); }});
		button(new ItemSprite(ItemSpriteSheet.ARTIFACT_TALISMAN), "xray", new Runnable(){ public void run(){ xray(); }});
		button(new ItemSprite(ItemSpriteSheet.POTION_CRIMSON), "freeze", new Runnable(){ public void run(){ freeze(); }});

		float bw = (width - 1) / 2f;
		RedButton skip10 = smallButton(Messages.get(this, "skip", 10), new Runnable(){ public void run(){ skip(10); }});
		skip10.setRect(0, pos, bw, BTN_H);
		RedButton skip100 = smallButton(Messages.get(this, "skip", 100), new Runnable(){ public void run(){ skip(100); }});
		skip100.setRect(bw + 1, pos, bw, BTN_H);
		pos += BTN_H + GAP;

		resize(width, (int) pos);
	}

	private void button(Image icon, String key, final Runnable r){
		RedButton b = new RedButton(Messages.get(this, key), 8){
			@Override
			protected void onClick() {
				hide();
				HackCheats.markUsed();
				r.run();
			}
		};
		b.icon(icon);
		b.leftJustify = true;
		b.setRect(0, pos, width, BTN_H);
		add(b);
		pos += BTN_H + GAP;
	}

	private RedButton smallButton(String label, final Runnable r){
		RedButton b = new RedButton(label, 8){
			@Override
			protected void onClick() {
				hide();
				HackCheats.markUsed();
				r.run();
			}
		};
		add(b);
		return b;
	}

	private static boolean isTarget(Mob m){
		return m.alignment == Char.Alignment.ENEMY
				&& !(m instanceof TrainingDummy)
				&& !Char.hasProp(m, Char.Property.BOSS);
	}

	public static void killAll(){
		int n = 0;
		for (Mob m : Dungeon.level.mobs.toArray(new Mob[0])){
			if (isTarget(m) && m.isAlive()){
				//ghouls revive each other while any one is alive: finish them off with no revive
				m.die(m instanceof Ghoul ? Ghoul.GhoulLifeLink.class : Dungeon.hero);
				n++;
			}
		}
		GLog.p(Messages.get(WndFloor.class, "killed", n));
	}

	public static void revealSecrets(){
		Level level = Dungeon.level;
		int n = 0;
		for (int i = 0; i < level.length(); i++){
			if (level.secret[i]){
				level.discover(i);
				n++;
			}
		}
		for (Trap t : level.traps.valueList()){
			if (!t.visible){
				t.reveal();
				GameScene.updateMap(t.pos);
				n++;
			}
		}
		GameScene.updateFog();
		GLog.p(Messages.get(WndFloor.class, "secrets_found", n));
	}

	public static void xray(){
		HackCheats.reveal(Dungeon.level);
		for (Heap h : Dungeon.level.heaps.valueList()) h.seen = true;
		Buff.affect(Dungeon.hero, MindVision.class, 50f);
		Buff.affect(Dungeon.hero, Awareness.class, 50f);
		Dungeon.observe();
		GameScene.updateFog();
		GLog.p(Messages.get(WndFloor.class, "xray_on"));
	}

	public static void freeze(){
		int n = 0;
		for (Mob m : Dungeon.level.mobs.toArray(new Mob[0])){
			if (m.alignment == Char.Alignment.ENEMY && !(m instanceof TrainingDummy)){
				Buff.prolong(m, Paralysis.class, 30f);
				n++;
			}
		}
		GLog.p(Messages.get(WndFloor.class, "frozen", n));
	}

	public static void skip(int turns){
		GLog.i(Messages.get(WndFloor.class, "skipped", turns));
		Dungeon.hero.spendAndNext(turns);
	}
}
