package com.metapp.goodmoods;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

import static com.metapp.goodmoods.MyUtils.counter;
import static com.metapp.goodmoods.MyUtils.isOnline;

public class MainActivity extends AppCompatActivity
        implements
        EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks
        , MainListAdapter.ImageClickListener {

    private static final int RC_STORAGE_PERMISSIONS = 123;
    private DatabaseReference mrDatabase;
    private DatabaseReference mDatabase;
    private RecyclerView rc;
    private TextView tv_warning;
    private boolean isFirstTime = false;
    private String[] perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private AdView mAdView;
    private String url = "";
    private SpinKitView spinKitView;

    private BroadcastReceiver networkBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isOnline(context)) {
                tv_warning.setVisibility(View.GONE);
                spinKitView.setVisibility(View.VISIBLE);
                isFirstTime = false;
                loadBanner();
                loadData();
            } else {
                if(isFirstTime) {
                    spinKitView.setVisibility(View.GONE);
                    tv_warning.setVisibility(View.VISIBLE);
                }
            }
        }
    };
   /* @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(MainActivity.this, Dashboard.class);
        startActivity(setIntent);
        finish();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);

        isFirstTime = true;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        spinKitView = findViewById(R.id.spin_kit);
        tv_warning = findViewById(R.id.no_net);
        setSupportActionBar(toolbar);

        MobileAds.initialize(this,
                getResources().getString(R.string.app_id));

        spinKitView.setVisibility(View.VISIBLE);

        mAdView = findViewById(R.id.adView);
        loadBanner();

        rc = findViewById(R.id.main_list);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("wallpaper").child("superhero");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");

        registerReceiver(networkBroadCast, intentFilter);


    }

    private void loadBanner() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void loadData() {
        final ArrayList<String> urls = new ArrayList<>();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    urls.add(dataSnapshot1.getValue().toString());
                }

                rc.setAdapter(new MainListAdapter(MainActivity.this, urls));
                rc.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
                spinKitView.setVisibility(View.GONE);
        }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("lksjdf", databaseError.toString());
                spinKitView.setVisibility(View.GONE);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(networkBroadCast);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!EasyPermissions.hasPermissions(this, perms)) {
            requestPermissions();
        }
        if (!isOnline(this)) {
            if (isFirstTime)
            {
                tv_warning.setVisibility(View.VISIBLE);
            }
        }


    }

    private void requestPermissions() {
        EasyPermissions.requestPermissions(
                new PermissionRequest.Builder(this, RC_STORAGE_PERMISSIONS, perms)
                        .build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_share:
                MyUtils.shareApp("http://play.google.com/store/apps/details?id=" + this.getPackageName(), this);
                break;
            case R.id.nav_rate_us:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + this.getPackageName())));
                break;
            case R.id.nav_exit:
                new AlertDialog.Builder(this, R.style.DialogTheme)
                        .setTitle("Exit")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.this.finish();

                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
                break;
            case R.id.nav_more:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:SunzTech")));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (!EasyPermissions.hasPermissions(this, this.perms)) {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                new AppSettingsDialog.Builder(this).build().show();
            }
            if (EasyPermissions.somePermissionDenied(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermissions();
            }
        }
    }

    @Override
    public void onRationaleAccepted(int requestCode) {

    }

    @Override
    public void onRationaleDenied(int requestCode) {
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            if (!(resultCode == RESULT_OK)) {
                new AppSettingsDialog.Builder(this).build().show();
            }
        }
    }

    @Override
    public void imageClicked(String url) {
        this.url = url;
        counter++;
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra("image_url", url);
        startActivity(intent);


    }
}
