package havabol;


/**
 * Created  on 3/3/17.
 */
public class Utilities
{

    public static int addInt(int x, int y){
         return x+y;
    }

    public static double addFloat(double x, double y){
        return x+y;
    }

    public static int subInt(int x, int y){
        return x-y;
    }

    public static double subFloat(double x, double y){
        return x-y;
    }

    public static int divInt(int x, int y){
        return x/y;
    }

    public static double divFloat(double x, double y){
        return x/y;
    }

    public static int mulInt(int x, int y){
        return x*y;
    }

    public static double mulFloat(double x, double y){
        return x*y;
    }

    public static double expInt(int x, int y){
        return Math.pow(x,y);
    }

    public static double expDouble(double x, double y){
        return Math.pow(x,y);
    }

    public static  < T extends Comparable > boolean isEqual(T x, T y){
        if(x.compareTo(y) == 0)
            return true;
        else
        return false;
    }

    public static <T extends Comparable > boolean isGreaterThan(T x, T y){
        if(x.compareTo(y) > 0){
            return true;
        }
        else return false;
    }

    public static <T extends Comparable > boolean isLessThan(T x, T y){
        if(x.compareTo(y) < 0){
            return true;
        }
        else return false;
    }



}
