package havabol;

public class STEntry
{
    public String symbol;
    public int primClassif;

    /**
     * STEntry constructor creates an object for the symbol table to identify the token
     * <p>
     * STEntry has 3 subclasses, STIdentifier, STControl, STFunction. These subclasses contain
     * additional information about the token entry related to the prime classification.
     *
     * @param symbol string for the symbol
     * @param primClassif primary classification of the symbol
     */

    public STEntry(String symbol, int primClassif)
    {
        this.symbol = symbol;
        this.primClassif = primClassif;
    }
}