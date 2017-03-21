package havabol;

/**
 * Created by pablovalero on 3/3/17.
 */
public class ResultValue
{

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
     * @param dclType  declare type of variable
     * @param struct   type of data structure for the variable
     * @return         ResultValue object
     */
    public ResultValue(int dclType, int struct)
    {
        this.type = dclType;
        this.stucture = struct;
    }

    /**
     * Creates a new ResultValue object
     * <p>
     * This constrctor initializes a resultValue object without a value
     * Mainly used by declareStmt();
     *
     * @param type  declare type of variable
     * @param structure   type of data structure for the variable
     * @param value    The value to assign to the variable
     * @param terminatingstr ....
     * @return         ResultValue object
     */
    public ResultValue Resultvalue(String value, int type, int structure, String terminatingstr){

        this.value = value;
        this.type = type;
        this.stucture = structure;
        this.terminatingStr = terminatingstr;

        return this;
    }

    public ResultValue ResultValue(){

        return null;
    }

}
