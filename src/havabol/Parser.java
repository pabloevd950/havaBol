package havabol;

import havabol.SymbolTable.STIdentifier;
import havabol.SymbolTable.SymbolTable;

import javax.xml.transform.Result;

/*
 * This is the simple Parser class for the HavaBol programming language.
 * All errors and exceptions are thrown up to the calling method and output to stderr from there.
 */
public class Parser
{
    // public values to help keep track of our havabol objects
    public SymbolTable symbolTable;
    public StorageManager storageManager;
    public Scanner scan;

    /**
     * Parser constructor that takes in the symbolTable, storageManager, and Scanner
     * objects and saves them to our class.
     * <p>
     * These objects will be used for the rest of the class in order to parse our source file
     * and interpret the code.
     *
     * @param symbolTable object that contains symbol definitions for our programming language
     * @param storageManager object that contains all user specified variables and their values and types
     * @param scan object that contains our source file and will aid in parsing the file
     */
    public Parser(SymbolTable symbolTable, StorageManager storageManager, Scanner scan)
    {
        this.symbolTable = symbolTable;
        this.storageManager = storageManager;
        this.scan = scan;
    }

    /**
     * This method gets the next token from the source file line using Scanner::getNext().
     * Then it goes through a switch statement to determine what to do with the given token.
     * <p>
     * Returns resultValues returned to the method by the helper functions provided to it.
     *
     * @param bExec Tells the statement function whether we need to execute the code we find or
     *              just look at it
     * @return A generic ResultValue with all fields VOID if the bottom is hit, otherwise returns
     *         the calculated ResultValue
     * @throws Exception generic Exception type to handle any processing errors
     */
    public ResultValue statement(Boolean bExec) throws Exception
    {
        // advance token
        scan.getNext();

        // determine what to do with the current token
        switch (scan.currentToken.primClassif)
        {// CONTROL, OPERAND, FUNCTION, OPERATOR, SEPARATOR, EOF, DEBUG, or defaults to error
            case Token.CONTROL:
                switch (scan.currentToken.subClassif)
                { // control token found, so determine the sub type for proper execution
                  // DECLARE, FLOW, END, defaults to error
                    case Token.DECLARE:
                        return declareStmt(bExec);
                    case Token.FLOW:
                        if (scan.currentToken.tokenStr.equals("if"))
                            return ifStmt(bExec);
                        else if (scan.currentToken.tokenStr.equals("while"))
                            return whileStmt(bExec);
                        /*else if (scan.currentToken.tokenStr.equals("for"))
                        {
                            ResultValue res = forStmt(bExec);
                            res.type = Token.VOID;
                            return res;
                        }*/
                    case Token.END:
                        // end token so return
                        return new ResultValue("", Token.END
                                , ResultValue.primitive, scan.currentToken.tokenStr);
                    // should never hit this, otherwise MAJOR FUCK UP
                    default:
                        error("ERROR: UNIDENTIFIED CONTROL VARIABLE %s"
                                , scan.currentToken.tokenStr);
                }
                break;
            case Token.OPERAND:
                return assignStmt(bExec);
            case Token.FUNCTION:
                return function(bExec);
            case Token.OPERATOR:
            case Token.SEPARATOR:
            case Token.EOF:
                break;
            case Token.DEBUG:
                switch ( scan.getNext() )
                {// debug token found, determine which to change
                    case "Token":
                        scan.currentToken.primClassif = Token.DEBUG;

                        switch (scan.getNext())
                        {// are we turning on or off
                            case "on":
                                scan.currentToken.primClassif = Token.DEBUG;
                                if (!scan.getNext().equals(";"))
                                    error("MISSING ';'");
                                scan.bShowToken = true;
                                break;
                            case "off":
                                scan.currentToken.primClassif = Token.DEBUG;
                                if (!scan.getNext().equals(";"))
                                    error("MISSING ';'");
                                scan.bShowToken = false;
                                break;
                            default:
                                error("ERROR: HAS TO BE EITHER 'on' OR 'off' ");
                        }
                        break;
                    case "Expr":
                        scan.currentToken.primClassif = Token.DEBUG;

                        switch (scan.getNext())
                        {// are we turning on or off
                            case "on":
                                scan.currentToken.primClassif = Token.DEBUG;
                                if (!scan.getNext().equals(";"))
                                    error("MISSING ';'");
                                scan.bShowExpr = true;
                                break;
                            case "off":
                                scan.currentToken.primClassif = Token.DEBUG;
                                if (!scan.getNext().equals(";"))
                                    error("MISSING ';'");
                                scan.bShowExpr = false;
                                break;
                            default:
                                error("ERROR: HAS TO BE EITHER 'on' OR 'off' ");
                        }
                        break;
                    case "Assign":
                        scan.currentToken.primClassif = Token.DEBUG;

                        switch (scan.getNext())
                        {// are we turning on or off
                            case "on":
                                scan.currentToken.primClassif = Token.DEBUG;
                                if (!scan.getNext().equals(";"))
                                error("MISSING ';' ");
                                scan.bShowAssign = true;
                                break;
                            case "off":
                                scan.currentToken.primClassif = Token.DEBUG;
                                if (!scan.getNext().equals(";"))
                                    error("MISSING ';'");
                                scan.bShowAssign = false;
                                break;
                            default:
                                error("ERROR: HAS TO BE EITHER 'on' OR 'off' ");
                        }
                        break;
                    default:
                        error("ERROR: HAS TO BE EITHER 'Expr', 'Assign', OR 'Token' " +
                                "                       AND FOUND " + scan.currentToken.tokenStr);
                }
                break;
            // should never hit this, otherwise MAJOR FUCK UP
            default:
                error("INTERNAL ERROR CAUSED BY %s", scan.currentToken.tokenStr);
        }
        return new ResultValue("", Token.VOID, Token.VOID, scan.currentToken.tokenStr);
    }

    /**
     * This method is called when ever a Control Declare token is encountered.
     * It will determine the type and assign it to the next token's result value
     * <p>
     * This function will also allow havabol to support declaring and initializing on
     * the same line.
     *
     * @param bExec Tells the statement function whether we need to execute the code we find or
     *              just look at it
     * @return ResultValue object that contains the final result of execution
     * @throws Exception generic Exception type to handle any processing errors
     */
    public ResultValue declareStmt(Boolean bExec) throws Exception
    {
        int structure = -1;
        int dclType = -1;

        switch (scan.currentToken.tokenStr)
        {// check data type of the current token
            case "Int":
                dclType = Token.INTEGER;
                break;
            case "Float":
                dclType = Token.FLOAT;
                break;
            case "Bool":
                dclType = Token.BOOLEAN;
                break;
            case "String":
                dclType = Token.STRING;
                break;
            default:
                error("ERROR: INVALID DECLARE DATA TYPE %s", scan.currentToken.tokenStr);
        }

        // advance to the next token
        scan.getNext();

        // the next token should of been a variable name, otherwise error
        if(scan.currentToken.primClassif != Token.OPERAND)
            error("ERROR: %s SHOULD BE AN OPERAND FOR DECLARATION", scan.currentToken.tokenStr);

        if (bExec)
        {// we are executing, not ignoring
            // put declaration into symbol table and storage manager
            String variableStr = scan.currentToken.tokenStr;

            symbolTable.putSymbol(variableStr, new STIdentifier(variableStr
                                                , scan.currentToken.primClassif, dclType
                                                , 1, 1, 1));
            storageManager.putEntry(variableStr, new ResultValue(dclType, structure));
        }

        // if the next token is '=' call assignStmt to assign value to operand
        if(scan.nextToken.tokenStr.equals("="))
            return assignStmt(bExec);
        // else check if it is an operator, because that is an error
        else if (scan.nextToken.primClassif == Token.OPERATOR)
            error("ERROR: DECLARE CANNOT PERFORM %s OPERATION BEFORE INITIALIZATION"
                        , scan.nextToken.tokenStr);
        // else check for statement terminating ';'
        else if(! scan.getNext().equals(";"))
            error("ERROR: UNTERMINATED DECLARATION STATEMENT, ';' EXPECTED");

        return new ResultValue("", Token.VOID, ResultValue.primitive
                                                        , scan.currentToken.tokenStr);
    }

    /**
     * This method is called when ever an operand or a '=' is encountered.
     * It will determine the value and assign it to the specified operand
     * <p>
     * Also handles +=, -=, *=, /=
     *
     * @param bExec Tells the statement function whether we need to execute the code we find or
     *              just look at it
     * @return ResultValue object that contains the final result of execution
     * @throws Exception generic Exception type to handle any processing errors
     */
    public ResultValue assignStmt(Boolean bExec) throws Exception
    {
        // not used rn
        Numeric nOp2;  // numeric value of second operand
        Numeric nOp1;  // numeric value of first operand
        ResultValue res = null;
        ResultValue resExpr;

        // used
        String variableStr;
        int leftType = storageManager.getEntry(scan.currentToken.tokenStr).type;

        // make sure current token is an identifier to properly assign
        if (scan.currentToken.subClassif != Token.IDENTIFIER)
            error("ERROR: %s IS NOT A VALID TARGET VARIABLE FOR ASSIGNMENT"
                                                    , scan.currentToken.tokenStr);
        variableStr = scan.currentToken.tokenStr;

        // advance to the next token
        scan.getNext();

        // make sure current token is an operator
        if (scan.currentToken.primClassif != Token.OPERATOR)
            error("ERROR: ASSIGN EXPECTED AN OPERATOR BUT FOUND %s", scan.currentToken.tokenStr);

        // determine what kind of operation to execute
        switch (scan.currentToken.tokenStr)
        {
            case "=":
                if (bExec)
                {
                    ResultValue res1 = assign(variableStr, expr(), leftType);
                    return res1;
                }
                else
                    skipTo(scan.currentToken.tokenStr, ";");
                break;
            // see parsing part 2
            case "+=":
            case "-=":
            case "*=":
            case "/=":
            case "^=":
                break;
            default:
                error("ERROR: EXPECTED ASSIGNMENT OPERATOR BUT FOUND %s", scan.currentToken.tokenStr);
        }

        // if we ever hit this line, bExec is false
        return new ResultValue("", Token.VOID, ResultValue.primitive
                                                        , scan.currentToken.tokenStr);
    }

    /**
     * This method will assign the ResultValue object returned by expr() to the
     * variable string specified. The variable string will be the key.
     * <p>
     * Makes sure that the variable has been declared to a type already.
     *
     * @param variableStr contains the string of the variable we are assigning a value to
     * @param resExpr the ResultValue object which contains the value to assign
     * @param type Token type which we will be assigning
     * @return ResultValue object that contains the final result of execution
     * @throws Exception generic Exception type to handle any processing errors
     */
    private ResultValue assign(String variableStr, ResultValue resExpr, int type) throws Exception
    {
        // get entry from storage manager and make sure it is in the table
        ResultValue res = storageManager.getEntry(variableStr);
        if(res == null)
            error("ERROR: ASSIGN REQUIRES THAT '%s' BE DECLARED", variableStr);

        switch (type)
        {// determine the type of value to assign to variable
            case Token.INTEGER:
                 resExpr.value = Utilities.toInteger(this, resExpr);
                break;
            case Token.FLOAT:
                resExpr.value = Utilities.toFloat(this, resExpr);
                resExpr.type = Token.FLOAT;
                break;
            case Token.BOOLEAN:
                resExpr.value = Utilities.toBoolean(this, resExpr);
                break;
            case Token.STRING:
                break;
            default:
                error("ERROR: ASSIGN TYPE '%s' IS NOT A RECOGNIZED TYPE", variableStr);
        }

        // assign value to the variable and return result value
        storageManager.putEntry(variableStr, resExpr);

        // check for debug on
        if(scan.bShowAssign)
        {
            System.out.print("\t\t...");
            System.out.println("Variable Name: " + variableStr + " Value: " + resExpr.value);
        }

        return resExpr;
    }

    /**
     * This method will evaluate an expression and return a ResultValue object
     * that contains the final result.
     * <p>
     * Right now, this only handles single operation expressions
     *
     * @return ResultValue object that contains the final result of execution
     * @throws Exception generic Exception type to handle any processing errors
     */
    public ResultValue expr() throws Exception
    {
        //Result value for operands
        ResultValue firstResValue, secondResValue;

        //Result value for result
        ResultValue res = new ResultValue();

        // values for first and second operand
        int firstOperandSubClassif;
        String firstValue = "";
        int secondOperandSubClassif;
        String secondValue = "";

        // helper variables for operation
        Boolean isNegative = false;
        String operator;

        // advance token to the operand
        scan.getNext();

        // check if first operand is negative
        if (scan.currentToken.tokenStr.equals("-"))
        {
            isNegative = true;
            scan.getNext();
        }

        /* pablo you could probably use the same expression code but change it a little
         * to do complex expressions. I'd try to implement a while loop that goes until
         * base case "; , )" are encountered. Same way you determine if we have a single
         * value. Just seems like a good suggestion in order to not change your code that
         * much.
         */
        // save first operand attributes
        firstOperandSubClassif = scan.currentToken.subClassif;
        firstValue = scan.currentToken.tokenStr;

        // check if operand is an identifier or a constant
        if(firstOperandSubClassif == Token.IDENTIFIER)
            // if identifier get its result value
            firstResValue = storageManager.getEntry(firstValue);
        else// create a new result value object
            firstResValue = new ResultValue(scan.currentToken.tokenStr, scan.currentToken.subClassif);

        // check if first operand is supposed to be negative
        if(isNegative)
        {// supposed to be negative so change the sign
            firstResValue.value = Utilities.toNegative(this, firstResValue);
            isNegative = false;
        }

        // determine operator
        operator = scan.nextToken.tokenStr;

        if(operator.equals(";") || operator.equals(",") || operator.equals(")"))
        { // assign is done, so return the single value
            // check for debug on
            if(scan.bShowExpr)
            {
                System.out.print("\t\t...");
                System.out.println("Result Value: " + res.value);
            }

            return new ResultValue(firstResValue.value, firstResValue.type,1, operator);
        }

        // advance once to the operator and then once more to the token after operator
        scan.getNext();
        scan.getNext();

        // check if second operator is negative
        if (scan.currentToken.tokenStr.equals("-"))
        {
            isNegative = true;
            scan.getNext();
        }

        // save second operand attributes
        secondOperandSubClassif = scan.currentToken.subClassif;
        secondValue = scan.currentToken.tokenStr;

        // check if second operand is identifier or constant
        if(secondOperandSubClassif == Token.IDENTIFIER)
            // if identifier get its result value
            secondResValue = storageManager.getEntry(secondValue);
        else// create a new result value object
            secondResValue = new ResultValue(scan.currentToken.tokenStr, scan.currentToken.subClassif);

        // check if second operand is supposed to be negative
        if(isNegative)
        {// if there was a unary minus, apply to the second operand
            secondResValue.value = Utilities.toNegative(this, secondResValue);
            isNegative = false;
        }

        // determine operation to perform
        switch (operator)
        {
            case "+":
                res = Utilities.add(this, firstResValue, secondResValue);
                break;
            case "-":
                res = Utilities.sub(this, firstResValue, secondResValue);
                break;
            case "*":
                res = Utilities.mul(this, firstResValue, secondResValue);
                break;
            case "/":
                res = Utilities.div(this, firstResValue, secondResValue);
                break;
            case "^":
                res = Utilities.exp(this, firstResValue, secondResValue);
                break;
            case "<":
                res = Utilities.isLessThan(this, firstResValue, secondResValue);
                break;
            case ">":
                res = Utilities.isGreaterThan(this, firstResValue, secondResValue);
                break;
            case "<=":
                res = Utilities.isLessThanorEq(this, firstResValue, secondResValue);
                break;
            case ">=":
                res = Utilities.isGreaterThanorEq(this, firstResValue, secondResValue);
                break;
            case "==":
                res = Utilities.isEqual(this, firstResValue, secondResValue);
                break;
            case "!=":
                res = Utilities.notEqualTo(this, firstResValue, secondResValue);
                break;
            default:
                error("ERROR: '%s' IS NOT A VALID OPERATOR FOR EXPRESSION", operator);
        }

        // create final result value of expression to return
        res = new ResultValue(res.value, res.type,1, scan.nextToken.tokenStr);

        // check for debug on
        if(scan.bShowExpr)
        {
            System.out.print("\t\t...");
            System.out.println("Result Value: " + res.value);
        }

        return res;
    }

    /**
     * This method is provided in order to skip to a specified position
     * in the source file.
     * <p>
     * Handles EOF exception
     *
     * @param start start string we are starting from, included more for error purposes
     * @param end string literal we are looking for to terminate looping
     * @throws Exception generic Exception type to handle any processing errors
     */
    public void skipTo(String start, String end) throws Exception
    {
        // for error purposes
        int iColPos = scan.currentToken.iColPos;
        int iSourceLineNr = scan.currentToken.iSourceLineNr;

        // loop until we encounter EOF or the string literal we are looking for
        while (! scan.currentToken.tokenStr.equals(end)
                && scan.currentToken.primClassif != Token.EOF)
            scan.getNext();

        if (scan.currentToken.primClassif == Token.EOF)
            // the while loop exited on an EOF, not a matching token
            error("ERROR: LINE %d POSITION %d" + "\n\t%s DID NOT HAVE SEPARATOR, %s"
                    , iSourceLineNr, iColPos, start, end);

        // we found a match, so we will return with no error
        return;
    }

    /**
     * This method is provided to Parser::ifStmt, whileStmt, and forStmt.
     * <p>
     * It goes through
     * all lines between the start of the statement and the end of the statement specified
     * by the terminatingStr parameter.
     *
     * @param bExec Tells the statement function whether we need to execute the code we find or
     *              just look at it
     * @param terminatingStr Tells us when we are done executing lines for the statement
     * @return ResultValue object that contains the final result of execution
     * @throws Exception generic Exception type to handle any processing errors
     */
    public ResultValue statements(Boolean bExec, String terminatingStr) throws Exception
    {
        ResultValue result = statement(bExec);

        // loop until we find our terminating string
        while (! terminatingStr.contains(result.terminatingStr))
            result = statement(bExec);

        result.terminatingStr = scan.currentToken.tokenStr;
        return result;
    }

    /**
     * This method executes 'if' statements for HavaBol. It uses bExec and the
     * ResultValue object returned from calling expression in order to determine
     * if we are executing.
     * <p>
     * If we are not executing, we will still look over the lines to catch errors
     *
     * @param bExec Tells the statement function whether we need to execute the code we find or
     *              just look at it
     * @return ResultValue object that contains the final result of execution
     * @throws Exception generic Exception type to handle any processing errors
     */
    public ResultValue ifStmt(Boolean bExec) throws Exception
    {
        ResultValue resCond;

        // do we need to evaluate the condition
        if (bExec)
        {// we are executing, not ignoring
            // evaluate expression
            resCond = expr();

            // did the condition return true?
            if (resCond.value.equals("T"))
            {// condition returned true, execute statements on the true part
                resCond = statements(true, "endif else");

                // what ended the statements after the true part? else of endif
                if (resCond.terminatingStr.equals("else"))
                {// has an else
                    if (! scan.getNext().equals(":"))
                        error("ERROR: EXPECTED ':' AFTER ELSE");

                    resCond = statements(false, "endif");
                }
            }
            else
            {// condition returned false, ignore all statements after the if
                resCond = statements(false, "endif else");

                // check for else
                if (resCond.terminatingStr.equals("else"))
                { // if it is an 'else', execute
                    if (! scan.getNext().equals(":"))
                        error("ERROR: EXPECTED ':' AFTER ELSE");

                    resCond = statements(true, "endif");
                }
            }
        }
        else
        {// we are ignoring execution, so ignore conditional, true and false part
            // ignore conditional
            skipTo("if", ":");

            // ignore true part
            resCond = statements(false, "endif else");

            // if the statements terminated with an 'else', we need to parse statements
            if (resCond.terminatingStr.equals("else"))
            { // it is an else, so we need to skip statements
                if (! scan.getNext().equals(":"))
                    error("ERROR: EXPECTED ':' AFTER ELSE");

                // ignore false part
                resCond = statements(false, "endif");
            }
        }

        // did we have an 'endif;'?
        if (!resCond.terminatingStr.equals("endif") || !scan.nextToken.tokenStr.equals(";"))
            error("ERROR: EXPECTED 'endif;' FOR 'if' EXPRESSION");

        return new ResultValue("", Token.END, ResultValue.primitive, ";");
    }

    /**
     * This method executes 'while' statements for HavaBol. It uses bExec and the
     * ResultValue object returned from calling expression in order to determine
     * if we keep executing.
     * <p>
     * If we are not executing, we will still look over the lines to catch errors
     *
     * @param bExec Tells the statement function whether we need to execute the code we find or
     *              just look at it
     * @return ResultValue object that contains the final result of execution
     * @throws Exception generic Exception type to handle any processing errors
     */
    public ResultValue whileStmt(Boolean bExec) throws Exception
    {
        ResultValue resCond;

        // do we need to evaluate the condition
        if (bExec)
        {// we are executing, not ignoring
            Token whileToken = scan.currentToken;

            // evaluate expression
            resCond = expr();

            while (resCond.value.equals("T"))
            {// did the condition return true?
                resCond = statements(true, "endwhile");

                // did statements() end on an endwhile;?
                if (! resCond.terminatingStr.equals("endwhile") || !scan.nextToken.tokenStr.equals(";"))
                    error("ERROR: EXPECTED 'endwhile;' FOR 'while' EXPRESSION");

                // reset while loop token
                scan.setTo(whileToken);

                // check expression case
                resCond = expr();
            }

            // expr() returned false, so skip ahead to the end of the while
            resCond = statements(false, "endwhile");
        }
        else
        {// we are ignoring execution, so ignore conditional, true and false part
            // ignore conditional
            skipTo("while", ":");

            // ignore statements
            resCond = statements(false, "endwhile");
        }

        // did we have an endwhile;
        if (! resCond.terminatingStr.equals("endwhile") || !scan.nextToken.tokenStr.equals(";"))
            error("ERROR: EXPECTED 'endwhile;' FOR 'while' EXPRESSION");

        return new ResultValue("", Token.END, ResultValue.primitive, ";");
    }

    /**
     *
     * @param bExec
     * @return
     * @throws Exception
     */
    public ResultValue forStmt(Boolean bExec) throws Exception
    {
        ResultValue resCond;

        // do we need to evaluate the condition?
        if (bExec)
        {// we are executing, not ignoring
            Token forToken = scan.currentToken;

            // evaluate control variables


        }
        else
        {// we are ignoring execution, so control variables
            // ignore control variables
            skipTo("for", ":");

            // ignore statements
            resCond = statements(false, "endfor");
        }

        // did we have an endfor;
        if (! resCond.terminatingStr.equals("endfor") || !scan.nextToken.tokenStr.equals(";"))
            error("ERROR: EXPECTED 'endfor;' FOR 'while' EXPRESSION");

        return new ResultValue("", Token.END, ResultValue.primitive, "endfor");
    }

    /**
     * This method is provided to Parser to execute HavaBol builtin and user
     * defined functions.
     * <p>
     * function uses bExec in order to determine if we are executing or skipping.
     *
     * @param bExec Tells the statement function whether we need to execute the code we find or
     *              just look at it
     * @return ResultValue object that contains the final result of execution
     * @throws Exception generic Exception type to handle any processing errors
     */
    private ResultValue function(Boolean bExec) throws Exception
    {
        ResultValue res;

        switch (scan.currentToken.subClassif)
        {// determine if function is built in, or user defined
            case Token.BUILTIN:
                //do shit only for print
                if (scan.currentToken.tokenStr.equals("print"))
                {
                    String printLine = "";

                    // make sure the print function is in correct syntax
                    if (! scan.getNext().equals("(") )
                        error("ERROR: PRINT FUNCTION IS MISSING SEPARATOR '('");

                    Token previousToken = scan.currentToken;
                    while ( !scan.getNext().equals(")") )
                    {// loop until we find a ')'
                        if(scan.currentToken.subClassif <= Token.STRING && scan.currentToken.subClassif > 0)
                        {
                            scan.setTo(previousToken);
                            res = expr();
                            printLine += res.value;
                        }
                        else if (scan.currentToken.tokenStr.equals(","))
                            // ',' automatically add a space to our line
                            printLine += " ";
                        else if (scan.currentToken.primClassif == Token.OPERATOR)
                        {// operator encountered, evaluate and add to print string
                            scan.setTo(previousToken);
                            ResultValue resExpr = expr();
                            printLine += resExpr.value;
                        }
                        else if (scan.currentToken.tokenStr.equals(";"))
                            // should not be encountered unless a ')' is missing
                            error("ERROR: EXPECTED ')' BEFORE ';' TOKEN %s"
                                    , scan.currentToken.tokenStr);

                        previousToken = scan.currentToken;
                    }

                    // make sure we end on a ';'
                    if ( !scan.getNext().equals(";") )
                        error("ERROR: PRINT FUNCTION IS MISSING TERMINATOR ';'");

                    // check if we are executing
                    if (bExec)
                        System.out.println(printLine);
                }
                else// for right now, any other function gets skipped
                    skipTo(scan.currentToken.tokenStr,";");
                break;
            case Token.USER:
                // do other shit later
                skipTo(scan.currentToken.tokenStr, ";");
                break;
            default:// should never hit this, otherwise MAJOR FUCK UP
                error("INTERNAL ERROR: %s NOT A RECOGNIZED FUNCTION"
                           , scan.currentToken.tokenStr);
        }

        return new ResultValue("", Token.BUILTIN
                        , ResultValue.primitive, scan.currentToken.tokenStr);
    }

    /**
     * This method is provided to the parser to simplify all processing errors encountered.
     * <p>
     * This method will call ParserException class in order to throw an error with a formatted
     * message
     *
     * @param fmt string that contains the method and variable formats
     * @param varArgs arguments to put in the format string
     * @throws Exception generic Exception type to handle any processing errors
     */
    public void error (String fmt, Object... varArgs) throws Exception
    {
        throw new ParserException(scan.currentToken.iSourceLineNr+1
                                , String.format(fmt, varArgs)
                                , scan.sourceFileNm);
    }
}
