package com.cisco.telepresence.sandbox;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.cisco.telepresence.sandbox.stage.util.ImageBlur;
import com.cisco.telepresence.sandbox.stage.util.MultiTouchListener;
import com.cisco.telepresence.sandbox.stage.util.FrameDragBuilder;

public class Experiment extends Activity implements MultiTouchListener.MultiTouchCallback {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment);
        setTouchListener(findViewById(R.id.exp_box));
        setDropSpots();

        Bitmap original = BitmapFactory.decodeResource(getResources(), R.drawable.team_photo);
        Bitmap blurry = ImageBlur.blurBitmap(this, original);
        ((ImageView) findViewById(R.id.photo)).setImageBitmap(blurry);
    }

    private void setDropSpots() {
        View.OnDragListener dragListener = new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                //if (dragEvent.getAction() == DragEvent.ACTION_DROP)
                switch (dragEvent.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        Log.i("jalla", "drag started");
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.i("jalla", "drag entered");
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        Log.i("jalla", "drag exited");
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        Log.i("jalla", "drag ended");
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        Log.i("jalla", "drag location " + dragEvent.getX() + "," + dragEvent.getY());
                        break;
                    case DragEvent.ACTION_DROP:
                        Log.i("jalla", "drag drop");
                        break;
                }
                Log.i("jalla", "drag action: " + dragEvent.getAction());
                return true;
            }
        };
        findViewById(R.id.exp_screen).setOnDragListener(dragListener);
    }


    private void setTouchListener(View view) {
        final MultiTouchListener touchHandler = new MultiTouchListener(this, this);
        view.setOnTouchListener(touchHandler);
    }

    @Override
    public void onScaleView(View view, float scale) {
        Log.i("jalla", "onscale " + scale);
        View ghost = findViewById(R.id.ghost);
        ViewGroup.LayoutParams p = ghost.getLayoutParams();
        p.width = (int) (p.width * scale);
        p.height = (int) (p.width *9 /16);
        ghost.setLayoutParams(p);
    }

    @Override
    public void onSingleTap(View view) {
        Log.i("jalla", "onsingletap ");

    }

    @Override
    public void onDoubleTap(View view) {
        Log.i("jalla", "ondoubletap");
    }

    @Override
    public void onEndTouch() {
        Log.i("jalla", "onendtouch");
    }

    @Override
    public void onLongPress(View view) {
        Log.i("jalla", "onlongpress");
        View.DragShadowBuilder shadow = new FrameDragBuilder(view);
        view.startDrag(null, shadow, null, 0);
    }
}
