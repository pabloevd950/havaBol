package havabol;

import java.util.ArrayList;

/**
 * Created by cyr471 on 4/10/17.
 */
public class ResultArray extends ResultValue implements Cloneable
{
    ArrayList<ResultValue> array;
    int iPopulatedLen=0, iDeclaredLen=-1, iNegSub=0, type;
    String name;

    public ResultArray(String name, ArrayList array, int type, int structure, int iPopulatedLen, int iDeclaredLen, int iNegSub)
    {
        super(name, type, structure, ";");
        this.name = name;
        this.array = array;
        this.type = type;
        this.iPopulatedLen = iPopulatedLen;
        this.iDeclaredLen = iDeclaredLen;
        this.iNegSub = iNegSub;
    }
    public ResultArray(String name, int type, int structure)
    {
        super(name, type, structure, ";");
        this.name = name;
        this.type = type;
        this.structure = structure;
    }
    public ResultArray(String value, int type, int structure , String terminatingStr)
    {
        super(value, type, structure, terminatingStr);
    }
    public ResultArray(ArrayList array, int type)
    {
        this.array = array;
        this.type = type;

    }

    public ResultArray clone() throws CloneNotSupportedException {
        ResultArray res = (ResultArray) super.clone();
        return res;
    }
}
