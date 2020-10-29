package bundesgerichte_law_corpus.webScraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * The Webscraper is used to crawl the decision files from the www.rechtsprechung-im-internet.de Website, to
 * store them at the filesystem to do the analysis on them later
 */
public class RIIWebScraper {


    /**
     * Creates a Crawler, which downloads all decision files by counting up the identifier number
     *
     * @throws IOException
     */
    public static void main(String[] args) {
        try {
            HashMap<String, ArrayList<Element>> allDecisionsFromDatabase = getAllDecisionsFromDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * TEST
     * @param court_dec
     */
    private void downloadNewDecisions(ArrayList<Element> court_dec) throws InterruptedException, IOException, ParseException {

        ArrayList<String> urls = new ArrayList<>();

        for (Element dec : court_dec) {
            String d = dec.select("modified").text();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = sdf.parse(d);
            Date relDate = new Date(1598220000999L);
            if (date.after(relDate)) {
                String link = dec.select("link").text().trim();
                urls.add(link);
            }
        }

        for (int i = 0; i < urls.size(); i++) {

            if (i % 15 == 0) {
                //System.out.println("Now waiting 5 seconds");
                TimeUnit.SECONDS.sleep(1);
            }

            String url = urls.get(i);

            String id = url.substring(60);
            id = id.substring(0, id.indexOf(".zip"));

            Files.copy(new URL(url).openStream(), Paths.get("../Resources/" + id + ".zip"));

            // Destination Path for the zip
            String zipDestinationPath = "../Resources/" + id + ".zip";
            File destinationDirectory = new File("../Resources");

            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipDestinationPath));
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {
                File destFile = new File(destinationDirectory, zipEntry.getName());
                String destDirPath = destinationDirectory.getCanonicalPath();
                String destFilePath = destFile.getCanonicalPath();

                if (!destFilePath.startsWith(destDirPath + File.separator)) {
                    throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
                }
                File newFile = destFile;
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();

            // We don't need the zipfile anymore, so we delete it
            File f = new File(zipDestinationPath);
            f.delete();

            System.out.println("Decision " + id + " stored.");
        }

        System.out.println("hello");
    }

    /**
     * @throws IOException
     */
    private static HashMap<String, ArrayList<Element>> getAllDecisionsFromDatabase() throws IOException {

        String url = "https://www.rechtsprechung-im-internet.de/jportal/docs/bsjrs";

        URL URLObj = new URL(url);
        BufferedReader in = new BufferedReader(new InputStreamReader(URLObj.openStream()));

        Files.copy(new URL(url).openStream(), Paths.get("src/main/resources/all_decision_information.xml"));


        File file = new File("src/main/resources/all_decision_information.xml");
        FileInputStream fis = new FileInputStream(file);
        Document doc = Jsoup.parse(fis, null, "", Parser.xmlParser());

        Elements decisions = doc.select("item");

        ArrayList<Element> bverfg_decisions = new ArrayList<>();
        ArrayList<Element> bgh_decisions = new ArrayList<>();
        ArrayList<Element> bverwg_decisions = new ArrayList<>();
        ArrayList<Element> bfh_decisions = new ArrayList<>();
        ArrayList<Element> bag_decisions = new ArrayList<>();
        ArrayList<Element> bsg_decisions = new ArrayList<>();
        ArrayList<Element> bpatg_decisions = new ArrayList<>();
        ArrayList<Element> gSdoGdB_decisions = new ArrayList<>();


        for (Element dec : decisions) {
            if (dec.select("gericht").text().contains("BVerfG")) {
                bverfg_decisions.add(dec);
            } else if (dec.select("gericht").text().contains("BGH")) {
                bgh_decisions.add(dec);
            } else if (dec.select("gericht").text().contains("BPatG")) {
                bpatg_decisions.add(dec);
            } else if (dec.select("gericht").text().contains("BAG")) {
                bag_decisions.add(dec);
            } else if (dec.select("gericht").text().contains("BSG")) {
                bsg_decisions.add(dec);
            } else if (dec.select("gericht").text().contains("BFH")) {
                bfh_decisions.add(dec);
            } else if (dec.select("gericht").text().contains("BVerwG")) {
                bverwg_decisions.add(dec);
            } else {
                gSdoGdB_decisions.add(dec);
            }

        }




        // Uncomment if you want to check your DB against the RII-Database
        /*
        ArrayList<String> allMyCollectedDecisions = new ArrayList<>();
        File folder_decs = new File("../Resources");
        for (File file_dec : folder_decs.listFiles()) {
            String name = file_dec.getName();
            String[] parts = name.split("\\.");
            allMyCollectedDecisions.add(parts[0]);
        }
        System.out.println("ok");


        ArrayList<String> groundTruthDecisions = new ArrayList<>();

        for (Element e : bverfg_decisions) {
            String dec_url = e.select("link").text();
            String[] parts = dec_url.split("\\/");
            String idzip = parts[parts.length - 1];
            String id = idzip.split("\\.")[0];
            groundTruthDecisions.add(id);
        }


        for (Element e : bgh_decisions) {
            String dec_url = e.select("link").text();
            String[] parts = dec_url.split("\\/");
            String idzip = parts[parts.length - 1];
            String id = idzip.split("\\.")[0];
            groundTruthDecisions.add(id);
        }

        for (Element e : bverwg_decisions) {
            String dec_url = e.select("link").text();
            String[] parts = dec_url.split("\\/");
            String idzip = parts[parts.length - 1];
            String id = idzip.split("\\.")[0];
            groundTruthDecisions.add(id);
        }

        for (Element e : bfh_decisions) {
            String dec_url = e.select("link").text();
            String[] parts = dec_url.split("\\/");
            String idzip = parts[parts.length - 1];
            String id = idzip.split("\\.")[0];
            groundTruthDecisions.add(id);
        }

        for (Element e : bag_decisions) {
            String dec_url = e.select("link").text();
            String[] parts = dec_url.split("\\/");
            String idzip = parts[parts.length - 1];
            String id = idzip.split("\\.")[0];
            groundTruthDecisions.add(id);
        }

        for (Element e : bsg_decisions) {
            String dec_url = e.select("link").text();
            String[] parts = dec_url.split("\\/");
            String idzip = parts[parts.length - 1];
            String id = idzip.split("\\.")[0];
            groundTruthDecisions.add(id);
        }

        for (Element e : bpatg_decisions) {
            String dec_url = e.select("link").text();
            String[] parts = dec_url.split("\\/");
            String idzip = parts[parts.length - 1];
            String id = idzip.split("\\.")[0];
            groundTruthDecisions.add(id);
        }

        for (Element e : gSdoGdB_decisions) {
            String dec_url = e.select("link").text();
            String[] parts = dec_url.split("\\/");
            String idzip = parts[parts.length - 1];
            String id = idzip.split("\\.")[0];
            groundTruthDecisions.add(id);
        }

        ArrayList<String> ueberschuss = new ArrayList<>();
        ueberschuss.addAll(allMyCollectedDecisions);
        ArrayList<String> fehlende = new ArrayList<>();
        fehlende.addAll(groundTruthDecisions);

        ueberschuss.removeAll(groundTruthDecisions);
        fehlende.removeAll(allMyCollectedDecisions);




        System.out.println("now test");
        */
























        HashMap<String, ArrayList<Element>> data = new HashMap<>();
        data.put("BVerfG", bverfg_decisions);
        data.put("BGH", bgh_decisions);
        data.put("BPatG", bpatg_decisions);
        data.put("BAG", bag_decisions);
        data.put("BSG", bsg_decisions);
        data.put("BFH", bfh_decisions);
        data.put("BVerwG", bverwg_decisions);
        data.put("GSdoGdB", gSdoGdB_decisions);

        return data;
    }

    /**
     * Downloads all Decision Files that are found with the given parameters.
     *
     * @param court_decisions
     */
    private void downloadDecisions(ArrayList<Element> court_decisions) throws IOException, InterruptedException {

        ArrayList<String> urls = new ArrayList<>();


        for (Element decision : court_decisions) {
                String link = decision.select("link").text().trim();
                urls.add(link);

        }

        for (int i = 0; i <= urls.size(); i++) {

            if (i % 15 == 0) {
                //System.out.println("Now waiting 5 seconds");
                TimeUnit.SECONDS.sleep(1);
            }

            String url = urls.get(i);

            String id = url.substring(60);
            id = id.substring(0, id.indexOf(".zip"));

            Files.copy(new URL(url).openStream(), Paths.get("../Resources/" + id + ".zip"));

            // Destination Path for the zip
            String zipDestinationPath = "../Resources/" + id + ".zip";
            File destinationDirectory = new File("../Resources");

            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipDestinationPath));
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {
                File destFile = new File(destinationDirectory, zipEntry.getName());
                String destDirPath = destinationDirectory.getCanonicalPath();
                String destFilePath = destFile.getCanonicalPath();

                if (!destFilePath.startsWith(destDirPath + File.separator)) {
                    throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
                }
                File newFile = destFile;
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();

            // We don't need the zipfile anymore, so we delete it
            File f = new File(zipDestinationPath);
            f.delete();

            System.out.println("Decision " + id + " stored.");
        }

        System.out.println("hello");
    }
}
