package com.geozen.demo.foursquare.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

public class Location {
	public static final String ADDRESS = "address";
	public static final String CITY = "city";
	public static final String COUNTRY = "country";
	public static final String CROSS_STREET = "crossStreet";
	public static final String DISTANCE = "distance";
	public static final String LAT = "lat";
	public static final String LNG = "lng";
	public static final String POSTAL_CODE = "postalCode";
	public static final String STATE = "state";

	// "location": {
	// "address": "469 W State Highway 7",
	// "city": "Broomfield",
	// "country": "United States",
	// "crossStreet": "I-25 and Hwy 7",
	// "distance": 614,
	// "lat": 40.00107209967364,
	// "lng": -104.99293502682174,
	// "postalCode": "80023",
	// "state": "CO"
	// },

	public String mAddress;
	public String mCity;
	public String mCountry;
	public String mCrossStreet;
	public String mPostalCode;
	public String mState;
	public int mDistance;
	public double mLat;
	public double mLng;

	public Location(JSONObject json) throws JSONException {
		if (json.has(ADDRESS)) {
			mAddress = json.getString(ADDRESS);
		} else {
			mAddress = "";
		}
		if (json.has(CITY)) {
			mCity = json.getString(CITY);
		} else {
			mCity = "";
		}
		if (json.has(COUNTRY)) {
			mCountry = json.getString(COUNTRY);
		} else {
			mCountry = "";
		}
		if (json.has(CROSS_STREET)) {
			mCrossStreet = json.getString(CROSS_STREET);
		} else {
			mCrossStreet = "";
		}
		if (json.has(POSTAL_CODE)) {
			mPostalCode = json.getString(POSTAL_CODE);
		} else {
			mPostalCode = "";
		}
		if (json.has(STATE)) {
			mState = json.getString(STATE);
		} else {
			mState = "";
		}
		if (json.has(DISTANCE)) {
			mDistance = json.getInt(DISTANCE);
		} else {
			mDistance = 0;
		}
		if (json.has(LAT)) {
			mLat = json.getDouble(LAT);
		} else {
			mLat = 0.0d;
		}
		if (json.has(LNG)) {
			mLng = json.getDouble(LNG);
		} else {
			mLng = 0.0d;
		}
	}

	public String getVenueLocationCrossStreetOrCity() {
		if (!TextUtils.isEmpty(mCrossStreet)) {
			return "(" + mCrossStreet + ")";
		} else if (!TextUtils.isEmpty(mCity) && !TextUtils.isEmpty(mState)
				&& !TextUtils.isEmpty(mPostalCode)) {
			return mCity + ", " + mState + " " + mPostalCode;
		} else {
			return null;
		}
	}
}
