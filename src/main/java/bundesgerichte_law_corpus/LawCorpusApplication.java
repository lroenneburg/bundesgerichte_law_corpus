package bundesgerichte_law_corpus;

import bundesgerichte_law_corpus.elasticsearch.repository.DecisionRepository;
import bundesgerichte_law_corpus.model.Decision;
import bundesgerichte_law_corpus.model.FundstellenDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;


@SpringBootApplication
@EnableScheduling
public class LawCorpusApplication extends SpringBootServletInitializer {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(LawCorpusApplication.class, args);
        //System.out.println("started");




        /*
        ArrayList<String> decisionIDs = new ArrayList<>();
        File decision_folder = new File("../Resources/Decisions");
        File[] files = decision_folder.listFiles();
        for (int i = 0; i < files.length; i++) {
            decisionIDs.add(files[i].getName().split("\\.")[0]);

        }


        //
        DataMapper dataMapper = new DataMapper();
        ArrayList<Decision> decisions = dataMapper.mapDecisionObjects(decisionIDs);

        FileWriter myWriter = new FileWriter("../Resources/citations_short.txt");
        for (Decision d : decisions) {
            ArrayList<String> occurringCitations = d.getOccurringCitations();
            for (String s : occurringCitations) {
                fundstellen.add(s);
                myWriter.write(s + "\n");
            }
        }

        myWriter.close();

        */

        /*

        ArrayList<String> fundstellen = new ArrayList<>();
        ArrayList<String> fundstellen_od = new ArrayList<>();
        ArrayList<String> fundstellen_oredundant = new ArrayList<>();


        File file = new File("../Resources/citations_short.txt");

        BufferedReader br = new BufferedReader(new FileReader(file));

        String string;

        while ((string = br.readLine()) != null) {
            fundstellen.add(string);
        }

        Set<String> set = new HashSet<>(fundstellen);
        //fundstellen.clear();
        fundstellen_od.addAll(set);
        fundstellen_oredundant.addAll(set);
        fundstellen_oredundant.removeIf(Pattern.compile("BVerfGE.*").asPredicate());
        fundstellen_oredundant.removeIf(Pattern.compile("BFHE.*").asPredicate());
        fundstellen_oredundant.removeIf(Pattern.compile("BVerwGE.*").asPredicate());
        fundstellen_oredundant.removeIf(Pattern.compile("BGHZ.*").asPredicate());
        fundstellen_oredundant.removeIf(Pattern.compile("BSGE.*").asPredicate());
        fundstellen_oredundant.removeIf(Pattern.compile("BGHSt.*").asPredicate());
        Collections.sort(fundstellen_oredundant);

        System.out.println("look");
        System.out.println("out");

        */



        /*
        ArrayList<String> decisionIDs = new ArrayList<>();
        File decision_folder = new File("../Resources");
        File[] files = decision_folder.listFiles();
        for (int i = 0; i < files.length; i++) {
                decisionIDs.add(files[i].getName().split("\\.")[0]);

        }
        System.out.println("Crawling Finished.");
        System.out.println("Start Mapping...");
        DataMapper dataMapper = new DataMapper();
        ArrayList<Decision> decisions = dataMapper.mapDecisionObjects(decisionIDs);
        System.out.println("Decisions Mapped successfully");


        FileWriter myWriter = new FileWriter("src/main/resources/docketNumberList.txt");
        for (Decision dec : decisions) {

            try {

                String s = "";
                for (String dn : dec.getDocketNumber()) {
                    s += (dn + ", ");
                }
                s = s.substring(0, s.length() - 2);
                myWriter.write(s + "\n");

            } catch (IOException e) {
                System.out.println("An error occurred with " + dec.getDecisionID());
                e.printStackTrace();
            }

        }
        myWriter.close();
        */
        /*
        DataMapper dataMapper = new DataMapper();

        ArrayList<String> al = new ArrayList<>();
        al.add("KVRE387461001");
        ArrayList<Decision> decisions = dataMapper.mapDecisionObjects(al);
        System.out.println("yo");
        */


    }

}
