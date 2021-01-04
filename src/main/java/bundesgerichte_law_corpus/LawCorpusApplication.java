package bundesgerichte_law_corpus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.io.*;


/**
 * The LawCorpus application starts the spring Boot Application which runs all services, the web application and
 * the scheduled tasks
 */
@SpringBootApplication
@EnableScheduling
public class LawCorpusApplication extends SpringBootServletInitializer {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(LawCorpusApplication.class, args);
    }

}
