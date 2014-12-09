package com.cisco.telepresence.sandbox.stage;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.cisco.telepresence.sandbox.R;

public class StageWithoutCodec extends Activity
{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View stage = View.inflate(this, R.layout.stage, null);
        setContentView(stage);
        new StageController(this, stage);
    }





}
