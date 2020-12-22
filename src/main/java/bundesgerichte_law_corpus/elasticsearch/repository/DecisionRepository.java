package bundesgerichte_law_corpus.elasticsearch.repository;

import bundesgerichte_law_corpus.model.Decision;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.Optional;

public interface DecisionRepository extends ElasticsearchRepository<Decision, String> {

    @Override
    Optional<Decision> findById(String id);

    Optional<Decision> findByEcli(String ecli);

    Optional<Decision> findByDocketnumber(String dn);

    @Override
    long count();

    @Override
    boolean existsById(String id);

    @Override
    void deleteById(String id);

    @Override
    void deleteAll();

    ArrayList<Decision> findByCourtType(String courtType);

    //@Query("{\"bool\": {\"must\": [{\"match\": {\"authors.name\": \"?0\"}}]}}")
    /*
    @Query("{\n" +
            "  \"query\": {\n" +
            "    \"match\": {\n" +
            "      \"courtType\": \"BVerfG\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"fields\": [\"courtType\"],\n" +
            "  \"_source\": false\n" +
            "}")
            */
    //@Query(value="{\"bool\":{\"must\":{\"field\":{\"courtType\":\"BVerfG\"}}}}")
    @Query("{\n" +
            "    \"match\": {\n" +
            "      \"decisionTitle\": \"?0\"\n" +
            "    }\n" +
            "}")
    ArrayList<Decision> findByCustomQuery(@Param("term") String term);
}
