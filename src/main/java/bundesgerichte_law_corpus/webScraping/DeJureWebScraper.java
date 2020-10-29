package bundesgerichte_law_corpus.webScraping;

import bundesgerichte_law_corpus.DataMapper;
import bundesgerichte_law_corpus.model.Decision;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;

import org.openqa.selenium.Proxy;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;


public class DeJureWebScraper {


    public static void main(String[] args) {
        //crawlMappingInformation();
        //testproxy();
        crawlData();

    }

    private static void crawlData() {
        System.setProperty("webdriver.chrome.driver", "C:/Program Files/WebDriver/bin/chromedriver.exe");

        Proxy proxy = new Proxy();
        //proxy.setHttpProxy("176.9.75.42:8080");
        //proxy.setSocksProxy("148.251.178.165:62854");
        ChromeOptions options = new ChromeOptions();
        options.setCapability("proxy", proxy);
        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10).getSeconds());
        try {
            driver.get("https://www.wieistmeineip.de/");
            //driver.findElement(By.name("q")).sendKeys("cheese" + Keys.ENTER);
            WebElement firstResult = wait.until(presenceOfElementLocated(By.id("ipv4-value")));
            System.out.println(firstResult.getAttribute("textContent"));
        } finally {
            driver.quit();
        }
    }

    private void testproxy() {
        // My IP: 31.18.250.80
        ArrayList<String[]> proxy_list = new ArrayList<>();
        //proxy_list.add(new String[]{"", ""});

        String[] working_addrs = new String[]{
                "123.56.161.63:80",     //CHI
                "161.202.226.194:8123", //JAP
                "157.230.103.189:37331",//US
                "212.87.220.2:3128",    //US
                "78.47.16.54:80",       //GER
                "173.212.202.65:80",    //GER
                "5.189.133.231:80",     //GER
                "173.212.202.65:80",    //GER
                "91.198.137.34:8888",   //GER
                "91.205.174.26:80",     //GER
                "80.241.222.137:80",    //GER
                "80.241.222.138:80",    //GER
                "83.82.43.237:80",      //NL
        };


        String[] random_proxies = new String[]{
                "74.208.128.22:80",
                "169.57.157.146:8123",
                "169.57.157.148:80",
                "119.81.189.194:80",
                "119.81.189.194:8123",
                "169.57.157.148:8123",
                "161.202.226.194:80",
                "101.200.127.78:80",
                "115.223.7.110:80",
                "8.209.91.61:1090",
                "161.202.226.194:8123",
                "122.228.197.152:80",
                "183.166.162.174:3000",
                "60.166.120.159:9999",
                "175.18.16.235:8080",
                "222.66.94.130:80",
                "180.109.145.27:4216",
                "113.214.13.1:1080",
                "115.219.131.142:3000",
                "173.212.202.65:80",
                "5.189.133.231:80",
        };


        for (String adr : random_proxies) {
            String[] parts = adr.split(":");
            proxy_list.add(new String[]{parts[0], parts[1]});
        }


        for (String[] proxy_el : proxy_list) {
            try {

                /*
                URL url = new URL("https://dejure.org");
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy_el[0], Integer.parseInt(proxy_el[1]))); // or whatever your proxy is
                HttpURLConnection uc = (HttpURLConnection) url.openConnection(proxy);

                uc.connect();

                String line = null;
                StringBuffer tmp = new StringBuffer();
                BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                while ((line = in.readLine()) != null) {
                    tmp.append(line);
                }

                Document doc = Jsoup.parse(String.valueOf(tmp));
                System.out.println(doc);
                */

                Document doc = Jsoup //
                        //.connect("https://www.whatismyip.com/")
                        //.connect("http://whatismyip.host/") //
                        .connect("https://dejure.org/")
                        .proxy(proxy_el[0], Integer.parseInt(proxy_el[1])) // sets a HTTP proxy
                        //.userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2") //
                        //.header("Content-Language", "en-US") //
                        //.followRedirects(true)
                        //.timeout(25000)
                        .get();
                //Elements address = doc.getElementsByClass("ipaddress");
                System.out.println(proxy_el[0] + ":" + proxy_el[1] + " is working!");
            } catch (IOException e) {
                System.out.println("Proxy: " + proxy_el[0] + " failed.");
                //e.printStackTrace();

            }
        }
    }


    private void crawlMappingInformation() {


        System.out.println("Crawl all Decisions for Dejure.org Web Scraping...");
        ArrayList<String> decisionIDs = new ArrayList<>();
        File decision_folder = new File("../Resources");
        File[] files = decision_folder.listFiles();
        for (int i = 0; i < files.length; i++) {
            // Just first of all BVerfG
            if (files[i].getName().contains("KVRE")) {
                decisionIDs.add(files[i].getName().split("\\.")[0]);
            }

        }
        System.out.println("Crawling Finished.");
        System.out.println("Start Mapping...");
        DataMapper dataMapper = new DataMapper();
        ArrayList<Decision> decisions = dataMapper.mapDecisionObjects(decisionIDs);
        System.out.println("Decisions Mapped successfully");

        ArrayList<String[]> proxy_list = new ArrayList<>();
        String[] proxies = new String[]{
                "123.56.161.63:80",     //CHI
                "161.202.226.194:8123", //JAP
                "157.230.103.189:37331",//US
                "212.87.220.2:3128",    //US
                "78.47.16.54:80",       //GER
                "173.212.202.65:80",    //GER
                "5.189.133.231:80",     //GER
                "173.212.202.65:80",    //GER
                "91.198.137.34:8888",   //GER
                "91.205.174.26:80",     //GER
                "80.241.222.137:80",    //GER
                "80.241.222.138:80",    //GER
                "83.82.43.237:80",      //NL
        };

        for (String adr : proxies) {
            String[] parts = adr.split(":");
            proxy_list.add(new String[]{parts[0], parts[1]});
        }


        for (int i = 3000; i <= decisions.size(); i++) {

            if (i % 1 == 0) {
                try {
                    TimeUnit.SECONDS.sleep(2);
                    System.out.println("Now waiting 2 seconds");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            ArrayList<String> dockNr = decisions.get(i).getDocketNumber();

            String court = decisions.get(i).getCourtType(); //short form
            String date = decisions.get(i).getDecisionDate(); // XX.XX.XXXX
            String docketNumber = dockNr.get(0); // //normal form
            // BeispielURL: https://dejure.org/dienste/vernetzung/rechtsprechung?Gericht=BVerfG&Datum=09.02.2010&Aktenzeichen=1%20BvL%201/09


            //convert docketNumber
            String docketNumber_converted = docketNumber.replaceAll("\\s", "%20");

            //URL
            //String url = "https://dejure.org/dienste/vernetzung/rechtsprechung?Gericht=" + court + "&Datum=" + date + "&Aktenzeichen=" + docketNumber_converted;
            String url = "https://dejure.org/dienste/vernetzung/rechtsprechung?Gericht=BVerfG&Datum=07.06.2016&Aktenzeichen=1%20BvR%20519/16";

            try {
                Element pfs_div = null;
                Element wzv_div = null;
                //Document document = Jsoup.connect(url).get();
                Document document = null;
                for (String[] prx : proxy_list) {
                    try {
                        document = Jsoup.connect(url).proxy(prx[0], Integer.parseInt(prx[1])).get();
                        break;
                    } catch (IOException e) {
                        System.out.println("Der Proxy " + prx[0] + " funktioniert nicht.");
                    }
                }

                Elements abschnittdivs = document.getElementsByClass("abschnittdiv");
                for (Element div : abschnittdivs) {
                    if (div.select("h4").text().equals("Papierfundstellen")) {
                        pfs_div = div;
                    } else if (div.select("h4").attr("id").equals("h4_zitiert_von-1")) {
                        wzv_div = div;
                    }
                }

                if (pfs_div != null && wzv_div != null) {
                    Elements listOfFundstellen = pfs_div.select("ul").select("li");

                    ArrayList<String> fundstellen = new ArrayList<>();
                    for (Element e : listOfFundstellen) {
                        fundstellen.add(e.text());
                    }



                    Elements listofWZV = wzv_div.select("ul");

                    ArrayList<String> citedBy = new ArrayList<>();
                    for (Element e : listofWZV) {
                        Elements li = e.select("li");
                        for (Element le : li) {
                            citedBy.add(le.selectFirst("a").text());
                        }
                    }

                    String fileDN = "";
                    for (String s : dockNr) {
                        fileDN = fileDN + "%" + s;
                    }
                    fileDN = docketNumber.replaceAll("\\s", "_");
                    fileDN = fileDN.replaceAll("\\/", "-");

                    //File file = new File("C:/Users/lenna/Desktop/Bachelorarbeit/FundstellenMapping/" + court + "_" + fileDN + ".txt");
                    //file.createNewFile();
                    FileWriter fileWriter = new FileWriter("C:/Users/lenna/Desktop/Bachelorarbeit/FundstellenMapping/" + court + "_" + decisions.get(i).getDecisionID() + "_" + fileDN + ".txt");
                    String inDocumentDN = "Aktenzeichen: ";
                    for (String s : dockNr) {
                        inDocumentDN = inDocumentDN + ", " + s;
                    }
                    fileWriter.write(inDocumentDN);
                    fileWriter.write("\n");
                    fileWriter.write("--------------------\n");
                    fileWriter.write("Fundstellen:\n");
                    for (String s : fundstellen) {
                        fileWriter.write(s + "\n");
                    }
                    fileWriter.write("\n");
                    fileWriter.write("--------------------\n");
                    fileWriter.write("Wird zitiert von:\n");
                    for (String s : citedBy) {
                        fileWriter.write(s + "\n");
                    }
                    fileWriter.close();
                    //System.out.println("Saved with data " + decisions.get(i).getDecisionID());
                } else if (pfs_div != null && wzv_div == null) {

                    Elements listOfFundstellen = pfs_div.select("ul").select("li");

                    ArrayList<String> fundstellen = new ArrayList<>();
                    for (Element e : listOfFundstellen) {
                        fundstellen.add(e.text());
                    }

                    String fileDN = "";
                    for (String s : dockNr) {
                        fileDN = fileDN + "%" + s;
                    }
                    fileDN = docketNumber.replaceAll("\\s", "_");
                    fileDN = fileDN.replaceAll("\\/", "-");

                    FileWriter fileWriter = new FileWriter("C:/Users/lenna/Desktop/Bachelorarbeit/FundstellenMapping/" + court + "_" + decisions.get(i).getDecisionID() + "_" + fileDN + ".txt");
                    String inDocumentDN = "Aktenzeichen: ";
                    for (String s : dockNr) {
                        inDocumentDN = inDocumentDN + ", " + s;
                    }
                    fileWriter.write(inDocumentDN);
                    fileWriter.write("\n");
                    fileWriter.write("--------------------\n");
                    fileWriter.write("Fundstellen:\n");
                    for (String s : fundstellen) {
                        fileWriter.write(s + "\n");
                    }
                    fileWriter.close();


                } else if (pfs_div == null && wzv_div != null) {

                    Elements listofWZV = wzv_div.select("ul");

                    ArrayList<String> citedBy = new ArrayList<>();
                    for (Element e : listofWZV) {
                        Elements li = e.select("li");
                        for (Element le : li) {
                            citedBy.add(le.selectFirst("a").text());
                        }
                    }

                    String fileDN = "";
                    for (String s : dockNr) {
                        fileDN = fileDN + "%" + s;
                    }
                    fileDN = docketNumber.replaceAll("\\s", "_");
                    fileDN = fileDN.replaceAll("\\/", "-");

                    FileWriter fileWriter = new FileWriter("C:/Users/lenna/Desktop/Bachelorarbeit/FundstellenMapping/" + court + "_" + decisions.get(i).getDecisionID() + "_" + fileDN + ".txt");
                    String inDocumentDN = "Aktenzeichen: ";
                    for (String s : dockNr) {
                        inDocumentDN = inDocumentDN + ", " + s;
                    }
                    fileWriter.write(inDocumentDN);
                    fileWriter.write("\n");
                    fileWriter.write("--------------------\n");
                    fileWriter.write("Wird zitiert von:\n");
                    for (String s : citedBy) {
                        fileWriter.write(s + "\n");
                    }
                    fileWriter.close();


                } else {
                    //File fi = new File("C:/Users/lenna/Desktop/Bachelorarbeit/FundstellenMapping/NO_DATA_" + docketNumber_converted + ".txt");
                    //fi.createNewFile();
                    String dn = "";
                    for (String d : dockNr) {
                        dn = dn + "%" + d;
                    }
                    dn = dn.substring(1);
                    dn = dn.replaceAll("\\/", "-");
                    dn = dn.replaceAll("\\s", "_");
                    FileWriter fileWriter = new FileWriter("C:/Users/lenna/Desktop/Bachelorarbeit/FundstellenMapping/NO_DATA_" + court + "_" + decisions.get(i).getDecisionID() + "_" + dn + ".txt");
                    fileWriter.close();
                    //System.out.println("Saved without data " + decisions.get(i).getDecisionID());
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Problem mit " + decisions.get(i).getDecisionID());
            }


        }


    }
}
