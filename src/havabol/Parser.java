package havabol;

import havabol.SymbolTable.STIdentifier;
import havabol.SymbolTable.SymbolTable;

import java.util.ArrayList;
import javax.lang.model.type.DeclaredType;
import javax.xml.transform.Result;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Pattern;

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
                        else if (scan.currentToken.tokenStr.equals("for"))
                            return forStmt(bExec);
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

        if (bExec)
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

                        symbolTable.putSymbol(variableStr, new STIdentifier(variableStr
                                , identifier.primClassif, dclType
                                , ResultValue.fixedArray, 1, 1));
                        //store into storagemanager
                        storageManager.putEntry(variableStr, new ResultArray(identifier.tokenStr, dclType, structure));

                        return declareArray(bExec, variableStr, dclType, 0);
                    }
                    //anything else is an error
                    else
                        error("ERROR:UNEXPECTED SYMBOL %s, EXPECTED EITHER ']' OR '='", scan.nextToken.tokenStr);
                }
                // size is declared
                else
                {
                    //do expression to find declared length
                    int length = Integer.parseInt(Utilities.toInteger(this, expression()));

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
                        //store in storagemanager
                        storageManager.putEntry(variableStr, new ResultArray(identifier.tokenStr, null, dclType, structure, 0, length, (length + 1) * -1));

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

                        return declareArray(bExec, variableStr, dclType, length);
                    }
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
            return assignStmt(bExec);
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
     * assume currentToken is '='
     * @param bExec
     * @param type
     * @param declared
     * @return
     * @throws Exception
     */
    public ResultArray declareArray(Boolean bExec, String variableStr, int type, int declared) throws Exception
    {
        //will add this into the arraylist
        ResultValue resExpr = new ResultValue(-1,-1);
        //to be returned
        ResultArray resArray;
        //the list that is set if expression is called
        ArrayList<ResultValue> expressionVals = new ArrayList<>();
        //will act as iPopulated
        int iAmt = 1;
        Boolean bFirst = true;
        // loop using expression, until ';' is found
        while(!resExpr.terminatingStr.equals(";") && !scan.currentToken.tokenStr.equals(";"))
        {
            // evaluate expression to receive values for arraylist
            resExpr = expression();

            //to fix pablo's shit
            if (bFirst == true)
            {
                //if it's the first one, need to do an additional getNext to realign
                scan.getNext();
                bFirst = false;
            }

            //increment amount populated and check to see if greater than declare
            if (declared > 0 && iAmt++ > declared)
                error("ERROR: CANNOT DECLARE MORE THAN '%d' INTO ARRAY '%s'", declared, variableStr);
            switch (type)
            {// determine the type of value to assign to ResultValue to add to array
                /*this may cause an error due to address sharing of objects
                * if that happens, then declare as new and then set with clone later aka in here :)
                * This may apply everywhere for arrays idk yet =D*/
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

        //create ResultArray to return
        resArray = new ResultArray(variableStr, expressionVals, type, ResultValue.fixedArray, iAmt, declared, (declared + 1) * 1);

        //check for debug to be on
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
        // returned value
        ResultValue res;
        //leftType is the data type, index is the index being assigned to, if given
        int leftType = -1, iIndex = 0;
        //name of the variable
        String variableStr;
        //flag to determine if assigning to index or entire array
        Boolean bIndex = false;
        //temporary resultarray object
        ResultArray resA;

        //if executing, then get its saved data type
        if (bExec)
            leftType = storageManager.getEntry(scan.currentToken.tokenStr).type;

        // make sure current token is an identifier to properly assign
        if (scan.currentToken.subClassif != Token.IDENTIFIER)
            error("ERROR: %s IS NOT A VALID TARGET VARIABLE FOR ASSIGNMENT"
                                                    , scan.currentToken.tokenStr);
        //save variable name
        variableStr = scan.currentToken.tokenStr;

        // pull storage manager entry of variable
        res = storageManager.getEntry(variableStr);

        //if the reference is not in symbol table while executing
        if(res == null && bExec)
            // undeclared variable while bExec true
            error("ERROR: ASSIGN REQUIRES THAT '%s' BE DECLARED", variableStr);

        //advance token to either '[' or operator i.e '=', '+=', etc.
        scan.getNext();

        //check to see if array index is given
        if (scan.currentToken.tokenStr.equals("["))
        {
            // arrays must call expression to determine array index
            iIndex = Integer.parseInt(Utilities.toInteger(this, expression()));
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
                if (bExec)
                {
                    // check to see if array or not
                    switch (res.structure)
                    {
                        case ResultValue.primitive:
                            //not an array, so do basic assign
                            ResultValue res1 = assign(variableStr, expression(), leftType);
                            return res1;
                        case ResultValue.fixedArray:
                            // this means that more than one index is being changed, so change array
                            if (bIndex == false)
                                resA = assignArray(variableStr, leftType, ((ResultArray)res).iDeclaredLen);
                            // this means only one index
                            else
                            {
                                //check to see if index requested is in bounds
                                //haven't handled negatives yet
                                if (iIndex >= ((ResultArray)res).iDeclaredLen)
                                    error("ERROR: '%d' IS OUT OF BOUNDS", iIndex);
                                resA = assignIndex(variableStr, leftType, iIndex);
                            }

                            return resA;
                        default:
                            error("ERROR: STRUCTURE TYPE '%d' IS NOT ALLOWED ON '%s'", res.structure, res.value);
                    }
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
     * variable string specified. The variable string will be the key.
     * <p>
     * Makes sure that the variable has been declared to a type already.
     *Assume only one value in value list, and that currentToken is '='
     * @param variableStr contains the string of the variable we are assigning a value to
     * @param type Token type which we will be assigning
     * @param declared The total amount allowed to add
     * @return ResultValue object that contains the final result of execution
     * @throws Exception generic Exception type to handle any processing errors
     */
    private ResultArray assignArray(String variableStr, int type, int declared) throws Exception
    {
        //will add this into the arraylist
        ResultValue resExpr = new ResultValue(-1,-1);
        //to be returned
        ResultArray resArray = new ResultArray(variableStr, -1, -1);

        //this is for operands aka scalar with variables and constants and array to array assignment
        if (scan.nextToken.subClassif == Token.OPERAND)
        {
            //iterate through for loop
            int i;
            //this is the array that we need to act on
            ResultArray array1 = (ResultArray)storageManager.getEntry(variableStr);
            //this is the value that we need to assign to
            ResultValue value2 = storageManager.getEntry(scan.nextToken.tokenStr);

            if ( array1 == null)
                // make sure item has been defined
                error("ERROR: VARIABLE '%s' IS NOT DEFINED IN THE SCOPE", variableStr);
            if ( value2 == null)
                // make sure item has been defined
                error("ERROR: VARIABLE '%s' IS NOT DEFINED IN THE SCOPE", scan.nextToken.tokenStr);

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
                    //check if debugger is on
                    if(scan.bShowAssign)
                        System.out.println("\t\t...Variable Name: " + variableStr + " Value: " + resExpr.value);
                }
                //create resulting array
                resArray = new ResultArray(variableStr, array1.array, type, ResultValue.fixedArray, array1.array.size(), declared, (declared+1)*1);
                //add into storagemanager
                storageManager.putEntry(variableStr, resArray);

                return resArray;

            }
            //it is an array
            else if (value2.structure == ResultValue.fixedArray)
            {
                //typecast into resultarray
                ResultArray array2 = (ResultArray)value2;
                //iFirst is declared length of first, iSecond is how much to change
                int iFirst = array1.iDeclaredLen, iSecond = array2.iPopulatedLen;

                //if the first's declared length is less than the amount to populate, truncate the populating amount
                if (iFirst < iSecond)
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
                            //set into array of first
                            array1.array.set(i, resExpr);
                            break;
                        case Token.FLOAT:
                            resExpr = array2.array.get(i);
                            resExpr.value = Utilities.toFloat(this, resExpr);
                            resExpr.type = Token.FLOAT;
                            //set into array of first
                            array1.array.set(i, resExpr);
                            break;
                        case Token.BOOLEAN:
                            resExpr = array2.array.get(i);
                            resExpr.value = Utilities.toBoolean(this, resExpr);
                            resExpr.type = Token.BOOLEAN;
                            //set into array of first
                            array1.array.set(i, resExpr);
                            break;
                        case Token.STRING:
                            resExpr = array2.array.get(i);
                            resExpr.type = Token.STRING;
                            //set into array of first
                            array1.array.set(i, resExpr);
                            break;
                        default:
                            error("ERROR: ASSIGN TYPE '%s' IS NOT A RECOGNIZED TYPE", variableStr);
                    }
                    if(scan.bShowAssign)
                        System.out.println("\t\t...Variable Name: " + variableStr + " Value: " + resExpr.value);
                }
                //create ResultArray to return
                resArray = new ResultArray(variableStr, array1.array, type, ResultValue.fixedArray, array1.array.size(), declared, (declared + 1) * 1);
                //add into storagemanager
                storageManager.putEntry(variableStr, resArray);

                return resArray;
            }


        }
        return resArray;
    }

    /**
     * Assume currentToken is on '='
     * @param variableStr
     * @param type
     * @param index
     * @return
     * @throws Exception
     */
    public ResultArray assignIndex(String variableStr, int type, int index) throws Exception
    {
        //will add this into the arraylist
        ResultValue resExpr = new ResultValue(-1,-1);
        //to be returned
        ResultArray resArray;

        //this is for operands aka variables and constants
        if (scan.nextToken.primClassif == Token.OPERAND)
        {
            //this is the array that we need to act on
            ResultArray array1 = (ResultArray)storageManager.getEntry(variableStr);
            //this is the value that we need to use to assign to index
            //if it is a variable, it should return its value, else will return value of expression
            ResultValue value2 = expression();

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
                    System.out.println("\t\t...Variable Name: " + variableStr + " Value: " + resExpr.value);
            }
            else
                error("ERROR: CANNOT ASSIGN STRUCTURE '%d' INTO AN INDEX", value2.structure);
            //create resulting array
            resArray = new ResultArray(variableStr, array1.array, type, ResultValue.fixedArray, array1.array.size()
                    , array1.iDeclaredLen, (array1.iDeclaredLen+1)*1);
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
    public ResultValue expression() throws Exception
    {
        // result value and operand stacks
        Stack outPutStack = new Stack<ResultValue>();
        Stack stack = new Stack<Token>();

        // operator and operand tokens
        Token poppedOperator, popped;
        Token operand;

        Token hashTag = new Token("#");
        hashTag.primClassif = Token.SEPARATOR;

        // result value for operands and final result
        ResultValue firstResValue, secondResValue, res;

        Boolean bFound; //Boolean to determine if we found left paren
        Boolean inFunc = false; //Boolean to signal if we are in an expression called from function
        Boolean bCategory = false; //Boolean to check proper infix notation
        Boolean moveForward = true; //Boolean used to control moving forward to next token
        Boolean semiFlag = false;


        //System.out.println("** " + scan.currentToken.tokenStr + "  Token at start of expression" + scan.iSourceLineNr);

        // check if this expression was called from function()
        if(scan.currentToken.primClassif == Token.FUNCTION || scan.currentToken.tokenStr.equals(","))
        {
            inFunc = true;
            Token paren = new Token("(");
            paren.primClassif = Token.SEPARATOR;
            stack.push(hashTag);
            //stack.push(paren);
        }

        // advance to start of expression
        if(!scan.currentToken.tokenStr.equals(","))
            scan.getNext();



//        System.out.println("** " + scan.currentToken.tokenStr + "  Token before start of while" + scan.iSourceLineNr);

        // control token used to check for unary minus
        Token prevToken = scan.currentToken;

        // loop through expression
        while(scan.currentToken.primClassif == Token.OPERAND // check if token is operand
                || scan.currentToken.primClassif == Token.OPERATOR // check if it is an operator
                || scan.currentToken.primClassif == Token.FUNCTION // check for functions
                || "()".contains(scan.currentToken.tokenStr)// check if its separator
                || (",".contains(scan.currentToken.tokenStr) && inFunc == true)//comma if we are in function
                || semiFlag == true)

        {
            //System.out.println(" ** " + scan.currentToken.tokenStr + " Token in while");

            // check token type
            switch (scan.currentToken.primClassif)
            {
                // token is an operand
                case Token.OPERAND:
                    if(bCategory == true)
                        // we encountered an unexpected operand, looking for an operator
                        error("ERROR: UNEXPECTED OPERAND '%s', EXPECTED OPERATOR."
                                , scan.currentToken.tokenStr);

                    // set operand equal to current token
                    operand = scan.currentToken;

                    // get result value of operand. If its an identifier, get it from the storage manager
                    if(operand.subClassif == Token.IDENTIFIER)
                        // if identifier get its result value
                        firstResValue = storageManager.getEntry(operand.tokenStr);
                    else
                        // create a new result value object
                        firstResValue = new ResultValue(operand.tokenStr, operand.subClassif);

                    if(scan.nextToken.tokenStr.equals("["))
                    {
                        String arrayNameStr = scan.currentToken.tokenStr;
                        scan.getNext();
                        ResultValue arrayIndex = expression();
                        scan.getNext();
                        ResultArray firstArrValue = (ResultArray) storageManager.getEntry(arrayNameStr);
                        firstResValue = firstArrValue.array.get((Integer.parseInt(Utilities.toInteger(this, arrayIndex))));
                    }
                    // check if the next operator is a unary minus
                    try
                    {
                        // take a look at the top of the stack, no pop
                        poppedOperator = (Token)stack.peek();

                        if(poppedOperator.tokenStr.equals("u-"))
                        {// top of the stack contained a unary minus
                            // pop it off the stack
                            stack.pop();

                            // negate the operand before we push it on the stack
                            firstResValue = Utilities.mul(this,
                                    new ResultValue("-1",Token.INTEGER), firstResValue);
                        }
                    }
                    catch (Exception e)
                    {
                        // catch empty stack exception and do nothing
                        // means this is our first operand, so there is no error
                    }
                    // push operand to the stack and signal that we now want an operator
                    if(scan.nextToken.tokenStr.equals(";")) {
                        if(!stack.empty())
                        {
                            Token test = (Token) stack.peek();
                            //System.out.println(test.tokenStr + " PEEKED");
                            if(test.tokenStr.equals("("))
                                semiFlag = true;

                        }
                        //stack.push(new Token(")"));
                    }
                    outPutStack.push(firstResValue);
                    bCategory = true;

                    break;
                // token is an operator
                case Token.OPERATOR:
                    if(bCategory == false && !scan.currentToken.tokenStr.equals("-"))
                        // we encountered an unexpected operator, looking for an operand
                        error("ERROR: UNEXPECTED OPERATOR '%s', EXPECTED OPERAND."
                                , scan.currentToken.tokenStr);

                    // determine operator, look for unary minus
                    switch (scan.currentToken.tokenStr)
                    {
                        // minus
                        case "-":
                            // check if the previous token was an operator.
                            if(prevToken.primClassif == Token.OPERATOR || prevToken.tokenStr.equals(","))
                            {// previous token was an operator, either want an operand or unary minus
                                //Check if next token is an operand or separator.
                                if(scan.nextToken.primClassif == Token.OPERAND || scan.nextToken.equals("("))
                                {
                                    // unary minus is true. Change the "-" to "u-"
                                    stack.push(new Token("u-"));

                                }

                                /*
                                we need to make sure that we are handling the case where parenthesis
                                are negated such as -(3). doesn't work right now
                                 */

                                //If it isn't, then we encountered an error
                                else
                                {
                                    //Throw exception
                                }
                                break;
                            }
                            // operator is not unary minus
                        default:
                            // loop through expression while the stack is not empty
                            while(!stack.empty())
                            {
                                // check precedence
                                if(getPrecedence(scan.currentToken) < getPrecedence((Token)stack.peek()))
                                    // precedence of current operator is higher, break
                                    break;
                                else if(!stack.empty())
                                {// stack is not empty and precedence is right, evaluate
                                    // pop operator
                                    poppedOperator = (Token)stack.pop();

                                    // pop last value
                                    firstResValue = (ResultValue) outPutStack.pop();

                                    // pop second value
                                    secondResValue = (ResultValue)outPutStack.pop();

                                    res = evaluate(secondResValue, firstResValue, poppedOperator.tokenStr);

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
                    // call function to get result value
                    outPutStack.push(function(true));

                    // make sure we don't skip over the next operator or terminator after function call
                    moveForward = true;

                    // functions are considered operands, so we are now looking for an operator
                    bCategory = true;
                    break;
                // handle separators
                case Token.SEPARATOR:
                    switch (scan.currentToken.tokenStr)
                    {// determine left or right paren

                        case "(":
                            stack.push(scan.currentToken);
                            break;
                        case ";":
                            semiFlag = false;
                            moveForward = false;
                        case ",":
                            if(stack.peek().equals(hashTag))
                            {
                                //Replace commas with parentheses

                                //Token used to determine when to return
                                Token commasymbol = new Token("#");
                                commasymbol.primClassif = Token.SEPARATOR;

                                //Token to simulate left paren
                                Token leftParen = new Token("(");
                                leftParen.primClassif = Token.SEPARATOR;

                                //Push these on to stack
                                stack.push(commasymbol);
                                stack.push(leftParen);
                                break;
                            }
                            else
                            {
                                //Comma will act as right paren
                                Token rightParen = new Token(")");
                                rightParen.primClassif = Token.SEPARATOR;
                                moveForward = false;
                                //if(prevToken.tokenStr.equals(")"))
                                //{
                                //    System.out.println("Might need to do something here");
                                //}
                                //stack.push(rightParen);
                                //inFunc = false;
                            }
                        case "]":

                        case ")":
                            // right parenthesis found, set flag false until we find matching left paren
                            bFound = false;

                            // loop through stack until matching left paren is found
                            while (!stack.empty())
                            {// stack is not empty and left paren not found, pop top of stack
                                popped = (Token)stack.pop();
                                //System.out.println(popped.tokenStr + " Popped **");
                                if (popped.tokenStr.equals("("))
                                {// left paren found, set flag to true and break
                                    bFound = true;

                                    if (inFunc == true)
                                    {// we are in a function call
                                        popped = (Token)stack.peek();
                                        if(popped.tokenStr.equals("#"))
                                        {   // next token contains our function symbol, leave
                                            res = (ResultValue)outPutStack.peek();
                                            //scan.getNext();
                                            /*if(scan.currentToken.tokenStr.equals(")"))
                                            {
                                                scan.getNext();
                                            }*/

                                            //System.out.println( "  **  " + scan.currentToken.tokenStr + " retrn from while");

                                            return res;
                                        }
                                    }

                                    // not in a function and left paren found, leave while loop
                                    break;
                                }
                                else
                                {// not a left paren, work with stack
                                    // get the first two operands for our operator
                                    firstResValue = (ResultValue)outPutStack.pop();
                                    secondResValue = (ResultValue)outPutStack.pop();

                                    // evaluate and push result back to stack
                                    res = evaluate(secondResValue, firstResValue, popped.tokenStr);
                                    outPutStack.push(res);
                                }
                            }
                            if (bFound == false)
                                // left paren was not encountered
                                //error("ERROR: EXPECTED LEFT PARENTHESIS");

                                break;
                    }
            }
            // set previous token to the current token
            prevToken = scan.currentToken;

            // advance token unless returned from function call
            if(moveForward)
                scan.getNext();

            moveForward = true;
        }

        // this should get the last result value
        while(!stack.empty())
        {
            poppedOperator = (Token)stack.pop();
            //System.out.println(scan.currentToken.tokenStr);
            if (poppedOperator.tokenStr.equals("(")) {
                // unmatched left parentesis
                System.out.println(scan.currentToken.tokenStr);
                error("ERROR: UNMATCHED LEFT PARENTHESIS FOR EXPRESSION");
            }
            else if (poppedOperator.tokenStr.equals("u-"))
                // we have unary minus
                outPutStack.push(evaluate(new ResultValue("-1", Token.INTEGER)
                        , (ResultValue) outPutStack.pop(), "*"));
            else
            {// evaluate normally
                ResultValue resvalue = (ResultValue) outPutStack.pop();
                ResultValue res2value = (ResultValue) outPutStack.pop();
                outPutStack.push(evaluate(res2value, resvalue, poppedOperator.tokenStr));
            }
        }

        // final value
        //scan.currentToken.printToken();
        res = (ResultValue) outPutStack.pop();

        if(scan.bShowExpr)
            // debug Expr on
            System.out.println("\t\t...Result Value: " + res.value);

        scan.setTo(prevToken);

        res.terminatingStr = scan.nextToken.tokenStr;
        //Return final result value

        //System.out.println(scan.currentToken.tokenStr + " retrn out of while  Value is" + res.value);

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
                //es = Utilities.mul(this, firstResValue, )
                break;
            case "#":
                res = Utilities.concatenate(this,firstResValue, secondResValue);
                break;
            default:
                error("ERROR: '%s' IS NOT A VALID OPERATOR FOR EXPRESSION", operator);
        }

        return res;
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

        if(operator.equals(";") || operator.equals(",") || operator.equals(")") || operator.equals("to")
                || operator.equals(":") || operator.equals("by") || operator.equals("]"))
        { // assign is done, so return the single value
            // check for debug on
            if(scan.bShowExpr)
                System.out.println("\t\t...Result Value: " + res.value);

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
            System.out.println("\t\t...Result Value: " + res.value);

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
            resCond = expression();

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
            else if (resCond.value.equals("F"))
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
            resCond = expression();

            while (resCond.value.equals("T"))
            {// did the condition return true?
                resCond = statements(true, "endwhile");

                // did statements() end on an endwhile;?
                if (! resCond.terminatingStr.equals("endwhile") || !scan.nextToken.tokenStr.equals(";"))
                    error("ERROR: EXPECTED 'endwhile;' FOR 'while' EXPRESSION");

                // reset while loop token
                scan.setTo(whileToken);

                // check expression case
                resCond = expression();
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

                    // create int control variable
                    cv = Integer.parseInt(assignStmt(true).value);

                    // make sure we have the required end variable for our counting for loop
                    if ( !scan.getNext().equals("to") )
                        error("ERROR: EXPECTED END VARIABLE BUT FOUND %s", scan.currentToken.tokenStr);

                    // create end variable
                    ev = Integer.parseInt(expression().value);

                    // check if we have an increment variable, default to 1
                    if ( scan.getNext().equals("by"))
                    {
                        iv = Integer.parseInt(expression().value);

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
                        resCond = statements(true, "endfor");

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

                    if (storageManager.getEntry(item) != null)
                        // make sure item has not been already defined
                        error("ERROR: VARIABLE '%s' IS ALREADY DEFINED IN THE SCOPE", item);

                    // advance to 'in' token
                    scan.getNext();

                    // make sure next token is an operand
                    if (scan.nextToken.primClassif != Token.OPERAND)
                        error("ERROR: EXPECTED VARIABLE BUT FOUND %s", scan.nextToken.tokenStr);

                    // evaluate iterable expression
                    resCond = expression();

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
                            resCond = statements(true, "endfor");

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
                            resCond = statements(true, "endfor");

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
                    string = expression().value;

                    if ( !scan.getNext().equals("by") )
                        // make sure we have our delimiter
                        error("ERROR: MISSING 'BY' SEPARATOR FOR DELIMITER");

                    // save delimiter
                    delimiter = expression().value;

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
                        resCond = statements(true, "endfor");

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
        resCond = statements(false, "endfor");

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
        int type = Token.BUILTIN;
        ResultValue res;
        String value = "";

        switch (scan.currentToken.subClassif)
        {// determine if function is built in or user defined
            case Token.BUILTIN:
                // make sure the function is in correct syntax
                if (! scan.nextToken.tokenStr.equals("(") )
                    error("ERROR: '%s' FUNCTION IS MISSING SEPARATOR '('", scan.currentToken.tokenStr);

                // check if we are executing
                if (bExec == false)
                    skipTo(scan.currentToken.tokenStr, ")");
                    // we are executing, determine function
                else if (scan.currentToken.tokenStr.equals("print"))
                {// print function
                    String printLine = "";

                    // begin building the output line created by the print
                    while ( !scan.currentToken.tokenStr.equals(";") )
                    {// expression will return on a ',' or ';', auto add space for a ','
                        printLine += expression().value + " ";

                        while ( scan.currentToken.tokenStr.equals(")") )
                            // print is not terminated by a ';'
                            //error("ERROR: PRINT FUNCTION IS MISSING TERMINATOR ';'");
                            scan.getNext();
                    }

                    // print out the line
                    System.out.println(printLine);
                }
                else if (scan.currentToken.tokenStr.equals("LENGTH"))
                {// length function
                    // get value of parameter
                    res = expression();

                    // make sure we only have one parameter
                    if (scan.currentToken.tokenStr.equals(","))
                        error("ERROR: EXPECTED ONLY ONE PARAMETER FOR LENGTH FUNCTION");

                    // calculate length of given string
                    value = "" + res.value.length();

                    // set type to an int
                    type = Token.INTEGER;

                    //scan.getNext();
                }
                else if (scan.currentToken.tokenStr.equals("SPACES"))
                {
                    //get value of parameter
                    res = expression();

                    // make sure we only have one parameter
                    if (scan.currentToken.tokenStr.equals(","))
                        error("ERROR: EXPECTED ONLY ONE PARAMETER FOR SPACES FUNCTION");

                    // determine if string contains only spaces or is empty
                    if (res.value.trim().length() == 0)
                        value = "T";
                    else
                        value = "F";

                    // set type to a boolean
                    type = Token.BOOLEAN;
                }
                else if (scan.currentToken.tokenStr.equals("ELEM"))
                {
                    // advance to our parameter
                    scan.getNext();
                    scan.getNext();

                    // get value of parameter
                    ResultArray array = (ResultArray)storageManager.getEntry(scan.currentToken.tokenStr);

                    if (array == null)
                        error("ERROR: UNDECLARED ARRAY '%s' PASSED TO ELEM()"
                                , scan.currentToken.tokenStr);
                    else if (array.structure != ResultValue.fixedArray)
                        error("ERROR: ELEM CAN ONLY OPERATE ON ARRAYS, PASSED '%s'"
                                , scan.currentToken.tokenStr);

                    value = "" + array.iPopulatedLen;
                    type = Token.INTEGER;

                    // make sure we only have one parameter
                    if (!scan.getNext().equals(")"))
                        error("ERROR: EXPECTED ONLY ONE PARAMETER FOR SPACES FUNCTION");

                    // advance past out right paren
                    //scan.getNext();
                }
                else if (scan.currentToken.tokenStr.equals("MAXELEM"))
                {
                    // advance to our parameter
                    scan.getNext();
                    scan.getNext();

                    // get value of parameter
                    ResultArray array = (ResultArray)storageManager.getEntry(scan.currentToken.tokenStr);

                    if (array == null)
                        error("ERROR: UNDECLARED ARRAY '%s' PASSED TO ELEM()"
                                , scan.currentToken.tokenStr);
                    else if (array.structure != ResultValue.fixedArray)
                        error("ERROR: ELEM CAN ONLY OPERATE ON ARRAYS, PASSED '%s'"
                                , scan.currentToken.tokenStr);

                    value = "" + array.iDeclaredLen;
                    type = Token.INTEGER;

                    // make sure we only have one parameter
                    if (!scan.getNext().equals(")"))
                        error("ERROR: EXPECTED ONLY ONE PARAMETER FOR SPACES FUNCTION");
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
        /*if ( !scan.getNext().equals(";") )
            error("ERROR: PRINT FUNCTION IS MISSING TERMINATOR ';'");*/

        return new ResultValue(value, type, ResultValue.primitive, scan.currentToken.tokenStr);
    }

    public ResultValue testFunc() throws Exception
    {
        //System.out.println("Entering testFunc with current token as " + scan.currentToken.tokenStr);
        int type = Token.BUILTIN;
        ResultValue res;
        String value = "";

        switch (scan.currentToken.subClassif)
        {// determine if function is built in or user defined
            case Token.BUILTIN:
                // make sure the function is in correct syntax
                if (! scan.nextToken.tokenStr.equals("(") )
                    error("ERROR: '%s' FUNCTION IS MISSING SEPARATOR '('", scan.currentToken.tokenStr);

                // determine function
                if (scan.currentToken.tokenStr.equals("LENGTH"))
                {// length function
                    // advance to our parameter
                    //scan.getNext();
                    //scan.getNext();
                    // get value of parameter
                    //System.out.println("Start");
                    res = expression();
                    //scan.currentToken.printToken();
                    //System.out.println("End");

                    // make sure we only have one parameter
                    //                    if (!scan.currentToken.tokenStr.equals(")"))
                    //                        error("ERROR: EXPECTED ONLY ONE PARAMETER FOR LENGTH FUNCTION");

                    // calculate length of given string
                    value = "" + res.value.length();
                    // set type to an int
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
        // if ( !scan.currentToken.tokenStr.equals(";") )
        //    error("ERROR: PRINT FUNCTION IS MISSING TERMINATOR ';'");

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
     * This method recieves an operator as a token and checks it precedence.
     * Operators with higher precedence have lower int value. As precedence decreases, int value goes up.
     * @param operator
     * @return int value of precedence.
     */
    public int getPrecedence(Token operator)
    {   int precedence;
        switch(operator.tokenStr)
        {
            case "u-":
                precedence = 0;
                break;
            case "^":
                precedence = 1;
                break;
            case "*":
                precedence = 2;
                break;
            case "/":
                precedence = 2;
                break;
            case "+":
                precedence = 3;
                break;
            case "-":
                precedence = 3;
                break;
            case "#":
                precedence = 4;
                break;
            default:
                precedence = 5;
        }

        return precedence;
    }

}
