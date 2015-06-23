package com.ionis.igem.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_us);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.button_us_presentation)
    protected void onClickPresentation() {
        startActivity(new Intent(this, PresentationActivity.class));
    }

}
