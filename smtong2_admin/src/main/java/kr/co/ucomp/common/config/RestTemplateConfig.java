package kr.co.ucomp.common.config;


import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;

@Configuration
public class RestTemplateConfig {

	@Bean
	public RestTemplate restTemplate() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {

		HttpComponentsClientHttpRequestFactory crf = new HttpComponentsClientHttpRequestFactory();

		HttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
			.setDefaultConnectionConfig(ConnectionConfig.custom()
					.setSocketTimeout(10, TimeUnit.SECONDS) //읽기시간초과 타임아웃
					.setConnectTimeout(30, TimeUnit.SECONDS) //연결시간초과 타임아웃
				.build())
			// SSL을 무시하는 HttpClient 생성
		    .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
			      .setSslContext(SSLContextBuilder.create().loadTrustMaterial(TrustAllStrategy.INSTANCE).build())
			      .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
			      .build())			
			.setMaxConnTotal(100) //커넥션풀적용(최대 오픈되는 커넥션 수)
			.setMaxConnPerRoute(10) //커넥션풀적용(IP:포트 1쌍에 대해 수행 할 연결 수제한)
			.build();


		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
        crf.setHttpClient(httpClient);
		
		 RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(crf));
	     restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
	     
	     return restTemplate;
	}
}


class RequestResponseLoggingInterceptor implements ClientHttpRequestInterceptor {
	 
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
 
        // 전
        ClientHttpResponse response = execution.execute(request, body);
        // 후
 
        return response;
 
    }
 
}