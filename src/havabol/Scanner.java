package havabol;

/*
 * This is the simple Scanner class for the HavaBol programming language.
 * All errors and exceptions are thrown up to the calling method and output to stderr from there.
 */

import havabol.SymbolTable.STControl;
import havabol.SymbolTable.STFunction;
import havabol.SymbolTable.SymbolTable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Scanner
{
    // public variables
    public static final String delimiters = " \t;:()\'\"=!<>+-*/[]#,^\n"; // terminate a token
    public static Token currentToken;       // the token established with the most recent call to getNext()
    public int iSourceLineNr;              // line number in sourceLineM for current text line
    public int iColPos;                    // column position within the current text line
    public Token nextToken;                 // the token following the currentToken
    public String sourceFileNm;            // source code file name
    public Boolean bShowToken = false;
    public Boolean bShowExpr = false;
    public Boolean bShowAssign = false;


    // private variables
    private ArrayList<String> sourceLineM;  // array list of source text lines
    private SymbolTable symbolTable;        // object responsible for providing symbol definitions
    private char[] textCharM;               // char [] for the current text line

    /**
     * Scanner constructor that takes in the source file name and simple table as
     * arguments and saves them. Constructor also initializes other variables needed
     * for keeping track of position in the source file lines.
     * <p>
     * The constructor opens the source file specified and reads all the lines into an array list.
     * If there is any IOExceptions, it gets thrown back up to main.
     * The constructor then sets our char [] to the first line of the source file so
     * the getNext() method can find the tokens.
     *
     * @param sourceFileNm provided as an argument to the main function, this is the name of the source file
     * @param symbolTable  object that contains symbol definitions for our programming language
     * @throws IOException Exception if there is a problem opening or reading from the source file
     */
    public Scanner(String sourceFileNm, SymbolTable symbolTable) throws Exception
    {
        // save source file name and symbol table object
        this.sourceFileNm = sourceFileNm;
        this.symbolTable = symbolTable;

        // initialize variables to read in source file
        java.util.Scanner inp = new java.util.Scanner(new File(sourceFileNm));

        // initialize variables to read in source file
        sourceLineM = new ArrayList<String>();

        // read source file until EOF and populate sourceLineM (ArrayList)
        while (inp.hasNext())
            sourceLineM.add(inp.nextLine());

        // initialize variables to track position in source file
        iSourceLineNr = -1;
        iColPos = 0;
        textCharM = sourceLineM.get(0).toCharArray();
        nextToken = new Token();

        // check that there is a next token, of there isn't, then source file is empty
        getNext();
        if (nextToken.tokenStr.isEmpty())
            throw new HBException("Empty source file:" + sourceFileNm);

        inp.close();
    }

    /**
     * This method gets the next token in the source file line. If there are no more tokens, it returns
     * an empty string, otherwise it returns the token given that there were no processing errors.
     * <p>
     * The method automatically advances to the next source line when necessary and sets the attributes for
     * our Token object.
     *
     * @return returns an empty string if there are no remaining tokens and the token string if one is found
     * @throws Exception generic Exception type to handle an processing errors found such as unterminated
     *                   string literal, invalid floating point, or an invalid numeric constant
     */
    public String getNext() throws Exception
    {
        String token = "";                  // string used to create the token from the source file
        String operator = "+-*/<>!=#^";     // list of operators
        String separator = "():;[]";        // list of separators
        String operators = "<>!=^*/";       // list of potential two character operations
        String operations = "<=,>=,!=,==,+=,-=,*=,/=, ^=";
        String escapeChars = "t\"na\\\''";

        // set currentToken to nextToken object to keep track of tokens and reset nextToken
        clone(nextToken);
        nextToken = new Token();

        // check if we encountered EOF
        if (currentToken.primClassif == Token.EOF)
            return "";

        // Automatically advance to the next source line when necessary
        if (iColPos >= textCharM.length || iSourceLineNr == -1)
        { // cursor position is beyond the length of the line so grab a new line,
          // if iSourceLineNr is equal to -1, then this is the first line.
            do
            { // find a line that is not empty
                if (++iSourceLineNr >= sourceLineM.size())
                { // EOF encountered, there are no more tokens
                    nextToken.primClassif = Token.EOF;
                    return currentToken.tokenStr;
                }

                //check for comments
                if(sourceLineM.get(iSourceLineNr).contains("//")
                                            && !sourceLineM.get(iSourceLineNr).matches("['\"]//['\"]"))
                {
                    int index = sourceLineM.get(iSourceLineNr).indexOf("//");
                    if(index == 0)
                    {
                        //Check if whole line is comment
                        if (++iSourceLineNr >= sourceLineM.size())
                        {
                            nextToken.primClassif = Token.EOF;
                            return currentToken.tokenStr;
                        }
                    }
                    else // throw away part of line that is comment
                        sourceLineM.set(iSourceLineNr, sourceLineM.get(iSourceLineNr).substring(0, index).trim());
                }

                textCharM = sourceLineM.get(iSourceLineNr).toCharArray();
                iColPos = 0;
            } // if the line we just grabbed is empty (no tokens) advance to next line
            while (sourceLineM.get(iSourceLineNr).trim().length() == 0);
        }

        // trim white space at the beginning of the line starting from the cursor position
        while (iColPos < textCharM.length && Character.isWhitespace(textCharM[iColPos]))
            iColPos++;

        // set line and position number for the next token to be at the beginning of the token
        nextToken.iSourceLineNr = iSourceLineNr;
        nextToken.iColPos = iColPos;

        // create token
        if (textCharM[iColPos] == '"' || textCharM[iColPos] == '\'')
        {// token contains a string
            // save the quote so we can find the string literal terminator
            char quote = textCharM[iColPos++];

            // create string literal token
            while (true)
            { // loop until a matching quotation is found that is not escaped
                if (textCharM[iColPos] == quote && textCharM[iColPos - 1] != '\\')
                    break;
                else if (iColPos >= textCharM.length - 1)
                    // unterminated String literal encountered
                    throw new HBException("Unterminated String Literal", token, sourceLineM);

                // determine escape character value
                if (textCharM[iColPos] == '\\' && escapeChars.contains(String.valueOf(textCharM[iColPos+1])))
                {// escape char found, check to see what the next char contains to determine escaped value
                    if(textCharM[iColPos+1] == 'n')
                        token += String.valueOf((char)0x0a);
                    else if (textCharM[iColPos+1] == 't')
                        token += String.valueOf((char)0x09);
                    else if (textCharM[iColPos+1] == 'a')
                        token += String.valueOf((char)0x0A);
                    else if (textCharM[iColPos+1] == '\\')
                        token += '\\';
                    else if (textCharM[iColPos+1] == '"')
                        token += '"';
                    else if (textCharM[iColPos+1] == '\'')
                        token += '\'';
                    // increment iColPos to the next char after the escape values
                    iColPos += 2;
                }
                else
                    token += textCharM[iColPos++];
            }
            // save Token attribute type as a string and advance cursor position away from quotation mark
            iColPos++;
            token = "\"" + token + "\"";
            nextToken.subClassif = Token.STRING;
        }
        else if (delimiters.indexOf((textCharM[iColPos])) >= 0)
        {// token contains a delimiter
            token += textCharM[iColPos++];

            // check if the delimiter we saved is an operator, if it is and we are within our boundaries,
            // then check if then next position contains an '='
            if (operators.contains(token) && iColPos != textCharM.length && textCharM[iColPos] == '=')
                token += textCharM[iColPos++];
        }
        else
            // token is an operand
            for (; iColPos < textCharM.length; iColPos++)
            {// build token until a delimiter is found
                if (delimiters.indexOf((textCharM[iColPos])) >= 0)
                    break;
                token += textCharM[iColPos];
            }

        // determine token classification
        if (token.equals("debug"))
            //token is a debug
            nextToken.primClassif = Token.DEBUG;
        else if (operator.contains(token) || operations.contains(token))//bridget was here
            // token is an operator
            nextToken.primClassif = Token.OPERATOR;
        else if (separator.contains(token))
            // token is a separator
            nextToken.primClassif = Token.SEPARATOR;
        else if (  symbolTable.getSymbol(token) == null
                || symbolTable.getSymbol(token).primClassif == Token.OPERAND)
        {   // token is an operand
            nextToken.primClassif = Token.OPERAND;

            // determine sub classification of operand token
            if (nextToken.subClassif == Token.STRING);
                // token is a string literal which is already set
            else if (Character.isDigit(token.charAt(0)))
            {   // token starts with a digit, determine if integer or float
                if (token.contains("."))
                {// token is a float
                    if (token.matches("^[0-9]+\\.[0-9]*$"))
                        nextToken.subClassif = Token.FLOAT;
                    else
                        //token contains an improper floating point
                        throw new HBException("Invalid Numeric Constant:", token, sourceLineM);
                }
                else if (token.matches("^[0-9]+$"))
                    // token is an int
                    nextToken.subClassif = Token.INTEGER;
                else
                    // token starts with a digit but contains non-digit characters
                    throw new HBException("Invalid Numeric Constant:", token, sourceLineM);
            }
            else if (token.equals("T") || token.equals("F"))
                //token is a boolean (T or F)
                nextToken.subClassif = Token.BOOLEAN;
            else
                // token is an identifier (variable or data type)
                nextToken.subClassif = Token.IDENTIFIER;
        }
        else if (symbolTable.getSymbol(token).primClassif == Token.CONTROL)
        {// control token recognized
            STControl entry = (STControl)symbolTable.getSymbol(token);
            nextToken.primClassif = Token.CONTROL;

            if (entry.subClassif == Token.FLOW)
                nextToken.subClassif = Token.FLOW;
            else if (entry.subClassif == Token.END)
                nextToken.subClassif = Token.END;
            else if (entry.subClassif == Token.DECLARE)
                nextToken.subClassif = Token.DECLARE;
        }
        else if (symbolTable.getSymbol(token).primClassif == Token.FUNCTION)
        {// function token recognized
            STFunction entry = (STFunction)symbolTable.getSymbol(token);
            nextToken.primClassif = Token.FUNCTION;

            if (entry.definedBy == Token.BUILTIN)
                nextToken.subClassif = Token.BUILTIN;
            else if (entry.definedBy == Token.USER)
                nextToken.subClassif = Token.USER;
        }

        // pablo wtf is this? you added a step to add quotation marks to
        // strings on line 190 and now you're taking them back off?
        if(nextToken.subClassif == Token.STRING){
            token = token.substring(1, token.length() - 1);
        }

        // set nextToken to the token built and return the current token string
        nextToken.tokenStr = token;

        // check if debugging is on
        if(bShowToken)
        {// if only want one , uncomment line below
            System.out.print("\t\t...");
            currentToken.printToken();

            // bShowToken = false;
        }

        return currentToken.tokenStr;
    }

    /**
     * This method clones the given Token object to the currentToken object
     * <p>
     * tokenStr, primClassif, subClassif, iSourceLineNr, iColPos, are all copied into
     * currentToken
     *
     * @param token is the token in which needs to be cloned.
     */
    public void clone(Token token)
    {
        currentToken = new Token();
        currentToken.tokenStr = token.tokenStr;
        currentToken.primClassif = token.primClassif;
        currentToken.subClassif = token.subClassif;
        currentToken.iSourceLineNr = token.iSourceLineNr;
        currentToken.iColPos = token.iColPos;
    }

    /**
     * This method is provided to set the scanner back to a certain location in code.
     * <p>
     * This is will take the input token, set the source line and col pos to that token,
     * and then rebuild current and nextToken to have currentToken be the one we sent and nextToken
     * be the token after in the line.
     *
     * @param token This is token we want to set our scanner to
     * @throws Exception if there is a getNext exception, we are ready to handle it
     */
    public void setTo(Token token) throws Exception
    {
        // set line, column position, and the line char array to the token we are given
        this.iSourceLineNr = token.iSourceLineNr;
        this.iColPos = token.iColPos;
        this.textCharM = sourceLineM.get(this.iSourceLineNr).toCharArray();

        // call getNext, currentToken = token that would of been next before we changed the poition
        //               nextToken = the token we are changing our position to
        getNext();

        // call getNext again, this time currentToken will be the token we want to be set to
        // and nextToken will be the one that follows and normal continuation will occur
        getNext();
    }
}