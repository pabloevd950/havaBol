package havabol;

/**
 * Created by pablovalero on 3/3/17.
 */
public class ResultValue
{

    String value;
    String type;
    String stucture;
    String terminatingStr;

    public ResultValue Resultvalue(String value, String type, String structure, String terminatingstr){

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
