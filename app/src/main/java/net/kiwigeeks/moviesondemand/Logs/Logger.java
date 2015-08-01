package net.kiwigeeks.moviesondemand.Logs;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by itl on 2/07/2015.
 */
public class Logger {
    public static void m(String message) {
        Log.d("Logged", "" + message);
    }

    public static void shortTost(Context context, String message) {
        Toast.makeText(context, message + "", Toast.LENGTH_SHORT).show();
    }
    public static void longToast(Context context, String message) {
        Toast.makeText(context, message + "", Toast.LENGTH_LONG).show();
    }

}