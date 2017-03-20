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
        //Storage Entry object
        StorageEntry entry;
        String name = scan.currentToken.tokenStr;
        int dclType = scan.currentToken.primClassif;
        Object value = null;

        //If the next token is an '=', the one after that should be the value. Not checking for expressions yet
        if(scan.getNext().equals("="))
        {
            value = scan.getNext();
        }
        //Assuming the previous token was a single value and not expression, the next token should be a ';' terminator
         if(scan.getNext().equals(";"))
         {

             entry = storageManager.getEntry(name);
             if(entry != null)
             {
                 switch(entry.entryType){
                     case Token.INTEGER:
                         entry.intValue = (int) value;
                     case Token.FLOAT:
                         entry.floatValue = (float) value;
                     case Token.BOOLEAN:
                         entry.boolValue = (boolean) value;
                     case Token.STRING:
                         entry.strValue = (String) value;

                 }

             }


             //Get declaration in symbol table
             //Put in storage manager
             //scan.getNext();
         }



            return null;
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
        String name;
        Object val =  null;
        String type = scan.currentToken.tokenStr;
        int structure = 1;
        int dclType = 1;
        int parm;
        int nonLocal;
        //Check data type. Refer to integer values from symbol table
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
        name = scan.getNext();
        //Check if name is an operand
        if(scan.currentToken.primClassif != 1)
        {
            error("This should be an operand", name);
        }
        //Check if variable is being initialized
        if(scan.nextToken.tokenStr.equals("="))
        {
            assignStmt();
            //We should call assignment here
            //val = scan.getNext();
        }
        //Check for finished declaration and enter into symboltablez
        else if(scan.nextToken.tokenStr.equals(";"))
        {
            scan.getNext();
            //Put in symbol table and storage manager
            STIdentifier symbolEntry = new STIdentifier(name, scan.currentToken.primClassif , dclType,
                    1, 1, 1);
            symbolTable.putSymbol(name, symbolEntry);
            StorageEntry variableEntry =  new StorageEntry(name, dclType);
            storageManager.putEntry(name,variableEntry);

        }

        return null;
    }

    public void expression(){


    }


    public void error (String fmt, Object... varArgs) throws Exception
    {
        throw new ParserException(scan.currentToken.iSourceLineNr
                                , String.format(fmt, varArgs)
                                , scan.sourceFileNm);
    }
}
