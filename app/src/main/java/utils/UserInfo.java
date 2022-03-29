package utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import collection.Collection;

/**
 * Created by User on 3/21/2018.
 */

public class UserInfo {

    public static final String PREF_NAME = "CHAARPAYE_PREF_NAME";
    public static String name = "null";
    public static String poe = "null";
    public static String password = "null";
    public static ArrayList<Collection> mCollections = new ArrayList<>();
    public static void addToCollection(Collection collection){
        mCollections.add(collection);
    }
    public static void removeFromCollection(Collection collection){
        mCollections.remove(collection);
    }
    public static ArrayList<Collection> getCollections(){return mCollections;}

    public static boolean hasToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.contains("TOKEN");
    }

    public static String getToken(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString("TOKEN",null);
    }

    public static void setToken(Context context , String newToken){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString("TOKEN",newToken);
        editor.apply();
    }

    public static void removeToken(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("TOKEN");
        editor.apply();
        name = "null";
        poe = "null";
        password = "null";
    }

    public static Map<String , String> getHeader(Context context){
        Map<String, String> header = new HashMap<>();
        String token = UserInfo.getToken(context);
        header.put("Authorization", "Bearer " + token);
        return header;
    }
}
