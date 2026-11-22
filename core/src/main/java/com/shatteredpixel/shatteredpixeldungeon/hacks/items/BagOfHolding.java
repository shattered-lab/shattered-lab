/*
 * Shattered Lab
 * Bag of holding: +20 slots for any items.
 * Fills only when the main backpack is full, so it does not mess up the usual layout.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

public class BagOfHolding extends Bag {

	{
		image = ItemSpriteSheet.HACK_BAG_OF_HOLDING;
		unique = true;
	}

	private boolean restoring = false;

	@Override
	public boolean canHold(Item item) {
		if (item instanceof Bag) {
			return false;
		}
		//own contents on load
		if (restoring) {
			return super.canHold(item);
		}
		//while the backpack is loading, the bag must not take its items
		if (Belongings.bundleRestoring) {
			return false;
		}
		if (Dungeon.hero == null || items.contains(item)) {
			return super.canHold(item);
		}

		//the stack is already in the bag - add to it there
		if (item.stackable) {
			for (Item i : items) {
				if (item.isSimilar(i)) return super.canHold(item);
			}
		}

		Belongings.Backpack bp = Dungeon.hero.belongings.backpack;

		//the stack is already in the backpack - let it merge there
		if (item.stackable) {
			for (Item i : bp.items) {
				if (item.isSimilar(i)) return false;
			}
		}

		//special bags (for seeds, scrolls etc.) come first
		for (Item i : bp.items) {
			if (i instanceof Bag && !(i instanceof BagOfHolding) && ((Bag) i).canHold(item)) {
				return false;
			}
		}

		//while the backpack has room - put it there
		if (bp.items.size() < bp.capacity()) {
			return false;
		}

		return super.canHold(item);
	}

	@Override
	public int capacity() {
		return 20;
	}

	@Override
	public int value() {
		return 50;
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		restoring = true;
		super.restoreFromBundle(bundle);
		restoring = false;
	}
}
