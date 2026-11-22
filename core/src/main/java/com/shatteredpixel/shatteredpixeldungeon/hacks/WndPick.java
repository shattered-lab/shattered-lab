/*
 * Shattered Lab
 * Simple scrolling list window: title + rows (icon, text), tapping a row calls onPick.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public abstract class WndPick extends Window {

	private static final int WIDTH_P = 120;
	private static final int WIDTH_L = 160;

	public static class Entry {
		public final Image icon;
		public final String text;
		public final boolean title;
		public Entry(Image icon, String text){
			this.icon = icon; this.text = text; this.title = false;
		}
		public Entry(String title){
			this.icon = null; this.text = title; this.title = true;
		}
	}

	public WndPick(String title, final ArrayList<Entry> entries){
		super();

		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

		RenderedTextBlock tl = PixelScene.renderTextBlock(title, 9);
		tl.hardlight(TITLE_COLOR);
		tl.maxWidth(width);
		tl.setPos((width - tl.width())/2f, 2);
		add(tl);

		ScrollingListPane list = new ScrollingListPane();
		add(list);

		for (int i = 0; i < entries.size(); i++){
			final Entry e = entries.get(i);
			if (e.title){
				list.addTitle(e.text);
				continue;
			}
			final int index = i;
			list.addItem(new ScrollingListPane.ListItem(e.icon, null, e.text){
				@Override
				public boolean onClick(float x, float y) {
					if (inside(x, y)){
						onPick(index);
						return true;
					}
					return false;
				}
			});
		}

		int height = Math.min(Math.max(80, entries.size() * 18 + 20),
				(int) (PixelScene.uiCamera.height * 0.75f));
		resize(width, height);
		float top = tl.bottom() + 3;
		list.setRect(0, top, width, height - top);
	}

	protected abstract void onPick(int index);
}
