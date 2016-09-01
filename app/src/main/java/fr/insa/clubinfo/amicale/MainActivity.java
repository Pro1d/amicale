package fr.insa.clubinfo.amicale;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import fr.insa.clubinfo.amicale.dialogs.NoticeNicknameChat;
import fr.insa.clubinfo.amicale.dialogs.StartPlanexDialog;
import fr.insa.clubinfo.amicale.dialogs.WashINSAAlarmCancelDialog;
import fr.insa.clubinfo.amicale.dialogs.WashINSAAlarmCreateDialog;
import fr.insa.clubinfo.amicale.dialogs.WashINSAAlarmStopDialog;
import fr.insa.clubinfo.amicale.fragments.ChatFragment;
import fr.insa.clubinfo.amicale.fragments.HomeFragment;
import fr.insa.clubinfo.amicale.fragments.PreferencesFragment;
import fr.insa.clubinfo.amicale.fragments.WashINSAFragment;
import fr.insa.clubinfo.amicale.helpers.ImageBitmap;
import fr.insa.clubinfo.amicale.helpers.WashINSAAlarm;
import fr.insa.clubinfo.amicale.views.ImageViewer;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnCompleteListener<AuthResult> {

    private int currentFragmentId = -1;
    private int lastConsistentFragmentId = -1;
    private Fragment activeFragment;
    public static Handler handler;
    private FirebaseAuth firebaseAuth;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageBitmap.initScreenWidthAndHeight(this);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Fullscreen image viewer
        View fullscreenImageViewer = findViewById(R.id.main_vp_fullscreen_image_view_pager);
        ImageViewer.instantiateImageViewer(fullscreenImageViewer, drawer);

        // Side navigation menu
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Static access to dialog - Initialization
        WashINSAAlarmCreateDialog.initialize();
        WashINSAAlarmStopDialog.initialize();
        WashINSAAlarmCancelDialog.initialize();
        StartPlanexDialog.initialize();
        NoticeNicknameChat.initialize(this);

        // WashINSAlarm
        WashINSAAlarm.onActivityCreated(getIntent(), this);
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                WashINSAAlarm.handleMessage(msg, MainActivity.this);
                return true;
            }
        });

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // restore selected fragment
        int fragmentId = getSharedPreferences("fragment", MODE_PRIVATE).getInt("current_fragment", R.id.nav_home);
        switchToFragment(fragmentId);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.signInAnonymously().addOnCompleteListener(this);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState.containsKey("current_fragment")) {
            int fragmentId = savedInstanceState.getInt("current_fragment");
            switchToFragment(fragmentId);
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("current_fragment", lastConsistentFragmentId);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WashINSAAlarm.stopRingtone();
        // Remove washinsa handler
        handler = null;
        // save current selected fragment
        getSharedPreferences("fragment", MODE_PRIVATE).edit().putInt("current_fragment", lastConsistentFragmentId).apply();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(!(activeFragment != null
                    && activeFragment instanceof fr.insa.clubinfo.amicale.fragments.Fragment
                    && ((fr.insa.clubinfo.amicale.fragments.Fragment) activeFragment).onBackPressed())) {
                if(currentFragmentId != lastConsistentFragmentId) {
                    switchToFragment(lastConsistentFragmentId);
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        boolean itemSelected = selectFragment(id);

        if(itemSelected) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }

        return itemSelected;
    }

    /**
     * @return true if a fragment is selected
     */
    private boolean selectFragment(int id) {
        if(currentFragmentId == id)
            return true;


        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = null;
        boolean isConsistentFragment = false;

        switch(id) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                isConsistentFragment = true;
                break;
            case R.id.nav_chat:
                fragment = new ChatFragment();
                isConsistentFragment = true;
                break;
            case R.id.nav_washinsa:
                fragment = new WashINSAFragment();
                isConsistentFragment = true;
                break;
            case R.id.nav_preferences:
                fragment = new PreferencesFragment();
                break;
            case R.id.nav_timetable:
                StartPlanexDialog.startPlanex(this);
                break;
        }

        if(fragment != null) {
            // Switch to the new fragment
            fragmentManager.beginTransaction().replace(R.id.main_fl_content, fragment).commit();
            activeFragment = fragment;
            currentFragmentId = id;
            if(isConsistentFragment)
                lastConsistentFragmentId = id;
            return true;
        }
        else {
            return false;
        }
    }

    private void switchToFragment(int fragmentId) {
        selectFragment(fragmentId);
        navigationView.setCheckedItem(fragmentId);
    }

    public void switchToSettingsFragment() {
        switchToFragment(R.id.nav_preferences);
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {

    }
}
