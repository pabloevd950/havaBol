package havabol;

import havabol.SymbolTable.STIdentifier;
import havabol.SymbolTable.SymbolTable;

import javax.xml.transform.Result;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by tkb442 on 3/3/17.
 */
public class Parser
{

    public SymbolTable symbolTable;
    public StorageManager storageManager;
    public Scanner scan;

    public Parser (SymbolTable symbolTable, StorageManager storageManager, Scanner scan)
    {
        this.symbolTable = symbolTable;
        this.storageManager = storageManager;
        this.scan = scan;
    }

    public ResultValue statement(Boolean bExec) throws Exception
    {
        //System.out.println("STATEMENT CALLED WITH BEXEC " + bExec);
        // advance token
        scan.getNext();
        //scan.currentToken.printToken();

        switch (scan.currentToken.primClassif)
        {
            case Token.CONTROL:
                switch (scan.currentToken.subClassif)
                {
                    case Token.DECLARE:
                        return declareStmt(bExec);
                    case Token.FLOW:
                        if (scan.currentToken.tokenStr.equals("if"))
                            return ifStmt(bExec);
                        else if (scan.currentToken.tokenStr.equals("while"))
                            return whileStmt(bExec);
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
            // should never hit this, otherwise MAJOR FUCK UP
            default:
                error("INTERNAL ERROR CAUSED BY %s", scan.currentToken.tokenStr);
            }

        return new ResultValue("", Token.VOID, Token.VOID, "");
    }

    /**
     *
     * @return
     * @throws Exception
     * @param bExec
     */
    public ResultValue declareStmt(Boolean bExec) throws Exception
    {
        //System.out.println("Declare statement here with " + scan.currentToken.tokenStr );
        //System.out.println("BEXEC IS " + bExec);
        
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
                error("ERROR: INVALID DATA TYPE %s", scan.currentToken.tokenStr);
        }

        // advance to the next token
        scan.getNext();

        // the next token should of been a variable name, otherwise error
        if(scan.currentToken.primClassif != Token.OPERAND)
            error("ERROR: %s SHOULD BE AN OPERAND", scan.currentToken.tokenStr);

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
            error("ERROR: CANNOT PERFORM %s OPERATION BEFORE INITIALIZATION"
                        , scan.nextToken.tokenStr);
        // else check for statement terminating ';'
        else if(! scan.getNext().equals(";"))
            error("ERROR: UNTERMINATED DECLARATION STATEMENT, ';' EXPECTED");

        return new ResultValue("", Token.VOID, ResultValue.primitive
                                                        , scan.currentToken.tokenStr);
    }

    /**
     *
     * @param bExec
     * @return
     * @throws Exception
     */
    public ResultValue assignStmt(Boolean bExec) throws Exception
    {
        //System.out.println("Assignment statement starts here for line " + scan.currentToken.tokenStr);
        //System.out.println("BEXEC IS " +bExec);

        Numeric nOp2;  // numeric value of second operand
        Numeric nOp1;  // numeric value of first operand
        ResultValue res = null;
        ResultValue resExpr;
        String variableStr;

        // make sure current token is an identifier to properly assign
        if (scan.currentToken.subClassif != Token.IDENTIFIER)
            error("ERROR: %s IS NOT A VALID TARGET VARIABLE FOR ASSIGNMENT"
                                                    , scan.currentToken.tokenStr);
        variableStr = scan.currentToken.tokenStr;

        // advance to the next token
        scan.getNext();

        // make sure current token is an operator
        if (scan.currentToken.primClassif != Token.OPERATOR)
            error("ERROR: EXPECTED AN OPERATOR BUT FOUND %s", scan.currentToken.tokenStr);

        // determine what kind of operation to execute
        switch (scan.currentToken.tokenStr)
        {
            case "=":
                if (bExec){
                    ResultValue res1 = assign(variableStr, expr());
                    //System.out.println("Variable name " + variableStr +  " Value is " + res1.value + " Type is " + res1.type);
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
                error("expected assignment operator");
        }

        // if we ever hit this line, bExec is false
        return new ResultValue("", Token.VOID, ResultValue.primitive
                                                        , scan.currentToken.tokenStr);
    }

    /**
     *
     * @param variableStr
     * @param resExpr
     * @return
     * @throws Exception
     */
    private ResultValue assign(String variableStr, ResultValue resExpr) throws Exception
    {
        // get entry from storage manager and make sure it is in the table
        ResultValue res = storageManager.getEntry(variableStr);
        if(res == null)
            error("ERROR: %s HAS NOT BEEN DECLARED YET", variableStr);

        // assign value to the variable and return result value
        storageManager.putEntry(variableStr, resExpr);
        return resExpr;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public ResultValue expr() throws Exception
    {
        //System.out.println("EXPR STATEMENT CALLED");
        //Result value for operands
        ResultValue firstResValue, secondResValue;
        //Result value for result
        ResultValue res = new ResultValue();

        int firstOperandSubClassif;
        int secondOperandSubClassif;
        String terminatingStr;
        String firstValue ="";
        String secondValue ="";
        Boolean isNegative = false;


        scan.getNext(); // get the operand
        //Check if the first operand is negative
        if (scan.currentToken.tokenStr.equals("-"))
        {
            isNegative = true;
            scan.getNext();
        }
        //Check if it is an identifier or a constant
        //If identifier get its result value
        firstOperandSubClassif = scan.currentToken.subClassif;
        firstValue = scan.currentToken.tokenStr;
        if(firstOperandSubClassif == Token.IDENTIFIER)
        {
            firstResValue = storageManager.getEntry(firstValue);
        }
        else
        {
            firstResValue = new ResultValue(scan.currentToken.tokenStr, scan.currentToken.subClassif);
        }
        //If it was negative make it negative
        if(isNegative == true)
        {
            firstResValue.value = Utilities.toNegative(this, firstResValue);
            isNegative = false;
        }
        //Get operator. If it grabs a ';' return the single value
        String operator = scan.getNext();
        if(operator.equals(";")){
             res = new ResultValue(firstResValue.value, firstResValue.type,1,";");
            return res;
        }
        //If not check if the next one is negative
        scan.getNext();
        if (scan.currentToken.tokenStr.equals("-"))
        {
            isNegative = true;
            scan.getNext();
        }
        //Check if the second operand is identifier or constant
        secondOperandSubClassif = scan.currentToken.subClassif;
        secondValue = scan.currentToken.tokenStr;
        if(secondOperandSubClassif == Token.IDENTIFIER)
        {
            secondResValue = storageManager.getEntry(secondValue);
        }
        else
        {
            secondResValue = new ResultValue(scan.currentToken.tokenStr, scan.currentToken.subClassif);
        }
        //If there was a unary minus apply it to the second operand
        if(isNegative == true)
        {
            secondResValue.value = Utilities.toNegative(this, secondResValue);
            isNegative = false;
        }

        //Perform operarion based on operator
        switch (operator){
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
                res = Utilities.notEqalTo(this, firstResValue, secondResValue);
                break;
            default:
                error("ERROR: '%s' IS NOT A VALID OPERATOR"
                        , operator);

        }
        //Get terminating string
        //terminatingStr = scan.nextToken.tokenStr;
        terminatingStr = scan.getNext();

        //Final result value returned for a double operand operation
        res = new ResultValue(res.value, res.type,1,terminatingStr);
        //System.out.println("Res type " + res.type + " Current type " + res.type + " Value " + res.value + " termStr is " + terminatingStr);
        return res;
    }

    /**
     *
     * @param start
     * @param end
     * @throws Exception
     */
    public void skipTo(String start, String end) throws Exception
    {
        // for error purposes
        int iColPos = scan.currentToken.iColPos;
        int iSourceLineNr = scan.currentToken.iSourceLineNr;

        while (! scan.currentToken.tokenStr.equals(end)
                && scan.currentToken.primClassif != Token.EOF)
            scan.getNext();

        if (scan.currentToken.primClassif == Token.EOF)
        {// the while loop exited on an EOF, not a matching token
            error("ERROR: LINE %d POSITION %d" +
                            "\n\t%s DID NOT HAVE SEPARATOR, %s"
                    , iSourceLineNr, iColPos, start, end);
        }

        // we found a match, so we will return with no error
        return;
    }

    /**
     *
     * @param bExec
     * @return
     * @throws Exception
     */
    public ResultValue ifStmt(Boolean bExec) throws Exception
    {
        //System.out.println("If statement here with BEXEC + " + bExec);

        ResultValue resCond;

        // do we need to evaluate the condition
        if (bExec)
        {// we are executing, not ignoring
            // advance token and evaluate expression
            //scan.getNext();
            resCond = expr();

            //System.out.println(">>expr was " + resCond.value);
            // did the condition return true?
            if (resCond.value.equals("T"))
            {// condition returned true, execute statements on the true part
                resCond = statements(true);

                // what ended the statements after the true part? else of endif
                if (resCond.terminatingStr.equals("else"))
                {// has an else
                    if (! scan.getNext().equals(":"))
                        error("ERROR: EXPECTED ':' AFTER ELSE");
                    resCond = statements(false);
                }
            }
            else
            {// condition returned false, ignore all statements after the if
                resCond = statements(false);

                // check for else
                if (resCond.terminatingStr.equals("else"))
                { // if it is an 'else', execute
                    if (! scan.getNext().equals(":"))
                        error("ERROR: EXPECTED ':' AFTER ELSE");
                    resCond = statements(true);
                }
            }
        }
        else
        {// we are ignoring execution, so ignore conditional, true and false part
            // ignore conditional
            skipTo("if", ":");

            // ignore true part
            resCond = statements(false);

            // if the statements terminated with an 'else', we need to parse statements
            if (resCond.terminatingStr.equals("else"))
            { // it is an else, so we need to skip statements
                if (! scan.getNext().equals(":"))
                    error("ERROR: EXPECTED ':' AFTER ELSE");

                // ignore false part
                resCond = statements(false);
            }
        }

        //scan.currentToken.printToken();

        // did we have an endif; *this was after all the else checks
        if (!resCond.terminatingStr.equals("endif") || !scan.nextToken.tokenStr.equals(";"))
            error("ERROR: EXPECTED 'endif;' FOR 'if' EXPRESSION");

        return new ResultValue("", Token.END, ResultValue.primitive, "endif");
    }

    /**
     *
     * @param bExec
     * @return
     * @throws Exception
     */
    public ResultValue statements(Boolean bExec) throws Exception
    {
        //System.out.println("STATEMENTS CALLED WITH BEXEC " + bExec);
        //System.out.println(scan.currentToken.tokenStr);
        ResultValue result = statement(bExec);
        //while (scan.currentToken.primClassif != Token.END)
        while (result.type != Token.END)
            result = statement(bExec);

        result.terminatingStr = scan.currentToken.tokenStr;
        return result;
    }

    /**
     *
     * @param bExec
     * @return
     * @throws Exception
     */
    public ResultValue whileStmt(Boolean bExec) throws Exception
    {
        //System.out.println("While statement here");

        ResultValue resCond;

        // do we need to evaluate the condition
        if (bExec)
        {// we are executing, not ignoring
            // save location and advance token before evaluating condition
            int iSourceLineNr = scan.currentToken.iSourceLineNr;
            int iColPos = scan.currentToken.iColPos;
            Token nextToken = scan.nextToken;
            //scan.getNext();

            resCond = expr();
            while (resCond.value.equals("T"))
            {// did the condition return true?
                System.out.println("While loop started");
                resCond = statements(true);

                // did we execute an if statement?
                while (resCond.terminatingStr.equals("endif") || resCond.terminatingStr.equals("else"))
                    resCond = statements(true);


                // did statements() end on an endwhile;?
                if (! resCond.terminatingStr.equals("endwhile")
                   || !scan.nextToken.tokenStr.equals(";"))
                    error("ERROR: EXPECTED 'endwhile;' FOR 'while' EXPRESSION");

                System.out.println("while loop passed");

                // reset scanner cursor and check while condition again
                scan.iSourceLineNr = iSourceLineNr;
                scan.iColPos = iColPos;
                scan.nextToken = nextToken;

                scan.currentToken.printToken();
                scan.nextToken.printToken();

                resCond = expr();

                System.out.println(resCond.value);
            }

            // expr() returned false, so skip ahead to the end of the while
            resCond = statements(false);
        }
        else
        {// we are ignoring execution, so ignore conditional, true and false part
            // ignore conditional
            skipTo("while", ":");

            // ignore statements
            resCond = statements(false);

            while (resCond.terminatingStr.equals("endif") || resCond.terminatingStr.equals("else"))
                resCond = statements(false);
        }

        // did we have an endwhile;
        if (! resCond.terminatingStr.equals("endwhile") || !scan.nextToken.tokenStr.equals(";"))
            error("ERROR: EXPECTED 'endwhile;' FOR 'while' EXPRESSION");

        return new ResultValue("", Token.END, ResultValue.primitive, "endwhile");
    }

    /**
     *
     * @param bExec
     * @return
     * @throws Exception
     */
    private ResultValue function(Boolean bExec) throws Exception
    {
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

                    while ( !scan.getNext().equals(")"))
                    {
                        switch (scan.currentToken.subClassif)
                        {
                            case Token.STRING:
                            case Token.INTEGER:
                            case Token.FLOAT:
                            case Token.BOOLEAN:
                            case Token.DATE:
                                printLine += scan.currentToken.tokenStr;
                                break;
                            case Token.IDENTIFIER:
                                printLine +=
                                        storageManager.getEntry(scan.currentToken.tokenStr).value;
                            default:
                                if (scan.currentToken.tokenStr.equals(","))
                                    printLine += " ";
                                else if (scan.currentToken.tokenStr.equals(";"))
                                    error("ERROR: EXPECTED ')' BEFORE ';' TOKEN");
                        }
                    }
                    if (bExec)
                        System.out.println(printLine);
                    if ( !scan.getNext().equals(";") )
                        error("ERROR: PRINT FUNCTION IS MISSING TERMINATOR ';'");
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


    public void infixExpr()
    {
        ArrayList<Token> out = new ArrayList<Token>();
        Stack<ResultValue> stack = new Stack<ResultValue>();

        /*for token from left to right
        {
            switch (Token.primClassif)
        }*/





    }






    public void error (String fmt, Object... varArgs) throws Exception
    {
        throw new ParserException(scan.currentToken.iSourceLineNr
                                , String.format(fmt, varArgs)
                                , scan.sourceFileNm);
    }
}
