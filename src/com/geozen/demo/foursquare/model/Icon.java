package com.geozen.demo.foursquare.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;

public class Icon {
	public static final String NAME = "name";
	public static final String PREFIX = "prefix";
	public static final String SIZES = "sizes";
	
	public String mName;
	public String mPrefix;
	public int[] sizes;

	public Icon(JSONObject json) throws JSONException {

		mName = json.getString(NAME);
		mPrefix = json.getString(PREFIX);
		JSONArray jsonSizes = json.getJSONArray(SIZES);
		sizes = new int[jsonSizes.length()];
		
		for (int i = 0; i < sizes.length; i++) {
			sizes[i] = jsonSizes.getInt(i);
		}
	}
	
	 public Uri getUri() {
	    	return Uri.parse(mPrefix.replace("https", "http") + String.valueOf(sizes[0])+mName);
	    }
}
