package bundesgerichte_law_corpus;


import bundesgerichte_law_corpus.model.Decision;
import bundesgerichte_law_corpus.model.FundstellenDictionary;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.ClusteringAlgorithm;
import org.jgrapht.alg.scoring.BetweennessCentrality;
import org.jgrapht.alg.scoring.ClosenessCentrality;
import org.jgrapht.alg.scoring.ClusteringCoefficient;
import org.jgrapht.alg.scoring.PageRank;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.nio.gexf.GEXFExporter;
import org.jgrapht.nio.gexf.SimpleGEXFImporter;
import org.jgrapht.nio.json.JSONExporter;
import org.nlpub.watset.graph.ChineseWhispers;
import org.nlpub.watset.graph.MarkovClustering;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 */
public class NetworkController {

    private String _aktenzeichenRegEx = "(((VGS|RiZ\\s?s?\\(R\\)|KZR|VRG|RiZ|EnRB|StbSt\\s?\\(B\\)|AnwZ\\s?\\(Brfg\\)|RiSt|PatAnwSt\\s?\\(R\\)|AnwZ\\s?\\(B\\)|PatAnwZ|EnVZ|AnwSt\\s?\\(B\\)|NotSt\\s?\\(Brfg\\)|KVZ|KZB|AR\\s?\\(Ri\\)|NotZ\\s?\\(Brfg\\)|RiSt\\s?\\(B\\)|AnwZ\\s?\\(P\\)|EnZB|RiSt\\s?\\(R\\)|NotSt\\s?\\(B\\)|AnwSt|WpSt\\s?\\(R\\)|KVR|AR\\s?\\(Kart\\)|EnZR|StbSt\\s?\\(R\\)|WpSt\\s?\\(B\\)|KZA|AR\\s?\\(Enw\\)|AnwSt\\s?\\(R\\)|KRB|RiZ\\s?\\(B\\)|PatAnwSt\\s?\\(B\\)|EnVR|AnwZ|NotZ|EnZA|AR)\\s\\d+/\\d+)|((GSZ|LwZB|WpSt\\s?\\(B\\)|AnwZ|LwZR|KVZ|EnRB|PatAnwSt\\s?\\(B\\)|ARP|VGS|WpSt\\s?\\(R\\)|RiSt\\s?\\(B\\)|EnZA|KRB|AnwSt\\s?\\(R\\)|NotSt\\s?\\(Brfg\\)|EnVR|LwZA|ZB|AR\\s?\\(Vollz\\)|StB|ZR|AR\\s?\\(VS\\)|BJs|BLw|NotZ\\s?\\(Brfg\\)|RiZ\\s?\\(B\\)|PatAnwSt\\s?\\(R\\)|AK|RiZ|PatAnwZ|ARs|StbSt\\s?\\(R\\)|VRG|NotSt\\s?\\(B\\)|AR\\s?\\(Enw\\)|AR\\s?\\(VZ\\)|StE|KVR|AR\\s?\\(Ri\\)|AR|AnwSt|NotZ|StbSt\\s?\\(B\\)|StR|ZA|AnwZ\\s?\\(B\\)|EnZR|AR\\s?\\(Kart\\)|GSSt|AnwZ\\s?\\(P\\)|ZR\\s?\\(Ü\\)|AnwZ\\s?\\(Brfg\\)|KZB|BGns|KZR|RiSt|KZA|BAusl|AnwSt\\s?\\(B\\)|BGs|RiZ\\s?\\(R\\)|EnZB|RiSt\\s?\\(R\\)|ARZ|EnVZ)\\s\\d+/\\d+)|([I+|IV|V|VI|VII|VIII|IX|X|XI|XII|1-6]+[a-z]?\\s[A-Za-z\\(\\)]{2,20}\\s\\d+/\\d\\d))";

    public NetworkController() {
        //HashMap<ArrayList<String>, String> fundstellenMapping = createFundstellenMapping();
        System.out.println("ookay");

        //createNetwork(decisions);

        //Graph<String, DefaultEdge> decisionNetwork = createDecisionNetwork(decisions);
        //Validator validator = new Validator(docketNumberNetwork, decisions);

        //Graph<String, DefaultEdge> entityNetwork = createEntityNetwork(decisions);
        //Validator entity_validator = new Validator(entityNetwork, decisions, "entity");

        Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        SimpleGEXFImporter simpleGEXFImporter = new SimpleGEXFImporter();

        //try {
            //simpleGEXFImporter.importGraph(graph, new FileReader(new File("../Resources/network_graph.gexf")));
        //} catch (FileNotFoundException e) {
        //    e.printStackTrace();
        //}

        Set<String> strings = graph.vertexSet();
        System.out.println();

    }

    public Graph<String, DefaultEdge> createNetwork(ArrayList<Decision> decisions) {

        HashMap<String, Decision> testmap = new HashMap<>();

        for (Decision d : decisions) {
            testmap.put(d.getDocketNumber().toString(), d);
        }

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
                String decE = dec.replaceAll("  ", " ");
                String dn;
                if (!dec.matches(_aktenzeichenRegEx)) {
                    dn = fundstellenDictionary.getAktenzeichenForFundstelle(decE);
                }
                else {
                    dn = decE;
                }

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
        return graph;
    }

    public void saveGraphToFile(Graph<String, DefaultEdge> graph, String path) {

        Set<String> strings = graph.vertexSet();
        ArrayList<String> list = new ArrayList<>();
        list.addAll(strings);

        if (graph.vertexSet().size() > 5 && !path.equals("../Resources/network_graph.gexf")) {


            String sid = path.replaceAll("[^\\d]", " ").trim();
            int id = Integer.parseInt(sid);
            ArrayList<String> entries = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                int item = new Random().nextInt(strings.size());
                entries.add(list.get(item));
            }

            try {
                FileWriter fileWriter = new FileWriter("../Resources/ClusterEvaluation/blindtest_" + id + ".txt");
                for (int i = 0; i < entries.size(); i++) {
                    fileWriter.write(entries.get(i));
                    fileWriter.write("\n");
                }
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


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
        } else if (path.contains(".gexf")) {
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
        } else {
            System.out.println("Dateityp nicht erkennbar. Graph kann nicht exportiert werden.");
        }


    }


    public Map<String, Double> generatePageRank(Graph<String, DefaultEdge> network) {

        //importer.importGraph(graph, networkfile);
        Set<String> vertexes = network.vertexSet();
        Set<DefaultEdge> defaultEdges = network.edgeSet();

        PageRank<String, DefaultEdge> pageRank = new PageRank<>(network);

        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();
        Map<String, Double> scores = pageRank.getScores();


        scores.entrySet().stream().sorted(Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

        ArrayList<Entry<String, Double>> score_ranking = new ArrayList<>(sortedMap.entrySet());

        ArrayList<Entry<String, Double>> top_twenty = new ArrayList<>(score_ranking.subList(0, 20));

        try {
            FileWriter fileWriter = new FileWriter("../Resources/pagerank_result.txt");
            for (Entry entry : score_ranking) {
                fileWriter.write(entry.getKey().toString() + ": " + entry.getValue() + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        for (Entry entry : top_twenty) {
            System.out.println(entry.getKey().toString() + ": " + entry.getValue());
        }
        System.out.println();
        return scores;
    }

    public void generateDegrees(Graph<String, DefaultEdge> graph) {

        HashMap<String, Integer> outdegrees = new HashMap<>();
        HashMap<String, Integer> indegrees = new HashMap<>();

        HashMap<Integer, ArrayList<String>> allDegreesIND = new HashMap<>();
        HashMap<Integer, ArrayList<String>> allDegreesOUTD = new HashMap<>();

        for (String vertex : graph.vertexSet()) {
            int outdegree = graph.outDegreeOf(vertex);
            int indegree = graph.inDegreeOf(vertex);
            int degree = graph.degreeOf(vertex);
            outdegrees.put(vertex, outdegree);
            indegrees.put(vertex, indegree);

            if (!allDegreesIND.containsKey(indegree)) {
                allDegreesIND.put(indegree, new ArrayList<>());
            }
            allDegreesIND.get(indegree).add(vertex);

            if (!allDegreesOUTD.containsKey(outdegree)) {
                allDegreesOUTD.put(outdegree, new ArrayList<>());
            }
            allDegreesOUTD.get(outdegree).add(vertex);
        }

        LinkedHashMap<String, Integer> sortedInDegrees = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> sortedOutDegrees = new LinkedHashMap<>();

        indegrees.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> sortedInDegrees.put(x.getKey(), x.getValue()));
        ArrayList<Map.Entry<String, Integer>> indegree_ranking = new ArrayList<>(sortedInDegrees.entrySet());
        ArrayList<Map.Entry<String, Integer>> top_ten_indegree = new ArrayList<>(indegree_ranking.subList(0, 10));

        outdegrees.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> sortedOutDegrees.put(x.getKey(), x.getValue()));
        ArrayList<Map.Entry<String, Integer>> outdegree_ranking = new ArrayList<>(sortedOutDegrees.entrySet());
        ArrayList<Map.Entry<String, Integer>> top_ten_outdegree = new ArrayList<>(outdegree_ranking.subList(0, 10));

        System.out.println("Top Ten IND: ");
        for (Entry entry : top_ten_indegree) {
            System.out.println(entry.getKey().toString() + ": " + entry.getValue());
        }

        try {
            FileWriter fileWriter = new FileWriter("../Resources/IND_result.txt");
            for (Entry entry : indegree_ranking) {
                fileWriter.write(entry.getKey().toString() + ": " + entry.getValue() + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Top Ten OUTD: ");
        for (Entry entry : top_ten_outdegree) {
            System.out.println(entry.getKey().toString() + ": " + entry.getValue());
        }

        try {
            FileWriter fileWriter = new FileWriter("../Resources/OUTD_result.txt");
            for (Entry entry : outdegree_ranking) {
                fileWriter.write(entry.getKey().toString() + ": " + entry.getValue() + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();

        // #####

        ArrayList<Integer> outds = new ArrayList<>();
        for (Map.Entry<String, Integer> e : sortedOutDegrees.entrySet()) {
            outds.add(e.getValue());
        }
        int[] outds_array = new int[outds.size()];
        for (int i = 0; i < outds.size(); i++) {
            outds_array[i] = outds.get(i);
        }


        // Mean Outdegree
        double total_out = 0.0;
        for (int i : outds_array) {
            total_out += i;
        }
        double mean_out = total_out / outds_array.length;
        System.out.println("Mean OUTD " + mean_out);

        Arrays.sort(outds_array);
        // Median Outdegree
        double median_out;
        if (outds_array.length % 2 == 0) {
            median_out = ((double) outds_array[outds_array.length / 2] + (double) outds_array[outds_array.length / 2 - 1]) / 2;
        } else {
            median_out = (double) outds_array[outds_array.length / 2];
        }
        System.out.println("Median OUTD " + median_out);


        ArrayList<Integer> inds = new ArrayList<>();
        for (Map.Entry<String, Integer> e : sortedInDegrees.entrySet()) {
            inds.add(e.getValue());
        }

        int[] inds_array = new int[inds.size()];

        for (int i = 0; i < inds.size(); i++) {
            inds_array[i] = inds.get(i);
        }


        // Mean Indegree
        double total = 0.0;
        for (int i : inds_array) {
            total += i;
        }
        double mean = total / inds_array.length;
        System.out.println("Mean IND " + mean);

        Arrays.sort(inds_array);
        // Median Indegree
        double median;
        if (inds_array.length % 2 == 0) {
            median = ((double) inds_array[inds_array.length / 2] + (double) inds_array[inds_array.length / 2 - 1]) / 2;
        } else {
            median = (double) inds_array[inds_array.length / 2];
        }
        System.out.println("Median IND " + median);


        DefaultCategoryDataset indegree_dataset = new DefaultCategoryDataset();

        for (int i : allDegreesIND.keySet()) {
            indegree_dataset.addValue(allDegreesIND.get(i).size(), "Indegree Anzahl", String.valueOf(i));
        }

        JFreeChart indegree_chart = ChartFactory.createBarChart("Indegree Verteilung", "Indegree", "Anzahl der Entscheidungen", indegree_dataset, PlotOrientation.VERTICAL, true, true, false);
        //HistogramDataset dataset = new HistogramDataset();
        //dataset.setType(HistogramType.RELATIVE_FREQUENCY);


        //JFreeChart chart = ChartFactory.createHistogram("Indegree Verteilung", "Indegree", "Anzahl der Entscheidungen", dataset, PlotOrientation.VERTICAL, false, false, false);


        DefaultCategoryDataset outdegree_dataset = new DefaultCategoryDataset();

        for (int i : allDegreesOUTD.keySet()) {
            outdegree_dataset.addValue(allDegreesOUTD.get(i).size(), "Anzahl Outdegree", String.valueOf(i));
        }
        JFreeChart outdegree_chart = ChartFactory.createBarChart("Outdegree Verteilung", "Outdegree", "Anzahl der Entscheidungen", outdegree_dataset, PlotOrientation.VERTICAL, true, true, false);


        try {
            ChartUtilities.saveChartAsPNG(new File("../Resources/Analyse/indegree_chart.png"), indegree_chart, 500, 300);
            ChartUtilities.saveChartAsPNG(new File("../Resources/Analyse/outdegree_chart.png"), outdegree_chart, 500, 300);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(".");

    }

    public Map<String, Double> generateClosenessCentrality(Graph<String, DefaultEdge> graph) {
        ClosenessCentrality<String, DefaultEdge> closenessCentrality = new ClosenessCentrality<>(graph);
        Map<String, Double> closeness_scores = closenessCentrality.getScores();

        LinkedHashMap<String, Double> closeness_sortedMap = new LinkedHashMap<>();
        closeness_scores.entrySet().stream().sorted(Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> closeness_sortedMap.put(x.getKey(), x.getValue()));

        ArrayList<Entry<String, Double>> closeness_ranking = new ArrayList<>(closeness_sortedMap.entrySet());
        ArrayList<Entry<String, Double>> closeness_top_twenty = new ArrayList<>(closeness_ranking.subList(0, 20));
        System.out.println("Top Twenty close: ");
        for (Entry entry : closeness_top_twenty) {
            System.out.println(entry.getKey().toString() + ": " + entry.getValue());
        }

        try {
            FileWriter fileWriter = new FileWriter("../Resources/closeness_result.txt");
            for (Entry entry : closeness_ranking) {
                fileWriter.write(entry.getKey().toString() + ": " + entry.getValue() + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return closeness_scores;
    }

    public Map<String, Double> generateBetweennessCentrylity(Graph<String, DefaultEdge> graph) {

        BetweennessCentrality<String, DefaultEdge> betweennessCentrality = new BetweennessCentrality<>(graph);
        Map<String, Double> betweenness_scores = betweennessCentrality.getScores();

        LinkedHashMap<String, Double> betweenness_sortedMap = new LinkedHashMap<>();

        betweenness_scores.entrySet().stream().sorted(Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> betweenness_sortedMap.put(x.getKey(), x.getValue()));

        ArrayList<Entry<String, Double>> betweenness_ranking = new ArrayList<>(betweenness_sortedMap.entrySet());

        ArrayList<Entry<String, Double>> betweenness_top_twenty = new ArrayList<>(betweenness_ranking.subList(0, 20));
        System.out.println("Top Twenty between: ");
        for (Entry entry : betweenness_top_twenty) {
            System.out.println(entry.getKey().toString() + ": " + entry.getValue());
        }


        try {
            FileWriter fileWriter = new FileWriter("../Resources/betweenness_result.txt");
            for (Entry entry : betweenness_ranking) {
                fileWriter.write(entry.getKey().toString() + ": " + entry.getValue() + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return betweenness_scores;

    }

    public Map<String, Double> generateClusteringCoeffizient(Graph<String, DefaultEdge> graph) {

        Graph<String, DefaultEdge> undirectedGraph = new AsUndirectedGraph(graph);

        ClusteringCoefficient<String, DefaultEdge> clusteringCoefficient = new ClusteringCoefficient<>(undirectedGraph);
        double averageClusteringCoefficient = clusteringCoefficient.getAverageClusteringCoefficient();
        double globalClusteringCoefficient = clusteringCoefficient.getGlobalClusteringCoefficient();
        Map<String, Double> clcoeff_scores = clusteringCoefficient.getScores();

        LinkedHashMap<String, Double> clcoeff_sortedMap = new LinkedHashMap<>();
        clcoeff_scores.entrySet().stream().sorted(Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> clcoeff_sortedMap.put(x.getKey(), x.getValue()));

        ArrayList<Entry<String, Double>> clcoeff_ranking = new ArrayList<>(clcoeff_sortedMap.entrySet());
        ArrayList<Entry<String, Double>> clcoeff_top_twenty = new ArrayList<>(clcoeff_ranking.subList(0, 20));
        System.out.println("Average: " + averageClusteringCoefficient);
        System.out.println("Global: " + globalClusteringCoefficient);
        System.out.println("Top Twenty ClCoeff: ");
        for (Entry entry : clcoeff_top_twenty) {
            System.out.println(entry.getKey().toString() + ": " + entry.getValue());
        }
        System.out.println();

        try {
            FileWriter fileWriter = new FileWriter("../Resources/clustercoeffizient_result.txt");
            fileWriter.write("Average: " + averageClusteringCoefficient + "\n");
            fileWriter.write("Global: " + globalClusteringCoefficient + "\n");
            for (Entry entry : clcoeff_ranking) {
                fileWriter.write(entry.getKey().toString() + ": " + entry.getValue() + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return clcoeff_scores;
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
