package com.geozen.demo.foursquare.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Checkin implements FoursquareType {

	public static final String ID = "id";
	public static final String TYPE = "type";
	public static final String PRIVATE = "private";
	public static final String USER = "user";
	public static final String VENUE = "venue";
	public static final String CREATED_AT = "createdAt";

	public String mId;

	public Venue mVenue;
	public long mCreatedAt;

	// public Location mLocation;

	public Checkin() {
		mId = "";
	}

	public Checkin(JSONObject json) throws JSONException {

		mId = json.getString(ID);

		if (json.has(VENUE)) {
			mVenue = new Venue(json.getJSONObject(VENUE));
		}
		if (json.has(CREATED_AT)) {
			mCreatedAt = json.getLong(CREATED_AT) * 1000L;
		}

		// mLocation = new Location(json.getJSONObject(LOCATION));
	}

}
