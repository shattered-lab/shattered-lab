/*
 * Shattered Lab
 * Hacker's grimoire: all mod cheats in one item.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.hacks.WndHackMenu;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import java.util.ArrayList;

public class HackerGrimoire extends Item {

	public static final String AC_OPEN = "OPEN";

	{
		image = ItemSpriteSheet.HACK_GRIMOIRE;
		unique = true;
		stackable = false;
		bones = false;
		defaultAction = AC_OPEN;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_OPEN);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (action.equals(AC_OPEN)) {
			GameScene.show(new WndHackMenu());
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
