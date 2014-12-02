package com.cisco.telepresence.sandbox.stage;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.cisco.telepresence.sandbox.R;
import com.cisco.telepresence.sandbox.stage.view.ScreenPresenter;
import com.cisco.telepresence.sandbox.stage.view.ScreenView;

public class StageActivity extends Activity
{
    private static final String TAG = "pimpmystage";
    private static TextView debugView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stage);
        debugView = (TextView) findViewById(R.id.debugLabel);

        ScreenView screenView = (ScreenView) findViewById(R.id.singlescreen);
        ScreenPresenter screenPresenter = new ScreenPresenter(screenView); //TODO

        debug("Pimp my stage started");
    }

    public static void debug(String msg) {
        Log.i(TAG, msg);
        debugView.setText(msg);
    }
}
