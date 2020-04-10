package com.wisdge.dataservice.xhr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wisdge.dataservice.exceptions.IllegalUrlException;
import com.wisdge.dataservice.exceptions.XhrException;
import com.wisdge.utils.DomUtils;

/**
 * XHR数据接口服务类
 * 
 * @author Kevin MOU
 */
public class XHRPoolService {
	private static final Logger logger = LoggerFactory.getLogger(XHRPoolService.class);
	
	private static final String XHR_GET_LOGHEAD = "[XHR-GET] ";
	private static final String XHR_POST_LOGHEAD = "[XHR-POST] ";
	private static final String UTF_8 = "UTF-8";
	private static final String CONTENTTYPE_JSON_UTF8 = "application/json;charset=UTF-8";

	public static final int RESULT_XML = 0;
	public static final int RESULT_JSON = 1;

	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_WSGET = "WSGET";
	public static final String METHOD_WSPOST = "WSPOST";
	
	private String protocal = "TLSv1.2";
	
	/**
	 * 连接超时时间 （单位毫秒）
	 */
	private int requestTimeout = 60000;
	private int connectTimeout = 30000;
	private int socketTimeout = 600000;
	
	/**
	 * 连接池最大连接数
	 */
	private int maxConnection = 300;

	/**
	 * 单个路由默认连接数
	 */
	private int maxPerRoute = 100;

	/**
	 * 连接丢失后,重试次数
	 */
	private int maxRetryCount = 3;

	private PoolingHttpClientConnectionManager connManager = null;
	private CloseableHttpClient httpClient = null;
	private IdleConnectionMonitorThread thread;
	private ProxyConfig proxyConfig;
	
	public int getRequestTimeout() {
		return requestTimeout;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}
	
	public int getSocketTimeout() {
		return socketTimeout;
	}

	public int getMaxConnection() {
		return maxConnection;
	}

	public int getMaxPerRoute() {
		return maxPerRoute;
	}

	public int getMaxRetryCount() {
		return maxRetryCount;
	}

	public String getProtocal() {
		return protocal;
	}

	public void setProtocal(String protocal) {
		this.protocal = protocal;
	}

	public XHRPoolService(int maxConnection, int maxPerRoute, int maxRetryCount, int maxTimeout, ProxyConfig proxyConfig) {
		this.maxConnection = maxConnection;
		this.maxPerRoute = maxPerRoute;
		this.maxRetryCount = maxRetryCount;
		this.requestTimeout = this.connectTimeout = this.socketTimeout = maxTimeout;
		this.proxyConfig = proxyConfig;
		initialize();
	}

	public XHRPoolService(int maxConnection, int maxPerRoute, int maxRetryCount, int maxTimeout) {
		this.maxConnection = maxConnection;
		this.maxPerRoute = maxPerRoute;
		this.maxRetryCount = maxRetryCount;
		this.requestTimeout = this.connectTimeout = this.socketTimeout = maxTimeout;
		initialize();
	}
	
	public XHRPoolService(int maxConnection, int maxPerRoute, int maxRetryCount,  int requestTimeout, int connectTimeout, int socketTimeout) {
		this.maxConnection = maxConnection;
		this.maxPerRoute = maxPerRoute;
		this.maxRetryCount = maxRetryCount;
		this.requestTimeout = requestTimeout;
		this.connectTimeout = connectTimeout;
		this.socketTimeout = socketTimeout;
		initialize();
	}
	
	public XHRPoolService(int maxConnection, int maxPerRoute, int maxRetryCount,  int requestTimeout, int connectTimeout, int socketTimeout, ProxyConfig proxyConfig) {
		this.maxConnection = maxConnection;
		this.maxPerRoute = maxPerRoute;
		this.maxRetryCount = maxRetryCount;
		this.requestTimeout = requestTimeout;
		this.connectTimeout = connectTimeout;
		this.socketTimeout = socketTimeout;
		this.proxyConfig = proxyConfig;
		initialize();
	}
	
	public XHRPoolService(int maxConnection, int maxPerRoute, int maxRetryCount,  int requestTimeout, int connectTimeout, int socketTimeout, String protocal) {
		this.maxConnection = maxConnection;
		this.maxPerRoute = maxPerRoute;
		this.maxRetryCount = maxRetryCount;
		this.requestTimeout = requestTimeout;
		this.connectTimeout = connectTimeout;
		this.socketTimeout = socketTimeout;
		this.protocal = protocal;
		initialize();
	}
	
	public XHRPoolService(int maxConnection, int maxPerRoute, int maxRetryCount,  int requestTimeout, int connectTimeout, int socketTimeout, String protocal, ProxyConfig proxyConfig) {
		this.maxConnection = maxConnection;
		this.maxPerRoute = maxPerRoute;
		this.maxRetryCount = maxRetryCount;
		this.requestTimeout = requestTimeout;
		this.connectTimeout = connectTimeout;
		this.socketTimeout = socketTimeout;
		this.protocal = protocal;
		this.proxyConfig = proxyConfig;
		initialize();
	}

	public XHRPoolService(ProxyConfig proxyConfig) {
		this.proxyConfig = proxyConfig;
		initialize();
	}

	public XHRPoolService() {
		initialize();
	}

	/**
	 * 设置HttpClient连接池
	 */
	private void initialize() {
		try {
			Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
					.register("http", PlainConnectionSocketFactory.getSocketFactory())
					.register("https", createSSLConnSocketFactory()).build();

			// 创建连接管理器
			connManager = new PoolingHttpClientConnectionManager(registry);
			connManager.setMaxTotal(maxConnection);// 设置最大连接数
			logger.debug("maxConnection: " + maxConnection);
			connManager.setDefaultMaxPerRoute(maxPerRoute);// 设置每个路由默认连接数
			logger.debug("maxPerRoute: " + maxPerRoute);
			logger.debug("maxRetryCount: " + maxRetryCount);
			

			// 设置目标主机的连接数
			// HttpHost host = new HttpHost("account.dafy.service");//针对的主机
			// connManager.setMaxPerRoute(new HttpRoute(host), 50);//每个路由器对每个服务器允许最大50个并发访问

			RequestConfig config = RequestConfig.custom()
					.setConnectionRequestTimeout(requestTimeout)// 设置从连接池获取连接实例的超时
					.setConnectTimeout(connectTimeout)// 设置建立连接超时
					.setSocketTimeout(socketTimeout)// 设置数据读取超时
					.build();
			HttpClientBuilder clientBuilder = HttpClients.custom()
					.setConnectionManager(connManager)
					.setRetryHandler(httpRequestRetry())
					.setDefaultRequestConfig(config);

			// 创建httpClient对象
			if (proxyConfig != null && proxyConfig.avaliable()) {
				HttpHost proxyHost = new HttpHost(proxyConfig.getHost(), proxyConfig.getPort());
				DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxyHost);
				httpClient = clientBuilder.setRoutePlanner(routePlanner).build();
			} else {
				httpClient = clientBuilder.build();
			}

			thread = new IdleConnectionMonitorThread(connManager);
			thread.start();

		} catch (Exception e) {
			logger.error("获取httpClient(https)对象池异常:" + e.getMessage(), e);
		}
	}

	/**
	 * 创建SSL连接
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 * 
	 * @throws Exception
	 */
	private SSLConnectionSocketFactory createSSLConnSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {
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
		SSLContext ctx = SSLContext.getInstance(protocal);
		ctx.init(null, new TrustManager[] { xtm }, null);

		return new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);
	}

	/**
	 * 配置请求连接重试机制
	 */
	private HttpRequestRetryHandler httpRequestRetry() {
		return new HttpRequestRetryHandler() {
			@Override
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				if (executionCount >= maxRetryCount) {
					// 已经重试MAX_EXECUT_COUNT次，放弃
					return false;
				}
				if (exception instanceof NoHttpResponseException) {
					// 如果服务器丢掉了连接，需要重试
					logger.error("httpclient 服务器连接丢失");
					return true;
				}
				if (exception instanceof SSLHandshakeException) {
					// SSL握手异常，无需重试
					logger.error("httpclient SSL握手异常");
					return false;
				}
				if (exception instanceof InterruptedIOException) {
					// 超时，无需重试
					logger.error("httpclient 连接超时");
					return false;
				}
				if (exception instanceof UnknownHostException) {
					// 目标服务器不可达，无需重试
					logger.error("httpclient 目标服务器不可达");
					return false;
				}
				if (exception instanceof ConnectTimeoutException) {
					// 连接被拒绝，无需重试
					logger.error("httpclient 连接被拒绝");
					return false;
				}
				if (exception instanceof SSLException) {
					// SSL异常，无需重试
					logger.error("httpclient SSL异常");
					return false;
				}

				// 如果请求是幂等的，就再次尝试
				HttpClientContext clientContext = HttpClientContext.adapt(context);
				HttpRequest request = clientContext.getRequest();
				if (!(request instanceof HttpEntityEnclosingRequest)) {
					return true;
				}
				return false;
			}
		};
	}

	/**
	 * 关闭并销毁连接池对象
	 */
	public void destroy() {
		try {
			thread.shutdown();
		} catch (Exception e) {
		}

		if (connManager == null) {
			return;
		}
		// 关闭连接池
		connManager.shutdown();
		// 设置httpClient失效
		httpClient = null;
		connManager = null;
	}

	/**
	 * 从XHR接口通过GET方法获得字符串对象值
	 * 
	 * @param url
	 *            HTTP接口地址
	 * @return String
	 * @throws IllegalUrlException 
	 * @throws IOException 
	 * @throws XhrException 
	 * @throws Exception
	 */
	public String get(String url) throws XhrException, IOException, IllegalUrlException {
		return get(url, null);
	}

	/**
	 * 从XHR接口通过GET方法获得字符串对象值
	 * 
	 * @param url
	 *            HTTP接口地址
	 * @param params
	 *            GET参数的MAP对象
	 * @return String
	 * @throws IllegalUrlException
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public String get(String url, Map<String, Object> params) throws XhrException, IOException, IllegalUrlException {
		return this.get(url, params, new HashMap<String, String>());
	}

	/**
	 * 从XHR接口通过GET方法获得字符串对象值
	 * 
	 * @param url
	 *            HTTP接口地址
	 * @param params
	 *            GET参数的MAP对象
	 * @return String
	 * @throws IllegalUrlException
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public String get(String url, Map<String, Object> params, Map<String, String> heads) throws XhrException, IOException, IllegalUrlException {
		if (null == url || !url.toLowerCase().startsWith("http")) {
			throw new IllegalUrlException(404, url);
		}
		
		url = makeGetMethodUrl(url, params);
		logger.debug(XHR_GET_LOGHEAD + url);
		HttpGet httpGet = new HttpGet(url);
		if (heads != null) {
			Iterator<String> iter = heads.keySet().iterator();
			while(iter.hasNext()) {
				String name = iter.next();
				httpGet.setHeader(name, heads.get(name));
			}
		}

		CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
		try {
			return entity2String(httpResponse, url);
		} finally {
			httpResponse.close();
			httpGet.releaseConnection();
		}
	}

	/**
	 * 从XHR接口通过GET方法处理请求
	 * @param url
	 *            HTTP接口地址
	 * @param params
	 *            GET参数的MAP对象
	 * @param handler
	 *            IResponseHandler
	 * @throws ClientProtocolException
	 * @throws IllegalUrlException
	 * @throws IOException
	 */
	public void get(String url, Map<String, Object> params, IResponseHandler handler) throws IllegalUrlException, IOException {
		if (null == url || !url.toLowerCase().startsWith("http")) {
			throw new IllegalUrlException(404, url);
		}

		url = makeGetMethodUrl(url, params);
		logger.debug(XHR_GET_LOGHEAD + url);
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
		try {
			handler.doHandle(httpResponse);
		} finally {
			httpResponse.close();
			httpGet.releaseConnection();
		}
	}
	
	public CloseableHttpResponse getResponse(String url) throws IllegalUrlException, IOException {
		return getResponse(url, null);
	}
	/**
	 * 从XHR接口通过GET方法获得CloseableHttpResponse对象，使用后务必自行close
	 * 
	 * @param url
	 *            HTTP接口地址
	 * @param params
	 *            GET参数的MAP对象
	 * @return CloseableHttpResponse
	 * @throws IllegalUrlException
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public CloseableHttpResponse getResponse(String url, Map<String, Object> params) throws IllegalUrlException, IOException {
		if (null == url || !url.toLowerCase().startsWith("http")) {
			throw new IllegalUrlException(404, url);
		}

		url = makeGetMethodUrl(url, params);
		logger.debug(XHR_GET_LOGHEAD + url);
		HttpGet httpGet = new HttpGet(url);
		return httpClient.execute(httpGet);
	}

	/**
	 * 从XHR接口通过GET方法获得byte数组
	 * 
	 * @param url
	 *            HTTP接口地址
	 * @param params
	 *            GET参数的MAP对象
	 * @return byte[]
	 * @throws IllegalUrlException
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public byte[] getForBytes(String url, Map<String, Object> params) throws IllegalUrlException, XhrException, IOException {
		if (null == url || !url.toLowerCase().startsWith("http")) {
			throw new IllegalUrlException(404, url);
		}

		url = makeGetMethodUrl(url, params);
		logger.debug(XHR_GET_LOGHEAD + url);
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
		try {
			return entity2Bytes(httpResponse, url);
		} finally {
			httpResponse.close();
			httpGet.releaseConnection();
		}
	}
	
	/** 
     * 获取response header中Content-Disposition中的filename值 
     * @param response 
     * @return 
     */  
    public String getFileName(CloseableHttpResponse response) {  
        Header contentHeader = response.getFirstHeader("Content-Disposition");  
        String filename = null;  
        if (contentHeader != null) {  
            HeaderElement[] values = contentHeader.getElements();  
            if (values.length == 1) {  
                NameValuePair param = values[0].getParameterByName("filename");  
                if (param != null) {  
                    filename = param.getValue();  
                }  
            }  
        }  
        return filename;  
    }

	private String makeGetMethodUrl(String url, Map<String, Object> params) throws UnsupportedEncodingException {
		url = url.trim().replaceAll("\n", "");
		int sp = url.indexOf('?');
		if (sp == -1) {
			url += "?";
		}

		if (params != null) {
			String key;
			String value;
			Iterator<String> iter = params.keySet().iterator();
			while (iter.hasNext()) {
				key = iter.next();
				value = params.get(key).toString();
				if (! url.endsWith("?")) {
					url += "&";
				}
				url += key + "=" + encodeStringFromObject(value);
			}
		}
		return url;
	}

	private String encodeStringFromObject(Object object) throws UnsupportedEncodingException {
		if (object == null) {
			return "";
		} else {
			return URLEncoder.encode(object.toString(), UTF_8);
		}
	}

	/**
	 * 从XHR接口以POST方式获得一个字符串的返回值
	 * 
	 * @param url
	 *            HTTP接口地址
	 * @return String
	 * @throws ClientProtocolException
	 * @throws IllegalUrlException
	 * @throws IOException
	 */
	public String post(String url) throws IllegalUrlException, XhrException, IOException {
		return post(url, new HashMap<String, Object>());
	}

	/**
	 * 从XHR接口以POST方式获得一个字符串的返回值
	 * 
	 * @param url
	 *            HTTP接口地址
	 * @param params
	 *            POST方法中提交的参数MAP对象
	 * @return String
	 * @throws IllegalUrlException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String post(String url, Map<String, Object> params) throws XhrException, IllegalUrlException, IOException {
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
				nvps.add(new BasicNameValuePair(key, value == null ? "" : value.toString()));
			}
		}

		logger.debug(XHR_POST_LOGHEAD + url);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, UTF_8));
		CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
		try {
			return entity2String(httpResponse, url);
		} finally {
			httpResponse.close();
			httpPost.releaseConnection();
		}
	}
	
	public String post(String url, Map<String, Object> params, Map<String, String> heads) throws XhrException, IllegalUrlException, IOException {
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
				nvps.add(new BasicNameValuePair(key, value == null ? "" : value.toString()));
			}
		}

		logger.debug(XHR_POST_LOGHEAD + url);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, UTF_8));
		if (heads != null) {
			Iterator<String> iter = heads.keySet().iterator();
			while(iter.hasNext()) {
				String name = iter.next();
				httpPost.setHeader(name, heads.get(name));
			}
		}
		
		CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
		try {
			return entity2String(httpResponse, url);
		} finally {
			httpResponse.close();
			httpPost.releaseConnection();
		}
	}
	
	public String post(HttpPost httpPost) throws XhrException, IOException {
		CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
		try {
			return entity2String(httpResponse, httpPost.getURI().getPath());
		} finally {
			httpResponse.close();
			httpPost.releaseConnection();
		}
	}

	/**
	 * 从XHR接口通过POST方法处理请求
	 * @param url
	 *            HTTP接口地址
	 * @param params
	 *            GET参数的MAP对象
	 * @param handler
	 *            IResponseHandler
	 * @throws ClientProtocolException
	 * @throws IllegalUrlException
	 * @throws IOException
	 */
	public void post(String url, Map<String, Object> params, IResponseHandler handler) throws IllegalUrlException, IOException {
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
				nvps.add(new BasicNameValuePair(key, value == null ? "" : value.toString()));
			}
		}

		logger.debug(XHR_POST_LOGHEAD + url);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, UTF_8));
		CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
		try {
			handler.doHandle(httpResponse);
		} finally {
			httpResponse.close();
			httpPost.releaseConnection();
		}
	}
	
	public CloseableHttpResponse postResponse(String url) throws IllegalUrlException, IOException {
		return postResponse(url, new HashMap<String, Object>());
	}

	/**
	 * 从XHR接口以POST方式获得CloseableHttpResponse对象，需自行close
	 * 
	 * @param url
	 *            HTTP接口地址
	 * @param params
	 *            POST方法中提交的参数MAP对象
	 * @return CloseableHttpResponse
	 * @throws IllegalUrlException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public CloseableHttpResponse postResponse(String url, Map<String, Object> params) throws IllegalUrlException, IOException {
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
				nvps.add(new BasicNameValuePair(key, value == null ? "" : value.toString()));
			}
		}

		logger.debug(XHR_POST_LOGHEAD + url);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, UTF_8));
		return httpClient.execute(httpPost);
	}

	public byte[] postForBytes(String url, Map<String, Object> params) throws IllegalUrlException, XhrException, IOException {
		return postForBytes(url, params, null);
	}
	
	/**
	 * 从XHR接口以POST方式获得byte数组
	 * 
	 * @param url
	 *            HTTP接口地址
	 * @param params
	 *            POST方法中提交的参数MAP对象
	 * @param heads
	 *            POST方法中提交的请求头heads
	 * @return byte[]
	 * @throws IllegalUrlException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public byte[] postForBytes(String url, Map<String, Object> params, Map<String, String> heads) throws IllegalUrlException, XhrException, IOException {
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
				nvps.add(new BasicNameValuePair(key, value == null ? "" : value.toString()));
			}
		}
		
		logger.debug(XHR_POST_LOGHEAD + url);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, UTF_8));
		if (heads != null) {
			Iterator<String> iter = heads.keySet().iterator();
			while(iter.hasNext()) {
				String name = iter.next();
				httpPost.setHeader(name, heads.get(name));
			}
		}
		CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
		try {
			return entity2Bytes(httpResponse, url);
		} finally {
			httpResponse.close();
			httpPost.releaseConnection();
		}
	}

	/**
	 * 从XHR接口以POST方式获得文件
	 * 
	 * @param url
	 *            HTTP接口地址
	 * @param params
	 *            POST方法中提交的参数MAP对象
	 * @return FileBean
	 * @throws IllegalUrlException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public FileBean postForFile(String url, Map<String, Object> params) throws IllegalUrlException, XhrException, IOException {
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
				nvps.add(new BasicNameValuePair(key, value == null ? "" : value.toString()));
			}
		}
		
		logger.debug(XHR_POST_LOGHEAD + url);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, UTF_8));
		CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
		try {
			Header[] cds = httpResponse.getHeaders("Content-disposition");
			if (cds.length == 0)
				throw new IllegalUrlException(500, "没有获取到Content-disposition");
			HeaderElement[] elements = cds[0].getElements();
			if (elements.length == 0)
				throw new IllegalUrlException(500, "没有获取到HeaderElement");
				
			String filename = elements[0].getParameterByName("filename").getValue();
			byte[] data = entity2Bytes(httpResponse, url);
			return new FileBean(filename, data);
		} finally {
			httpResponse.close();
			httpPost.releaseConnection();
		}
	}

	/**
	 * 从XHR接口通过POST JSON方法获得一个字符串对象值
	 * 
	 * @param url
	 *            HTTP接口地址
	 * @param jsonBody
	 *            POST到接口的JSON正文
	 * @return String
	 * @throws ClientProtocolException
	 * @throws IllegalUrlException
	 * @throws IOException
	 */
	public String post(String url, String jsonBody) throws XhrException, IllegalUrlException, IOException {
		return post(url, jsonBody, CONTENTTYPE_JSON_UTF8);
	}
	
	public String post(String url, String jsonBody, String contentType) throws XhrException, IllegalUrlException, IOException {
		return post(url, jsonBody, contentType, new HashMap<String, String>());
	}
	
	public String post(String url, String jsonBody, String contentType, Map<String, String> heads) throws XhrException, IllegalUrlException, IOException {
		heads.put("Content-type", contentType);
		return post(url, jsonBody, heads);
	}
	
	public String post(String url, String jsonBody, Map<String, String> heads) throws XhrException, IllegalUrlException, IOException {
		if (null == url || !url.toLowerCase().startsWith("http")) {
			throw new IllegalUrlException(404, url);
		}

		logger.debug(XHR_POST_LOGHEAD + url);
		HttpPost httpPost = new HttpPost(url);
		StringEntity se = new StringEntity(jsonBody, UTF_8);
		if (heads != null) {
			Iterator<String> iter = heads.keySet().iterator();
			while(iter.hasNext()) {
				String name = iter.next();
				httpPost.setHeader(name, heads.get(name));
			}
		}
		httpPost.setEntity(se);

		//logger.debug("doPostPayload: " + jsonBody);
		CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
		try {
//			int statusCode = httpResponse.getStatusLine().getStatusCode();
//			if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY || statusCode == HttpStatus.SC_MOVED_PERMANENTLY) {
//				Header header = httpResponse.getFirstHeader("location"); // 跳转的目标地址是在 HTTP-HEAD 中的
//	            String newUri = header.getValue(); // 这就是跳转后的地址，再向这个地址发出新申请，以便得到跳转后的信息是啥。
//	            logger.debug("HttpRequest 302 return: {}", newUri);
//	            return post(newUri, jsonBody, heads);
//			}
			return entity2String(httpResponse, url);
		} finally {
			httpResponse.close();
			httpPost.releaseConnection();
		}
	}

	/**
	 * 从XHR接口通过POST方法处理请求
	 * @param url
	 *            HTTP接口地址
	 * @param postBody
	 *            POST到接口的JSON正文
	 * @param handler
	 *            IResponseHandler
	 * @throws ClientProtocolException
	 * @throws IllegalUrlException
	 * @throws IOException
	 */
	public void post(String url, String postBody, IResponseHandler handler) throws IllegalUrlException, IOException {
		if (null == url || !url.toLowerCase().startsWith("http")) {
			throw new IllegalUrlException(404, url);
		}

		logger.debug(XHR_POST_LOGHEAD + url);
		HttpPost httpPost = new HttpPost(url);
		StringEntity se = new StringEntity(postBody, UTF_8);
		httpPost.setHeader("Content-Type", CONTENTTYPE_JSON_UTF8);
		httpPost.setEntity(se);

		//logger.debug("doPostPayload: " + postBody);
		CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
		try {
			handler.doHandle(httpResponse);
		} finally {
			httpResponse.close();
			httpPost.releaseConnection();
		}
	}

	/**
	 * 从XHR接口通过POST方法处理请求
	 * @param url
	 *            HTTP接口地址
	 * @param postBody
	 *            POST到接口的JSON正文
	 * @return CloseableHttpResponse
	 * @throws ClientProtocolException
	 * @throws IllegalUrlException
	 * @throws IOException
	 */
	public CloseableHttpResponse postResponse(String url, String postBody) throws IllegalUrlException, IOException {
		if (null == url || !url.toLowerCase().startsWith("http")) {
			throw new IllegalUrlException(404, url);
		}

		logger.debug(XHR_POST_LOGHEAD + url);
		HttpPost httpPost = new HttpPost(url);
		StringEntity se = new StringEntity(postBody, UTF_8);
		httpPost.setHeader("Content-Type", CONTENTTYPE_JSON_UTF8);
		httpPost.setEntity(se);
		return httpClient.execute(httpPost);
	}

	/**
	 * 从XHR接口通过POST XML方法获得一个字符串对象值
	 * 
	 * @param url
	 *            HTTP接口地址
	 * @param xmlBody
	 *            POST到接口的XML正文
	 * @return String
	 * @throws ClientProtocolException
	 * @throws IllegalUrlException
	 * @throws IOException
	 */
	public String postXML(String url, String xmlBody, Map<?, ?> heads) throws XhrException, IllegalUrlException, IOException {
		return post(url, xmlBody, "application/xml; charset=UTF-8");
	}

	public String postXSD(String url, String xmlBody, Map<?, ?> heads) throws XhrException, IOException, IllegalUrlException {
		return post(url, xmlBody, "text/xml; charset=UTF-8");
	}

	public String postSOAP(String url, String xmlBody, Map<?, ?> heads) throws XhrException, IOException, IllegalUrlException {
		return post(url, xmlBody, "application/soap+xml; charset=UTF-8");
	}

	public byte[] postForBytes(String url, String jsonBody) throws IllegalUrlException, XhrException, IOException {
		return postForBytes(url, jsonBody, null);
	}
	
	public byte[] postForBytes(String url, String jsonBody, Map<String, String> heads) throws IllegalUrlException, XhrException, IOException {
		if (null == url || !url.toLowerCase().startsWith("http")) {
			throw new IllegalUrlException(404, url);
		}

		logger.debug(XHR_POST_LOGHEAD + url);
		HttpPost httpPost = new HttpPost(url);
		StringEntity se = new StringEntity(jsonBody, UTF_8);
		httpPost.setHeader("Content-Type", CONTENTTYPE_JSON_UTF8);
		httpPost.setEntity(se);

		logger.debug("doPostPayload: " + jsonBody);
		if (heads != null) {
			Iterator<String> iter = heads.keySet().iterator();
			while(iter.hasNext()) {
				String name = iter.next();
				httpPost.setHeader(name, heads.get(name));
			}
		}
		CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
		try {
			logger.debug("queryResponse: " + httpResponse.getStatusLine().getStatusCode());
			return entity2Bytes(httpResponse, url);
		} finally {
			httpResponse.close();
			httpPost.releaseConnection();
		}
	}
	
	/**
	 * 从XHR接口执行delete请求
	 * 
	 * @param url
	 *            HTTP接口地址
	 */
	public String delete(String url) throws IllegalUrlException, XhrException, IOException {
		return post(url, new HashMap<String, Object>());
	}

	/**
	 * 从XHR接口以delete方式获得一个字符串的返回值
	 * 
	 * @param url
	 *            HTTP接口地址
	 * @param params
	 *            delete方法中提交的headers参数MAP对象
	 */
	public String delete(String url, Map<String, Object> headers) throws XhrException, IllegalUrlException, IOException {
		if (null == url || !url.toLowerCase().startsWith("http")) {
			throw new IllegalUrlException(404, url);
		}

		HttpDelete httpDelete = new HttpDelete(url);
		url = url.trim().replaceAll("\n", "");
		if (headers != null) {
			Iterator<String> iter = headers.keySet().iterator();
			while(iter.hasNext()) {
				String name = iter.next();
				httpDelete.setHeader(name, (String) headers.get(name));
			}
		}
		logger.debug(XHR_POST_LOGHEAD + url);
		CloseableHttpResponse httpResponse = httpClient.execute(httpDelete);
		try {
			return entity2String(httpResponse, url);
		} finally {
			httpResponse.close();
			httpDelete.releaseConnection();
		}
	}

	public String entity2String(HttpResponse httpResponse) throws XhrException, IOException {
		return entity2String(httpResponse, null);
	}

	public String entity2String(HttpResponse httpResponse, String url) throws XhrException, IOException {
		StatusLine statusLine = httpResponse.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			String payload = "";
			try {
				if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY || statusCode == HttpStatus.SC_MOVED_PERMANENTLY) {
					Header header = httpResponse.getFirstHeader("location"); // 跳转的目标地址是在 HTTP-HEAD 中的
					payload = header.getValue(); // 这就是跳转后的地址，再向这个地址发出新申请，以便得到跳转后的信息是啥。
				} else {
					HttpEntity responseEntity = httpResponse.getEntity();
					if (responseEntity != null)
						payload = EntityUtils.toString(responseEntity, UTF_8);
				}
			} catch(Exception e) {}
			throw new XhrException(statusCode, statusLine.getReasonPhrase(), payload);
		}

		HttpEntity responseEntity = httpResponse.getEntity();
		return EntityUtils.toString(responseEntity, UTF_8);
	}

	public byte[] entity2Bytes(HttpResponse httpResponse) throws XhrException, IOException {
		return entity2Bytes(httpResponse, null);
	}

	public byte[] entity2Bytes(HttpResponse httpResponse, String url) throws XhrException, IOException {
		StatusLine statusLine = httpResponse.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			String payload = "";
			try {
				if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY || statusCode == HttpStatus.SC_MOVED_PERMANENTLY) {
					Header header = httpResponse.getFirstHeader("location"); // 跳转的目标地址是在 HTTP-HEAD 中的
					payload = header.getValue(); // 这就是跳转后的地址，再向这个地址发出新申请，以便得到跳转后的信息是啥。
				} else {
					HttpEntity responseEntity = httpResponse.getEntity();
					if (responseEntity != null)
						payload = EntityUtils.toString(responseEntity, UTF_8);
				}
			} catch(Exception e) {}
			throw new XhrException(statusCode, statusLine.getReasonPhrase(), payload);
		}

		InputStream is = httpResponse.getEntity().getContent();
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int l = -1;
			byte[] tmp = new byte[1024];
			while ((l = is.read(tmp)) != -1) {
				baos.write(tmp, 0, l);
			}
			baos.flush();
			byte[] buffer = baos.toByteArray();
			baos.close();
			return buffer;
		} finally {
			is.close();
		}
	}

	/**
	 * 执行一个HttpRequest
	 * 
	 * @param request
	 *            HttpUriRequest对象
	 * @return String
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws ParseException
	 * @throws IllegalUrlException
	 * @throws XhrException 
	 */
	public String execute(HttpUriRequest request) throws IOException, XhrException {
		CloseableHttpResponse httpResponse = httpClient.execute(request);
		try {
			return entity2String(httpResponse, request.getURI().getPath());
		} finally {
			httpResponse.close();
		}
	}

	/**
	 * 从接口读取byte对象
	 * 
	 * @param url
	 *            HTTP接口地址
	 * @param body
	 *            POST到接口的正文
	 * @param method
	 *            接口类型，GET或者POST
	 * @return byte[]
	 * @throws ClientProtocolException
	 * @throws IllegalUrlException
	 * @throws IOException
	 */
	public byte[] readBytes(String url, Map<String, Object> params, String method) throws IllegalUrlException, XhrException, IOException {
		if (method.equalsIgnoreCase(METHOD_GET)) {
			return getForBytes(url, params);
		} else {
			return postForBytes(url, params);
		}
	}

	public Element soap(String url, String methodName, String namespace, Map<String, Object> parameterMap) throws IOException, IllegalUrlException, XhrException, DocumentException {
		String xmlBody = buildWebServiceRequestData(methodName, namespace, parameterMap);
		String strXML = postSOAP(url, xmlBody, null);
		Element bodyElement = DomUtils.parser(strXML).getRootElement().element("Body");
		Element responseElement = bodyElement.element(methodName + "Response");
		return responseElement.element(methodName + "Result");
	}

	public String buildWebServiceRequestData(String methodName, String namespace, Map<String, Object> parameterMap) {
		StringBuilder soapRequestData = new StringBuilder();
		soapRequestData.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		soapRequestData.append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
				+ " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
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

	public PoolingHttpClientConnectionManager getConnectionManager() {
		return this.connManager;
	}

	public CloseableHttpClient getHttpClient() {
		return this.httpClient;
	}
}

class IdleConnectionMonitorThread extends Thread {

	private final HttpClientConnectionManager connMgr;
	private volatile boolean shutdown;

	public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
		super();
		this.connMgr = connMgr;
	}

	@Override
	public void run() {
		try {
			while (!shutdown) {
				synchronized (this) {
					wait(5000);
					// Close expired connections
					connMgr.closeExpiredConnections();
					// Optionally, close connections
					// that have been idle longer than 30 sec
					connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
				}
			}
		} catch (Exception e) {
			// terminate
		}
	}

	public void shutdown() {
		shutdown = true;
		synchronized (this) {
			notifyAll();
		}
	}
}