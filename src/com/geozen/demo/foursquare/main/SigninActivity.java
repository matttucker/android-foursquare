/**
 * @author matt@geozen.com
 */

package com.geozen.demo.foursquare.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import com.geozen.demo.foursquare.R;
import com.geozen.demo.foursquare.app.GeoZenApplication;
import com.geozen.demo.foursquare.jiramot.DialogError;
import com.geozen.demo.foursquare.jiramot.FoursquareError;
import com.geozen.demo.foursquare.jiramot.Foursquare.DialogListener;

public class SigninActivity extends Activity {

	private GeoZenApplication mApp;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.signin);

		mApp = (GeoZenApplication) getApplication();

		if (mApp.mFoursquare.isSessionValid()) {
			// We're authed so go directly to the site.
			Intent intent = new Intent(this, MainActivity.class);

			startActivity(intent);
			finish();
		}

		ImageButton connect = (ImageButton) findViewById(R.id.connectButton);
		connect.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mApp.mFoursquare.authorize(SigninActivity.this,
						new FoursquareAuthenDialogListener());
			}
		});

	}

	public void onPause() {

		mApp.mFoursquare.dismissDialog();
		super.onPause();
	}

	private class FoursquareAuthenDialogListener implements DialogListener {

		public void onComplete(Bundle values) {
			SharedPreferences.Editor editor = mApp.mPrefs.edit();
			String access_token = mApp.mFoursquare.getAccessToken();
			editor.putString("access_token", access_token);
			editor.commit();

			Intent intent = new Intent(SigninActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}

		public void onFoursquareError(FoursquareError error) {
			Toast.makeText(SigninActivity.this, error.getMessage(),
					Toast.LENGTH_LONG).show();

		}

		public void onError(DialogError error) {
			Toast.makeText(SigninActivity.this, error.getMessage(),
					Toast.LENGTH_LONG).show();

		}

		public void onCancel() {

		}

	}

}
