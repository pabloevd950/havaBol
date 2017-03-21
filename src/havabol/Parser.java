package havabol;

import havabol.SymbolTable.STIdentifier;
import havabol.SymbolTable.SymbolTable;
import havabol.StorageManager;

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

    public void statement() throws Exception
    {
        // Print a column heading
        System.out.printf("%-11s %-12s %s\n"
                , "primClassif"
                , "subClassif"
                , "tokenStr");

        while (! scan.getNext().isEmpty())
        {
            scan.currentToken.printToken();
            switch (scan.currentToken.subClassif)
            {

                case Token.FLOW:
                    if (scan.currentToken.tokenStr.equals("if"))
                        ifStmt();
                    else
                        whileStmt();
                case Token.IDENTIFIER:
                    assignStmt();
                    break;
                case Token.DECLARE:
                    declareStmt();
                    break;
                case Token.BUILTIN:
                    function();
                    break;
                case Token.SEPARATOR:
                    //scan.getNext();
                    //break;

                default:
                    System.out.println("Need to handle this");
                    System.out.println(scan.currentToken.tokenStr + " " + scan.nextToken.tokenStr);

                    //scan.currentToken.printToken();
//                    throw new Exception();

            }
        }
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

        ResultValue res = storageManager.getEntry(variableStr);
        if(res == null){
            error("Not declared yet");
        }
        res = resO2;
        storageManager.putEntry(variableStr,res);
        return res;
    }

    public ResultValue ifStmt()
    {

        System.out.println("If statement here");
        return null;
    }

    public ResultValue whileStmt()
    {
        System.out.println("While statement here");
        return null;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public ResultValue declareStmt() throws Exception
    {
        System.out.println("Declare statement here with " + scan.currentToken.tokenStr );
        //Initialize variables for symbol table and storage manager
        String variableStr;
        String type = scan.currentToken.tokenStr;
        int structure = 1;
        int dclType = 1;
        int parm;
        int nonLocal;

        //Check data type.
        switch (type)
        {
            case "Int":
                dclType = Token.INTEGER;
                break;
            case "Float":
                dclType = Token.FLOAT;
                break;

            case "Booelan":
                dclType = Token.BOOLEAN;
                break;

            case "String":
                dclType = Token.STRING;
                break;
            default:
                System.out.print(type);
                error("Invalid Data type", type);
        }
        //Name if declared variable
        scan.getNext();

        //Check if name is an operand
        if(scan.currentToken.primClassif != 1)
        {
            error("This should be an operand", scan.currentToken.tokenStr);
        }
        variableStr = scan.currentToken.tokenStr;

        //Put in symbol table and storage manager
        STIdentifier symbolEntry = new STIdentifier(variableStr, scan.currentToken.primClassif , dclType,
                1, 1, 1);
        symbolTable.putSymbol(variableStr, symbolEntry);
        ResultValue variableEntry =  new ResultValue(dclType, structure);
        storageManager.putEntry(variableStr,variableEntry);
        //scan.getNext();

        if(scan.nextToken.tokenStr.equals("="))
        {
            ResultValue res = assignStmt();

        }
        //Check for finished declaration and enter into symboltable
        else if(scan.nextToken.tokenStr.equals(";"))
        {
            scan.getNext();
        }

        return null;
    }

    public ResultValue expression(){

        return null;
    }


    public void error (String fmt, Object... varArgs) throws Exception
    {
        throw new ParserException(scan.currentToken.iSourceLineNr
                                , String.format(fmt, varArgs)
                                , scan.sourceFileNm);
    }
}
