package havabol;


import java.util.HashMap;
/**
 * Created by pablovalero on 3/6/17.
 */


public class StorageManager
{
    HashMap <String, ResultValue> sT;

    public StorageManager(){
        sT = new HashMap <String, ResultValue>();
    }


    public ResultValue getEntry(String key)
    {
        return sT.get(key);
    }



    public void putEntry(String key, ResultValue entry)
    {
        sT.put(key, entry);
    }



    //We should check if variable already exists here.



}
