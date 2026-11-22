/*
 * Shattered Lab
 * Bridge to the crash report service. On Android the launcher plugs it in (Firebase Crashlytics),
 * in other builds service == null and all calls do nothing.
 */

package com.watabou.utils;

public abstract class CrashReports {

	public static CrashReports service;

	public static boolean available(){
		return service != null;
	}

	public abstract void setEnabled( boolean enabled );
	public abstract void nonFatal( Throwable t );
	public abstract void key( String key, String value );
	public abstract void log( String message );

	public static void enable( boolean enabled ){
		if (service == null) return;
		try { service.setEnabled(enabled); } catch (Throwable ignored){}
	}

	public static void report( Throwable t ){
		if (service == null || t == null) return;
		try { service.nonFatal(t); } catch (Throwable ignored){}
	}

	public static void setKey( String key, Object value ){
		if (service == null) return;
		try { service.key(key, String.valueOf(value)); } catch (Throwable ignored){}
	}

	public static void breadcrumb( String message ){
		if (service == null || message == null) return;
		try { service.log(message); } catch (Throwable ignored){}
	}
}
