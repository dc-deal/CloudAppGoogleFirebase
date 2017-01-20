package net.livingrecordings.giggermainapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;

import net.livingrecordings.giggermainapp.LoginScreens.LoginActivity;
import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    public static final String PAGER_STATE = "PGSTATE";
//    NavigationView viewNav;
    GiggerIntentHelperClass ghc;

    public void showLoginDialog() {
        Intent LoginIntent = new Intent(this,LoginActivity.class);
        this.startActivity(LoginIntent);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    private void showItemLIstFragement() {
        t = ft.beginTransaction();
        t.replace(R.id.placeholder_MAINACTIVITYfragment, new ItemListFragment());
        t.commit();
    }

    private void showBands() {
        t = ft.beginTransaction();
        t.replace(R.id.placeholder_MAINACTIVITYfragment, new BandListFragment());
        t.commit();
    }



    private FragmentManager ft;
    private FragmentTransaction t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        ghc = new GiggerIntentHelperClass(this);

        String eqString = getResources().getString(R.string.Equipment);
        String conString = getResources().getString(R.string.BandsAndContacts);

        ft = getSupportFragmentManager();

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.gigs:
                                break;
                            case R.id.equipment:
                                showItemLIstFragement();
                                break;
                            case R.id.contacts:
                                showBands();
                                break;

                        }
                        return false;
                    }
                });

        Compability.getInstance().MenuButtonHack(this);
        showItemLIstFragement();
    }

    public boolean fireBaseLogin(){
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {


        }
        super.onBackPressed();// ev ein beenden!??!)
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.main_menu_action_settings) {
            startSettings();
            return true;
        }
        if (id == R.id.main_menu_login) {
            // neu einloggen.
            showLoginDialog();
            return true;
        }

        // es gibt hier auch eine suche..
        // wenn die geklcikt wird, wird sobald der erste buchstabe eingegeben wird der equipmentmanager aufgerufen(also die grosse equipmenr-baumansicht)
        // und alles nach dem begriff fragmental gefiltert angezeigt.
        // sortierkriterium muss ich mir noch ausdenken, aber alphabetisch wäre shconmal ein Anfang.
        // der jeweilige tabbreeich wird dann durch den kategoriemanager ausgetauscht. DAS WIRD LUSTIG ;)
        // aber die selbe suche kann ich auch im Kategoriemanager Fragment selber laufen lassen, das ist doch mal was.

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_equipment) {

        } else if (id == R.id.nav_contacts) {

        } else if (id == R.id.nav_share_equip) {
            // equipment teilen...
        } else if (id == R.id.nav_contUser) {
            // user anschreiben
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void startSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }


}
