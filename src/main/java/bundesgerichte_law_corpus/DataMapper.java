package bundesgerichte_law_corpus;

import bundesgerichte_law_corpus.model.Decision;
import bundesgerichte_law_corpus.model.DecisionSection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The DataMapper extracts the decision metadata from the downloaded decision-XML and maps it into a decision object
 */
public class DataMapper {

    // Regular Expression which finds all types of docketNumbers
    private String _aktenzeichenRegEx = "(((VGS|RiZ\\s?s?\\(R\\)|KZR|VRG|RiZ|EnRB|StbSt\\s?\\(B\\)|AnwZ\\s?\\(Brfg\\)|RiSt|PatAnwSt\\s?\\(R\\)|AnwZ\\s?\\(B\\)|PatAnwZ|EnVZ|AnwSt\\s?\\(B\\)|NotSt\\s?\\(Brfg\\)|KVZ|KZB|AR\\s?\\(Ri\\)|NotZ\\s?\\(Brfg\\)|RiSt\\s?\\(B\\)|AnwZ\\s?\\(P\\)|EnZB|RiSt\\s?\\(R\\)|NotSt\\s?\\(B\\)|AnwSt|WpSt\\s?\\(R\\)|KVR|AR\\s?\\(Kart\\)|EnZR|StbSt\\s?\\(R\\)|WpSt\\s?\\(B\\)|KZA|AR\\s?\\(Enw\\)|AnwSt\\s?\\(R\\)|KRB|RiZ\\s?\\(B\\)|PatAnwSt\\s?\\(B\\)|EnVR|AnwZ|NotZ|EnZA|AR)\\s\\d+/\\d+)|" +
            "((GSZ|LwZB|WpSt\\s?\\(B\\)|AnwZ|LwZR|KVZ|EnRB|PatAnwSt\\s?\\(B\\)|ARP|VGS|WpSt\\s?\\(R\\)|RiSt\\s?\\(B\\)|EnZA|KRB|AnwSt\\s?\\(R\\)|NotSt\\s?\\(Brfg\\)|EnVR|LwZA|ZB|AR\\s?\\(Vollz\\)|StB|ZR|AR\\s?\\(VS\\)|BJs|BLw|NotZ\\s?\\(Brfg\\)|RiZ\\s?\\(B\\)|PatAnwSt\\s?\\(R\\)|AK|RiZ|PatAnwZ|ARs|StbSt\\s?\\(R\\)|VRG|NotSt\\s?\\(B\\)|AR\\s?\\(Enw\\)|AR\\s?\\(VZ\\)|StE|KVR|AR\\s?\\(Ri\\)|AR|AnwSt|NotZ|StbSt\\s?\\(B\\)|StR|ZA|AnwZ\\s?\\(B\\)|EnZR|AR\\s?\\(Kart\\)|GSSt|AnwZ\\s?\\(P\\)|ZR\\s?\\(Ü\\)|AnwZ\\s?\\(Brfg\\)|KZB|BGns|KZR|RiSt|KZA|BAusl|AnwSt\\s?\\(B\\)|BGs|RiZ\\s?\\(R\\)|EnZB|RiSt\\s?\\(R\\)|ARZ|EnVZ)\\s\\d+/\\d+)|" +
            "([I+|IV|V|VI|VII|VIII|IX|X|XI|XII|1-6]+[a-z]?\\s[A-Za-z\\(\\)]{2,20}\\s\\d+/\\d\\d))";

    //private String _referenceNumberRegex = "BVerfGE\\s[0-9]+,\\s[0-9]+";

    //private String _citationRegEx = "BVerfGE\\s*\\d{1,3},\\s\\d{1,3}(?:.?\\s*?[(<\\[].*?[\\])>])?(?:[,;]\\s*?\\d{1,3},\\s*?\\d{1,3}(?:.?\\s*?[(<\\[].*?[\\])>])?)*";

    private String _citationRegEx = "\\([^\\(]*\\s((RGZ|RGSt|RAG|PrOVG|RVA|RFH|StGH|SZ|KH|RG|BGE|BGHZ|BGHSt|BAGE|BVerwGE|BSGE|BFHE|BVerfGE|SZ|SSt|VfSlg|BGE|AbfallR|AcP|AfkKR|AfP|AG|AiB|AktStR|ANAZAR|AnwBl|AöR|AOStB|ArbR|ArbRB|ARSP|ArztR|ASR|AuAS|A\\&R|AUR|AuR|AVR|AW\\-Prax|BauR|BayVBl|BB|BKR|Blutalkohol|Br|BRAK\\-Mitt|BtPrax|CCLR|CCZ|CR|CSR|DAJV\\sNewsletter|DAJV|DAR|DB|DGVZ|DNotZ|DÖD|DÖV|DPJZ|DRiZ|DS|DSB|DStR|DuD|DVBl|DZWIR|EnWZ|ErbR|ErbStB|ErtrStB|EuGRZ|EuR|EurUP|EUUStB|EuZA|EuZW|EWeRK|EWiR|EWS|FA|FamFR|FamRB|FamRK|FamRZ|FF|Food|FoodR|Food\\sPrax|FR|FuR|FuS|GA|GewArch|GesR|GmbHR|GmbHStB|GPR|GRUR|GRUR\\-Int|GRUR\\-Prax|GSZ|GuP|GWR|HFR|HRRS|I\\+E|IHR|InfAuslR|IAR|info\\salso|InTeR|IPrax|IPRB|IR|IRZ|IStR|ITRB|IWRZ|JA|JAmt|JOR|JR|JRP|JURA|JurBüro|JuS|JZ|KJ|KommJur|Kriminalistik|KritV|KSzW|KTS|KuR|K\\&R|KUR|LKV|LMuR|MarkenR|MIR|MDR|MedR|medstra|MietRB|ML|MMR|MPR|MR\\-Int|MschrKrim|MwStR|NdsVBl|NdsVBl\\.|NJ|NJOZ|NJW|NJW-RR|NordÖR|NotBZ|NPLY|npoR|NStZ|N\\&R|NuR|NVwZ|NWVBl|NZA|NZBau|NZFam|NZG|NZI|NZKart|NZM|NZS|NZV|NZWehrr|NZWiSt|öAT|OER|PersV|PflR|PharmR|Praxis\\sder\\sRechtspsychologie|RabelsZ|RAW|RdA|RdE|RdFin|RdL|RdJB|RdTW|RDV|RechtsMed|ree|RiA|JaR|RIW|RNotZ|RohR|Rpfleger|RphZ|RPG|RRa|RT|R\\&P|RuP|r\\+s|RuZ|RW|SächsVBl|SchiedsAZ|SchiedsVZ|SchuR|SchulR\\sheute|SGb|SR|SpuRt|STAAT|StAZ|SteuK|StoffR|StraFo|StudZR|StuW|StV|SVR|ThürVBl|TranspR|Ubg|UFITA|UStB|UPR|UR|VBlBW|VergabeR|VersR|VERW|VerwA|VIA|VR|VRS|VRÜ|VuR|VSSR|Vollstr|WiRO|WissR|wistra|WM|WRP|W\\+B|WuW|ZaöRV|ZAR|ZAT|ZBB|ZBR|ZChinR|ZD|ZErb|ZESAR|ZEuP|ZEuS|ZEV|ZevKR|ZfA|ZfB|ZfBR|ZFE|ZfF|ZfgG|ZfPR|ZfIR|ZfPW|ZfRsoz|zfs|ZfSH\\/SGB|ZfU|ZFW|ZfWG|ZfZ|ZG|ZGE|ZGR|ZGS|ZHR|ZIAS|ZIP|ZIS|IWRZ|ZJapanR|ZJS|ZKJ|ZLR|ZLW|ZMGR|ZMR|ZNR|ZNER|ZParl|ZRP|ZRG|ZStV|ZStW|ZTR|ZUM|ZUR|ZVertriebsR|ZVI|ZVR|ZVertriebsR|ZWeR|ZWE|ZWH)\\s[0-9]+\\, [0-9]+)[^\\)]*\\)";


    ArrayList<Decision> _allDecisionsInDB = new ArrayList<>();


    /**
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws URISyntaxException
     * @throws InterruptedException
     */
    public DataMapper() {

        //File folder = new File("../Resources/Decisions");

        /*
        Decision decision_bvg = readDecisionXML("../Resources/Decisions/KVRE387011001.xml");
        Decision decision_bgh = readDecisionXML("../Resources/Decisions/JURE100055033.xml");
        Decision decision_bvwg = readDecisionXML("../Resources/Decisions/WBRE202000501.xml");
        Decision decision_bfh = readDecisionXML("../Resources/Decisions/STRE202050176.xml");
        Decision decision_bag = readDecisionXML("../Resources/Decisions/KARE600028845.xml");
        Decision decision_bsg = readDecisionXML("../Resources/Decisions/KSRE120051501.xml");
        Decision decision_bpatg = readDecisionXML("../Resources/Decisions/MPRE237120964.xml");
        Decision decision_gsogdb1 = readDecisionXML("../Resources/Decisions/KORE300212013.xml");
        Decision decision_gsogdb2 = readDecisionXML("../Resources/Decisions/KARE600031798.xml");

        _allDecisionsInDB.add(decision_bvg);
        _allDecisionsInDB.add(decision_bgh);
        _allDecisionsInDB.add(decision_bvwg);
        _allDecisionsInDB.add(decision_bfh);
        _allDecisionsInDB.add(decision_bag);
        _allDecisionsInDB.add(decision_bsg);
        _allDecisionsInDB.add(decision_bpatg);
        _allDecisionsInDB.add(decision_gsogdb1);
        _allDecisionsInDB.add(decision_gsogdb2);
        System.out.println("all tested");
        */

        /*

        int counter = 0;

        for (File dec_file : folder.listFiles()) {
            try {
                Decision decision = readDecisionXML(dec_file.getCanonicalPath());
                _allDecisionsInDB.add(decision);
                counter++;

                if (counter % 100 == 0) {
                    System.out.println(counter + " Decisions processed");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */


        //System.out.println("finished mapping");
        //NetworkController network = new NetworkController(_allDecisionsInDB);
    }


    public ArrayList<Decision> mapDecisionObjects(ArrayList<String> decisionIDs) {

        int counter = 0;
         for (String dec_id : decisionIDs) {
            String filepath = "../Resources/Decisions/" + dec_id + ".xml";
            try {
                Decision decision = readDecisionXML(filepath);
                counter++;

                if (counter % 1000 == 0) {
                    System.out.println(counter + " Decisions mapped");
                }
                _allDecisionsInDB.add(decision);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return _allDecisionsInDB;
    }


    private Decision readDecisionXML(String filePath) throws IOException {

        // Parse the Decision XML
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        Document doc = Jsoup.parse(fis, null, "", Parser.xmlParser());

        // decisionID
        String decisionID = doc.select("doknr").text().trim();

        // ecli
        String ecli = doc.select("ecli").text().trim();

        // courtType
        String courtType = doc.select("gertyp").text().trim();

        // formation
        String formation = doc.select("spruchkoerper").text().trim();

        //decisionDate
        String raw_decisionDate_string = doc.select("entsch-datum").text().trim();
        String year = raw_decisionDate_string.substring(0, 4);
        String month = raw_decisionDate_string.substring(4, 6);
        String day = raw_decisionDate_string.substring(6, 8);
        String decisionDate = day + "." + month + "." + year;

        //docketNumber
        ArrayList<String> docketNumber = new ArrayList<>();
        String raw_docketNumber = doc.select("aktenzeichen").text().trim();
        String[] docketNrs = raw_docketNumber.split(",");
        for (String dn : Arrays.asList(docketNrs)) {
            docketNumber.add(dn.trim());
        }

        //decisionType
        String decisionType = doc.select("doktyp").text().trim();

        // norms
        ArrayList<String> norms = new ArrayList<>();
        String raw_norms_string = doc.select("norm").html().trim();
        String[] norm_parts = raw_norms_string.split(",");
        for (String part : norm_parts) {
            if (!part.equals("")) {
                norms.add(part.trim());
            }
        }

        // previousCourts
        ArrayList<String> previousCourts = new ArrayList<>();
        for (Element e : doc.select("vorinstanz")) {
            String[] lc_parts = e.html().replace("\n", "").split("<br />");
            for (String part : lc_parts) {
                if (!part.equals("")) {
                    previousCourts.add(part);
                }
            }
        }

        // decisionTitle
        String decisionTitle = doc.select("titelzeile").text().trim();

        // guidingPrinciple
        ArrayList<String> guidingPrinciple = new ArrayList<>();
        Elements raw_guidingPrinciple = doc.select("leitsatz");
        Elements gui_princ_el = raw_guidingPrinciple.select("p");
        for (Element e : gui_princ_el) {
            if (!e.html().trim().equals("")) {
                guidingPrinciple.add(e.text().trim());
            }
        }

        //otherOrientationSentence
        ArrayList<String> otherOrientationSentence = new ArrayList<>();
        Elements raw_otherOrientationSentence = doc.select("sonstosatz");
        Elements otherOrientationSentence_el = raw_otherOrientationSentence.select("p");
        for (Element e : otherOrientationSentence_el) {
            if (!e.html().trim().equals("")) {
                otherOrientationSentence.add(e.text().trim());
            }
        }


        // tenor
        ArrayList<String> tenor = new ArrayList<>();
        Elements raw_tenors = doc.select("tenor");
        Elements tenor_el = raw_tenors.select("p");
        for (Element e : tenor_el) {
            if (!e.html().trim().equals("")) {
                tenor.add(e.text().trim());
            }
        }

        // fact
        ArrayList<DecisionSection> fact = new ArrayList<>();
        Elements raw_fact = doc.select("tatbestand");
        Elements fact_el = raw_fact.select("dl");
        for (Element e : fact_el) {
            int rd_nr;
            String r_text;
            if (e.select("h2").isEmpty() && e.select("h1").isEmpty() && !e.select("dt").select("a").isEmpty()) {
                rd_nr = Integer.parseInt(e.select("dt").select("a").text().trim());
                r_text = e.select("p").text().trim();
                if (!r_text.equals("")) {
                    fact.add(new DecisionSection(rd_nr, r_text));
                }
            } else if (e.select("h2").isEmpty() && e.select("dt").select("a").isEmpty() && !e.select("h1").isEmpty()) {
                rd_nr = 0;
                r_text = e.select("h1").text().trim();
                if (!r_text.equals("")) {
                    fact.add(new DecisionSection(rd_nr, r_text));
                }
            } else if (e.select("h1").isEmpty() && e.select("dt").select("a").isEmpty() && !e.select("h2").isEmpty()) {
                rd_nr = 0;
                r_text = e.select("h2").text().trim();
                if (!r_text.equals("")) {
                    fact.add(new DecisionSection(rd_nr, r_text));
                }

            }
            // If no element is given
            else {
                rd_nr = 0;
                r_text = e.select("p").text().trim();
                if (!r_text.equals("")) {
                    fact.add(new DecisionSection(rd_nr, r_text));
                }
            }
        }


        //decisionReasons | "gruende" or "entscheidungsgruende"
        ArrayList<DecisionSection> reasonsOrDecReasons = new ArrayList<>();

        // "entscheidungsgruende"
        Elements raw_decision_reasons = doc.select("entscheidungsgruende");
        Elements decision_reasons_el = raw_decision_reasons.select("dl");
        if (!raw_decision_reasons.text().equals("")) {
            for (Element e : decision_reasons_el) {
                int rd_nr;
                String r_text;
                if (e.select("h2").isEmpty() && e.select("h1").isEmpty() && !e.select("dt").select("a").isEmpty()) {
                    rd_nr = Integer.parseInt(e.select("dt").select("a").text().trim());
                    r_text = e.select("p").text().trim();
                    if (!r_text.equals("")) {
                        reasonsOrDecReasons.add(new DecisionSection(rd_nr, r_text));
                    }
                } else if (e.select("h2").isEmpty() && e.select("dt").select("a").isEmpty() && !e.select("h1").isEmpty()) {
                    rd_nr = 0;
                    r_text = e.select("h1").text().trim();
                    if (!r_text.equals("")) {
                        reasonsOrDecReasons.add(new DecisionSection(rd_nr, r_text));
                    }
                } else if (e.select("h1").isEmpty() && e.select("dt").select("a").isEmpty() && !e.select("h2").isEmpty()) {
                    rd_nr = 0;
                    r_text = e.select("h2").text().trim();
                    if (!r_text.equals("")) {
                        reasonsOrDecReasons.add(new DecisionSection(rd_nr, r_text));
                    }

                }
                // If no element is given
                else {
                    rd_nr = 0;
                    r_text = e.select("p").text().trim();
                    if (!r_text.equals("")) {
                        reasonsOrDecReasons.add(new DecisionSection(rd_nr, r_text));
                    }
                }
            }
        }

        // "gruende"
        Elements raw_reasons = doc.select("gruende");
        Elements reasons_el = raw_reasons.select("dl");
        if (!raw_reasons.text().equals("")) {
            for (Element e : reasons_el) {
                int rd_nr;
                String r_text;
                if (e.select("h2").isEmpty() && e.select("h1").isEmpty() && !e.select("dt").select("a").isEmpty()) {
                    rd_nr = Integer.parseInt(e.select("dt").select("a").text().trim());
                    r_text = e.select("p").text().trim();
                    if (!r_text.equals("")) {
                        reasonsOrDecReasons.add(new DecisionSection(rd_nr, r_text));
                    }
                } else if (e.select("h2").isEmpty() && e.select("dt").select("a").isEmpty() && !e.select("h1").isEmpty()) {
                    rd_nr = 0;
                    r_text = e.select("h1").text().trim();
                    if (!r_text.equals("")) {
                        reasonsOrDecReasons.add(new DecisionSection(rd_nr, r_text));
                    }
                } else if (e.select("h1").isEmpty() && e.select("dt").select("a").isEmpty() && !e.select("h2").isEmpty()) {
                    rd_nr = 0;
                    r_text = e.select("h2").text().trim();
                    if (!r_text.equals("")) {
                        reasonsOrDecReasons.add(new DecisionSection(rd_nr, r_text));
                    }
                }
                // If no element is given
                else {
                    rd_nr = 0;
                    r_text = e.select("p").text().trim();
                    if (!r_text.equals("")) {
                        reasonsOrDecReasons.add(new DecisionSection(rd_nr, r_text));
                    }
                }
            }
        }

        //dissentingOpinions
        ArrayList<DecisionSection> dissentingOpinions = new ArrayList<>();
        Elements raw_dissentingOpinion = doc.select("abwmeinung");
        Elements dissOp_el = raw_dissentingOpinion.select("dl");
        for (Element e : dissOp_el) {
            int rd_nr;
            String r_text;
            if (e.select("h2").isEmpty() && e.select("h1").isEmpty() && !e.select("dt").select("a").isEmpty()) {
                rd_nr = Integer.parseInt(e.select("dt").select("a").text().trim());
                r_text = e.select("p").text().trim();
                if (!r_text.equals("")) {
                    dissentingOpinions.add(new DecisionSection(rd_nr, r_text));
                }
            } else if (e.select("h2").isEmpty() && e.select("dt").select("a").isEmpty() && !e.select("h1").isEmpty()) {
                rd_nr = 0;
                r_text = e.select("h1").text().trim();
                if (!r_text.equals("")) {
                    dissentingOpinions.add(new DecisionSection(rd_nr, r_text));
                }
            } else if (e.select("h1").isEmpty() && e.select("dt").select("a").isEmpty() && !e.select("h2").isEmpty()) {
                rd_nr = 0;
                r_text = e.select("h2").text().trim();
                if (!r_text.equals("")) {
                    dissentingOpinions.add(new DecisionSection(rd_nr, r_text));
                }

            }
            // If no element is given
            else {
                rd_nr = 0;
                r_text = e.select("p").text().trim();
                if (!r_text.equals("")) {
                    dissentingOpinions.add(new DecisionSection(rd_nr, r_text));
                }
            }
        }

        // url
        String raw_decisionUrl_string = doc.select("identifier").text().trim();
        String decisionURL = raw_decisionUrl_string.substring(0, raw_decisionUrl_string.lastIndexOf('&')) + "&doc.part=L" + raw_decisionUrl_string.substring(raw_decisionUrl_string.lastIndexOf('&'));


        // occurringCitations
        // Searching for occurrences in decision title, otherOrientationSentence, guiding principle, tenor, fact, reasons and dissenting opinions
        ArrayList<String> occurringCitations = new ArrayList<>();
        occurringCitations.addAll(findAllRegExMatches(decisionTitle));
        for (String str : otherOrientationSentence) {
            occurringCitations.addAll(findAllRegExMatches(str));
        }
        for (String str : guidingPrinciple) {
            occurringCitations.addAll(findAllRegExMatches(str));
        }
        for (String str : tenor) {
            occurringCitations.addAll(findAllRegExMatches(str));
        }
        for (DecisionSection fds : fact) {
            occurringCitations.addAll(findAllRegExMatches(fds.getText()));
        }
        for (DecisionSection ds : reasonsOrDecReasons) {
            occurringCitations.addAll(findAllRegExMatches(ds.getText()));
        }

        for (DecisionSection ds : dissentingOpinions) {
            occurringCitations.addAll(findAllRegExMatches(ds.getText()));
        }

        //TODO uncomment for person network
        //occurringJudges
        ArrayList<String> occurringJudges = new ArrayList<>();
        //PDFController pdfController = new PDFController(ecli, year, month);
        //ArrayList<String> occurringJudges = pdfController.getOccuringJudges();


        /*
        try {
            //File myObj = new File("resources/EntityRecognition/temp_document.txt");
            String pr_ecli = ecli.split(":")[4];
            pr_ecli = pr_ecli.replace(".", "_");

            File db_file = new File("resources/EntityRecognition/temp_db/" + pr_ecli + ".txt");
            Scanner myReader = new Scanner(db_file);
            while (myReader.hasNextLine()) {
                String str_part = myReader.nextLine();
                occuringPersons.add(str_part.trim());

            }
            myReader.close();


        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        */


        // Creating the Decision Object
        Decision dec = new Decision(decisionID, ecli, courtType, formation, decisionDate, docketNumber, decisionType,
                norms, previousCourts, decisionTitle, guidingPrinciple, otherOrientationSentence, tenor, fact, reasonsOrDecReasons,
                dissentingOpinions, decisionURL, occurringCitations, occurringJudges);

        return dec;

    }











    /**
     * @param text
     * @return
     */
    private ArrayList<String> findAllRegExMatches(String text) {
        ArrayList<String> allMatches = new ArrayList<>();
        Pattern pattern_fundstellen = Pattern.compile(_citationRegEx);
        //Pattern pattern_aktenzeichen = Pattern.compile(_aktenzeichenRegEx);
        Matcher matcher_fundstellen = pattern_fundstellen.matcher(text);
        //Matcher matcher_aktenzeichen = pattern_aktenzeichen.matcher(text);

        while (matcher_fundstellen.find()) {
            String match = matcher_fundstellen.group(1);
            allMatches.add(match);
        }

        //while (matcher_aktenzeichen.find()) {
        //    String match_2 = matcher_aktenzeichen.group();
        //    allMatches.add(match_2);
        //}

        return allMatches;
    }


    public ArrayList<Decision> getAllDecisionsInDB() {
        return _allDecisionsInDB;
    }

}
