package bundesgerichte_law_corpus.webScraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FundstellenScraper {

    private static String _aktenzeichenRegEx = "(((VGS|RiZ\\s?s?\\(R\\)|KZR|VRG|RiZ|EnRB|StbSt\\s?\\(B\\)|AnwZ\\s?\\(Brfg\\)|RiSt|PatAnwSt\\s?\\(R\\)|AnwZ\\s?\\(B\\)|PatAnwZ|EnVZ|AnwSt\\s?\\(B\\)|NotSt\\s?\\(Brfg\\)|KVZ|KZB|AR\\s?\\(Ri\\)|NotZ\\s?\\(Brfg\\)|RiSt\\s?\\(B\\)|AnwZ\\s?\\(P\\)|EnZB|RiSt\\s?\\(R\\)|NotSt\\s?\\(B\\)|AnwSt|WpSt\\s?\\(R\\)|KVR|AR\\s?\\(Kart\\)|EnZR|StbSt\\s?\\(R\\)|WpSt\\s?\\(B\\)|KZA|AR\\s?\\(Enw\\)|AnwSt\\s?\\(R\\)|KRB|RiZ\\s?\\(B\\)|PatAnwSt\\s?\\(B\\)|EnVR|AnwZ|NotZ|EnZA|AR)\\s\\d+/\\d+)|" +
            "((GSZ|LwZB|WpSt\\s?\\(B\\)|AnwZ|LwZR|KVZ|EnRB|PatAnwSt\\s?\\(B\\)|ARP|VGS|WpSt\\s?\\(R\\)|RiSt\\s?\\(B\\)|EnZA|KRB|AnwSt\\s?\\(R\\)|NotSt\\s?\\(Brfg\\)|EnVR|LwZA|ZB|AR\\s?\\(Vollz\\)|StB|ZR|AR\\s?\\(VS\\)|BJs|BLw|NotZ\\s?\\(Brfg\\)|RiZ\\s?\\(B\\)|PatAnwSt\\s?\\(R\\)|AK|RiZ|PatAnwZ|ARs|StbSt\\s?\\(R\\)|VRG|NotSt\\s?\\(B\\)|AR\\s?\\(Enw\\)|AR\\s?\\(VZ\\)|StE|KVR|AR\\s?\\(Ri\\)|AR|AnwSt|NotZ|StbSt\\s?\\(B\\)|StR|ZA|AnwZ\\s?\\(B\\)|EnZR|AR\\s?\\(Kart\\)|GSSt|AnwZ\\s?\\(P\\)|ZR\\s?\\(Ü\\)|AnwZ\\s?\\(Brfg\\)|KZB|BGns|KZR|RiSt|KZA|BAusl|AnwSt\\s?\\(B\\)|BGs|RiZ\\s?\\(R\\)|EnZB|RiSt\\s?\\(R\\)|ARZ|EnVZ)\\s\\d+/\\d+)|" +
            "([I+|IV|V|VI|VII|VIII|IX|X|XI|XII|1-6]+[a-z]?\\s[A-Za-z\\(\\)]{2,20}\\s\\d+/\\d\\d))";

    public static void main(String[] args) {


        //scrapeFundstellenBO();
        //scrapeNachzugBO();
        //checkFailuresFundstellenBO();
        //processFundstellenBO();
        compareLists();

        /*
        try {
            scrapeBverfGE();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    private static void compareLists() {

        ArrayList<String> known_fundstellen = new ArrayList<>();
        ArrayList<String> alle_aktenzeichen = new ArrayList<>();
        ArrayList<String> alle_fs = new ArrayList<>();
        ArrayList<String> alle_fs_wo_az_bekannt = new ArrayList<>();


        File allKnown = new File("../Resources/Fundstellen/alle_aktenzeichen_mit_fundstelle.txt");
        File alldocketnumbers = new File("../Resources/Fundstellen/docketNumberList.txt");
        File all_fs = new File("../Resources/citations_short.txt");
        File all_fs_wo_az_known = new File("../Resources/Fundstellen/alle_fundstellen_mit_aktenzeichen.txt");

        BufferedReader br1 = null;

        try {

            br1 = new BufferedReader(new FileReader(allKnown));

            String line;
            while ((line = br1.readLine()) != null) {
                known_fundstellen.add(line);
            }
            br1.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        BufferedReader br2 = null;
        try {

            br2 = new BufferedReader(new FileReader(alldocketnumbers));

            String line;
            while ((line = br2.readLine()) != null) {
                alle_aktenzeichen.add(line);
            }
            br2.close();

        } catch (IOException e) {
            e.printStackTrace();
        }





        BufferedReader br3 = null;
        try {

            br3 = new BufferedReader(new FileReader(all_fs));

            String line;
            while ((line = br3.readLine()) != null) {
                alle_fs.add(line);
            }
            br3.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader br4 = null;
        try {

            br4 = new BufferedReader(new FileReader(all_fs_wo_az_known));

            String line;
            while ((line = br4.readLine()) != null) {
                alle_fs_wo_az_bekannt.add(line);
            }
            br4.close();

        } catch (IOException e) {
            e.printStackTrace();
        }



        alle_aktenzeichen.removeAll(known_fundstellen);

        Set<String> set = new HashSet<>(alle_fs);
        alle_fs.clear();
        alle_fs.addAll(set);
        // Result: Alle FS welche noch nicht mit AZ gemappt sind (unique)
        alle_fs.removeAll(alle_fs_wo_az_bekannt);
        Collections.sort(alle_fs);

        System.out.println("lists");

    }

    private static void processFundstellenBO() {
        File folder = new File("../Resources/Fundstellen/NJW_data");

        ArrayList<String> fundstellen = new ArrayList<>();


        for (File file : folder.listFiles()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                Document doc = Jsoup.parse(fis, null, "", Parser.xmlParser());


                Elements h3s = doc.select("h3");

                Element theList = null;
                for (Element e : h3s) {

                    if (file.getName().equals("1982_41.html")) {
                        System.out.println(".");
                    }

                    if (e.text().equals("Rechtsprechung") || e.text().equals("Entscheidungen") || e.text().equals("Mitteilungen")) {
                        List<Node> nodelist = e.parent().childNodes();
                        if (nodelist.get(nodelist.size() - 1).toString().equals(" ")) {
                            theList = (Element) nodelist.get(nodelist.size() - 2);
                        } else {
                            theList = (Element) nodelist.get(nodelist.size() - 1);
                        }

                    }
                }

                Elements li = theList.select("li");

                for (Element e : li) {
                    String aztext = e.getElementsByClass("inhdok").get(0).select("a").get(0).textNodes().get(0).text();
                    String site = e.getElementsByClass("inhseite").get(0).select("a").get(0).textNodes().get(0).text();
                    String year = e.getElementsByClass("inhdok").get(0).select("a").select("span").get(0).text().split("\\s")[2];

                    Pattern pattern_aktenzeichen = Pattern.compile(_aktenzeichenRegEx);

                    Matcher matcher_aktenzeichen = pattern_aktenzeichen.matcher(aztext);
                    String docketnumber = "ERROR";
                    if (matcher_aktenzeichen.find()) {
                         docketnumber = matcher_aktenzeichen.group(1);
                    } else {
                        System.out.println("problem mit " + file.getName() + ": " + aztext);
                    }

                    String fundstelle = docketnumber + ";\t NJW " + year + ", " + site;
                    fundstellen.add(fundstelle);

                    //System.out.println(".");
                }

                String az = "";
                String site = "";

                //System.out.println("k");



            } catch (FileNotFoundException e) {
                System.out.println("Problem with " + file.getName());
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Problem with " + file.getName());
                e.printStackTrace();
            }
        }


        System.out.println("done");

        FileWriter writer = null;
        try {
            writer = new FileWriter("../Resources/Fundstellen/njw_table.txt");
            for(String str: fundstellen) {
                writer.write(str + System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void scrapeNachzugBO() {
        String[] nachzug = new String[] {
                "1952_25",
                "1952_26",
                "1952_28",
                "1952_32",
                "1952_33",
                "1952_34",
                "1952_35",
                "1966_42",
                "1980_24",
                "1984_01",
                "1984_02",
                "1984_12",
                "1984_36",
                "1986_18",
                "1986_26",
                "1989_34",
                "1989_35",
                "1990_05",
                "1992_27",
                "1992_28",
                "1992_49",
                "1993_19",
                "1994_31",
                "1994_32",
                "1995_22",
                "1995_23",
                "1997_24",
                "1997_46",
                "1998_17",
                "1998_18",
                "1998_40",
                "1999_31",
                "2002_21",
                "2007_13",
                "2007_34",
                "2007_35",
                "2009_35",
                "2010_05",
                "2011_15",
                "2011_36",
                "2012_05",
                "2012_27",
                "2013_17",
                "2013_18",
                "2019_16"
        };

        System.setProperty("webdriver.chrome.driver", "C:/Program Files/WebDriver/bin/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        ChromeOptions options = new ChromeOptions();

        for (String s : nachzug) {
            String[] parts = s.split("_");

            String url = "https://beck-online.beck.de/?vpath=bibdata%2fzeits%2fNJW%2f" + parts[0] + "%2fcont%2fNJW%2e" + parts[0] + "%2eH" + parts[1] + "%2eNAMEINHALTSVERZEICHNIS%2ehtm";

            try {
                driver.get(url);
                TimeUnit.MILLISECONDS.sleep(1500);
                String pageSource = driver.getPageSource();

                File file = new File("../Resources/Fundstellen/NJW_data/" + parts[0] + "_" + parts[1] + ".html");

                try (FileOutputStream fos = new FileOutputStream(file);
                     BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                    byte[] bytes = pageSource.getBytes();
                    bos.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
            } finally {

            }


        }
        driver.quit();
    }

    private static void checkFailuresFundstellenBO() {
        File folder = new File("../Resources/Fundstellen/NJW_data");


        ArrayList<String> error_files = new ArrayList<>();

        // Check which downloads didn't work
        for (File file : folder.listFiles()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                Document doc = Jsoup.parse(fis, null, "", Parser.xmlParser());

                String fehler = doc.select("title").text();

                if (fehler.contains("Fehler")) {
                    error_files.add(file.getName());
                    System.out.println("Fehler bei " + file.getName());
                }

            } catch (FileNotFoundException e) {
                System.out.println("Problem with " + file.getName());
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Problem with " + file.getName());
                e.printStackTrace();
            }

        }
        System.out.println("fine");
    }

    private static void scrapeFundstellenBO() {

        //FileWriter myWriter = new FileWriter("src/main/resources/Fundstellen/NJW_data/njw.txt");

        //String year = "2005";
        //String heft = "12";

        System.setProperty("webdriver.chrome.driver", "C:/Program Files/WebDriver/bin/chromedriver.exe");

        WebDriver driver = new ChromeDriver();
        ChromeOptions options = new ChromeOptions();

        for (int year = 2020; year <= 2020; year++) {
            for (int heft = 1; heft <= 45; heft++) {

                String conv_heft = Integer.toString(heft);
                if (heft < 10) {
                    conv_heft = "0" + Integer.toString(heft);
                }

                if (year == 1956 && (heft == 12 || heft == 31 || heft == 51 /*|| heft == 36 || heft == 51*/)) {
                    conv_heft = "" + Integer.toString(heft) + Integer.toString(heft + 1);
                    heft++;
                }

                if (year == 1957 && (heft == 15 || heft == 33 || heft == 51 /*|| heft == 36 || heft == 51*/)) {
                    conv_heft = "" + Integer.toString(heft) + Integer.toString(heft + 1);
                    heft++;
                }

                if (year == 1958 && (heft == 14 || heft == 33 || heft == 51 /*|| heft == 36 || heft == 51*/)) {
                    conv_heft = "" + Integer.toString(heft) + Integer.toString(heft + 1);
                    heft++;
                }

                if (year == 1959 && (heft == 1 || heft == 19 || heft == 32 /*|| heft == 36 || heft == 51*/)) {
                    if (heft < 10) {
                        conv_heft = "0" + Integer.toString(heft) + "0" + Integer.toString(heft + 1);
                        heft++;
                    }
                    else {
                        conv_heft = "" + Integer.toString(heft) + Integer.toString(heft + 1);
                        heft++;
                    }
                }

                if (year == 1960 && (heft == 1 || heft == 32 /*|| heft == 36 || heft == 51*/)) {
                    if (heft < 10) {
                        conv_heft = "0" + Integer.toString(heft) + "0" + Integer.toString(heft + 1);
                        heft++;
                    }
                    else {
                        conv_heft = "" + Integer.toString(heft) + Integer.toString(heft + 1);
                        heft++;
                    }
                }

                if (year == 1961 && (heft == 14 || heft == 32 /*|| heft == 36 || heft == 51*/)) {
                    if (heft < 10) {
                        conv_heft = "0" + Integer.toString(heft) + "0" + Integer.toString(heft + 1);
                        heft++;
                    }
                    else {
                        conv_heft = "" + Integer.toString(heft) + Integer.toString(heft + 1);
                        heft++;
                    }
                }

                if (year == 1962 && (heft == 1 || heft == 17 || heft == 33)) {
                    if (heft < 10) {
                        conv_heft = "0" + Integer.toString(heft) + "0" + Integer.toString(heft + 1);
                        heft++;
                    }
                    else {
                        conv_heft = "" + Integer.toString(heft) + Integer.toString(heft + 1);
                        heft++;
                    }
                }

                if (year == 1963 && (heft == 1 || heft == 14 || heft == 35)) {
                    if (heft < 10) {
                        conv_heft = "0" + Integer.toString(heft) + "0" + Integer.toString(heft + 1);
                        heft++;
                    }
                    else {
                        conv_heft = "" + Integer.toString(heft) + Integer.toString(heft + 1);
                        heft++;
                    }
                }

                if (year == 1964 && (heft == 1 || heft == 14 || heft == 38 || heft == 51)) {
                    if (heft < 10) {
                        conv_heft = "0" + Integer.toString(heft) + "0" + Integer.toString(heft + 1);
                        heft++;
                    }
                    else {
                        conv_heft = "" + Integer.toString(heft) + Integer.toString(heft + 1);
                        heft++;
                    }
                }

                if (year == 1965 && (heft == 1 || heft == 16 || heft == 34)) {
                    if (heft < 10) {
                        conv_heft = "0" + Integer.toString(heft) + "0" + Integer.toString(heft + 1);
                        heft++;
                    }
                    else {
                        conv_heft = "" + Integer.toString(heft) + Integer.toString(heft + 1);
                        heft++;
                    }
                }

                if (year == 1966 && (heft == 1 || heft == 14)) {
                    if (heft < 10) {
                        conv_heft = "0" + Integer.toString(heft) + "0" + Integer.toString(heft + 1);
                        heft++;
                    }
                    else {
                        conv_heft = "" + Integer.toString(heft) + Integer.toString(heft + 1);
                        heft++;
                    }
                }

                if (year == 1967 && (heft == 1 || heft == 12)) {
                    if (heft < 10) {
                        conv_heft = "0" + Integer.toString(heft) + "0" + Integer.toString(heft + 1);
                        heft++;
                    }
                    else {
                        conv_heft = "" + Integer.toString(heft) + Integer.toString(heft + 1);
                        heft++;
                    }
                }

                if ((year == 1968 || year == 1969 || year == 1970 || year == 1971 || year == 1972 || year == 1973 || year == 1974 || year == 1975) && heft == 1) {
                    if (heft < 10) {
                        conv_heft = "0" + Integer.toString(heft) + "0" + Integer.toString(heft + 1);
                        heft++;
                    }
                    else {
                        conv_heft = "" + Integer.toString(heft) + Integer.toString(heft + 1);
                        heft++;
                    }
                }

                if (year == 1976 && (heft == 1 || heft == 21)) {
                    if (heft < 10) {
                        conv_heft = "0" + Integer.toString(heft) + "0" + Integer.toString(heft + 1);
                        heft++;
                    }
                    else {
                        conv_heft = "" + Integer.toString(heft) + Integer.toString(heft + 1);
                        heft++;
                    }
                }

                if (year == 1977 && (heft == 1)) {
                    if (heft < 10) {
                        conv_heft = "0" + Integer.toString(heft) + "0" + Integer.toString(heft + 1);
                        heft++;
                    }
                    else {
                        conv_heft = "" + Integer.toString(heft) + Integer.toString(heft + 1);
                        heft++;
                    }
                }

                if (year == 1978 && (heft == 1 || heft == 16)) {
                    if (heft < 10) {
                        conv_heft = "0" + Integer.toString(heft) + "0" + Integer.toString(heft + 1);
                        heft++;
                    }
                    else {
                        conv_heft = "" + Integer.toString(heft) + Integer.toString(heft + 1);
                        heft++;
                    }
                }

                if ((year == 1979 || year == 1980 || year == 1981 || year == 1982 || year == 1983) && heft == 1) {
                    if (heft < 10) {
                        conv_heft = "0" + Integer.toString(heft) + "0" + Integer.toString(heft + 1);
                        heft++;
                    }
                    else {
                        conv_heft = "" + Integer.toString(heft) + Integer.toString(heft + 1);
                        heft++;
                    }
                }

                if (year == 1984 && (heft == 27 || heft == 29)) {
                    if (heft < 10) {
                        conv_heft = "0" + Integer.toString(heft) + "0" + Integer.toString(heft + 1);
                        heft++;
                    }
                    else {
                        conv_heft = "" + Integer.toString(heft) + Integer.toString(heft + 1);
                        heft++;
                    }
                }

                if ((year == 1985 || year == 1987 || year == 1988 || year == 1998) && heft == 1) {
                    if (heft < 10) {
                        conv_heft = "0" + Integer.toString(heft) + "0" + Integer.toString(heft + 1);
                        heft++;
                    }
                    else {
                        conv_heft = "" + Integer.toString(heft) + Integer.toString(heft + 1);
                        heft++;
                    }
                }

                if (year == 1986 && (heft == 1 || heft == 20)) {
                    if (heft < 10) {
                        conv_heft = "0" + Integer.toString(heft) + "0" + Integer.toString(heft + 1);
                        heft++;
                    }
                    else {
                        conv_heft = "" + Integer.toString(heft) + Integer.toString(heft + 1);
                        heft++;
                    }
                }

                if ((year == 2004 || year == 2005 || year == 2006 || year == 2007 || year == 2008 || year == 2009 || year == 2010 || year == 2011 || year == 2012 || year == 2013 || year == 2014 || year == 2015 || year == 2016 || year == 2017 || year == 2018 || year == 2019) && heft == 1) {
                    if (heft < 10) {
                        conv_heft = "0" + Integer.toString(heft) + "0" + Integer.toString(heft + 1);
                        heft++;
                    }
                    else {
                        conv_heft = "" + Integer.toString(heft) + Integer.toString(heft + 1);
                        heft++;
                    }
                }

                if (year == 2020 && heft == 1) {
                    if (heft < 10) {
                        conv_heft = "0" + Integer.toString(heft) + "0" + Integer.toString(heft + 1);
                        heft++;
                    }
                    else {
                        conv_heft = "" + Integer.toString(heft) + Integer.toString(heft + 1);
                        heft++;
                    }
                }







                String url = "https://beck-online.beck.de/?vpath=bibdata%2fzeits%2fNJW%2f" + Integer.toString(year) + "%2fcont%2fNJW%2e" + Integer.toString(year) + "%2eH" + conv_heft + "%2eNAMEINHALTSVERZEICHNIS%2ehtm";



                //Proxy proxy = new Proxy();
                //proxy.setHttpProxy("176.9.75.42:8080");
                //proxy.setSocksProxy("148.251.178.165:62854");

                //options.setCapability("proxy", proxy);
                //WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10).getSeconds());
                try {
                    driver.get(url);
                    TimeUnit.MILLISECONDS.sleep(1500);
                    String pageSource = driver.getPageSource();
                    //driver.findElement(By.name("q")).sendKeys("cheese" + Keys.ENTER);
                    //WebElement firstResult = wait.until(presenceOfElementLocated(By.id("ipv4-value")));
                    //System.out.println(firstResult.getAttribute("textContent"));


                    File file = new File("../Resources/Fundstellen/NJW_data/" + year + "_" + conv_heft + ".html");

                    try (FileOutputStream fos = new FileOutputStream(file);
                         BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                        //convert string to byte array
                        byte[] bytes = pageSource.getBytes();
                        //write byte array to file
                        bos.write(bytes);
                        bos.close();
                        //System.out.print("Data written to file successfully.");
                    } catch (IOException e) {
                        System.out.println("Problem mit " + year + " " + heft);
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    System.out.println("Problem mit " + year + " " + heft);
                } finally {

                }

            }

        }
        driver.quit();



    }

    private static void scrapeBverfGE() throws IOException {

        FileWriter myWriter = new FileWriter("src/main/resources/Fundstellen/BVerfGE.txt");
        for (int i = 1; i <= 1; i++) {
            //for (int i = 10; i <= 150; i = i + 10) {
            String url = "https://www.bundesverfassungsgericht.de/DE/Entscheidungen/Liste/" + i + "ff/liste_node.html";
            try {
                TimeUnit.SECONDS.sleep(1);
                Document document = Jsoup.connect(url).get();
                Elements lists = document.getElementsByClass("toggleEntry");
                Elements tr = lists.select("tr");
                for (int c = tr.size() - 1; c >= 0; c--) {
                    String fs;
                    if (tr.get(c).childNode(0).childNode(0).nodeName().equals("a")) {
                        fs = tr.get(c).childNode(0).childNode(0).childNode(0).toString();
                    } else {
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
