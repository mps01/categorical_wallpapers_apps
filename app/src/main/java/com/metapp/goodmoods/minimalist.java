package com.metapp.goodmoods;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class minimalist extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(minimalist.this, Dashboard.class);
        startActivity(setIntent);
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minimalist);
    }
}