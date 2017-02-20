package havabol;

public class STControl extends STEntry
{
    public int subClassif;


    /**
     * STControl constructor that is a subclass of STEntry that helps identify the symbol in the symbol table
     * <p>
     * STControl class classify the control primary classification into flow, end, declare
     *
     * @param symbol string for the symbol
     * @param primClassif primary classification of the symbol
     * @param subClassif subClassification (flow, end, declare)
     */
    public STControl(String symbol, int primClassif, int subClassif)
    {
        super(symbol, primClassif);
        this.subClassif = subClassif;
    }
}
