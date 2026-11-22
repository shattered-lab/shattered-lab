/*
 * Shattered Lab
 * Cheat toggles. Stored as an invisible hero buff, so they are saved with the run.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Bundle;

public class HackCheats extends Buff {

	{
		revivePersists = true;
	}

	public boolean god;
	public boolean oneShot;
	public boolean noHunger;
	public boolean revealMap;
	public boolean infCharges;
	public boolean infTalents;
	//whether cheats were used in the run (for the rankings mark)
	public boolean used;

	public static HackCheats get(){
		if (Dungeon.hero == null) return null;
		return Dungeon.hero.buff(HackCheats.class);
	}

	public static HackCheats getOrCreate(){
		HackCheats c = get();
		if (c == null && Dungeon.hero != null) c = Buff.affect(Dungeon.hero, HackCheats.class);
		return c;
	}

	public static boolean god(){       HackCheats c = get(); return c != null && c.god; }
	public static boolean oneShot(){   HackCheats c = get(); return c != null && c.oneShot; }
	public static boolean noHunger(){  HackCheats c = get(); return c != null && c.noHunger; }
	public static boolean revealMap(){ HackCheats c = get(); return c != null && c.revealMap; }
	public static boolean infTalents(){HackCheats c = get(); return c != null && c.infTalents; }
	public static boolean used(){      HackCheats c = get(); return c != null && c.used; }

	public static void markUsed(){
		HackCheats c = getOrCreate();
		if (c != null) c.used = true;
	}

	//damage with cheats applied: called from Char.damage
	public static int modifyDamage(Char target, int dmg, Object src){
		if (target == Dungeon.hero && god()) return 0;
		if (target != Dungeon.hero && target.alignment != Char.Alignment.ALLY
				&& oneShot() && fromHero(src)) {
			return Math.max(dmg, target.HP + target.shielding());
		}
		return dmg;
	}

	private static boolean fromHero(Object src){
		if (src == null) return false;
		if (src == Dungeon.hero || src instanceof Item) return true;
		return src.getClass().getName().contains(".actors.hero.");
	}

	//whole floor map revealed
	public static void reveal(Level level){
		if (level == null) return;
		for (int i = 0; i < level.length(); i++){
			if (level.discoverable[i]) level.mapped[i] = true;
		}
	}

	public static void onLevelSwitch(Level level){
		if (revealMap()) reveal(level);
	}

	@Override
	public boolean act() {
		if (infCharges && target instanceof Hero){
			refill((Hero) target);
		}
		spend(TICK);
		return true;
	}

	public static void refill(Hero hero){
		for (Item item : hero.belongings){
			if (item instanceof Wand){
				Wand w = (Wand) item;
				if (w.curCharges < w.maxCharges){
					w.curCharges = w.maxCharges;
					Item.updateQuickslot();
				}
			} else if (item instanceof MagesStaff){
				((MagesStaff) item).gainCharge(100f);
			} else if (item instanceof Artifact){
				((Artifact) item).shdFillCharge();
			} else if (item instanceof ClassArmor){
				((ClassArmor) item).charge = 100;
			}
		}
		MeleeWeapon.Charger charger = hero.buff(MeleeWeapon.Charger.class);
		if (charger != null && charger.charges < charger.chargeCap()){
			charger.charges = charger.chargeCap();
		}
	}

	public void applyReveal(){
		if (revealMap && Dungeon.level != null){
			reveal(Dungeon.level);
			GameScene.updateFog();
		}
	}

	private static final String GOD = "god", ONESHOT = "oneshot", HUNGER = "nohunger",
			MAP = "revealmap", CHARGES = "infcharges", TALENTS = "inftalents", USED = "used";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(GOD, god);
		bundle.put(ONESHOT, oneShot);
		bundle.put(HUNGER, noHunger);
		bundle.put(MAP, revealMap);
		bundle.put(CHARGES, infCharges);
		bundle.put(TALENTS, infTalents);
		bundle.put(USED, used);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		god = bundle.getBoolean(GOD);
		oneShot = bundle.getBoolean(ONESHOT);
		noHunger = bundle.getBoolean(HUNGER);
		revealMap = bundle.getBoolean(MAP);
		infCharges = bundle.getBoolean(CHARGES);
		infTalents = bundle.getBoolean(TALENTS);
		used = bundle.getBoolean(USED);
	}
}
