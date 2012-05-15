/**
 * Copyright 2010 Mark Wyszomierski
 */

package com.geozen.demo.foursquare.model;

import org.json.JSONException;
import org.json.JSONObject;


public class Category implements FoursquareType {

	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String ICON = "icon";
	
    /** The category's id. */
    public String mId;
    
    public String mName;

    /** Icon associated with this category. */
    public Icon mIcon;
    
   

    
    public Category() {
       
    }
    
    public Category(JSONObject json) throws JSONException {
    	mId = json.getString(ID);
    	mName = json.getString(NAME);
    	mIcon = new Icon(json.getJSONObject(ICON));
    }
    
   
}
