package bundesgerichte_law_corpus;

import bundesgerichte_law_corpus.model.Decision;
import org.jgrapht.Graph;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultEdge;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class Analysis {

    public static void main(String[] args) {
        //runData();
        comparisonWithAnnotatedData();

    }

    public static void runData() {
        System.out.println("Crawl all Decisions...");
        ArrayList<String> decisionIDs = new ArrayList<>();
        File decision_folder = new File("../Resources/Decisions");
        File[] files = decision_folder.listFiles();
        for (int i = 0; i < 400; i++) {
            decisionIDs.add(files[i].getName().split("\\.")[0]);
        }

        System.out.println("Start Mapping...");
        DataMapper dataMapper = new DataMapper();
        ArrayList<Decision> decs = dataMapper.mapDecisionObjects(decisionIDs);
        System.out.println("Decisions Mapped successfully");


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

        networkController.generateDegrees(network);
        Map<String, Double> closeness = networkController.generateClosenessCentrality(network);
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
                }
                else {
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
    }

    private static void comparisonWithAnnotatedData() {


        // Read the annotated Data
        File folder_result = new File("../Resources/AnnotierResult");

        File[] folders = folder_result.listFiles();


        HashMap<String, HashMap<String, String>> annotated_outcomes = new HashMap<>();
        HashMap<String, ArrayList<String>> annotated_citations = new HashMap<>();

        ArrayList<String> training_sentences = new ArrayList<>();

        for (File inner_folder : folders) {
            if (inner_folder.getName().contains("9_AZR_475_18")) {
                System.out.println();
            }

            for (File file : inner_folder.listFiles()) {
                ArrayList<String> lines = new ArrayList<>();
                BufferedReader br;
                try {
                    br = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = br.readLine()) != null) {
                        lines.add(line);
                    }
                    br.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                LinkedHashMap<String, ArrayList<String>> annotations = new LinkedHashMap<>();
                HashMap<String, String> sentenceMap = new HashMap<>();

                ArrayList<String> irrelevantSentences = new ArrayList<>();

                String sentence = "";
                int sentCounter = 1;
                for (String line : lines) {
                    if (line.startsWith("#Text")) {
                        sentence = sentCounter + "\t" + line;
                        sentCounter++;
                        irrelevantSentences.add(line.replace("#Text=", ""));
                    }
                    String[] parts = line.split("\\t");
                    //if (!line.startsWith("#") && !line.equals("") && parts.length == 5 && (parts[parts.length - 2].equals("_") ^ parts[parts.length - 1].equals("_"))) {
                    if (!line.startsWith("#") && !line.equals("") && ((parts[parts.length - 2].matches("\\*\\[[0-9]+\\]") ^ parts[parts.length - 1].matches("\\*\\[[0-9]+\\]")) || (parts[parts.length - 2].matches("[A-Za-z]+\\[[0-9]+\\]") ^ parts[parts.length - 1].matches("[A-Za-z]+\\[[0-9]+\\]")))) {
                        String text_string = line.split("\\t")[2];
                        String text_all = line;
                        //String annotated_as_1 = line.split("\\t")[3];
                        //String annotated_as_2 = line.split("\\t")[4];
                        String annotated_as;
                        if (line.split("\\t")[3].equals("_")) {
                            annotated_as = line.split("\\t")[4];
                        } else {
                            annotated_as = line.split("\\t")[3];
                        }
                        //String text_string = line.split("[0-9]+-[0-9]+\\t[0-9]+-[0-9]+")[0];

                        if (annotations.get(annotated_as) == null) {
                            annotations.put(annotated_as, new ArrayList<>());
                            annotations.get(annotated_as).add(text_all);
                            sentenceMap.put(annotated_as, sentence);
                        } else {
                            annotations.get(annotated_as).add(text_all);
                        }

                    }
                }
                Set<String> annos = annotations.keySet();

                ArrayList<String> citations = new ArrayList<>();
                HashMap<String, String> outcome = new HashMap<>();
                String needed_format = "";

                for (String s : annos) {
                    ArrayList<String> tokens = annotations.get(s);
                    needed_format = sentenceMap.get(s);


                    StringBuffer buffer = new StringBuffer("                                                                                                                                                                                                                                                                                                                                                                                                                                                                               ");

                    String begin_first_token = tokens.get(0).split("\\t")[1];
                    String n1 = begin_first_token.split("-")[0];
                    for (String token : tokens) {
                        String[] split = token.split("\\t");
                        String position = split[1];
                        String number1 = position.split("-")[0];
                        int begin = Integer.parseInt(number1) - Integer.parseInt(n1);
                        buffer.insert(begin, split[2]);
                    }
                    String annotated_token = buffer.toString().trim();

                    if (s.matches("\\*\\[[0-9]+\\]")) {
                        if (annotated_token.matches("\\d+,\\s\\d+")) {
                            String number = s.replace("*[", "");
                            number = number.replace("]", "");
                            int n = Integer.parseInt(number) - 1;
                            String before_value = "*[" + n + "]";
                            //System.out.println(inner_folder.getName() + "_" + file.getName());
                            while(annotations.get(before_value).get(0).split("\\t")[2].matches("\\d+")) {
                                n = n -1;
                                before_value = "*[" + n + "]";
                            }
                            String pre = "";
                            if (annotations.get(before_value).get(1).split("\\t")[2].equals("-")) {
                                String first = annotations.get(before_value).get(0).split("\\t")[2];
                                String second = annotations.get(before_value).get(2).split("\\t")[2];
                                pre = first + "-" + second;
                            }
                            else {
                                pre = annotations.get(before_value).get(0).split("\\t")[2];
                            }

                            annotated_token = pre + " " + annotated_token;
                        }

                        citations.add(annotated_token);
                    } else if (s.matches("[A-Za-z]+\\[[0-9]+\\]")) {
                        String outcV = s.split("\\[")[0];
                        training_sentences.add(needed_format + "\t" + outcV);
                        irrelevantSentences.remove(annotated_token);
                        outcome.put(outcV, annotated_token);
                    }
                }

                /*
                try {
                    FileWriter fileWriter = new FileWriter("../Resources/NLPTraining/irrelevant_sentences_" + inner_folder.getName());
                    if (irrelevantSentences.size() >= 25) {
                        for (int i = 0; i < 25; i++) {
                            String s = irrelevantSentences.get(i);
                            fileWriter.write(s + "\n");
                        }
                    }
                    else {
                        for (int i = 0; i < irrelevantSentences.size(); i++) {
                            String s = irrelevantSentences.get(i);
                            fileWriter.write(s + "\n");
                        }
                    }
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */


                if (file.getName().equals("dirk")) {

                }
                String az = inner_folder.getName().replace("webanno_", "");
                az = az.replace(".txt", "");
                String annotator = file.getName().replace(".tsv", "");
                annotated_citations.put(az + "_" + annotator, citations);
                annotated_outcomes.put(az + "_" + annotator, outcome);


            }


        }



        //Now my own results
        File folder_testset = new File("../Resources/AnnotierSet");

        ArrayList<String> dIDs = new ArrayList<>();
        File[] files = folder_testset.listFiles();
        for (int i = 0; i < files.length; i++) {
            dIDs.add(files[i].getName().split("\\.")[0]);
        }
        DataMapper dataMapper = new DataMapper();
        ArrayList<Decision> decisions = dataMapper.mapDecisionObjects(dIDs);
        System.out.println();

        int findByMe = 0;
        int findByAnnos = 0;
        int findCorrectly = 0;

        for (Decision decision : decisions) {
            String dn = decision.getDocketNumber().toString();
            dn = dn.replaceAll("\\[", "");
            dn = dn.replaceAll("\\]", "");
            ArrayList<String> myoccuringCitations = decision.getOccurringCitations();

            //Set setItems = new LinkedHashSet(myoccuringCitations);
            //myoccuringCitations.clear();
            //myoccuringCitations.addAll(setItems);

            String key = dn.replaceAll("\\]", "");
            key = key.replaceAll("\\s", "_");
            key = key.replaceAll("\\/", "_");
            String key_dirk = key + "_dirk";
            String key_lauritz = key + "_lauritz";

            ArrayList<String> anno_cit_this_doc;
            System.out.println(dn);
            if (annotated_citations.get(key_dirk) != null || annotated_citations.get(key_lauritz) != null) {

                if ( annotated_citations.get(key_dirk)!= null && !annotated_citations.get(key_dirk).isEmpty()) {
                    anno_cit_this_doc = annotated_citations.get(key_dirk);
                } else if (annotated_citations.get(key_lauritz) != null && !annotated_citations.get(key_lauritz).isEmpty()){
                    anno_cit_this_doc = annotated_citations.get(key_lauritz);
                }
                else {
                    anno_cit_this_doc = new ArrayList<>();
                }
                //Set setItems2 = new LinkedHashSet(anno_cit_this_doc);
                //anno_cit_this_doc.clear();
                //anno_cit_this_doc.addAll(setItems2);

                // Alle die Annotiert wurden, die ich nicht finde
                ArrayList<String> afin = new ArrayList<>();
                afin.addAll(anno_cit_this_doc);
                afin.removeAll(myoccuringCitations);

                // Alle die ich finde, die nicht annotiert wurden
                ArrayList<String> ifan = new ArrayList<>();
                ifan.addAll(myoccuringCitations);
                ifan.removeAll(anno_cit_this_doc);

                ArrayList<String> rightButDoubles = new ArrayList<>();
                for (String s : ifan) {
                    if (s.matches("(I+|IX|VII|VI|VIII|XI|[0-9]{0,2})\\s(ABR|AZR|BvR)\\s[0-9]{1,4}\\/[0-9]{2}")) {
                        rightButDoubles.add(s);
                    }
                }
                ifan.removeAll(rightButDoubles);

                findByMe = findByMe + myoccuringCitations.size();
                findByAnnos = findByAnnos + anno_cit_this_doc.size();

                int my = myoccuringCitations.size() - ifan.size();
                int they = anno_cit_this_doc.size() - afin.size();

                if (my != they) {
                    System.out.println("Mistake" + dn);
                }
                findCorrectly = findCorrectly + my;
                //counter++;

            /*
            if (afin.size() == 0 && ifan.size() == 0) {
                fik = fik + myoccuringCitations.size();
            } else if (afin.size() == 0 && ifan.size() != 0) {

            } else if (afin.size() != 0 && ifan.size() == 0) {
                fik =
            } else if (afin.size() != 0 && ifan.size() != 0) {

            }
            else {
                System.out.println("SONDERFALL");
            }
            */

                System.out.println();
            }
        }

        System.out.println("Score");
        System.out.println();



        /*
        if (!training_sentences.isEmpty()) {
            try {
                FileWriter fileWriter = new FileWriter("../Resources/NLPTraining/training_sentences_classification.txt");
                for (String s : training_sentences) {
                    fileWriter.write(s + "\n");
                }
                fileWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */

    }


    private static void computeLengthOfDecisions() {
    }
}
