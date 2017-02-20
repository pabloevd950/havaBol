package havabol;

/*
  cs4713p1.java by Kris Gilly
  This is the simple Scanner class for the HavaBol programming language.
  All errors and exceptions are thrown up to main and output to stderr from there.
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Scanner
{
    // public variables
    public static final String delimiters = " \t;:()\'\"=!<>+-*/[]#,^\n"; // terminate a token
    public static Token currentToken;       // the token established with the most recent call to getNext()
    public Token nextToken;                 // the token following the currentToken

    // private variables
    private String sourceFileNm;            // source code file name
    private ArrayList<String> sourceLineM;  // array list of source text lines
    private SymbolTable symbolTable;        // object responsible for providing symbol definitions
    private char[] textCharM;               // char [] for the current text line
    private int iSourceLineNr;              // line number in sourceLineM for current text line
    private int iColPos;                    // column position within the current text line

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
        iSourceLineNr = 0;
        iColPos = 0;
        textCharM = sourceLineM.get(iSourceLineNr).toCharArray();
        currentToken = new Token();
        nextToken = new Token();

        // check that there is a next token, of there isn't, then source file is empty
        getNext();
        if (nextToken.tokenStr.isEmpty())
            throw new HBException("Empty source file:" + sourceFileNm);

        // print first line in the source file
        System.out.format("  %d %s\n", iSourceLineNr+1, sourceLineM.get(iSourceLineNr));
        inp.close();
    }

    /**
     * This method gets the next token in the source file line. If there are no more tokens, it returns
     * and empty string, otherwise it returns the token given that there were no processing errors.
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

        // set currentToken to nextToken object to keep track of tokens
        clone(nextToken);

        // check if we encountered EOF
        if (nextToken.primClassif == Token.EOF)
            return "";

        // Automatically advance to the next source line when necessary
        if (iColPos >= textCharM.length)
        { // cursor position is beyond the length of the line so grab a new line
            do
            { // find a line that is not empty
                if (++iSourceLineNr >= sourceLineM.size())
                { // EOF encountered, there are no more tokens
                    nextToken.primClassif = Token.EOF;
                    return currentToken.tokenStr;
                }
                textCharM = sourceLineM.get(iSourceLineNr).toCharArray();
                iColPos = 0;
                // print the source line we just grabbed
                System.out.format("  %d %s\n", iSourceLineNr + 1, sourceLineM.get(iSourceLineNr));
            } // if the line we just grabbed is empty (no tokens) advance to next line
            while (sourceLineM.get(iSourceLineNr).trim().length() == 0);
        }

        // trim white space at the beginning of the line starting from the cursor position
        while (iColPos < textCharM.length && Character.isWhitespace(textCharM[iColPos]))
            iColPos++;

        // create token
        if (textCharM[iColPos] == '"' || textCharM[iColPos] == '\'')
        {// token contains a string
            char quote = textCharM[iColPos++];  // save the quote so we can find the string literal terminator

            // create string literal token
            while (true)
            { // loop until a matching quotation is found that is not escaped
                if (textCharM[iColPos] == quote && textCharM[iColPos - 1] != '\\')
                    break;
                else if (iColPos >= textCharM.length - 1)
                    // unterminated String literal encountered
                    throw new HBException("Unterminated String Literal", token, sourceLineM);
                token += textCharM[iColPos++];
            }
            // save Token attribute type as a string and advance cursor position away from quotation mark
            iColPos++;
            nextToken.subClassif = Token.STRING;
        }
        else if (delimiters.indexOf((textCharM[iColPos])) >= 0)
            // token contains a delimiter
            token += textCharM[iColPos++];
        else
            // token is an operand
            for (; iColPos < textCharM.length; iColPos++)
            {// build token until a delimiter is found
                if (delimiters.indexOf((textCharM[iColPos])) >= 0)
                    break;
                token += textCharM[iColPos];
            }

        // determine token classification
        if (operator.contains(token))
            // token is an operator
            nextToken.primClassif = Token.OPERATOR;
        else if (separator.contains(token))
            // token is a separator
            nextToken.primClassif = Token.SEPARATOR;
        else
        {   // token is an operand
            nextToken.primClassif = Token.OPERAND;

            // determine sub classification of token
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
            else
                // token is an identifier (variable or data type)
                nextToken.subClassif = Token.IDENTIFIER;
        }

        // set nextToken to the token built and return the current token string
        nextToken.tokenStr = token;
        return currentToken.tokenStr;
    }

    /**
     * This method clones the given Token object to the current Token object
     * @param token is the token in which needs to be cloned.
     */
    public void clone(Token token)
    {
        currentToken.tokenStr = token.tokenStr;
        currentToken.primClassif = token.primClassif;
        currentToken.subClassif = token.subClassif;
        currentToken.iSourceLineNr = token.iSourceLineNr;
        currentToken.iColPos = token.iColPos;
    }
}