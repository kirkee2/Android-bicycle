package com.example.lkj.bicycleproject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.example.lkj.bicycleproject.Connection.Connect;
import com.example.lkj.bicycleproject.Connection.WebHook;
import com.example.lkj.bicycleproject.Fragment.FindRoadFragment;
import com.example.lkj.bicycleproject.Fragment.MainFragment;
import com.example.lkj.bicycleproject.Fragment.RecommandRoadFragment;
import com.example.lkj.bicycleproject.Fragment.RecordFragment;
import com.example.lkj.bicycleproject.Kakao_Login.LoginActivity;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String kakaoId;

    private ImageButton home;
    private ImageButton findRoad;
    private ImageButton recommandRoad;
    private ImageButton record;
    private ImageButton selected;
    private ImageButton weather;
    private CircularImageView thumbNailImage;
    private TextView userId;

    private Toolbar toolbar;
    private String[] navItems = {"설정","홈페이지","버전 : 0.0.1(Beta)"};
    private ListView navList;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private LocationManager locationManager;
    private View naviHeader;


    private Fragment currentFragment = null;
    private long backKeyPressedTime = 0;

    private AQuery aquery ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestMe();
        home = (ImageButton)findViewById(R.id.btHome);
        findRoad = (ImageButton)findViewById(R.id.btFindRoad);
        recommandRoad = (ImageButton)findViewById(R.id.btRecommandRoad);
        record = (ImageButton)findViewById(R.id.btRecord);
        weather = (ImageButton)findViewById(R.id.weather);

        naviHeader = getLayoutInflater().inflate(R.layout.navi_header, null, false);

        thumbNailImage = (CircularImageView)naviHeader.findViewById(R.id.thumbNailImage);
        userId = (TextView)naviHeader.findViewById(R.id.userId);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        aquery = new AQuery( this );

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

        navList.addHeaderView(naviHeader);



        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alertCheckGPS();
        }

        permissionCheck();
    }

    private void alertCheckGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("GPS가 꺼져있습니다. GPS를 켜시겠습니까? GPS가 꺼져있다면 기능의 제한이 있습니다.")
                .setCancelable(false)
                .setPositiveButton("GPS 켜기",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(gpsOptionsIntent);
                            }
                        })
                .setNegativeButton("취소",
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

    private void permissionCheck() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                permissionCheck();
                //Toast.makeText(MainActivity.this, "권한 승인 안됨." + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("권한을 승인하지 않으시면 이 서비스를 사용하실 수 없습니다.\n\n권한을 승인해주세요.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.CAMERA)
                .check();

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
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
            Intent intent;

            switch (position) {
                case 1:
                    intent = new Intent(MainActivity.this,SettingActivity.class);
                    startActivity(intent);
                    drawer.closeDrawer(navList);
                    break;
                case 2:
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/kirkee2/Android-bicycle"));
                    startActivity(intent);
                    //intent.setPackage("com.android.chrome");
                    drawer.closeDrawer(navList);
                    break;
            }
        }

    }

    protected void requestMe() { //유저의 정보를 받아오는 함수
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {

                Toast.makeText(getApplicationContext(),"세션이 종료됬습니다. 다시 로그인 해주세요.",Toast.LENGTH_LONG).show();
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    finish();
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Toast.makeText(getApplicationContext(),"세션이 종료됬습니다. 다시 로그인 해주세요.",Toast.LENGTH_LONG).show();


                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }

            @Override
            public void onNotSignedUp() {
                Toast.makeText(getApplicationContext(),"세션이 종료됬습니다. 다시 로그인 해주세요.",Toast.LENGTH_LONG).show();

            } // 카카오톡 회원이 아닐 시 showSignup(); 호출해야함

            @Override
            public void onSuccess(UserProfile userProfile) {  //성공 시 userProfile 형태로 반환
                kakaoId = userProfile.getId() + "";

                JSONObject json = new JSONObject();


                try {
                    json.put("kakao", kakaoId);
                    new UserInfo().execute(getResources().getString(R.string.server_ip) + "/UserInfo.php", json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class UserInfo extends AsyncTask<String, Void, JSONObject> {
        protected JSONObject doInBackground(String... urls) {

            try {
                JSONObject jsonObj = new JSONObject(urls[1]);

                Connect con = new Connect(urls[0]);

                return con.postJsonObject(con.getURL(), jsonObj);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(JSONObject result) {
            if (result == null) {
                userId.setText("아이디를 받아오지 못 했습니다");
                thumbNailImage.setImageResource(R.drawable.thumb_nail_basic_blue3);
            } else {
                try {
                    if(result.getString("code").compareTo("0") == 0) {
                        userId.setText(result.getString("id"));
                        aquery.id( thumbNailImage ).image("http://kirkee2.cafe24.com/memberImage/"+kakaoId+".jpg" );
                    }else if(result.getString("code").compareTo("1") == 0) {
                        userId.setText("아이디를 받아오지 못 했습니다");
                        thumbNailImage.setImageResource(R.drawable.thumb_nail_basic_blue3);
                    }else{
                        Toast.makeText(MainActivity.this,"서버 에러",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    JSONObject json = new JSONObject();

                    try {
                        json.put("kakao", kakaoId);
                        json.put("auth", data.getStringExtra(Intents.Scan.RESULT));
                        new AuthCheck().execute(getResources().getString(R.string.server_ip) + "/AuthCheck.php", json.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                }
                break;
            default:
        }
    }

    private class AuthCheck extends AsyncTask<String, Void, JSONObject> {
        protected JSONObject doInBackground(String... urls) {
            try {
                JSONObject jsonObj = new JSONObject(urls[1]);

                Connect con = new Connect(urls[0]);

                return con.postJsonObject(con.getURL(), jsonObj);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(JSONObject result) {
            if (result == null) {
            } else {
                JSONObject json = new JSONObject();
                try {
                    json.put("kakao", kakaoId);
                    RecordFragment.Refresh();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onWeather(View view){
        Intent intent = new Intent(MainActivity.this,WeatherActivity.class);
        startActivity(intent);
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
