package bundesgerichte_law_corpus.elasticsearch;

import bundesgerichte_law_corpus.Analysis;
import bundesgerichte_law_corpus.DataMapper;
import bundesgerichte_law_corpus.NetworkController;
import bundesgerichte_law_corpus.elasticsearch.repository.DecisionRepository;
import bundesgerichte_law_corpus.model.Decision;
import bundesgerichte_law_corpus.model.DecisionSection;
import org.jgrapht.Graph;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
public class DecisionController {

    @Autowired
    ElasticsearchOperations operations;

    @Autowired
    DecisionRepository _decisionRepository;


    @RequestMapping("/all")
    public ArrayList<Decision> getAllDecisions() {
        ArrayList<Decision> decisions = new ArrayList<>();
        _decisionRepository.findAll().forEach(decisions::add);
        return decisions;
    }

    @RequestMapping(value = "/newTestDecision")
    public Decision addDecisions() {

        //new ElasticConfig().elasticsearchTemplate().indexOps(Decision.class).create();

        ArrayList<DecisionSection> dissentingOpinions = new ArrayList<>();
        ArrayList<String> sonstosatz = new ArrayList<>();
        ArrayList<String> dn = new ArrayList<>();
        dn.add("2 BvR 2299/09");
        ArrayList<String> norms = new ArrayList<>();
        norms.add("Art 103 Abs 1 GG, Art 1 Abs 1 GG, Art 25 GG");
        norms.add("Art 2 Abs 1 GG, § 93c Abs 1 S 1 BVerfGG");
        norms.add("§ 32 IRG");
        norms.add("Art 302 StGB TUR");
        norms.add("Art 104 Verf TUR");
        ArrayList<String> lowerCourts = new ArrayList<>();
        lowerCourts.add("vorgehend OLG Hamm, 17. September 2009, Az: (2) 4 AuslA 22/08 (338/09), Beschluss");
        lowerCourts.add("vorgehend OLG Hamm, 24. August 2009, Az: (2) 4 AuslA 22/08 (297/09), Beschluss");
        lowerCourts.add("vorgehend OLG Hamm, 2. Juni 2009, Az: (2) 4 AuslA 22/08 (152/09), Beschluss");
        ArrayList<String> tenor = new ArrayList<>();
        tenor.add("Der Beschluss des Oberlandesgerichts Hamm vom 2. Juni 2009 - (2) 4 Ausl. A 22/08 (152 und 153/09) OLG Hamm - verletzt den Beschwerdeführer in seinem Grundrecht aus Artikel 2 Absatz 1 in Verbindung mit Artikel 1 Absatz 1 des Grundgesetzes, soweit in ihm die Auslieferung des Beschwerdeführers zur Strafverfolgung für zulässig erklärt wird. Der Beschluss wird insoweit aufgehoben und die Sache an das Oberlandesgericht Hamm zurückverwiesen");
        tenor.add("Der Beschluss des Oberlandesgerichts Hamm vom 17. September 2009 - (2) 4 Ausl. A 22/08 (338/09) OLG Hamm - verletzt den Beschwerdeführer in seinem Grundrecht aus Artikel 2 Absatz 1 in Verbindung mit Artikel 1 Absatz 1 des Grundgesetzes, soweit die Einwendungen des Beschwerdeführers gegen die Zulässigkeit der Auslieferung zurückgewiesen worden sind. Der Beschluss wird insoweit aufgehoben.");
        tenor.add("Im Übrigen wird die Verfassungsbeschwerde nicht zur Entscheidung angenommen.");
        tenor.add("...");
        ArrayList<DecisionSection> decReasons = new ArrayList<>();
        decReasons.add(new DecisionSection(1, "Die Verfassungsbeschwerde betrifft die Zulässigkeit der Auslieferung zum Zwecke der Strafverfolgung an die Republik Türkei wegen Staatsschutzdelikten bei drohender Verurteilung zu einer sogenannten erschwerten lebenslänglichen Freiheitsstrafe."));
        decReasons.add(new DecisionSection(2, "Der Beschwerdeführer besitzt die türkische Staatsangehörigkeit. Unter Bezugnahme auf einen Haftbefehl des Schwurgerichts zu D. vom 28. November 2007 ersuchte die türkische Regierung um seine Auslieferung. Ihm wird vorgeworfen, als Gebietsverantwortlicher der Arbeiterpartei Kurdistans (PKK) für die Region E. die Ausführung eines Bombenanschlags auf den Gouverneur von B. am 5. April 1999 durch ein Mitglied der PKK, den T..., beschlossen und angeordnet zu haben. Bei diesem Bombenattentat kamen T... und eine weitere Person ums Leben; weitere 14 Personen, darunter Polizeibeamte, wurden verletzt."));
        decReasons.add(new DecisionSection(3, "WEGEN TESTZWECKEN GEKÜRZT - UNVOLLSTAENDIG!"));
        ArrayList<String> occCit = new ArrayList<>();
        occCit.add("BVerfGE 63, 332 <337 f.>");
        occCit.add("BVerfGE 50, 205 <214 f.>");
        ArrayList<String> occJudge = new ArrayList<>();
        occJudge.add("Lennart Rönneburg");
        occJudge.add("Dirk Hartung");
        occJudge.add("Eugen Ruppert");

        ArrayList<String> gui_princ = new ArrayList<>();
        ArrayList<DecisionSection> fact = new ArrayList<>();

        Decision decision1 = new Decision("KVRE387011001", "ECLI:DE:BVerfG:2010:rk20100116.2bvr229909", "BVerfG", "2. Senat 2. Kammer", "16.01.2010",
                dn, "Stattgebender Kammerbeschluss", norms, lowerCourts, "Teilweise stattgebender Kammerbeschluss: Auslieferung verletzt Grundrechte des Betroffenen aus Art 2 Abs 1 GG iVm Art 1 Abs 1 GG, wenn die Vollstreckung einer erschwerten lebenslangen Freiheitsstrafe droht - hier: Auslieferung in die Türkei zum Zweck der Strafverfolgung wegen Staatsschutzdelikten - Möglichkeit der Begnadigung nach türkischem Recht aufgrund tatbestandlicher Einschränkungen im Hinblick auf Verhältnismäßigkeitsgrundsatz des GG unzureichend", gui_princ,
                sonstosatz, tenor, fact, decReasons, dissentingOpinions, "http://www.rechtsprechung-im-internet.de/jportal/?quelle=jlink&docid=KVRE387011001&psml=bsjrsprod.psml&max=true", occCit,
                occJudge);
        //_decisionRepository.save(decision1);
        return null;
    }


    @RequestMapping(value = "/addAllDecisionsToDB")
    public String addDB() throws IOException {

        System.out.println("Crawl all Decisions...");
        ArrayList<String> decisionIDs = new ArrayList<>();
        File decision_folder = new File("../Resources");
        File[] files = decision_folder.listFiles();
        for (int i = 10000; i <= 35000; i++) {
            decisionIDs.add(files[i].getName().split("\\.")[0]);
        }

        System.out.println("Start Mapping...");
        DataMapper dataMapper = new DataMapper();
        ArrayList<Decision> decisions = dataMapper.mapDecisionObjects(decisionIDs);
        System.out.println("Decisions Mapped successfully");


        int counter = 0;

        System.out.println("Start uploading to ElasticSearch...");
        for (Decision d : decisions) {
            _decisionRepository.save(d);
            counter++;
        }
        System.out.println("Finished Uploading.");
        return counter + " Decisions added successfully to Database.";
    }


    @RequestMapping(
            method = RequestMethod.GET,
            path = "/getDecision",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Optional<Decision> getDecisionFromESDatabase(@RequestParam String decisionid) {
        return _decisionRepository.findById(decisionid);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/getDBSize",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public long getSizeOfESDatabase() {
        return _decisionRepository.count();
    }


    @RequestMapping(
            method = RequestMethod.GET,
            path = "/getDecisionByECLI",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Optional<Decision> getDecisionByECLIFromESDatabase(@RequestParam String ecli) {
        return _decisionRepository.findByEcli(ecli);
    }


    @RequestMapping(
            method = RequestMethod.GET,
            path = "/getAll",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Iterable<Decision> getAllDecision() {
        Iterable<Decision> all = _decisionRepository.findAll();
        System.out.println("lel");
        return null;
    }


    @RequestMapping(
            method = RequestMethod.GET,
            path = "/deleteAll",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String deleteTheDB() {
        _decisionRepository.deleteAll();
        return "All deleted";
    }


    @RequestMapping(
            method = RequestMethod.GET,
            path = "/testNetwork",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String testNetwork() {
        ArrayList<Decision> decs = new ArrayList<>();
        ArrayList<Decision> all = (ArrayList<Decision>) _decisionRepository.findAll();
        //ArrayList<Decision> bverfg = _decisionRepository.findByCourtType("BVerfG");
        //ArrayList<Decision> bgh = _decisionRepository.findByCourtType("BGH");
        //ArrayList<Decision> bverwg = _decisionRepository.findByCourtType("BVerwG");
        //ArrayList<Decision> BFH = _decisionRepository.findByCourtType("BFH");
        //ArrayList<Decision> bag = _decisionRepository.findByCourtType("BAG");
        //ArrayList<Decision> bsg = _decisionRepository.findByCourtType("BSG");
        //ArrayList<Decision> bpatg = _decisionRepository.findByCourtType("BPatG");

        decs.addAll(all);
        //decs.addAll(bgh);
        NetworkController networkController = new NetworkController();
        networkController.createNetwork(decs);
        return "Done.";
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/computeClusters",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String testPageRank() {
        ArrayList<Decision> decs = new ArrayList<>();
        ArrayList<Decision> bverfg = _decisionRepository.findByCourtType("BVerfG");
        decs.addAll(bverfg);
        HashMap<String, Decision> decMap = new HashMap<>();
        for (Decision d : decs) {
            String docketNumber = d.getDocketNumber().toString();
            docketNumber = docketNumber.replaceAll("\\[", "");
            docketNumber = docketNumber.replaceAll("\\]", "");
            decMap.put(docketNumber, d);
        }


        //decs.addAll(bgh);
        NetworkController networkController = new NetworkController();
        Graph<String, DefaultEdge> network = networkController.createNetwork(decs);

        //NetworkController networkController = new NetworkController();
        networkController.generatePageRank(network);
        List<Set<String>> cw_cluster = networkController.generateClusteringWithCW(network);
        //List<Set<String>> mcl_clusters = networkController.generateClusteringwithMCL(network);

        ArrayList<Graph<String, DefaultEdge>> cluster_graphs = new ArrayList<>();

        for (Set<String> cluster : cw_cluster) {
            if (cluster.size() >= 3) {

                Set<String> allVertices = network.vertexSet();
                Set<DefaultEdge> allEdges = network.edgeSet();

                Set<String> clusterVertices = new HashSet<>();
                ArrayList<DefaultEdge> clusterEdges = new ArrayList<>();


                for (String vertex : allVertices) {
                    if (cluster.contains(vertex)) {
                        clusterVertices.add(vertex);
                    }
                }

                Graph<String, DefaultEdge> cluster_subgraph = new AsSubgraph(network, clusterVertices, null);
                cluster_graphs.add(cluster_subgraph);
            }

        }

        File folder = new File("src/main/resources/networks");

        for (File file : folder.listFiles()) {
            if (file.isFile()) {
                file.delete();
            }
        }

        int amount = 0;
        for (int i = 0; i < cluster_graphs.size(); i++) {
            //String regular_path = "../Resources/Cluster/";
            String regular_path = "src/main/resources/networks/";
            amount++;

            Set<String> vertexes = cluster_graphs.get(i).vertexSet();
            for (String s : vertexes) {
                Decision decision = decMap.get(s);
                if (decision != null) {
                    decision.setClusterName(String.valueOf(i));
                    //TODO _decisionRepository.save(decision);
                }

            }

            String path = regular_path + "graph_cluster_" + i + ".json ";

            networkController.saveGraphToFile(cluster_graphs.get(i), path);
        }

        return amount + " Clusters computed";
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/doAnalysis",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String doAnalysis() {
        ArrayList<Decision> decs = new ArrayList<>();
        ArrayList<Decision> bverfg = _decisionRepository.findByCourtType("BVerfG");
        decs.addAll(bverfg);
        //decs.addAll(bgh);
        NetworkController networkController = new NetworkController();
        Graph<String, DefaultEdge> network = networkController.createNetwork(decs);

        Analysis analysis = new Analysis(network);

        return "Analysis done.";
    }



    @RequestMapping(
            method = RequestMethod.GET,
            path = "/decision",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Optional<Decision> getDecision(@RequestParam String docketnumber, ModelMap model) {

        Optional<Decision> decision = _decisionRepository.findByDocketnumber(docketnumber);
        model.addAttribute("message", "hello");
        return decision;
    }


}
