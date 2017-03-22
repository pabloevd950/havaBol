package havabol;

import havabol.SymbolTable.STIdentifier;
import havabol.SymbolTable.SymbolTable;

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

    public void statement(Boolean bExec) throws Exception
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
                                whileStmt();
                            break;
                        case Token.END:
                        default:
                            error("ERROR: UNIDENTIFIED CONTROL VARIABLE %s"
                                , scan.currentToken.tokenStr);
                    }
                    break;
                case Token.OPERAND:
                    assignStmt();
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
                error("Invalid Data type", scan.currentToken.tokenStr);
        }

        // advance to the next token
        scan.getNext();

        // the next token should of been a variable name, otherwise error
        if(scan.currentToken.primClassif != Token.OPERAND)
            error("This should be an operand", scan.currentToken.tokenStr);

        if (bExec)
        {// we are executing, not ignoring
            // put declaration into symbol table and storage manager
            String variableStr = scan.currentToken.tokenStr;

            symbolTable.putSymbol(variableStr, new STIdentifier(variableStr
                                                , scan.currentToken.primClassif, dclType
                                                , 1, 1, 1));

            storageManager.putEntry(variableStr, new ResultValue(dclType, structure));
        }


        //scan.getNext

        //If the next token is '=' call assignStmt to assign value to operand
        if(scan.nextToken.tokenStr.equals("="))
        {
            ResultValue res = assignStmt();
        }
        //Else check if statement is finished
        else if(scan.nextToken.tokenStr.equals(";"))
            scan.getNext();

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




    public ResultValue expr() throws Exception
    {
        scan.getNext(); // get the operand



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

    public ResultValue assignStmt() throws Exception
    {
        System.out.println("Assignment statement starts here for line " + scan.currentToken.tokenStr);
        int dclType = scan.currentToken.primClassif;

        StorageEntry entry;

        ResultValue res = null;
        String variableStr ;
        String operatorStr;
        ResultValue resO2;
        ResultValue resO1;
        Numeric nOp2;  // numeric value of second operand
        Numeric nOp1;  // numeric value of first operand


        if (scan.currentToken.subClassif != Token.IDENTIFIER)
            error("expected a variable for the target of an assignment");

        variableStr  = scan.currentToken.tokenStr;
        System.out.println(variableStr);
        scan.getNext();

        if(scan.currentToken.primClassif != Token.OPERATOR)
            error("Expected an operand");
        operatorStr = scan.currentToken.tokenStr;
        System.out.println(operatorStr);


        if(operatorStr.equals("=")){
            resO2 = expression();
            res = assign(variableStr, resO2);  // assign to target
        }
        else{
            error("expected assignment operator");

        }

        scan.getNext();



        return res;
    }

    private ResultValue assign(String variableStr, ResultValue resO2) throws Exception
    {
        //Get entry from storage manager
        ResultValue res = storageManager.getEntry(variableStr);
        //Make sure entry was in table
        if(res == null)
            error("Not declared yet");

        res = resO2;
        //Replace old value in the same key
        storageManager.putEntry(variableStr,res);
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

        // do we need to evaluate the condition
        if (bExec)
        {// we are executing, not ignoring
            ResultValue resCond = expr();

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

                // did we have an endif
                if (! resCond.terminatingStr.equals("endif"))
                    error("ERROR: EXPECTED 'endif' FOR 'if' EXPRESSION");
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

                // did we have an endif;
                if (!resCond.terminatingStr.equals("endif") || !scan.getNext().equals(";"))
                    error("ERROR: EXPECTED 'endif' FOR 'if' EXPRESSION");
            }
        }
        else
        {// we are ignoring execution, so ignore conditional, true and false part
            // ignore conditional
            skipTo("if", ":");

            // ignore true part
            ResultValue resCond = statements(false);

            // if the statements terminated with an 'else', we need to parse statements
            if (resCond.terminatingStr.equals("else"))
            { // it is an else, so we need to skip statements
                if (! scan.getNext().equals(":"))
                    error("ERROR: EXPECTED ':' AFTER ELSE");

                // ignore false part
                resCond = statements(false);
            }

            // did we have an endif;
            if (!resCond.terminatingStr.equals("endif") || !scan.getNext().equals(";"))
                error("ERROR: EXPECTED 'endif' FOR 'if' EXPRESSION");


        }

        return null;
    }

    public ResultValue whileStmt()
    {
        System.out.println("While statement here");
        return null;
    }



    public ResultValue expression(){

        return null;
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
