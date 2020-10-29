package bundesgerichte_law_corpus.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.ArrayList;


@Document(indexName = "bundesgerichte_decisions", type = "decision")
/**
 * This class maps a decision on a Java Object and stores all the information of the Decision-XML and additionally
 * the extracted Information like occurring citations and involved Judges
 */
public class Decision {

    @Id
    private String decisionId;

    @Field(type = FieldType.Text)
    private String ecli;

    @Field(type = FieldType.Text)
    private String courtType;

    @Field(type = FieldType.Text)
    private String formation;

    @Field(type = FieldType.Date)
    private String decisionDate;

    @Field(type = FieldType.Text)
    private ArrayList<String> docketnumber;

    @Field(type = FieldType.Text)
    private String decisionType;

    @Field(type = FieldType.Text)
    private ArrayList<String> norms;

    @Field(type = FieldType.Text)
    private ArrayList<String> previousCourts;

    @Field(type = FieldType.Text)
    private String decisionTitle;

    @Field(type = FieldType.Text)
    private ArrayList<String> guidingPrinciple;

    @Field(type = FieldType.Text)
    private ArrayList<String> otherOrientationSentence;

    @Field(type = FieldType.Text)
    private ArrayList<String> tenor;

    private ArrayList<DecisionSection> fact;

    @Field(type = FieldType.Nested, includeInParent = true)
    private ArrayList<DecisionSection> decisionReasons;

    private ArrayList<DecisionSection> dissentingOpinions;

    @Field(type = FieldType.Text)
    private String url;

    @Field(type = FieldType.Text)
    private ArrayList<String> occurringCitations;

    @Field(type = FieldType.Text)
    private ArrayList<String> occurringJudges;

    /**
     * Creates a decision Object with all relevant Information related to the decision
     * @param decisionId The unique identifier of the decision
     * @param ecli The ecli (European Case Law Identifier) of the decision
     * @param courtType The type of the court (e.g. BVerfG or BGH or ...)
     * @param formation The formation ("Spruchkoerper") of the court
     * @param decisionDate The date the Decision was taken
     * @param docketnumber The docketNumber of the decision
     * @param decisionType The type of the decision
     * @param norms All norms that were used for the decision
     * @param previousCourts All previous courts of the decision
     * @param decisionTitle The decision title
     * @param guidingPrinciple The guiding principle for the decision
     * @param otherOrientationSentence Other orientation sentences
     * @param tenor All tenor sentences of the decision
     * @param fact The fact ("Tatbestand") of the decision
     * @param decisionReasons The Reasons for the decision
     * @param dissentingOpinions
     * @param url The url to the decision on "https://rechtsprechung-im-internet.de"
     * @param occurringCitations The citations that are found in the reasons of the decision
     * @param occurringJudges The judges which are involved in the decision
     */
    public Decision(String decisionId, String ecli, String courtType, String formation, String decisionDate,
                    ArrayList<String> docketnumber, String decisionType, ArrayList<String> norms, ArrayList<String> previousCourts,
                    String decisionTitle, ArrayList<String> guidingPrinciple, ArrayList<String> otherOrientationSentence, ArrayList<String> tenor,
                    ArrayList<DecisionSection> fact, ArrayList<DecisionSection> decisionReasons, ArrayList<DecisionSection> dissentingOpinions, String url,
                    ArrayList<String> occurringCitations, ArrayList<String> occurringJudges) {
        this.decisionId = decisionId;
        this.ecli = ecli;
        this.courtType = courtType;
        this.formation = formation;
        this.decisionDate = decisionDate;
        this.docketnumber = docketnumber;
        this.decisionType = decisionType;
        this.norms = norms;
        this.previousCourts = previousCourts;
        this.decisionTitle = decisionTitle;
        this.guidingPrinciple = guidingPrinciple;
        this.otherOrientationSentence = otherOrientationSentence;
        this.tenor = tenor;
        this.fact = fact;
        this.decisionReasons = decisionReasons;
        this.dissentingOpinions = dissentingOpinions;
        this.url = url;
        this.occurringCitations = occurringCitations;
        this.occurringJudges = occurringJudges;
    }


    /**
     * Gets the unique identifier (id) of this decision
     * @return the identifier
     */
    public String getDecisionID() {
        return decisionId;
    }

    /**
     * Gets the ecli (European Case Law Identifier) of this decision
     * @return The ecli
     */
    public String getEcli() {
        return ecli;
    }

    /**
     * Gets the court type of this decision
     * @return The court type
     */
    public String getCourtType() {
        return courtType;
    }

    /**
     * Gets the formation of this decision
     * @return The formation
     */
    public String getFormation() {
        return formation;
    }

    /**
     * Gets the date of of this decision
     * @return The decision date
     */
    public String getDecisionDate() {
        return decisionDate;
    }

    /**
     * Gets the docket number of the decision
     * @return The docket number
     */
    public ArrayList<String> getDocketNumber() {
        return docketnumber;
    }

    /**
     * Gets the type of this decision
     * @return The decision type
     */
    public String getDecisionType() {
        return decisionType;
    }

    /**
     * Gets all norms of this decision
     * @return The norms
     */
    public ArrayList<String> getNorms() {
        return norms;
    }

    /**
     * Gets all previous courts of this decision
     * @return The previous courts
     */
    public ArrayList<String> getPreviousCourts() {
        return previousCourts;
    }

    /**
     * Gets the title for this decision
     * @return The decision title
     */
    public String getDecisionTitle() {
        return decisionTitle;
    }

    /**
     * Gets the guiding principle of this decision
     * @return The guiding principle
     */
    public ArrayList<String> getGuidingPrinciple() {
        return guidingPrinciple;
    }

    /**
     * Gets the other orientation sentence
     * @return The list of the sentences
     */
    public ArrayList<String> getOtherOrientationSentence() {
        return otherOrientationSentence;
    }

    /**
     * Gets all Tenor sentences of this decision
     * @return The tenor sentences
     */
    public ArrayList<String> getTenor() {
        return tenor;
    }

    /**
     * Gets the fact ("Tatbestand") of this decision
     * @return The fact
     */
    public ArrayList<DecisionSection> getFact() {
        return fact;
    }


    /**
     * Gets all reasons for this decision
     * @return The decision reasons as sections
     */
    public ArrayList<DecisionSection> getDecisionReasons() {
        return decisionReasons;
    }


    /**
     * Gets the dissenting opinion of this decision
     * @return The dissenting opinion
     */
    public ArrayList<DecisionSection> getDissentingOpinions() {
        return dissentingOpinions;
    }

    /**
     * Gets the url of this decision
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets all citations occuring in this decision
     * @return The citations as reference Numbers ("Fundstelle")
     */
    public ArrayList<String> getOccurringCitations() {
        return occurringCitations;
    }

    /**
     * Gets all judges involved in this decision
     * @return The judges names
     */
    public ArrayList<String> getOccurringJudges() {
        return occurringJudges;
    }
}
