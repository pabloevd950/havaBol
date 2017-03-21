package havabol;


import java.util.HashMap;
/**
 * Created by pablovalero on 3/6/17.
 */


public class StorageManager
{
    HashMap <String, ResultValue> sT;

    /**
     *
     */
    public StorageManager(){
        sT = new HashMap <String, ResultValue>();
    }

    /**
     *
     * @param key key to get value from hashmap
     * @return ResultValue entry from hasMap
     */
    public ResultValue getEntry(String key)
    {
        return sT.get(key);
    }


    /**
     *
     * @param key key to get value from hashmap
     * @param entry ResultValue entry to assign to key
     */
    public void putEntry(String key, ResultValue entry)
    {
        sT.put(key, entry);
    }



    //We should check if variable already exists here.



}
