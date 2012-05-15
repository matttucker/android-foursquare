/**
 * Copyright 2009 Joe LaPenna
 */

package com.geozen.demo.foursquare.error;

/**
 * @author Joe LaPenna (joe@joelapenna.com)
 */
public class GeoZenException extends Exception {
	private static final long serialVersionUID = 1L;

	private String mExtra;

	public GeoZenException(String message) {
		super(message);
	}

	public GeoZenException(String message, String extra) {
		super(message);
		mExtra = extra;
	}

	public String getExtra() {
		return mExtra;
	}
}
