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

    private ArrayList<Boolean> bControl;
    private ArrayList<String> szControl;
    private int i;

    public Parser (SymbolTable symbolTable, StorageManager storageManager, Scanner scan)
    {
        this.symbolTable = symbolTable;
        this.storageManager = storageManager;
        this.scan = scan;
        this.bControl = new ArrayList<Boolean>();
        this.szControl = new ArrayList<String>();
        this.i = -1;
    }

    public ResultValue statement(Boolean bExec) throws Exception
    {
        while (! scan.getNext().isEmpty())
        {
            scan.currentToken.printToken();

            switch (scan.currentToken.primClassif)
            {
                case Token.CONTROL:
                    switch (scan.currentToken.subClassif)
                    {
                        case Token.DECLARE:
                            declareStmt(bExec);
                            break;
                        case Token.FLOW:
                            if (scan.currentToken.tokenStr.equals("if"))
                                ifStmt(bExec);
                            else if (scan.currentToken.tokenStr.equals("while"))
                                whileStmt(bExec);
                            break;
                        case Token.END:
                            // was an end token expected?
                            if (i >= 0 && !bControl.get(i))
                                error("ERROR: UNEXPECTED CONTROL END TOKEN %s"
                                         , scan.currentToken.tokenStr);
                            // end token was expected so return
                            return new ResultValue(szControl.get(i), Token.END
                                    , ResultValue.primitive, scan.currentToken.tokenStr);
                        // should never hit this, otherwise MAJOR FUCK UP
                        default:
                            error("ERROR: UNIDENTIFIED CONTROL VARIABLE %s"
                                , scan.currentToken.tokenStr);
                    }
                    break;
                case Token.OPERAND:
                    assignStmt(bExec);
                    break;
                case Token.FUNCTION:
                    function();
                    break;
                case Token.OPERATOR:
                case Token.SEPARATOR:
                    break;
                default:
                    System.out.println("*************Need to handle this************");
                    System.out.println(scan.currentToken.tokenStr + " " + scan.nextToken.tokenStr);
            }
        }

        return null;
    }

    /**
     *
     * @return
     * @throws Exception
     * @param bExec
     */
    public ResultValue declareStmt(Boolean bExec) throws Exception
    {
        System.out.println("Declare statement here with " + scan.currentToken.tokenStr );
        
        int structure = -1;
        int dclType = -1;
        ResultValue resultValue;

        switch (scan.currentToken.tokenStr)
        {// check data type of the current token
            case "Int":
                dclType = Token.INTEGER;
                break;
            case "Float":
                dclType = Token.FLOAT;
                break;
            case "Boolean":
                dclType = Token.BOOLEAN;
                break;
            case "String":
                dclType = Token.STRING;
                break;
            default:
                System.out.print(scan.currentToken.tokenStr);
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
            assignStmt(bExec);
        // else check if it is an operator, because that is an error
        else if (scan.nextToken.primClassif == Token.OPERATOR)
            error("ERROR: CANNOT PERFORM %s OPERATION BEFORE INITIALIZATION"
                        , scan.nextToken.tokenStr);
        // else check for statement terminating ';'
        else if(! scan.getNext().equals(";"))
            error("ERROR: UNTERMINATED DECLARATION STATEMENT, ';' EXPECTED");

        return null;
    }

    public ResultValue assignStmt(Boolean bExec) throws Exception
    {
        System.out.println("Assignment statement starts here for line " + scan.currentToken.tokenStr);

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
                if (bExec)
                    res = assign(variableStr, expr());
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

        return res;
    }

    private ResultValue assign(String variableStr, ResultValue resExpr) throws Exception
    {
        // get entry from storage manager and make sure it is in the table
        ResultValue res = storageManager.getEntry(variableStr);
        if(res == null)
            error("ERROR: %s HAS NOT BEEN DECLARED YET", variableStr);

        // assign value to the variable and return result value
        storageManager.putEntry(variableStr,resExpr);
        return resExpr;
    }

    public ResultValue expr() throws Exception
    {
        int firstOperandSubClassif;
        int secondOperandSubClassif;
        int firstOperandType;
        int secondOperandType;
        String firstValue ="";
        String secondValue ="";
        int currentType= -1;
        String currentValue = null;
        Boolean isNegative = false;
        double finalValue;
        int finalValueInt;

        scan.getNext(); // get the operand
        if (scan.currentToken.tokenStr.equals("-"))
        {
            isNegative = true;
            scan.getNext();
        }
        firstOperandSubClassif = scan.currentToken.subClassif;
        firstValue = scan.currentToken.tokenStr;

        if(firstOperandSubClassif == Token.IDENTIFIER)
        {
            firstOperandType = storageManager.getEntry(firstValue).type;
            firstValue = storageManager.getEntry(firstValue).value;
        }
        else
        {
            firstValue = scan.currentToken.tokenStr;
            firstOperandType = scan.currentToken.subClassif;
        }
        if(isNegative == true)
        {
            switch (firstOperandType)
            {
                case Token.INTEGER:
                    int x = Integer.parseInt(firstValue);
                    x*= -1;
                    firstValue = String.valueOf(x);
                    break;
                case Token.FLOAT:
                    double y = Double.parseDouble(firstValue);
                    y = y * -1;
                    firstValue = String.valueOf(y);
                case Token.STRING:
                    //Check if its a float or int represented as string?
                case Token.BOOLEAN:
            }
            isNegative = false;
        }

        String operator = scan.getNext();
        if(operator.equals(";")){
            currentValue = firstValue;
            currentType = scan.currentToken.subClassif;
            ResultValue res = new ResultValue(currentValue, currentType,1,";");
            return res;
        }

        scan.getNext();
        if (scan.currentToken.tokenStr.equals("-"))
        {
            isNegative = true;
            scan.getNext();
        }

        secondOperandSubClassif = scan.currentToken.subClassif;
        secondValue = scan.currentToken.tokenStr;

        if(secondOperandSubClassif == Token.IDENTIFIER){
            secondOperandType = storageManager.getEntry(secondValue).type;
            secondValue = storageManager.getEntry(secondValue).value;
        }
        else
        {
            secondValue = scan.currentToken.tokenStr;
            secondOperandType = scan.currentToken.subClassif;
        }


        switch (operator){
            case "+":
                if(firstOperandType == Token.INTEGER){
                    finalValueInt = Utilities.addInt(Integer.parseInt(firstValue), Integer.parseInt(secondValue));
                    currentValue = Integer.toString(finalValueInt);
                    currentType = firstOperandType;
                }
                else if(firstOperandType ==  Token.FLOAT){
                    finalValue = Utilities.addFloat(Double.parseDouble(firstValue), Float.parseFloat(secondValue));
                    currentValue = Double.toString(finalValue);
                    currentType = firstOperandType;
                }
                break;

            case "-":
                if(firstOperandType == Token.INTEGER){
                    finalValueInt = Utilities.subInt(Integer.parseInt(firstValue), Integer.parseInt(secondValue));
                    currentValue = Integer.toString(finalValueInt);
                    currentType = firstOperandType;
                }
                else if(firstOperandType ==  Token.FLOAT){
                    finalValue = Utilities.subFloat(Double.parseDouble(firstValue), Double.parseDouble(secondValue));
                    currentValue = Double.toString(finalValue);
                    currentType = firstOperandType;
                }
                break;

            case "*":

            case "/":

            case "^":
                if(firstOperandType == Token.INTEGER){
                    finalValue = Utilities.expInt(Integer.parseInt(firstValue), Integer.parseInt(secondValue));
                    currentValue = Double.toString(finalValue);
                    currentType = firstOperandType;
                }
                else if(firstOperandType ==  Token.FLOAT){
                    finalValue = Utilities.expDouble(Double.parseDouble(firstValue), Double.parseDouble(secondValue));
                    currentValue = Double.toString(finalValue);
                    currentType = firstOperandType;
                }
                break;

        }

        ResultValue res = new ResultValue(currentValue, currentType,1,";");
        return res;
    }


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

    public ResultValue ifStmt(Boolean bExec) throws Exception
    {
        System.out.println("If statement here");

        ResultValue resCond;

        // add to arraylists
        this.bControl.add(true);
        this.szControl.add("if");
        this.i++;

        // do we need to evaluate the condition
        if (bExec)
        {// we are executing, not ignoring
            // advance token and evaluate expression
            scan.getNext();
            resCond = expr();

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

        // did we have an endif; *this was after all the else checks
        if (!resCond.terminatingStr.equals("endif") || !scan.getNext().equals(";"))
            error("ERROR: EXPECTED 'endif;' FOR 'if' EXPRESSION");

        // remove from array list
        this.bControl.remove(i);
        this.szControl.remove(i);
        this.i--;

        return null;
    }

    public ResultValue statements(Boolean bExec) throws Exception
    {
        ResultValue result = new ResultValue();
        while (scan.currentToken.primClassif != Token.END)
            statement(bExec);

        result.terminatingStr = scan.currentToken.tokenStr;
        return result;
    }

    public ResultValue whileStmt(Boolean bExec) throws Exception
    {
        System.out.println("While statement here");

        ResultValue resCond;

        // add to arraylists
        this.bControl.add(true);
        this.szControl.add("while");
        this.i++;

        // do we need to evaluate the condition
        if (bExec)
        {// we are executing, not ignoring
            // save location and advance token before evaluating condition
            int iSourceLineNr = scan.currentToken.iSourceLineNr;
            int iColPos = scan.currentToken.iColPos;
            Token nextToken = scan.nextToken;
            scan.getNext();

            resCond = expr();
            while (resCond.value.equals("T"))
            {// did the condition return true?
                resCond = statements(true);

                // did statements() end on an endwhile;?
                if (! resCond.terminatingStr.equals("endwhile") || ! scan.getNext().equals(";"))
                    error("ERROR: EXPECTED 'endwhile;' FOR 'while' EXPRESSION");

                // reset scanner cursor and check while condition again
                scan.iSourceLineNr = iSourceLineNr;
                scan.iColPos = iColPos;
                scan.nextToken = nextToken;
                scan.getNext();
                resCond = expr();
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
        }

        // did we have an endwhile;
        if (! resCond.terminatingStr.equals("endwhile") || ! scan.getNext().equals(";"))
            error("ERROR: EXPECTED 'endwhile;' FOR 'while' EXPRESSION");

        // remove from array list
        this.bControl.remove(i);
        this.szControl.remove(i);
        this.i--;

        return null;
    }


    private void function() throws Exception
    {
        if(scan.currentToken.tokenStr.equals("print")){
            if(scan.getNext().equals("("))
            {
                String printingLine = scan.getNext();
                if((scan.getNext().equals(")"))&& scan.getNext().equals(";")){
                    System.out.println(printingLine);
                }
            }

        }


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
