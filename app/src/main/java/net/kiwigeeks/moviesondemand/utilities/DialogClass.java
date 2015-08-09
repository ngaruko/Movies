package net.kiwigeeks.moviesondemand.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;


public class DialogClass extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AlertDialog alertDialog
                = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Connection Error");
        alertDialog.setMessage("Please check your internet connection!");

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        finish();


                    }
                });

        alertDialog.show();
    }


}