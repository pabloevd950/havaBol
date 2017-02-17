package havabol;

public class STIdentifier extends STEntry
{
    public int dclType;
    public int structure;
    public int parm;
    public int nonLocal;

    public STIdentifier (String symbol, int primClassif, int dclType, int structure, int parm, int nonLocal)
    {
        super(symbol, primClassif);
        this.dclType = dclType;
        this.structure = structure;
        this.parm = parm;
        this.nonLocal = nonLocal;
    }
}
