package com.alc.journal;

import android.content.Context;
import android.widget.Toast;

/**
 * Created with love by Dozie on 6/30/2018.
 */

class Helper {
    static void showAlert(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
