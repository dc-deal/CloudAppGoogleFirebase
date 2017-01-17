package net.livingrecordings.giggermainapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;

import net.livingrecordings.giggermainapp.LoginScreens.LoginActivity;
import net.livingrecordings.giggermainapp.giggerMainClasses.GiggerContactCollection;
import net.livingrecordings.giggermainapp.giggerMainClasses.GiggerIntentHelperClass;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    public static final String PAGER_STATE = "PGSTATE";
    public ViewPager viewPager;
    public TabLayout tabLayout;
    public MainActivityPageAdapter mainPageAdapter;
    NavigationView viewNav;
    GiggerIntentHelperClass ghc;
    GiggerContactCollection gc;


    public void showLoginDialog() {
        Intent LoginIntent = new Intent(this,LoginActivity.class);
        this.startActivity(LoginIntent);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        // hab gemerkt das ist eher nutzlos. der pager ist klug genug sich
        // selbst die position zu merken, und das bundle wird nciht über das beenden der app übertragen.
        savedInstanceState.putInt(PAGER_STATE, tabLayout.getSelectedTabPosition());
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

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
        gc = new GiggerContactCollection();

        viewNav = (NavigationView) findViewById(R.id.nav_view);
        viewNav.setNavigationItemSelectedListener(this);

        //-----------------------------------
        // getting the tab layout started.
        // alles funktioniert auch, wenn noch nicht eingellogt wurde. dann wird nix angezeigt.
        tabLayout = (TabLayout) findViewById(R.id.tab_layout_main);
        String eqString = getResources().getString(R.string.Equipment);
        String conString = getResources().getString(R.string.BandsAndContacts);
        // das das menü auch eingestellt ist. später kann das mal per einstellung gemacht werden.
        viewNav.getMenu().getItem(0).setChecked(true);

        tabLayout.addTab(tabLayout.newTab().setText(eqString));
        tabLayout.addTab(tabLayout.newTab().setText(conString));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        viewPager = (ViewPager) findViewById(R.id.pager);
        mainPageAdapter = new MainActivityPageAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(mainPageAdapter);
        if (savedInstanceState != null){
            int pstate = savedInstanceState.getInt(PAGER_STATE);
            viewPager.setCurrentItem(pstate);
        }

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                viewNav.getMenu().getItem(tab.getPosition()).setChecked(true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        //-------------------------------------------------------------
        // Abwärtskompabilität. Vor der 4.4. version
        // wurde der Menübutton noch nciht nagezeigt, hier aber
        // der code damit der MENÜ button auf jeden Fall ge-Forced wird.
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        } //menü hack ende
        //--------------------------------------------------------------

  //      NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
 //       View headerView = navigationView.inflateHeaderView(R.layout.main_nav_header);
   //     new LoginImageHelper().fillLoginInfoField(this,navigationView);

        // wen ja dann kann ich hier alles bauen
        // annsonsten dialog öffnen!!
    }

    public boolean fireBaseLogin(){
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewPager != null) {
            // vorsicht das gibt nen crash!
            viewNav.getMenu().getItem(viewPager.getCurrentItem()).setChecked(true);
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            boolean upDone = false;
            if (viewPager.getCurrentItem() == 0) { // also dem equip tree...
                // das is harter stoff. hier bekomme ich mein Fragment vom
                FragmentMainEquipListFragment eqList = (FragmentMainEquipListFragment) mainPageAdapter.getRegisteredFragment(0);
                if (eqList.menuRed.isOpened()) {
                    eqList.menuRed.close(true);
                    upDone = true;
                } else {
                    eqList.pushBack();
                    upDone = true;
                }
            }
            if (viewPager.getCurrentItem() == 1) { // also dem equip tree...
                FragmentMainContactList contList = (FragmentMainContactList) mainPageAdapter.getRegisteredFragment(1);
                if (contList.fabMenu.isOpened()) {
                    contList.fabMenu.close(true);
                    upDone = true;
                }
            }
            if (!upDone)
                super.onBackPressed();// ev ein beenden!??!)
        }
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
            viewPager.setCurrentItem(0);
        } else if (id == R.id.nav_contacts) {
            viewPager.setCurrentItem(2);
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


    public void startMyProfile() {
        ghc.intentShowContact(gc.loginUser);
    }


}
