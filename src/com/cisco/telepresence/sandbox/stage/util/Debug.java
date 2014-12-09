package com.cisco.telepresence.sandbox.stage.util;

import android.util.Log;

public class Debug {

    public static void debug(String message, Object ... args) {
        Log.i("pimpmystage", String.format(message, args));
    }
}
