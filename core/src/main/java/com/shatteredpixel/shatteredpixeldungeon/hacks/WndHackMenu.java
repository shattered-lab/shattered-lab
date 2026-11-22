/*
 * Shattered Lab
 * Main menu of the lab grimoire. One column on phones, two on PC.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.hacks.items.HackerGrimoire;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class WndHackMenu extends Window {

	private static final int BTN_H = 22;
	private static final int GAP = 2;

	private static class Entry {
		final Image icon; final String key; final Runnable action;
		Entry(Image icon, String key, Runnable action){ this.icon = icon; this.key = key; this.action = action; }
	}

	public WndHackMenu(){
		super();

		boolean wide = PixelScene.landscape();
		int cols = wide ? 2 : 1;
		int width = wide ? 250 : 130;

		IconTitle title = new IconTitle(new ItemSprite(ItemSpriteSheet.HACK_GRIMOIRE),
				Messages.titleCase(new HackerGrimoire().name()));
		title.setRect(0, 0, width, 0);
		add(title);
		float top = title.bottom() + 4;

		ArrayList<Entry> entries = new ArrayList<>();
		entries.add(new Entry(Icons.get(Icons.MAGNIFY), "search", new Runnable(){ public void run(){ WndItemSearch.open(); }}));
		entries.add(new Entry(new ItemSprite(ItemSpriteSheet.SWORD), "workshop", new Runnable(){ public void run(){ WndWorkshop.selectAndOpen(); }}));
		entries.add(new Entry(new ItemSprite(ItemSpriteSheet.HACK_GRIMOIRE), "cheats", new Runnable(){ public void run(){ GameScene.show(new WndCheats()); }}));
		entries.add(new Entry(new ItemSprite(ItemSpriteSheet.STONE_BLINK), "teleport", new Runnable(){ public void run(){ HackTeleport.select(); }}));
		entries.add(new Entry(Icons.get(Icons.STAIRS), "lift", new Runnable(){ public void run(){ GameScene.show(new WndLift()); }}));
		entries.add(new Entry(new ItemSprite(ItemSpriteSheet.ARTIFACT_TALISMAN), "floor", new Runnable(){ public void run(){ GameScene.show(new WndFloor()); }}));
		entries.add(new Entry(new ItemSprite(ItemSpriteSheet.ARTIFACT_HOURGLASS), "snapshots", new Runnable(){ public void run(){ GameScene.show(new WndSnapshots()); }}));
		entries.add(new Entry(new ItemSprite(ItemSpriteSheet.HACK_BESTIARY), "summon", new Runnable(){ public void run(){ GameScene.show(new WndSummon()); }}));
		entries.add(new Entry(HeroSprite.avatar(Dungeon.hero), "hero", new Runnable(){ public void run(){ GameScene.show(new WndHeroEditor()); }}));
		entries.add(new Entry(Icons.get(Icons.TALENT), "talents", new Runnable(){ public void run(){ GameScene.show(new WndTalentsCheat()); }}));
		entries.add(new Entry(new ItemSprite(ItemSpriteSheet.BACKPACK), "kits", new Runnable(){ public void run(){ GameScene.show(new WndKits()); }}));

		float bw = (width - (cols - 1) * GAP) / (float) cols;
		for (int i = 0; i < entries.size(); i++){
			final Entry e = entries.get(i);
			int col = i % cols, row = i / cols;
			RedButton b = new RedButton(Messages.get(this, e.key), 8){
				@Override
				protected void onClick() {
					hide();
					e.action.run();
				}
			};
			b.icon(e.icon);
			b.leftJustify = true;
			b.setRect(col * (bw + GAP), top + row * (BTN_H + GAP), bw, BTN_H);
			add(b);
		}

		int rows = (entries.size() + cols - 1) / cols;
		resize(width, (int) (top + rows * (BTN_H + GAP)));
	}
}
