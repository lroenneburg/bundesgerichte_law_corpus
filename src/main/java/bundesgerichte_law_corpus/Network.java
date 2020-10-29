package bundesgerichte_law_corpus;


import bundesgerichte_law_corpus.model.Decision;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 *
 */
public class Network {

    public Network(ArrayList<Decision> decisions) {

        HashMap<ArrayList<String>, String> fundstellenMapping = createFundstellenMapping();
        System.out.println("ookay");

        Graph<String, DefaultEdge> decisionNetwork = createDecisionNetwork(decisions);
        //Validator validator = new Validator(docketNumberNetwork, decisions);

        //Graph<String, DefaultEdge> entityNetwork = createEntityNetwork(decisions);
        //Validator entity_validator = new Validator(entityNetwork, decisions, "entity");
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
