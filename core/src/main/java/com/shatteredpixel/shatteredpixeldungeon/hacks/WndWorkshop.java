/*
 * Shattered Lab
 * Workshop: item level, enchantment/glyph, curse, charge.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class WndWorkshop extends Window {

	private final int WIDTH = PixelScene.landscape() ? 150 : 120;
	private static final int BTN_H = 18;
	private static final int GAP = 2;

	//pick an item from the inventory
	public static void selectAndOpen(){
		GameScene.selectItem(new WndBag.ItemSelector() {
			@Override
			public String textPrompt() {
				return Messages.get(WndWorkshop.class, "prompt");
			}

			@Override
			public boolean itemSelectable(Item item) {
				return canEdit(item);
			}

			@Override
			public void onSelect(Item item) {
				if (item != null && canEdit(item)) {
					GameScene.show(new WndWorkshop(item));
				}
			}
		});
	}

	public static boolean canEdit(Item item){
		return item.isUpgradable() || item instanceof Artifact;
	}

	private final Item item;
	private float pos;

	public WndWorkshop(final Item item){
		super();
		this.item = item;

		IconTitle title = new IconTitle(new ItemSprite(item), Messages.titleCase(item.name()));
		title.setRect(0, 0, WIDTH, 0);
		add(title);
		pos = title.bottom() + GAP;

		RenderedTextBlock lvl = PixelScene.renderTextBlock(
				Messages.get(this, "level", levelText(item)), 7);
		lvl.setPos(0, pos);
		add(lvl);
		pos = lvl.bottom() + GAP;

		//level
		if (item instanceof Artifact){
			final Artifact a = (Artifact) item;
			row(new String[]{"-1", "+1", Messages.get(this, "max")}, new Runnable[]{
					new Runnable(){ public void run(){ setLevel(item, item.level() - 1); refresh(); }},
					new Runnable(){ public void run(){ setLevel(item, item.level() + 1); refresh(); }},
					new Runnable(){ public void run(){ setLevel(item, a.shdLevelCap()); refresh(); }}});
		} else {
			row(new String[]{"-10", "-1", "+1", "+10", "+100"}, new Runnable[]{
					new Runnable(){ public void run(){ setLevel(item, item.level() - 10); refresh(); }},
					new Runnable(){ public void run(){ setLevel(item, item.level() - 1); refresh(); }},
					new Runnable(){ public void run(){ setLevel(item, item.level() + 1); refresh(); }},
					new Runnable(){ public void run(){ setLevel(item, item.level() + 10); refresh(); }},
					new Runnable(){ public void run(){ setLevel(item, item.level() + 100); refresh(); }}});
		}

		//strength requirement (players asked for it: can go down to 1)
		if ((item instanceof Weapon && !(item instanceof SpiritBow)) || item instanceof Armor){
			RenderedTextBlock str = PixelScene.renderTextBlock(
					Messages.get(this, "str", strReq(item)), 7);
			str.setPos(0, pos);
			add(str);
			pos = str.bottom() + GAP;
			row(new String[]{"1", "-1", "+1", Messages.get(this, "str_reset")}, new Runnable[]{
					new Runnable(){ public void run(){ setStrReq(item, 1); refresh(); }},
					new Runnable(){ public void run(){ setStrReq(item, Math.max(1, strReq(item) - 1)); refresh(); }},
					new Runnable(){ public void run(){ setStrReq(item, strReq(item) + 1); refresh(); }},
					new Runnable(){ public void run(){ setStrReq(item, 0); refresh(); }}});
		}

		//enchantment / glyph
		if (item instanceof Weapon){
			button(Messages.get(this, "enchant"), new Runnable(){ public void run(){ pickEnchant((Weapon) item); }});
		} else if (item instanceof Armor){
			button(Messages.get(this, "glyph"), new Runnable(){ public void run(){ pickGlyph((Armor) item); }});
		}

		//curse
		if (!(item instanceof Artifact) || item.cursed){
			button(Messages.get(this, item.cursed ? "uncurse" : "curse"), new Runnable(){ public void run(){
				item.cursed = !item.cursed;
				item.cursedKnown = true;
				refresh();
			}});
		}

		//charge
		if (item instanceof Wand || item instanceof Artifact || item instanceof MagesStaff){
			button(Messages.get(this, "charge"), new Runnable(){ public void run(){
				if (item instanceof Wand) ((Wand) item).curCharges = ((Wand) item).maxCharges;
				if (item instanceof MagesStaff) ((MagesStaff) item).gainCharge(100f);
				if (item instanceof Artifact) ((Artifact) item).shdFillCharge();
				refresh();
			}});
		}

		resize(WIDTH, (int) pos);
	}

	public static int strReq(Item item){
		return item instanceof Weapon ? ((Weapon) item).STRReq() : ((Armor) item).STRReq();
	}

	//0 = the item's normal requirement
	public static void setStrReq(Item item, int value){
		value = Math.max(0, Math.min(value, 50));
		if (item instanceof Weapon) ((Weapon) item).shdStrReq = value;
		else if (item instanceof Armor) ((Armor) item).shdStrReq = value;
		HackCheats.markUsed();
	}

	private static String levelText(Item item){
		int l = item.level();
		return (l >= 0 ? "+" : "") + l;
	}

	private void row(String[] labels, final Runnable[] actions){
		float bw = (WIDTH - (labels.length - 1)) / (float) labels.length;
		for (int i = 0; i < labels.length; i++){
			final Runnable r = actions[i];
			RedButton b = new RedButton(labels[i], 7){
				@Override
				protected void onClick() {
					r.run();
				}
			};
			b.setRect(i * (bw + 1), pos, bw, BTN_H);
			add(b);
		}
		pos += BTN_H + GAP;
	}

	private void button(String label, final Runnable action){
		RedButton b = new RedButton(label, 7){
			@Override
			protected void onClick() {
				action.run();
			}
		};
		b.setRect(0, pos, WIDTH, BTN_H);
		add(b);
		pos += BTN_H + GAP;
	}

	private void refresh(){
		HackCheats.markUsed();
		Item.updateQuickslot();
		Hero hero = Dungeon.hero;
		if (hero != null){
			hero.updateHT(false);
			if (hero.sprite instanceof HeroSprite) ((HeroSprite) hero.sprite).updateArmor();
		}
		hide();
		GameScene.show(new WndWorkshop(item));
	}

	//level is set directly, with no risk of losing the enchantment
	public static void setLevel(Item item, int lvl){
		HackCheats.markUsed();
		if (item instanceof Artifact){
			Artifact a = (Artifact) item;
			int target = Math.max(0, Math.min(a.shdLevelCap(), lvl));
			while (a.level() < target) a.upgrade();
			if (a.level() > target) a.level(target);
		} else {
			item.level(lvl);
			if (item instanceof MagesStaff) ((MagesStaff) item).updateWand(true);
		}
		item.levelKnown = true;
		Item.updateQuickslot();
		if (Dungeon.hero != null) Dungeon.hero.updateHT(false);
	}

	private void pickEnchant(final Weapon w){
		final ArrayList<Class<?>> classes = new ArrayList<>();
		classes.add(null);
		for (Class<?> c : Weapon.Enchantment.common) classes.add(c);
		for (Class<?> c : Weapon.Enchantment.uncommon) classes.add(c);
		for (Class<?> c : Weapon.Enchantment.rare) classes.add(c);
		for (Class<?> c : Weapon.Enchantment.curses) classes.add(c);

		ArrayList<WndPick.Entry> entries = new ArrayList<>();
		for (Class<?> c : classes){
			if (c == null){
				entries.add(new WndPick.Entry(new ItemSprite(w), Messages.get(WndWorkshop.class, "none")));
				continue;
			}
			Weapon sample = (Weapon) Reflection.newInstance(w.getClass());
			if (sample == null) sample = w;
			sample.enchant((Weapon.Enchantment) Reflection.newInstance(c));
			sample.cursedKnown = true;
			entries.add(new WndPick.Entry(new ItemSprite(sample), Messages.titleCase(sample.name())));
		}
		GameScene.show(new WndPick(Messages.get(this, "enchant"), entries){
			@Override
			protected void onPick(int index) {
				hide();
				Class<?> c = classes.get(index);
				w.enchant(c == null ? null : (Weapon.Enchantment) Reflection.newInstance(c));
				w.cursedKnown = true;
				WndWorkshop.this.refresh();
			}
		});
	}

	private void pickGlyph(final Armor a){
		final ArrayList<Class<?>> classes = new ArrayList<>();
		classes.add(null);
		for (Class<?> c : Armor.Glyph.common) classes.add(c);
		for (Class<?> c : Armor.Glyph.uncommon) classes.add(c);
		for (Class<?> c : Armor.Glyph.rare) classes.add(c);
		for (Class<?> c : Armor.Glyph.curses) classes.add(c);

		ArrayList<WndPick.Entry> entries = new ArrayList<>();
		for (Class<?> c : classes){
			if (c == null){
				entries.add(new WndPick.Entry(new ItemSprite(a), Messages.get(WndWorkshop.class, "none")));
				continue;
			}
			Armor sample = (Armor) Reflection.newInstance(a.getClass());
			if (sample == null) sample = a;
			sample.inscribe((Armor.Glyph) Reflection.newInstance(c));
			sample.cursedKnown = true;
			entries.add(new WndPick.Entry(new ItemSprite(sample), Messages.titleCase(sample.name())));
		}
		GameScene.show(new WndPick(Messages.get(this, "glyph"), entries){
			@Override
			protected void onPick(int index) {
				hide();
				Class<?> c = classes.get(index);
				a.inscribe(c == null ? null : (Armor.Glyph) Reflection.newInstance(c));
				a.cursedKnown = true;
				WndWorkshop.this.refresh();
			}
		});
	}
}
