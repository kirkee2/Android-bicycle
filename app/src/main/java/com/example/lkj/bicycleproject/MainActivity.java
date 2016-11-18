package com.example.lkj.bicycleproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lkj.bicycleproject.Connection.WebHook;
import com.example.lkj.bicycleproject.Fragment.FindRoadFragment;
import com.example.lkj.bicycleproject.Fragment.MainFragment;
import com.example.lkj.bicycleproject.Fragment.RecommandRoadFragment;
import com.example.lkj.bicycleproject.Fragment.RecordFragment;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_GPS = 0;

    private ImageButton home;
    private ImageButton findRoad;
    private ImageButton recommandRoad;
    private ImageButton record;
    private ImageButton selected;

    private Toolbar toolbar;
    private String[] navItems = {"Setting","home page"};
    private ListView navList;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private LocationManager locationManager;

    private Fragment currentFragment = null;
    private long backKeyPressedTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        home = (ImageButton)findViewById(R.id.btHome);
        findRoad = (ImageButton)findViewById(R.id.btFindRoad);
        recommandRoad = (ImageButton)findViewById(R.id.btRecommandRoad);
        record = (ImageButton)findViewById(R.id.btRecord);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        home.setSelected(true);
        selected =home;
        currentFragment =new MainFragment();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        navList = (ListView)findViewById(R.id.left_drawer);
        toggle = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.open_drawer, R.string.close_drawer){

           @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
              super.onDrawerOpened(drawerView);
            }

        };

        drawer.addDrawerListener(toggle);

        navList.setAdapter(
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navItems));
        navList.setOnItemClickListener(new DrawerItemClickListener());

        navList.setClickable(false);

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alertCheckGPS();
        }

    }

    private void alertCheckGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS is disabled! Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(gpsOptionsIntent);
                            }
                        })
                .setNegativeButton("Do nothing",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void onBackPressed() {
        String arr[] = currentFragment.toString().split("\\{",2);
        if(arr[0].equals("MainFragment")){
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                Toast.makeText(this,"뒤로버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();

                return;
            }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                this.finish();
            }
        }else{
            selected.setSelected(false);
            home.setSelected(true);
            selected = home;
            currentFragment = new MainFragment();


            FragmentManager fragmentManager = getSupportFragmentManager();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.MainPagefragment, currentFragment);

            fragmentTransaction.commit();

        }
    }


    public void changeFragment(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btHome:
                selected.setSelected(false);
                home.setSelected(true);
                selected = home;
                currentFragment = new MainFragment();
                break;
            case R.id.btFindRoad:
                selected.setSelected(false);
                findRoad.setSelected(true);
                selected = findRoad;
                currentFragment = new FindRoadFragment();
                break;
            case R.id.btRecommandRoad:
                selected.setSelected(false);
                recommandRoad.setSelected(true);
                selected = recommandRoad;
                currentFragment = new RecommandRoadFragment();
                break;
            case R.id.btRecord:
                selected.setSelected(false);
                record.setSelected(true);
                selected = record;
                currentFragment = new RecordFragment();
                break;

        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.MainPagefragment, currentFragment);

        //fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position,
                                long id) {
            Intent intent;

            switch (position) {
                case 0:
                    intent = new Intent(MainActivity.this,SettingActivity.class);
                    startActivity(intent);
                    drawer.closeDrawer(navList);
                    break;
                case 1:
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ENOW-IJI"));
                    startActivity(intent);
                    //intent.setPackage("com.android.chrome");
                    drawer.closeDrawer(navList);
                    break;
            }
        }

    }

    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
