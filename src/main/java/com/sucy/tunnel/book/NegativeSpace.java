package com.sucy.tunnel.book;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class NegativeSpace {

    private static final HashMap<Integer,Character> key = new HashMap<>();
    private static final List<Integer> inf = Arrays.asList(1,2,3,4,5,6,7,8,16,32,64,128,256,512,1042);

    static {
    key.put(-1    , '\uF801');    key.put(1    , '\uF821');
    key.put(-2    , '\uF802');    key.put(2    , '\uF822');
    key.put(-3    , '\uF803');    key.put(3    , '\uF823');
    key.put(-4    , '\uF804');    key.put(4    , '\uF824');
    key.put(-5    , '\uF805');    key.put(5    , '\uF825');
    key.put(-6    , '\uF806');    key.put(6    , '\uF826');
    key.put(-7    , '\uF807');    key.put(7    , '\uF827');
    key.put(-8    , '\uF808');    key.put(8    , '\uF828');
    key.put(-16   , '\uF809');    key.put(16   , '\uF829');
    key.put(-32   , '\uF80A');    key.put(32   , '\uF82A');
    key.put(-64   , '\uF80B');    key.put(64   , '\uF82B');
    key.put(-128  , '\uF80C');    key.put(128  , '\uF82C');
    key.put(-256  , '\uF80D');    key.put(256  , '\uF82D');
    key.put(-512  , '\uF80E');    key.put(512  , '\uF82E');
    key.put(-1024 , '\uF80F');    key.put(1024 , '\uF82F');
    }
    public static String space(int pixels){
        if (pixels==0) return "";
        boolean negative = pixels<0;
        StringBuilder result = new StringBuilder();
        while (pixels!=0){
            int a = sel(pixels);
            pixels-=a;
            result.append(key.get(a));
        }
        return result.toString();
    }
    private static int sel(int i){
        int m = i<0?-1:1;
        i = Math.abs(i);
        for (int j = 0; j < 15; j++) {
            System.out.println(inf.get(j) +" "+i);
            if (inf.get(j)>i)
                return inf.get(j-1)*m;
        }
        return 1024*m;
    }
}
