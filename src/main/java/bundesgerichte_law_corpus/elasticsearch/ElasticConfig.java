package bundesgerichte_law_corpus.elasticsearch;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
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

    @Bean
    public RestHighLevelClient client() {

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("law-lennart", "di8Gt87"));

        //TestServer
        //ClientConfiguration clientConfiguration = ClientConfiguration.builder().connectedTo("localhost:9200").withSocketTimeout(100000).build();

        //ClientConfiguration clientConfiguration = ClientConfiguration.builder().connectedTo("https://ltdemos.informatik.uni-hamburg.de:443/depcc-index/").build();


        //RestClientBuilder builder = RestClient.builder(new HttpHost("http://localhost:9200"));

        RestClientBuilder builder = RestClient.builder(new HttpHost("https://ltdemos.informatik.uni-hamburg.de/depcc-index", 443))
               .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));

        return new RestHighLevelClient(builder);

        //return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(client());
    }
}
