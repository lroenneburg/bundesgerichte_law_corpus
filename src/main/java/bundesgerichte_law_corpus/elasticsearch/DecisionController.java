package bundesgerichte_law_corpus.elasticsearch;

import bundesgerichte_law_corpus.Analysis;
import bundesgerichte_law_corpus.DataMapper;
import bundesgerichte_law_corpus.NetworkController;
import bundesgerichte_law_corpus.elasticsearch.repository.DecisionRepository;
import bundesgerichte_law_corpus.model.Decision;
import bundesgerichte_law_corpus.model.DecisionSection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jgrapht.Graph;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultEdge;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.http.MediaType;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
        _decisionRepository.save(decision1);
        return null;
    }


    @RequestMapping(value = "/addAllDecisionsToDB")
    public String addDB() throws IOException {

        System.out.println("Crawl all Decisions...");
        ArrayList<String> decisionIDs = new ArrayList<>();
        File decision_folder = new File("../Resources/Decisions");
        File[] files = decision_folder.listFiles();
        for (int i = 44998; i < files.length; i++) {
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
        //ArrayList<Decision> all = (ArrayList<Decision>) _decisionRepository.findAll();
        ArrayList<Decision> bverfg = _decisionRepository.findByCourtType("BVerfG");
        //ArrayList<Decision> bgh = _decisionRepository.findByCourtType("BGH");
        //ArrayList<Decision> bverwg = _decisionRepository.findByCourtType("BVerwG");
        //ArrayList<Decision> BFH = _decisionRepository.findByCourtType("BFH");
        //ArrayList<Decision> bag = _decisionRepository.findByCourtType("BAG");
        //ArrayList<Decision> bsg = _decisionRepository.findByCourtType("BSG");
        //ArrayList<Decision> bpatg = _decisionRepository.findByCourtType("BPatG");

        decs.addAll(bverfg);
        //decs.addAll(bgh);
        NetworkController networkController = new NetworkController();
        networkController.createNetwork(decs);
        return "Done.";
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "/computeCluster",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String computeCluster() {
        //ArrayList<Decision> decs = new ArrayList<>();
        //TODO change to findAll


        System.out.println("Crawl all Decisions...");
        ArrayList<String> decisionIDs = new ArrayList<>();
        File decision_folder = new File("../Resources/Decisions");
        File[] files = decision_folder.listFiles();
        for (int i = 200; i < files.length; i++) {
            decisionIDs.add(files[i].getName().split("\\.")[0]);
        }

        System.out.println("Start Mapping...");
        DataMapper dataMapper = new DataMapper();
        ArrayList<Decision> decs = dataMapper.mapDecisionObjects(decisionIDs);
        System.out.println("Decisions Mapped successfully");


        //ArrayList<Decision> bverfg = _decisionRepository.findByCourtType("BVerfG");
        /*
        ArrayList<Decision> bgh = _decisionRepository.findByCourtType("BGH");
        ArrayList<Decision> bverwg = _decisionRepository.findByCourtType("BVerwG");
        ArrayList<Decision> BFH = _decisionRepository.findByCourtType("BFH");
        ArrayList<Decision> bag = _decisionRepository.findByCourtType("BAG");
        ArrayList<Decision> bsg = _decisionRepository.findByCourtType("BSG");
        ArrayList<Decision> bpatg = _decisionRepository.findByCourtType("BPatG");


        decs.addAll(bgh);
        decs.addAll(bverwg);
        decs.addAll(BFH);
        decs.addAll(bag);
        decs.addAll(bsg);
        decs.addAll(bpatg);
        */

        //decs.addAll(bverfg);

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
        Map<String, Double> pagerank = networkController.generatePageRank(network);
        //networkController.generateDegrees(network);
        //Map<String, Double> closeness = networkController.generateClosenessCentrality(network);
        //Map<String, Double> betweenness = networkController.generateBetweennessCentrylity(network);
        Map<String, Double> clusteringCoeffizient = networkController.generateClusteringCoeffizient(network);
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
                    decision.setPageRank(pagerank.get(s));
                    // TODO centralities
                    //_decisionRepository.save(decision);
                }

            }

            String path = regular_path + "graph_cluster_" + i + ".json ";

            networkController.saveGraphToFile(cluster_graphs.get(i), path);
        }

        // Add Court Data to Files
        File folder1 = new File("src/main/resources/networks");

        for (File f : folder1.listFiles()) {
            String fileName = f.getName();
            BufferedReader br;
            String obj_string = "";
            try {
                br = new BufferedReader(new FileReader(f));
                String line;
                while ((line = br.readLine()) != null) {
                    obj_string += line;
                }
                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject obj = new JSONObject(obj_string);
            JSONArray nodes = obj.getJSONArray("nodes");
            for (int i = 0; i < nodes.length(); i++) {
                JSONObject jsonObject = nodes.getJSONObject(i);
                Decision current_decision = decMap.get(jsonObject.get("label"));
                if (current_decision != null) {
                    jsonObject.put("court", current_decision.getCourtType());
                    jsonObject.put("pageRank", current_decision.getPageRank());
                } else {
                    jsonObject.put("court", "NID");
                    jsonObject.put("pageRank", "NID");
                }

            }

            try {
                FileWriter fileWriter = new FileWriter("src/main/resources/networks/" + fileName);
                fileWriter.write(obj.toString());
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

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


    @RequestMapping(
            method = RequestMethod.GET,
            path = "/getCl",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ArrayList<String> getExistingClusters() {
        File folder = new File("src/main/resources/networks");

        ArrayList<String> cluster_names = new ArrayList();
        for (File network_file : folder.listFiles()) {
            String filename = network_file.getName();
            cluster_names.add(filename);
        }

        //Collections.sort(cluster_names, (v, v1) -> v.substring(13, 16).compareTo(v1));
        return cluster_names;
    }


    @RequestMapping(
            method = RequestMethod.GET,
            path = "/getClData",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String getClusterInformation(@RequestParam String cl) {
        File file = new File("src/main/resources/networks/" + cl);
        BufferedReader br;
        String obj = "";
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                obj += line;
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return obj;
    }


    @RequestMapping(
            method = RequestMethod.GET,
            path = "/searchQuery",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ArrayList<Decision> testQuery(@RequestParam String term) {


        ArrayList<Decision> byCustomQuery = _decisionRepository.findByCustomQuery(term);
        //Query searchQuery = new NativeSearchQueryBuilder().withFilter(regexpQuery("courtType", "BVerfG")).build();
        //SearchHits<Decision> articles = _decisionRepository.search(searchQuery, Decision.class, IndexCoordinates.of("bundesgerichte_decisions"));
        // NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withFields("courtType", "docketNumber", "clusterName", "pageRank").
        return byCustomQuery;
    }


    @RequestMapping(
            method = RequestMethod.GET,
            path = "/testNLP",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String testNLPService() {

        ArrayList<String> strings = new ArrayList<>();
        strings.add("Die Beschwerde gegen die Nichtzulassung der Revision in dem Urteil des 5. Zivilsenats des Oberlandesgerichts");
        strings.add("Köln vom 18. April 2007 wird auf Kosten des Klägers zurückgewiesen.");
        strings.add("Apple Inc. introduced a new mobile device today");
        strings.add("Die Gerechtigkeit wurde mit einem Pinguin besprochen.");
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = null;
        try {
            requestBody = objectMapper.writeValueAsString(strings);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:5000/ClassificationService")).POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();

        String result = "Not working";
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            result = response.body();
            System.out.println("done");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
