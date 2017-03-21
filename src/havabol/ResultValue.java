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



    public ResultValue(int dclType, int struct)
    {
        this.type = dclType;
        this.stucture = struct;
    }

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
