/*
 * Shattered Lab
 * Scroll of mega upgrade: +10 item levels at once, with no risk of losing the enchantment.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.hacks.WndWorkshop;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.InventoryScroll;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class ScrollOfMegaUpgrade extends InventoryScroll {

	public static final int AMOUNT = 10;

	{
		preferredBag = Belongings.Backpack.class;
		talentChance = 0;
	}

	@Override
	public void reset() {
		super.reset();
		image = ItemSpriteSheet.HACK_SCROLL_MEGA;
	}

	@Override
	public boolean isKnown() {
		return true;
	}

	@Override
	protected boolean usableOnItem(Item item) {
		return item.isUpgradable() || item instanceof Artifact;
	}

	@Override
	protected void onItemSelected(Item item) {
		WndWorkshop.setLevel(item, item.level() + AMOUNT);
		curUser.sprite.emitter().start(Speck.factory(Speck.UP), 0.2f, 5);
		GLog.p(Messages.get(this, "upgraded", item.name(), item.level()));

		//player request: keep the item list open while scrolls are left in the stack.
		//closing the list stops, the remaining scrolls stay in the backpack
		ScrollOfMegaUpgrade next = curUser.belongings.getItem(ScrollOfMegaUpgrade.class);
		if (next != null) {
			curItem = next;
			GameScene.selectItem(next.itemSelector);
		}
	}

	@Override
	public int value() {
		return 300 * quantity;
	}

	@Override
	public int energyVal() {
		return 20 * quantity;
	}
}
