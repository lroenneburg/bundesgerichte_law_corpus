package bundesgerichte_law_corpus;

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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This class fetches the data from the rechtsprechung-im-internet.de Site and then calls the DataMapper to get the
 * Information into Java Objects
 */
public class DecisionDownloader {


    private final String _bverfgURL = "https://www.rechtsprechung-im-internet.de/jportal/docs/feed/bsjrs-bverfg.xml";
    private final String _bghURL = "https://www.rechtsprechung-im-internet.de/jportal/docs/feed/bsjrs-bgh.xml";
    private final String _bverwgURL = "https://www.rechtsprechung-im-internet.de/jportal/docs/feed/bsjrs-bverwg.xml";
    private final String _bfhURL = "https://www.rechtsprechung-im-internet.de/jportal/docs/feed/bsjrs-bfh.xml";
    private final String _bagURL = "https://www.rechtsprechung-im-internet.de/jportal/docs/feed/bsjrs-bag.xml";
    private final String _bsgURL = "https://www.rechtsprechung-im-internet.de/jportal/docs/feed/bsjrs-bsg.xml";
    private final String _bpatgURL = "https://www.rechtsprechung-im-internet.de/jportal/docs/feed/bsjrs-bpatg.xml";


    public DecisionDownloader() {


    }

    /**
     *
     * @return
     */
    public ArrayList<String> updateDatabase() throws ParseException, IOException {

        ArrayList<String> urls = new ArrayList<>();

        String url = "https://www.rechtsprechung-im-internet.de/jportal/docs/bsjrs";

        //URL URLObj = new URL(url);
        //BufferedReader in = new BufferedReader(new InputStreamReader(URLObj.openStream()));

        File file_old = new File("src/main/resources/all_decision_information.xml");
        file_old.delete();
        Files.copy(new URL(url).openStream(), Paths.get("src/main/resources/all_decision_information.xml"));


        File file = new File("src/main/resources/all_decision_information.xml");
        FileInputStream fis = new FileInputStream(file);
        Document doc = Jsoup.parse(fis, null, "", Parser.xmlParser());

        Elements decisions = doc.select("item");

        for (Element dec : decisions) {
            String d = dec.select("modified").text();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = sdf.parse(d);
            // TODO my costum time (last 2 days)
            //Date relDate = new Date(1602762380000L);
            //Date relDate = new Date(1598994459000L);
            // costum time 1 day
            //Date relDate = new Date(System.currentTimeMillis() - 86400000L);
            // TODO 12 hour version
            Date relDate = new Date(System.currentTimeMillis() - 43200000L);
            if (date.after(relDate)) {
                String link = dec.select("link").text().trim();
                urls.add(link);
            }
        }

        ArrayList<String> new_ids = new ArrayList<>();

        // Download
        for (int i = 0; i < urls.size(); i++) {

            if (i % 10 == 0) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            String dec_url = urls.get(i);

            String id = dec_url.substring(60);
            id = id.substring(0, id.indexOf(".zip"));
            new_ids.add(id);

            Files.copy(new URL(dec_url).openStream(), Paths.get("../Resources/Decisions/" + id + ".zip"));

            // Destination Path for the zip
            String zipDestinationPath = "../Resources/Decisions/" + id + ".zip";
            File destinationDirectory = new File("../Resources/Decisions");

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

            //System.out.println("Decision " + id + " stored.");
        }

        // Delete all files that are not .xml's (images, etc)
        File folder = new File("../Resources/Decisions");
        for (File fi : folder.listFiles()) {
            if (!fi.getName().endsWith(".xml")) {
                fi.delete();
            }
        }
        return new_ids;
    }

















    public ArrayList<String> downloadTheNewestDecisions() throws IOException {
        /*
        // Uncomment if you want to use a predefined Testset of 50 Decisions
        List<String> testDataSet = createTestDataSet();
        */

        // Read the RSS-Feed for every specific court
        ArrayList<String> bverfg_decision_ids = readRSSFeed(_bverfgURL);
        ArrayList<String> bgh_decision_ids = readRSSFeed(_bghURL);
        ArrayList<String> bverwg_decision_ids = readRSSFeed(_bverwgURL);
        ArrayList<String> bfh_decision_ids = readRSSFeed(_bfhURL);
        ArrayList<String> bag_decision_ids = readRSSFeed(_bagURL);
        ArrayList<String> bsg_decision_ids = readRSSFeed(_bsgURL);
        ArrayList<String> bpatg_decision_ids = readRSSFeed(_bpatgURL);

        // Sum uo all the new Decision_IDs from the RSS feeds to one ArrayList
        ArrayList<String> allIDs = new ArrayList<>();
        allIDs.addAll(bverfg_decision_ids);
        allIDs.addAll(bgh_decision_ids);
        allIDs.addAll(bverwg_decision_ids);
        allIDs.addAll(bfh_decision_ids);
        allIDs.addAll(bag_decision_ids);
        allIDs.addAll(bsg_decision_ids);
        allIDs.addAll(bpatg_decision_ids);


        // Download the XML-Files for every new decision
        for (int i = 0; i < allIDs.size(); i++) {

            // Force to download max. 10 decisions per second
            if (i % 10 == 0) {
                //System.out.println("Now waiting 1 second");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            downloadDecisionXML(allIDs.get(i));
        }

        // Delete all files that are not .xml's (images, etc)
        File folder = new File("../Resources/Decisions");
        for (File file : folder.listFiles()) {
            if (!file.getName().endsWith(".xml")) {
                file.delete();
            }
        }
        return allIDs;
    }

    /**
     * Reads the RSS Feed of the RII Site to fetch the latest decision uploads for a specific court
     *
     * @param url The url for the feed of a specific court
     * @return A List of the unique IDs for all decisions in the feed
     * @throws IOException Thrown when rssFeed is not reachable
     */
    private ArrayList<String> readRSSFeed(String url) throws IOException {
        ArrayList<String> all_decIDs = new ArrayList<>();

        URL rssURL = new URL(url);
        BufferedReader in = new BufferedReader(new InputStreamReader(rssURL.openStream()));
        String line;

        while ((line = in.readLine()) != null) {

            if (line.contains("</guid>")) {
                String decID = line.replace("<guid>", "");
                decID = decID.replaceFirst("<guid\\sisPermaLink=\".*\">", "");
                decID = decID.replace("</guid>", "").trim();
                all_decIDs.add(decID);
            }
        }

        return all_decIDs;

    }


    /**
     * Downloads the Decision ZIP File and extracts the Decision XML-File for the given ID and stores it on the filesystem
     *
     * @param decisionID The ID which XML should be downloaded
     * @throws IOException Thrown if the website is not reachable
     */
    private void downloadDecisionXML(String decisionID) throws IOException {
        // Standard url, where all decisions are stored at the webserver
        String url = "https://www.rechtsprechung-im-internet.de/jportal/docs/bsjrs/" + decisionID + ".zip";
        Files.copy(new URL(url).openStream(), Paths.get("../Resources/Decisions/" + decisionID + ".zip"));

        // Destination Path for the zip
        String zipDestinationPath = "../Resources/Decisions/" + decisionID + ".zip";
        File destinationDirectory = new File("../Resources/Decisions");

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

    }




    /**
     * Creates a predefined testset of decisions
     * @return The testdataset
     */
    private List<String> createTestDataSet() {
        String[] testdataset = {
                "KVRE412581601",
                "KVRE410221501",
                "KVRE409841501",
                "KVRE409611501",
                "KVRE409201501",
                "KVRE409391501",
                "KVRE408521501",
                "KVRE408491501",
                "KVRE408451501",
                "KVRE408401501",
                "KVRE407641401",
                "KVRE407911501",
                "KVRE407651401",
                "KVRE407141401",
                "KVRE394271101",
                "KVRE407411401",
                "KVRE407131401",
                "KVRE407001401",
                "KVRE407661401",
                "KVRE407731401",
                "KVRE407821501",
                "KVRE394361101",
                "KVRE406771401",
                "KVRE406291401",
                "KVRE406371401",
                "KVRE406381401",
                "KVRE406431401",
                "KVRE406031401",
                "KVRE393401101",
                "KVRE393971101",
                "KVRE405121401",
                "KVRE404441301",
                "KVRE404091301",
                "KVRE404201301",
                "KVRE404021301",
                "KVRE403531301",
                "KVRE403371301",
                "KVRE403381301",
                "KVRE403401301",
                "KVRE403541301",
                "KVRE400121201",
                "KVRE400071201",
                "KVRE399821201",
                "KVRE399961201",
                "KVRE399741201",
                "KVRE399661201",
                "KVRE400021201",
                "KVRE399951201",
                "KVRE399971201",
                "KVRE399791201"};
        return Arrays.asList(testdataset);
    }

}
