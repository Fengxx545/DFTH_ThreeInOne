package com.dftaihua.dfth_threeinone.utils;

import java.util.Arrays;

/**
 * Created by Administrator on 2017/4/25 0025.
 */

public class ECGLeaderUtils {
    public static boolean[] getSingleLeaders(){
        boolean[] leaders = new boolean[12];
        Arrays.fill(leaders,false);
        leaders[0] = true;
        return leaders;
    }

    public static boolean[] getTwelveLeaders(){
        boolean[] leaders = new boolean[12];
        Arrays.fill(leaders,false);
        leaders[0] = true;
        leaders[1] = true;
        leaders[2] = true;
        return leaders;
    }
}
