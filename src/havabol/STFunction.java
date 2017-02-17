package havabol;

import java.util.ArrayList;

public class STFunction extends STEntry
{
    public int returnType;
    public int definedBy;
    public int numArgs;
    public ArrayList <String> parmList;

    public STFunction(String symbol, int primClassif, int returnType, int definedBy, int numArgs, ArrayList <String> parmList)
    {
        super(symbol, primClassif);
        this.returnType = returnType;
        this.definedBy = definedBy;
        this.numArgs = numArgs;
        this.parmList = parmList;
    }
}
