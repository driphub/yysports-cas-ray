/**
 * 
 */
package com.yysports.cas.login.utils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author RAY
 *
 */
@Slf4j
public class RestUtils {

	private RestUtils() {
	}

	private RestUtils(Builder clientBuilder) {
		super();
		this.clientBuilder = clientBuilder;
	}

	private enum HttpType {
		GET, DELETE, POST, PUT;
	}

	private static OkHttpClient okHttpClient = new OkHttpClient();
	/**
	 * MediaType
	 */
	private static final MediaType MEDIATYPE_JSON = MediaType
			.parse("application/json; charset=utf-8");

	/**
	 * Gson
	 */
	private static final Gson GSON = new Gson();

	private OkHttpClient.Builder clientBuilder = null;

	/**
	 * 
	 * @param allowTrust 加密連線
	 * @return
	 */
	public static RestUtils createRest(boolean allowTrust) {
		Builder builder = okHttpClient.newBuilder().readTimeout(30, TimeUnit.SECONDS);
		if (!allowTrust) {
			return new RestUtils(builder);
		}

		final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				X509Certificate[] cArrr = new X509Certificate[0];
				return cArrr;
			}

			@Override
			public void checkServerTrusted(final X509Certificate[] chain, final String authType)
					throws CertificateException {
			}

			@Override
			public void checkClientTrusted(final X509Certificate[] chain, final String authType)
					throws CertificateException {
			}
		} };

		SSLContext sslContext;
		try {
			sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
			builder.sslSocketFactory(sslContext.getSocketFactory(),
					((X509TrustManager) trustAllCerts[0]));
		} catch (NoSuchAlgorithmException ex) {
			log.error("NoSuchAlgorithmException while sslContext.init ex: {}", ex);
			throw new RuntimeException("NoSuchAlgorithmException while sslContext.init", ex);
		} catch (KeyManagementException ex) {
			log.error("KeyManagementException while sslContext.init ex: {}", ex);
			throw new RuntimeException("KeyManagementException while sslContext.init", ex);
		}

		HostnameVerifier hostnameVerifier = new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		builder.hostnameVerifier(hostnameVerifier);

		return new RestUtils(builder);
	}

	/**
	 * 建立一個GET REQUEST
	 * 
	 * @param <T> 自定義類
	 * @param url URL
	 * @param t   class
	 * @return
	 */
	public <T> T createGet(String url, Class<T> t) {
		return execute(url, HttpType.GET, null, t);
	}

	/**
	 * 建立一個POST，並傳入資料
	 * 
	 * @param <T>  自定義類
	 * @param url  URL
	 * @param data data
	 * @param t    class
	 * @return
	 */
	public <T> T createPost(String url, Object data, Class<T> t) {
		return execute(url, HttpType.POST, data, t);
	}

	/**
	 * 建立一個PUT，並傳入資料
	 * 
	 * @param <T>  自定義類
	 * @param url  URL
	 * @param data data
	 * @param t    class
	 * @return
	 */
	public <T> T createPut(String url, Object data, Class<T> t) {
		return execute(url, HttpType.PUT, data, t);
	}

	/**
	 * 建立一個DELETE，並傳入資料
	 * 
	 * @param <T>  自定義類
	 * @param url  URL
	 * @param data data
	 * @param t    class
	 * @return
	 */
	public <T> T createDelete(String url, Object data, Class<T> t) {
		return execute(url, HttpType.DELETE, data, t);
	}
	/**
	 * 建立一個DELETE
	 * 
	 * @param <T>  自定義類
	 * @param url  URL
	 * @param t    class
	 * @return
	 */
	public <T> T createDelete(String url, Class<T> t) {
		return createDelete(url, null, t);
	}

	@SuppressWarnings("unchecked")
	private <T> T execute(String url, HttpType type, Object data, Class<T> t) {

		okhttp3.Request.Builder builder = new Request.Builder().url(url).header("Cache-Control",
				"no-cache");
		RequestBody body = null;
		if (data != null) {
			body = RequestBody.create(MEDIATYPE_JSON, GSON.toJson(data));
		}
		Request request = null;
		switch (type) {
			case GET:
				request = builder.get().build();
				break;
			case POST:
				request = builder.post(body).build();
				break;
			case PUT:
				request = builder.put(body).build();
				break;
			case DELETE:
				request = builder.delete().build();
				if (data != null) {
					request = builder.delete(body).build();
				}
				break;
			default:
				throw new RuntimeException("RestUtils HttpType type undefinded");
		}

		try {
			Response response = clientBuilder.build().newCall(request).execute();
			if (response.isSuccessful()) {
				String respStr = response.body().string();
				if (t.equals(String.class)) {
					return (T) respStr;
				}
				if (respStr != null) {
					return GSON.fromJson(respStr, t);
				}
			}
			return null;
		} catch (IOException e) {
			log.info("create request cause exception, url: {}, params: {}, ex: {}", url, data);
			return null;
		}
	}
}
