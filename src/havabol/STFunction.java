package havabol;

import java.util.ArrayList;

public class STFunction extends STEntry
{
    public int returnType;
    public int definedBy;
    public int numArgs;
    public ArrayList <String> parmList;

    /**\
     * STFunction constructor that is a subclass of STEntry that helps identify the symbol in the symbol table
     * <p>
     * STFunction class classify the function primary classification into user or builtin
     *
     * @param symbol string for the symbol
     * @param primClassif primary classification of the symbol
     * @param returnType return data type (Int, Float, Bool, Date, Void)
     * @param definedBy what defined it (user, builtin)
     * @param numArgs the number of arguments. For variable length, VAR_ARGS.
     * @param parmList reference to an ArrayList of formal parameters
     */
    public STFunction(String symbol, int primClassif, int returnType, int definedBy, int numArgs, ArrayList <String> parmList)
    {
        super(symbol, primClassif);
        this.returnType = returnType;
        this.definedBy = definedBy;
        this.numArgs = numArgs;
        this.parmList = parmList;
    }
}
