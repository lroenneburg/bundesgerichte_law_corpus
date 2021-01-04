package bundesgerichte_law_corpus.elasticsearch;

import org.elasticsearch.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "bundesgerichte_law_corpus.elasticsearch.repository")
@ComponentScan(basePackages = { "com.baeldung.spring.data.es.service" })
public class ElasticConfig {

    @Value("${eldb.host}")
    String host;

    @Bean
    public RestHighLevelClient client() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder().connectedTo(host).withSocketTimeout(100000).build();
        RestHighLevelClient rest = RestClients.create(clientConfiguration).rest();

        return rest;
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        ElasticsearchRestTemplate elasticsearchRestTemplate = new ElasticsearchRestTemplate(client());
        return elasticsearchRestTemplate;

    }
}
