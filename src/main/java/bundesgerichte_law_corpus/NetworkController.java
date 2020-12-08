package bundesgerichte_law_corpus;


import bundesgerichte_law_corpus.elasticsearch.repository.DecisionRepository;
import bundesgerichte_law_corpus.model.Decision;
import bundesgerichte_law_corpus.model.FundstellenDictionary;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ClusteringAlgorithm;
import org.jgrapht.alg.scoring.PageRank;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.nio.gexf.GEXFExporter;
import org.jgrapht.nio.json.JSONEventDrivenImporter;
import org.jgrapht.nio.json.JSONExporter;
import org.nlpub.watset.graph.ChineseWhispers;
import org.nlpub.watset.graph.MarkovClustering;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 */
public class NetworkController {

    @Autowired
    DecisionRepository _decisionRepository;

    public NetworkController() {
        //HashMap<ArrayList<String>, String> fundstellenMapping = createFundstellenMapping();
        System.out.println("ookay");

        //createNetwork(decisions);

        //Graph<String, DefaultEdge> decisionNetwork = createDecisionNetwork(decisions);
        //Validator validator = new Validator(docketNumberNetwork, decisions);

        //Graph<String, DefaultEdge> entityNetwork = createEntityNetwork(decisions);
        //Validator entity_validator = new Validator(entityNetwork, decisions, "entity");


    }

    public Graph<String, DefaultEdge> createNetwork(ArrayList<Decision> decisions) {

        FundstellenDictionary fundstellenDictionary = new FundstellenDictionary();

        Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        //for (Decision decision : decisions) {
        for (int i = 0; i < decisions.size(); i++) {
            ArrayList<String> docketnumbers = decisions.get(i).getDocketNumber();
            String dnr = docketnumbers.toString();
            dnr = dnr.replaceAll("\\[", "");
            dnr = dnr.replaceAll("\\]", "");
            graph.addVertex(dnr);

            ArrayList<String> relatedDecisions = decisions.get(i).getOccurringCitations();
            for (String dec : relatedDecisions) {
                String dn = fundstellenDictionary.getAktenzeichenForFundstelle(dec);
                if (dn != null) {
                    String dockNr = docketnumbers.toString();
                    dockNr = dockNr.replaceAll("\\[", "");
                    dockNr = dockNr.replaceAll("\\]", "");
                    graph.addVertex(dn);
                    graph.addEdge(dockNr, dn);
                }

            }


        }

        saveGraphToFile(graph, "../Resources/network_graph.gexf");
        return  graph;
    }

    public void saveGraphToFile(Graph<String, DefaultEdge> graph, String path) {

        if (path.contains(".json")) {
            JSONExporter<String, DefaultEdge> jsonExporter = new JSONExporter();

            jsonExporter.setVertexAttributeProvider((v) -> {
                Map<String, Attribute> map = new LinkedHashMap<>();
                map.put("label", DefaultAttribute.createAttribute(v));
                return map;
            });

            Writer writer1 = new StringWriter();
            jsonExporter.exportGraph(graph, writer1);

            try {
                FileWriter myWriter = new FileWriter(path);
                myWriter.write(writer1.toString());
                myWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        else if (path.contains(".gexf")) {
            GEXFExporter<String, DefaultEdge> exporter = new GEXFExporter<>();

            exporter.setVertexAttributeProvider((v) -> {
                Map<String, Attribute> map = new LinkedHashMap<>();
                map.put("label", DefaultAttribute.createAttribute(v));
                return map;
            });
            Writer writer = new StringWriter();
            exporter.exportGraph(graph, writer);

            try {
                FileWriter myWriter = new FileWriter(path);
                myWriter.write(writer.toString());
                myWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //System.out.println(writer.toString());
        }
        else {
            System.out.println("Dateityp nicht erkennbar. Graph kann nicht exportiert werden.");
        }


    }


    public ArrayList<Entry<String, Double>> generatePageRank(Graph<String, DefaultEdge> network) {

        //File networkfile = new File("../Resources/network_graph.gexf");

        //Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        //SimpleGEXFImporter<String, DefaultEdge> importer = new SimpleGEXFImporter<>();


        //importer.importGraph(graph, networkfile);
        Set<String> vertexes = network.vertexSet();
        Set<DefaultEdge> defaultEdges = network.edgeSet();

        PageRank<String, DefaultEdge> pageRank = new PageRank<>(network);

        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
        Map<String, Double> scores = pageRank.getScores();

        scores.entrySet().stream().sorted(Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

        ArrayList<Entry<String , Double>> score_ranking = new ArrayList<>(sortedMap.entrySet());

        ArrayList<Entry<String, Double>> top_twenty = new ArrayList<>(score_ranking.subList(0, 20));

        System.out.println("loe");
        return score_ranking;
    }

    public List<Set<String>> generateClusteringWithCW(Graph<String, DefaultEdge> network) {

        // Chinese Whispers
        Graph<String, DefaultEdge> undirectedGraph = new AsUndirectedGraph(network);
        ChineseWhispers<String, DefaultEdge> chineseWhispers = ChineseWhispers.<String, DefaultEdge>builder().apply(undirectedGraph);
        ClusteringAlgorithm.Clustering<String> clustering = chineseWhispers.getClustering();
        List<Set<String>> clusters = clustering.getClusters();
        Collections.sort(clusters, (o1, o2) -> Integer.valueOf(o2.size()).compareTo(o1.size()));
        System.out.println("CW Clustering completed.");

        return clusters;
    }

    public List<Set<String>> generateClusteringwithMCL(Graph<String, DefaultEdge> network) {

        // Markov-Clustering
        Graph<String, DefaultEdge> undirectedGraph = new AsUndirectedGraph(network);

        //ChineseWhispers<String, DefaultEdge> chineseWhispers = ChineseWhispers.<String, DefaultEdge>builder().apply(undirectedGraph);
        MarkovClustering<String, DefaultEdge> markovClustering = MarkovClustering.<String, DefaultEdge>builder().apply(undirectedGraph);
        ClusteringAlgorithm.Clustering<String> clustering_mcl = markovClustering.getClustering();
        List<Set<String>> clusters = clustering_mcl.getClusters();
        Collections.sort(clusters, (o1, o2) -> Integer.valueOf(o2.size()).compareTo(o1.size()));
        System.out.println("MCL Clustering completed.");

        return clusters;
    }






    private HashMap<ArrayList<String>, String> createFundstellenMapping() {

        HashMap<ArrayList<String>, String> fundstelleToDocketNumber = new HashMap<>();
        String citationRegEx = "BVerfGE\\s*\\d{1,3},\\s\\d{1,3}(?:.?\\s*?[(<\\[].*?[\\])>])?(?:[,;]\\s*?\\d{1,3},\\s*?\\d{1,3}(?:.?\\s*?[(<\\[].*?[\\])>])?)*";

        //Temp
        //ArrayList<String> allDifferentValues = new ArrayList<>();

        File folder = new File("C:/Users/lenna/Desktop/Bachelorarbeit/FundstellenMapping/");
        File[] files = folder.listFiles();
        for (File file : files) {
            if (!file.getName().startsWith("NO_DATA_")) {

                ArrayList<String> docketNumber = new ArrayList<>();
                ArrayList<String> fundstellen = new ArrayList<>();
                ArrayList<String> citedBy = new ArrayList<>();

                Path fileName = Path.of("C:/Users/lenna/Desktop/Bachelorarbeit/FundstellenMapping/" + file.getName());
                try {
                    String string = Files.readString(fileName);
                    String[] strings = string.split("\n");
                    List<String> parts = Arrays.asList(strings);
                    List<String> dnPart = Arrays.asList(parts.get(0).substring(15).trim().split(","));
                    List<String> fsPart = parts.subList(parts.indexOf("--------------------") + 2, parts.lastIndexOf("--------------------") - 1);
                    List<String> cbPart = parts.subList(parts.lastIndexOf("--------------------") + 2, parts.size());
                    docketNumber.addAll(dnPart);
                    fundstellen.addAll(fsPart);
                    citedBy.addAll(cbPart);

                    //allDifferentValues.addAll(fundstellen);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                for (String fs : fundstellen) {
                    if (fs.matches(citationRegEx)) {
                        fundstelleToDocketNumber.put(docketNumber, fs);
                    }
                }


            }
        }
        //ArrayList<String> nowAllDiffVal = new ArrayList<>();
        //for (String s : allDifferentValues) {
        //    nowAllDiffVal.add(s.split("\\s")[0]);
        //}
        //Set setItems = new LinkedHashSet(nowAllDiffVal);
        //nowAllDiffVal.clear();
        //nowAllDiffVal.addAll(setItems);

        return fundstelleToDocketNumber;
    }


    /**
     *
     */
    private Graph<String, DefaultEdge> createEntityNetwork(ArrayList<Decision> decisions) {
        Graph<String, DefaultEdge> entity_graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        for (Decision dec : decisions) {
            ArrayList<String> dn = dec.getDocketNumber();
            for (String d : dn) {
                entity_graph.addVertex(d);
            }
            ArrayList<String> persons = dec.getOccurringJudges();
            for (String person : persons) {
                entity_graph.addVertex(person);
            }


        }

        for (Decision dec : decisions) {
            ArrayList<String> persons = dec.getOccurringJudges();
            ArrayList<String> dn = dec.getDocketNumber();
            Set<String> vertexes = entity_graph.vertexSet();
            for (String per : persons) {
                if (vertexes.contains(per)) {
                    for (String d : dn) {
                        entity_graph.addEdge(per, d);
                    }
                }
            }
        }
        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>(v -> {
            v = v.replace('/', '_');
            v = v.replace(" ", "_");
            v = v.replace(",", "_");
            v = v.replace("-", "_");
            v = v.replace("ä", "ae");
            v = v.replace("Ä", "Ae");
            v = v.replace("ö", "oe");
            v = v.replace("Ö", "Oe");
            v = v.replace("ü", "ue");
            v = v.replace("Ü", "Ue");
            v = v.replace("ß", "ss");
            v = "_" + v;
            return v;
        });
        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(v));
            return map;
        });
        Writer writer = new StringWriter();
        exporter.exportGraph(entity_graph, writer);
        System.out.println(writer.toString());

        return entity_graph;
    }


    /**
     *
     */
    private Graph<String, DefaultEdge> createDecisionNetwork(ArrayList<Decision> decs) {

        Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        for (Decision dec : decs) {
            ArrayList<String> docketNumberList = dec.getDocketNumber();
            // Create a vertex for every decision in DB
            String dn_vertex = "";
            for (String dn : docketNumberList) {
                //dn_vertex = dn_vertex + dn + ","
            }
            //TODO remove graph.addVertex(dn);
            // Create a Vertex for every pointed decision
            for (String occuringCit : dec.getOccurringCitations()) {
                graph.addVertex(occuringCit);
            }
        }


        for (Decision dec : decs) {
            ArrayList<String> occCits = dec.getOccurringCitations();
            ArrayList<String> dn = dec.getDocketNumber();
            Set<String> vertexes = graph.vertexSet();

            for (String oc : occCits) {
                if (vertexes.contains(oc)) {
                    for (String d : dn) {
                        graph.addEdge(d, oc);
                    }
                }
            }
        }


        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>(v -> {
            v = v.replace('/', '_');
            v = v.replace(" ", "_");
            v = v.replace(",", "_");
            v = v.replace("(", "_");
            v = v.replace(")", "_");
            v = "_" + v;
            return v;
        });
        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(v));
            return map;
        });
        Writer writer = new StringWriter();
        exporter.exportGraph(graph, writer);
        System.out.println(writer.toString());

        return graph;
    }
}
