package bundesgerichte_law_corpus;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Pattern;

/**
 *
 */
public class PDFController {

    private String _firstBVerfGUrlPart = "https://www.bundesverfassungsgericht.de/SharedDocs/Downloads/DE/";
    private String _lastBVerfGUrlPart = ".pdf?__blob=publicationFile";

    private HashMap<String, ArrayList<String>> _extractedEntities;

    public PDFController(String ecli, String year, String month) throws IOException, InterruptedException {
        //downloadPDF("BVerfG", ecli, year, month);
        ArrayList<String> sentences = convertToText();
        //_extractedEntities = extractEntities(sentences, ecli);


    }

    /**
     *
     */
    private void downloadPDF(String court, String ecli, String year, String month) throws MalformedURLException {
        String[] parts = ecli.split(":");
        String str = parts[parts.length - 1];
        str = str.replace(".", "_");

        String pdfUrl = _firstBVerfGUrlPart + year + "/" + month + "/" + str + _lastBVerfGUrlPart;

        URL url = new URL(pdfUrl);
        try (InputStream in = url.openStream()) {
            //Files.copy(in, Paths.get("resources/DecisionPDFs/" + str + ".pdf"), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(in, Paths.get("resources/DecisionPDFs/" + "temp_document" + ".pdf"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("Decision not found in BVerfG PDF elasticsearch:" + ecli);
        }
    }

    /**
     * Converts the PDF-Text into a Text Document, saves this to the resources and deletes the PDF Document afterwards
     *
     * @return
     */
    private ArrayList<String> convertToText() {
        PDFToTextConverter pdfToTextConverter = new PDFToTextConverter();
        pdfToTextConverter.convertPDFToText("resources/DecisionPDFs/temp_document.pdf");

        File f = new File("resources/DecisionPDFs/temp_document.pdf");
        f.delete();

        ArrayList<String> nec_decision_parts = new ArrayList<>();
        try {
            File myObj = new File("resources/DecisionPDFs/temp_document.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String str_part = myReader.nextLine();
                if (str_part.contains("Gründe:")) {
                    break;
                } else {
                    nec_decision_parts.add(str_part);
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return nec_decision_parts;
    }

    /**
     * @param sentences
     * @param ecli
     */
    private HashMap<String, ArrayList<String>> extractEntities(ArrayList<String> sentences, String ecli) throws IOException, InterruptedException {
        ArrayList<String> listOfWords = new ArrayList<>();
        //Pattern pattern  = Pattern.compile("[^A-Za-z0-9äÄöÖüÜß\\s][a-zA-Z0-9äÄöÖüÜß]|[a-zA-Z0-9äÄöÖüÜß][^A-Za-z0-9äÄöÖüÜß\\s]");
        //Pattern pattern = Pattern.compile("[^A-Za-z0-9äÄöÖüÜß\\s]");
        //Pattern pattern_two = Pattern.compile("[a-zA-Z0-9äÄöÖüÜß][^A-Za-z0-9äÄöÖüÜß\\\\s]");
        Pattern pattern = Pattern.compile("[\\.\\,]");


        for (String sentence : sentences) {
            //sentence = sentence.replaceAll("(?i)([\\.\\,])", " $1");
            sentence = sentence.replaceAll("(?i)([^A-Za-z0-9äÄöÖüÜß\\s\\/\\-])", " $1");
            sentence = sentence.replaceAll("([^A-Za-z0-9äÄöÖüÜß\\s\\/\\-])(?i)", "$1 ");
            String[] words = sentence.split(" ");
            for (String w : words) {
                //w = w.replaceAll("[\\:\\;\\.\\,\\(\\)]", "");
                listOfWords.add(w);
            }
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter("resources/EntityRecognition/text.tsv"));
        for (String element : listOfWords) {
            writer.write(element + "\n");
        }
        writer.close();


        ProcessBuilder pb = new ProcessBuilder("java", "-jar", "resources/EntityRecognition/GermaNER-nofb-09-09-2015.jar", "-mx1400m", "-t", "resources/EntityRecognition/text.tsv", "-o", "resources/EntityRecognition/output_text.tsv");
        Process p = pb.start();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String s = "";
        while((s = in.readLine()) != null){
            System.out.println(s);
        }
        int status = p.waitFor();
        System.out.println("Exited with status: " + status);


        ArrayList<String> persons = new ArrayList<>();
        ArrayList<String> locations = new ArrayList<>();
        ArrayList<String> organisations = new ArrayList<>();
        try {
            File myObj = new File("resources/EntityRecognition/output_text.tsv");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String tagged_string = myReader.nextLine();
                String[] parts = tagged_string.split(" ");
                if (parts[parts.length - 1].equals("B-PER") || parts[parts.length - 1].equals("I-PER")) {
                    persons.add(tagged_string);
                }
                else if (parts[parts.length - 1].equals("B-LOC") || parts[parts.length - 1].equals("I-LOC")) {
                    locations.add(tagged_string);
                }
                else if (parts[parts.length - 1].equals("B-ORG") || parts[parts.length - 1].equals("I-ORG")) {
                    organisations.add(tagged_string);
                }

            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        // For Organisations
        String temp_orgs = "";
        for (String org : organisations) {
            String[] ps = org.split("  ");
            temp_orgs = temp_orgs + ps[1] + " " + ps[0] + " ";
        }
        organisations.clear();
        List<String> fl = Arrays.asList(temp_orgs.split("B-ORG"));
        for (String e : fl) {
            e = e.trim();
            e = e.replace("I-ORG", "");
            if (!e.equals("")) {
                e = e.replace("  ", " ");
                organisations.add(e);
            }
        }

        //For Persons
        String temp_per = "";
        for (String per : persons) {
            String[] ps_per = per.split("  ");
            temp_per = temp_per + ps_per[1] + " " + ps_per[0] + " ";
        }
        persons.clear();
        List<String> fl_per = Arrays.asList(temp_per.split("B-PER"));
        for (String e : fl_per) {
            e = e.trim();
            e = e.replace("I-PER", "");
            if (!e.equals("")) {
                e = e.replace("  ", " ");
                persons.add(e);
            }
        }

        //For Locations
        String temp_loc = "";
        for (String loc : locations) {
            String[] ps_loc = loc.split("  ");
            temp_loc = temp_loc + ps_loc[1] + " " + ps_loc[0] + " ";
        }
        locations.clear();
        List<String> fl_loc = Arrays.asList(temp_loc.split("B-LOC"));
        for (String e : fl_loc) {
            e = e.trim();
            e = e.replace("I-LOC", "");
            if (!e.equals("")) {
                e = e.replace("  ", " ");
                locations.add(e);
            }
        }

        // Remove duplicates
        LinkedHashSet<String> hashSet = new LinkedHashSet<>(organisations);
        organisations.clear();
        organisations.addAll(hashSet);

        LinkedHashSet<String> hashSet2 = new LinkedHashSet<>(persons);
        persons.clear();
        persons.addAll(hashSet2);

        LinkedHashSet<String> hashSet3 = new LinkedHashSet<>(locations);
        locations.clear();
        locations.addAll(hashSet3);

        HashMap<String, ArrayList<String>> entities = new HashMap<>();

        entities.put("Persons", persons);
        entities.put("Organisations", organisations);
        entities.put("Locations", locations);

        ecli = ecli.split(":")[4];
        ecli = ecli.replace(".", "_");
        FileWriter wr = new FileWriter("resources/EntityRecognition/" + ecli + ".txt");
        for(String str : persons) {
            wr.write(str + System.lineSeparator());
        }
        wr.close();

        // Delete the files
        File fo = new File("resources/EntityRecognition/output_text.tsv");
        File fi = new File("resources/EntityRecognition/text.tsv");
        File fin = new File("resources/EntityRecognition/text.tsv.normalized");
        //fo.delete();
        //fi.delete();
        //fin.delete();

        return entities;
    }


    /**
     *
     * @return
     */
    public ArrayList<String> getOccuringPersons() {
        return _extractedEntities.get("Persons");
    }

    /**
     *
     * @return
     */
    public ArrayList<String> getOccuringLocations() {
        return _extractedEntities.get("Locations");
    }

    /**
     *
     * @return
     */
    public ArrayList<String> getOccuringOrganisations() {
        return _extractedEntities.get("Organisations");
    }
}
