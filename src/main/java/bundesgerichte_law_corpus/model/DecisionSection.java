package bundesgerichte_law_corpus.model;

/**
 * This class Represents a section of a bundesgerichte_law_corpus.model.Decision. It consists of a recital and the section Text.
 */
public class DecisionSection {

    private int _recital = 0;
    private String _text = "";

    /**
     * Creates a Decision Section
     * @param _recital The recital ("Randnummer") of the section
     * @param _text The text in the section
     */
    public DecisionSection(int _recital, String _text) {
        this._recital = _recital;
        this._text = _text;
        //this._recital = 0;
        //this._text = "test";
    }

    /**
     * Gets the recital of this section
     * @return The recital
     */
    public int getRecital() {
        return _recital;
    }

    /**
     * Gets the Text of this section
     * @return The text
     */
    public String getText() {
        return _text;
    }

    /**
     * Sets the Text of this section
     * @param _recital the recital
     */
    public void setRecital(int recital) {
        this._recital = recital;
    }
}
