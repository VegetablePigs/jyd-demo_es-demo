package com.jyd.es_demo2.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: jyd
 * @Date: 2022/12/15 15:24
 * @Company: 广州风雨雷科技有限公司
 * @Version: 1.0
 * @Desc:
 **/
@Configuration
public class RestClientConfig {
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        // 如果有多个从节点可以持续在内部new多个HttpHost，参数1是IP，参数2是端口，参数3是通信协议
        return new RestHighLevelClient(RestClient.builder(new HttpHost("8.134.54.209", 9200, "http")));
    }
}
