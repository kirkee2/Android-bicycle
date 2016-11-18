package com.example.lkj.bicycleproject.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lkj.bicycleproject.Connection.Connect;
import com.example.lkj.bicycleproject.Connection.Weather;
import com.example.lkj.bicycleproject.Connection.WebHook;
import com.example.lkj.bicycleproject.GPSStatusBroadcastReceiver;
import com.example.lkj.bicycleproject.Kakao_Login.LoginActivity;
import com.example.lkj.bicycleproject.MainActivity;
import com.example.lkj.bicycleproject.R;
import com.example.lkj.bicycleproject.StartActivity;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainFragment extends Fragment implements LocationListener {
    private LocationManager locationManager;
    private String provider;
    private TextView latituteField;
    private TextView longitudeField;
    private PermissionListener permissionlistener;
    private Location lastKnownLocation;
    String[] tmp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        latituteField = (TextView) view.findViewById(R.id.textView13);
        longitudeField = (TextView) view.findViewById(R.id.textView14);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(getActivity(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            new TedPermission(getActivity().getApplicationContext())
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                    .check();
        } else {
            if (provider != null && locationManager.isProviderEnabled(provider)) {

            }else{
                List<String> list = locationManager.getAllProviders();

                for (int i = 0; i < list.size(); i++) {
                    //장치 이름 하나 얻기
                    String temp = list.get(i);

                    //사용 가능 여부 검사
                    if (locationManager.isProviderEnabled(temp)) {
                        provider = temp;
                        break;
                    }
                }
            }

            Toast.makeText(getActivity().getApplicationContext(),"vasdsadd = " + provider + " asddsad = " +locationManager.isProviderEnabled(provider),Toast.LENGTH_LONG).show();

            Location location = locationManager.getLastKnownLocation(provider);

            if (location == null) {
                Toast.makeText(getActivity().getApplicationContext(),"location = " + location ,Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity(), "사용가능한 위치 정보 제공자가 없습니다.", Toast.LENGTH_SHORT).show();
            } else {
                //최종 위치에서 부터 이어서 GPS 시작...
                Toast.makeText(getActivity().getApplicationContext(),"location = " + location ,Toast.LENGTH_LONG).show();
                onLocationChanged(location);
            }
        }

        return view;
    }

    @Override
    public void onResume() {
        //Activity LifrCycle 관련 메서드는 무조건 상위 메서드 호출 필요
        super.onResume();

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            new TedPermission(getActivity().getApplicationContext())
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                    .check();
        } else {
            locationManager.requestLocationUpdates(provider, 500, 1, this);
        }
    }

    public void btStart(View v){
        startActivity(new Intent(getActivity(), StartActivity.class));
    }
    @Override
    public void onPause() {
        //Activity LifrCycle 관련 메서드는 무조건 상위 메서드 호출 필요
        super.onPause();

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            new TedPermission(getActivity().getApplicationContext())
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                    .check();
        } else {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        lastKnownLocation = location;


        new GetWeather().execute(lat,lng);
        // String이외의 데이터 형을 String으로 변환하는 메서드
        latituteField.setText(String.valueOf(lat) + " / " + String.valueOf(lng));
        // String이외의 데이터 형을 String으로 변화하는 꼼수~!!
        longitudeField.setText(getAddress(lat, lng));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public String getAddress(double latitude, double longitude){
        String address = null;

        //위치정보를 활용하기 위한 구글 API 객체
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

        //주소 목록을 담기 위한 HashMap
        List<Address> list = null;

        try{
            list = geocoder.getFromLocation(latitude, longitude, 1);
        } catch(Exception e){
            e.printStackTrace();
        }

        if(list == null){
            return null;
        }

        if(list.size() > 0){
            Address addr = list.get(0);
            address = addr.getCountryName() + " "
                    + addr.getAdminArea() + " "
                    + addr.getLocality() + " "
                    + addr.getThoroughfare() + " "
                    + addr.getFeatureName();

            Toast.makeText(getActivity().getApplicationContext(),"postal = " +addr.getPostalCode() + " addr = " + addr.toString(),Toast.LENGTH_LONG).show();
        }

        return address;
    }

    private class GetWeather extends AsyncTask<Double, Void, Void> {
        @Override
        protected Void doInBackground(Double... params) {
            Weather weather = new Weather(params[0],params[1]);

            //tmp = weather.getTemp();
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {

            //new WebHook().execute("첫번째 온도 = " + tmp[0] + " 두번째 온도 = " + tmp[1] +"세번째 온도 = "+ tmp[2],null,null);
        }
    }

}
