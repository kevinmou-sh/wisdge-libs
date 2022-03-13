package com.wisdge.commons.es;

import com.wisdge.dataservice.utils.JSonUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.*;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Elasticsearch RestClient 工厂类
 */
@Slf4j
@Data
@NoArgsConstructor
@ToString
public class ESClientFactory {
	private String host = "127.0.0.1";
	private int port = 9200;
	private String schema = "http";
	private List<ESServer> esServers;
	private int connectTimeout = 1000;
	private int socketTimeout = 30000;
	private int connectRequestTimeout = 500;
	private int maxConnectNum = 100;
	private int maxConnectPerRoute = 100;
	private boolean uniqueConnectTimeConfig = true;
	private boolean uniqueConnectNumConfig = true;
	private RestClientBuilder builder;
	private RestClient restClient;
	private String userName;
	private String password;
	private String initIndex;
	private String namespace;

	private static final RequestOptions COMMON_OPTIONS;
	static {
		RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
		builder.addHeader("Content-Type", "application/json; charset=UTF-8");
		COMMON_OPTIONS = builder.build();
	}

	public void init() {
		if (esServers != null && ! esServers.isEmpty()) {
			HttpHost[] httpHosts = new HttpHost[esServers.size()];
			for (int i = 0; i < esServers.size(); i++) {
				ESServer esServer = esServers.get(i);
				httpHosts[i] = new HttpHost(esServer.getHost(), esServer.getPort(), esServer.getSchema());
			}
			builder = RestClient.builder(httpHosts);
		} else {
			HttpHost httpHost  = new HttpHost(host, port, schema);
			builder = RestClient.builder(httpHost);
		}

		if (uniqueConnectTimeConfig) {
			setConnectTimeOutConfig();
		}
		setHttpClientConfig();

		restClient = builder.build();

		try {
			initIndex();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private void initIndex() throws Exception {
		if (StringUtils.isNotEmpty(this.initIndex)) {
			String endpoint = "/" + this.initIndex;
			Response res = this.performRequest("HEAD", endpoint);
			int statusCode = res.getStatusLine().getStatusCode();
			log.debug("Initialized index status {}", statusCode);
		}
	}

	// 主要关于异步httpclient的连接延时配置
	public void setConnectTimeOutConfig(){
		builder.setRequestConfigCallback(requestConfigBuilder -> {
			requestConfigBuilder.setConnectTimeout(connectTimeout);
			requestConfigBuilder.setSocketTimeout(socketTimeout);
			requestConfigBuilder.setConnectionRequestTimeout(connectRequestTimeout);
			return requestConfigBuilder;
		});
	}

	// 主要关于异步httpclient的连接数配置以及验证账号和密码
	public void setHttpClientConfig() {
		builder.setHttpClientConfigCallback(httpClientBuilder -> {
			if (uniqueConnectNumConfig) {
				httpClientBuilder.setMaxConnTotal(maxConnectNum);
				httpClientBuilder.setMaxConnPerRoute(maxConnectPerRoute);
			}
			if (StringUtils.isNotEmpty(this.userName)) { // 需要验证
				CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
				credentialsProvider.setCredentials(AuthScope.ANY,
						new UsernamePasswordCredentials(this.userName, this.password));  //es账号密码
				httpClientBuilder.disableAuthCaching();
				httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
				log.debug("Set es credential: {} - {}", this.userName, this.password);
			}

			if ("https".equalsIgnoreCase(this.schema)) {
				setSSL(httpClientBuilder);
			}

			return httpClientBuilder;
		});
	}

	private void setSSL(HttpAsyncClientBuilder httpClientBuilder) {
		try {
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
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, new TrustManager[] { xtm }, null);
			httpClientBuilder.setSSLContext(ctx);
			httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
		} catch (Exception ignore) {}
	}

	public RestClient getClient(){
		return restClient;
	}

	public Response performRequest(String method, String endpoint) throws Exception {
		return performRequest(method, endpoint, null, null);
	}

	public Response performRequest(String method, String endpoint, Object entity) throws Exception {
		return performRequest(method, endpoint, entity, null);
	}

	public Response performRequest(String method, String endpoint, Object entity, Header header) throws Exception {
		endpoint = "/" + (StringUtils.isEmpty(namespace) ? "" : namespace) + (endpoint.startsWith("/") ? endpoint.substring(1):endpoint);
		Request request = new Request(method, endpoint);
		if (header != null) {
			RequestOptions.Builder options = COMMON_OPTIONS.toBuilder();
			for(HeaderElement head: header.getElements()) {
				options.addHeader(head.getName(), head.getValue());
			}
			request.setOptions(options);
		} else {
			request.setOptions(COMMON_OPTIONS);
		}

		if (entity != null) {
			if (entity instanceof String) {
				request.setEntity(new StringEntity((String) entity, StandardCharsets.UTF_8));
			} else {
				request.setEntity(new StringEntity(JSonUtils.parse(entity), StandardCharsets.UTF_8));
			}
		}

		try {
			return restClient.performRequest(request);
		} catch (ResponseException e) {
			return e.getResponse();
		} catch (Exception e) {
			throw e;
		}
	}

	public void close() {
		if (restClient != null) {
			try {
				restClient.close();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

}
