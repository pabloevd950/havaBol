/*
  This is a simple driver for the havabol language interpreter.
  Command Arguments:
      java havabol.HavaBol arg1
             arg1 is the havabol source file name.
  Output:
      Anything sent to STDOUT will be printed to the terminal
      debug <option> on prints the STDOUT values, variables, tokens, etc. to STDOUT
  Notes:
      1. This creates a SymbolTable, StorageManager, scanner, and parser objects
         for use in interpreting the havabol source code.
      2. This uses both the student's Parser class and Scanner class to get
         each token from the input file.
         It uses the getNext method until it returns an empty string.
      3. If the Scanner raises an exception, this driver prints
         information about the exception and terminates.
      4. The token is printed using the Token::printToken() method in debugging.
 */
package havabol;

import havabol.SymbolTable.SymbolTable;

public class HavaBol
{
    public static void main(String[] args)
    {
        // Create the SymbolTable and storage manager
        SymbolTable symbolTable = new SymbolTable();
        StorageManager storageManager = new StorageManager();

        try
        {
            // create scanner and parser objects
            Scanner scan = new Scanner(args[0], symbolTable);
            Parser parser = new Parser(symbolTable, storageManager, scan);
            ResultValue res;

            // begin parsing file
            while (scan.currentToken.primClassif != Token.EOF)
            {
                res = parser.statement(true);
                //scan.currentToken.printToken();
                //System.out.println("*"+res.terminatingStr);

                if (res.type == Token.END)
                    parser.error("ERROR: OUT OF PLACE TOKEN '%s'\n\t" +
                                      "TOKEN NOT VALID HERE", res.terminatingStr);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}