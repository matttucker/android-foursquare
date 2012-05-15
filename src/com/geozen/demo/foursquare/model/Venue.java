package com.geozen.demo.foursquare.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.geozen.demo.foursquare.model.Category;

public class Venue implements FoursquareType {

	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String CATEGORIES = "categories";
	public static final String LOCATION = "location";

	public String mId;
	public String mName;
	public Category[] mCategories;
	public Location mLocation;

	// private Category mCategory;

	public Venue() {
		mId = "";
		mName = "";
	}

	public Venue(JSONObject json) throws JSONException {

		mId = json.getString(ID);
		mName = json.getString(NAME);

		JSONArray jsonCats = json.getJSONArray(CATEGORIES);
		mCategories = new Category[jsonCats.length()];

		for (int i = 0; i < mCategories.length; i++) {
			mCategories[i] = new Category(jsonCats.getJSONObject(i));
		}

		mLocation = new Location(json.getJSONObject(LOCATION));
	}

}
