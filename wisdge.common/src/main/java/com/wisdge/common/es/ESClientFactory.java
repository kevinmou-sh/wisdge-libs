package com.wisdge.common.es;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 
 * Elasticsearch RestClient 工厂类
 *
 */
public class ESClientFactory {
	private static final Logger logger = LoggerFactory.getLogger(ESClientFactory.class);

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

    public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public int getConnectRequestTimeout() {
		return connectRequestTimeout;
	}

	public void setConnectRequestTimeout(int connectRequestTimeout) {
		this.connectRequestTimeout = connectRequestTimeout;
	}

	public int getMaxConnectNum() {
		return maxConnectNum;
	}

	public void setMaxConnectNum(int maxConnectNum) {
		this.maxConnectNum = maxConnectNum;
	}

	public int getMaxConnectPerRoute() {
		return maxConnectPerRoute;
	}

	public void setMaxConnectPerRoute(int maxConnectPerRoute) {
		this.maxConnectPerRoute = maxConnectPerRoute;
	}

	public boolean isUniqueConnectTimeConfig() {
		return uniqueConnectTimeConfig;
	}

	public void setUniqueConnectTimeConfig(boolean uniqueConnectTimeConfig) {
		this.uniqueConnectTimeConfig = uniqueConnectTimeConfig;
	}

	public boolean isUniqueConnectNumConfig() {
		return uniqueConnectNumConfig;
	}

	public void setUniqueConnectNumConfig(boolean uniqueConnectNumConfig) {
		this.uniqueConnectNumConfig = uniqueConnectNumConfig;
	}

	public List<ESServer> getEsServers() {
		return esServers;
	}

	public void setEsServers(List<ESServer> esServers) {
		this.esServers = esServers;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getInitIndex() {
		return initIndex;
	}

	public void setInitIndex(String initIndex) {
		this.initIndex = initIndex;
	}

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
				logger.debug("Set es credential: {} - {}", this.userName, this.password);
			}
			return httpClientBuilder;
		});
	}

    public RestClient getClient(){
        return restClient;
    }

	public Response performRequest(String method, String endpoint) throws Exception {
		return performRequest(method, endpoint, null, null);
	}

    public Response performRequest(String method, String endpoint, String entityString) throws Exception {
    	return performRequest(method, endpoint, entityString, null);
	}

	public Response performRequest(String method, String endpoint, String entityString, Header header) throws Exception {
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

		if (! StringUtils.isEmpty(entityString))
			request.setEntity(new StringEntity(entityString, StandardCharsets.UTF_8));
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
            	logger.error(e.getMessage(), e);
            }
        }
    }
}
