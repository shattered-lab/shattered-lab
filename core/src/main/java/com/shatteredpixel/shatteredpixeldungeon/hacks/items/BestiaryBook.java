/*
 * Shattered Lab
 * Bestiary: a book for summoning monsters (lies on the arena lectern).
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.hacks.WndSummon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;

public class BestiaryBook extends Item {

	public static final String AC_SUMMON = "SUMMON";

	{
		image = ItemSpriteSheet.HACK_BESTIARY;
		unique = true;
		stackable = false;
		bones = false;
		defaultAction = AC_SUMMON;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_SUMMON);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (action.equals(AC_SUMMON)) {
			GameScene.show(new WndSummon());
		}
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public int value() {
		return 0;
	}
}
