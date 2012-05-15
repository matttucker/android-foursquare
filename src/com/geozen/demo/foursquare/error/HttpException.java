package com.geozen.demo.foursquare.error;

class HttpException extends Exception {
	private static final long serialVersionUID = 1L;

	private String mExtra;

	public HttpException(String message) {
		super(message);
	}

	public HttpException(String message, String extra) {
		super(message);
		mExtra = extra;
	}

	public String getExtra() {
		return mExtra;
	}
}
