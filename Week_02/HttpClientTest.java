public class HttpClientTest {

	private static CloseableHttpClient httpclient;

	static {
		createSSLClientDefault();
	}

	/**
	 * 创建SSLClientDefault
	 */
	public static void createSSLClientDefault() {

		SSLContext sslContext = null;
		try {
			//使用 loadTrustMaterial() 方法实现一个信任策略，信任所有证书
			sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
				// 信任所有
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
		} catch (Exception e) {
			log.error("SSLContext 初始化异常", e.getMessage(), e);
		}

		if (null == sslContext) {
			log.error("无法获取到有效的sslContext,不再继续初始化");
			return;
		}

		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		Builder builder = RequestConfig.custom();
		// 设置请求超时时间
		builder.setConnectionRequestTimeout(100 * 1000);
		builder.setSocketTimeout(100 * 1000);
		builder.setConnectionRequestTimeout(100 * 1000);
		httpClientBuilder.setDefaultRequestConfig(builder.build());
		httpClientBuilder.setMaxConnPerRoute(10);
		httpClientBuilder.setMaxConnTotal(10);
		// 链接的存活时间
		httpClientBuilder.setConnectionTimeToLive(20, TimeUnit.SECONDS);
		// 清除过期链接 [默认10s清理一次]
		httpClientBuilder.evictExpiredConnections();

		httpclient = httpClientBuilder.setSSLContext(sslContext).setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
	}




	/**
	 * doPost
	 *
	 * @throws Exception
	 */
	public static String doPost(String url, Map<String, Object> paramMap) throws Exception {

		return doPost(url, paramMap, null);
	}

	

	/**
	 * doPost
	 *
	 * @throws Exception
	 */
	public static String doPost(String url, Map<String, Object> paramMap, Map<String, String> headerMap) throws
			Exception {

		HttpPost httpPost = new HttpPost(url);
		if (null != paramMap && !paramMap.isEmpty()) {
			List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
			for (Map.Entry<String, Object> item : paramMap.entrySet()) {
				Object valueObj = item.getValue();
				if (null == valueObj) {
					continue;
				}
				nvpList.add(new BasicNameValuePair(item.getKey(), valueObj.toString()));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(nvpList, Consts.UTF_8));
		}

		// 装载header
		if (null != headerMap && !headerMap.isEmpty()) {
			for (Map.Entry<String, String> headerItem : headerMap.entrySet()) {
				httpPost.setHeader(headerItem.getKey(), headerItem.getValue());
			}
		}

		String resultString = StringUtils.EMPTY;
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpPost);
			HttpEntity resultEntity = response.getEntity();
			resultString = EntityUtils.toString(resultEntity, Charset.forName("UTF-8"));
		} finally {
			if (null != response) {
				response.close();
			}
		}
		return resultString;
	}
	
	public  static void main(String args []){
	
	    HttpClientTest.doPost("http://localhost:8801",new HashMap<>(),new HashMap<>());
	
	}

	
}