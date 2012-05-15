/*
 * Copyright 2010 Small Light Room CO., LTD.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.geozen.demo.foursquare.jiramot;

/**
 * Encapsulation of Foursquare.
 * 
 */

import static com.geozen.demo.foursquare.app.Config.LOGTAG;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieSyncManager;

import com.geozen.demo.foursquare.error.CredentialsException;
import com.geozen.demo.foursquare.model.Checkin;
import com.geozen.demo.foursquare.model.Venue;

public class Foursquare {

	private static final String LOGIN = "oauth";
	public static final String API_END_POING_BASE_URL = "https://api.foursquare.com/v2/";
	public static String REDIRECT_URI;
	public static final String API_URL = "https://foursquare.com/oauth2/";
	public static final String TOKEN = "access_token";
	public static final String EXPIRES = "expires_in";
	public static final String SINGLE_SIGN_ON_DISABLED = "service_disabled";
	private static final String API_VERSION_DATE = "20120202";
	public static String AUTHENTICATE_URL = "https://foursquare.com/oauth2/authenticate";// +

	private String mClientId;
	private String mClientSecret;
	private String mAccessToken = null;

	private DialogListener mAuthDialogListener;
	private HttpApi mHttpApi;
	private FoursquareDialog mDialog;

	public Foursquare(String clientId, String clientSecret, String redirectUrl) {
		if (clientId == null || clientSecret == null) {
			throw new IllegalArgumentException(
					"You must specify your application ID when instantiating "
							+ "a Foursquare object. See README for details.");
		}
		mClientId = clientId;
		mClientSecret = clientSecret;
		REDIRECT_URI = redirectUrl;

		mHttpApi = new HttpApi();

	}

	public void authorize(Context activity, final DialogListener listener) {
		mAuthDialogListener = listener;
		startDialogAuth(activity);
	}

	private void startDialogAuth(Context activity) {
		CookieSyncManager.createInstance(activity);
		Bundle params = new Bundle();
		dialog(activity, LOGIN, params, new DialogListener() {

			public void onComplete(Bundle values) {
				// ensure any cookies set by the dialog are saved
				CookieSyncManager.getInstance().sync();
				String _token = values.getString(TOKEN);
				setAccessToken(_token);
				if (isSessionValid()) {
					Log.d("Foursquare-authorize",
							"Login Success! access_token=" + getAccessToken());
					mAuthDialogListener.onComplete(values);
				} else {
					mAuthDialogListener.onFoursquareError(new FoursquareError(
							"Failed to receive access token."));
				}
			}

			public void onError(DialogError error) {
				Log.d("Foursquare-authorize", "Login failed: " + error);
				mAuthDialogListener.onError(error);
			}

			public void onFoursquareError(FoursquareError error) {
				Log.d("Foursquare-authorize", "Login failed: " + error);
				mAuthDialogListener.onFoursquareError(error);
			}

			public void onCancel() {
				Log.d("Foursquare-authorize", "Login canceled");
				mAuthDialogListener.onCancel();
			}
		});
	}

	public void dialog(Context context, String action, Bundle parameters,
			final DialogListener listener) {

		String endpoint = "";

		parameters.putString("client_id", mClientId);
		parameters.putString("display", "touch");
		if (action.equals(LOGIN)) {
			endpoint = AUTHENTICATE_URL;
			parameters.putString("client_secret", mClientSecret);
			parameters.putString("response_type", "token");
			parameters.putString("redirect_uri", REDIRECT_URI);
		}

		String url = endpoint + "?" + Util.encodeUrl(parameters);
		if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
			Util.showAlert(context, "Error",
					"Application requires permission to access the Internet");
		} else {
			mDialog = new FoursquareDialog(context, url, listener);
			mDialog.show();
		}
	}

	public void dismissDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			try {

				mDialog.dismiss();
				mDialog = null;
			} catch (Exception e) {
				Log.w(LOGTAG,
						"Could not close foursquare signup dialog correctly.");
			}

		}
	}

	public boolean isSessionValid() {
		if (getAccessToken() != null) {
			return true;
		}
		return false;
	}

	public void setAccessToken(String token) {
		mAccessToken = token;
	}

	public String getAccessToken() {
		return mAccessToken;
	}

	public Venue[] venues(float lat, float lon, List<NameValuePair> params)
			throws MalformedURLException, IOException, JSONException,
			CredentialsException {
		if (mAccessToken == null) {
			throw new CredentialsException("No access token.");
		}
		ArrayList<NameValuePair> newParams = new ArrayList<NameValuePair>(
				params);
		newParams.add(new BasicNameValuePair("ll", String.valueOf(lat) + ","
				+ String.valueOf(lon)));
		newParams.add(new BasicNameValuePair("v", API_VERSION_DATE));
		newParams.add(new BasicNameValuePair("oauth_token", mAccessToken));

		String json = mHttpApi.get(
				"https://api.foursquare.com/v2/venues/search", newParams);

		JSONObject result = new JSONObject(json);
		JSONArray jsonVenues = result.getJSONObject("response").getJSONArray(
				"venues");

		Venue[] venues = new Venue[jsonVenues.length()];

		for (int i = 0; i < venues.length; i++) {
			venues[i] = new Venue(jsonVenues.getJSONObject(i));
		}

		return venues;
	}

	public Venue venue(String id) throws MalformedURLException, IOException,
			JSONException {

		ArrayList<NameValuePair> newParams = new ArrayList<NameValuePair>();
		newParams.add(new BasicNameValuePair("v", API_VERSION_DATE));
		newParams.add(new BasicNameValuePair("oauth_token", mAccessToken));

		String json = mHttpApi.get(
				"https://api.foursquare.com/v2/venues/" + id, newParams);
		JSONObject result = new JSONObject(json);
		JSONObject jsonVenue = result.getJSONObject("response").getJSONObject(
				"venue");

		return new Venue(jsonVenue);

	}

	public Checkin[] checkins(List<NameValuePair> params)
			throws MalformedURLException, IOException, JSONException {
		ArrayList<NameValuePair> newParams = new ArrayList<NameValuePair>(
				params);
		newParams.add(new BasicNameValuePair("v", API_VERSION_DATE));
		newParams.add(new BasicNameValuePair("oauth_token", mAccessToken));

		String json = mHttpApi.get(
				"https://api.foursquare.com/v2/users/self/checkins", newParams);
		JSONObject result = new JSONObject(json);
		JSONArray jsonCheckins = result.getJSONObject("response")
				.getJSONObject("checkins").getJSONArray("items");

		Checkin[] checkins = new Checkin[jsonCheckins.length()];

		for (int i = 0; i < checkins.length; i++) {

			checkins[i] = new Checkin(jsonCheckins.getJSONObject(i));

		}

		return checkins;
	}

	public void checkin(List<NameValuePair> params)
			throws MalformedURLException, IOException, JSONException {
		ArrayList<NameValuePair> newParams = new ArrayList<NameValuePair>(
				params);
		newParams.add(new BasicNameValuePair("v", API_VERSION_DATE));
		newParams.add(new BasicNameValuePair("oauth_token", mAccessToken));
		@SuppressWarnings("unused")
		String json = mHttpApi.post(
				"https://api.foursquare.com/v2/checkins/add", newParams);

		// JSONObject result = new JSONObject(json);
	}

	public static interface DialogListener {

		/**
		 * Called when a dialog completes.
		 * 
		 * Executed by the thread that initiated the dialog.
		 * 
		 * @param values
		 *            Key-value string pairs extracted from the response.
		 */
		public void onComplete(Bundle values);

		/**
		 * Called when a Foursquare responds to a dialog with an error.
		 * 
		 * Executed by the thread that initiated the dialog.
		 * 
		 */
		public void onFoursquareError(FoursquareError e);

		/**
		 * Called when a dialog has an error.
		 * 
		 * Executed by the thread that initiated the dialog.
		 * 
		 */
		public void onError(DialogError e);

		/**
		 * Called when a dialog is canceled by the user.
		 * 
		 * Executed by the thread that initiated the dialog.
		 * 
		 */
		public void onCancel();

	}

}
