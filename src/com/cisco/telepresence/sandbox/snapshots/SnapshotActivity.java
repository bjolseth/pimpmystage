package com.cisco.telepresence.sandbox.snapshots;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import com.cisco.telepresence.sandbox.R;
import com.cisco.telepresence.system.ServiceProvider;
import com.cisco.telepresence.system.SystemAIDLService;

public class SnapshotActivity extends Activity {

    private SystemAIDLService telepresenceService;
    private Handler handler;
    final int FadeInTimeMs = 1000;
    final int PollIntervalMs = 2000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snapshots);
        handler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        telepresenceService = ServiceProvider.getSystemService();
        updateSelfviewSnap();
        updateSnapsRegularly();
        //addListeners();
    }

    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }

    private void updateSnapsRegularly() {
        Runnable updateSnap = new Runnable() {
            @Override
            public void run() {
                updateSelfviewSnap();
                updateSnapsRegularly(); // call again for continuous loop
            }
        };
        handler.postDelayed(updateSnap, PollIntervalMs);
    }

    private void updateSelfviewSnap() {
        Log.i("Sandbox", "Fetching snapshot");
        final ImageView selfView = (ImageView) findViewById(R.id.snapshotNew);
        final Bitmap snapshot = getSelfviewSnap();
        selfView.setImageBitmap(snapshot);

        Animation fadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
        selfView.setAlpha(1.0f);
        fadeInAnimation.setDuration(FadeInTimeMs);
        selfView.startAnimation(fadeInAnimation);
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                ImageView selfViewOld = (ImageView) findViewById(R.id.snapshotOld);
                selfViewOld.setImageBitmap(snapshot);
                selfView.setAlpha(0f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}

        });
    }

    private Bitmap getSelfviewSnap() {
//        try {
//            return nulltelepresenceService.getSourcePreviewForSourceId(null, "localMain");
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
        return null;
    }
}
