package havabol;

/**
 * Created by pablovalero on 3/3/17.
 */
public class ResultValue
{
    public final int primitive = 1;
    public final int fixedArray = 2;
    public final int unboundedArray = 3;

    String value;
    String terminatingStr;
    int stucture;
    int type;

    /**
     * Creates a new ResultValue object
     * <p>
     * This constrctor initializes a resultValue object without a value
     * Mainly used by declareStmt();
     *
     * @param type  declare type of variable
     * @param structure   type of data structure for the variable
     * @param value    The value to assign to the variable
     * @param terminatingStr ....
     * @return         ResultValue object
     */
    public ResultValue(String value, int type, int structure, String terminatingStr)
    {
        this.value = value;
        this.type = type;
        this.stucture = structure;
        this.terminatingStr = terminatingStr;
    }

    /**
     * Creates a new ResultValue object
     * <p>
     * This constrctor initializes a resultValue object without a value
     * Mainly used by declareStmt();
     *
     * @param type  declare type of variable
     * @param structure   type of data structure for the variable
     * @return         ResultValue object
     */
    public ResultValue(int type, int structure)
    {
        this("", type, structure, "");
    }

    public ResultValue()
    {
        this(-1, -1);
    }
}
