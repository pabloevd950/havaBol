package havabol;

import java.util.ArrayList;

/**
 * Created by pablovalero on 3/3/17.
 */
public class ResultValue implements Cloneable
{
    // structure constants for ResultValue
    public static final int primitive = 1;
    public static final int fixedArray = 2;
    public static final int unboundedArray = 3;

    String value;
    int type;
    int structure;
    String terminatingStr;


    /**
     * Creates a new ResultValue object
     * <p>
     * This constructor initializes a resultValue object without a value
     *
     * @param value    The value to assign to the variable
     *                 NOTE: If it is an array, value is the name of it
     * @param type  declare type of variable
     * @param structure   type of data structure for the variable
     * @param terminatingStr ....
     * @return         ResultValue object
     */
    public ResultValue(String value, int type, int structure , String terminatingStr)
    {
        this.value = value;
        this.type = type;
        this.structure = structure;
        this.terminatingStr = terminatingStr;
    }


    /**
     * Creates a new ResultValue object
     * <p>
     * This constructor initializes a resultValue object without a value
     *
     * @param type  declare type of variable
     * @param structure   type of data structure for the variable
     * @return         ResultValue object
     */
    public ResultValue(int type, int structure)
    {
        this("", type, structure, "");
    }

    /**
     * Creates a new ResultValue object
     * <p>
     * This constructor initializes a resultValue object without a value
     *
     * @param value The value to assign to the variable
     * @param type declare type of variable
     */
    public ResultValue(String value, int type)
    {
        this(value, type, primitive, ";");
//        this.value = value;
//        this.type = type;
    }

    /**
     * Creates a new ResultValue object
     * <p>
     * This constructor initializes a resultValue object without a value
     *
     */
    public ResultValue()
    {
        this(-1, -1);
    }

    /**
     * Clones a ResultValue object
     * @return a clone of object
     * @throws CloneNotSupportedException
     */
    public ResultValue clone() throws CloneNotSupportedException {
        ResultValue res = (ResultValue) super.clone();
        return res;
    }
}
