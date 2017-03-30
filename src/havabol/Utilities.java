package havabol;


/**
 * Created  on 3/3/17.
 */
public class Utilities
{

    /**
     *
     * @param parser
     * @param firstOp
     * @param secondOp
     * @return
     * @throws ParserException
     */
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
                        , "Can not add to variable of type \'" + secondOp.type + "\' to variable of type \'" + firstOp.type + "\'"
                        , parser.scan.sourceFileNm);

        }
        return res;
    }

    /**
     *
     * @param parser
     * @param firstOp
     * @param secondOp
     * @return
     * @throws ParserException
     */
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
                        , "Can not subtract \'" + secondOp.type + "\' from variable of type \'" + firstOp.type + "\'"
                        , parser.scan.sourceFileNm);

        }
        return res;
    }

    /**
     *
     * @param parser
     * @param firstOp
     * @param secondOp
     * @return
     * @throws ParserException
     */
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
                        , "Can not divide variable of type \'" + firstOp.type + "\' by type \'" + secondOp.type + "\'"
                        , parser.scan.sourceFileNm);

        }
        return res;
    }



    public static ResultValue mul(Parser parser, ResultValue firstOp, ResultValue secondOp)throws ParserException{
        ResultValue res = null;
        ResultValue resTemp = res;
        String temp;
        int x;
        switch (firstOp.type){
            case Token.INTEGER:
                temp = Utilities.toInteger(parser , secondOp);
                System.out.println(firstOp.value);
                System.out.println(parser.scan.iSourceLineNr);
                if(firstOp.type == Token.FLOAT){
                    Double.parseDouble(firstOp.value);
                     x = Integer.parseInt(firstOp.value);
                }
                else
                     x = Integer.parseInt(firstOp.value);
//                int x = Integer.parseInt(toInteger(parser, resTemp));
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
                        , "Can not multiply variable of type \'" + firstOp.type + "\' by variable of type\'" + secondOp.type + "\'"
                        , parser.scan.sourceFileNm);

        }
        return res;
    }

    /**
     *
     * @param parser
     * @param firstOp
     * @param secondOp
     * @return
     * @throws ParserException
     */
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
     * @param firstOP
     * @param secondOP
     * @return
     * @throws ParserException
     */
    public static ResultValue isLessThan(Parser parser, ResultValue firstOP, ResultValue secondOP) throws ParserException
    {

        ResultValue res = new ResultValue(Token.BOOLEAN, -1);
        String temp;

        switch (firstOP.type)
        {
            case Token.INTEGER:
                temp = Utilities.toInteger(parser, secondOP);
                int iOp1 = Integer.parseInt(firstOP.value);
                int iOp2 = Integer.parseInt(temp);
                if (iOp1 < iOp2)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.FLOAT:
                temp = Utilities.toFloat(parser, secondOP);
                double fOp1 = Double.parseDouble(firstOP.value);
                double fOp2 = Double.parseDouble(temp);
                if (fOp1 < fOp2)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.STRING:
                int comResult = firstOP.value.compareTo(secondOP.value);
                if (comResult < 0)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.BOOLEAN:
                break;
            case Token.DATE:
                break;
            default:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "Unknown type '" + firstOP.type + "'"
                        , parser.scan.sourceFileNm);

        }
        return res;
    }


    /**
     * Evaluate a greater than comparison on two ResultValues
     *
     * @param parser
     * @param firstOP
     * @param secondOp
     * @return
     * @throws ParserException
     */
    public static ResultValue isGreaterThan(Parser parser, ResultValue firstOP, ResultValue secondOp) throws ParserException
    {

        ResultValue res = new ResultValue(Token.BOOLEAN, -1);
        String temp;

        switch (firstOP.type)
        {
            case Token.INTEGER:
                temp = Utilities.toInteger(parser, secondOp);
                int iOp1 = Integer.parseInt(firstOP.value);
                int iOp2 = Integer.parseInt(temp);
                if (iOp1 > iOp2)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.FLOAT:
                temp = Utilities.toFloat(parser, secondOp);
                double fOp1 = Double.parseDouble(firstOP.value);
                double fOp2 = Double.parseDouble(temp);
                if (fOp1 > fOp2)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.STRING:
                int comResult = firstOP.value.compareTo(secondOp.value);
                if (comResult > 0)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.BOOLEAN:
                break;
            case Token.DATE:
                break;
            default:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "Unknown type '" + firstOP.type + "'"
                        , parser.scan.sourceFileNm);

        }
        return res;
    }

    /**
     * Evaluate a equal to comparison on two ResultValues
     *
     * @param parser
     * @param firstOp
     * @param secondOp
     * @return
     * @throws ParserException
     */
    public static ResultValue isEqual(Parser parser, ResultValue firstOp, ResultValue secondOp) throws ParserException
    {

        ResultValue res = new ResultValue(Token.BOOLEAN, -1);
        String temp;

        switch (firstOp.type)
        {
            case Token.INTEGER:
                temp = Utilities.toInteger(parser, secondOp);
                int iOp1 = Integer.parseInt(firstOp.value);
                int iOp2 = Integer.parseInt(temp);
                if (iOp1 == iOp2)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.FLOAT:
                temp = Utilities.toFloat(parser, secondOp);
                double fOp1 = Double.parseDouble(firstOp.value);
                double fOp2 = Double.parseDouble(temp);
                if (fOp1 == fOp2)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.BOOLEAN:
            case Token.STRING:
                int comResult = firstOp.value.compareTo(secondOp.value);
                if (comResult == 0)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.DATE:
                break;
            default:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "Unknown type '" + firstOp.type + "'"
                        , parser.scan.sourceFileNm);

        }
        return res;
    }

    /**
     * Evaluate a less than or equal to comparison on two ResultValues
     *
     * @param parser
     * @param firstOp
     * @param secondOp
     * @return
     * @throws ParserException
     */
    public static ResultValue isLessThanorEq(Parser parser, ResultValue firstOp, ResultValue secondOp) throws ParserException
    {

        ResultValue res = new ResultValue(Token.BOOLEAN, -1);
        String temp;

        switch (firstOp.type)
        {
            case Token.INTEGER:
                temp = Utilities.toInteger(parser, secondOp);
                int iOp1 = Integer.parseInt(firstOp.value);
                int iOp2 = Integer.parseInt(temp);
                if (iOp1 <= iOp2)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.FLOAT:
                temp = Utilities.toFloat(parser, secondOp);
                double fOp1 = Double.parseDouble(firstOp.value);
                double fOp2 = Double.parseDouble(temp);
                if (fOp1 <= fOp2)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.STRING:
                int comResult = firstOp.value.compareTo(secondOp.value);
                if (comResult <= 0)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.BOOLEAN:
                break;
            case Token.DATE:
                break;
            default:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "Unknown type '" + firstOp.type + "'"
                        , parser.scan.sourceFileNm);

        }
        return res;
    }

    /**
     * Evaluate a less than or equal to comparison on two ResultValues
     *
     * @param parser
     * @param firstOp
     * @param secondOp
     * @return
     * @throws ParserException
     */
    public static ResultValue isGreaterThanorEq(Parser parser, ResultValue firstOp, ResultValue secondOp) throws ParserException
    {

        ResultValue res = new ResultValue(Token.BOOLEAN, -1);
        String temp;

        switch (firstOp.type)
        {
            case Token.INTEGER:
                temp = Utilities.toInteger(parser, secondOp);
                int iOp1 = Integer.parseInt(firstOp.value);
                int iOp2 = Integer.parseInt(temp);
                if (iOp1 >= iOp2)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.FLOAT:
                temp = Utilities.toFloat(parser, secondOp);
                double fOp1 = Double.parseDouble(firstOp.value);
                double fOp2 = Double.parseDouble(temp);
                if (fOp1 >= fOp2)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.STRING:
                int comResult = firstOp.value.compareTo(secondOp.value);
                if (comResult >= 0)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.BOOLEAN:
                break;
            case Token.DATE:
                break;
            default:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "Unknown type '" + firstOp.type + "'"
                        , parser.scan.sourceFileNm);

        }
        return res;
    }

    /**
     * Evaluate a less than or equal to comparison on two ResultValues
     *
     * @param parser
     * @param firstOp
     * @param secondOp
     * @return
     * @throws ParserException
     */
    public static ResultValue notEqalTo(Parser parser, ResultValue firstOp, ResultValue secondOp) throws ParserException
    {

        ResultValue res = new ResultValue(Token.BOOLEAN, -1);
        String temp;

        switch (firstOp.type)
        {
            case Token.INTEGER:
                temp = Utilities.toInteger(parser, secondOp);
                int iOp1 = Integer.parseInt(firstOp.value);
                int iOp2 = Integer.parseInt(temp);
                if (iOp1 != iOp2)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.FLOAT:
                temp = Utilities.toFloat(parser, secondOp);
                double fOp1 = Double.parseDouble(firstOp.value);
                double fOp2 = Double.parseDouble(temp);
                if (fOp1 != fOp2)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.BOOLEAN: // In this case bool is the same as string
            case Token.STRING:
                int comResult = firstOp.value.compareTo(secondOp.value);
                if (comResult != 0)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.DATE:
                break;
            default:
                throw new ParserException(parser.scan.iSourceLineNr
                        , "Unknown type '" + firstOp.type + "'"
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
    public static String toBoolean(Parser parser, ResultValue value) throws ParserException
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
    public static String toFloat(Parser parser, ResultValue value) throws ParserException
    {
        double temp;
        try {
            temp = (int)Double.parseDouble(value.value);
            return Double.toString(temp);
            //value.value = Double.parseDouble(value.value);
            //System.out.println(Double.parseDouble(value.value) + "IN toFloat");
            //System.out.println(value.value);
            //return value.value;
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
                break;
            case Token.STRING:
                break;
                //Check if its a float or int represented as string?
        }
        return value.value;

    }


}
