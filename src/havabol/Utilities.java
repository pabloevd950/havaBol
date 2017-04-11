package havabol;


/* Utilities class that will handle all basic operations for the havabol programming language.
 * This class will deal with ResultValue objects and return every value as a string representation.
 */
public class Utilities
{
    /**
     * This method is included in order to add two values
     * <p>
     * Method can add Int, floats, and strings.
     *
     * @param parser Parser object which we will reference for errors
     * @param firstOp first operand we will perform operation on
     * @param secondOp second operand we will perform operation on
     * @return ResultValue object which contains the result and type of the operation
     * @throws ParserException generic Exception type to handle any processing errors
     */
    public static ResultValue add(Parser parser, ResultValue firstOp, ResultValue secondOp) throws Exception
    {
        ResultValue res = null;
        String temp;

        switch (firstOp.type)
        {
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
                double d3, d4, result3;
                int i3, i4, result4;

                if (firstOp.value.contains(".")) 
                {

                    d3 = Double.parseDouble(Utilities.toFloat(parser, firstOp));
                    d4 = Double.parseDouble(Utilities.toFloat(parser, secondOp));
                    result3 = d3 + d4;
                    res = new ResultValue(String.valueOf(result3), firstOp.type);
                }
                else 
                {
                    i3 = Integer.parseInt(Utilities.toInteger(parser , firstOp));
                    i4 = Integer.parseInt(Utilities.toInteger(parser , secondOp));
                    result4 = i3 + i4;
                    res = new ResultValue(String.valueOf(result4), firstOp.type);
                }
                break;
            default:
                parser.error("ERROR: CANNOT ADD %s TO %s", secondOp.type, firstOp.type);
        }
        return res;
    }

    /**
     * This method is included in order to subtract two values
     * <p>
     * Method can subtract ints and floats.
     *
     * @param parser Parser object which we will reference for errors
     * @param firstOp first operand we will perform operation on
     * @param secondOp second operand we will perform operation on
     * @return ResultValue object which contains the result and type of the operation
     * @throws ParserException generic Exception type to handle any processing errors
     */
    public static ResultValue sub(Parser parser, ResultValue firstOp, ResultValue secondOp) throws Exception
    {
        ResultValue res = null;
        String temp;
        switch (firstOp.type)
        {
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
                double d3, d4, result3;
                int i3, i4, result4;

                if (firstOp.value.contains("."))
                {

                    d3 = Double.parseDouble(Utilities.toFloat(parser, firstOp));
                    d4 = Double.parseDouble(Utilities.toFloat(parser, secondOp));
                    result3 = d3 - d4;
                    res = new ResultValue(String.valueOf(result3), firstOp.type);
                }
                else
                {
                    i3 = Integer.parseInt(Utilities.toInteger(parser , firstOp));
                    i4 = Integer.parseInt(Utilities.toInteger(parser , secondOp));
                    result4 = i3 - i4;
                    res = new ResultValue(String.valueOf(result4), firstOp.type);
                }
                break;
            default:
                parser.error("ERROR: CANNOT SUBTRACT %s FROM %s", secondOp.type, firstOp.type);
        }
        return res;
    }

    /**
     * This method is included in order to divide two values
     * <p>
     * Method can divide Int and floats.
     *
     * @param parser Parser object which we will reference for errors
     * @param firstOp first operand we will perform operation on
     * @param secondOp second operand we will perform operation on
     * @return ResultValue object which contains the result and type of the operation
     * @throws ParserException generic Exception type to handle any processing errors
     */
    public static ResultValue div(Parser parser, ResultValue firstOp, ResultValue secondOp) throws Exception
    {
        ResultValue res = null;
        String temp;
        switch (firstOp.type)
        {
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
                double d3, d4, result3;
                int i3, i4, result4;

                if (firstOp.value.contains("."))
                {

                    d3 = Double.parseDouble(Utilities.toFloat(parser, firstOp));
                    d4 = Double.parseDouble(Utilities.toFloat(parser, secondOp));
                    result3 = d3 / d4;
                    res = new ResultValue(String.valueOf(result3), firstOp.type);
                }
                else
                {
                    i3 = Integer.parseInt(Utilities.toInteger(parser , firstOp));
                    i4 = Integer.parseInt(Utilities.toInteger(parser , secondOp));
                    result4 = i3 / i4;
                    res = new ResultValue(String.valueOf(result4), firstOp.type);
                }
                break;
            default:
                parser.error("ERROR: CANNOT DIVIDE %s WITH %s", secondOp.type, firstOp.type);
        }
        return res;
    }

    /**
     * This method is included in order to multiply two values
     * <p>
     * Method can multiply Int and floats.
     *
     * @param parser Parser object which we will reference for errors
     * @param firstOp first operand we will perform operation on
     * @param secondOp second operand we will perform operation on
     * @return ResultValue object which contains the result and type of the operation
     * @throws ParserException generic Exception type to handle any processing errors
     */
    public static ResultValue mul(Parser parser, ResultValue firstOp, ResultValue secondOp) throws Exception
    {
        ResultValue res = null;
        ResultValue resTemp = res;
        String temp;
        int x;
       // System.out.println(firstOp.value + " " + secondOp.value);
        switch (firstOp.type)
        {
            case Token.INTEGER:
                temp = Utilities.toInteger(parser , secondOp);

                if(firstOp.type == Token.FLOAT)
                {
                    Double.parseDouble(firstOp.value);
                     x = Integer.parseInt(firstOp.value);
                }
                else
                     x = Integer.parseInt(firstOp.value);

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
                double d3, d4, result3;
                int i3, i4, result4;

                if (firstOp.value.contains("."))
                {

                    d3 = Double.parseDouble(Utilities.toFloat(parser, firstOp));
                    d4 = Double.parseDouble(Utilities.toFloat(parser, secondOp));
                    result3 = d3 * d4;
                    res = new ResultValue(String.valueOf(result3), firstOp.type);
                }
                else
                {
                    i3 = Integer.parseInt(Utilities.toInteger(parser , firstOp));
                    i4 = Integer.parseInt(Utilities.toInteger(parser , secondOp));
                    result4 = i3 * i4;
                    res = new ResultValue(String.valueOf(result4), firstOp.type);
                }
                break;
            default:
                parser.error("ERROR: CANNOT MULTIPLY %s WITH %s", secondOp.type, firstOp.type);
        }
        return res;
    }

    /**
     * This method is included in order to exponent two values
     * <p>
     * Method can exponent Int and floats.
     *
     * @param parser Parser object which we will reference for errors
     * @param firstOp first operand we will perform operation on
     * @param secondOp second operand we will perform operation on
     * @return ResultValue object which contains the result and type of the operation
     * @throws ParserException generic Exception type to handle any processing errors
     */
    public static ResultValue exp(Parser parser, ResultValue firstOp, ResultValue secondOp)throws Exception
    {
        ResultValue res = null;
        String temp;
        switch (firstOp.type)
        {
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
                double d3, d4, result3;
                int i3, i4, result4;

                if (firstOp.value.contains("."))
                {

                    d3 = Double.parseDouble(Utilities.toFloat(parser, firstOp));
                    d4 = Double.parseDouble(Utilities.toFloat(parser, secondOp));
                    result3 = Math.pow(d3, d4);
                    res = new ResultValue(String.valueOf(result3), firstOp.type);
                }
                else
                {
                    i3 = Integer.parseInt(Utilities.toInteger(parser , firstOp));
                    i4 = Integer.parseInt(Utilities.toInteger(parser , secondOp));
                    result4 = (int)Math.pow(i3, i4);
                    res = new ResultValue(String.valueOf(result4), firstOp.type);
                }
                break;
            default:
                parser.error("ERROR: CANNOT RAISE %s TO %s", secondOp.type, firstOp.type);
        }
        return res;
    }

    /**
     * This method is included in order to evaluate a less than comparison
     * on two ResultValues
     * <p>
     * Method can compare int, floats, booleans, and strings.
     *
     * @param parser Parser object which we will reference for errors
     * @param firstOP first operand we will perform operation on
     * @param secondOP second operand we will perform operation on
     * @return ResultValue object which contains the result and type of the operation
     * @throws ParserException generic Exception type to handle any processing errors
     */
    public static ResultValue isLessThan(Parser parser, ResultValue firstOP, ResultValue secondOP)
                                                                                        throws Exception
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
                parser.error("ERROR: UNKNOWN TYPE %s", firstOP.type);
        }
        return res;
    }

    /**
     * This method is included in order to evaluate a greater than comparison
     * on two ResultValues
     * <p>
     * Method can compare int, floats, booleans, and strings.
     *
     * @param parser Parser object which we will reference for errors
     * @param firstOP first operand we will perform operation on
     * @param secondOp second operand we will perform operation on
     * @return ResultValue object which contains the result and type of the operation
     * @throws ParserException generic Exception type to handle any processing errors
     */
    public static ResultValue isGreaterThan(Parser parser, ResultValue firstOP, ResultValue secondOp)
                                                                                    throws Exception
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
                parser.error("ERROR: UNKNOWN TYPE %s", firstOP.type);
        }
        return res;
    }

    /**
     * This method is included in order to evaluate an equal comparison
     * on two ResultValues
     * <p>
     * Method can compare int, floats, booleans, and strings.
     *
     * @param parser Parser object which we will reference for errors
     * @param firstOp first operand we will perform operation on
     * @param secondOp second operand we will perform operation on
     * @return ResultValue object which contains the result and type of the operation
     * @throws ParserException generic Exception type to handle any processing errors
     */
    public static ResultValue isEqual(Parser parser, ResultValue firstOp, ResultValue secondOp)
                                                                                    throws Exception
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
                parser.error("ERROR: UNKNOWN TYPE %s", firstOp.type);
        }
        return res;
    }

    /**
     * This method is included in order to evaluate a less than or equal comparison
     * on two ResultValues
     * <p>
     * Method can compare int, floats, booleans, and strings.
     *
     * @param parser Parser object which we will reference for errors
     * @param firstOp first operand we will perform operation on
     * @param secondOp second operand we will perform operation on
     * @return ResultValue object which contains the result and type of the operation
     * @throws ParserException generic Exception type to handle any processing errors
     */
    public static ResultValue isLessThanorEq(Parser parser, ResultValue firstOp, ResultValue secondOp)
                                                                                        throws Exception
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
                parser.error("ERROR: UNKNOWN TYPE %s", firstOp.type);
        }
        return res;
    }

    /**
     * This method is included in order to evaluate a greater than or equal comparison
     * on two ResultValues
     * <p>
     * Method can compare int, floats, booleans, and strings.
     *
     * @param parser Parser object which we will reference for errors
     * @param firstOp first operand we will perform operation on
     * @param secondOp second operand we will perform operation on
     * @return ResultValue object which contains the result and type of the operation
     * @throws ParserException generic Exception type to handle any processing errors
     */
    public static ResultValue isGreaterThanorEq(Parser parser, ResultValue firstOp, ResultValue secondOp)
                                                                                        throws Exception
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
                parser.error("ERROR: UNKNOWN TYPE %s", firstOp.type);
        }
        return res;
    }

    /**
     * This method is included in order to determine if two ResultValues
     * are not equal.
     * <p>
     * Method can compare int, floats, booleans, and strings.
     *
     * @param parser Parser object which we will reference for errors
     * @param firstOp first operand we will perform operation on
     * @param secondOp second operand we will perform operation on
     * @return ResultValue object which contains the result and type of the operation
     * @throws ParserException generic Exception type to handle any processing errors
     */
    public static ResultValue notEqualTo(Parser parser, ResultValue firstOp, ResultValue secondOp)
                                                                                            throws Exception
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
                parser.error("ERROR: UNKNOWN TYPE %s", firstOp.type);
        }
        return res;
    }

    /**
     * This method coerces a value into a Bool type.
     * <p>
     * Throws an exception if it can not be coerced.
     *
     * @param parser Used for error generation
     * @param value ResultValue to be coerced
     * @return Boolean string value
     * @throws ParserException - Value can not be parsed as a Bool
     */
    public static String toBoolean(Parser parser, ResultValue value) throws Exception
    {
        if (value.value.equals("T"))
            return "T";
        else if (value.value.equals("F"))
            return "F";
        else
            parser.error("ERROR: CANNOT COERCE %s AS BOOL", value.value);

        return null;
    }

    /**
     * This method coerces a value into a Float type.
     * <p>
     * Throws an exception if it can not be coerced.
     *
     * @param parser -  Used for error generation
     * @param value - ResultValue to be coerced
     * @return string representation of a Double
     * @throws ParserException - Value can not be parsed as a Float
     */
    public static String toFloat(Parser parser, ResultValue value) throws Exception
    {
        double temp;

        try
        {
            temp = Double.parseDouble(value.value);
            return Double.toString(temp);
        }
        catch (Exception e)
        {
            // Do nothing
        }
        parser.error("ERROR: CANNOT COERCE %s AS FLOAT", value.value);

        return null;
    }

    /**
     * This method coerces a value into a Int type
     * <p>
     * Throws an exception if it can not be coerced.
     *
     * @param parser -  Used for error generation
     * @param value - ResultValue to be coerced
     * @return string representation of an Int
     * @throws ParserException - Value can not be parsed as a Int
     */
    public static String toInteger(Parser parser, ResultValue value) throws Exception
    {
        int temp;

        try
        {
            Integer.parseInt(value.value);
            return value.value;
        }
        catch (Exception e)
        {
            // Do nothing
        }

        try
        {
            temp = (int)Double.parseDouble(value.value);
            return Integer.toString(temp);
        }
        catch (Exception e)
        {
            // Do nothing
        }

        parser.error("ERROR: CANNOT COERCE %s AS INT", value.value);
        return null;
    }

    /**
     * This method coerces a value into a negative version
     * <p>
     *
     * @param parser -  Used for error generation
     * @param value - ResultValue to be coerced
     * @return string representation of the value
     */
    public static String toNegative(Parser parser, ResultValue value)
    {
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
        }

        return value.value;
    }

    public static ResultValue concatenate(Parser parser, ResultValue left, ResultValue right)
    {
        /*// left operand must be a string
        if ( left.type != Token.STRING)
            parser.error("ERROR: CANNOT CONCATENATE VARIABLE OF TYPE %s, STRING EXPECTED", left.type);*/

        ResultValue res = new ResultValue(Token.STRING, ResultValue.primitive);
        res.value = left.value + right.value;
        return res;
    }
}