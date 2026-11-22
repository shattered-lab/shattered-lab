/*
 * Shattered Lab
 * Run snapshots: save the current moment and roll back to it, like in an emulator.
 * A snapshot is a copy of the save folder (game<N>) into game<N>_snap<K>.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks;

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.ColorBlock;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WndSnapshots extends Window {

	public static final int SLOTS = 3;
	private static final int BTN_H = 18;
	private static final int GAP = 2;
	private static final String INFO = "snapinfo.dat";

	public WndSnapshots(){
		super();

		int width = PixelScene.landscape() ? 170 : 130;

		RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(this, "title"), 9);
		title.hardlight(TITLE_COLOR);
		title.setPos((width - title.width())/2f, 2);
		add(title);

		RenderedTextBlock hint = PixelScene.renderTextBlock(Messages.get(this, "hint"), 6);
		hint.maxWidth(width);
		hint.setPos(0, title.bottom() + 3);
		add(hint);

		float pos = hint.bottom() + 5;

		for (int k = 1; k <= SLOTS; k++){
			final int snap = k;
			Bundle info = readInfo(snap);

			ColorBlock sep = new ColorBlock(width, 1, 0xFF222222);
			sep.y = pos;
			add(sep);
			pos += 3;

			RenderedTextBlock label = PixelScene.renderTextBlock(
					Messages.get(this, "slot", snap) + "  " + describe(info), 7);
			label.maxWidth(width);
			label.hardlight(info != null ? 0xFFFFFF : 0x999999);
			label.setPos(0, pos);
			add(label);
			pos = label.bottom() + 3;

			float bw = (width - 2) / 3f;
			RedButton save = new RedButton(Messages.get(this, "save"), 7){
				@Override
				protected void onClick() {
					if (readInfo(snap) != null){
						GameScene.show(new WndOptions(Icons.get(Icons.WARNING),
								Messages.get(WndSnapshots.class, "overwrite_title"),
								Messages.get(WndSnapshots.class, "overwrite_body"),
								Messages.get(WndSnapshots.class, "yes"),
								Messages.get(WndSnapshots.class, "no")){
							@Override
							protected void onSelect(int index) {
								if (index == 0) { WndSnapshots.this.hide(); save(snap); }
							}
						});
					} else {
						hide();
						save(snap);
					}
				}
			};
			save.setRect(0, pos, bw, BTN_H);
			add(save);

			RedButton load = new RedButton(Messages.get(this, "load"), 7){
				@Override
				protected void onClick() {
					GameScene.show(new WndOptions(Icons.get(Icons.WARNING),
							Messages.get(WndSnapshots.class, "load_title"),
							Messages.get(WndSnapshots.class, "load_body"),
							Messages.get(WndSnapshots.class, "yes"),
							Messages.get(WndSnapshots.class, "no")){
						@Override
						protected void onSelect(int index) {
							if (index == 0) { WndSnapshots.this.hide(); load(snap); }
						}
					});
				}
			};
			load.enable(info != null);
			load.setRect(bw + 1, pos, bw, BTN_H);
			add(load);

			RedButton del = new RedButton(Messages.get(this, "delete"), 7){
				@Override
				protected void onClick() {
					FileUtils.deleteDir(snapFolder(snap));
					hide();
					GameScene.show(new WndSnapshots());
				}
			};
			del.enable(info != null);
			del.setRect(2 * (bw + 1), pos, bw, BTN_H);
			add(del);

			pos += BTN_H + GAP + 2;
		}

		resize(width, (int) pos);
	}

	private static String snapFolder(int snap){
		return GamesInProgress.gameFolder(GamesInProgress.curSlot) + "_snap" + snap;
	}

	private static Bundle readInfo(int snap){
		String f = snapFolder(snap) + "/" + INFO;
		if (!FileUtils.fileExists(f)) return null;
		try {
			return FileUtils.bundleFromFile(f);
		} catch (Exception e) {
			return null;
		}
	}

	private static String describe(Bundle info){
		if (info == null) return Messages.get(WndSnapshots.class, "empty");
		return Messages.get(WndSnapshots.class, "info",
				info.getInt("depth"), info.getInt("level"), info.getString("time"));
	}

	//copies all files in the folder (a save is only files, no subfolders)
	private static void copyFiles(String from, String to){
		FileHandle src = FileUtils.getFileHandle(from);
		FileHandle dst = FileUtils.getFileHandle(to);
		if (dst.exists()) dst.deleteDirectory();
		dst.mkdirs();
		for (FileHandle f : src.list()){
			if (!f.isDirectory()) f.copyTo(dst.child(f.name()));
		}
	}

	private static void save(int snap){
		try {
			Dungeon.saveAll();
			copyFiles(GamesInProgress.gameFolder(GamesInProgress.curSlot), snapFolder(snap));

			Bundle info = new Bundle();
			info.put("depth", Dungeon.depth);
			info.put("level", Dungeon.hero.lvl);
			info.put("turns", (int) Statistics.duration);
			info.put("time", new SimpleDateFormat("dd.MM HH:mm", Locale.ROOT).format(new Date()));
			FileUtils.bundleToFile(snapFolder(snap) + "/" + INFO, info);

			GLog.p(Messages.get(WndSnapshots.class, "saved", snap));
		} catch (Exception e) {
			ShatteredPixelDungeon.reportException(e);
			GLog.n(Messages.get(WndSnapshots.class, "error"));
		}
	}

	private static void load(int snap){
		try {
			int slot = GamesInProgress.curSlot;
			copyFiles(snapFolder(snap), GamesInProgress.gameFolder(slot));
			FileUtils.deleteFile(GamesInProgress.gameFolder(slot) + "/" + INFO);
			GamesInProgress.setUnknown(slot);

			ActionIndicator.clearAction();
			InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
			ShatteredPixelDungeon.switchScene(InterlevelScene.class);
		} catch (Exception e) {
			ShatteredPixelDungeon.reportException(e);
			GLog.n(Messages.get(WndSnapshots.class, "error"));
		}
	}
}
