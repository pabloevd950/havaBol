package havabol;

import havabol.SymbolTable.SymbolTable;

/**
 * Created by tkb442 on 3/3/17.
 */
public class Parser
{
    public SymbolTable symbolTable;
    public Scanner scan;

    public Parser (SymbolTable symbolTable, Scanner scan)
    {
        this.symbolTable = symbolTable;
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
            //scan.currentToken.printToken();
            switch (scan.currentToken.tokenStr)
            {
                case "if":
                    return ifStmt();


            }
        }
    }

    public void ifStmt()
    {

    }
}
