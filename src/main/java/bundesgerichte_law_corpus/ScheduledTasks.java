package bundesgerichte_law_corpus;

import bundesgerichte_law_corpus.elasticsearch.repository.DecisionRepository;
import bundesgerichte_law_corpus.model.Decision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Component
public class ScheduledTasks {
    // The Decision Downloader, who downloads the newest decisions
    private DecisionDownloader _decisionDownloader = new DecisionDownloader();
    // The DataMapper, who maps the XML document of a decision to a decision Object
    private DataMapper _dataMapper = new DataMapper();

    // The repository of the ES database
    @Autowired
    DecisionRepository _decisionRepository;

    /**
     * Calls the "Rechtsprechung-Im-Internet.de" Website to look, if there are new decisions
     */
    //@Scheduled(cron = "0 */5 * ? * *")
    @Scheduled(cron = "0 0 */12 ? * *") //Every 12 hours
    public void dailyRequest() {
        System.out.println("Start crawling new RII-Feed Decisions...");
        ArrayList<String> new_ids = new ArrayList<>();

        try {
            // We map the new decisions
            new_ids = _decisionDownloader.updateDatabase();
            ArrayList<Decision> decisions = _dataMapper.mapDecisionObjects(new_ids);
            // Upload the Decisions to the Elastic Database
            for (Decision d : decisions) {
                _decisionRepository.save(d);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        System.out.println("RII-Feed Request executed on " + formatter.format(date));
        System.out.println( new_ids.size() + " new Decisions added to Database.");
    }

    /**
     * Updates the Decision Network
     */
    @Scheduled(cron = "0 0 1 ? * SUN") //Every sunday at at 1 AM
    public void updateNetwork() {
        Iterable<Decision> decisions = _decisionRepository.findAll();
        NetworkController networkController = new NetworkController();
        networkController.createNetwork((ArrayList<Decision>) decisions);
        //TODO pagerank, centralities, clusterung,etc.
    }

    /**
     * Calls the NLP Tasks
     */
    @Scheduled(cron = "0 0 2 ? * *") // Everyday at 2 Am
    public void doNLPTasks() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("localhost:5000")).build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            System.out.println("done");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
