package havabol;

import java.util.ArrayList;

/**
 * Created by cyr471 on 4/10/17.
 */
public class ResultArray extends ResultValue
{
    ArrayList<ResultValue> array;
    int iPopulatedLen, iDeclaredLen, iNegSub;

    public ResultArray(ArrayList array, int type, int structure, int iPopulatedLen, int iDeclaredLen, int iNegSub)
    {
        super(type, structure);
        this.array = array;
        this.iPopulatedLen = iPopulatedLen;
        this.iDeclaredLen = iDeclaredLen;
        this.iNegSub = iNegSub;
    }
    public ResultArray(int type, int structure)
    {
        super(type, structure);
    }
}
