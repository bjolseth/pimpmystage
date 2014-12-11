package com.cisco.telepresence.sandbox;

import android.app.Activity;
import android.view.View;

public class Test extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.cliptest);
        findViewById(R.id.test_zoombutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = findViewById(R.id.testcontainer    );
                float scale = v.getScaleX() < 1 ? 1 : 0.4f;
                v.setScaleX(scale);
                v.setScaleY(scale);
            }
        });
    }
}
