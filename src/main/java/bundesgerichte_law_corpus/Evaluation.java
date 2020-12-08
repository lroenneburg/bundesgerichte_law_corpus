package bundesgerichte_law_corpus;

import bundesgerichte_law_corpus.model.Decision;
import bundesgerichte_law_corpus.model.DecisionSection;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class Evaluation {


    public static void main(String[] args) throws IOException {
        //convertTextToCoNLLFormat();
        convertDecisionsForWebAnno();
    }

    private static void convertDecisionsForWebAnno() throws IOException {
        ArrayList<String> decisionIDs = new ArrayList<>();

        File folder = new File("../Resources/AnnotierSet");

        for (File f : folder.listFiles()) {
            decisionIDs.add(f.getName().split("\\.")[0]);
        }

        DataMapper dataMapper = new DataMapper();
        ArrayList<Decision> decisions = dataMapper.mapDecisionObjects(decisionIDs);

        InputStream inputStream = new FileInputStream("../OpenNLP/de-sent.bin");
        SentenceModel model = new SentenceModel(inputStream);

        SentenceDetectorME detector = new SentenceDetectorME(model);

        for (Decision decision : decisions) {
            int sentID = 0;
            int stellennummer = 0;

            ArrayList<String> docketNumber = decision.getDocketNumber();
            String decisionTitle = decision.getDecisionTitle();
            ArrayList<String> guidingPrinciple = decision.getGuidingPrinciple();
            ArrayList<String> otherOrientationSentence = decision.getOtherOrientationSentence();
            ArrayList<String> tenor = decision.getTenor();
            ArrayList<DecisionSection> fact = decision.getFact();
            ArrayList<DecisionSection> decisionReasons = decision.getDecisionReasons();
            ArrayList<DecisionSection> dissentingOpinions = decision.getDissentingOpinions();

            ArrayList<String> all_one_sentences_in_list = new ArrayList<>();
            all_one_sentences_in_list.add(decisionTitle);
            all_one_sentences_in_list.addAll(guidingPrinciple);
            all_one_sentences_in_list.addAll(otherOrientationSentence);
            all_one_sentences_in_list.addAll(tenor);

            ArrayList<DecisionSection> all_dec_section_list = new ArrayList<>();
            all_dec_section_list.addAll(fact);
            all_dec_section_list.addAll(decisionReasons);
            all_dec_section_list.addAll(dissentingOpinions);

            String dn_string = docketNumber.toString().replaceAll("\\[", "");
            dn_string = dn_string.replaceAll("\\]", "");
            dn_string = dn_string.replaceAll("\\s", "_");
            dn_string = dn_string.replaceAll("\\/", "_");

            FileWriter fileWriter = new FileWriter("../OpenNLP/webanno_" + dn_string + ".txt");
            //System.out.println("#FORMAT=WebAnno TSV 3.2");
            fileWriter.write("#FORMAT=WebAnno TSV 3.2");
            fileWriter.write("\n");
            fileWriter.write("\n");
            fileWriter.write("\n");





            for (String gp : all_one_sentences_in_list) {
                String text = gp;

                try {

                    String[] sentences = detector.sentDetect(text);

                    for (String sentence : sentences) {
                        SimpleTokenizer simpleTokenizer = SimpleTokenizer.INSTANCE;


                        Span[] spans = simpleTokenizer.tokenizePos(sentence);

                        //Span[] spans = detector.sentPosDetect(text);
                        //System.out.println("#Text=" + text);
                        fileWriter.write("#Text=" + sentence + "\n");
                        int tokenID = 1;
                        int last = 0;
                        for (Span span : spans) {
                            int begin = stellennummer + span.getStart();
                            int end = stellennummer + span.getEnd();
                            //System.out.println(sentID + "-" + tokenID + "\t" + span.getStart() + "-" + span.getEnd() + "\t" + span);
                            fileWriter.write(sentID + "-" + tokenID + "\t" + begin + "-" + end + "\t" + sentence.substring(span.getStart(), span.getEnd()) + "\n");
                            tokenID++;
                            last = end;
                        }
                        stellennummer = last + 1;
                        sentID++;
                        fileWriter.write("\n");
                    }


                    //System.out.println("");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


            for (DecisionSection ds : all_dec_section_list) {
                String text = ds.getText();

                try {

                    String[] sentences = detector.sentDetect(text);

                    for (String sentence : sentences) {
                        SimpleTokenizer simpleTokenizer = SimpleTokenizer.INSTANCE;


                        Span[] spans = simpleTokenizer.tokenizePos(sentence);

                        //Span[] spans = detector.sentPosDetect(text);
                        //System.out.println("#Text=" + text);
                        fileWriter.write("#Text=" + sentence + "\n");
                        int tokenID = 1;
                        int last = 0;
                        for (Span span : spans) {
                            //System.out.println(sentID + "-" + tokenID + "\t" + span.getStart() + "-" + span.getEnd() + "\t" + span);
                            int begin = stellennummer + span.getStart();
                            int end = stellennummer + span.getEnd();
                            fileWriter.write(sentID + "-" + tokenID + "\t" + begin + "-" + end + "\t" + sentence.substring(span.getStart(), span.getEnd()) + "\n");
                            tokenID++;
                            last = end;
                        }
                        stellennummer = last + 1;
                        sentID++;
                        fileWriter.write("\n");
                    }


                    //System.out.println("");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            fileWriter.write("\n");



            fileWriter.close();
        }

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
