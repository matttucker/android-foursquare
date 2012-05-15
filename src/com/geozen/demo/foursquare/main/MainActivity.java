/**
 * @author matt@geozen.com
 */

package com.geozen.demo.foursquare.main;

import static com.geozen.demo.foursquare.app.Config.DEBUG;
import static com.geozen.demo.foursquare.app.Config.LOGTAG;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.geozen.demo.foursquare.R;
import com.geozen.demo.foursquare.app.GeoZenApplication;

public class MainActivity extends Activity {

	private static GeoZenApplication mApp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mApp = (GeoZenApplication) getApplication();
		// Don't start the main activity if we don't have credentials
		if (!mApp.isReady()) {
			if (DEBUG)
				Log.d(LOGTAG, "Not ready for user.");
			redirectToSignInActivity();
			return;
		}

		setContentView(R.layout.main);

	}

	private void redirectToSignInActivity() {
		Intent intent = new Intent(this, SigninActivity.class);
		intent.setAction(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.signout:
			((GeoZenApplication) getApplication()).signout();
			MainActivity.this.finish();
			return true;
		}

		return false;
	}
}
