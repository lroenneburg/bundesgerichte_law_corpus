package bundesgerichte_law_corpus;

import bundesgerichte_law_corpus.model.Decision;
import com.opencsv.CSVReader;
import org.apache.commons.lang3.ArrayUtils;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 *
 */
public class Validator {

    private String _edgesDocumentPath = "resources/Coupette/bverfge-edges.csv";
    private String _nodeDocumentPath = "resources/Coupette/bverfge-nodes.csv";
    private String _ground_truth_judgesPath = "resources/EntityRecognition/ground_truth_judges.csv";

    /**
     * This constructor is used for the case, that we want to evaluate the DocketNumberNetwork against the results of
     * Corinna Coupettes approach
     */
    public Validator(Graph<String, DefaultEdge> graph, ArrayList<Decision> decisions) {
        HashMap<String, String> dictionary = getDictData(_nodeDocumentPath);

        //HashMap<String, String> backwards_dictionary = new HashMap<>();
        //for(Map.Entry<String, String> entry : dictionary.entrySet()){
        //    backwards_dictionary.put(entry.getValue(), entry.getKey());
        //}

        ArrayList<String> fsForDecisionsFromTestSet = new ArrayList<>();
        for (Decision d : decisions) {
            ArrayList<String> dn = d.getDocketNumber();
            fsForDecisionsFromTestSet.add(dn.get(0));
        }

        ArrayList<String[]> references = getReferenceData(_edgesDocumentPath, fsForDecisionsFromTestSet);


        ArrayList<String[]> my_references = new ArrayList<>();

        for (Decision de : decisions) {
            ArrayList<String> refs = de.getOccurringCitations();
            Set<String> set = new HashSet<>(refs);
            refs.clear();
            refs.addAll(set);

            String source = de.getDocketNumber().get(0);
            String source_fs = dictionary.get(source);
            for (String target_fs : refs) {
                my_references.add(new String[]{source_fs, target_fs});
            }
        }

        ArrayList<String> temp_ref_strings = new ArrayList<>();
        for (String[] reference : references) {
            String str = reference[0] + "__" + reference[1];
            temp_ref_strings.add(str);
        }
        Set<String> set = new HashSet<>(temp_ref_strings);
        temp_ref_strings.clear();
        temp_ref_strings.addAll(set);

        references.clear();
        for (String string : temp_ref_strings) {
            String[] ps = string.split("__");
            references.add(new String[]{ps[0].trim(), ps[1].trim()});
        }

        compareResults(references, my_references);

        System.out.println("test");
    }

    /**
     * This contructor is used for the case, that we want to evaluate the NER of the judges against the ground truth
     * of a law domain expert
     */
    public Validator(Graph<String, DefaultEdge> graph, ArrayList<Decision> decisions, String entity) {
        HashMap<String, ArrayList<String>> groundTruth = getGroundTruthData(_ground_truth_judgesPath);
        compareEntities(groundTruth, decisions);
    }


    private void compareResults(ArrayList<String[]> references, ArrayList<String[]> my_references) {

        int match_counter = 0;
        ArrayList<String[]> matches = new ArrayList<>();

        for (String[] cou_ref : references) {
            String cou_source = cou_ref[0];
            String cou_target = cou_ref[1];
            //System.out.println("[ " + cou_source + " ] -> [ " + cou_target + " ]");

            for (String[] my_ref : my_references) {
                String my_source = my_ref[0];
                String my_target = my_ref[1];

                if (cou_source.equals(my_source) && cou_target.equals(my_target)) {
                    match_counter++;
                    matches.add(my_ref);
                    break;

                }
            }
        }
        double accuracy = (double) match_counter / (double) references.size();
        //double precision = (double) match_counter/ ;
        System.out.println("Coupette found " + references.size() + " Decision-references for the test dataset.");
        System.out.println("I found " + my_references.size() + " Decision-references for the test dataset.");
        System.out.println("Found " + match_counter + " matching References for the test dataset.");
        System.out.println("Accuracy: " + accuracy);
        //System.out.println("Precision: " + precision);

    }


    /**
     *
     */
    private HashMap<String, String> getDictData(String filepath) {
        HashMap<String, String> docketNumberDict = new HashMap<>();


        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(filepath));
            String[] line;
            while ((line = reader.readNext()) != null) {
                String az = line[2].replace("BVerfG", "").trim();
                String fs = line[3];
                docketNumberDict.put(az, fs);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return docketNumberDict;

    }

    /**
     *
     */
    private ArrayList<String[]> getReferenceData(String filepath, ArrayList<String> decisionsfromTS) {


        ArrayList<String[]> references = new ArrayList<>();

        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(filepath));
            String[] line = reader.readNext();
            while ((line = reader.readNext()) != null) {
                String src = line[0];
                String target = line[1];
                if (decisionsfromTS.contains(src)) {
                    references.add(new String[]{src, target});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return references;
    }


    /**
     * @param path
     */
    private HashMap<String, ArrayList<String>> getGroundTruthData(String path) {
        HashMap<String, ArrayList<String>> groundTruth = new HashMap<>();

        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(path));
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (!line[0].equals("Dateiname")) {
                    String ecli_of_file = line[0];
                    ecli_of_file = ecli_of_file.replace(".pdf", "");
                    ArrayList<String> judges = new ArrayList<>();
                    String[] array = ArrayUtils.removeElement(line, ecli_of_file);
                    judges.addAll(Arrays.asList(array));
                    groundTruth.put(ecli_of_file, judges);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return groundTruth;
    }


    /**
     * @param groundTruth
     * @param decisions
     */
    private void compareEntities(HashMap<String, ArrayList<String>> groundTruth, ArrayList<Decision> decisions) {

        int right_match_total = 0;
        int gt_total = 0;
        int my_total = 0;


        for (Decision dec : decisions) {
            String ecli = dec.getEcli();
            ecli = ecli.split(":")[4].replace(".", "_");

            ArrayList<String> my_persons = dec.getOccurringJudges();
            ArrayList<String> gt_persons = groundTruth.get(ecli);
            ArrayList<String> gt_persons_clear = new ArrayList<>();
            for (String gtp : gt_persons) {
                if (!gtp.equals("") && !gtp.contains(".pdf")) {
                    gt_persons_clear.add(gtp);
                }
            }
            gt_persons.clear();
            gt_persons = gt_persons_clear;

            System.out.println("For " + ecli + " i found " + my_persons.size() + " and gt is " + gt_persons.size());
            gt_total = gt_total + gt_persons.size();
            my_total = my_total + my_persons.size();

            for (String my_per : my_persons) {
                for (String gt_per : gt_persons) {
                    if (my_per.equals(gt_per)) {
                        right_match_total++;
                        break;
                    }
                }
            }
        }

        System.out.println("Found " + right_match_total + " right matches");
        System.out.println("Found " + gt_total + " in gt");
        System.out.println("Found " + my_total + " in mine");
        System.out.println("finish");
    }


    private void compareAndEvaluate(HashMap<String, String> dictionary, ArrayList<String[]> references, Graph<String, DefaultEdge> graph) {

        int counter = 0;

        for (String[] ref : references) {
            String source = ref[0];
            String target = ref[1];

            Set<DefaultEdge> defaultEdges = graph.edgeSet();
            for (DefaultEdge de : defaultEdges) {
                String mySource = graph.getEdgeSource(de).trim();
                if (mySource.contains(",")) {
                    mySource = mySource.split(",")[0].trim();
                }
                String myTarget = graph.getEdgeTarget(de).trim();
                if (myTarget.contains(",")) {
                    myTarget = myTarget.split(",")[0].trim();
                }

                if (!mySource.contains("BVerfGE")) {
                    mySource = dictionary.get(mySource);
                }
                if (!myTarget.contains("BVerfGE")) {
                    myTarget = dictionary.get(myTarget);
                }
                if (source.equals(mySource) && target.equals(myTarget)) {
                    counter++;
                    System.out.println("Matched: " + mySource + " to " + myTarget);
                }
            }
        }
        System.out.println("Coupette found " + references.size() + " Decision-references for the test dataset.");
        System.out.println("I found " + graph.edgeSet().size() + " Decision-references for the test dataset.");
        System.out.println("Found " + counter + " matching References for the test dataset.");
    }
}
