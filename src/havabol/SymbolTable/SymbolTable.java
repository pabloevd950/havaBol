package havabol.SymbolTable;

import havabol.Token;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable
{
    public HashMap <String, STEntry> ht;
    private int VAR_ARGS = -1;
    private ArrayList<String> parmArgs;

    /**
     * SymbolTable constructor that will be used to provide methods for maintaining the global symbol tables
     * <p>
     * This constructor uses a hash map with a string as a key and STEntry object for the value.
     */
    public SymbolTable()
    {
        ht = new HashMap <String, STEntry>();
        initGlobal();
    }

    /**
     * getSymbol method returns the symbol table entry for the given symbol
     * <p>
     * returns the STEntry for the inputted symbol or will raise a NULL exception
     *
     * @param symbol string for the symbol
     * @return STEntry which is either an Identifier, Control, or Function subclass
     */
    public STEntry getSymbol(String symbol)
    {
        return ht.get(symbol);
    }

    /**
     * putSymbol method stores the symbol and its corresponding entry in the symbol table
     * <p>
     * STEntry has 3 subclasses, however we do not care which it is here
     *
     * @param symbol string for the symbol
     * @param entry STEntry which is either an Identifier, Control, or Function subclass
     */
    public void putSymbol(String symbol, STEntry entry)
    {
        ht.put(symbol, entry);
    }

    /**
     * initGlobal method inserts reserved symbol entries into the global symbol table
     * <p>
     * intializes builtin values for HavaBol
     */
    private void initGlobal()
    {
        ht.put("def", new STControl("def", Token.CONTROL, Token.FLOW));
        ht.put("enddef", new STControl("enddef", Token.CONTROL, Token.END));
        ht.put("if", new STControl("if", Token.CONTROL, Token.FLOW));
        ht.put("endif", new STControl("endif", Token.CONTROL,Token.END));
        ht.put("else", new STControl("else", Token.CONTROL, Token.END));
        ht.put("for", new STControl("for",Token.CONTROL,Token.FLOW));
        ht.put("endfor", new STControl("endfor", Token.CONTROL, Token.END));
        ht.put("while", new STControl("while", Token.CONTROL, Token.FLOW));
        ht.put("endwhile", new STControl("endwhile", Token.CONTROL, Token.END));
        ht.put("print", new STFunction("print",Token.FUNCTION,Token.VOID, Token.BUILTIN, VAR_ARGS, parmArgs));
        ht.put("Int", new STControl("Int",Token.CONTROL,Token.DECLARE));
        ht.put("Float", new STControl("Float",Token.CONTROL,Token.DECLARE));
        ht.put("String", new STControl("String", Token.CONTROL, Token.DECLARE));
        ht.put("Bool", new STControl("Bool", Token.CONTROL, Token.DECLARE));
        ht.put("Date", new STControl("Date", Token.CONTROL, Token.DECLARE));
        ht.put("LENGTH", new STFunction("LENGTH", Token.FUNCTION, Token.INTEGER, Token.BUILTIN, VAR_ARGS, parmArgs));
        ht.put("MAXLENGTH", new STFunction("MAXLENGTH", Token.FUNCTION, Token.INTEGER, Token.BUILTIN, VAR_ARGS, parmArgs));
        ht.put("SPACES", new STFunction("SPACES", Token.FUNCTION, Token.INTEGER, Token.BUILTIN, VAR_ARGS, parmArgs));
        ht.put("ELEM", new STFunction("ELEM", Token.FUNCTION, Token.INTEGER, Token.BUILTIN, VAR_ARGS, parmArgs));
        ht.put("MAXELEM", new STFunction("MAXELEM", Token.FUNCTION, Token.INTEGER, Token.BUILTIN, VAR_ARGS, parmArgs));
        ht.put("and", new STEntry("and", Token.OPERATOR));
        ht.put("or", new STEntry("or", Token.OPERATOR));
        ht.put("not", new STEntry("not", Token.OPERATOR));
        ht.put("in", new STEntry("in", Token.OPERATOR));
        ht.put("notin", new STEntry("notin", Token.OPERATOR));
    }
}