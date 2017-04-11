package havabol;

import java.util.ArrayList;

/**
 * Created by cyr471 on 4/10/17.
 */
public class ResultArray extends ResultValue
{
    ArrayList<ResultValue> array;
    int iPopulatedLen, iDeclaredLen, iNegSub, type;
    String name;

    public ResultArray(String name, ArrayList array, int type, int structure, int iPopulatedLen, int iDeclaredLen, int iNegSub)
    {
        super(name, type, structure, "");
        this.name = name;
        this.array = array;
        this.type = type;
        this.iPopulatedLen = iPopulatedLen;
        this.iDeclaredLen = iDeclaredLen;
        this.iNegSub = iNegSub;
    }
    public ResultArray(String name, int type, int structure)
    {
        super(name, type, structure, "");
        this.name = name;
        this.type = type;
        this.structure = structure;
    }
}
