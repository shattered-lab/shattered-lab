/*
 * Shattered Lab
 * Catalog of the floor 0 library: which items go in which section.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks;

import com.shatteredpixel.shatteredpixeldungeon.hacks.items.BagOfHolding;
import com.shatteredpixel.shatteredpixeldungeon.hacks.items.BestiaryBook;
import com.shatteredpixel.shatteredpixeldungeon.hacks.items.HackerGrimoire;
import com.shatteredpixel.shatteredpixeldungeon.hacks.items.ScrollOfMegaUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.hacks.items.WandOfEverything;
import com.shatteredpixel.shatteredpixeldungeon.hacks.items.LibraryCard;
import com.shatteredpixel.shatteredpixeldungeon.hacks.items.ScrollOfDegradation;
import com.shatteredpixel.shatteredpixeldungeon.hacks.items.ScrollOfPolymorph;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.ArcaneResin;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KingsCrown;
import com.shatteredpixel.shatteredpixeldungeon.items.LiquidMetal;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClericArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.DuelistArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.HuntressArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.LeatherArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.MageArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.MailArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.RogueArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ScaleArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.WarriorArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.AlchemistsToolkit;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CapeOfThorns;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.EtherealChains;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.LloydsBeacon;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SandalsOfNature;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.UnstableSpellbook;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.ScrollHolder;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.ArcaneBomb;
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
import com.shatteredpixel.shatteredpixeldungeon.items.food.Berry;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Blandfruit;
import com.shatteredpixel.shatteredpixeldungeon.items.food.ChargrilledMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.FrozenCarpaccio;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MeatPie;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Pasty;
import com.shatteredpixel.shatteredpixeldungeon.items.food.PhantomMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.SmallRation;
import com.shatteredpixel.shatteredpixeldungeon.items.food.StewedMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.SupplyRation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.AquaBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.BlizzardBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.CausticBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.InfernalBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.ShockingBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.UnstableBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfAquaticRejuvenation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfArcaneArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfDragonsBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfFeatherFall;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfHoneyedHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfIcyTouch;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfToxicEssence;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CeremonialCandle;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CorpseDust;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DarkGold;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Embers;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.GooBlob;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.BowFragment;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.BrokenHilt;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.BrokenStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.CloakScrap;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.SealShard;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.TornPage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Alchemize;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.BeaconOfReturning;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.CurseInfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.MagicalInfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.PhaseShift;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.ReclaimTrap;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Recycle;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.SummonElemental;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.TelekineticGrab;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.UnstableSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.WildEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.AssassinsBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.BattleAxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Crossbow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Cudgel;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dirk;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Flail;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gauntlet;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Glaive;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gloves;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greataxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatshield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.HandAxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Katana;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Longsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Mace;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Quarterstaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Rapier;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RoundShield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RunicBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sai;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Scimitar;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Shortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sickle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Spear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WarHammer;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WarScythe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Whip;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Bolas;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.FishingSpear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ForceCube;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.HeavyBoomerang;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Javelin;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Kunai;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Shuriken;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingClub;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingHammer;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingSpear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingSpike;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingStone;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Tomahawk;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Trident;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.AdrenalineDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.BlindingDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.ChillingDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.CleansingDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.DisplacingDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.HealingDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.HolyDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.IncendiaryDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.ParalyticDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.PoisonDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.RotDart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.ShockingDart;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;

public class LibraryCatalog {

	//creates a new item instance for the shelf (and for giving to the player)
	public interface Maker {
		Item make();
	}

	public static class Section {
		public final String key;
		public final ArrayList<ArrayList<Maker>> rows = new ArrayList<>();

		Section(String key){
			this.key = key;
		}

		Section row(Maker... makers){
			rows.add(new ArrayList<>(Arrays.asList(makers)));
			return this;
		}

		@SafeVarargs
		final Section rowOf(Class<? extends Item>... classes){
			ArrayList<Maker> r = new ArrayList<>();
			for (Class<? extends Item> c : classes) r.add(of(c));
			rows.add(r);
			return this;
		}

		Section rowArr(Class<?>[] classes){
			ArrayList<Maker> r = new ArrayList<>();
			for (Class<?> c : classes) r.add(of((Class<? extends Item>) c));
			rows.add(r);
			return this;
		}
	}

	public static Maker of(final Class<? extends Item> cls){
		return new Maker() {
			@Override
			public Item make() {
				return prepare(Reflection.newInstance(cls));
			}
		};
	}

	//all library items are identified and uncursed (except the ones cursed on purpose)
	public static Item prepare(Item item){
		if (item == null) return null;
		item.cursed = false;
		item.identify(false);
		return item;
	}

	private static Maker enchanted(final Class<?> enchant, final boolean curse){
		return new Maker() {
			@Override
			public Item make() {
				Weapon w = new Sword();
				w.enchant((Weapon.Enchantment) Reflection.newInstance(enchant));
				w.identify(false);
				w.cursed = curse;
				return w;
			}
		};
	}

	private static Maker inscribed(final Class<?> glyph, final boolean curse){
		return new Maker() {
			@Override
			public Item make() {
				Armor a = new MailArmor();
				a.inscribe((Armor.Glyph) Reflection.newInstance(glyph));
				a.identify(false);
				a.cursed = curse;
				return a;
			}
		};
	}

	private static Maker staff(){
		return new Maker() {
			@Override
			public Item make() {
				return prepare(new MagesStaff(new WandOfMagicMissile()));
			}
		};
	}

	private static Class<?>[] concat(Class<?>[]... arrays){
		ArrayList<Class<?>> all = new ArrayList<>();
		for (Class<?>[] a : arrays) all.addAll(Arrays.asList(a));
		return all.toArray(new Class<?>[0]);
	}

	private static ArrayList<Maker> makers(Class<?>[] classes){
		ArrayList<Maker> r = new ArrayList<>();
		for (Class<?> c : classes) r.add(of((Class<? extends Item>) c));
		return r;
	}

	//all library sections - for item search
	public static ArrayList<Section> allSections(){
		ArrayList<Section> all = new ArrayList<>();
		all.add(mod());
		all.add(potions());
		all.add(scrolls());
		all.add(alchemy());
		all.add(stonesSeeds());
		all.add(wandsRings());
		all.add(artifacts());
		all.add(melee());
		all.add(missilesArmor());
		all.add(foodBombs());
		all.add(misc());
		all.add(quest());
		all.add(enchants());
		all.add(glyphs());
		return all;
	}

	// ===== Sections =====
	// section keys match text keys: hacks.librarylevel.<key>

	public static Section mod(){
		return new Section("sec_mod")
				.rowOf(HackerGrimoire.class, ScrollOfMegaUpgrade.class, ScrollOfDegradation.class,
						ScrollOfPolymorph.class, WandOfEverything.class, LibraryCard.class,
						BagOfHolding.class, BestiaryBook.class)
				.rowOf(LloydsBeacon.class, CapeOfThorns.class);
	}

	public static Section artifacts(){
		Section s = new Section("sec_artifacts").rowOf(
				AlchemistsToolkit.class, ChaliceOfBlood.class, CloakOfShadows.class, DriedRose.class,
				EtherealChains.class, HolyTome.class, HornOfPlenty.class, MasterThievesArmband.class,
				SandalsOfNature.class, SkeletonKey.class, TalismanOfForesight.class,
				TimekeepersHourglass.class, UnstableSpellbook.class);
		ArrayList<Class<?>> trinkets = new ArrayList<>(Arrays.asList(Generator.Category.TRINKET.classes));
		trinkets.add(TrinketCatalyst.class);
		int half = (trinkets.size()+1)/2;
		s.rowArr(trinkets.subList(0, half).toArray(new Class<?>[0]));
		s.rowArr(trinkets.subList(half, trinkets.size()).toArray(new Class<?>[0]));
		return s;
	}

	public static Section wandsRings(){
		return new Section("sec_wands_rings")
				.rowArr(Generator.Category.WAND.classes)
				.rowArr(Generator.Category.RING.classes);
	}

	public static Section melee(){
		Section s = new Section("sec_melee");
		ArrayList<Maker> t1 = new ArrayList<>(Arrays.asList(
				of(WornShortsword.class), of(Gloves.class), of(Dagger.class), staff(),
				of(Rapier.class), of(Cudgel.class), of(SpiritBow.class)));
		s.rows.add(t1);
		s.rowOf(Shortsword.class, HandAxe.class, Spear.class, Quarterstaff.class, Dirk.class, Sickle.class);
		s.rowOf(Sword.class, Mace.class, Scimitar.class, RoundShield.class, Sai.class, Whip.class);
		s.rowOf(Longsword.class, BattleAxe.class, Flail.class, RunicBlade.class, AssassinsBlade.class, Crossbow.class, Katana.class);
		s.rowOf(Greatsword.class, WarHammer.class, Glaive.class, Greataxe.class, Greatshield.class, Gauntlet.class, WarScythe.class);
		return s;
	}

	public static Section missilesArmor(){
		return new Section("sec_missiles_armor")
				.rowOf(ThrowingStone.class, ThrowingKnife.class, ThrowingSpike.class, FishingSpear.class,
						ThrowingClub.class, Shuriken.class, ThrowingSpear.class, Kunai.class)
				.rowOf(Bolas.class, Javelin.class, Tomahawk.class, HeavyBoomerang.class,
						Trident.class, ThrowingHammer.class, ForceCube.class)
				.rowOf(ClothArmor.class, LeatherArmor.class, MailArmor.class, ScaleArmor.class, PlateArmor.class,
						WarriorArmor.class, MageArmor.class, RogueArmor.class, HuntressArmor.class,
						DuelistArmor.class, ClericArmor.class);
	}

	public static Section enchants(){
		Section s = new Section("sec_enchants");
		ArrayList<Maker> good = new ArrayList<>();
		for (Class<?> c : concat(Weapon.Enchantment.common, Weapon.Enchantment.uncommon)) good.add(enchanted(c, false));
		ArrayList<Maker> rest = new ArrayList<>();
		for (Class<?> c : Weapon.Enchantment.rare) rest.add(enchanted(c, false));
		for (Class<?> c : Weapon.Enchantment.curses) rest.add(enchanted(c, true));
		s.rows.add(good);
		s.rows.add(rest);
		return s;
	}

	public static Section stonesSeeds(){
		return new Section("sec_stones_seeds")
				.rowArr(Generator.Category.STONE.classes)
				.rowArr(Generator.Category.SEED.classes)
				.rowOf(Dart.class, AdrenalineDart.class, BlindingDart.class, ChillingDart.class, CleansingDart.class,
						DisplacingDart.class, HealingDart.class, HolyDart.class, IncendiaryDart.class,
						ParalyticDart.class, PoisonDart.class, RotDart.class, ShockingDart.class);
	}

	public static Section alchemy(){
		return new Section("sec_alchemy")
				.rowOf(ElixirOfAquaticRejuvenation.class, ElixirOfArcaneArmor.class, ElixirOfDragonsBlood.class,
						ElixirOfFeatherFall.class, ElixirOfHoneyedHealing.class, ElixirOfIcyTouch.class,
						ElixirOfMight.class, ElixirOfToxicEssence.class)
				.rowOf(AquaBrew.class, BlizzardBrew.class, CausticBrew.class, InfernalBrew.class,
						ShockingBrew.class, UnstableBrew.class, ArcaneBomb.class)
				.rowOf(Alchemize.class, BeaconOfReturning.class, CurseInfusion.class, MagicalInfusion.class,
						PhaseShift.class, ReclaimTrap.class, Recycle.class, SummonElemental.class,
						TelekineticGrab.class, UnstableSpell.class, WildEnergy.class, EnergyCrystal.class);
	}

	public static Section potions(){
		Section s = new Section("sec_potions");
		ArrayList<Maker> reg = new ArrayList<>(), exo = new ArrayList<>();
		for (Class<?> c : Generator.Category.POTION.classes){
			reg.add(of((Class<? extends Item>) c));
			Class<?> e = ExoticPotion.regToExo.get(c);
			if (e != null) exo.add(of((Class<? extends Item>) e));
		}
		s.rows.add(reg);
		s.rows.add(exo);
		return s;
	}

	public static Section scrolls(){
		Section s = new Section("sec_scrolls");
		ArrayList<Maker> reg = new ArrayList<>(), exo = new ArrayList<>();
		for (Class<?> c : Generator.Category.SCROLL.classes){
			reg.add(of((Class<? extends Item>) c));
			Class<?> e = ExoticScroll.regToExo.get(c);
			if (e != null) exo.add(of((Class<? extends Item>) e));
		}
		s.rows.add(reg);
		s.rows.add(exo);
		return s;
	}

	public static Section foodBombs(){
		return new Section("sec_food_bombs")
				.rowOf(Food.class, Pasty.class, SmallRation.class, SupplyRation.class, MeatPie.class,
						MysteryMeat.class, ChargrilledMeat.class, StewedMeat.class, FrozenCarpaccio.class,
						PhantomMeat.class, Berry.class, Blandfruit.class)
				.rowOf(Bomb.class, Firebomb.class, FlashBangBomb.class, FrostBomb.class, HolyBomb.class,
						Noisemaker.class, RegrowthBomb.class, ShrapnelBomb.class, SmokeBomb.class, WoollyBomb.class,
						Honeypot.class);
	}

	public static Section glyphs(){
		Section s = new Section("sec_glyphs");
		ArrayList<Maker> good = new ArrayList<>();
		for (Class<?> c : concat(Armor.Glyph.common, Armor.Glyph.uncommon, Armor.Glyph.rare)) good.add(inscribed(c, false));
		ArrayList<Maker> bad = new ArrayList<>();
		for (Class<?> c : Armor.Glyph.curses) bad.add(inscribed(c, true));
		s.rows.add(good);
		s.rows.add(bad);
		return s;
	}

	public static Section misc(){
		return new Section("sec_misc")
				.rowOf(Gold.class, EnergyCrystal.class, Ankh.class, Torch.class, ArcaneResin.class,
						LiquidMetal.class, Stylus.class, Waterskin.class, Dewdrop.class,
						DriedRose.Petal.class, TimekeepersHourglass.sandBag.class, KingsCrown.class,
						TengusMask.class, BrokenSeal.class)
				.rowOf(Amulet.class, VelvetPouch.class, PotionBandolier.class, ScrollHolder.class,
						MagicalHolster.class);
	}

	public static Section quest(){
		return new Section("sec_quest")
				.rowOf(Pickaxe.class, DarkGold.class, CorpseDust.class, Embers.class, CeremonialCandle.class,
						GooBlob.class, MetalShard.class, DwarfToken.class)
				.rowOf(BowFragment.class, BrokenHilt.class, BrokenStaff.class, CloakScrap.class,
						SealShard.class, TornPage.class);
	}
}
