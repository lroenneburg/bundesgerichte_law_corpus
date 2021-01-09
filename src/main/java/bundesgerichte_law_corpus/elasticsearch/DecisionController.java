package bundesgerichte_law_corpus.elasticsearch;

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

/**
 * The decision controller manages the (non-thymeleaf) REST web client requests for the webapplication and elasticsearch
 */
@RestController
public class DecisionController {

    // The Elasticsearch Operations
    @Autowired
    ElasticsearchOperations operations;

    // The decisionRepository to get the decisions from elasticsearch
    @Autowired
    DecisionRepository _decisionRepository;


    /**
     * Adds a static test-decision with set attributes to the database
     *
     * @return The added test-decision
     */
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


    /**
     * Adds all locally saved decisions to the elasticsearch database
     *
     * @return the message that the upload was successfull
     * @throws IOException occurs, when the folder with the decision files were not found
     */
    @RequestMapping(value = "/addAllDecisionsToDB")
    public String addDB() throws IOException {

        // First we crawl all local decisions that should be added to the DB
        System.out.println("Crawl all Decisions...");
        ArrayList<String> decisionIDs = new ArrayList<>();
        File decision_folder = new File("../Resources/Decisions");
        File[] files = decision_folder.listFiles();
        for (int i = 0; i < files.length; i++) {
            decisionIDs.add(files[i].getName().split("\\.")[0]);
        }

        // We start to map the decisions to decision Objects to store them in the database
        System.out.println("Start Mapping...");
        DataMapper dataMapper = new DataMapper();
        ArrayList<Decision> decisions = dataMapper.mapDecisionObjects(decisionIDs);
        System.out.println("Decisions Mapped successfully");


        // To get a feedback how much decisions we added
        int counter = 0;

        System.out.println("Start uploading to ElasticSearch...");
        for (Decision d : decisions) {
            _decisionRepository.save(d);
            counter++;
        }
        System.out.println("Finished Uploading.");
        return counter + " Decisions added successfully to Database.";
    }

    /**
     * Gets a specific decision from the ES database
     *
     * @param decisionid the (RII-intern) decisionID of the document ("Dokumentennummer")
     * @return the decision with all information from the database
     */
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/getDecision",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Optional<Decision> getDecisionFromESDatabase(@RequestParam String decisionid) {
        return _decisionRepository.findById(decisionid);
    }


    /**
     * Gets the size of the database, so the amount of decisions stored in it
     *
     * @return the count of all decisions in the ES database
     */
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/getDBSize",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public long getSizeOfESDatabase() {
        return _decisionRepository.count();
    }


    /**
     * Generates a new Decision network with all decisions in the database, computes the PageRank, the degrees and the
     * clustering coefficient and the Cluster with the Chinese Whispers Algorithm
     *
     * @return
     */
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/computeNetworkOperations",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String computeCluster() {

        // Crawls the decisions locally, as we cant crawl that much decisions from ES at time
        System.out.println("Crawl all Decisions...");
        ArrayList<String> decisionIDs = new ArrayList<>();
        File decision_folder = new File("../Resources/Decisions");
        File[] files = decision_folder.listFiles();
        for (int i = 200; i < files.length; i++) {
            decisionIDs.add(files[i].getName().split("\\.")[0]);
        }

        // Maps the decision documents to decision objects
        System.out.println("Start Mapping...");
        DataMapper dataMapper = new DataMapper();
        ArrayList<Decision> decs = dataMapper.mapDecisionObjects(decisionIDs);
        System.out.println("Decisions Mapped successfully");

        //Decision Map, to find the decision information easy by docketNumber
        HashMap<String, Decision> decMap = new HashMap<>();

        // Replace the brackets of the docketnumber string, because there could be more than one docketnumber (array)
        for (Decision d : decs) {
            String docketNumber = d.getDocketNumber().toString();
            docketNumber = docketNumber.replaceAll("\\[", "");
            docketNumber = docketNumber.replaceAll("\\]", "");
            decMap.put(docketNumber, d);
        }

        // Starts the Networkcontroller to create the decision network and to perform network operations
        NetworkController networkController = new NetworkController();
        Graph<String, DefaultEdge> network = networkController.createNetwork(decs);
        //Generates the Pagerank
        Map<String, Double> pagerank = networkController.generatePageRank(network);

        // paused Operations to reduce time issues
        //networkController.generateDegrees(network);
        //Map<String, Double> closeness = networkController.generateClosenessCentrality(network);
        //Map<String, Double> betweenness = networkController.generateBetweennessCentrylity(network);

        Map<String, Double> clusteringCoeffizient = networkController.generateClusteringCoeffizient(network);
        List<Set<String>> cw_cluster = networkController.generateClusteringWithCW(network);

        //List<Set<String>> mcl_clusters = networkController.generateClusteringwithMCL(network);

        // List for all cluster (sub)graphs of the network
        ArrayList<Graph<String, DefaultEdge>> cluster_graphs = new ArrayList<>();

        // We only save the clusters if they have 3 nodes or more
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

                // Creates a Subgraph for every cluster
                Graph<String, DefaultEdge> cluster_subgraph = new AsSubgraph(network, clusterVertices, null);
                cluster_graphs.add(cluster_subgraph);
            }

        }

        // The folder, where we save the clusters
        File folder = new File("src/main/resources/networks");

        // We delete the old cluster graphs
        for (File file : folder.listFiles()) {
            if (file.isFile()) {
                file.delete();
            }
        }

        int amount = 0;
        // Now we save every cluster graph to the filesystem and we update the pagerank and the clusterID in the elasticsearch
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
                    _decisionRepository.save(decision);
                }

            }

            String path = regular_path + "graph_cluster_" + i + ".json ";

            // The network controller saves the Graphs as .JSON files to the folder
            networkController.saveGraphToFile(cluster_graphs.get(i), path);
        }

        // Now we add the court Data to Files to have the possibility to color them in the frontend
        File folder1 = new File("src/main/resources/networks");

        // For every graph file we read the object
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
                    // If the decision is not in the database, we set the value to NID, so the decision will be
                    // treated as a non-corpus-decision
                    jsonObject.put("court", "NID");
                    jsonObject.put("pageRank", "NID");
                }

            }

            // We write the file down to the folder again
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


    /**
     * Gets all existing Cluster Graph IDs
     *
     * @return the list of cluster IDs (unsorted)
     */
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/getCl",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ArrayList<String> getExistingClusters() {
        File folder = new File("src/main/resources/networks");

        ArrayList<Integer> cluster_ids = new ArrayList();
        for (File network_file : folder.listFiles()) {
            String filename = network_file.getName();
            String temp = filename.split("_")[2];
            int id = Integer.parseInt(temp.split("\\.")[0]);
            cluster_ids.add(id);
        }
        Collections.sort(cluster_ids);
        ArrayList<String> cluster_names = new ArrayList();
        for (int i : cluster_ids) {
            cluster_names.add("graph_cluster_" + i + ".json");
        }
        System.out.println();

        return cluster_names;
    }


    /**
     * Gets the nodes and the Edges of a cluster with a specific Name
     *
     * @param cl the clusterName
     * @return the cluster information
     */
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/getClData",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String getClusterInformation(@RequestParam String cl) {
        File file = new File("src/main/resources/networks/" + cl);
        System.out.println();
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


    /**
     * Performs a search Query on the ES, to search for decisions with a specific term in them
     *
     * @param term the term you want to search in the index
     * @return the first ten results of the query
     */
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/searchQuery",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ArrayList<Decision> testQuery(@RequestParam String term) {

        ArrayList<Decision> byCustomQuery = _decisionRepository.findByCustomQuery(term);
        return byCustomQuery;
    }


    /**
     * Performs the call to the NLP Python Service, to do the nlp tasks
     *
     * @return the message, if the task was succesfull
     */
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/doNLPTasks",
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
