package bundesgerichte_law_corpus.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class FundstellenDictionary {

    private HashMap<String, String> _dictionary;


    public FundstellenDictionary() {
        _dictionary = new HashMap<>();
        generateDictionary();
        System.out.println("test");
    }


    /**
     * Generates the FundstellenDictionary from the Fundstellen mapping dataa
     */
    private void generateDictionary() {

        //BVerfGE Fundstellen
        File bverfge_file = new File("../Resources/Fundstellen/BVerfGE_fundstellen.txt");

        BufferedReader br1 = null;

        try {

            br1 = new BufferedReader(new FileReader(bverfge_file));

            String line;
            while ((line = br1.readLine()) != null) {
                String [] parts = line.split(";");
                _dictionary.put(parts[0].trim(), parts[1].trim());
            }
            br1.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //NJW Fundstellen
        File njw_file = new File("../Resources/Fundstellen/NJW_fundstellen.txt");
        BufferedReader br2 = null;

        try {

            br2 = new BufferedReader(new FileReader(njw_file));

            String line;
            while ((line = br2.readLine()) != null) {
                String [] parts = line.split(";");
                if (!parts[0].equals("ERROR")) {
                    _dictionary.put(parts[1].trim(), parts[0].trim());
                }
            }
            br2.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getAktenzeichenForFundstelle(String fundstelle) {
        return _dictionary.get(fundstelle);
    }

}
