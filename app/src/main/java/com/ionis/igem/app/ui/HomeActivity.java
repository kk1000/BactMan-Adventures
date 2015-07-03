package com.ionis.igem.app.ui;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.ionis.igem.app.R;

import java.io.IOException;
import java.net.URI;

public class HomeActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = "HomeActivity";
    private MediaPlayer mPlayer;

    @InjectView(R.id.videoView)
    protected SurfaceView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.inject(this);

        SurfaceHolder holder = videoView.getHolder();
        holder.addCallback(this);
    }


    @OnClick(R.id.button_home_new_game)
    protected void onClickNewGame() {
        Toast.makeText(getApplicationContext(), "You just clicked on new game!", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.button_home_team)
    protected void onClickTeam() {
        startActivity(new Intent(this, UsActivity.class));
    }

    @OnClick(R.id.button_home_iGEM)
    protected void onClickIGEM() {
        startActivity(new Intent(this, iGEMActivity.class));
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        int screenWidth = screenSize.x;
        ViewGroup.LayoutParams lp = videoView.getLayoutParams();
        lp.width = screenWidth;
        lp.height = (int) (((float) videoView.getHeight() / (float) videoView.getWidth()) * (float)screenWidth);
        videoView.setLayoutParams(lp);

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drop);
        mPlayer = MediaPlayer.create(getApplicationContext(),uri, holder);
        mPlayer.setLooping(true);
        mPlayer.start();
        Log.d(TAG, "onCreate - MediaPlayer started.");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed - Destroying MediaPlayer.");
        mPlayer.release();
    }
}
