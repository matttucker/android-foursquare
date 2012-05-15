/**
 * @author matt@geozen.com
 */

package com.geozen.demo.foursquare.app;

import static com.geozen.demo.foursquare.app.Config.DEBUG;
import static com.geozen.demo.foursquare.app.Config.LOGTAG;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.geozen.demo.foursquare.jiramot.Foursquare;

public class GeoZenApplication extends Application {

	public static final String PACKAGE_NAME = "com.geozen.demo.foursquare";
	private static final String GEOZEN_PREF_FILE = "/data/data/com.geozen.demo.foursquare/shared_prefs/com.geozen.app.prefs.xml";

	static public boolean mZoneEditMode;

	public SharedPreferences mPrefs;

	public Foursquare mFoursquare = new Foursquare(Secret.CLIENT_ID,
			Secret.CLIENT_SECRET, "http://www.geozen.com");

	private static GeoZenApplication mInstance;

	@Override
	public void onCreate() {

		Log.i(LOGTAG, "Using Debug Log:\t" + DEBUG);
		mInstance = this;

		mPrefs = getSharedPreferences(Preferences.PREFS_NAME, MODE_PRIVATE);

		String access_token = mPrefs.getString(Preferences.ACCESS_TOKEN, null);
		if (access_token != null) {
			mFoursquare.setAccessToken(access_token);
		}

	}

	public static Context getContext() {
		return mInstance;
	}

	public boolean isReady() {
		return mFoursquare.isSessionValid();
	}

	public boolean isFirstRun() {
		File file = new File(GEOZEN_PREF_FILE);
		return !file.exists();
	}

	public void signout() {
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putString(Preferences.ACCESS_TOKEN, null);
		editor.putLong("access_expires", 0);
		editor.commit();
		mFoursquare.setAccessToken(null);
	}

	public boolean isConnected() {

		boolean connected = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();

			if (netInfo != null) {
				connected = netInfo.isConnected();
			}
		}
		return connected;
	}

	public static GeoZenApplication getInstance() {

		return mInstance;
	}

}
