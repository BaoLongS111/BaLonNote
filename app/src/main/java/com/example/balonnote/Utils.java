package com.example.balonnote;

import android.content.Context;

public class Utils {

    private static volatile Utils instance;
    public static Utils getUtils(){
        if(instance==null){
            synchronized (Utils.class){
                if(instance==null){
                    instance = new Utils();
                }
            }
        }
        return instance;
    }

    // dp转像素工具类
    public int dp2px(float dpValue,float scale) {
        return (int) (dpValue * scale + 0.5f);
    }
}
