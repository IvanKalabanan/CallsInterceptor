package com.stfalcon.call;

import java.util.ArrayList;

/**
 * Created by root on 07.06.15.
 */
public class StaticClass {
    public static ArrayList<String> arrName = new ArrayList<>();
    public static ArrayList<String> arrPhone = new ArrayList<>();
    public static ArrayList<String> arrText = new ArrayList<>();
    public static ArrayList<String> arrState = new ArrayList<>();
    public static ArrayList<String> arrDate = new ArrayList<>();
    public static ArrayList<String> arrTime = new ArrayList<>();

    public static void clearAll(){
        arrName.clear();
        arrState.clear();
        arrText.clear();
        arrPhone.clear();
        arrDate.clear();
        arrTime.clear();
    }
}
