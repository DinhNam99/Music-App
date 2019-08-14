package com.mymusic.dell.mymusic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mymusic.dell.mymusic.fragment.FavoriteFragment;
import com.mymusic.dell.mymusic.fragment.HomeFragment;
import com.mymusic.dell.mymusic.fragment.PlaylistFragment;
import com.mymusic.dell.mymusic.view.ActivityAllSong;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private String[] LIST_PERMISSION = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };
    boolean isExit = false;

    public static final int REQUEST_INTENT_SONG = 1;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edSearch)
    EditText editText;

    @BindView(R.id.tv_name_song)
    TextView tvNameSong;
    @BindView(R.id.tv_artist)
    TextView artist;
    @BindView(R.id.im_prev)
    ImageView im_prev;
    @BindView(R.id.im_next)
    ImageView im_next;
    @BindView(R.id.im_play)
    ImageView im_play;
    @BindView(R.id.imagM)
    ImageView imageSong;


    FavoriteFragment favoriteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.main_layout);
        ButterKnife.bind(this);

        //toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        if (checkPermisson()) {
            HomeFragment fragment = HomeFragment.newInstance(1);
            loadFragment(fragment);
        }
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivityAllSong.class);
                startActivity(intent);
            }
        });
        isExit = false;

    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.home:
                    fragment = new HomeFragment();
                    fragment = HomeFragment.newInstance(1);
                    loadFragment(fragment);
                    return true;
                case R.id.playlist:
                    fragment = new PlaylistFragment();

                    loadFragment(fragment);
                    return true;
                case R.id.favorite:
                    fragment = new FavoriteFragment();
                    fragment = FavoriteFragment.newInstance(2);
                    favoriteFragment = (FavoriteFragment) fragment;
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment)
                .commit();
    }

    private boolean checkPermisson() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String p : LIST_PERMISSION) {
                if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(LIST_PERMISSION, 0);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (checkPermisson()) {
            HomeFragment fragment = HomeFragment.newInstance(1);
            loadFragment(fragment);
        } else {
            finish();
        }
    }


    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        Log.d("OnResume", "onResume");
        if (!Objects.equals(favoriteFragment, null))
            favoriteFragment.updateList();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (isExit) {
            super.onBackPressed();
            return;
        }
        this.isExit = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                isExit = false;
            }
        }, 2000);
    }


}
