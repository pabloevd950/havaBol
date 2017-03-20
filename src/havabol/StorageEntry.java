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
    public StorageEntry(Object value, String name, int type){
        if((value instanceof Integer)|| type == 2){
            this.intValue = (int) value;
        }
        if((value instanceof Float)|| type == 3){
            this.floatValue = (float) value;
        }
        if((value instanceof Boolean)|| type == 4){
            this.boolValue = (boolean) value;
        }
        if((value instanceof String)|| type == 5){
            this.strValue = (String) value;
        }
        this.entryName = name;
        this.entryType = type;
    }

    //Entry object
    public StorageEntry(String name, int type){
        this.entryName = name;
        this.entryType = type;
    }

}
