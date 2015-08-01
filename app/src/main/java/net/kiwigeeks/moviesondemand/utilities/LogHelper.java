package net.kiwigeeks.moviesondemand.utilities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Jim on 11/5/13.
 */
public class LogHelper {
  public static final String LOG_TAG = "Movie app";

  public static void log(String message) {
    Log.d(LOG_TAG, message);
  }

  public static void logIntent(String label, Intent intent) {
    log("-------------------------- " + label + " --------------------------");
    String action = intent.getAction();
    log("action=" + (action == null ? "<null>" : action));
    Bundle extras = intent.getExtras();
    if (extras != null) {
      for (String key : extras.keySet()) {
        log("extra key=" + key);
      }
    }
    log("------------------------------------------------------------------");
  }
}
