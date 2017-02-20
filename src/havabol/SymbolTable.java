package havabol;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable
{
    public HashMap <String, STEntry> ht;
    private int VAR_ARGS = -1;
    private ArrayList<String> parmArgs;

    /**
     * SymbolTable constructor
     */
    public SymbolTable()
    {
        ht = new HashMap <String, STEntry>();
        initGlobal();
    }

    public STEntry getSymbol(String symbol)
    {
        return ht.get(symbol);
    }

    public void putSymbol(String symbol, STEntry entry)
    {
        ht.put(symbol, entry);
    }

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