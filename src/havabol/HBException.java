package havabol;

import java.util.ArrayList;

public class HBException extends Exception
{
    /**
     * HBException constructor that takes in error message, token string, and the source file line list
     * <p>
     * Invokes super to print the error and accesses line and column number from the Token class.
     *
     * @param error contains the error message to be printed
     * @param token contains the token that caused the error
     * @param sourceLineM contains the list of lines from the source file
     */
    public HBException (String error, String token, ArrayList<String> sourceLineM)
    {
        super(error
                + "\n\t(Line: " + Scanner.currentToken.iSourceLineNr + " Column: " + Scanner.currentToken.iColPos + ")"
                + "\n\tError:" + token + " >" + sourceLineM.get(Scanner.currentToken.iSourceLineNr));
    }

    /**
     * HBException constructor that takes in error message and prints it
     * <p>
     * Invokes super to print default error message
     *
     * @param error contains the error message to be printed
     */
    public HBException (String error)
    {
        super(error);
    }
}
