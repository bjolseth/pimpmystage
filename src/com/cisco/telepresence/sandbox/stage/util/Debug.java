package com.cisco.telepresence.sandbox.stage.util;

import android.util.Log;

public class Debug {

    private static final String TAG = "pimpmystage";

    public static void debug(String message, Object ... args) {
        Log.i(TAG, String.format(message, args));
    }
}
