package havabol;

import java.util.ArrayList;

/**
 * Created by tkb442 on 2/17/17.
 */
public class HBException extends Exception
{
    public HBException (String error, String token, ArrayList<String> sourceLineM)
    {
        super(error + "\n\t(Line: " + Scanner.currentToken.iSourceLineNr + " Column: " + Scanner.currentToken.iColPos + ")" +
                "\n\tError:" + token + " >" + sourceLineM.get(Scanner.currentToken.iSourceLineNr));
    }

    public HBException (String error)
    {
        super(error);
    }
}
