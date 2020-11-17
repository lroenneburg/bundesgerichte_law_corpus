package bundesgerichte_law_corpus;

import bundesgerichte_law_corpus.model.Decision;
import bundesgerichte_law_corpus.model.DecisionSection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class Evaluation {


    public static void main(String[] args) {
        convertTextToCoNLLFormat();
    }

    private static void convertTextToCoNLLFormat() {



        ArrayList<String> decisionIDs = new ArrayList<>();

        File folder = new File("../Resources/AnnotierSet");

        for (File f : folder.listFiles()) {
            decisionIDs.add(f.getName().split("\\.")[0]);
        }

        DataMapper dataMapper = new DataMapper();
        ArrayList<Decision> decisions = dataMapper.mapDecisionObjects(decisionIDs);


        for (Decision decision : decisions) {
            ArrayList<String> docketNumber = decision.getDocketNumber();

            String decisionTitle = decision.getDecisionTitle();
            ArrayList<String> guidingPrinciple = decision.getGuidingPrinciple();
            ArrayList<String> otherOrientationSentence = decision.getOtherOrientationSentence();
            ArrayList<String> tenor = decision.getTenor();
            ArrayList<DecisionSection> decisionReasons = decision.getDecisionReasons();
            ArrayList<DecisionSection> dissentingOpinions = decision.getDissentingOpinions();

            ArrayList<String> listOfWords = new ArrayList<>();
            Pattern pattern = Pattern.compile("[\\.\\,]");


            decisionTitle = decisionTitle.replaceAll("(?i)([^A-Za-z0-9äÄöÖüÜß\\s\\/\\-])", " $1");
            decisionTitle = decisionTitle.replaceAll("([^A-Za-z0-9äÄöÖüÜß\\s\\/\\-])(?i)", "$1 ");
            String[] dec_title_parts = decisionTitle.split(" ");
            listOfWords.addAll(Arrays.asList(dec_title_parts));
            listOfWords.add("#_#");

            for (String gp : guidingPrinciple) {
                String s = gp.replaceAll("(?i)([^A-Za-z0-9äÄöÖüÜß\\s\\/\\-])", " $1");
                s = s.replaceAll("([^A-Za-z0-9äÄöÖüÜß\\s\\/\\-])(?i)", "$1 ");
                String[] gp_parts = s.split(" ");
                listOfWords.addAll(Arrays.asList(gp_parts));
                listOfWords.add("#_#");
            }

            for (String oos : otherOrientationSentence) {
                String s = oos.replaceAll("(?i)([^A-Za-z0-9äÄöÖüÜß\\s\\/\\-])", " $1");
                s = s.replaceAll("([^A-Za-z0-9äÄöÖüÜß\\s\\/\\-])(?i)", "$1 ");
                String[] oos_parts = s.split(" ");
                listOfWords.addAll(Arrays.asList(oos_parts));
                listOfWords.add("#_#");
            }

            for (String tenor_s : tenor) {
                String s = tenor_s.replaceAll("(?i)([^A-Za-z0-9äÄöÖüÜß\\s\\/\\-])", " $1");
                s = s.replaceAll("([^A-Za-z0-9äÄöÖüÜß\\s\\/\\-])(?i)", "$1 ");
                String[] tenor_s_parts = s.split(" ");
                listOfWords.addAll(Arrays.asList(tenor_s_parts));
                listOfWords.add("#_#");
            }

            for (DecisionSection section : decisionReasons) {
                String text = section.getText();
                text = text.replaceAll("(?i)([^A-Za-z0-9äÄöÖüÜß\\s\\/\\-])", " $1");
                text = text.replaceAll("([^A-Za-z0-9äÄöÖüÜß\\s\\/\\-])(?i)", "$1 ");
                String[] dr_parts = text.split(" ");
                listOfWords.addAll(Arrays.asList(dr_parts));
                listOfWords.add("#_#");

            }

            for (DecisionSection section : dissentingOpinions) {
                String text = section.getText();
                text = text.replaceAll("(?i)([^A-Za-z0-9äÄöÖüÜß\\s\\/\\-])", " $1");
                text = text.replaceAll("([^A-Za-z0-9äÄöÖüÜß\\s\\/\\-])(?i)", "$1 ");
                String[] do_parts = text.split(" ");
                listOfWords.addAll(Arrays.asList(do_parts));
                listOfWords.add("#_#");

            }

            listOfWords.removeIf(n -> n.equals(" ") || n.isEmpty());
            for (int i = 0; i < listOfWords.size(); i++) {
                if (listOfWords.get(i).equals("#_#")) {
                    listOfWords.set(i, " ");
                }
            }

            try {
                String dn = "";
                for (String dns : docketNumber) {
                    dn = "," + dn + dns;
                }
                dn = dn.replaceAll("\\/", "_");
                dn = dn.replaceFirst(",", "");

                BufferedWriter writer = new BufferedWriter(new FileWriter("../Resources/CoNLL/" + dn + ".tsv"));
                for (String element : listOfWords) {
                    writer.write(element + "\n");
                }
                writer.close();
            } catch (IOException e) {
                System.out.println("Problem with " + decision.getDecisionID());
                e.printStackTrace();
            }
        }
    }
}
