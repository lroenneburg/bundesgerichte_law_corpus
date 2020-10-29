package bundesgerichte_law_corpus;

import java.io.IOException;

/**
 *
 */
public class PDFToTextConverter {


    /**
     * Converts a PDF-file to a plaintext file.
     * @param path The path where to file to convert is found.
     */
    public void convertPDFToText(String path) {
        try {
            // PDF in Text umwandeln


            ProcessBuilder pb = new ProcessBuilder("resources/DecisionPDFs/pdftotext", "-enc", "UTF-8", path);

            Process p = pb.start();
            p.waitFor();




        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
