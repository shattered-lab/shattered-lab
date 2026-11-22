/*
 * Shattered Lab
 * Talents: infinite points, reset, change subclass and armor ability at any time.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KingsCrown;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndChooseAbility;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndChooseSubclass;

public class WndTalentsCheat extends Window {

	private final int WIDTH = PixelScene.landscape() ? 150 : 120;
	private static final int BTN_H = 18;
	private static final int GAP = 2;

	private float pos;

	public WndTalentsCheat(){
		super();

		RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(this, "title"), 9);
		title.hardlight(TITLE_COLOR);
		title.setPos((WIDTH - title.width())/2f, 2);
		add(title);

		RenderedTextBlock hint = PixelScene.renderTextBlock(Messages.get(this, "hint"), 6);
		hint.maxWidth(WIDTH);
		hint.setPos(0, title.bottom() + 3);
		add(hint);
		pos = hint.bottom() + 4;

		final HackCheats c = HackCheats.getOrCreate();
		CheckBox inf = new CheckBox(Messages.get(WndCheats.class, "talents")){
			@Override
			protected void onClick() {
				super.onClick();
				c.infTalents = checked();
			}
		};
		inf.checked(c.infTalents);
		inf.setRect(0, pos, WIDTH, BTN_H);
		add(inf);
		pos += BTN_H + GAP;

		button(Messages.get(WndCheats.class, "level_max"), new Runnable(){ public void run(){
			Hero h = Dungeon.hero;
			while (h.lvl < Hero.MAX_LEVEL) h.earnExp(h.maxExp() - h.exp, WndCheats.class);
			GLog.p(Messages.get(WndCheats.class, "level_now", h.lvl));
		}});

		button(Messages.get(this, "respec"), new Runnable(){ public void run(){
			respec(Dungeon.hero);
			GLog.p(Messages.get(WndTalentsCheat.class, "respec_done"));
		}});

		button(Messages.get(this, "subclass"), new Runnable(){ public void run(){
			hide();
			Hero h = Dungeon.hero;
			h.subClass = HeroSubClass.NONE;
			respec(h);
			final Hero user = h;
			//curUser is protected, so we set it from inside a mask subclass
			TengusMask mask = new TengusMask(){
				{ curUser = user; }
			};
			GameScene.show(new WndChooseSubclass(mask, h));
		}});

		button(Messages.get(this, "ability"), new Runnable(){ public void run(){
			hide();
			Hero h = Dungeon.hero;
			h.armorAbility = null;
			respec(h);
			Armor armor = h.belongings.armor();
			GameScene.show(new WndChooseAbility(new KingsCrown(),
					armor instanceof ClassArmor ? null : armor, h));
		}});

		resize(WIDTH, (int) pos);
	}

	//all talent points are refunded, subclass and ability are kept
	public static void respec(Hero hero){
		hero.talents.clear();
		Talent.initClassTalents(hero);
		if (hero.subClass != HeroSubClass.NONE) Talent.initSubclassTalents(hero);
		if (hero.armorAbility != null) Talent.initArmorTalents(hero);
		hero.updateHT(false);
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
