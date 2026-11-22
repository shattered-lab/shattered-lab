/*
 * Shattered Lab
 * Crash reports: on by default, the player gets a notice on first launch,
 * can be turned off in settings. Plus game state keys for the report and a test crash.
 * The service itself (Firebase Crashlytics) is plugged in only in the Android build, see CrashReports.
 */

package com.shatteredpixel.shatteredpixeldungeon.hacks;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.utils.CrashReports;
import com.watabou.utils.FileUtils;

public class HackReports {

	private static final String KEY_CONSENT = "shd_reports";
	//NOTICE: notice not shown yet (reports pile up on the phone), YES: sending on, NO: sending off
	public static final int NOTICE = 0, YES = 1, NO = 2;

	//flag file to test sending: "crash" - real crash, "nonfatal" - error without a crash
	public static final String TEST_FLAG = "shd_crashtest";

	public static int consent(){
		return SPDSettings.getInt(KEY_CONSENT, NOTICE, NOTICE, NO);
	}

	public static boolean enabled(){
		return consent() != NO;
	}

	public static void setConsent(boolean yes){
		SPDSettings.put(KEY_CONSENT, yes ? YES : NO);
		CrashReports.enable(yes);
	}

	//called by the launcher right after the service is plugged in
	public static void applyConsent(){
		CrashReports.setKey("mod_version", Game.version + " (" + Game.versionCode + ")");
		int c = consent();
		if (c == YES)     CrashReports.enable(true);
		else if (c == NO) CrashReports.enable(false);
		//NOTICE: nothing is sent before the notice, reports go out after it
	}

	public static void onTitle(PixelScene scene){
		if (!CrashReports.available()) return;
		CrashReports.breadcrumb("title");

		if (consent() == NOTICE){
			scene.addToFront(new WndOptions(Icons.get(Icons.DATA),
					Messages.get(HackReports.class, "title"),
					Messages.get(HackReports.class, "body"),
					Messages.get(HackReports.class, "ok")){
				@Override
				protected void onSelect(int index) {
					acknowledged();
				}

				@Override
				public void onBackPressed() {
					super.onBackPressed();
					acknowledged();
				}
			});
		} else {
			maybeTestCrash();
		}
	}

	private static void acknowledged(){
		if (consent() != NOTICE) return;
		setConsent(true);
		maybeTestCrash();
	}

	public static void onGameScene(){
		if (!CrashReports.available() || Dungeon.hero == null) return;
		CrashReports.setKey("depth", Dungeon.depth + (Dungeon.branch != 0 ? "/" + Dungeon.branch : ""));
		CrashReports.setKey("hero", Dungeon.hero.heroClass.name()
				+ (Dungeon.hero.subClass != HeroSubClass.NONE ? "/" + Dungeon.hero.subClass.name() : ""));
		CrashReports.setKey("hero_lvl", Dungeon.hero.lvl);
		CrashReports.setKey("challenges", Dungeon.challenges);
		CrashReports.setKey("cheats", HackCheats.used());
		HackCheats c = HackCheats.get();
		CrashReports.setKey("cheat_flags", c == null ? "-" :
				(c.god ? "god " : "") + (c.oneShot ? "oneshot " : "") + (c.noHunger ? "nohunger " : "")
				+ (c.revealMap ? "map " : "") + (c.infCharges ? "charges " : "") + (c.infTalents ? "talents" : ""));
		CrashReports.setKey("hp", Dungeon.hero.HP + "/" + Dungeon.hero.HT);
		CrashReports.setKey("str", Dungeon.hero.STR());
		CrashReports.setKey("seed", Dungeon.seed);
		CrashReports.setKey("custom_seed", !Dungeon.customSeedText.isEmpty());
		CrashReports.breadcrumb("game depth " + Dungeon.depth);
	}

	//test fires only after consent; on refusal the flag is just deleted
	private static void maybeTestCrash(){
		if (!FileUtils.fileExists(TEST_FLAG)) return;
		if (consent() != YES){
			if (consent() == NO) FileUtils.deleteFile(TEST_FLAG);
			return;
		}
		String mode;
		try {
			mode = FileUtils.getFileHandle(TEST_FLAG).readString().trim();
		} catch (Exception e){
			mode = "";
		}
		FileUtils.deleteFile(TEST_FLAG);
		if (mode.equals("crash")){
			throw new RuntimeException("Shattered Lab: test crash");
		} else if (mode.equals("nonfatal")){
			Game.reportException(new IllegalStateException("Shattered Lab: test non-fatal"));
		}
	}
}
