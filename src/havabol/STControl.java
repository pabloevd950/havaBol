package havabol;

public class STControl extends STEntry
{
    public int subClassif;

    public STControl(String symbol, int primClassif, int subClassif)
    {
        super(symbol, primClassif);
        this.subClassif = subClassif;
    }
}
