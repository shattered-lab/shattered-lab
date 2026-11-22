/*
 * Shattered Lab
 * Lift: instant travel to any floor 0..26.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;

public class WndLift extends Window {

	private final int WIDTH = PixelScene.landscape() ? 150 : 120;
	private static final int COLS = 5;
	private static final int BTN = 23;
	private static final int GAP = 1;

	public WndLift(){
		super();

		RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(this, "title"), 9);
		title.hardlight(TITLE_COLOR);
		title.setPos((WIDTH - title.width())/2f, 2);
		add(title);

		RenderedTextBlock hint = PixelScene.renderTextBlock(Messages.get(this, "hint"), 6);
		hint.maxWidth(WIDTH);
		hint.setPos(0, title.bottom() + 2);
		add(hint);

		float top = hint.bottom() + 4;
		float bw = (WIDTH - (COLS - 1) * GAP) / (float) COLS;

		for (int d = 0; d <= 26; d++){
			final int depth = d;
			int idx = d;
			int col = idx % COLS, row = idx / COLS;
			RedButton b = new RedButton(Integer.toString(d), 8){
				@Override
				protected void onClick() {
					hide();
					go(depth);
				}
			};
			if (d == 0){
				b.textColor(0x66FF66);
			} else if (d % 5 == 0){
				b.textColor(0xFF6666); //boss floors
			}
			b.enable(d != Dungeon.depth || Dungeon.branch != 0);
			b.setRect(col * (bw + GAP), top + row * (BTN + GAP), bw, BTN);
			add(b);
		}

		int rows = (26 / COLS) + 1;
		resize(WIDTH, (int) (top + rows * (BTN + GAP)));
	}

	public static void go(int depth){
		if (!Dungeon.interfloorTeleportAllowed()){
			GLog.w(Messages.get(WndLift.class, "preventing"));
			return;
		}
		HackCheats.markUsed();
		Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
		Level.beforeTransition();
		Invisibility.dispel();
		InterlevelScene.mode = InterlevelScene.Mode.RETURN;
		InterlevelScene.returnDepth = depth;
		InterlevelScene.returnBranch = 0;
		InterlevelScene.returnPos = depth == 0 ? LibraryLevel.SPAWN + LibraryLevel.W : -1;
		Game.switchScene(InterlevelScene.class);
	}
}
