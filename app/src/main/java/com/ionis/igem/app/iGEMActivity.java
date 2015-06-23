package com.ionis.igem.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class iGEMActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_igem);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.button_igem_what)
    protected void onClickWhat() {
        startActivity(new Intent(this, WhatActivity.class));
    }


}
