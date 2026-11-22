/*
 * Shattered Lab
 * Crash reports via Firebase Crashlytics. Compiled only in the Firebase build
 * (build_apk.sh, FIREBASE=1); the launcher loads the class via reflection.
 * Collection is off by default in the manifest (firebase_crashlytics_collection_enabled=false)
 * and is turned on only after the player agrees.
 */

package com.shatteredpixel.shatteredpixeldungeon.android;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.watabou.utils.CrashReports;

public class FirebaseReports extends CrashReports {

	private final FirebaseCrashlytics crashlytics;

	public FirebaseReports(){
		//throws IllegalStateException if FirebaseApp did not start (no google_app_id etc.)
		crashlytics = FirebaseCrashlytics.getInstance();
	}

	@Override
	public void setEnabled(boolean enabled) {
		crashlytics.setCrashlyticsCollectionEnabled(enabled);
		if (enabled) {
			crashlytics.sendUnsentReports();
		} else {
			crashlytics.deleteUnsentReports();
		}
	}

	@Override
	public void nonFatal(Throwable t) {
		crashlytics.recordException(t);
	}

	@Override
	public void key(String key, String value) {
		crashlytics.setCustomKey(key, value);
	}

	@Override
	public void log(String message) {
		crashlytics.log(message);
	}
}
