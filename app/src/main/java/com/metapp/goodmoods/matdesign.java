package com.metapp.goodmoods;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class matdesign extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matdesign);
    }
    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(matdesign.this, Dashboard.class);
        startActivity(setIntent);
        finish();
    }
}