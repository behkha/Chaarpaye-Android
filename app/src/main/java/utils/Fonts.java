package utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by User on 3/6/2018.
 */

public class Fonts {

    public static Typeface get_iran_sans_font(Context context)
    {
        return Typeface.createFromAsset( context.getAssets() , "fonts/IRANSans.ttf" );
    }

    public static Typeface get_iran_sans_bold_font(Context context) {
        return Typeface.createFromAsset( context.getAssets() , "fonts/IRANSans_Bold.ttf" );
    }

    public static Typeface get_iran_sans_light_font(Context context) {
        return Typeface.createFromAsset( context.getAssets() , "fonts/IRANSans_Light.ttf" );
    }

    public static Typeface get_iran_sans_medium_font(Context context) {
        return Typeface.createFromAsset( context.getAssets() , "fonts/IRANSans_Medium.ttf" );
    }


    public static Typeface get_iran_sans_ultra_light_font(Context context) {
        return Typeface.createFromAsset( context.getAssets() , "fonts/IRANSans_UltraLight.ttf" );
    }


    public static Typeface get_iran_sans_fa_num_font(Context context)
    {
        return Typeface.createFromAsset( context.getAssets() , "fonts/IRANSansMobile(FaNum).ttf" );
    }

    public static Typeface get_iran_sans_fa_num_bold_font(Context context) {
        return Typeface.createFromAsset( context.getAssets() , "fonts/IRANSansMobile(FaNum)_Bold.ttf" );
    }

    public static Typeface get_iran_sans_fa_num_light_font(Context context) {
        return Typeface.createFromAsset( context.getAssets() , "fonts/IRANSansMobile(FaNum)_Light.ttf" );
    }

    public static Typeface get_iran_sans_fa_num_medium_font(Context context) {
        return Typeface.createFromAsset( context.getAssets() , "fonts/IRANSansMobile(FaNum)_Medium.ttf" );
    }


    public static Typeface get_iran_sans_fa_num_ultra_light_font(Context context) {
        return Typeface.createFromAsset( context.getAssets() , "fonts/IRANSansMobile(FaNum)_UltraLight.ttf" );
    }
}
