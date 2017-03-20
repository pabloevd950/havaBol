package havabol;

/**
 * Created by pablovalero on 3/7/17.
 */
public class StorageEntry
{
    String strValue;
    int intValue;
    float floatValue;
    boolean boolValue;

    String entryName;
    int entryType;

    //Entry objecy with a value
    public StorageEntry(Object value, String name, int type)
    {
        if((value instanceof Integer)|| type == Token.INTEGER)
            this.intValue = (int) value;
        else if((value instanceof Float)|| type == Token.FLOAT)
            this.floatValue = (float) value;
        else if((value instanceof Boolean)|| type == Token.BOOLEAN)
            this.boolValue = (boolean) value;
        else if((value instanceof String)|| type == Token.STRING)
            this.strValue = (String) value;

        this.entryName = name;
        this.entryType = type;
    }

    //Entry object
    public StorageEntry(String name, int type)
    {
        this.entryName = name;
        this.entryType = type;
    }

}
