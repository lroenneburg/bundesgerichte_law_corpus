package bundesgerichte_law_corpus;

import bundesgerichte_law_corpus.elasticsearch.repository.DecisionRepository;
import bundesgerichte_law_corpus.model.Decision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Component
public class ScheduledTasks {
    private DecisionDownloader _decisionDownloader = new DecisionDownloader();
    private DataMapper _dataMapper = new DataMapper();

    @Autowired
    DecisionRepository _decisionRepository;

    //@Scheduled(cron = "0 */23 * ? * *")
    @Scheduled(cron = "0 0 */12 ? * *")
    public void dailyRIIRSSRequest() {
        System.out.println("Start crawling new RII-Feed Decisions...");
        ArrayList<String> new_ids = new ArrayList<>();

        try {
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
}
