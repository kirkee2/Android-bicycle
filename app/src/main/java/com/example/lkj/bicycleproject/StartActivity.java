package com.example.lkj.bicycleproject;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lkj.bicycleproject.Connection.WebHook;
import com.example.lkj.bicycleproject.Fragment.StartMapFragment;
import com.example.lkj.bicycleproject.Fragment.StartSpeedFragment;
import com.example.lkj.bicycleproject.Kakao_Login.LoginActivity;
import com.github.anastr.speedviewlib.SpeedView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {

    private String kakaoId;

    private Toolbar toolbar;
    private FragmentPagerAdapter adapterViewPager;
    private ViewPager pager = null;

    private Timer mTimer = new Timer();
    private TimerTask m1000MsCountTimerTask = null;
    private int mCount = 0;
    private boolean timerOnCheck = false;
    private boolean timerPauseCheck = false;


    private TextView distance;
    private TextView timeRecord;
    private TextView calory;

    private Button startPause;
    private Button stop;


    private LocationManager locationManager;
    private Location location;

    private Location previousLocation;
    private float movedDistance = 0;
    private float totalDistance = 0;

    private int caloryInt;


    private float mySpeed;
    private float maxSpeed;

    private LocationListener locationListener;

    private View pagerView;

    private SpeedView speedometer;

    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        requestMe();

        distance = (TextView)findViewById(R.id.distance);
        timeRecord = (TextView)findViewById(R.id.timeRecord);
        calory = (TextView)findViewById(R.id.calory);
        startPause = (Button)findViewById(R.id.start);
        stop = (Button)findViewById(R.id.stop);

        pager = (ViewPager) findViewById(R.id.startPager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        /*
        pagerView = getLayoutInflater().inflate(R.layout.fragment_start_speed, null, false);

        speedometer = (SpeedView) pagerView.findViewById(R.id.speedView);

        speedometer.speedTo(70, 4000);
        */

        //startSpeedFragment.setSpeed();


        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapterViewPager);

        //pager.setCurrentItem(1);
        //new setAdapterTask().execute();

        //StartSpeedFragment startSpeedFragment = (StartSpeedFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.startPager+":0");
        //StartSpeedFragment startSpeedFragment = (StartSpeedFragment) getSupportFragmentManager().findFragmentById(R.id.startPager);

        //new WebHook().execute(startSpeedFragment+"");

        //startSpeedFragment.setSpeed(70);

        //StartSpeedFragment startSpeedFragment = new StartSpeedFragment();
        //StartSpeedFragment.newInstance(12);
        //Bundle bundle = new Bundle();
        //bundle.putInt("speed", 50);
        //startSpeedFragment.setArguments(bundle);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                if (location != null) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();

                    if (location.hasSpeed()) {


                        mySpeed = location.getSpeed() * (float) 3.6;
                        if (mySpeed > maxSpeed) {
                            maxSpeed = mySpeed;
                        }

                        StartSpeedFragment.speedometer.speedTo((int) mySpeed,1000);
                        //speed.setText("현재 속도 : " + mySpeed + " km/h, 최고속도 : " + maxSpeed + " km/h");

                        //speedometer.speedTo((int) mySpeed);
                    } else {
                        //speed.setText("0");
                        //speedometer.speedTo(0);
                    }


                    if (previousLocation == null) {
                        previousLocation = location;
                    } else {
                        movedDistance = previousLocation.distanceTo(location);

                        totalDistance += movedDistance;

                        distance.setText((int)(totalDistance) + "m");
                        caloryInt = (int) (totalDistance/40);
                        calory.setText(caloryInt+"");
                        //movedDistanceText.setText("움직인 거리 : " + movedDistance + "m");

                        previousLocation = location;
                    }

                    Toast.makeText(getApplicationContext(), "onLocationChanged   위도 : " + lat + " / 경도 : " + lng + " /정확도 : " + location.getAccuracy(), Toast.LENGTH_LONG).show();
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                //Toast.makeText(getApplicationContext(), "onStatusChanged     상태변환됨.", Toast.LENGTH_LONG).show();
            }

            public void onProviderEnabled(String provider) {
                Toast.makeText(getApplicationContext(), "GPS가 켜졌습니다.", Toast.LENGTH_LONG).show();
            }

            public void onProviderDisabled(String provider) {
                pauseTimerTask();
                startPause.setText("start");
                startPause.setBackgroundColor(0xFF5E7AC8);
                Toast.makeText(getApplicationContext(), "GPS가 꺼졌습니다. GPS를 켜주세요.", Toast.LENGTH_LONG).show();
            }
        };


        /*
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //What to do on back clicked
            }
        });
        */
    }
    public ViewPager getViewPager() {
        if (null == pager) {
            pager = (ViewPager) findViewById(R.id.startPager);
        }
        return pager;
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;
        FragmentManager fm;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            fm = fragmentManager;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return new StartSpeedFragment();
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return new StartMapFragment();
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 0){
                return "속도계";
            }else if(position == 1){
                return "지도";
            }else{
                return null;
            }
        }
    }

    private class setAdapterTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            pager.setAdapter(adapterViewPager);
        }
    }


    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        String[] providers = {LocationManager.GPS_PROVIDER};
        //List<String> providers = locationManager.getProviders(true);
        //providers.add(LocationManager.GPS_PROVIDER);
        //providers.add(LocationManager.NETWORK_PROVIDER);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionCheck();

                return null;
            }

            //Toast.makeText(getApplicationContext(), "프로바이더     " + provider, Toast.LENGTH_LONG).show();
            Location location = locationManager.getLastKnownLocation(provider);
            if (location == null) {
                //Toast.makeText(getApplicationContext(), "프로바이더     컨티뉴         " + provider, Toast.LENGTH_LONG).show();
                continue;
            }
            if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = location;
            }
        }
        return bestLocation;
    }

    private void permissionCheck() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(StartActivity.this, "권한 승인됨.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(StartActivity.this, "권한 승인 안됨." + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("권한을 승인하지 않으시면 이 서비스를 사용하실 수 없습니다.\n\n권한을 승인해주세요.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();

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

    public void startPause(View view){
        if(!timerOnCheck){
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                alertCheckGPS();
            }else {
                startTimerTask();
                timerOnCheck = true;
                startPause.setText("pause");
                startPause.setBackgroundColor(0xFFE7902E);
            }
        }else{
            if(!timerPauseCheck){
                pauseTimerTask();
                startPause.setText("start");
                startPause.setBackgroundColor(0xFF5E7AC8);
            }else{
                startTimerTask();
                startPause.setText("pause");
                startPause.setBackgroundColor(0xFFE7902E);
            }
        }
    }

    public void stop(View view){
        if(timerOnCheck){
            timerOnCheck = false;
            stopTimerTask();
        }else{
            Toast.makeText(getApplicationContext(),"이미 꺼져있습니다.",Toast.LENGTH_LONG).show();
        }
    }

    private void startTimerTask() {
        if (ActivityCompat.checkSelfPermission(StartActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(StartActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionCheck();
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 3, locationListener);

        Toast.makeText(getApplicationContext(),"주행 시작하였습니다.",Toast.LENGTH_LONG).show();

        timerPauseCheck = false;
        m1000MsCountTimerTask = new TimerTask() {

            @Override
            public void run() {
                mCount++;
                timeRecord.post(new Runnable() {
                    @Override
                    public void run() {

                        int hour = mCount/3600;
                        int hourRest = mCount%3600;
                        int minute = hourRest/60;
                        int minuteRest = hourRest%60;
                        int second = minuteRest;

                        timeRecord.setText(String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second));
                    }
                });
            }
        };
        mTimer.schedule(m1000MsCountTimerTask, 1000, 1000);
    }

    private void stopTimerTask() {
        if (m1000MsCountTimerTask != null) {
            m1000MsCountTimerTask.cancel();
            m1000MsCountTimerTask = null;

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionCheck();
            }
            locationManager.removeUpdates(locationListener);

            //mCount = 0;
        }

       //timeRecord.setText("00:00:00");
    }

    private void pauseTimerTask() {
        if (m1000MsCountTimerTask != null) {
            timerPauseCheck = true;
            m1000MsCountTimerTask.cancel();
            previousLocation = null;

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionCheck();
            }
            locationManager.removeUpdates(locationListener);

            Toast.makeText(getApplicationContext(),"주행 일시 정지하였습니다.",Toast.LENGTH_LONG).show();
        }
    }

    protected void requestMe() { //유저의 정보를 받아오는 함수
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    finish();
                } else {
                    Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {


                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }

            @Override
            public void onNotSignedUp() {

            } // 카카오톡 회원이 아닐 시 showSignup(); 호출해야함

            @Override
            public void onSuccess(UserProfile userProfile) {  //성공 시 userProfile 형태로 반환
                kakaoId = userProfile.getId() + "";
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }else{

        }
        return super.onOptionsItemSelected(item);
    }

    public void onStop(){
        super.onStop();

        mTimer.cancel();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionCheck();
        }
        locationManager.removeUpdates(locationListener);
    }

    protected void onDestroy() {
        mTimer.cancel();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionCheck();
        }
        locationManager.removeUpdates(locationListener);
        super.onDestroy();
    }

}
