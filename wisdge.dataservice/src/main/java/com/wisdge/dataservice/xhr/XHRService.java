package com.wisdge.dataservice.xhr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import com.wisdge.dataservice.exceptions.IllegalUrlException;
import com.wisdge.utils.DomUtils;

/**
 * XHR数据接口服务类
 *
 * @author Kevin MOU
 */
public class XHRService {

	private final static Log logger = LogFactory.getLog(XHRService.class);

	public static final int RESULT_XML = 0;
	public static final int RESULT_JSON = 1;

	private static final String POST_JSON = "JSON";
	private static final String POST_XML = "XML";

	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";

	/**
	 * 创建SSL连接
	 *
	 * @throws Exception
	 */
	private static SSLConnectionSocketFactory createSSLConnSocketFactory() throws Exception {
		// 创建TrustManager
		X509TrustManager xtm = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		// TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext
		SSLContext ctx = SSLContext.getInstance("TLS");
		// SSLContext ctx = SSLContext.getInstance("TLSv1");

		// 使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
		ctx.init(null, new TrustManager[] { xtm }, null);
	    return new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);
	}

	/**
	 * 为httpClient赋予SSL安全连接特性
	 *
	 * @param proxyConfig 代理服务配置
	 * @return CloseableHttpClient
	 * @throws Exception
	 */
	public static CloseableHttpClient getSSLClient(ProxyConfig proxyConfig) {
		try {
			SSLConnectionSocketFactory sslsf = createSSLConnSocketFactory();
			HttpClientBuilder clientBuilder = HttpClients.custom().setSSLSocketFactory(sslsf);
			if (proxyConfig != null) {
				HttpHost proxyHost = new HttpHost(proxyConfig.getHost(), proxyConfig.getPort());
				DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxyHost);
				return clientBuilder.setRoutePlanner(routePlanner).build();
			} else {
				return clientBuilder.build();
			}
		} catch (Exception e) {
			logger.error(e, e);
			return HttpClients.createDefault();
		}
	}

	public static CloseableHttpClient getSSLClient() {
		return getSSLClient(null);
	}

	public static String get(String url) throws IOException, IllegalUrlException {
		return get(url, null, null);
	}

	public static String get(String url, Map<String, Object> params) throws IOException, IllegalUrlException {
		return get(url, params, null);
	}

	/**
	 * 从XHR接口通过GET方法获得字符串对象值
	 *
	 * @param url
	 *            HTTP接口地址
	 * @param params
	 *            GET参数的MAP对象
	 * @param proxyConfig
	 *            Proxy配置
	 * @return String
	 * @throws IOException
	 * @throws IllegalUrlException
	 */
	public static String get(String url, Map<String, Object> params, ProxyConfig proxyConfig) throws IllegalUrlException, IOException {
		return get(url, params, proxyConfig, null);
	}

	public static String get(String url, Map<String, Object> params, ProxyConfig proxyConfig, Map<String, String> heads) throws IllegalUrlException, IOException {
		if (null == url || !url.toLowerCase().startsWith("http")) {
			throw new IllegalUrlException(404, null);
		}

		CloseableHttpClient httpClient = getSSLClient(proxyConfig);
		try {
			CloseableHttpResponse httpResponse = getResponse(url, params, httpClient, null);
			return entity2String(httpResponse, true);
		} finally {
			httpClient.close();
		}
	}

	public static CloseableHttpResponse getResponse(String url, CloseableHttpClient httpClient) throws ClientProtocolException, IllegalUrlException, IOException {
		return getResponse(url, null, httpClient, null);
	}

	/**
	 * 从XHR接口以GET方式获得一个字符串的返回值
	 *
	 * @param url
	 *            HTTP接口的地址
	 * @param params
	 *            GET方法中提交的参数MAP对象
	 * @param httpClient
	 *            执行方法的CloseableHttpClient对象
	 * @return CloseableHttpResponse
	 * @throws IllegalUrlException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static CloseableHttpResponse getResponse(String url, Map<String, Object> params, CloseableHttpClient httpClient, Map<String, String> heads) throws IllegalUrlException, ClientProtocolException, IOException {
		if (null == url || !url.toLowerCase().startsWith("http")) {
			throw new IllegalUrlException(404, url);
		}

		url = url.trim().replaceAll("\n", "");
		int sp = url.indexOf("?");
		if (sp == -1) {
			url += "?";
		}

		if (params != null) {
			String key, value;
			Iterator<String> iter = params.keySet().iterator();
			while (iter.hasNext()) {
				key = iter.next();
				value = params.get(key).toString();
				if (!url.endsWith("?")) {
					url += "&";
				}
				url += key + "=" + encodeStringFromObject(value);
			}
		}
		logger.debug("[XHRSERVICE-GET] " + url);
		HttpGet httpGet = new HttpGet(url);
		if (heads != null) {
			Iterator<String> iter = heads.keySet().iterator();
			while(iter.hasNext()) {
				String name = iter.next();
				httpGet.setHeader(name, heads.get(name));
			}
		}
		return httpClient.execute(httpGet);
	}

	private static String encodeStringFromObject(Object object) throws UnsupportedEncodingException {
		if (object == null) {
			return "";
		} else {
			// String encodedString = URLDecoder.decode(EncodeUtils.unescape(source), "UTF-8");
			// return URLEncoder.encode(EncodeUtils.escape(object.toString()), "UTF-8");
			return URLEncoder.encode(object.toString(), "UTF-8");
		}
	}

	public static String post(String url) throws IllegalUrlException, IOException {
		return post(url, new HashMap<String, Object>(), null);
	}

	public static String post(String url, Map<String, Object> params) throws IllegalUrlException, IOException {
		return post(url, params, null);
	}

	/**
	 * 从XHR接口通过POST方法获得一个字符串对象值
	 *
	 * @param url
	 *            HTTP接口地址
	 * @param params
	 *            POST参数的MAP对象
	 * @param proxyConfig
	 *            Proxy配置
	 * @return String
	 * @throws IllegalUrlException
	 * @throws IOException
	 */
	public static String post(String url, Map<String, Object> params, ProxyConfig proxyConfig) throws IllegalUrlException, IOException {
		return post(url, params, null, null);
	}

	/**
	 * 从XHR接口通过POST方法获得一个字符串对象值
	 *
	 * @param url
	 *            HTTP接口地址
	 * @param params
	 *            POST参数的MAP对象
	 * @param proxyConfig
	 *            Proxy配置
	 * @return String
	 * @throws IllegalUrlException
	 * @throws IOException
	 */
	public static String post(String url, Map<String, Object> params, ProxyConfig proxyConfig, Map<String, String> heads) throws IllegalUrlException, IOException {
		CloseableHttpClient httpClient = getSSLClient(proxyConfig);
		try {
			CloseableHttpResponse httpResponse = postResponse(url, params, httpClient, heads);
			return entity2String(httpResponse, true);
		} finally {
			httpClient.close();
		}
	}

	public static String post(String url, String jsonBody) throws IOException, IllegalUrlException {
		return post(url, jsonBody, null);
	}


	/**
	 * 从XHR接口通过POST JSON方法获得一个字符串对象值
	 *
	 * @param url
	 *            HTTP接口地址
	 * @param jsonBody
	 *            POST到接口的JSON正文
	 * @param proxyConfig
	 *            Proxy配置
	 * @return String
	 * @throws ClientProtocolException
	 * @throws IllegalUrlException
	 * @throws IOException
	 */
	public static String post(String url, String jsonBody, ProxyConfig proxyConfig) throws ClientProtocolException, IllegalUrlException, IOException {
		return postPayload(url, jsonBody, POST_JSON, proxyConfig);
	}

	public static String postXML(String url, String xmlBody) throws ClientProtocolException, IllegalUrlException, IOException {
		return postXML(url, xmlBody, null);
	}

	/**
	 * 从XHR接口通过POST XML方法获得一个字符串对象值
	 *
	 * @param url
	 *            HTTP接口地址
	 * @param xmlBody
	 *            POST到接口的XML正文
	 * @param proxyConfig
	 *            Proxy配置
	 * @return String
	 * @throws ClientProtocolException
	 * @throws IllegalUrlException
	 * @throws IOException
	 */
	public static String postXML(String url, String xmlBody, ProxyConfig proxyConfig) throws ClientProtocolException, IllegalUrlException, IOException {
		return postPayload(url, xmlBody, POST_XML, proxyConfig);
	}

	private static String postPayload(String url, String jsonBody, String payloadType, ProxyConfig proxyConfig) throws IllegalUrlException, ClientProtocolException, IOException {
		if (null == url || !url.toLowerCase().startsWith("http")) {
			throw new IllegalUrlException(404, url);
		}

		CloseableHttpClient httpClient = getSSLClient(proxyConfig);
		try {
			CloseableHttpResponse httpResponse = postResponse(url, jsonBody, payloadType, httpClient, null);
			return entity2String(httpResponse, true);
		} finally {
			httpClient.close();
		}
	}

	public static CloseableHttpResponse postResponse(String url, CloseableHttpClient httpClient) throws ClientProtocolException, IllegalUrlException, IOException {
		return postResponse(url, null, httpClient, null);
	}

	/**
	 * 从XHR接口以POST方式获得一个字符串的返回值
	 *
	 * @param url
	 *            HTTP接口地址
	 * @param params
	 *            POST方法中提交的参数MAP对象
	 * @param httpClient
	 *            执行HTTP的CloseableHttpClient对象
	 * @return CloseableHttpResponse
	 * @throws IllegalUrlException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static CloseableHttpResponse postResponse(String url, Map<String, Object> params, CloseableHttpClient httpClient, Map<String, String> heads) throws IllegalUrlException, ClientProtocolException, IOException {
		if (null == url || !url.toLowerCase().startsWith("http")) {
			throw new IllegalUrlException(404, url);
		}

		url = url.trim().replaceAll("\n", "");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		// add parameters extend
		if (params != null) {
			Iterator<String> iter = params.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				Object value = params.get(key);
				// nvps.add(new BasicNameValuePair(key, value == null ? "" : EncodeUtils.escape(value.toString())));
				nvps.add(new BasicNameValuePair(key, value == null ? "" : value.toString()));
			}
		}
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
		return httpClient.execute(httpPost);
	}

	/**
	 * 从XHR接口以POST方式获得一个字符串的返回值
	 *
	 * @param httpPost
	 * @param httpClient
	 *            执行HTTP的CloseableHttpClient对象
	 * @return CloseableHttpResponse
	 * @throws IllegalUrlException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static CloseableHttpResponse postResponse(HttpPost httpPost, CloseableHttpClient httpClient) throws IllegalUrlException, ClientProtocolException, IOException {
		return httpClient.execute(httpPost);
	}

	/**
	 * 从XHR接口通过POST JSON方法获得CloseableHttpResponse对象
	 *
	 * @param url
	 *            HTTP接口地址
	 * @param jsonBody
	 *            POST到接口的JSON格式正文
	 * @param httpClient
	 *            执行HTTP的CloseableHttpClient对象
	 * @return CloseableHttpResponse
	 * @throws IllegalUrlException
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static CloseableHttpResponse postResponse(String url, String jsonBody, String payloadType, CloseableHttpClient httpClient, Map<String, String> heads) throws IllegalUrlException, ClientProtocolException, IOException {
		if (null == url || !url.toLowerCase().startsWith("http")) {
			throw new IllegalUrlException(404, url);
		}

		HttpPost httpPost = new HttpPost(url);
		StringEntity se = new StringEntity(jsonBody, "UTF-8");
		if (payloadType.equalsIgnoreCase(POST_JSON)) {
			httpPost.setHeader("Content-Type", "application/jaon; charset=UTF-8");
		} else if (payloadType.equalsIgnoreCase(POST_XML)) {
			httpPost.setHeader("Content-Type", "application/xml; charset=UTF-8");
		} else {
			throw new IllegalArgumentException("不识别的PAYLOAD类型-" + payloadType);
		}

		if (heads != null) {
			Iterator<String> iter = heads.keySet().iterator();
			while(iter.hasNext()) {
				String name = iter.next();
				httpPost.setHeader(name, heads.get(name));
			}
		}

		httpPost.setEntity(se);
		return httpClient.execute(httpPost, HttpClientContext.create());
	}

	/**
	 * 从HttpResponse中获得字符串返回值
	 *
	 * @param httpResponse
	 *            HttpResponse对象
	 * @return 字符串
	 * @throws IOException
	 * @throws IllegalUrlException
	 */
	public static String entity2String(HttpResponse httpResponse) throws IOException, IllegalUrlException, ParseException {
		if (httpResponse.getStatusLine().getStatusCode() != 200) {
			throw new IllegalUrlException(httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase());
		}

		// logger.debug(httpResponse.getStatusLine().getStatusCode());
		HttpEntity responseEntity = httpResponse.getEntity();
		return EntityUtils.toString(responseEntity, ContentType.getOrDefault(responseEntity).getCharset());
	}

	public static String entity2String(CloseableHttpResponse httpResponse, boolean close) throws ParseException, IOException, IllegalUrlException {
		try {
			return entity2String(httpResponse);
		} finally {
			if (close)
				httpResponse.close();
		}
	}

	public static String execute(HttpUriRequest request) throws ClientProtocolException, IOException, ParseException, IllegalUrlException {
		return execute(request, null);
	}

	public static String execute(HttpUriRequest request, ProxyConfig proxyConfig) throws ClientProtocolException, IOException, ParseException, IllegalUrlException {
		CloseableHttpClient httpClient = getSSLClient(proxyConfig);
		try {
			CloseableHttpResponse httpResponse = httpClient.execute(request);
			try {
				return entity2String(httpResponse, true);
			} finally {
				httpResponse.close();
			}
		} finally {
			httpClient.close();
		}
	}

	/**
	 * 从接口读取byte对象
	 *
	 * @param url
	 *            HTTP接口地址
	 * @param params
	 *            POST到接口的正文
	 * @param method
	 *            接口类型，GET或者POST
	 * @param proxyConfig
	 *            Proxy配置
	 * @return byte[]
	 * @throws ClientProtocolException
	 * @throws IllegalUrlException
	 * @throws IOException
	 */
	public static byte[] readBytes(String url, Map<String, Object> params, String method, ProxyConfig proxyConfig) throws ClientProtocolException, IllegalUrlException, IOException {
		if (null == url || !url.toLowerCase().startsWith("http")) {
			throw new IllegalUrlException(404, url);
		}

		CloseableHttpClient httpClient = getSSLClient(proxyConfig);
		try {
			CloseableHttpResponse httpResponse = null;
			if (method.equalsIgnoreCase(METHOD_GET)) {
				httpResponse = getResponse(url, params, httpClient, null);
			} else {
				httpResponse = postResponse(url, params, httpClient, null);
			}
			return entity2Bytes(httpResponse, true);
		} finally {
			httpClient.close();
		}
	}

	public static byte[] readBytes(String url, Map<String, Object> params, String method) throws ClientProtocolException, IllegalUrlException, IOException {
		return readBytes(url, params, method, null);
	}

	public static byte[] entity2Bytes(HttpResponse httpResponse) throws IllegalUrlException, UnsupportedOperationException, IOException {
		if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			throw new IllegalUrlException(httpResponse.getStatusLine().getStatusCode(), "[" + httpResponse.getStatusLine().getStatusCode() + "]");
		}

		InputStream is = httpResponse.getEntity().getContent();
		try {
			// System.out.println("HttpClient data service, size: " + is.available());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int l = -1;
			byte[] tmp = new byte[1024];
			while ((l = is.read(tmp)) != -1) {
				baos.write(tmp, 0, l);
			}
			baos.flush();
			byte[] buffer = baos.toByteArray();
			baos.close();
			// System.out.println("Receive data completely, size: " + buffer.length);
			return buffer;
		} finally {
			is.close();
		}
	}

	public static byte[] entity2Bytes(CloseableHttpResponse httpResponse, boolean close) throws UnsupportedOperationException, IllegalUrlException, IOException {
		try {
			return entity2Bytes(httpResponse);
		} finally {
			if (close)
				httpResponse.close();
		}
	}

	public static String soap(String url, String xmlBody) throws ClientProtocolException, ParseException, IOException, IllegalUrlException {
		return soap(url, xmlBody, null);
	}

	public static Element soap(String url, String methodName, String namespace, Map<String, Object> parameterMap) throws IOException, IllegalUrlException, ParseException, DocumentException {
		return soap(url, methodName, namespace, parameterMap, null);
	}

	public static Element soap(String url, String methodName, String namespace, Map<String, Object> parameterMap, ProxyConfig proxyConfig) throws ClientProtocolException, ParseException, IOException, IllegalUrlException, DocumentException {
		String xmlBody = buildWebServiceRequestData(methodName, namespace, parameterMap);
		// logger.debug(xmlBody);
		String strXML = soap(url, xmlBody, proxyConfig);
		Element bodyElement = DomUtils.parser(strXML).getRootElement().element("Body");
		Element responseElement = bodyElement.element(methodName + "Response");
		Element resultElement = responseElement.element(methodName + "Result");
		return resultElement;
	}

	public static String soap(String url, String xmlBody, ProxyConfig proxyConfig) throws ClientProtocolException, IOException, ParseException, IllegalUrlException {
		CloseableHttpClient httpClient = getSSLClient(proxyConfig);
		HttpPost httpPost = new HttpPost(url);
		StringEntity se = new StringEntity(xmlBody, "UTF-8");
		httpPost.setHeader("Content-Type", "application/soap+xml; charset=UTF-8");
		httpPost.setEntity(se);

		try {
			CloseableHttpResponse httpResponse = httpClient.execute(httpPost, HttpClientContext.create());
			return entity2String(httpResponse, true);
		} finally {
			httpClient.close();
		}
	}

	public static String buildWebServiceRequestData(String methodName, String namespace, Map<String, Object> parameterMap) {
		StringBuffer soapRequestData = new StringBuffer();
		soapRequestData.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		soapRequestData.append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
				+ " xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">");
		soapRequestData.append("<soap:Body>");
		soapRequestData.append("<" + methodName + " xmlns=\"" + namespace + "\">");

		if (parameterMap != null) {
			Set<String> nameSet = parameterMap.keySet();
			for (String name : nameSet) {
				soapRequestData.append("<" + name + ">" + parameterMap.get(name) + "</" + name + ">");
			}
		}

		soapRequestData.append("</" + methodName + ">");
		soapRequestData.append("</soap:Body>");
		soapRequestData.append("</soap:Envelope>");

		return soapRequestData.toString();
	}

	public static void closeResponse(CloseableHttpResponse httpResponse) {
		try {
			httpResponse.close();
		} catch (Exception e) {
			logger.error(e, e);
		}
	}

	public static void closeClient(CloseableHttpClient httpClient) {
		try {
			httpClient.close();
		} catch (Exception e) {
			logger.error(e, e);
		}
	}
}
