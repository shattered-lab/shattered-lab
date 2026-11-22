/*
 * Shattered Lab
 * Search for any item in the game by name.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

public class WndItemSearch {

	private static final int MAX_RESULTS = 80;

	public static void open(){
		GameScene.show(new WndTextInput(
				Messages.get(WndItemSearch.class, "title"),
				Messages.get(WndItemSearch.class, "body"),
				"", 30, false,
				Messages.get(WndItemSearch.class, "find"),
				Messages.get(WndItemSearch.class, "cancel")){
			@Override
			public void onSelect(boolean positive, String text) {
				if (positive) {
					lastQuery = text == null ? "" : text.trim();
					showResults(lastQuery);
				}
			}
		});
	}

	private static String lastQuery = "";

	private static void showResults(String query){
		String q = query.toLowerCase(Locale.ROOT);

		final ArrayList<Item> samples = new ArrayList<>();
		final ArrayList<LibraryCatalog.Maker> makers = new ArrayList<>();
		HashSet<String> seen = new HashSet<>();

		for (LibraryCatalog.Section s : LibraryCatalog.allSections()){
			for (ArrayList<LibraryCatalog.Maker> row : s.rows){
				for (LibraryCatalog.Maker m : row){
					Item sample = m.make();
					if (sample == null) continue;
					String sig = LibraryLevel.signature(sample);
					if (!seen.add(sig)) continue;
					String name = sample.name().toLowerCase(Locale.ROOT);
					String cls = sample.getClass().getSimpleName().toLowerCase(Locale.ROOT);
					if (q.isEmpty() || name.contains(q) || cls.contains(q)){
						samples.add(sample);
						makers.add(m);
						if (samples.size() >= MAX_RESULTS) break;
					}
				}
			}
		}

		if (samples.isEmpty()){
			GameScene.show(new WndMessage(Messages.get(WndItemSearch.class, "nothing", query)));
			return;
		}

		ArrayList<WndPick.Entry> entries = new ArrayList<>();
		for (Item i : samples){
			entries.add(new WndPick.Entry(new ItemSprite(i), Messages.titleCase(i.name())));
		}
		String title = q.isEmpty()
				? Messages.get(WndItemSearch.class, "all", samples.size())
				: Messages.get(WndItemSearch.class, "found", samples.size());
		GameScene.show(new WndPick(title, entries){
			@Override
			protected void onPick(int index) {
				GameScene.show(new WndLibraryItem(samples.get(index), makers.get(index)));
			}
		});
	}
}
