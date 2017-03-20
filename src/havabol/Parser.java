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
            switch (scan.currentToken.subClassif)
            {
                case Token.FLOW:
                    if (scan.currentToken.tokenStr.equals("if"))
                        ifStmt();
                    else
                        whileStmt();
                case Token.IDENTIFIER:
                    assignStmt();
                default:
                    throw new Exception();

            }
        }
    }

    public ResultValue assignStmt()
    {
        return null;
    }

    public ResultValue ifStmt()
    {



        return null;
    }

    public ResultValue whileStmt()
    {
        return null;
    }

    public void error (String fmt, Object... varArgs) throws Exception
    {
        throw new ParserException(scan.currentToken.iSourceLineNr
                                , String.format(fmt, varArgs)
                                , scan.sourceFileNm);
    }
}
