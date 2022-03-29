package utils;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.behkha.chaarpaye.R;

/**
 * Created by User on 3/19/2018.
 */

public class Server {

    public static String DOMAIN = "https://api.chaarpaye.ir";
    public static String API = "/v1";
    public static void errorHandler(Context context , VolleyError volleyError){
        String message;
        if (volleyError instanceof NetworkError) {
            message = context.getResources().getString(R.string.no_internet_connection);
            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
        } else if (volleyError instanceof ServerError) {
            message = "The server could not be found. Please try again after some time!!";
            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
        } else if (volleyError instanceof AuthFailureError) {
            message = context.getResources().getString(R.string.no_internet_connection);
            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
        }  else if (volleyError instanceof TimeoutError) {
            message = context.getResources().getString(R.string.no_internet_connection);
            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
        }

    }
    public static String convertPersianToLatin(String numbers){
        String[] persianNumbers = {"۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹"};
        String[] latinNumbers = {"0","1","2","3","4","5","6","7","8","9"};
        String result = numbers;
        for (int i = 0; i < 10; i++) {
            result = result.replaceAll( persianNumbers[i] , latinNumbers[i] );
        }
        return result;
    }
}
