/*
 * Shattered Lab
 * Scroll of degradation: the reverse of upgrade, -1 to the chosen item's level.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.InventoryScroll;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class ScrollOfDegradation extends InventoryScroll {

	{
		preferredBag = Belongings.Backpack.class;
		talentChance = 0;
	}

	@Override
	public void reset() {
		super.reset();
		image = ItemSpriteSheet.HACK_SCROLL_DEGRADE;
	}

	@Override
	public boolean isKnown() {
		return true;
	}

	@Override
	protected boolean usableOnItem(Item item) {
		return item.isUpgradable();
	}

	@Override
	protected void onItemSelected(Item item) {
		item.degrade();
		item.levelKnown = true;
		Item.updateQuickslot();
		curUser.updateHT(false);
		curUser.sprite.emitter().burst(ShadowParticle.UP, 8);
		GLog.w(Messages.get(this, "degraded", item.name(), item.level()));
	}

	@Override
	public int value() {
		return 10 * quantity;
	}

	@Override
	public int energyVal() {
		return 2 * quantity;
	}
}
