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

    // Regular Expression which finds types of docketNumbers and "Fundstellen"
    private String _citationRegEx = "(((VGS|RiZ\\s?s?\\(R\\)|KZR|VRG|RiZ|EnRB|StbSt\\s?\\(B\\)|AnwZ\\s?\\(Brfg\\)|RiSt|PatAnwSt\\s?\\(R\\)|AnwZ\\s?\\(B\\)|PatAnwZ|EnVZ|AnwSt\\s?\\(B\\)|NotSt\\s?\\(Brfg\\)|KVZ|KZB|AR\\s?\\(Ri\\)|NotZ\\s?\\(Brfg\\)|RiSt\\s?\\(B\\)|AnwZ\\s?\\(P\\)|EnZB|RiSt\\s?\\(R\\)|NotSt\\s?\\(B\\)|AnwSt|WpSt\\s?\\(R\\)|KVR|AR\\s?\\(Kart\\)|EnZR|StbSt\\s?\\(R\\)|WpSt\\s?\\(B\\)|KZA|AR\\s?\\(Enw\\)|AnwSt\\s?\\(R\\)|KRB|RiZ\\s?\\(B\\)|PatAnwSt\\s?\\(B\\)|EnVR|AnwZ|NotZ|EnZA|AR)\\s\\d+/\\d+)|((GSZ|LwZB|WpSt\\s?\\(B\\)|AnwZ|LwZR|KVZ|EnRB|PatAnwSt\\s?\\(B\\)|ARP|VGS|WpSt\\s?\\(R\\)|RiSt\\s?\\(B\\)|EnZA|KRB|AnwSt\\s?\\(R\\)|NotSt\\s?\\(Brfg\\)|EnVR|LwZA|ZB|AR\\s?\\(Vollz\\)|StB|ZR|AR\\s?\\(VS\\)|BJs|BLw|NotZ\\s?\\(Brfg\\)|RiZ\\s?\\(B\\)|PatAnwSt\\s?\\(R\\)|AK|RiZ|PatAnwZ|ARs|StbSt\\s?\\(R\\)|VRG|NotSt\\s?\\(B\\)|AR\\s?\\(Enw\\)|AR\\s?\\(VZ\\)|StE|KVR|AR\\s?\\(Ri\\)|AR|AnwSt|NotZ|StbSt\\s?\\(B\\)|StR|AZR|ZA|AnwZ\\s?\\(B\\)|EnZR|AR\\s?\\(Kart\\)|GSSt|AnwZ\\s?\\(P\\)|ZR\\s?\\(Ü\\)|AnwZ\\s?\\(Brfg\\)|KZB|BGns|KZR|RiSt|KZA|BAusl|AnwSt\\s?\\(B\\)|BGs|RiZ\\s?\\(R\\)|EnZB|RiSt\\s?\\(R\\)|ARZ|EnVZ)\\s\\d+/\\d+)|([I+|IV|V|VI|VII|VIII|IX|X|XI|XII|0-9]+[a-z]?\\s[A-Za-z\\(\\)]{1,20}\\s\\d+/\\d\\d))|[A-Z]{1}\\s[0-9]+\\s[A-Z]+\\s\\d+\\/\\d+\\s[A-Z]{1}|C\\-\\d+\\/\\d+|(RGZ|RGSt|RAG|PrOVG|RVA|RFH|StGH|SZ|KH|RG|BGE|B|BGHZ|BGHSt|BAGE|BVerwGE|BRAK-Mitt\\.|BSGE|BFHE|BVerfGE|BVerfGK|SZ|SSt|VfSlg|BGE|AbfallR|AcP|AfkKR|AfP|AG|AiB|AktStR|ANAZAR|AnwBl|AöR|AOStB|ArbR|ArbRB|ARSP|ArztR|ASR|AuAS|A\\&R|AUR|AuR|AVR|AW\\-Prax|BauR|BayVBl|BB|BKR|Blutalkohol|Br|BRAK\\-Mitt|BtPrax|CCLR|CCZ|CR|CSR|DAJV\\sNewsletter|DAJV|DAR|DB|DGVZ|DNotZ|DÖD|DÖV|DPJZ|DRiZ|DS|DSB|DStR|DuD|DVBl|DZWIR|EnWZ|ErbR|ErbStB|ErtrStB|EuGRZ|EuR|EurUP|EUUStB|EuZA|EuZW|EWeRK|EWiR|EWS|FA|FamFR|FamRB|FamRK|FamRZ|FF|FGPrax|Food|FoodR|Food\\sPrax|FR|FuR|FuS|GA|GewArch|GesR|GmbHR|GmbHStB|GPR|GRUR|GRUR\\-Int|GRUR\\-Prax|GSZ|GuP|GWR|HFR|HRRS|I\\+E|IHR|InfAuslR|IAR|info\\salso|InTeR|IPrax|IPRB|IR|IRZ|IStR|ITRB|IWRZ|JA|JAmt|JOR|JR|JRP|JURA|JurBüro|JuS|JFG|JZ|KJ|KommJur|Kriminalistik|KritV|KSzW|KTS|KuR|K\\&R|KUR|LKV|LMuR|MarkenR|MIR|MDR|MedR|medstra|MietRB|ML|MMR|MPR|MR\\-Int|MschrKrim|MwStR|NdsVBl|NdsVBl\\.|NJ|NJOZ|NJW|NJW-RR|NordÖR|NotBZ|NPLY|npoR|NStZ|NStZ-RR|N\\&R|NuR|NVwZ|NWVBl|NZA|NZBau|NZFam|NZG|NZI|NZKart|NZM|NZS|NZV|NZWehrr|NZWiSt|öAT|OER|PersV|PflR|PharmR|Praxis\\sder\\sRechtspsychologie|RabelsZ|RAW|RdA|RdE|RdFin|RdL|RdJB|RdTW|RDV|RechtsMed|ree|RiA|JaR|RIW|RNotZ|RohR|Rpfleger|RphZ|RPG|RRa|RT|R\\&P|RuP|r\\+s|RuZ|RW|SächsVBl|SchiedsAZ|SchiedsVZ|SchuR|SchulR\\sheute|SGb|SR|SpuRt|STAAT|StAZ|SteuK|StoffR|StraFo|StudZR|StuW|StV|SVR|ThürVBl|TranspR|Ubg|UFITA|UStB|UPR|UR|VBlBW|VergabeR|VersR|VERW|VerwA|VIA|VR|VRS|VRÜ|VuR|VSSR|Vollstr|WiRO|WissR|wistra|WM|WRP|W\\+B|WuW|ZaöRV|ZAR|ZAT|ZBB|ZBR|ZChinR|ZD|ZErb|ZESAR|ZEuP|ZEuS|ZEV|ZevKR|ZfA|ZfB|ZfBR|ZFE|ZfF|ZfgG|ZfPR|ZfIR|ZfPW|ZfRsoz|zfs|ZfSH\\/SGB|ZfU|ZFW|ZfWG|ZfZ|ZG|ZGE|ZGR|ZGS|ZHR|ZIAS|ZIP|ZIS|IWRZ|ZJapanR|ZJS|ZKJ|ZLR|ZLW|ZMGR|ZMR|ZNR|ZNER|ZParl|ZRP|ZRG|ZStV|ZStW|ZTR|ZUM|ZUR|ZVertriebsR|ZVI|ZVR|ZVertriebsR|ZWeR|ZWE|ZWH|ZInsO|NVwZ-RR|RGZ|BGHReport)\\s*\\d{1,6},\\s\\d{1,6}(?:.?\\s*?[(<\\[].*?[\\])>])?(?:[,]\\s*?\\d{1,6})?(?:[;]\\s*?\\d{1,6},\\s*?\\d{1,6}(?:.?\\s*?[(<\\[].*?[\\])>]|[,]\\s*?\\d{1,6})?)*";


    // List of all decisions, that are processed
    ArrayList<Decision> _allDecisionsProcessed = new ArrayList<>();



    /**
     * Instantiate a DataMapper object to call his functionalities
     */
    public DataMapper() {
    }


    /**
     * Maps all given decision IDs to decision objects
     * @param decisionIDs the list of decisionIDs from the decisions you want to get mapped
     * @return the list of the mapped decisions
     */
    public ArrayList<Decision> mapDecisionObjects(ArrayList<String> decisionIDs) {

        int counter = 0;
        for (String dec_id : decisionIDs) {
            String filepath = "../Resources/Decisions/" + dec_id + ".xml";
            Decision decision = readDecisionXML(filepath);
            counter++;

            if (counter % 1000 == 0) {
                System.out.println(counter + " Decisions mapped");
            }
            _allDecisionsProcessed.add(decision);
        }
        return _allDecisionsProcessed;
    }


    /**
     *
     * @param filePath
     * @return
     */
    private Decision readDecisionXML(String filePath) {

        // Parse the Decision XML
        File file = new File(filePath);
        FileInputStream fis;
        Document doc = null;
        // Parse the decision document (XML) with Jsoup
        try {
            fis = new FileInputStream(file);
            Jsoup.parse(fis, null, "", Parser.xmlParser());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


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

        // not used right now, but already defined to build the infrastructure for future work
        ArrayList<String> occurringJudges = new ArrayList<>();



        // Creating the Decision Object with all the information
        Decision dec = new Decision(decisionID, ecli, courtType, formation, decisionDate, docketNumber, decisionType,
                norms, previousCourts, decisionTitle, guidingPrinciple, otherOrientationSentence, tenor, fact, reasonsOrDecReasons,
                dissentingOpinions, decisionURL, occurringCitations, occurringJudges);

        return dec;

    }


    /**
     * Finds all regular expression-matches in the decision text (title, fact, guiding principle,
     * other orientation sentences, tenor, reasons, dissenting opinions)
     * @param text the text you want to search for regular expressions
     * @return the list of all matches
     */
    private ArrayList<String> findAllRegExMatches(String text) {
        ArrayList<String> allMatches = new ArrayList<>();
        Pattern pattern_fundstellen = Pattern.compile(_citationRegEx);
        //Pattern pattern_aktenzeichen = Pattern.compile(_aktenzeichenRegEx);
        Matcher matcher_fundstellen = pattern_fundstellen.matcher(text);
        //Matcher matcher_aktenzeichen = pattern_aktenzeichen.matcher(text);

        // while there are more matches
        while (matcher_fundstellen.find()) {
            String match = matcher_fundstellen.group();
            // if there are more than one citation in a match
            if (match.contains(";")) {
                String pre = matcher_fundstellen.group(7);
                String[] split = match.split(";");
                for (int i = 1; i < split.length; i++) {
                    String sec = split[i].trim();
                    String fs = pre + " " + sec;
                    // Its possible that the specific site is named in "<...>" - we dont need that, so we delete it
                    if (fs.matches(".*<.+>.*")) {
                        fs = fs.split("<")[0];

                    }
                    // Its possible that the specific site is named after a comma - we also dont need that, so we
                    // delete it
                    if (fs.split(",").length > 2) {
                        String s = fs.split(",")[0] + "," + fs.split(",")[1];
                        allMatches.add(s.trim());
                    }
                    else {
                        allMatches.add(fs.trim());
                    }

                    // we need to display the format "{zeitschrift|sammlung} {jahr}, {seite}"
                    if (split[0].split(",").length > 2) {
                        String s = split[0].split(",")[0] + "," + split[0].split(",")[1];
                        allMatches.add(s.trim());
                    }
                    else {
                        allMatches.add(split[0]);
                    }


                }

            } else {
                // Its possible that the specific site is named in "<...>" - we dont need that, so we delete it
                if (match.matches(".*<.+>.*")) {
                    match = match.split("<")[0];

                }
                // Its possible that the specific site is named after a comma - we also dont need that, so we
                // delete it
                if (match.split(",").length > 2) {
                    String s = match.split(",")[0] + "," + match.split(",")[1];
                    allMatches.add(s.trim());
                }
                else {
                    allMatches.add(match.trim());
                }

            }

        }
        // now we return all the matches
        return allMatches;
    }

}
