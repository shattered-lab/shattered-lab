/*
 * Shattered Lab
 * Wand of everything: casts the effect of any wand you choose.
 * Inside, a real wand of the chosen type with the same level is made, and the zap is delegated to it.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.hacks.WndPick;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class WandOfEverything extends Wand {

	public static final String AC_CHOOSE = "CHOOSE";

	{
		image = ItemSpriteSheet.HACK_WAND_ALL;
	}

	private Class<? extends Wand> mode = WandOfMagicMissile.class;
	private Wand active; //the wand that is firing now (fx and onZap must go through one object)

	private Wand makeInner(){
		Wand w = Reflection.newInstance(mode);
		if (w == null) w = new WandOfMagicMissile();
		w.level(level());
		w.cursed = false;
		w.levelKnown = w.curChargeKnown = true;
		w.curCharges = curCharges;
		return w;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_CHOOSE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		if (action.equals(AC_CHOOSE)) {
			choose();
		} else {
			super.execute(hero, action);
		}
	}

	public void choose(){
		final Class<?>[] wands = Generator.Category.WAND.classes;
		ArrayList<WndPick.Entry> entries = new ArrayList<>();
		for (Class<?> c : wands){
			Wand w = (Wand) Reflection.newInstance(c);
			entries.add(new WndPick.Entry(new ItemSprite(w), Messages.titleCase(w.name())));
		}
		GameScene.show(new WndPick(Messages.get(WandOfEverything.class, "choose"), entries){
			@Override
			protected void onPick(int index) {
				hide();
				mode = (Class<? extends Wand>) wands[index];
				updateQuickslot();
				GLog.p(Messages.get(WandOfEverything.class, "chosen", modeName()));
			}
		});
	}

	public void setMode(Class<? extends Wand> mode){
		this.mode = mode;
	}

	private String modeName(){
		Wand w = Reflection.newInstance(mode);
		return w == null ? "" : w.name();
	}

	@Override
	public String name() {
		return super.name() + " (" + modeName() + ")";
	}

	@Override
	public String desc() {
		Wand w = makeInner();
		return super.desc() + "\n\n" + Messages.get(this, "current", modeName()) + "\n\n" + w.desc();
	}

	@Override
	public String statsDesc() {
		return makeInner().statsDesc();
	}

	@Override
	public int collisionProperties(int target) {
		return makeInner().collisionProperties(target);
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		active = makeInner();
		active.fx(bolt, callback);
	}

	@Override
	public void onZap(Ballistica bolt) {
		Wand w = active != null ? active : makeInner();
		active = null;
		w.onZap(bolt);
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		//not imbued into a staff
	}

	private static final String MODE = "mode";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(MODE, mode);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(MODE)) {
			Class<?> c = bundle.getClass(MODE);
			if (c != null && Wand.class.isAssignableFrom(c)) mode = (Class<? extends Wand>) c;
		}
	}
}
