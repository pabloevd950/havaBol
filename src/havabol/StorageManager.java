package havabol;


import java.util.HashMap;
/**
 * Created by pablovalero on 3/6/17.
 */


public class StorageManager
{
    HashMap <String, StorageEntry> sT;

    public StorageManager(){
        sT = new HashMap <String, StorageEntry>();
    }


    public StorageEntry getEntry(String key)
    {
        return sT.get(key);
    }



    public void putEntry(String key, StorageEntry entry)
    {
        sT.put(key, entry);
    }


    //We should check if variable already exists here.



}
