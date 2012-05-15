package com.geozen.demo.foursquare.jiramot;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 * @author Matt Tucker
 */
public class HttpApi {
	protected static final Logger LOG = Logger.getLogger(HttpApi.class
			.getCanonicalName());
	protected static final boolean DEBUG = false;

	private static final String DEFAULT_CLIENT_VERSION = "GeoZen";
	private static final String CLIENT_VERSION_HEADER = "User-Agent";
	private static final int TIMEOUT = 10;
	static final int HTTP_PORT = 80;
	static final int SSL_PORT = 443;

	private final DefaultHttpClient mHttpClient;
	private final String mClientVersion;

	public HttpApi() {
		this(createHttpClient(), DEFAULT_CLIENT_VERSION);
	}

	public HttpApi(DefaultHttpClient httpClient, String clientVersion) {
		mHttpClient = httpClient;
		mClientVersion = clientVersion;
	}

	public HttpApi(String clientVersion) {
		this(createHttpClient(), clientVersion);
	}

	public String get(String url, List<NameValuePair> params)
			throws IOException {
		if (DEBUG)
			LOG.log(Level.FINE, "doHttpGet: " + url);
		HttpGet httpGet = createHttpGet(url, params);

		HttpResponse response = executeHttpRequest(httpGet);

		if (DEBUG)
			LOG.log(Level.FINE, "executed HttpRequest for: "
					+ httpGet.getURI().toString());

		return EntityUtils.toString(response.getEntity());

	}

	public String post(String url, List<NameValuePair> params)
			throws IOException {
		if (DEBUG)
			LOG.log(Level.FINE, "doHttpPost: " + url);
		HttpPost httpPost = createHttpPost(url, params);

		HttpResponse response = executeHttpRequest(httpPost);

		if (DEBUG)
			LOG.log(Level.FINE, "executed HttpRequest for: "
					+ httpPost.getURI().toString());

		return EntityUtils.toString(response.getEntity());

	}

	/**
	 * execute() an httpRequest catching exceptions and returning null instead.
	 * 
	 * @param httpRequest
	 * @return
	 * @throws IOException
	 */
	public HttpResponse executeHttpRequest(HttpRequestBase httpRequest)
			throws IOException {
		if (DEBUG)
			LOG.log(Level.FINE, "executing HttpRequest for: "
					+ httpRequest.getURI().toString());
		try {
			mHttpClient.getConnectionManager().closeExpiredConnections();
			return mHttpClient.execute(httpRequest);
		} catch (IOException e) {
			httpRequest.abort();
			throw e;
		}
	}

	public HttpGet createHttpGet(String url, List<NameValuePair> params) {
		if (DEBUG)
			LOG.log(Level.FINE, "creating HttpGet for: " + url);
		String query = URLEncodedUtils.format(stripNulls(params), HTTP.UTF_8);
		HttpGet httpGet = new HttpGet(url + "?" + query);
		httpGet.addHeader(CLIENT_VERSION_HEADER, mClientVersion);
		if (DEBUG)
			LOG.log(Level.FINE, "Created: " + httpGet.getURI());
		return httpGet;
	}

	public HttpPost createHttpPost(String url, List<NameValuePair> params) {
		if (DEBUG)
			LOG.log(Level.FINE, "creating HttpPost for: " + url);
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader(CLIENT_VERSION_HEADER, mClientVersion);
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(stripNulls(params),
					HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
			throw new IllegalArgumentException(
					"Unable to encode http parameters.");
		}
		if (DEBUG)
			LOG.log(Level.FINE, "Created: " + httpPost);
		return httpPost;
	}

	private List<NameValuePair> stripNulls(List<NameValuePair> params) {
		List<NameValuePair> newParams = new ArrayList<NameValuePair>();
		for (int i = 0; i < params.size(); i++) {
			NameValuePair param = params.get(i);
			if (param.getValue() != null) {
				if (DEBUG)
					LOG.log(Level.FINE, "Param: " + param);
				newParams.add(param);
			}
		}
		return newParams;
	}

	/**
	 * Create a thread-safe client. This client does not do redirecting, to
	 * allow us to capture correct "error" codes.
	 * 
	 * @return HttpClient
	 */
	public static final DefaultHttpClient createHttpClient() {
		// Sets up the http part of the service.
		final SchemeRegistry schemeRegistry = new SchemeRegistry();

		// Register the "http" protocol scheme, it is required
		// by the default operator to look up socket factories.
		// final SocketFactory sf = PlainSocketFactory.getSocketFactory();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), HTTP_PORT));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory
				.getSocketFactory(), SSL_PORT));

		// Set some client http client parameter defaults.
		final HttpParams httpParams = createHttpParams();
		HttpClientParams.setRedirecting(httpParams, false);

		final ClientConnectionManager ccm = new ThreadSafeClientConnManager(
				httpParams, schemeRegistry);
		return new DefaultHttpClient(ccm, httpParams);
	}

	/**
	 * Create the default HTTP protocol parameters.
	 */
	private static final HttpParams createHttpParams() {
		final HttpParams params = new BasicHttpParams();

		// Turn off stale checking. Our connections break all the time anyway,
		// and it's not worth it to pay the penalty of checking every time.
		HttpConnectionParams.setStaleCheckingEnabled(params, false);

		HttpConnectionParams.setConnectionTimeout(params, TIMEOUT * 1000);
		HttpConnectionParams.setSoTimeout(params, TIMEOUT * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);

		return params;
	}

}
