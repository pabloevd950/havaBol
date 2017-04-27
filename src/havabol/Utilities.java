package havabol;


import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
                parser.error("ERROR: CANNOT ADD '%s' AND '%s'", firstOp.value, secondOp.value);
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
                parser.error("ERROR: CANNOT SUBTRACT '%s' AND '%s'", firstOp.value, secondOp.value);
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
                parser.error("ERROR: CANNOT DIVIDE '%s' BY '%s'", firstOp.value, secondOp.value);
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
                parser.error("ERROR: CANNOT MULTIPLY '%s' WITH '%s'", firstOp.value, secondOp.value);
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
                parser.error("ERROR: CANNOT RAISE '%s' TO '%s'", firstOp.value, secondOp.value);
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
                parser.error("ERROR: CAN NOT PERFORM '<' COMPARISON ON 'Bool' TYPE");
                break;
            case Token.DATE:
                //make sure second is a valid date
                temp = Utilities.toDate(parser, secondOP);
                //if equal/greater, true
                if (firstOP.value.compareTo(temp) < 0)
                    res.value = "T";
                    //return false otherwise
                else
                    res.value = "F";
                break;
            default:
                parser.error("ERROR: UNKNOWN TYPE '%d' ONLY TYPES 2-6 ARE ALLOWED", firstOP.type);
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
            case Token.BOOLEAN:
                parser.error("ERROR: CAN NOT PERFORM '>' COMPARISON ON 'Bool' TYPE");
                break;
            case Token.STRING:
                int comResult = firstOP.value.compareTo(secondOp.value);
                if (comResult > 0)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.DATE:
                //make sure second is a valid date
                temp = Utilities.toDate(parser, secondOp);
                //if greater, true
                if (firstOP.value.compareTo(temp) > 0)
                    res.value = "T";
                //return false otherwise
                else
                    res.value = "F";
                break;
            default:
                parser.error("ERROR: UNKNOWN TYPE '%d' ONLY TYPES 2-6 ARE ALLOWED", firstOP.type);
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
                //make sure second is a valid date
                temp = Utilities.toDate(parser, secondOp);
                //if equal, true
                if (firstOp.value.compareTo(temp) == 0)
                    res.value = "T";
                //return false otherwise
                else
                    res.value = "F";
                break;
            default:
                parser.error("ERROR: UNKNOWN TYPE '%d' ONLY TYPES 2-6 ARE ALLOWED", firstOp.type);
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
            case Token.BOOLEAN:
                parser.error("ERROR: CAN NOT PERFORM '<=' COMPARISON ON 'Bool' TYPE");
                break;
            case Token.STRING:
                int comResult = firstOp.value.compareTo(secondOp.value);
                if (comResult <= 0)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.DATE:
                //make sure second is a valid date
                temp = Utilities.toDate(parser, secondOp);
                //if less/equal, true
                if (firstOp.value.compareTo(temp) <= 0)
                    res.value = "T";
                    //return false otherwise
                else
                    res.value = "F";
                break;
            default:
                parser.error("ERROR: UNKNOWN TYPE '%d' ONLY TYPES 2-6 ARE ALLOWED", firstOp.type);
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
            case Token.BOOLEAN:
                parser.error("ERROR: CAN NOT PERFORM '>=' COMPARISON ON 'Bool' TYPE");
                break;
            case Token.STRING:
                int comResult = firstOp.value.compareTo(secondOp.value);
                if (comResult >= 0)
                    res.value = "T";
                else
                    res.value = "F";
                break;
            case Token.DATE:
                //make sure second is a valid date
                temp = Utilities.toDate(parser, secondOp);
                //if equal/greater, true
                if (firstOp.value.compareTo(temp) >= 0)
                    res.value = "T";
                    //return false otherwise
                else
                    res.value = "F";
                break;
            default:
                parser.error("ERROR: UNKNOWN TYPE '%d' ONLY TYPES 2-6 ARE ALLOWED", firstOp.type);
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
                //make sure second is a valid date
                temp = Utilities.toDate(parser, secondOp);
                //if not equal, true
                if (firstOp.value.compareTo(temp) != 0)
                    res.value = "T";
                //return false otherwise
                else
                    res.value = "F";
                break;
            default:
                parser.error("ERROR: UNKNOWN TYPE '%d' ONLY TYPES 2-6 ARE ALLOWED", firstOp.type);
        }
        return res;
    }

    /**
     *
     * @param parser
     * @param item
     * @param list
     * @return
     * @throws Exception
     */
    public static ResultValue in(Parser parser, ResultValue item, ResultArray list) throws Exception
    {
        ResultValue res = new ResultValue(Token.BOOLEAN, -1);
        String temp;

        res.value = "F";
        switch (item.type)
        {
            case Token.INTEGER:
                for (ResultValue element : list.array)
                {
                    if (element == null)
                        continue;

                    temp = Utilities.toInteger(parser, element);
                    int i = Integer.parseInt(item.value);
                    int e = Integer.parseInt(temp);

                    if (i == e)
                    {// value in list, so logical operator is false
                        res.value = "T";
                        break;
                    }
                }
                break;
            case Token.FLOAT:
                for (ResultValue element : list.array)
                {
                    if (element == null)
                        continue;

                    temp = Utilities.toFloat(parser, element);
                    double i = Double.parseDouble(item.value);
                    double e = Double.parseDouble(temp);

                    if (i == e)
                    {// value in list, so logical operator is false
                        res.value = "T";
                        break;
                    }
                }
                break;
            case Token.DATE: //in this case, date is the same as string
            case Token.BOOLEAN: // in this case, bool is the same as string
            case Token.STRING:
                for (ResultValue element : list.array)
                    if ( item.value.compareTo(element.value) == 0 )
                    {
                        if (element == null)
                            continue;

                        res.value = "T";
                        break;
                    }
                break;
            default:
                parser.error("ERROR: UNKNOWN TYPE '%d' ONLY TYPES 2-6 ARE ALLOWED", item.type);
        }
        return res;
    }

    /**
     *
     * @param parser
     * @param item
     * @param list
     * @return
     * @throws Exception
     */
    public static ResultValue notin(Parser parser, ResultValue item, ResultArray list) throws Exception
    {
        ResultValue res = new ResultValue(Token.BOOLEAN, -1);
        String temp;

        res.value = "T";
        switch (item.type)
        {
            case Token.INTEGER:
                for (ResultValue element : list.array)
                {
                    if (element == null)
                        continue;

                    temp = Utilities.toInteger(parser, element);
                    int i = Integer.parseInt(item.value);
                    int e = Integer.parseInt(temp);

                    if (i == e)
                    {// value in list, so logical operator is false
                        res.value = "F";
                        break;
                    }
                }
                break;
            case Token.FLOAT:
                for (ResultValue element : list.array)
                {
                    if (element == null)
                        continue;

                    temp = Utilities.toFloat(parser, element);
                    double i = Double.parseDouble(item.value);
                    double e = Double.parseDouble(temp);

                    if (i == e)
                    {// value in list, so logical operator is false
                        res.value = "F";
                        break;
                    }
                }
                break;
            case Token.DATE:    // in this case, date is the same as string
            case Token.BOOLEAN: // in this case, bool is the same as string
            case Token.STRING:
                for (ResultValue element : list.array)
                    if ( item.value.compareTo(element.value) == 0 )
                    {
                        if (element == null)
                            continue;

                        res.value = "F";
                        break;
                    }
                break;
            default:
                parser.error("ERROR: UNKNOWN TYPE '%d' ONLY TYPES 2-6 ARE ALLOWED", item.type);
        }

        return res;
    }

    /**
     * This method will perform a not operation aka '!' on the given result value
     * <p>
     * Only will do this in expression
     * @param parser The caller added so that we can call error
     * @param expr   The expression that must be performed on
     * @return res   The result value last operated on
     * @throws Exception A generic error to specify what user did incorrectly
     */
    public static ResultValue not(Parser parser, ResultValue expr)
            throws Exception
    {
        ResultValue res = new ResultValue("", Token.BOOLEAN, ResultValue.primitive, ";");
        switch (expr.type)
        {
            case Token.BOOLEAN: // In this case bool is the same as string
            case Token.STRING:
                if (expr.value.equals("T"))
                    res.value = "F";
                else
                    res.value = "T";
                break;
            default:
                parser.error("ERROR: CANNOT COERCE '%s' TO BOOLEAN", expr.value);
        }
        return res;
    }

    /**
     * This method will perform an 'and' (&&) operation on the given result values
     * <p>
     * returns a Boolean of T or F for expression
     *
     * Only will do this in expression
     * @param parser The caller added so that we can call error
     * @param expr1   The first expression that must be performed on
     * @param expr2   The second expression that must be performed on
     * @return res   The result value last operated on
     * @throws Exception A generic error to specify what user did incorrectly
     */
    public static ResultValue and(Parser parser, ResultValue expr1, ResultValue expr2)
            throws Exception
    {
        ResultValue res = new ResultValue("", Token.BOOLEAN, ResultValue.primitive, ";");

        switch (expr1.type)
        {
            case Token.BOOLEAN: // In this case bool is the same as string
            case Token.STRING:
                expr2.value = toBoolean(parser, expr2);
                if (expr1.value.equals("T") && expr2.value.equals("T"))
                    res.value = "T";
                else
                    res.value = "F";
                break;
            default:
                parser.error("ERROR: CANNOT COERCE '%s' TO BOOLEAN", expr1.value);
        }
        return res;
    }

    /**
     * This method will perform an 'or; (||) operation on the given result values
     * <p>
     * Only will do this in expression
     *
     * @param parser The caller added so that we can call error
     * @param expr1   The first expression that must be performed on
     * @param expr2   The second expression that must be performed on
     * @return res   The result value last operated on
     * @throws Exception A generic error to specify what user did incorrectly
     */
    public static ResultValue or(Parser parser, ResultValue expr1, ResultValue expr2)
            throws Exception
    {
        ResultValue res = new ResultValue("", Token.BOOLEAN, ResultValue.primitive, ";");

        switch (expr1.type)
        {
            case Token.BOOLEAN: // In this case bool is the same as string
            case Token.STRING:
                expr2.value = toBoolean(parser, expr2);
                if (expr1.value.equals("T") || expr2.value.equals("T"))
                    res.value = "T";
                else
                    res.value = "F";
                break;
            default:
                parser.error("ERROR: CANNOT COERCE '%s' TO BOOLEAN", expr1.value);
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
            parser.error("ERROR: CANNOT COERCE '%s' AS BOOL", value.value);

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
        parser.error("ERROR: CANNOT COERCE '%s' AS FLOAT", value.value);

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

        parser.error("ERROR: CANNOT COERCE '%s' AS INT", value.value);
        return null;
    }

    /**
     * This method coerces a value into a Date type.
     * <p>
     * Throws an exception if it can not be coerced.
     *
     * @param parser Used for error generation
     * @param value ResultValue to be coerced
     * @return Date string value
     * @throws Exception - Value can not be parsed as a Date
     */
    public static String toDate(Parser parser, ResultValue value) throws Exception
    {
        if (value.value.matches("^[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]$") && validDate(parser, value))
            return value.value;
        else
            parser.error("ERROR: CANNOT COERCE %s AS DATE", value.value);
        return null;
    }

    /**
     * Validates the date
     * <p>
     * The month must be from 1-12. The day must be between 1 and the max for each month. If Feb 29 is specified,
     * validate that the year is a leap year.
     * </p>
     * @param parse Parser class used for error
     * @param check Date to be checked
     * @return True if the date is valid, false otherwise
     */
    public static boolean validDate(Parser parse, ResultValue check) throws Exception
    {
        int daysPerMonth[] =
                { 31, 29, 31
                  , 30, 31, 30
                  , 31, 31, 30
                  , 31, 30, 31 };

        int iYear = Integer.parseInt(check.value.substring(0, 4));
        int iMonth = Integer.parseInt(check.value.substring(5, 7));
        int iDay = Integer.parseInt(check.value.substring(8));

        // validate month
        if(iMonth < 1 || iMonth > 12)
            parse.error("ERROR: '%d' IS AN INVALID MONTH, MUST BE 1-12", iMonth);

        // validate day
        if(iDay < 1 || iDay > daysPerMonth[iMonth-1])
            parse.error("ERROR: '%d' IS AN INVALID DAY IN '%d', MUST BE 1-%d"
                    ,iDay, iMonth, daysPerMonth[iMonth-1]);

        // check for leap year
        if(iDay == 29 && iMonth == 2)
        {
            //divisible by 4, not divisbly by 400 or 100
            if(iYear % 4 == 0 && (iYear %100 != 0 || iYear % 400 == 0))
                return true;
            //not valid
            else
                parse.error("ERROR: '%d' IS/WAS/WILL NOT (BE) A LEAP YEAR", iYear);
        }

        return true;
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
        ResultValue res = new ResultValue(Token.STRING, ResultValue.primitive);
        res.value = left.value + right.value;
        return res;
    }

    /**
     * Validates the dates and returns the difference in days of the
     * first date minus the second date.
     * <p>
     * 1. We validate/convert the days to Date format.  If the date is invalid, we exit and show a message.
     * 2. For each of the dates, we determine the number of days since "0000-03-01" by starting the count at 1
     * for 0000-03-01. Using March 1st eliminates some leap day issues.
     * 3. Return the difference in days
     * @param parser the parser object useful for errors
     * @param date1 The first date/operand
     * @param date2 The second date/operand
     * @return a result value that contains an int that represents the days between them
     * @throws Exception general exception
     */
    public static ResultValue dateDiff(Parser parser, ResultValue date1, ResultValue date2) throws Exception
    {
        int iJulian1;
        int iJulian2;

        //validate date
        if (validDate(parser, date1) != true)
            parser.error("ERROR: '%s' IS AN INVALID DATE FORMAT ", date1.value);

        //validate date
        if (validDate(parser, date2) != true)
            parser.error("ERROR: '%s' IS AN INVALID DATE FORMAT ", date2.value);

        iJulian1 = DateToJulian(date1);
        iJulian2 = DateToJulian(date2);

        return new ResultValue("" + (iJulian1 - iJulian2), Token.DATE, ResultValue.primitive, ";");
    }

    /**
     * Validates the dates and returns the difference in years of the
     * first date minus the second date.
     * <p>
     * 1. We validate/convert the days to Date format.  If the date is invalid, we exit and show a message.
     * 2. For each of the dates, we determine the number of years since "0000-03-01" by starting the count at 1
     * for 0000-03-01. Using March 1st eliminates some leap day issues.
     * 3. Return the difference in years using integer division aka truncating
     * @param parser the parser object useful for errors
     * @param date1 The first date/operand
     * @param date2 The second date/operand
     * @return a result value that contains an int that represents the years between them
     * @throws Exception general exception
     */
    public static ResultValue dateAge(Parser parser, ResultValue date1, ResultValue date2) throws Exception
    {
        //parse through first getting year, month, and day
        int iYear = Integer.parseInt(date1.value.substring(0, 4));
        int iMonth = Integer.parseInt(date1.value.substring(5, 7));
        int iDay = Integer.parseInt(date1.value.substring(8));
        //parse through second getting year, month, and day
        int iYear2 = Integer.parseInt(date2.value.substring(0, 4));
        int iMonth2 = Integer.parseInt(date2.value.substring(5, 7));
        int iDay2 = Integer.parseInt(date2.value.substring(8));
        //return value
        int iDiff = iYear - iYear2;

        //validate date
        if (validDate(parser, date1) != true)
            parser.error("ERROR: '%s' IS AN INVALID DATE FORMAT ", date1.value);

        //validate date
        if (validDate(parser, date2) != true)
            parser.error("ERROR: '%s' IS AN INVALID DATE FORMAT ", date2.value);

        //check if months are equal
        if (iMonth2 == iMonth)
            //check if second day is less than
            if (iDay2 < iDay)
                //check if negative
                if (iDiff < 0)
                    iDiff++;
                //positive
                else
                    iDiff--;
        //months is before, so change
        else if (iMonth2 < iMonth)
            iDiff--;

        return new ResultValue("" + iDiff, Token.DATE, ResultValue.primitive, ";");
    }

    /**
     * Validates the dates and returns the difference in date format of the
     * first date minus specified days.
     * <p>
     * 1. We validate/convert the day to Date format.  If the date is invalid, we exit and show a message.
     * 2. For the date, we determine the number of days since "0000-03-01" by starting the count at 1
     * for 0000-03-01. Using March 1st eliminates some leap day issues.
     * 3. Return the difference in Date format
     * @param parser the parser object useful for errors
     * @param date The first date/operand
     * @param days The amount of dates to adjust
     * @return an int that represents the days between them
     * @throws Exception general exception
     */
    public static ResultValue dateAdj(Parser parser, ResultValue date, int days) throws Exception
    {
        //validate date
        if (validDate(parser, date) != true)
            parser.error("ERROR: '%s' IS AN INVALID DATE FORMAT ", date.value);

        //parse through getting year, month, and day
        int iYear = Integer.parseInt(date.value.substring(0, 4));
        int iMonth = Integer.parseInt(date.value.substring(5, 7));
        int iDay = Integer.parseInt(date.value.substring(8));

        //define format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //create calender
        Calendar calendar = new GregorianCalendar(iYear,iMonth-1,iDay);
        //add days
        calendar.add(Calendar.DAY_OF_MONTH, days);

        return new ResultValue(sdf.format(calendar.getTime()), Token.DATE, ResultValue.primitive, ";");
    }

    /**
     * Converts a date to a UTSA Julian Days value.  This will start numbering at 1 for 0000-03-01.
     * Making dates relaive to March 1st helps eliminate some leap day issues.
     * <p>
     * 1 We replace the month with the number of months since March.
     * March is 0, Apr is 1, May is 2, ..., Jan is 10, Feb is 11.
     * 2 Since Jan and Feb are before Mar, we subtract 1 from the year for those months.
     * 3 Jan 1 is 306 days from Mar 1.
     * 4 The days per month is in a pattern that begins with March and repeats every 5 months:
     * Mar 31 Aug 31 Jan 31
     * Apr 30 Sep 30
     * May 31 Oct 31
     * Jun 30 Nov 30
     * Jul 31 Dec 31
     * Therefore:
     * Mon  AdjMon  NumberDaysFromMarch (AdjMon*306 + 5)/10
     * Jan    10      306
     * Feb    11      337
     * Mar     0        0
     * Apr     1       31
     * May     2       61
     * Jun     3       92
     * Jul     4      122
     * Aug     5      153
     * Sep     6      184
     * Oct     7      214
     * Nov     8      245
     * Dec     9      275
     * 5 Leap years are
     * years that are divisible by 4 and
     * either years that are not divisible by 100 or
     * years that are divisible by 400
     * @param date The date being changed
     * @return the number of days since 0000-03-01 beginning with 1 for 0000-03-01.
     */
    public static int DateToJulian(ResultValue date)
    {
        int iCountDays;
        //parse through to get values
        int iYear = Integer.parseInt(date.value.substring(0, 4));
        int iMonth = Integer.parseInt(date.value.substring(5, 7));
        int iDay = Integer.parseInt(date.value.substring(8));
        // Calculate number of days since 0000-03-01

        // If month is March or greater, decrease it by 3.
        if (iMonth > 2)
            iMonth -= 3;
        else
        {
            iMonth += 9;  // adjust the month since we begin with March
            iYear--;      // subtract 1 from year if the month was Jan or Feb
        }
        iCountDays = 365 * iYear                                // 365 days in a year
                + iYear / 4 - iYear / 100 + iYear / 400   // add a day for each leap year
                + (iMonth * 306 + 5) / 10                           // see note 4
                + (iDay );                                          // add the days
        return iCountDays;
    }
}