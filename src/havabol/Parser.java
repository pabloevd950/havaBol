package havabol;

import com.sun.xml.internal.bind.v2.TODO;
import havabol.SymbolTable.STIdentifier;
import havabol.SymbolTable.SymbolTable;

import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Pattern;

/*
 * This is the simple Parser class for the HavaBol programming language.
 * All errors and exceptions are thrown up to the calling method and output to stderr from there.
 */
public class Parser
{
    // constants for execution status
    public static final int EXECUTING = 1;
    public static final int IGNORING = 2;
    public static final int CONTINUE = 3;
    public static final int BREAK = 4;


    // public values to help keep track of our havabol objects and execution status
    public SymbolTable symbolTable;
    public StorageManager storageManager;
    public Scanner scan;
    //public int iExec;

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
        //iExec = EXECUTING;
    }

    /**
     * This method gets the next token from the source file line using Scanner::getNext().
     * Then it goes through a switch statement to determine what to do with the given token.
     * <p>
     * Returns resultValues returned to the method by the helper functions provided to it.
     *
     * @param iExec Tells the statement function whether we need to execute the code we find or
     *              just look at it
     * @return A generic ResultValue with all fields VOID if the bottom is hit, otherwise returns
     *         the calculated ResultValue
     * @throws Exception generic Exception type to handle any processing errors
     */
    public ResultValue statement(int iExec) throws Exception
    {
        // advance token
        scan.getNext();

        // determine what to do with the current token
        switch (scan.currentToken.primClassif)
        {// EOF, CONTROL, OPERAND, FUNCTION, OPERATOR, SEPARATOR, DEBUG, or defaults to error
            case Token.EOF:
                return new ResultValue("", Token.EOF, Token.VOID, "");
            case Token.CONTROL:
                switch (scan.currentToken.subClassif)
                { // control token found, so determine the sub type for proper execution
                    // DECLARE, FLOW, END, defaults to error
                    case Token.DECLARE:
                        return declareStmt(iExec);
                    case Token.FLOW:
                        if (scan.currentToken.tokenStr.equals("if"))
                            return ifStmt(iExec);
                        else if (scan.currentToken.tokenStr.equals("while"))
                            return whileStmt(iExec);
                        else if (scan.currentToken.tokenStr.equals("for"))
                            return forStmt(iExec);
                        break;
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
                return assignStmt(iExec);
            case Token.FUNCTION:
                return function(iExec);
            case Token.OPERATOR:
            case Token.SEPARATOR:
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
     * @param iExec Tells the statement function whether we need to execute the code we find or
     *              just look at it
     * @return ResultValue object that contains the final result of execution
     * @throws Exception generic Exception type to handle any processing errors
     */
    public ResultValue declareStmt(int iExec) throws Exception
    {
        int structure = ResultValue.primitive;
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

        if (iExec == EXECUTING)
        {// we are executing, not ignoring
            // put declaration into symbol table and storage manager
            String variableStr = scan.currentToken.tokenStr;
            // save token to skip to in arrays for assignment
            Token identifier = scan.currentToken;

            // check if it's an array
            if (scan.nextToken.tokenStr.equals("["))
            {
                // advance token to left bracket
                scan.getNext();

                //assign structure type
                structure = ResultValue.fixedArray;
                // check if nothing in between brackets aka length is not declared
                if (scan.nextToken.tokenStr.equals("]"))
                {
                    // advance token to right bracket
                    scan.getNext();

                    // check what next token is
                    if (scan.nextToken.tokenStr.equals(";"))
                        error("ERROR: CANNOT DECLARE ARRAY WITHOUT LENGTH " +
                                "IF VALUE LIST NOT GIVEN");
                    // '=' triggers assignStmt after putting into SymbolTable and StorageManager
                    else if (scan.nextToken.tokenStr.equals("="))
                    {
                        // advance token to equal, so declareArray is on right token
                        scan.getNext();

                        //put symbol into table
                        symbolTable.putSymbol(variableStr, new STIdentifier(variableStr
                                , identifier.primClassif, dclType
                                , ResultValue.fixedArray, 1, 1));
                        //store into storagemanager
                        storageManager.putEntry(variableStr, new ResultArray(identifier.tokenStr, dclType, structure));

                        return declareArray(variableStr, dclType, 0);
                    }
                    //anything else is an error
                    else
                        error("ERROR:UNEXPECTED SYMBOL %s, EXPECTED EITHER ']' OR '='", scan.nextToken.tokenStr);
                }
                // size is declared or unbound
                else
                {
                    //it is an unbounded array
                    if(scan.nextToken.tokenStr.equals("unbound"))
                    {
                        //set to unbound
                        structure = ResultValue.unboundedArray;

                        // advance token to 'unbound'
                        scan.getNext();
                        // advance token to ']'
                        scan.getNext();
                        /* check what next token is*/
                        //unbounded array is declared, without valuelist
                        if (scan.nextToken.tokenStr.equals(";"))
                        {
                            //advance to ';'
                            scan.getNext();

                            //put into symbol table
                            symbolTable.putSymbol(variableStr, new STIdentifier(variableStr
                                    , identifier.primClassif, dclType
                                    , ResultValue.unboundedArray, 1, 1));
                            //store into storage manager
                            storageManager.putEntry(variableStr, new ResultArray(identifier.tokenStr,
                                    new ArrayList<ResultValue>(), dclType, structure,
                                        0, -1, 0));

                            return new ResultValue("", Token.DECLARE, ResultValue.primitive
                                    , scan.currentToken.tokenStr);
                        }
                        // '=' triggers assignStmt after putting into SymbolTable and StorageManager
                        else if (scan.nextToken.tokenStr.equals("="))
                        {
                            // advance token to '=', so declareArray is on right token
                            scan.getNext();

                            //put into symboltable
                            symbolTable.putSymbol(variableStr, new STIdentifier(variableStr
                                    , identifier.primClassif, dclType
                                    , ResultValue.unboundedArray, 1, 1));
                            //store into storagemanager
                            storageManager.putEntry(variableStr, new ResultArray(identifier.tokenStr, dclType, structure));

                            return declareArray(variableStr, dclType, -1);
                        }
                        //anything else is an error
                        else
                            error("ERROR:UNEXPECTED SYMBOL %s, EXPECTED EITHER ';' OR '='", scan.nextToken.tokenStr);
                    }
                    //do expression to find declared length of fixed array
                    int length = Integer.parseInt(Utilities.toInteger(this, expression(false)));

                    //check to see if length is reasonable
                    if (length < 0)
                        error("ERROR: ARRAY SIZE HAVE TO BE POSITIVE");

                    //advance to right bracket
                    scan.getNext();
                    //advance token to either ';' or '='
                    scan.getNext();
                    // value list is not given, but still declared
                    if (scan.currentToken.tokenStr.equals(";"))
                    {
                        //put variable name into symboltable
                        symbolTable.putSymbol(variableStr, new STIdentifier(variableStr
                                , identifier.primClassif, dclType
                                , ResultValue.fixedArray, 1, 1));
                        //create a random array that equals the length
                        ArrayList<ResultValue> garbo = new ArrayList<>();
                        for (int z = 0; z < length; z++)
                            garbo.add(null);
                        //store in storagemanager
                        storageManager.putEntry(variableStr, new ResultArray(identifier.tokenStr,
                                garbo, dclType, structure, 0, length, (length + 1) * -1));

                        return new ResultValue("", Token.DECLARE, ResultValue.fixedArray
                                , scan.currentToken.tokenStr);
                    }
                    // value list is given, so send to declareArray
                    else if (scan.currentToken.tokenStr.equals("="))
                    {
                        //put variable name into symboltable
                        symbolTable.putSymbol(variableStr, new STIdentifier(variableStr
                                , identifier.primClassif, dclType
                                , ResultValue.fixedArray, 1, 1));
                        //put entry into storagemanager
                        storageManager.putEntry(variableStr, new ResultArray(identifier.tokenStr, null, dclType, structure, 0, length, (length+1)*-1));

                        return declareArray(variableStr, dclType, length);
                    }
                    //anything else is an error
                    else
                        error("ERROR: EXPECTED EITHER ';' OR '=', UNEXPECTED TOKEN '%s'", scan.currentToken.tokenStr);
                }
            }
            // it is not an array
            else
            {
                //put varaible name into symboltable
                symbolTable.putSymbol(variableStr, new STIdentifier(variableStr
                        , scan.currentToken.primClassif, dclType
                        , 1, 1, 1));
                //put entry into storage manager
                storageManager.putEntry(variableStr, new ResultValue(dclType, structure));
            }
        }

        // if the next token is '=' call assignStmt to assign value to operand
        if(scan.nextToken.tokenStr.equals("="))
            return assignStmt(iExec);
        // else check if it is an operator, because that is an error
        else if (scan.nextToken.primClassif == Token.OPERATOR)
            error("ERROR: DECLARE CANNOT PERFORM %s OPERATION BEFORE INITIALIZATION"
                        , scan.nextToken.tokenStr);
        // else check for statement terminating ';'
        else if(! scan.getNext().equals(";"))
            error("ERROR: UNTERMINATED DECLARATION STATEMENT, ';' EXPECTED");

        return new ResultValue("", Token.DECLARE, ResultValue.primitive
                , scan.currentToken.tokenStr);
    }

    /**
     * This method will assign the ResultValue objects returned by expression() to the
     * variable specified in order to add it to the StorageManager. The variable string will be the key.
     * <p>
     * assume currentToken is '='
     * @param type  The data type of the variable
     * @param declared how many items were declared
     *                 if 0, length was not given
     * @return resArray the ResultArray object that was last changed
     * @throws Exception a generic exception to catch errors
     */
    public ResultArray declareArray(String variableStr, int type, int declared) throws Exception
    {
        //will add this into the array list
        ResultValue resExpr = new ResultValue(-1,-1);
        //to be returned
        ResultArray resArray;
        //the list that is set if expression is called
        ArrayList<ResultValue> expressionVals = new ArrayList<>();
        //will act as iPopulated
        int iAmt = 1;

        // loop using expression, until ';' is found
        while(!resExpr.terminatingStr.equals(";") && !scan.currentToken.tokenStr.equals(";"))
        {
            // evaluate expression to receive values for arraylist
            resExpr = expression(false);
            //advance past comma
            scan.getNext();
            //increment
            iAmt++;

            //increment amount populated and check to see if greater than declare when value list not given
            if (declared != -1 && iAmt-1 > declared && declared > 0 )
                error("ERROR: CANNOT DECLARE MORE THAN '%d' INTO ARRAY '%s'", declared, variableStr);
            switch (type)
            {// determine the type of value to assign to ResultValue to add to array
                case Token.INTEGER:
                    resExpr.value = Utilities.toInteger(this, resExpr);
                    resExpr.type = Token.INTEGER;
                    // add into list
                    expressionVals.add(resExpr);
                    break;
                case Token.FLOAT:
                    resExpr.value = Utilities.toFloat(this, resExpr);
                    resExpr.type = Token.FLOAT;
                    // add into list
                    expressionVals.add(resExpr);
                    break;
                case Token.BOOLEAN:
                    resExpr.value = Utilities.toBoolean(this, resExpr);
                    resExpr.type = Token.BOOLEAN;
                    // add into list
                    expressionVals.add(resExpr);
                    break;
                case Token.STRING:
                    resExpr.type = Token.STRING;
                    // add into list
                    expressionVals.add(resExpr);
                    break;
                default:
                    error("ERROR: ASSIGN TYPE '%s' IS NOT A RECOGNIZED TYPE", variableStr);

            }
        }
        // missing terminator
        if (!scan.currentToken.tokenStr.equals(";"))
            error("ERROR: EXPECTED ';', BUT FOUND '%s'", scan.currentToken.tokenStr);
        //change declare size if original length was not given i.e arr[] = 1, 2, 3;
        if (declared != -1 && expressionVals.size() > declared)
            declared = expressionVals.size();

        /*create ResultArray to return*/
        //unbounded array side
        if (declared == -1)
            resArray = new ResultArray(variableStr, expressionVals, type, ResultValue.unboundedArray, --iAmt, declared, iAmt);
        //fixed array
        else
            resArray = new ResultArray(variableStr, expressionVals, type, ResultValue.fixedArray, --iAmt, declared, iAmt);

        //check for debug to be on
        if(scan.bShowAssign)
        {
            System.out.print("\t\t...Variable Name: " + variableStr + " Values:");
            for(ResultValue z : resArray.array)
                System.out.print(" " + z.value);
            System.out.println();
        }

        //fill with garbage to be able to assign
        while(resArray.array.size() < declared)
            resArray.array.add(null);

        //add into storagemanager
        storageManager.putEntry(variableStr, resArray);

        return resArray;
    }

    /**
     * This method is called when ever an operand or a '=' is encountered.
     * It will determine the value and assign it to the specified operand
     * <p>
     * Also handles +=, -=, *=, /=
     *
     * @param iExec Tells the statement function whether we need to execute the code we find or
     *              just look at it
     * @return ResultValue object that contains the final result of execution
     * @throws Exception generic Exception type to handle any processing errors
     */
    public ResultValue assignStmt(int iExec) throws Exception
    {
        // returned value
        ResultValue res;
        //leftType is the data type, index is the index being assigned to, if given
        int leftType = -1, iIndex = 0;
        //name of the variable
        String variableStr;
        //flag to determine if assigning to index or entire array
        Boolean bIndex = false;
        //temporary result array object
        ResultArray resA;

        //if executing, then get its saved data type
        if (iExec == EXECUTING)
            try
            {
                leftType = storageManager.getEntry(scan.currentToken.tokenStr).type;
            }
            catch (Exception e)
            {
                error("ERROR: VARIABLE %s NOT YET DECLARED", scan.currentToken.tokenStr);
            }

        // make sure current token is an identifier to properly assign
        if (scan.currentToken.subClassif != Token.IDENTIFIER)
            error("ERROR: %s IS NOT A VALID TARGET VARIABLE FOR ASSIGNMENT"
                                                    , scan.currentToken.tokenStr);
        //save variable name
        variableStr = scan.currentToken.tokenStr;

        // pull storage manager entry of variable
        res = storageManager.getEntry(variableStr);

        //if the reference is not in symbol table while executing
        if(res == null && iExec == EXECUTING)
            // undeclared variable while iExec true
            error("ERROR: ASSIGN REQUIRES THAT '%s' BE DECLARED", variableStr);

        //advance token to either '[' or operator i.e '=', '+=', etc.
        scan.getNext();

        //check to see if array index is given
        if (scan.currentToken.tokenStr.equals("["))
        {
            // arrays must call expression to determine array index
            iIndex = Integer.parseInt(Utilities.toInteger(this, expression(false)));
            //advance from the index to right bracket
            scan.getNext();
            //advance from the right bracket to the operator
            scan.getNext();
            //turn on the flag that says we are assigning an index
            bIndex = true;
        }

        // make sure current token is an operator
        if (scan.currentToken.primClassif != Token.OPERATOR)
            error("ERROR: ASSIGN EXPECTED AN OPERATOR BUT FOUND %s", scan.currentToken.tokenStr);

        // determine what kind of operation to execute
        switch (scan.currentToken.tokenStr)
        {
            case "=":
                //if executing
                if (iExec == EXECUTING)
                {
                    // check to see if array or not
                    switch (res.structure)
                    {
                        case ResultValue.primitive:
                            //not an array, so do basic assign
                            ResultValue res1;
                            if (bIndex == false)
                            {
                                res1 = assign(variableStr, expression(false), leftType);

                                // TEMP
                                if (scan.currentToken.primClassif != Token.OPERAND)
                                    scan.getNext();
                            }
                            else
                            {
                                ResultValue newSubString = expression(false);
                                String value = storageManager.getEntry(variableStr).value;

                                if (iIndex == -1)
                                {
                                    iIndex = value.length() - 1;
                                }
                                if (iIndex > value.length() - 1)
                                    error("ERROR: '%d' IS OUT OF BOUNDS", iIndex);

                                String newValue = value.substring(0, iIndex) + newSubString.value + value.substring(iIndex + 1);
                                ResultValue finalString = new ResultValue(newValue, Token.STRING);
                                res1 = assign(variableStr, finalString, leftType);

                            }
                            return res1;
                        case ResultValue.unboundedArray:
                        case ResultValue.fixedArray:
                            // this means that more than one index is being changed, so change array
                            if (bIndex == false)
                                resA = assignArray(variableStr, leftType, ((ResultArray)res).iDeclaredLen);
                            // this means only one index
                            else
                            {
                                //check to see if index requested is in bounds
                                if (res.structure != ResultValue.unboundedArray && iIndex >= ((ResultArray) res).iDeclaredLen)
                                    error("ERROR: '%d' IS OUT OF BOUNDS", iIndex);

                                //if index is negative
                                if (iIndex < 0)
                                {
                                    //check to see if negative subscript is not valid
                                    if (iIndex < ((ResultArray) res).iNegSub * -1) {
                                        error("ERROR: CANNOT ACCESS INDEX '%d', MAX NEGATIVE SUBSCRIPT IS '%d'"
                                                , iIndex, ((ResultArray) res).iNegSub * -1);
                                    }
                                    //subscript is in bounds
                                    else
                                    {
                                        //fixed array
                                        if (((ResultArray) res).iDeclaredLen != -1)
                                            //add declared length in order to get positive subscript
                                            iIndex += ((ResultArray) res).iDeclaredLen;
                                        //unbounded
                                        else
                                            //add populated length to get positive subscript
                                            iIndex += ((ResultArray) res).iPopulatedLen;
                                    }
                                }
                                //unbounded
                                if (((ResultArray) res).iDeclaredLen == -1)
                                    //while the index is greater than the size, add garbage
                                    while (iIndex >= ((ResultArray) res).array.size())
                                        ((ResultArray) res).array.add(null);

                                resA = assignIndex(variableStr, leftType, iIndex);
                            }
                            return resA;
                        default:
                            error("ERROR: STRUCTURE TYPE '%d' IS NOT ALLOWED ON '%s'", res.structure, res.value);
                    }
                }
                //not executing
                else
                    skipTo(scan.currentToken.tokenStr, ";");
                break;
            // see parsing part 2
            case "+=":
                ResultValue resPlus = Utilities.add(this, res, expression(false));
                assign(variableStr, resPlus , leftType);
                break;
            case "-=":
                ResultValue resMin = Utilities.sub(this, res, expression(false));
                assign(variableStr, resMin , leftType);
                break;
            case "*=":
                ResultValue resMul = Utilities.mul(this, res, expression(false));
                assign(variableStr, resMul , leftType);
                break;
            case "/=":
                ResultValue resDiv = Utilities.div(this, res, expression(false));
                assign(variableStr, resDiv , leftType);
                break;
            case "^=":
                ResultValue resPow = Utilities.exp(this, res, expression(false));
                assign(variableStr, resPow , leftType);
                break;
            default:
                error("ERROR: EXPECTED ASSIGNMENT OPERATOR BUT FOUND %s", scan.currentToken.tokenStr);
        }

        // if we ever hit this line, iExec is ignoring
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
        switch (type)
        {// determine the type of value to assign to variable
            case Token.INTEGER:
                resExpr.value = Utilities.toInteger(this, resExpr);
                resExpr.type = Token.INTEGER;
                break;
            case Token.FLOAT:
                resExpr.value = Utilities.toFloat(this, resExpr);
                resExpr.type = Token.FLOAT;
                break;
            case Token.BOOLEAN:
                resExpr.value = Utilities.toBoolean(this, resExpr);
                resExpr.type = Token.BOOLEAN;
                break;
            case Token.STRING:
                resExpr.type = Token.STRING;
                break;
            default:
                error("ERROR: ASSIGN TYPE '%s' IS NOT A RECOGNIZED TYPE", variableStr);
        }
        // assign value to the variable and return result value
        storageManager.putEntry(variableStr, resExpr);

        // check for debug on
        if(scan.bShowAssign)
            System.out.println("\t\t...Variable Name: " + variableStr + " Value: " + resExpr.value);

        return resExpr;
    }

    /**
     * This method will assign the ResultValue objects returned by expression() to the
     * variable specified in order to add it to the StorageManager. The variable string will be the key.
     * <p>
     * Assume only one value in value list, and that currentToken is '='
     * @param variableStr contains the string of the variable we are assigning a value to
     * @param type Token type which we will be assigning
     * @param declared The total amount allowed to add to first array
     * @return ResultArray object that contains the final result of execution
     * @throws Exception generic Exception type to handle any processing errors
     */
    private ResultArray assignArray(String variableStr, int type, int declared) throws Exception
    {
        //will add this into the array list
        ResultValue resExpr = new ResultValue(-1,-1);
        //to be returned
        ResultArray resArray = new ResultArray(variableStr, -1, -1);
        //the populated length
        int len = 0;
        //this is for operands (aka scalar assignment with variables and constants) and array to array assignment
        if (scan.nextToken.primClassif == Token.OPERAND)
        {
            //iterate through for loop
            int i;
            //this is the array of the variable that we need to act on
            ResultArray array1 = (ResultArray)storageManager.getEntry(variableStr);
            //this is the value that we need to assign to
            ResultValue value2 = expression(false);

            if ( array1 == null)
                // make sure item has been defined
                error("ERROR: VARIABLE '%s' IS NOT DEFINED IN THE SCOPE", variableStr);
            if ( value2 == null)
                // make sure item has been defined
                error("ERROR: VARIABLE '%s' IS NOT DEFINED IN THE SCOPE", scan.nextToken.tokenStr);
            //if it's not an unbounded array adn trying to do scalar assignment
            if (array1.structure == ResultValue.unboundedArray && value2.structure == ResultValue.primitive)
                error("ERROR: CANNOT ASSIGN A SCALAR TO AN UNBOUNDED ARRAY");

            //if it's not an array
            if (value2.structure == ResultValue.primitive)
            {
                // loop until declared length filling all values
                for(i=0; i < array1.iDeclaredLen;i++)
                {
                    //switch based on the data type
                    switch(type)
                    {
                        case Token.INTEGER:
                            resExpr = value2;
                            resExpr.value = Utilities.toInteger(this, resExpr);
                            resExpr.type = Token.INTEGER;
                            //set into array of first
                            array1.array.set(i, resExpr);
                            break;
                        case Token.FLOAT:
                            resExpr = value2;
                            resExpr.value = Utilities.toFloat(this, resExpr);
                            resExpr.type = Token.FLOAT;
                            //set into array of first
                            array1.array.set(i, resExpr);
                            break;
                        case Token.BOOLEAN:
                            resExpr = value2;
                            resExpr.value = Utilities.toBoolean(this, resExpr);
                            resExpr.type = Token.BOOLEAN;
                            //set into array of first
                            array1.array.set(i, resExpr);
                            break;
                        case Token.STRING:
                            resExpr = value2;
                            resExpr.type = Token.STRING;
                            //set into array of first
                            array1.array.set(i, resExpr);
                            break;
                        default:
                            error("ERROR: ASSIGN TYPE '%s' IS NOT A RECOGNIZED TYPE", variableStr);
                    }
                }

                //create ResultArray to return
                if (declared == -1)
                    resArray = new ResultArray(variableStr, array1.array, type, ResultValue.unboundedArray, len, declared, len);
                else
                    resArray = new ResultArray(variableStr, array1.array, type, ResultValue.fixedArray, len, declared, len);

                //check if debugger is on
                if(scan.bShowAssign)
                {
                    System.out.print("\t\t...Variable Name: " + variableStr + " Values:");
                    for(ResultValue z : resArray.array)
                        System.out.print(" " + z.value);
                    System.out.println();
                }
                //add into storage manager
                storageManager.putEntry(variableStr, resArray);

                return resArray;

            }
            //it is a fixed array
            else if (value2.structure == ResultValue.fixedArray || value2.structure == ResultValue.unboundedArray)
            {
                //typecast into result array
                ResultArray array2 = (ResultArray)value2;
                //iFirst is declared length of first, iSecond is how much to change
                int iFirst = array1.iDeclaredLen, iSecond = array2.iPopulatedLen;

                //if the first's declared length is less than the amount to populate, truncate the populating amount
                //only if not an unbounded array, otherwise copy as many as possible
                if (declared != -1 && iFirst < iSecond)
                    iSecond = iFirst;
                //loop from 0 to amount to populate
                for(i=0; i < iSecond;i++)
                {
                    //switch based on data type
                    switch(type)
                    {
                        case Token.INTEGER:
                            resExpr = array2.array.get(i);
                            resExpr.value = Utilities.toInteger(this, resExpr);
                            resExpr.type = Token.INTEGER;
                            /*set into array of first*/
                            //if first array is fixed, simply set
                            if (declared != -1)
                                array1.array.set(i, resExpr);
                            //unbounded
                            else
                            {
                                //if the array list corresponding to unbounded array is smaller than index, add null
                                if (array1.array.size() <= i)
                                    array1.array.add(i, null);
                                //set into array
                                array1.array.set(i, resExpr);
                            }
                            break;
                        case Token.FLOAT:
                            resExpr = array2.array.get(i);
                            resExpr.value = Utilities.toFloat(this, resExpr);
                            resExpr.type = Token.FLOAT;
                            /*set into array of first*/
                            //if first array is fixed, simply set
                            if (declared != -1)
                                array1.array.set(i, resExpr);
                                //unbounded
                            else
                            {
                                //if the array list corresponding to unbounded array is smaller than index, add null
                                if (array1.array.size() <= i)
                                    array1.array.add(i, null);
                                //set into array
                                array1.array.set(i, resExpr);
                            }
                            break;
                        case Token.BOOLEAN:
                            resExpr = array2.array.get(i);
                            resExpr.value = Utilities.toBoolean(this, resExpr);
                            resExpr.type = Token.BOOLEAN;
                            /*set into array of first*/
                            //if first array is fixed, simply set
                            if (declared != -1)
                                array1.array.set(i, resExpr);
                            //unbounded
                            else
                            {
                                //if the array list corresponding to unbounded array is smaller than index, add null
                                if (array1.array.size() <= i)
                                    array1.array.add(i, null);
                                //set into array
                                array1.array.set(i, resExpr);
                            }
                            break;
                        case Token.STRING:
                            resExpr = array2.array.get(i);
                            resExpr.type = Token.STRING;
                            /*set into array of first*/
                            //if first array is fixed, simply set
                            if (declared != -1)
                                array1.array.set(i, resExpr);
                            //unbounded
                            else
                            {
                                //if the array list corresponding to unbounded array is smaller than index, add null
                                if (array1.array.size() <= i)
                                    array1.array.add(i, null);
                                //set into array
                                array1.array.set(i, resExpr);
                            }
                            break;
                        default:
                            error("ERROR: ASSIGN TYPE '%s' IS NOT A RECOGNIZED TYPE", variableStr);
                    }
                }
                //go through the arraylist to count which ones are populated
                for(ResultValue res : array1.array)
                    //if not null, increment length
                    if(res!=null)
                        len++;
                /*create ResultArray to return*/
                //unbound
                if (declared == -1)
                    resArray = new ResultArray(variableStr, array1.array, type, ResultValue.unboundedArray, len, declared, len);
                //fixed
                else
                    resArray = new ResultArray(variableStr, array1.array, type, ResultValue.fixedArray, len, declared, len);

                //check if debugger is on
                if(scan.bShowAssign)
                {
                    System.out.print("\t\t...Variable Name: " + variableStr + " Values:");
                    for(ResultValue z : resArray.array)
                        System.out.print(" " + z.value);
                    System.out.println();
                }

                //add into storagemanager
                storageManager.putEntry(variableStr, resArray);

                return resArray;
            }


        }
        else
            error("ERROR: EXPECTED OPERAND, BUT FOUND '%s'", scan.nextToken.tokenStr);

        return resArray;
    }

    /**
     * This method will assign the ResultValue object returned by expression to the
     * variable specified in order to add it to the StorageManager. The variable string will be the key.
     * <p>
     * Assume currentToken is on '='.
     * @param variableStr the name of the variable
     * @param type the data type of variable
     * @param index the index that is getting assigned
     * @return resArray this contains the last changed ResultArray object
     * @throws Exception generic exception that catches errors
     */
    public ResultArray assignIndex(String variableStr, int type, int index) throws Exception
    {
        //will add this into the arraylist
        ResultValue resExpr = new ResultValue(-1,-1);
        //to be returned
        ResultArray resArray;
        //populated length
        int iLen = 0;

        //this is for operands aka variables and constants
        if (scan.nextToken.primClassif == Token.OPERAND)
        {
            //this is the array that we need to act on
            ResultArray array1 = (ResultArray)storageManager.getEntry(variableStr);
            //this is the value that we need to use to assign to index
            //if it is a variable, it should return its value, else will return value of expression
            ResultValue value2 = expression(false);

            if (!scan.nextToken.tokenStr.equals(";"))
                error("ERROR: MISSING ';' TERMINATOR");

            if ( array1 == null)
                // make sure item has been defined
                error("ERROR: VARIABLE '%s' IS NOT DEFINED IN THE SCOPE", variableStr);
            if ( value2 == null)
                // make sure item has been defined
                error("ERROR: OPERAND '%s' IS NOT DEFINED IN THE SCOPE", scan.nextToken.tokenStr);

            //if it's a variable
            if (value2.structure == ResultValue.primitive)
            {
                //switch based on the array data type
                switch(type)
                {
                    case Token.INTEGER:
                        resExpr = value2;
                        resExpr.value = Utilities.toInteger(this, resExpr);
                        resExpr.type = Token.INTEGER;
                        //set into array of first
                        array1.array.set(index, resExpr);
                        break;
                    case Token.FLOAT:
                        resExpr = value2;
                        resExpr.value = Utilities.toFloat(this, resExpr);
                        resExpr.type = Token.FLOAT;
                        //set into array of first
                        array1.array.set(index, resExpr);
                        break;
                    case Token.BOOLEAN:
                        resExpr = value2;
                        resExpr.value = Utilities.toBoolean(this, resExpr);
                        resExpr.type = Token.BOOLEAN;
                        //set into array of first
                        array1.array.set(index, resExpr);
                        break;
                    case Token.STRING:
                        resExpr = value2;
                        resExpr.type = Token.STRING;
                        //set into array of first
                        array1.array.set(index, resExpr);
                        break;
                    default:
                        error("ERROR: ASSIGN TYPE '%s' IS NOT A RECOGNIZED TYPE", variableStr);
                }
                //check if debugger is on
                if(scan.bShowAssign)
                    System.out.println("\t\t...Variable Name: " + variableStr
                            +" Index: " + index + " Value: " + resExpr.value + " SIZE: " + array1.iPopulatedLen);
            }
            else
                error("ERROR: CANNOT ASSIGN STRUCTURE '%d' INTO AN INDEX", value2.structure);
            //count populated values
            for(ResultValue res : array1.array)
                //increment length if not null
                if(res != null)
                    iLen++;
            /*create ResultArray to return*/
            //unbound
            if (array1.iDeclaredLen == -1)
                resArray = new ResultArray(variableStr, array1.array, type, ResultValue.unboundedArray, iLen
                        , array1.iDeclaredLen, (array1.iDeclaredLen + 1));
            //fixed
            else
                resArray = new ResultArray(variableStr, array1.array, type, ResultValue.fixedArray, iLen
                        , array1.iDeclaredLen, (array1.iDeclaredLen+1));
            //add into storagemanager
            storageManager.putEntry(variableStr, resArray);

            return resArray;

        }
        else
            error("ERROR: CANNOT ASSIGN '%s' INTO INDEX", scan.nextToken.tokenStr);

        return null;
    }

    /**
     * This method will evaluate an expression and return a ResultValue object
     * that contains the final result.
     * <p>
     * Handles complex expression. This method assumes that the current token is
     * the token before the start of the expression. When it returns, the current token is at
     * the token succeeding the evaluated expression.
     *
     * @return ResultValue object that contains the final result of execution
     * @throws Exception generic Exception type to handle any processing errors
     */
    public ResultValue expression(Boolean infunc) throws Exception
    {
        Stack outPutStack = new Stack<ResultValue>();         // Stack for Result values
        Stack stack = new Stack<Token>();                     // Stack for operator tokens
        Token poppedOperator;                                 // Operator and operand tokens
        ResultValue firstResValue, secondResValue, res;       // Result value for operands and final result
        Boolean bFound;                                       // Boolean to determine if we found left paren
        Boolean bCategory = false;                            // Boolean to check proper infix notation

        //If we are calling from a function like print, or built in skip name.
        if(scan.currentToken.primClassif == Token.FUNCTION && scan.currentToken.tokenStr.equals("print"))
            scan.getNext();

        // Advance to start of expression.
        if(scan.currentToken.primClassif != Token.FUNCTION || scan.currentToken.tokenStr.equals("print"))
            scan.getNext();

        // control token used to check for unary minus, and return at desired token.
        Token prevToken = scan.currentToken;

        // loop through expression
        while(scan.currentToken.primClassif == Token.OPERAND // check if token is operand
                || scan.currentToken.primClassif == Token.OPERATOR // check if it is an operator
                || scan.currentToken.primClassif == Token.FUNCTION // check for functions
                || "()".contains(scan.currentToken.tokenStr)) // check if its separator
        {
            //System.out.println(scan.currentToken.tokenStr);
            switch (scan.currentToken.primClassif)
            {
                //If token is operand
                case Token.OPERAND:
                    if(bCategory == true)
                        // we encountered an unexpected operand, looking for an operator
                        error("ERROR: UNEXPECTED OPERAND '%s', EXPECTED OPERATOR.", scan.currentToken.tokenStr);

                    //Get result value of operand and push to stack
                    firstResValue = getOperand();
                    outPutStack.push(firstResValue);
                    //Next operand should be operator
                    bCategory = true;
                    break;

                // token is an operator
                case Token.OPERATOR:
                    if(bCategory == false
                            && !scan.currentToken.tokenStr.equals("-")
                            && !scan.currentToken.tokenStr.equals("not"))
                        // we encountered an unexpected operator, looking for an operand
                        error("ERROR: UNEXPECTED OPERATOR '%s', EXPECTED OPERAND", scan.currentToken.tokenStr);

                    // determine operator, look for unary minus
                    switch (scan.currentToken.tokenStr)
                    {
                        case "not":
                            if(scan.nextToken.primClassif == Token.OPERAND || scan.nextToken.equals("("))
                                stack.push(scan.currentToken);
                            break;
                        //Check if we have unary minus
                        case "-":
                            // check if the previous token was an operator. If so, we want operator or u-
                            if(prevToken.primClassif == Token.OPERATOR
                                    || prevToken.tokenStr.equals(",")
                                    || prevToken.tokenStr.equals("("))
                            {
                                //Check if next token is an operand or valid separator.
                                if(scan.nextToken.primClassif == Token.OPERAND || scan.nextToken.tokenStr.equals("("))
                                    // unary minus is true. Change the "-" to "u-" and push
                                    stack.push(new Token("u-"));
                                //If it isn't, then we encountered an error
                                else
                                    error("ERROR: UNEXPECTED OPERATOR '%s', EXPECTED OPERAND"
                                            , scan.nextToken.tokenStr);
                                break;
                            }
                            // operator is not unary minus
                        default:
                            // loop through expression while the stack is not empty
                            while(!stack.empty())
                            {
                                // check precedence
                                if(getPrecedence(scan.currentToken, false) < getPrecedence((Token)stack.peek(), true))
                                    // precedence of current operator is higher, break
                                    break;
                                else if(!stack.empty())
                                {   // stack is not empty and precedence is right, evaluate

                                    // pop operator and operands
                                    poppedOperator = (Token)stack.pop();
                                    firstResValue = (ResultValue) outPutStack.pop();

                                    if (poppedOperator.tokenStr.equals("u-"))
                                        // we have unary minus so we only need one operand
                                        res = (evaluate(new ResultValue("-1", Token.INTEGER)
                                                , firstResValue, "*"));
                                    else
                                    {
                                        secondResValue = (ResultValue) outPutStack.pop();
                                        res = evaluate(secondResValue, firstResValue, poppedOperator.tokenStr);
                                    }
                                    // push value back to top of output stack
                                    outPutStack.push(res);
                                }
                            }
                            // push the current token to the operator stack
                            stack.push(scan.currentToken);
                            break;
                    }
                    // we are now expecting an operand
                    bCategory = false;
                    break;


                // handle functions
                case Token.FUNCTION:
                    if(bCategory == true)
                        // we encountered an unexpected operand, looking for an operator
                        error("ERROR: UNEXPECTED FUNCTION CALL '%s', EXPECTED OPERATOR OR TERMINATOR."
                                , scan.currentToken.tokenStr);
                    // call function to get result value
                    stack.push(scan.currentToken);
                    scan.getNext();
                    break;

                // handle separators
                case Token.SEPARATOR:
                    switch (scan.currentToken.tokenStr)
                    {// determine left or right paren

                        case "(":
                            stack.push(scan.currentToken);
                            break;
                        case ")":
                            //Check if this is the last ")" in the function call
                            if(infunc && scan.nextToken.tokenStr.equals(";"))
                                break;

                            // right parenthesis found, set flag false until we find matching left paren
                            bFound = false;

                            // loop through stack until matching left paren is found
                            while (!stack.empty())
                            {// stack is not empty and left paren not found, pop top of stack
                                poppedOperator = (Token)stack.pop();
                                if (poppedOperator.tokenStr.equals("(") || poppedOperator.primClassif == Token.FUNCTION)
                                {// left paren found, set flag to true and check for func delimiter, and break
                                    bFound = true;
                                    //Check for function, and get result value
                                    if(poppedOperator.primClassif == Token.FUNCTION)
                                    {
                                        ResultValue temp = (ResultValue) outPutStack.pop();
                                        outPutStack.push(builtInFuncs(poppedOperator, temp));
                                    }
                                    // not in a function and left paren found, leave while loop
                                    break;
                                }
                                else if (poppedOperator.tokenStr.equals("u-"))
                                    // we have unary minus, apply it to operand
                                    outPutStack.push(evaluate(new ResultValue("-1", Token.INTEGER)
                                            , (ResultValue) outPutStack.pop(), "*"));

                                else
                                {// not a left paren, work with stack
                                    // get the first two operands for our operator
                                    firstResValue = (ResultValue)outPutStack.pop();
                                    secondResValue = (ResultValue)outPutStack.pop();

                                    // evaluate and push result back to stack
                                    res = evaluate(secondResValue, firstResValue, poppedOperator.tokenStr);
                                    outPutStack.push(res);
                                }
                            }

                            if ((bFound == false))
                                // left paren was not encountered
                                error("ERROR: EXPECTED LEFT PARENTHESIS");

                            break;
                    }
            }
            // set previous token to the current token
            prevToken = scan.currentToken;
            scan.getNext();
        }

        // this should get the last result value
        while(!stack.empty())
        {
            poppedOperator = (Token)stack.pop();
            if (poppedOperator.tokenStr.equals("(")) {
                // unmatched left parentesis
                error("ERROR: UNMATCHED RIGHT PARENTHESIS FOR EXPRESSION");
            }
            else if (poppedOperator.tokenStr.equals("u-"))
                // we have unary minus
                outPutStack.push(evaluate(new ResultValue("-1", Token.INTEGER), (ResultValue) outPutStack.pop(), "*"));
            else if (poppedOperator.tokenStr.equals("not"))
                outPutStack.push(evaluate(null, (ResultValue) outPutStack.pop() , poppedOperator.tokenStr));
            else
            {   // evaluate normally
                try
                {
                    ResultValue resvalue = (ResultValue) outPutStack.pop();
                    ResultValue res2value = (ResultValue) outPutStack.pop();
                    outPutStack.push(evaluate(res2value, resvalue, poppedOperator.tokenStr));
                }
                catch (Exception e)
                {
                    error("ERROR: INCORRECT PARAMETERS");
                }
            }
        }
        // final value
        res = (ResultValue) outPutStack.pop();

        if(scan.bShowExpr)
            // debug Expr on
            System.out.println("\t\t...Result Value: " + res.value);

        scan.setTo(prevToken);
        res.terminatingStr = scan.nextToken.tokenStr;

        //Return final result value
        return res;
    }


    /**
     *
     * This method recieves two operands and an operator and returns the resulting value
     * @param firstResValue first operand
     * @param secondResValue second operand
     * @param operator operator to determine what operation to perform on operands
     * @return Result value of operation
     * @throws Exception
     */
    public ResultValue evaluate(ResultValue firstResValue, ResultValue secondResValue, String operator) throws Exception
    {
        //Result value for return value
        ResultValue res = new ResultValue();


        //Operator string
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
            case "u-":
                res = Utilities.mul(this, firstResValue, secondResValue);
                break;
            case "#":
                res = Utilities.concatenate(this,firstResValue, secondResValue);
                break;
            case"not":
                res = Utilities.not(this, secondResValue);
                break;
            case"and":
                res = Utilities.and(this,firstResValue, secondResValue);
                break;
            case"or":
                res = Utilities.or(this,firstResValue, secondResValue);
                break;
            case"notin":
                System.out.println("NEED TO CALL SOMETHING FOR NOTIN");
                break;
            case"in":
                System.out.println("NEED TO CALL SOMETHING FOR IN");
                break;
            default:
                error("ERROR: '%s' IS NOT A VALID OPERATOR FOR EXPRESSION", operator);
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
     * @param iExec Tells the statement function whether we need to execute the code we find or
     *              just look at it
     * @param terminatingStr Tells us when we are done executing lines for the statement
     * @return ResultValue object that contains the final result of execution
     * @throws Exception generic Exception type to handle any processing errors
     */
    public ResultValue statements(int iExec, String terminatingStr) throws Exception
    {
        ResultValue result = statement(iExec);

        // loop until we find our terminating string
        while (! terminatingStr.contains(result.terminatingStr))
            result = statement(iExec);

        result.terminatingStr = scan.currentToken.tokenStr;
        return result;
    }

    /**
     * This method executes 'if' statements for HavaBol. It uses iExec and the
     * ResultValue object returned from calling expression in order to determine
     * if we are executing.
     * <p>
     * If we are not executing, we will still look over the lines to catch errors
     *
     * @param iExec Tells the statement function whether we need to execute the code we find or
     *              just look at it
     * @return ResultValue object that contains the final result of execution
     * @throws Exception generic Exception type to handle any processing errors
     */
    public ResultValue ifStmt(int iExec) throws Exception
    {
        ResultValue resCond;

        // do we need to evaluate the condition
        if (iExec == EXECUTING)
        {// we are executing, not ignoring
            // evaluate expression
            resCond = expression(false);

            // did the condition return true?
            if (resCond.value.equals("T"))
            {// condition returned true, execute statements on the true part
                resCond = statements(EXECUTING, "endif else");

                // what ended the statements after the true part? else of endif
                if (resCond.terminatingStr.equals("else"))
                {// has an else
                    if (! scan.getNext().equals(":"))
                        error("ERROR: EXPECTED ':' AFTER ELSE");

                    resCond = statements(IGNORING, "endif");
                }
            }
            else if (resCond.value.equals("F"))
            {// condition returned false, ignore all statements after the if
                resCond = statements(IGNORING, "endif else");

                // check for else
                if (resCond.terminatingStr.equals("else"))
                { // if it is an 'else', execute
                    if (! scan.getNext().equals(":"))
                        error("ERROR: EXPECTED ':' AFTER ELSE");

                    resCond = statements(EXECUTING, "endif");
                }
            }
            else
                // resCond value was not a boolean so it is an error
                error("ERROR: EXPECTED BOOLEAN FOR IF STATEMENT " +
                        "BUT FOUND %s", scan.currentToken.tokenStr);
        }
        else
        {// we are ignoring execution, so ignore conditional, true and false part
            // ignore conditional
            skipTo("if", ":");

            // ignore true part
            resCond = statements(IGNORING, "endif else");

            // if the statements terminated with an 'else', we need to parse statements
            if (resCond.terminatingStr.equals("else"))
            { // it is an else, so we need to skip statements
                if (! scan.getNext().equals(":"))
                    error("ERROR: EXPECTED ':' AFTER ELSE");

                // ignore false part
                resCond = statements(IGNORING, "endif");
            }
        }

        // did we have an 'endif;'?
        if (!resCond.terminatingStr.equals("endif") || !scan.nextToken.tokenStr.equals(";"))
            error("ERROR: EXPECTED 'endif;' FOR 'if' EXPRESSION");

        return new ResultValue("", Token.END, ResultValue.primitive, ";");
    }

    /**
     * This method executes 'while' statements for HavaBol. It uses iExec and the
     * ResultValue object returned from calling expression in order to determine
     * if we keep executing.
     * <p>
     * If we are not executing, we will still look over the lines to catch errors
     *
     * @param iExec Tells the statement function whether we need to execute the code we find or
     *              just look at it
     * @return ResultValue object that contains the final result of execution
     * @throws Exception generic Exception type to handle any processing errors
     */
    public ResultValue whileStmt(int iExec) throws Exception
    {
        ResultValue resCond;

        // do we need to evaluate the condition
        if (iExec == EXECUTING)
        {// we are executing, not ignoring
            Token whileToken = scan.currentToken;

            // evaluate expression
            resCond = expression(false);

            while (resCond.value.equals("T"))
            {// did the condition return true?
                resCond = statements(EXECUTING, "endwhile");

                // did statements() end on an endwhile;?
                if (! resCond.terminatingStr.equals("endwhile") || !scan.nextToken.tokenStr.equals(";"))
                    error("ERROR: EXPECTED 'endwhile;' FOR 'while' EXPRESSION");

                // reset while loop token
                scan.setTo(whileToken);

                // check expression case
                resCond = expression(false);
            }

            // expr() returned false, so skip ahead to the end of the while
            resCond = statements(IGNORING, "endwhile");
        }
        else
        {// we are ignoring execution, so ignore conditional, true and false part
            // ignore conditional
            skipTo("while", ":");

            // ignore statements
            resCond = statements(IGNORING, "endwhile");
        }

        // did we have an endwhile;
        if (! resCond.terminatingStr.equals("endwhile") || !scan.nextToken.tokenStr.equals(";"))
            error("ERROR: EXPECTED 'endwhile;' FOR 'while' EXPRESSION");

        return new ResultValue("", Token.END, ResultValue.primitive, ";");
    }

    /**
     *
     * @param iExec
     * @return
     * @throws Exception
     */
    public ResultValue forStmt(int iExec) throws Exception
    {
        ResultValue resCond;

        // do we need to evaluate the condition?
        if (iExec == EXECUTING)
        {// we are executing, not ignoring
            Token forToken = scan.currentToken;

            // advance to the start of the control variable
            scan.getNext();

            // make sure that we have a control variable
            if (scan.currentToken.subClassif != Token.IDENTIFIER)
                error("ERROR: EXPECTED CONTROL VARIABLE BUT FOUND %s", scan.currentToken.tokenStr);

            // check look ahead token for the type of for loop
            switch (scan.nextToken.tokenStr)
            {// =, in, from, or error
                // counting for
                case "=":
                    // declare for loop variables
                    int cv, ev, iv;
                    String cvStr = scan.currentToken.tokenStr;

                    // check if we need to implicitly declare variable
                    if (storageManager.getEntry(cvStr) == null)
                        // didn't exist so we implicitly declare
                        storageManager.putEntry(cvStr, new ResultValue("", Token.INTEGER
                                , ResultValue.primitive, "to"));

                    // create int control variable
                    cv = Integer.parseInt(assignStmt(EXECUTING).value);

                    // make sure we have the required end variable for our counting for loop
                    if ( !scan.getNext().equals("to") )
                        error("ERROR: EXPECTED END VARIABLE BUT FOUND %s", scan.currentToken.tokenStr);

                    // create end variable
                    ev = Integer.parseInt(expression(false).value);

                    // check if we have an increment variable, default to 1
                    if ( scan.getNext().equals("by"))
                    {
                        iv = Integer.parseInt(expression(false).value);

                        // advance token to the expected ':'
                        scan.getNext();
                    }
                    else
                        iv = 1;

                    // make sure we end on an ':'
                    if ( !scan.currentToken.tokenStr.equals(":"))
                        error("ERROR: EXPECTED ':' AFTER FOR LOOP VARIABLES");

                    // execute counting for
                    for (int i = cv; i < ev; i += iv)
                    {
                        resCond = statements(EXECUTING, "endfor");

                        // did statements() end on an endfor;?
                        if (!resCond.terminatingStr.equals("endfor") || !scan.nextToken.tokenStr.equals(";"))
                            error("ERROR: EXPECTED 'endfor;' FOR 'for' EXPRESSION");

                        // update cv in storage manager
                        resCond = storageManager.getEntry(cvStr);
                        resCond.value = "" + (Integer.parseInt(resCond.value) + iv);
                        storageManager.putEntry(cvStr, resCond);

                        // set position back to the beginning of the for loop
                        scan.setTo(forToken);
                        skipTo("for", ":");
                    }
                    break;
                // for fuck in berto
                case "in":
                    // declare for loop variables
                    String item = scan.currentToken.tokenStr;
                    String object;

                    // advance to 'in' token
                    scan.getNext();

                    // make sure next token is an operand
                    if (scan.nextToken.primClassif != Token.OPERAND)
                        error("ERROR: EXPECTED VARIABLE BUT FOUND %s", scan.nextToken.tokenStr);

                    // evaluate iterable expression
                    resCond = expression(false);

                    // make sure we end on an ':'
                    if ( !scan.getNext().equals(":"))
                        error("ERROR: EXPECTED ':' AFTER FOR LOOP VARIABLES");

                    // make sure we have an appropriate iterable object (array or string)
                    if ( resCond.structure == ResultValue.fixedArray
                            ||resCond.structure == ResultValue.unboundedArray )
                    {// we are iterating through an array
                        // value should contain the array name in the case of an array
                        ResultArray array = (ResultArray)storageManager.getEntry(resCond.value);

                        // save the array list
                        ArrayList<ResultValue> arrayList = array.array;

                        // check if item was already declared in the scope
                        if ( storageManager.getEntry(item) == null )
                         // add item to storage manager as the array type
                            storageManager.putEntry(item, new ResultValue("", array.type
                                    , ResultValue.primitive, "in"));

                        for (ResultValue elem : arrayList)
                        {
                            if (elem == null)
                                continue;

                            // update cv in storage manager
                            resCond = storageManager.getEntry(item);
                            resCond.value = "" + elem.value;
                            storageManager.putEntry(item, resCond);
                            resCond = statements(EXECUTING, "endfor");

                            // did statements() end on an endfor;?
                            if( !resCond.terminatingStr.equals("endfor") ||
                                    !scan.nextToken.tokenStr.equals(";"))
                                error("ERROR: EXPECTED 'endfor;' FOR 'for' EXPRESSION");

                            // set position back to the beginning of the for loop
                            scan.setTo(forToken);
                            skipTo("for", ":");
                        }
                    }
                    else
                    {// we are iterating over a string or expression
                        object = resCond.value;

                        // add item to storage manager as a string
                        storageManager.putEntry(item, new ResultValue("", Token.STRING
                                , ResultValue.primitive, "in"));

                        for (char c : object.toCharArray())
                        {
                            // update cv in storage manager
                            resCond = storageManager.getEntry(item);
                            resCond.value = "" + c;
                            storageManager.putEntry(item, resCond);
                            resCond = statements(EXECUTING, "endfor");

                            // did statements() end on an endfor;?
                            if( !resCond.terminatingStr.equals("endfor") ||
                                    !scan.nextToken.tokenStr.equals(";"))
                                error("ERROR: EXPECTED 'endfor;' FOR 'for' EXPRESSION");

                            // set position back to the beginning of the for loop
                            scan.setTo(forToken);
                            skipTo("for", ":");
                        }
                    }
                    break;
                // for by delimiter
                case "from":
                    // declare for loop variables
                    String stringCV = scan.currentToken.tokenStr;
                    String string, delimiter;
                    String stringM[];

                    if (storageManager.getEntry(stringCV) != null)
                        // make sure string cv has not been already defined
                        error("ERROR: VARIABLE '%s' IS ALREADY DEFINED IN THE SCOPE", stringCV);

                    // advance to 'from' token
                    scan.getNext();

                    // make sure next token is an operand
                    if (scan.nextToken.primClassif != Token.OPERAND)
                        error("ERROR: EXPECTED VARIABLE BUT FOUND %s", scan.nextToken.tokenStr);

                    // save string value to iter on
                    string = expression(false).value;

                    if ( !scan.getNext().equals("by") )
                        // make sure we have our delimiter
                        error("ERROR: MISSING 'BY' SEPARATOR FOR DELIMITER");

                    // save delimiter
                    delimiter = expression(false).value;

                    if ( !scan.getNext().equals(":") )
                        // make sure we have our ending ':'
                        error("ERROR: MISSING ':' SEPARATOR AT END OF FOR LOOP DECLARATION");

                    // split string into an array with our delimiter
                    stringM = string.split(Pattern.quote(delimiter));

                    // add string control variable to storage manager as a string
                    storageManager.putEntry(stringCV, new ResultValue("", Token.STRING
                            , ResultValue.primitive, "from"));

                    // iterate through our split string
                    for (String s : stringM)
                    {
                        // update string cv in storage manager
                        resCond = storageManager.getEntry(stringCV);
                        resCond.value = "" + s;
                        storageManager.putEntry(stringCV, resCond);
                        resCond = statements(EXECUTING, "endfor");

                        // did statements() end on an endfor;?
                        if( !resCond.terminatingStr.equals("endfor") ||
                                !scan.nextToken.tokenStr.equals(";"))
                            error("ERROR: EXPECTED 'endfor;' FOR 'for' EXPRESSION");

                        // set position back to the beginning of the for loop
                        scan.setTo(forToken);
                        skipTo("for", ":");
                    }

                    break;
                // unrecognized for separator
                default:
                    error("ERROR: UNRECOGNIZED CONTROL SEPARATOR '%s'", scan.nextToken.tokenStr);
            }
        }
        else
            // we are ignoring execution, so control variables
            // ignore control variables
            skipTo("for", ":");

        // ignore statements
        resCond = statements(IGNORING, "endfor");

        // did we have an endfor;
        if (! resCond.terminatingStr.equals("endfor") || !scan.nextToken.tokenStr.equals(";"))
            error("ERROR: EXPECTED 'endfor;' FOR 'while' EXPRESSION");

        return new ResultValue("", Token.END, ResultValue.primitive, ";");
    }


    /*public ResultValue selectStmt(int iExec) throws Exception
    {
        ResultValue resCond;

        // do we need to evaluate the condition
        if (iExec == EXECUTING)
        {// we are executing, not ignoring
            // evaluate expression
            resCond = expression(false);

            // did the condition return true?
            if (resCond.value.equals("T"))
            {// condition returned true, execute statements on the true part
                resCond = statements(EXECUTING, "endif else");

                // what ended the statements after the true part? else of endif
                if (resCond.terminatingStr.equals("else"))
                {// has an else
                    if (! scan.getNext().equals(":"))
                        error("ERROR: EXPECTED ':' AFTER ELSE");

                    resCond = statements(IGNORING, "endif");
                }
            }
            else if (resCond.value.equals("F"))
            {// condition returned false, ignore all statements after the if
                resCond = statements(IGNORING, "endif else");

                // check for else
                if (resCond.terminatingStr.equals("else"))
                { // if it is an 'else', execute
                    if (! scan.getNext().equals(":"))
                        error("ERROR: EXPECTED ':' AFTER ELSE");

                    resCond = statements(EXECUTING, "endif");
                }
            }
            else
                // resCond value was not a boolean so it is an error
                error("ERROR: EXPECTED BOOLEAN FOR IF STATEMENT " +
                        "BUT FOUND %s", scan.currentToken.tokenStr);
        }
        else
        {// we are ignoring execution, so ignore conditional, true and false part
            // ignore conditional
            skipTo("if", ":");

            // ignore true part
            resCond = statements(IGNORING, "endif else");

            // if the statements terminated with an 'else', we need to parse statements
            if (resCond.terminatingStr.equals("else"))
            { // it is an else, so we need to skip statements
                if (! scan.getNext().equals(":"))
                    error("ERROR: EXPECTED ':' AFTER ELSE");

                // ignore false part
                resCond = statements(IGNORING, "endif");
            }
        }

        // did we have an 'endif;'?
        if (!resCond.terminatingStr.equals("endif") || !scan.nextToken.tokenStr.equals(";"))
            error("ERROR: EXPECTED 'endif;' FOR 'if' EXPRESSION");

        return new ResultValue("", Token.END, ResultValue.primitive, ";");
    }*/






    /**
     * This method is called by expression to get the result value of an encountered
     * built in function.
     * @param functionName name of built in function called
     * @param parameter parameter to pass into built in function
     * @return Result value of value returned by function
     * @throws Exception
     */
    private ResultValue builtInFuncs(Token functionName, ResultValue parameter) throws Exception
    {
        ResultValue res;
        String value = "";
        int type = Token.BUILTIN;

        if (functionName.tokenStr.equals("LENGTH"))
         {  // length function
            // get value of parameter
            // calculate length of given string
            value = "" + parameter.value.length();

            // set type to an int
            type = Token.INTEGER;

         //scan.getNext();
        }
        else if (functionName.tokenStr.equals("SPACES"))
        {
            // determine if string contains only spaces or is empty
            if (parameter.value.trim().length() == 0)
                value = "T";
            else
                value = "F";

            // set type to a boolean
            type = Token.BOOLEAN;
        }
        else if (functionName.tokenStr.equals("ELEM"))
        {

            // get value of parameter
            try
            {
                ResultArray array = (ResultArray) parameter;

                if (array == null)
                    error("ERROR: UNDECLARED ARRAY '%s' PASSED TO ELEM()", scan.currentToken.tokenStr);
                else if (array.structure != ResultValue.fixedArray)
                    error("ERROR: ELEM CAN ONLY OPERATE ON ARRAYS, PASSED '%s'", scan.currentToken.tokenStr);

                value = "" + array.iPopulatedLen;
                type = Token.INTEGER;
            }
            catch (Exception e)
            {
                error("INCORRECT PARAMETER FOR ELEM");
            }

            // make sure we only have one parameter
        }
        else if (functionName.tokenStr.equals("MAXELEM"))
        {

            // get value of parameter
            try
            {
                ResultArray array = (ResultArray) parameter;
                if (array == null)
                    error("ERROR: UNDECLARED ARRAY '%s' PASSED TO ELEM()"
                            , scan.currentToken.tokenStr);
                else if (array.structure != ResultValue.fixedArray)
                    error("ERROR: ELEM CAN ONLY OPERATE ON ARRAYS, PASSED '%s'"
                            , scan.currentToken.tokenStr);

                value = "" + array.iDeclaredLen;
                type = Token.INTEGER;
            }
            catch(Exception e)
            {
                error("INCORRECT PARAMETER FOR MAXELEM");
            }


        }
        return new ResultValue(value, type, ResultValue.primitive, scan.currentToken.tokenStr);
    }
    /**
     * This method is provided to Parser to execute HavaBol builtin and user
     * defined functions.
     * <p>
     * function uses iExec in order to determine if we are executing or skipping.
     *
     * @param iExec Tells the statement function whether we need to execute the code we find or
     *              just look at it
     * @return ResultValue object that contains the final result of execution
     * @throws Exception generic Exception type to handle any processing errors
     */
    private ResultValue function(int iExec) throws Exception
    {
        /**TODO
         * There is some redundancy involving built in functions right now
         * that I need to fix.
         */
        int type = Token.BUILTIN;
        Token funcName;
        ResultValue res;
        String value = "";

        switch (scan.currentToken.subClassif)
        {// determine if function is built in or user defined
            case Token.BUILTIN:
                // make sure the function is in correct syntax
                if (! scan.nextToken.tokenStr.equals("(") )
                    error("ERROR: '%s' FUNCTION IS MISSING SEPARATOR '('", scan.currentToken.tokenStr);

                // check if we are executing
                if (iExec == IGNORING)
                    skipTo(scan.currentToken.tokenStr, ")");
                // we are executing, determine function
                else if (scan.currentToken.tokenStr.equals("print"))
                {// print function
                    String printLine = "";
                    Token prevToken = null;
                    // begin building the output line created by the print
                    while ( !scan.currentToken.tokenStr.equals(";") )
                    {// expression will return on a ',' or ';', auto add space for a ','
                        printLine += expression(true).value + " ";
                        prevToken = scan.currentToken;
                        scan.getNext();


                        while ( scan.currentToken.tokenStr.equals(")") )
                            // print is not terminated by a ';'
                            //error("ERROR: PRINT FUNCTION IS MISSING TERMINATOR ';'");
                            scan.getNext();
                        //check to see if end of file, if it is, bad
                        if (scan.currentToken.primClassif == Token.EOF)
                            error("ERROR: MISSING ';'");
                    }

//                    // print out the line
                    if(!prevToken.tokenStr.equals(")") && scan.nextToken.primClassif != Token.EOF)
                        error("ERROR: FUNCTION MISSING ClOSING ')'");

                    System.out.println(printLine);
                }
                else if (scan.currentToken.tokenStr.equals("LENGTH"))
                {// length function
                    res = expression(false);
                    value = res.value;
                    // set type to an int
                    type = Token.INTEGER;
                }
                else if (scan.currentToken.tokenStr.equals("SPACES"))
                {
                    res = expression(false);
                    value = res.value;
                    // set type to a boolean
                    type = Token.BOOLEAN;
                }
                else if (scan.currentToken.tokenStr.equals("ELEM"))
                {
                    res = expression(false);
                    type = Token.INTEGER;
                    value = res.value;
                }
                else if (scan.currentToken.tokenStr.equals("MAXELEM"))
                {
                    value = expression(false).value;
                    type = Token.INTEGER;
                }
                break;
            case Token.USER:
                // do other shit later
                skipTo(scan.currentToken.tokenStr, ";");
                break;
            default:// should never hit this, otherwise MAJOR FUCK UP
                error("INTERNAL ERROR: %s NOT A RECOGNIZED FUNCTION"
                        , scan.currentToken.tokenStr);
        }

        // make sure we end on a ';'
        if ( !scan.nextToken.tokenStr.equals(";") && !scan.currentToken.tokenStr.equals(";"))
            error("ERROR: PRINT FUNCTION IS MISSING TERMINATOR ';'");

        return new ResultValue(value, type, ResultValue.primitive, scan.currentToken.tokenStr);
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

    /**
     * This method returns a Result value object based on the current token.
     * <p>
     * This method assumes that current token is the operand. If token is an identifier, getOperand
     * extracts the corresponding Result Value from the storage manager. Else it just returns a new Result Value
     * for the constant.
     * @return Result Value of the operand starting at the current token.
     * @throws Exception
     */
    public ResultValue getOperand() throws Exception
    {
        Token operand = scan.currentToken; // Operand
        ResultValue firstResValue;         // Result value of operand to return
        ResultValue index, index2 = null;
        // get result value of operand. If its an identifier, get it from the storage manager
        if(operand.subClassif == Token.IDENTIFIER)
            // if identifier get its result value
            firstResValue = storageManager.getEntry(operand.tokenStr);
        else
            // create a new result value object
            firstResValue = new ResultValue(operand.tokenStr, operand.subClassif);

        //Array and string handling
        if(scan.nextToken.tokenStr.equals("["))
        {   //Dealing with an array or string
            //Get object from storage manager
            int first, second;
            ResultValue arrayOrStr = storageManager.getEntry(scan.currentToken.tokenStr);
            if(arrayOrStr == null)
                error("ERROR: '%s' WAS NEVER INITIALIZED", scan.currentToken.tokenStr);

            String name = scan.currentToken.tokenStr;

            scan.getNext();
            if(scan.nextToken.tokenStr.equals("~"))
                index = new ResultValue("0", 1);
            else
                index = expression(false);

            if(scan.nextToken.tokenStr.equals("~"))
            {
                scan.getNext();
                if(scan.nextToken.tokenStr.equals("]"))
                    index2 = new ResultValue("-1", 1);
                else
                    index2 = expression(false);
            }

            scan.getNext();
            //If the structure is 2, it is an array
            if(arrayOrStr.structure > ResultValue.primitive)
            {
                if (index2 == null)
                {
                    //Get value of index of the array
                    ResultArray firstArrValue = (ResultArray) storageManager.getEntry(name);
                    firstResValue = firstArrValue.array.get((Integer.parseInt(Utilities.toInteger(this, index))));
                }
                else
                {
                    ResultArray firstArrValue = (ResultArray) storageManager.getEntry(name);
                    firstResValue = firstArrValue;
                }
                if(firstResValue == null)
                    error("ERROR: '%s[%s]' WAS NEVER INITIALIZED", name, index.value);
            }
            else
            {
                if(index2 == null)
                {
                    firstResValue = storageManager.getEntry(name);
                    String strVal = firstResValue.value;
                    if (index.value.equals("-1")) index.value = String.valueOf(firstResValue.value.length() - 1);
                    char newChar = strVal.charAt((Integer.parseInt(Utilities.toInteger(this, index))));
                    firstResValue = new ResultValue(String.valueOf(newChar), 1);
                }
                else
                {
                    firstResValue = storageManager.getEntry(name);
                    String strVal = firstResValue.value;
                    if (index2.value.equals("-1")) index2.value = String.valueOf(firstResValue.value.length());

                    strVal = strVal.substring((Integer.parseInt(Utilities.toInteger(this, index)))
                            , (Integer.parseInt(Utilities.toInteger(this, index2))));
                    firstResValue = new ResultValue(strVal, 1);
                }
            }
        }

        return firstResValue;
    }


    /**
     * This method recieves an operator as a token and checks it precedence.
     * Operators with higher precedence have lower int value. As precedence decreases, int value goes up.
     * @param operator
     * @return int value of precedence.
     */
    public int getPrecedence(Token operator, Boolean inStack)
    {   int precedence;
        switch(operator.tokenStr)
        {
            case "u-":
                precedence = 2;
                break;
            case "^":
                precedence = 3;
                if(inStack)
                    precedence = 4;
                break;
            case "*":
                precedence = 5;
                break;
            case "/":
                precedence = 5;
                break;
            case "+":
                precedence = 6;
                break;
            case "-":
                precedence = 6;
                break;
            case "#":
                precedence = 7;
                break;
            case "in":
            case "not in":
                precedence = 8;
                break;
            case"not":
                precedence = 9;
                break;
            case"and":
                precedence = 10;
                break;
            case"or":
                precedence = 10;
                break;
            default:
                precedence = 8;
        }

        return precedence;
    }

}
