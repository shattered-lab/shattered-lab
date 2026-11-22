/*
 * Shattered Lab
 * What happens at the start of every run.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.QuickSlot;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.hacks.items.BagOfHolding;
import com.shatteredpixel.shatteredpixeldungeon.hacks.items.HackerGrimoire;
import com.shatteredpixel.shatteredpixeldungeon.hacks.items.LibraryCard;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.watabou.utils.Reflection;

public class HackStart {

	public static void onNewHero(Hero hero) {

		//the newbie tutorial hides the toolbar until floor 1 - not needed in the mod
		SPDSettings.intro(false);

		//the whole guidebook is already read - the red book and floor pages no longer appear
		for (String page : Document.ADVENTURERS_GUIDE.pageNames()) {
			Document.ADVENTURERS_GUIDE.findPage(page);
			Document.ADVENTURERS_GUIDE.readPage(page);
		}

		//the hero knows all potions, scrolls and rings
		Generator.Category[] cats = {
				Generator.Category.POTION, Generator.Category.SCROLL, Generator.Category.RING};
		for (Generator.Category cat : cats) {
			for (Class<?> cls : cat.classes) {
				Item i = (Item) Reflection.newInstance(cls);
				if (i != null) i.identify();
			}
		}

		new BagOfHolding().collect();

		HackCheats.getOrCreate();

		LibraryCard card = new LibraryCard();
		card.collect();
		HackerGrimoire grimoire = new HackerGrimoire();
		grimoire.collect();
		quickslot(card);
	}

	private static void quickslot(Item item){
		for (int s = 0; s < QuickSlot.SIZE; s++) {
			if (Dungeon.quickslot.getItem(s) == null) {
				Dungeon.quickslot.setSlot(s, item);
				return;
			}
		}
	}
}
