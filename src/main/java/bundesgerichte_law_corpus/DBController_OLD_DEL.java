package bundesgerichte_law_corpus;

import bundesgerichte_law_corpus.model.Decision;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;


import java.io.IOException;

/**
 * The DB Controller manages the connection to the ElasticSearch elasticsearch
 */
public class DBController_OLD_DEL {

    //The config parameters for the connection
    private static final String _host = "localhost";
    private static final int _port_query = 9200;
    private static final int _port_rest = 9201;
    private static final String _scheme = "http";
    private static RestHighLevelClient _client;

    private static final String _index = "persondata";
    private static final String _type = "person";

    public DBController_OLD_DEL() throws IOException {
        _client = makeConnection();

        /*
        String jsonObject = "{\"age\":10,\"dateOfBirth\":1471466076564," +"\"fullName\":\"John Doe\"}";
        IndexRequest request = new IndexRequest("people");
        request.source(jsonObject, XContentType.JSON);

        try {
            IndexResponse response = _client.index(request, RequestOptions.DEFAULT);
            String index = response.getIndex();
            long version = response.getVersion();
            System.out.println("Index: " + index);
            System.out.println("Version: " + version);

        } catch(ElasticsearchException e) {
            e.getDetailedMessage();
        } catch (java.io.IOException ex){
            ex.getLocalizedMessage();
        }
        */
    }

    /**
     *
     * @param decision
     */
    public void addDecisionToDatabase(Decision decision) throws IOException {
        XContentBuilder builder = null;

        //ObjectMapper objectMapper = new ObjectMapper();
        //String jsonStr = objectMapper.writeValueAsString(decision);
        //System.out.println(jsonStr);



        //IndexRequest indexRequest = new IndexRequest("decisions");
        //indexRequest.source(jsonStr, XContentType.JSON);

        //IndexResponse response = _client.index(indexRequest, RequestOptions.DEFAULT);
    }



    /**
     * Implemented Singleton pattern here
     * so that there is just one connection at a time.
     * @return RestHighLevelClient
     */
    private static synchronized RestHighLevelClient makeConnection() {

        if(_client == null) {
            _client = new RestHighLevelClient(RestClient.builder(
                            new HttpHost(_host, _port_query, _scheme),
                            new HttpHost(_host, _port_rest, _scheme)));
        }

        return _client;
    }


    /**
     *
     * @throws IOException
     */
    private static synchronized void closeConnection() throws IOException {
        _client.close();
        _client = null;
    }

}
