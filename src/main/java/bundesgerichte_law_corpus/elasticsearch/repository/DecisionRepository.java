package bundesgerichte_law_corpus.elasticsearch.repository;

import bundesgerichte_law_corpus.model.Decision;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

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
}
