package havabol;

import java.util.HashMap;

public class StorageManager
{
    HashMap <String, ResultValue> sT;

    /**
     * StorageManager constructor that will be used to provide methods for maintaining the values
     * and types of user defined variables
     * <p>
     * This constructor uses a hash map with a string as a key and ResultValue object for the value.
     */
    public StorageManager()
    {
        sT = new HashMap <String, ResultValue>();
    }


    /**
     * getEntry method returns the StorageManger entry for the given variable
     * <p>
     * returns the STEntry for the inputted symbol or will raise a NULL exception
     *
     * @param key String to use as a key to get value from hashmap sT
     * @return ResultValue entry from hasMap
     */
    public ResultValue getEntry(String key) throws Exception
    {

            ResultValue getKey = sT.get(key);

        return sT.get(key);
    }


    /**
     * putEntry method stores the variable and its corresponding ResultValue in the
     * StorageManager
     * <p>
     * ResultValue tells about the variable we are storing
     *
     * @param key String to use as a key to get value from hashmap sT
     * @param entry ResultValue entry to assign to key
     */
    public void putEntry(String key, ResultValue entry)
    {
        sT.put(key, entry);
    }
}
