package com.gikee.ethereum.config;

import lombok.Data;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ElasticSearchConfig {

    @Value(value = "${spring.data.elasticsearch.cluster-name}")
    private String clusterName;

    @Value(value = "${spring.data.elasticsearch.cluster-nodes}")
    private String clusterNodes;

    @Value(value = "${spring.data.elasticsearch.poolSize}")
    private String poolSize;

    @Bean
    public RestHighLevelClient getRestClientBuilder() {

        RestClientBuilder restClientBuilder = null;
        RestHighLevelClient restHighLevelClient;

        String[] temp;
        String host;
        int port;

        String[] param = clusterNodes.split(",");
        HttpHost[] httpHosts = new HttpHost[param.length];
        for (int i = 0; i < param.length; i++) {
            temp = param[i].split(":");
            host = temp[0];
            port = Integer.parseInt(temp[1]);
            httpHosts[i] = new HttpHost(host, port, "http");
        }
        restClientBuilder = RestClient.builder(httpHosts);

        /*1.设置请求头，避免每个请求都必须指定*/
        Header[] defaultHeaders = new Header[]{
                new BasicHeader("header", "value")
        };
        restClientBuilder.setDefaultHeaders(defaultHeaders);

        /*2.设置在同一请求进行多次尝试时应该遵守的超时时间。默认值为30秒，与默认`socket`超时相同。
            如果自定义设置了`socket`超时，则应该相应地调整最大重试超时。*/
        restClientBuilder.setMaxRetryTimeoutMillis(50000);

        restHighLevelClient = new RestHighLevelClient(restClientBuilder);

        return restHighLevelClient;
    }

}
