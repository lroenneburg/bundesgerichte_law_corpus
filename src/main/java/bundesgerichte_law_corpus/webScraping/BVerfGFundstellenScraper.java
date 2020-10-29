package bundesgerichte_law_corpus.webScraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class BVerfGFundstellenScraper {

    public static void main(String[] args) {
        try {
            scrapeBverfGE();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void scrapeBverfGE() throws IOException {

        FileWriter myWriter = new FileWriter("src/main/resources/Fundstellen/BVerfGE.txt");
        for(int i = 1; i <= 1; i++) {
        //for (int i = 10; i <= 150; i = i + 10) {
            String url = "https://www.bundesverfassungsgericht.de/DE/Entscheidungen/Liste/" + i + "ff/liste_node.html";
            try {
                TimeUnit.SECONDS.sleep(1);
                Document document = Jsoup.connect(url).get();
                Elements lists = document.getElementsByClass("toggleEntry");
                Elements tr = lists.select("tr");
                for (int c = tr.size() - 1 ; c >= 0; c--) {
                    String fs;
                    if (tr.get(c).childNode(0).childNode(0).nodeName().equals("a")) {
                        fs = tr.get(c).childNode(0).childNode(0).childNode(0).toString();
                    }
                    else {
                        fs = tr.get(c).childNode(0).childNode(0).toString();
                    }
                    //System.out.println(fs);
                    String dn = "ERROR";
                    try {
                        dn = tr.get(c).childNode(2).childNode(0).toString();
                    } catch (Exception e) {
                        System.out.println("Problem with " + fs);
                    }

                    myWriter.write(fs + "\t\t" + dn + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error with " + url);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Error with " + url);
            }
        }
        myWriter.close();

    }
}
