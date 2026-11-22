/*
 * Shattered Lab
 * Hero editor: strength, bonus HP, class change mid-run.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;

import java.util.ArrayList;

public class WndHeroEditor extends Window {

	private static final int BTN_H = 18;
	private static final int GAP = 2;

	private final int width;
	private float pos;

	public WndHeroEditor(){
		super();
		width = PixelScene.landscape() ? 150 : 120;
		final Hero h = Dungeon.hero;

		IconTitle title = new IconTitle(HeroSprite.avatar(h),
				Messages.titleCase(h.className()));
		title.setRect(0, 0, width, 0);
		add(title);
		pos = title.bottom() + 4;

		//strength
		label(Messages.get(this, "str", h.STR));
		row(new String[]{"-1", "+1", "+5"}, new Runnable[]{
				new Runnable(){ public void run(){ h.STR = Math.max(1, h.STR - 1); refresh(); }},
				new Runnable(){ public void run(){ h.STR += 1; refresh(); }},
				new Runnable(){ public void run(){ h.STR += 5; refresh(); }}});

		//health
		label(Messages.get(this, "hp", h.HT, (h.HTBoost >= 0 ? "+" : "") + h.HTBoost));
		row(new String[]{"-10", "+10", "+50"}, new Runnable[]{
				new Runnable(){ public void run(){ h.HTBoost -= 10; h.updateHT(true); refresh(); }},
				new Runnable(){ public void run(){ h.HTBoost += 10; h.updateHT(true); refresh(); }},
				new Runnable(){ public void run(){ h.HTBoost += 50; h.updateHT(true); refresh(); }}});

		//class
		RedButton cls = new RedButton(Messages.get(this, "class"), 8){
			@Override
			protected void onClick() {
				hide();
				pickClass();
			}
		};
		cls.setRect(0, pos, width, BTN_H + 2);
		add(cls);
		pos += BTN_H + 2 + GAP;

		resize(width, (int) pos);
	}

	private void label(String text){
		RenderedTextBlock t = PixelScene.renderTextBlock(text, 7);
		t.setPos(0, pos);
		add(t);
		pos = t.bottom() + 2;
	}

	private void row(String[] labels, final Runnable[] actions){
		float bw = (width - (labels.length - 1)) / (float) labels.length;
		for (int i = 0; i < labels.length; i++){
			final Runnable r = actions[i];
			RedButton b = new RedButton(labels[i], 8){
				@Override
				protected void onClick() {
					HackCheats.markUsed();
					r.run();
				}
			};
			b.setRect(i * (bw + 1), pos, bw, BTN_H);
			add(b);
		}
		pos += BTN_H + GAP + 2;
	}

	private void refresh(){
		hide();
		GameScene.show(new WndHeroEditor());
	}

	private static void pickClass(){
		final HeroClass[] classes = HeroClass.values();
		ArrayList<WndPick.Entry> entries = new ArrayList<>();
		for (HeroClass c : classes){
			entries.add(new WndPick.Entry(HeroSprite.avatar(c, 1), Messages.titleCase(c.title())));
		}
		GameScene.show(new WndPick(Messages.get(WndHeroEditor.class, "class"), entries){
			@Override
			protected void onPick(int index) {
				hide();
				setClass(classes[index]);
			}
		});
	}

	public static void setClass(HeroClass c){
		Hero h = Dungeon.hero;
		if (h.heroClass == c) return;
		HackCheats.markUsed();
		h.heroClass = c;
		h.subClass = HeroSubClass.NONE;
		h.armorAbility = null;
		WndTalentsCheat.respec(h);
		//the duelist has special weapon abilities - they need their own charge
		if (c == HeroClass.DUELIST){
			Buff.affect(h, MeleeWeapon.Charger.class);
		} else {
			MeleeWeapon.Charger ch = h.buff(MeleeWeapon.Charger.class);
			if (ch != null) ch.detach();
		}
		if (h.sprite instanceof HeroSprite) ((HeroSprite) h.sprite).updateArmor();
		GameScene.updateAvatar();
		GLog.p(Messages.get(WndHeroEditor.class, "class_now", Messages.titleCase(c.title())));
	}
}
