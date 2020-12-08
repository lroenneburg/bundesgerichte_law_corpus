package bundesgerichte_law_corpus;

import it.unimi.dsi.fastutil.Hash;
import org.HdrHistogram.Histogram;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.gexf.SimpleGEXFImporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Analysis {

    public static void main(String[] args) {
    }

    public Analysis(Graph<String, DefaultEdge> graph) {
        computeDegreeHistograms(graph);
        computeLengthOfDecisions();
        computeLengthInAdditionToCourts();
    }

    private static void computeLengthInAdditionToCourts() {

    }

    private static void computeLengthOfDecisions() {
    }

    private static void computeDegreeHistograms(Graph<String, DefaultEdge> graph) {

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
        ArrayList<Map.Entry<String , Integer>> indegree_ranking = new ArrayList<>(sortedInDegrees.entrySet());
        ArrayList<Map.Entry<String, Integer>> top_ten_indegree = new ArrayList<>(indegree_ranking.subList(0, 10));

        outdegrees.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> sortedOutDegrees.put(x.getKey(), x.getValue()));
        ArrayList<Map.Entry<String , Integer>> outdegree_ranking = new ArrayList<>(sortedOutDegrees.entrySet());
        ArrayList<Map.Entry<String, Integer>> top_ten_outdegree = new ArrayList<>(outdegree_ranking.subList(0, 10));




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
            median_out = ((double)outds_array[outds_array.length/2] + (double)outds_array[outds_array.length/2 - 1])/2;
        }
        else {
            median_out = (double) outds_array[outds_array.length/2];
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
            median = ((double)inds_array[inds_array.length/2] + (double)inds_array[inds_array.length/2 - 1])/2;
        }
        else {
            median = (double) inds_array[inds_array.length/2];
        }
        System.out.println("Median IND " + median);


        DefaultCategoryDataset indegree_dataset = new DefaultCategoryDataset();

        for (int i : allDegreesIND.keySet()) {
            indegree_dataset.addValue(allDegreesIND.get(i).size(), "Indegree Anzahl" , String.valueOf(i));
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
}
