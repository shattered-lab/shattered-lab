/*
 * Shattered Lab
 * Cheat panel: toggles + quick buttons.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class WndCheats extends Window {

	private final int WIDTH = PixelScene.landscape() ? 150 : 120;
	private static final int BTN_H = 18;
	private static final int GAP = 2;

	private float pos;

	public WndCheats(){
		super();

		final HackCheats c = HackCheats.getOrCreate();

		RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(this, "title"), 9);
		title.hardlight(TITLE_COLOR);
		title.setPos((WIDTH - title.width())/2f, 2);
		add(title);
		pos = title.bottom() + 4;

		check("god", c.god, new Toggle(){ public void set(boolean v){ c.god = v; }});
		check("oneshot", c.oneShot, new Toggle(){ public void set(boolean v){ c.oneShot = v; }});
		check("nohunger", c.noHunger, new Toggle(){ public void set(boolean v){
			c.noHunger = v;
			Hunger h = Dungeon.hero.buff(Hunger.class);
			if (v && h != null) h.satisfy(Hunger.STARVING);
		}});
		check("map", c.revealMap, new Toggle(){ public void set(boolean v){ c.revealMap = v; c.applyReveal(); }});
		check("charges", c.infCharges, new Toggle(){ public void set(boolean v){
			c.infCharges = v;
			if (v) HackCheats.refill(Dungeon.hero);
		}});
		check("talents", c.infTalents, new Toggle(){ public void set(boolean v){ c.infTalents = v; }});

		pos += 2;
		button(Messages.get(this, "heal"), new Runnable(){ public void run(){
			Hero h = Dungeon.hero;
			h.HP = h.HT;
			h.sprite.showStatus(CharSprite.POSITIVE, Messages.get(WndCheats.class, "healed"));
		}});
		button(Messages.get(this, "level_up"), new Runnable(){ public void run(){
			levelUp(1);
		}});
		button(Messages.get(this, "level_max"), new Runnable(){ public void run(){
			levelUp(Hero.MAX_LEVEL);
		}});
		button(Messages.get(this, "ascend"), new Runnable(){ public void run(){
			hide();
			startAscension();
		}});

		resize(WIDTH, (int) pos);
	}

	private static void levelUp(int times){
		Hero h = Dungeon.hero;
		for (int i = 0; i < times && h.lvl < Hero.MAX_LEVEL; i++){
			h.earnExp(h.maxExp() - h.exp, WndCheats.class);
		}
		GLog.p(Messages.get(WndCheats.class, "level_now", h.lvl));
	}

	//gives the Amulet and turns on ascension mode, like after beating Yog-Dzewa
	public static void startAscension(){
		Hero h = Dungeon.hero;
		if (h.belongings.getItem(com.shatteredpixel.shatteredpixeldungeon.items.Amulet.class) == null){
			com.shatteredpixel.shatteredpixeldungeon.Statistics.amuletObtained = true;
			com.shatteredpixel.shatteredpixeldungeon.items.Amulet a = new com.shatteredpixel.shatteredpixeldungeon.items.Amulet();
			a.identify();
			if (!a.collect()) Dungeon.level.drop(a, h.pos).sprite.drop();
		}
		com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff.affect(h,
				com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge.class);
		com.shatteredpixel.shatteredpixeldungeon.Statistics.highestAscent = Math.max(1, Dungeon.depth);
		GLog.p(Messages.get(WndCheats.class, "ascend_started"));
	}

	private interface Toggle {
		void set(boolean value);
	}

	private void check(String key, boolean value, final Toggle toggle){
		CheckBox cb = new CheckBox(Messages.get(this, key)){
			@Override
			protected void onClick() {
				super.onClick();
				if (checked()) HackCheats.markUsed();
				toggle.set(checked());
			}
		};
		cb.checked(value);
		cb.setRect(0, pos, WIDTH, BTN_H);
		add(cb);
		pos += BTN_H + GAP;
	}

	private void button(String label, final Runnable r){
		RedButton b = new RedButton(label, 7){
			@Override
			protected void onClick() {
				HackCheats.markUsed();
				r.run();
			}
		};
		b.setRect(0, pos, WIDTH, BTN_H);
		add(b);
		pos += BTN_H + GAP;
	}
}
