package bundesgerichte_law_corpus.elasticsearch;

import bundesgerichte_law_corpus.elasticsearch.repository.DecisionRepository;
import bundesgerichte_law_corpus.model.Decision;
import bundesgerichte_law_corpus.model.DecisionSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Performs all thymeleaf based REST operations to give information from ES to the web application
 */
@Controller
public class WebController {

    // the ES operations
    @Autowired
    ElasticsearchOperations operations;

    // The decisionRepository to get the decisions from ES
    @Autowired
    DecisionRepository _decisionRepository;

    /**
     * Gets a specific decision from the ES database and saves the decision objject to a model, which can be
     * accessed by thymeleaf in the web application
     * @param docketnumber the docketnumber of the decision
     * @param model the model which passes the information to thymeleaf
     * @return the thymeleaf template which is called
     */
    @GetMapping(value = "/decision")
    public String getDecisionTemplate(@RequestParam(name = "az", required = true) String docketnumber, Model model) {
        Optional<Decision> decision = _decisionRepository.findByDocketnumber(docketnumber);

        Decision dec = decision.get();

        ArrayList<String> azs = dec.getDocketNumber();
        String aktenzeichen = ("" + azs);
        aktenzeichen = aktenzeichen.replaceAll("\\[", "");
        aktenzeichen = aktenzeichen.replaceAll("\\]", "");

        String titel = dec.getDecisionTitle();
        String ecli = dec.getEcli();
        String norms = "" + dec.getNorms();
        norms = norms.replaceAll("\\[", "");
        norms = norms.replaceAll("\\]", "");

        ArrayList<String> previousCourts = dec.getPreviousCourts();

        ArrayList<String> guidingprinciple = dec.getGuidingPrinciple();
        ArrayList<String> sonstosatz = dec.getOtherOrientationSentence();
        ArrayList<String> tenor = dec.getTenor();
        ArrayList<DecisionSection> fact = dec.getFact();
        ArrayList<DecisionSection> decisionReasons = dec.getDecisionReasons();
        ArrayList<DecisionSection> dissentingopinions = dec.getDissentingOpinions();
        ArrayList<String> citations = dec.getOccurringCitations();

        model.addAttribute("Aktenzeichen", aktenzeichen);
        model.addAttribute("Titel", titel);
        model.addAttribute("ECLI", ecli);
        model.addAttribute("Gerichtstyp", dec.getCourtType());
        model.addAttribute("Spruchkoerper", dec.getFormation());
        model.addAttribute("Entscheidungsdatum", dec.getDecisionDate());
        model.addAttribute("Entscheidungstyp", dec.getDecisionType());
        model.addAttribute("Normen", norms);
        model.addAttribute("Vorinstanzen", previousCourts);
        model.addAttribute("URL", dec.getUrl());
        if (!(dec.getPageRank() == 0.0)) {
            model.addAttribute("PageRank", dec.getPageRank());
        } else {
            model.addAttribute("PageRank", "");
        }

        model.addAttribute("Cluster", dec.getClusterName());
        model.addAttribute("Leitsatz", guidingprinciple);
        model.addAttribute("SonstigerOrientierungssatz", sonstosatz);
        model.addAttribute("Tenor", tenor);
        model.addAttribute("Tatbestand", fact);
        model.addAttribute("Entscheidungsgruende", decisionReasons);
        model.addAttribute("AbweichendeMeinungen", dissentingopinions);
        model.addAttribute("Zitate", citations);
        return "decision";
    }


    /**
     * Gets a specific Cluster from the file system to display the cluster Graph in the web application
     * @param cluster the cluster ID you want to display
     * @param model the model which passes the information to thymeleaf
     * @return the thymeleaf template which is called
     */
    @GetMapping(value = "/cluster")
    public String getClusterTemplate(@RequestParam(name = "id", required = true) String cluster, Model model) {

        model.addAttribute("Cluster", "/getClData?cl=graph_cluster_" + cluster);
        return "cluster";
    }


    /**
     * Performs a search query to find decisions in the index that contain a specific term
     * @param term the term you want to search
     * @param model the model which passes the information to thymeleaf
     * @return the thymeleaf template which is called
     */
    @GetMapping(value = "/search")
    public String testQuery(@RequestParam(name = "term", required = true) String term, Model model) {

        // If the queryteam matches a docketnumber of a decision, than we only need to return this decision
        Optional<Decision> searchResultDN = _decisionRepository.findByDocketnumber(term);
        if (!searchResultDN.isEmpty() && searchResultDN.get().getDocketNumber().toString().replaceAll("[\\[\\]]", "").equals(term)) {
            ArrayList<Decision> list = new ArrayList<>();
            list.add(searchResultDN.get());
            model.addAttribute("responses", list);
        }
        else {
            // If the term does not match the docketnumber, we search in the other categories of the
            // decision for that term
            ArrayList<Decision> searchResults = _decisionRepository.findByCustomQuery(term);
            model.addAttribute("responses", searchResults);
        }

        return "search";
    }
}
