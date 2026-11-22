/*
 * Shattered Lab
 * Item window: description + "take" buttons. Used by library shelves and search.
 * The player always gets a new copy, the original stays on the shelf.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem;

public class WndLibraryItem extends WndInfoItem {

	private static final float GAP = 2;
	private static final int BTN_HEIGHT = 18;

	public WndLibraryItem(final Heap heap, final LibraryLevel.Shelf shelf) {
		this(heap.peek(), shelf.maker);
	}

	public WndLibraryItem(final Item sample, final LibraryCatalog.Maker maker) {
		super(sample);

		int[] amounts;
		if (sample instanceof Gold || sample instanceof EnergyCrystal) {
			amounts = new int[]{100, 1000, 10000};
		} else if (sample.stackable) {
			amounts = new int[]{1, 10, 100};
		} else {
			amounts = new int[]{1};
		}

		float pos = height + GAP;
		float bw = (width - (amounts.length - 1)) / (float) amounts.length;
		for (int i = 0; i < amounts.length; i++) {
			final int n = amounts[i];
			String label;
			if (amounts.length == 1) {
				label = Messages.get(this, "take");
			} else if (sample instanceof Gold || sample instanceof EnergyCrystal) {
				label = "+" + n;
			} else {
				label = Messages.get(this, "take_n", n);
			}
			RedButton btn = new RedButton(label) {
				@Override
				protected void onClick() {
					hide();
					take(maker, n);
				}
			};
			btn.setRect(i * (bw + 1), pos, bw, BTN_HEIGHT);
			add(btn);
		}
		pos += BTN_HEIGHT;

		resize(width, (int) pos);
	}

	public static void take(LibraryCatalog.Maker maker, int n) {
		Hero hero = Dungeon.hero;
		Item item = maker.make();
		if (item == null) return;
		//dew does not stack in SPD (Dewdrop.quantity caps it at 1): x10/x100 go straight into the waterskin
		if (item instanceof Dewdrop && n > 1) {
			Waterskin flask = hero.belongings.getItem(Waterskin.class);
			if (flask != null && !flask.isFull()) {
				final int amount = n;
				HackCheats.markUsed();
				flask.collectDew(new Dewdrop(){ { quantity = amount; } });
				return;
			}
		}
		if (item.stackable) {
			item.quantity(n);
		}
		HackCheats.markUsed();
		String name = item.name();
		if (item.doPickUp(hero)) {
			if (!(item instanceof Gold || item instanceof EnergyCrystal)) {
				GLog.i(Messages.capitalize(Messages.get(Hero.class, "you_now_have", name))
						+ (n > 1 ? " x" + n : ""));
			}
		} else {
			GLog.n(Messages.get(WndLibraryItem.class, "no_room"));
		}
	}
}
