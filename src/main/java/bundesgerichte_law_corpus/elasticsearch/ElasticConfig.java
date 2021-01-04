package bundesgerichte_law_corpus.elasticsearch;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

import static org.elasticsearch.common.unit.ByteSizeUnit.MB;

@Configuration
@EnableElasticsearchRepositories(basePackages = "bundesgerichte_law_corpus.elasticsearch.repository")
@ComponentScan(basePackages = { "com.baeldung.spring.data.es.service" })
public class ElasticConfig {

    @Bean
    public RestHighLevelClient client() {

        //TestServer
        //ClientConfiguration clientConfiguration = ClientConfiguration.builder().connectedTo("localhost:9200").withSocketTimeout(100000).build();

        // VM BaseCamp
        ClientConfiguration clientConfiguration = ClientConfiguration.builder().connectedTo("basehack1.informatik.uni-hamburg.de:3000").withSocketTimeout(100000).build();




        //ClientConfiguration clientConfiguration = ClientConfiguration.builder().connectedTo("https://ltdemos.informatik.uni-hamburg.de:443/depcc-index/").build();


        //RestClientBuilder builder = RestClient.builder(new HttpHost("http://localhost:9200"));

        //RestClientBuilder builder = RestClient.builder(new HttpHost("https://ltdemos.informatik.uni-hamburg.de/depcc-index", 443))
        //       .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));

        //return new RestHighLevelClient(builder);

        //ClientConfiguration clientConfiguration = ClientConfiguration.builder()
        //        .connectedTo("ltdemos.informatik.uni-hamburg.de:443")
        //        .withPathPrefix("depcc-index").withSocketTimeout(100000).build();
        RestHighLevelClient rest = RestClients.create(clientConfiguration).rest();

        return rest;

        //return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        ElasticsearchRestTemplate elasticsearchRestTemplate = new ElasticsearchRestTemplate(client());
        return elasticsearchRestTemplate;

    }
}
