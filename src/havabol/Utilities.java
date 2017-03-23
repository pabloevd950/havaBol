package havabol;


/**
 * Created  on 3/3/17.
 */
public class Utilities
{

    public static int addInt(int x, int y){
        return x+y;
    }

    public static double addFloat(double x, double y){
        return x+y;
    }

    public static ResultValue add(Parser parser, ResultValue firstOp, ResultValue secondOp) throws ParserException{
        ResultValue res = null;
        String temp;
        switch (firstOp.type){
            case Token.INTEGER:
                 temp = Utilities.toInteger(parser , secondOp);
                 int x = Integer.parseInt(firstOp.value);
                 int y = Integer.parseInt(temp);
                 int result = x + y;
                 res = new ResultValue(String.valueOf(result), firstOp.type);
                 break;
            case Token.FLOAT:
                temp = Utilities.toFloat(parser , secondOp);
                double x2 = Double.parseDouble(firstOp.value);
                double y2 = Double.parseDouble(temp);
                double result2 = x2 + y2;
                res = new ResultValue(String.valueOf(result2), firstOp.type);
                break;
            case Token.STRING:
                break;
            default:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "Can not add to variable of type \'" + firstOp.type + "\'"
                        , parser.scan.sourceFileNm);

        }
        return res;
    }

    public static int subInt(int x, int y){
        return x-y;
    }

    public static double subFloat(double x, double y){
        return x-y;
    }

    public static ResultValue sub(Parser parser, ResultValue firstOp, ResultValue secondOp)throws ParserException{
        ResultValue res = null;
        String temp;
        switch (firstOp.type){
            case Token.INTEGER:
                temp = Utilities.toInteger(parser , secondOp);
                int x = Integer.parseInt(firstOp.value);
                int y = Integer.parseInt(temp);
                int result = x - y;
                res = new ResultValue(String.valueOf(result), firstOp.type);
                break;
            case Token.FLOAT:
                temp = Utilities.toFloat(parser , secondOp);
                double x2 = Double.parseDouble(firstOp.value);
                double y2 = Double.parseDouble(temp);
                double result2 = x2 - y2;
                res = new ResultValue(String.valueOf(result2), firstOp.type);
                break;
            case Token.STRING:
                break;
            default:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "Can not subtract from variable of type \'" + firstOp.type + "\'"
                        , parser.scan.sourceFileNm);

        }
        return res;
    }

    public static int divInt(int x, int y){
        return x/y;
    }

    public static double divFloat(double x, double y){
        return x/y;
    }

    public static ResultValue div(Parser parser, ResultValue firstOp, ResultValue secondOp)throws ParserException{
        ResultValue res = null;
        String temp;
        switch (firstOp.type){
            case Token.INTEGER:
                temp = Utilities.toInteger(parser , secondOp);
                int x = Integer.parseInt(firstOp.value);
                int y = Integer.parseInt(temp);
                int result = x / y;
                res = new ResultValue(String.valueOf(result), firstOp.type);
                break;
            case Token.FLOAT:
                temp = Utilities.toFloat(parser , secondOp);
                double x2 = Double.parseDouble(firstOp.value);
                double y2 = Double.parseDouble(temp);
                double result2 = x2 / y2;
                res = new ResultValue(String.valueOf(result2), firstOp.type);
                break;
            case Token.STRING:
                break;
            default:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "Can not divide variable of type \'" + firstOp.type + "\'"
                        , parser.scan.sourceFileNm);

        }
        return res;
    }

    public static int mulInt(int x, int y){
        return x*y;
    }

    public static double mulFloat(double x, double y){
        return x*y;
    }

    public static ResultValue mul(Parser parser, ResultValue firstOp, ResultValue secondOp)throws ParserException{
        ResultValue res = null;
        String temp;
        switch (firstOp.type){
            case Token.INTEGER:
                temp = Utilities.toInteger(parser , secondOp);
                int x = Integer.parseInt(firstOp.value);
                int y = Integer.parseInt(temp);
                int result = x * y;
                res = new ResultValue(String.valueOf(result), firstOp.type);
                break;
            case Token.FLOAT:
                temp = Utilities.toFloat(parser , secondOp);
                double x2 = Double.parseDouble(firstOp.value);
                double y2 = Double.parseDouble(temp);
                double result2 = x2 * y2;
                res = new ResultValue(String.valueOf(result2), firstOp.type);
                break;
            case Token.STRING:
                break;
            default:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "Can not multiply variable of type \'" + firstOp.type + "\'"
                        , parser.scan.sourceFileNm);

        }
        return res;
    }

    public static double expInt(int x, int y){
        return Math.pow(x,y);
    }

    public static double expDouble(double x, double y){
        return Math.pow(x,y);
    }

    public static ResultValue exp(Parser parser, ResultValue firstOp, ResultValue secondOp)throws ParserException{
        ResultValue res = null;
        String temp;
        switch (firstOp.type){
            case Token.INTEGER:
                temp = Utilities.toInteger(parser , secondOp);
                int x = Integer.parseInt(firstOp.value);
                int y = Integer.parseInt(temp);
                int result = (int)Math.pow(x,  y);
                res = new ResultValue(String.valueOf(result), firstOp.type);
                break;
            case Token.FLOAT:
                temp = Utilities.toFloat(parser , secondOp);
                double x2 = Double.parseDouble(firstOp.value);
                double y2 = Double.parseDouble(temp);
                double result2 = Math.pow(x2,  y2);
                res = new ResultValue(String.valueOf(result2), firstOp.type);
                break;
            case Token.STRING:
                break;
            default:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "Can not exponentiate to variable of type \'" + firstOp.type + "\'"
                        , parser.scan.sourceFileNm);
        }
        return res;
    }

    /**
     * Evaluate a less than comparison on two ResultValues
     *
     * @param parser
     * @param operandOne
     * @param operandTwo
     * @return
     * @throws ParserException
     */
    public static ResultValue isLessThan(Parser parser, ResultValue operandOne, ResultValue operandTwo) throws ParserException
    {

        ResultValue res = new ResultValue(Token.BOOLEAN, -1);
        String temp;

        switch (operandOne.type)
        {
            case Token.INTEGER:
                temp = Utilities.toInteger(parser, operandTwo);
                int iOp1 = Integer.parseInt(operandOne.value);
                int iOp2 = Integer.parseInt(temp);
                if (iOp1 < iOp2)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.FLOAT:
                temp = Utilities.toFloat(parser, operandTwo);
                double fOp1 = Double.parseDouble(operandOne.value);
                double fOp2 = Double.parseDouble(temp);
                if (fOp1 < fOp2)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.STRING:
                int comResult = operandOne.value.compareTo(operandTwo.value);
                if (comResult < 0)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.BOOLEAN:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "Can not apply comparison '<' to type '" + Token.BOOLEAN + "'"
                        , parser.scan.sourceFileNm);
            case Token.DATE:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "DATE type not implemented '" + operandOne.value + "'"
                        , parser.scan.sourceFileNm);
            default:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "Unknown type '" + operandOne.type + "'"
                        , parser.scan.sourceFileNm);

        }
        return res;
    }


    /**
     * Evaluate a greater than comparison on two ResultValues
     *
     * @param parser
     * @param operandOne
     * @param operandTwo
     * @return
     * @throws ParserException
     */
    public static ResultValue isGreaterThan(Parser parser, ResultValue operandOne, ResultValue operandTwo) throws ParserException
    {

        ResultValue res = new ResultValue(Token.BOOLEAN, -1);
        String temp;

        switch (operandOne.type)
        {
            case Token.INTEGER:
                temp = Utilities.toInteger(parser, operandTwo);
                int iOp1 = Integer.parseInt(operandOne.value);
                int iOp2 = Integer.parseInt(temp);
                if (iOp1 > iOp2)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.FLOAT:
                temp = Utilities.toFloat(parser, operandTwo);
                double fOp1 = Double.parseDouble(operandOne.value);
                double fOp2 = Double.parseDouble(temp);
                if (fOp1 > fOp2)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.STRING:
                int comResult = operandOne.value.compareTo(operandTwo.value);
                if (comResult > 0)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.BOOLEAN:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "Can not apply comparison '>' to '" + Token.BOOLEAN + "'"
                        , parser.scan.sourceFileNm);
            case Token.DATE:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "DATE type not implemented '" + operandOne.value + "'"
                        , parser.scan.sourceFileNm);
            default:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "Unknown type '" + operandOne.type + "'"
                        , parser.scan.sourceFileNm);

        }
        return res;
    }

    /**
     * Evaluate a equal to comparison on two ResultValues
     *
     * @param parser
     * @param operandOne
     * @param operandTwo
     * @return
     * @throws ParserException
     */
    public static ResultValue isEqual(Parser parser, ResultValue operandOne, ResultValue operandTwo) throws ParserException
    {

        ResultValue res = new ResultValue(Token.BOOLEAN, -1);
        String temp;

        switch (operandOne.type)
        {
            case Token.INTEGER:
                temp = Utilities.toInteger(parser, operandTwo);
                int iOp1 = Integer.parseInt(operandOne.value);
                int iOp2 = Integer.parseInt(temp);
                if (iOp1 == iOp2)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.FLOAT:
                temp = Utilities.toFloat(parser, operandTwo);
                double fOp1 = Double.parseDouble(operandOne.value);
                double fOp2 = Double.parseDouble(temp);
                if (fOp1 == fOp2)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.BOOLEAN: // In this case bool is the same as string
            case Token.STRING:
                int comResult = operandOne.value.compareTo(operandTwo.value);
                if (comResult == 0)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.DATE:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "DATE type not implemented '" + operandOne.value + "'"
                        , parser.scan.sourceFileNm);
            default:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "Unknown type '" + operandOne.type + "'"
                        , parser.scan.sourceFileNm);

        }
        return res;
    }

    /**
     * Evaluate a less than or equal to comparison on two ResultValues
     *
     * @param parser
     * @param operandOne
     * @param operandTwo
     * @return
     * @throws ParserException
     */
    public static ResultValue isLessThanorEq(Parser parser, ResultValue operandOne, ResultValue operandTwo) throws ParserException
    {

        ResultValue res = new ResultValue(Token.BOOLEAN, -1);
        String temp;

        switch (operandOne.type)
        {
            case Token.INTEGER:
                temp = Utilities.toInteger(parser, operandTwo);
                int iOp1 = Integer.parseInt(operandOne.value);
                int iOp2 = Integer.parseInt(temp);
                if (iOp1 <= iOp2)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.FLOAT:
                temp = Utilities.toFloat(parser, operandTwo);
                double fOp1 = Double.parseDouble(operandOne.value);
                double fOp2 = Double.parseDouble(temp);
                if (fOp1 <= fOp2)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.STRING:
                int comResult = operandOne.value.compareTo(operandTwo.value);
                if (comResult <= 0)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.BOOLEAN:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "Can not apply comparison '<=' to type '" + Token.BOOLEAN + "'"
                        , parser.scan.sourceFileNm);
            case Token.DATE:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "DATE type not implemented '" + operandOne.value + "'"
                        , parser.scan.sourceFileNm);
            default:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "Unknown type '" + operandOne.type + "'"
                        , parser.scan.sourceFileNm);

        }
        return res;
    }

    /**
     * Evaluate a less than or equal to comparison on two ResultValues
     *
     * @param parser
     * @param operandOne
     * @param operandTwo
     * @return
     * @throws ParserException
     */
    public static ResultValue isGreaterThanorEq(Parser parser, ResultValue operandOne, ResultValue operandTwo) throws ParserException
    {

        ResultValue res = new ResultValue(Token.BOOLEAN, -1);
        String temp;

        switch (operandOne.type)
        {
            case Token.INTEGER:
                temp = Utilities.toInteger(parser, operandTwo);
                int iOp1 = Integer.parseInt(operandOne.value);
                int iOp2 = Integer.parseInt(temp);
                if (iOp1 >= iOp2)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.FLOAT:
                temp = Utilities.toFloat(parser, operandTwo);
                double fOp1 = Double.parseDouble(operandOne.value);
                double fOp2 = Double.parseDouble(temp);
                if (fOp1 >= fOp2)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.STRING:
                int comResult = operandOne.value.compareTo(operandTwo.value);
                if (comResult >= 0)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.BOOLEAN:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "Can not apply comparison '>=' to type '" + Token.BOOLEAN + "'"
                        , parser.scan.sourceFileNm);
            case Token.DATE:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "DATE type not implemented '" + operandOne.value + "'"
                        , parser.scan.sourceFileNm);
            default:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "Unknown type '" + operandOne.type + "'"
                        , parser.scan.sourceFileNm);

        }
        return res;
    }

    /**
     * Evaluate a less than or equal to comparison on two ResultValues
     *
     * @param parser
     * @param operandOne
     * @param operandTwo
     * @return
     * @throws ParserException
     */
    public static ResultValue notEqalTo(Parser parser, ResultValue operandOne, ResultValue operandTwo) throws ParserException
    {

        ResultValue res = new ResultValue(Token.BOOLEAN, -1);
        String temp;

        switch (operandOne.type)
        {
            case Token.INTEGER:
                temp = Utilities.toInteger(parser, operandTwo);
                int iOp1 = Integer.parseInt(operandOne.value);
                int iOp2 = Integer.parseInt(temp);
                if (iOp1 != iOp2)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.FLOAT:
                temp = Utilities.toFloat(parser, operandTwo);
                double fOp1 = Double.parseDouble(operandOne.value);
                double fOp2 = Double.parseDouble(temp);
                if (fOp1 != fOp2)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.BOOLEAN: // In this case bool is the same as string
            case Token.STRING:
                int comResult = operandOne.value.compareTo(operandTwo.value);
                if (comResult != 0)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.DATE:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "DATE type not implemented '" + operandOne.value + "'"
                        , parser.scan.sourceFileNm);
            default:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "Unknown type '" + operandOne.type + "'"
                        , parser.scan.sourceFileNm);

        }
        return res;
    }

    /**
     * coerces a value into a Bool type and throws an exception if it can not be coerced.
     * <p>
     *
     * @param parser - Used for error generation
     * @param value - ResultValue to be coerced
     * @return
     * @throws ParserException - Value can not be parsed as a Bool
     */
    private static String toBoolean(Parser parser, ResultValue value) throws ParserException
    {

        if (value.value.equals("T"))
        {
            return "T";
        }
        else if (value.value.equals("F"))
        {
            return "F";
        }
        else
        {
            throw new ParserException(parser.scan.iSourceLineNr
                    , "Can not parse '" + value.value + "' as Bool"
                    , parser.scan.sourceFileNm);
        }
    }

    /**
     * coerces a value into a Float type and throws an exception if it can not be coerced.
     * <p>
     *
     * @param parser -  Used for error generation
     * @param value - ResultValue to be coerced
     * @return
     * @throws ParserException - Value can not be parsed as a Float
     */
    private static String toFloat(Parser parser, ResultValue value) throws ParserException
    {
        try {
            Double.parseDouble(value.value);
            return value.value;
        } catch (Exception e)
        {
            // Do nothing
        }
        throw new ParserException(parser.scan.iSourceLineNr
                , "Can not parse '" + value.value + "' as Int"
                , parser.scan.sourceFileNm);
    }

    /**
     * coerces a value into a Int type and throws an exception if it can not be coerced.
     * <p>
     *
     * @param parser -  Used for error generation
     * @param value - ResultValue to be coerced
     * @return
     * @throws ParserException - Value can not be parsed as a Int
     */
    public static String toInteger(Parser parser, ResultValue value) throws ParserException
    {
        int temp;
        try {
            Integer.parseInt(value.value);
            return value.value;
        } catch (Exception e)
        {
            // Do nothing
        }
        try {
            temp = (int)Double.parseDouble(value.value);
            return Integer.toString(temp);
        } catch (Exception e)
        {
            // Do nothing
        }
        throw new ParserException(parser.scan.iSourceLineNr
                , "Can not parse '" + value.value + "' as Int"
                , parser.scan.sourceFileNm);
    }

    public static String toNegative(Parser parser, ResultValue value){

        switch (value.type)
        {
            case Token.INTEGER:
                int x = Integer.parseInt(value.value);
                x*= -1;
                value.value = String.valueOf(x);
                break;
            case Token.FLOAT:
                double y = Double.parseDouble(value.value);
                y = y * -1;
                value.value = String.valueOf(y);
            case Token.STRING:
                //Check if its a float or int represented as string?
            case Token.BOOLEAN:
        }
        return value.value;

    }


}
