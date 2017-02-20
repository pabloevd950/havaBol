package havabol;

public class STIdentifier extends STEntry
{
    public int dclType;
    public int structure;
    public int parm;
    public int nonLocal;

    /**
     * STIdentifier constructor that is a subclass of STEntry that helps identify the symbol in the symbol table
     * <p>
     * STIdentifier classifies operands variables (Int, Float, String, Bool, Date)
     *
     * @param symbol string for the symbol
     * @param primClassif primary classification of the symbol
     * @param dclType declaration type (Int, Float, String, Bool, Date)
     * @param structure data structure (primitive, fixed array, unbounded array)
     * @param parm parameter type (not a parm, by reference, by value)
     * @param nonLocal nonLocal base Address Ref (0 - local, 1 - surrounding, ..., k - surrounding, 99 - global)
     */
    public STIdentifier (String symbol, int primClassif, int dclType, int structure, int parm, int nonLocal)
    {
        super(symbol, primClassif);
        this.dclType = dclType;
        this.structure = structure;
        this.parm = parm;
        this.nonLocal = nonLocal;
    }
}
