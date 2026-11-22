/*
 * Shattered Lab
 * Ready-made gear kits: one button - a full set.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.LeatherArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Camouflage;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Swiftness;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Thorns;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.AlchemistsToolkit;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Firebomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.FlashBangBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.FrostBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.HolyBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Noisemaker;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.RegrowthBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.ShrapnelBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.SmokeBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.WoollyBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfArcana;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEvasion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfFuror;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfTenacity;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFireblast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blocking;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Corrupting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Kinetic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Lucky;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Projecting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Vampiric;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.AssassinsBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatshield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Katana;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WarHammer;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class WndKits extends WndPick {

	static final String[] KITS = {
			"rich", "warrior", "mage", "rogue", "huntress", "duelist", "cleric",
			"alchemist", "bomber", "explorer"};

	public WndKits(){
		super(Messages.get(WndKits.class, "title"), entries());
	}

	private static ArrayList<WndPick.Entry> entries(){
		ArrayList<WndPick.Entry> list = new ArrayList<>();
		for (String k : KITS){
			ArrayList<Item> items = kit(k);
			list.add(new WndPick.Entry(new ItemSprite(items.get(0)), Messages.get(WndKits.class, k)));
		}
		return list;
	}

	@Override
	protected void onPick(int index) {
		hide();
		give(kit(KITS[index]));
		GLog.p(Messages.get(WndKits.class, "given", Messages.get(WndKits.class, KITS[index])));
	}

	private static void give(ArrayList<Item> items){
		Hero hero = Dungeon.hero;
		HackCheats.markUsed();
		for (Item i : items){
			if (i instanceof Gold){
				Dungeon.gold += i.quantity();
			} else if (i instanceof EnergyCrystal){
				Dungeon.energy += i.quantity();
			} else if (!i.collect()){
				Dungeon.level.drop(i, hero.pos).sprite.drop();
			}
		}
		Item.updateQuickslot();
		hero.updateHT(false);
	}

	private static Item lvl(Item i, int level){
		WndWorkshop.setLevel(i, level);
		i.identify(false);
		i.cursed = false;
		return i;
	}

	private static Item ench(Weapon w, Weapon.Enchantment e, int level){
		w.enchant(e);
		return lvl(w, level);
	}

	private static Item glyph(Armor a, Armor.Glyph g, int level){
		a.inscribe(g);
		return lvl(a, level);
	}

	private static Item qty(Item i, int q){
		i.quantity(q);
		i.identify(false);
		return i;
	}

	static ArrayList<Item> kit(String key){
		ArrayList<Item> k = new ArrayList<>();
		switch (key){
			case "rich":
				k.add(qty(new Gold(), 10000));
				k.add(qty(new EnergyCrystal(), 5000));
				k.add(qty(new ScrollOfUpgrade(), 20));
				k.add(qty(new PotionOfStrength(), 10));
				k.add(qty(new Ankh(), 3));
				break;
			case "warrior":
				k.add(ench(new Greatsword(), new Kinetic(), 10));
				k.add(glyph(new PlateArmor(), new Thorns(), 10));
				k.add(lvl(new RingOfMight(), 5));
				k.add(lvl(new RingOfTenacity(), 5));
				k.add(qty(new PotionOfHealing(), 10));
				break;
			case "mage":
				k.add(lvl(new MagesStaff(new WandOfMagicMissile()), 10));
				k.add(lvl(new WandOfDisintegration(), 10));
				k.add(lvl(new WandOfFireblast(), 10));
				k.add(lvl(new WandOfFrost(), 10));
				k.add(lvl(new WandOfLightning(), 10));
				k.add(lvl(new WandOfCorruption(), 10));
				k.add(lvl(new RingOfEnergy(), 5));
				k.add(lvl(new RingOfArcana(), 5));
				break;
			case "rogue":
				k.add(ench(new AssassinsBlade(), new Vampiric(), 10));
				k.add(glyph(new LeatherArmor(), new Swiftness(), 10));
				k.add(lvl(new CloakOfShadows(), 10));
				k.add(lvl(new RingOfEvasion(), 5));
				k.add(lvl(new RingOfHaste(), 5));
				break;
			case "huntress":
				k.add(ench(new SpiritBow(), new Projecting(), 10));
				k.add(qty(lvl(new ThrowingKnife(), 5), 20));
				k.add(glyph(new LeatherArmor(), new Camouflage(), 10));
				k.add(lvl(new RingOfSharpshooting(), 5));
				k.add(lvl(new RingOfFuror(), 5));
				break;
			case "duelist":
				k.add(ench(new Katana(), new Lucky(), 10));
				k.add(ench(new Greatshield(), new Blocking(), 10));
				k.add(lvl(new RingOfForce(), 5));
				k.add(lvl(new RingOfFuror(), 5));
				k.add(qty(new PotionOfHealing(), 10));
				break;
			case "cleric":
				k.add(lvl(new HolyTome(), 10));
				k.add(ench(new WarHammer(), new Corrupting(), 10));
				k.add(glyph(new PlateArmor(), new AntiMagic(), 10));
				k.add(lvl(new RingOfWealth(), 5));
				break;
			case "alchemist":
				k.add(lvl(new AlchemistsToolkit(), 10));
				k.add(qty(new EnergyCrystal(), 2000));
				for (Class<?> c : Generator.Category.POTION.classes) k.add(qty((Item) Reflection.newInstance(c), 10));
				for (Class<?> c : Generator.Category.SEED.classes) k.add(qty((Item) Reflection.newInstance(c), 10));
				break;
			case "bomber":
				k.add(qty(new Bomb(), 10));
				k.add(qty(new Firebomb(), 10));
				k.add(qty(new FrostBomb(), 10));
				k.add(qty(new FlashBangBomb(), 10));
				k.add(qty(new HolyBomb(), 10));
				k.add(qty(new ShrapnelBomb(), 10));
				k.add(qty(new RegrowthBomb(), 10));
				k.add(qty(new WoollyBomb(), 10));
				k.add(qty(new SmokeBomb(), 10));
				k.add(qty(new Noisemaker(), 10));
				break;
			case "explorer":
				k.add(lvl(new TalismanOfForesight(), 10));
				k.add(qty(new ScrollOfMagicMapping(), 10));
				k.add(qty(new ScrollOfTeleportation(), 10));
				k.add(qty(new PotionOfMindVision(), 10));
				k.add(qty(new Torch(), 10));
				break;
		}
		return k;
	}
}
