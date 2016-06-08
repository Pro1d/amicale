package fr.insa.clubinfo.amicale;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import fr.insa.clubinfo.amicale.dialogs.StartPlanexDialog;
import fr.insa.clubinfo.amicale.fragments.ChatFragment;
import fr.insa.clubinfo.amicale.fragments.HomeFragment;
import fr.insa.clubinfo.amicale.fragments.PreferencesFragment;
import fr.insa.clubinfo.amicale.fragments.WashINSAFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int currentFragmentId = -1;
    private NavigationView navigationView;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_home);
        selectFragment(R.id.nav_home);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
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

        switch(id) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_chat:
                fragment = new ChatFragment();
                break;
            case R.id.nav_washinsa:
                fragment = new WashINSAFragment();
                break;
            case R.id.nav_preferences:
                fragment = new PreferencesFragment();
                break;
            case R.id.nav_timetable:
                StartPlanexDialog.startPlanex(this);
                // TODO ?
                break;
        }

        if(fragment != null) {
            // Switch to the new fragment
            fragmentManager.beginTransaction().replace(R.id.main_fl_content, fragment).commit();
            currentFragmentId = id;
            return true;
        } else {
            return false;
        }
    }
}
