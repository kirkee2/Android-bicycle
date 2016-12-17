package com.example.lkj.bicycleproject;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lkj.bicycleproject.Connection.Connect;
import com.example.lkj.bicycleproject.Kakao_Login.LoginActivity;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity {

    private String kakaoId;

    private Toolbar toolbar;

    private TextView humidity;
    private TextView temperature;
    private TextView wind;
    private TextView rainPercent;

    private TextView record;
    private TextView address;
    private TextView time;
    private TextView temperatureKor;
    private TextView windKor;

    private ImageView temperatureImage;
    private ImageView windImage;

    private LocationManager locationManager;
    private Location location;
    private ArrayList<String> addressInfo;
    private LocationListener locationListener;

    private LinearLayout mainLayout;
    private RelativeLayout infoLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        requestMe();

        mainLayout = (LinearLayout)findViewById(R.id.mainLayout);
        infoLayout = (RelativeLayout)findViewById(R.id.infoLayout);

        temperature = (TextView) findViewById(R.id.temperature);
        rainPercent = (TextView) findViewById(R.id.rainPercent);
        wind = (TextView) findViewById(R.id.wind);
        humidity = (TextView) findViewById(R.id.humidity);
        record = (TextView) findViewById(R.id.record);
        address = (TextView) findViewById(R.id.address);
        time = (TextView) findViewById(R.id.time);
        temperatureKor = (TextView) findViewById(R.id.temperatureKor);
        windKor = (TextView) findViewById(R.id.windKor);

        temperatureImage = (ImageView) findViewById(R.id.temperatureImage);
        windImage = (ImageView) findViewById(R.id.windImage);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alertCheckGPS();
        }

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                if (location != null) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();

                    addressInfo = getAddressArray(Double.parseDouble(String.format("%.2f", lat)),Double.parseDouble(String.format("%.2f", lng)));

                    address.setText(addressInfo.get(0) + " " + addressInfo.get(1) + " " + addressInfo.get(2));

                    new Weather().execute(addressInfo.get(0), addressInfo.get(1), addressInfo.get(2));

                    //Toast.makeText(getApplicationContext(),"onLocationChanged " + addressInfo.get(0) +" " + addressInfo.get(1) +" "+ addressInfo.get(2),Toast.LENGTH_LONG).show();

                    infoLayout.setVisibility(View.INVISIBLE);
                    mainLayout.setVisibility(View.VISIBLE);
                }else{
                    infoLayout.setVisibility(View.VISIBLE);
                    mainLayout.setVisibility(View.INVISIBLE);
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Location lastKnownLocation = getLastKnownLocation();

                if (lastKnownLocation != null) {
                    double lat = lastKnownLocation.getLatitude();
                    double lng = lastKnownLocation.getLongitude();

                    addressInfo = getAddressArray(Double.parseDouble(String.format("%.2f", lat)),Double.parseDouble(String.format("%.2f", lng)));

                    address.setText(addressInfo.get(0) + " " + addressInfo.get(1) + " " + addressInfo.get(2));

                    new Weather().execute(addressInfo.get(0), addressInfo.get(1), addressInfo.get(2));

                    Toast.makeText(getApplicationContext(),"onProviderEnabled " + addressInfo.get(0) +" " + addressInfo.get(1) +" "+ addressInfo.get(2),Toast.LENGTH_LONG).show();


                    infoLayout.setVisibility(View.INVISIBLE);
                    mainLayout.setVisibility(View.VISIBLE);

                    new Weather().execute(addressInfo.get(0), addressInfo.get(1), addressInfo.get(2));
                }
                else{
                    infoLayout.setVisibility(View.VISIBLE);
                    mainLayout.setVisibility(View.INVISIBLE);
                }
            }

            public void onProviderDisabled(String provider) {
                infoLayout.setVisibility(View.VISIBLE);
                mainLayout.setVisibility(View.INVISIBLE);
            }
        };

        if (ActivityCompat.checkSelfPermission(WeatherActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionCheck();
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 120000, 5000, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 50000, locationListener);


        Location lastKnownLocation = getLastKnownLocation();

        if (lastKnownLocation != null) {
            double lat = lastKnownLocation.getLatitude();
            double lng = lastKnownLocation.getLongitude();

            addressInfo = getAddressArray(Double.parseDouble(String.format("%.2f", lat)),Double.parseDouble(String.format("%.2f", lng)));

            address.setText(addressInfo.get(0) + " " + addressInfo.get(1) + " " + addressInfo.get(2));

            new Weather().execute(addressInfo.get(0), addressInfo.get(1), addressInfo.get(2));

            //Toast.makeText(getApplicationContext(),"onCreate " + addressInfo.get(0) +" " + addressInfo.get(1) +" "+ addressInfo.get(2),Toast.LENGTH_LONG).show();

            infoLayout.setVisibility(View.INVISIBLE);
            mainLayout.setVisibility(View.VISIBLE);
        }else{
            infoLayout.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.INVISIBLE);
        }
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


    private class Weather extends AsyncTask<String, String, Document> {
        protected Document doInBackground(String... urls) {

            JSONArray siResult;
            JSONArray guResult;
            JSONArray dongResult;
            String si = urls[0];
            String gu = urls[1];
            String dong = urls[2];
            Connect con = new Connect("http://www.kma.go.kr/DFSROOT/POINT/DATA/top.json.txt");

            String siCode = null;
            String guCode = null;
            String dongCode = null;

            siResult = con.post2(con.getURL());

            if (siResult == null) {
                //publishProgress(1+"");
                return null;
            } else {
                for (int i = 0; i < siResult.length(); i++) {
                    try {
                        JSONObject jsonObject = siResult.getJSONObject(i);

                        if (si.compareTo((String) jsonObject.get("value")) == 0) {
                            siCode = (String) jsonObject.get("code");
                            //publishProgress("시코드 : " + siCode);
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        //publishProgress(2+"");
                        return null;
                    }
                }

                if (siCode == null) {
                    //publishProgress(3+"");
                    return null;
                } else {
                    con = new Connect("http://www.kma.go.kr/DFSROOT/POINT/DATA/mdl." + siCode + ".json.txt");

                    guResult = con.post2(con.getURL());

                    if (guResult == null) {
                        //publishProgress(4+"");
                        return null;
                    } else {
                        for (int i = 0; i < guResult.length(); i++) {
                            try {
                                JSONObject jsonObject2 = guResult.getJSONObject(i);

                                if (gu.compareTo((String) jsonObject2.get("value")) == 0) {
                                    guCode = (String) jsonObject2.get("code");
                                    //publishProgress("구코드 : " + guCode);

                                    break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                //publishProgress(5+"");
                                return null;
                            }
                        }
                    }
                }

                if (guCode == null) {
                    //publishProgress(6+"");
                    return null;
                } else {
                    con = new Connect("http://www.kma.go.kr/DFSROOT/POINT/DATA/leaf." + guCode + ".json.txt");

                    http://www.kma.go.kr/DFSROOT/POINT/DATA/leaf.11590.json.txt

                    dongResult = con.post2(con.getURL());

                    if (dongResult == null) {
                        //publishProgress(7+"");
                        return null;
                    } else {

                        for (int i = 0; i < dongResult.length(); i++) {
                            try {
                                JSONObject jsonObject3 = dongResult.getJSONObject(i);

                                if (dong.compareTo((String) jsonObject3.get("value")) == 0) {
                                    dongCode = (String) jsonObject3.get("code");
                                    //publishProgress("동코드 : " + dongCode);
                                    break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                //publishProgress(8+"");
                                return null;
                            }
                        }
                    }
                }

                if (dongCode == null) {
                    //publishProgress(9+"");
                    return null;
                } else {

                    Document doc;
                    con = new Connect("http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=" + dongCode);

                    doc = con.post4(con.getURL());

                    return doc;
                }
            }
        }

        protected void onProgressUpdate(String... progress)
        {
            //Toast.makeText(getApplicationContext(),"디버깅 "+ progress[0]+"",Toast.LENGTH_LONG).show();
        }

        protected void onPostExecute(Document result) {
            if (result == null) {
                infoLayout.setVisibility(View.VISIBLE);
                mainLayout.setVisibility(View.INVISIBLE);

                Toast.makeText(getApplicationContext(), "날씨 정보를 받는 도중 에러가 났습니다. 다시 한번 확인해주세요.", Toast.LENGTH_LONG).show();
            } else {
                String temp;
                String wfKor;
                String pop;
                String ws;
                String wd;
                String reh;
                String hour;
                String pubDate;

                Element root = result.getDocumentElement();
                // System.out.println(root.getTagName());

                Node xmlNode = root.getElementsByTagName("channel").item(0);

                Node xmlNodePubDate = ((Element) xmlNode).getElementsByTagName(
                        "pubDate").item(0);
                pubDate = xmlNodePubDate.getTextContent();

                xmlNode = root.getElementsByTagName("data").item(0);

                Node xmlNodeTemp = ((Element) xmlNode).getElementsByTagName(
                        "temp").item(0);
                Node xmlNodeWfKor = ((Element) xmlNode).getElementsByTagName(
                        "wfKor").item(0);
                Node xmlNodePop = ((Element) xmlNode).getElementsByTagName(
                        "pop").item(0);
                Node xmlNodeWs = ((Element) xmlNode).getElementsByTagName(
                        "ws").item(0);
                Node xmlNodeWd = ((Element) xmlNode).getElementsByTagName(
                        "wd").item(0);
                Node xmlNodeReh = ((Element) xmlNode).getElementsByTagName(
                        "reh").item(0);
                Node xmlNodeHour = ((Element) xmlNode).getElementsByTagName(
                        "hour").item(0);

                temp = xmlNodeTemp.getTextContent();
                wfKor = xmlNodeWfKor.getTextContent();
                pop = xmlNodePop.getTextContent();
                ws = xmlNodeWs.getTextContent();
                wd = xmlNodeWd.getTextContent();
                reh = xmlNodeReh.getTextContent();
                hour = xmlNodeHour.getTextContent();

                record.setText(pubDate + " 기상청 제공");
                time.setText((Integer.parseInt(hour) - 3) + "시~" + Integer.parseInt(hour) + "시");

                temperature.setText(temp+"℃");
                temperatureKor.setText("(" +wfKor+ ")");
                if(wfKor.compareTo("맑음") == 0){
                    temperatureImage.setImageResource(R.drawable.ic_wb_sunny_black_24dp);
                }else if(wfKor.compareTo("구름 조금") == 0 ){
                    temperatureImage.setImageResource(R.drawable.sunny_cloud);
                }else if(wfKor.compareTo("구름 많음") == 0 || wfKor.compareTo("흐림") == 0 ){
                    temperatureImage.setImageResource(R.drawable.ic_wb_cloudy_black_24dp);
                }else if(wfKor.compareTo("비") == 0 || wfKor.compareTo("눈/비") == 0 || wfKor.compareTo("눈") == 0 ){
                    temperatureImage.setImageResource(R.drawable.rain);
                }else{
                    temperatureImage.setImageResource(R.drawable.ic_wb_sunny_black_24dp);
                }
                //temperatureImage.setImageResource(R.drawable.ic_wb_sunny_black_24dp);

                wind.setText(String.format("%.2f", Double.valueOf(ws).doubleValue()));
                if(wd.compareTo("0")==0){
                    windKor.setText("(북)");
                }else if(wd.compareTo("1")==0){
                    windKor.setText("(북동)");
                    windImage.setRotation(45);
                }else if(wd.compareTo("2")==0){
                    windImage.setRotation(90);
                    windKor.setText("(동)");
                }else if(wd.compareTo("3")==0){
                    windImage.setRotation(135);
                    windKor.setText("(남동)");
                }else if(wd.compareTo("4")==0){
                    windImage.setRotation(180);
                    windKor.setText("(남)");
                }else if(wd.compareTo("5")==0){
                    windImage.setRotation(225);
                    windKor.setText("(남서)");
                }else if(wd.compareTo("6")==0){
                    windImage.setRotation(270);
                    windKor.setText("(서)");
                }else if(wd.compareTo("7")==0){
                    windImage.setRotation(315);
                    windKor.setText("(북서)");
                }else{
                    windKor.setText("(북)");
                    windImage.setImageResource(R.drawable.ic_arrow_n_black_24dp);
                }

                rainPercent.setText(pop + "%");
                humidity.setText(reh + "%");


                //longitudeField.setText("발표자료 : " + pubDate + " 기상청 제공" + "\n시각 : " + (Integer.parseInt(hour) - 3) + "시~" + Integer.parseInt(hour) + "시" + "\n현재 온도 : " + temp + "\n날씨 상황 :  " + wfKor + "\n강수확률 : " + pop + "\n풍속 : " + ws + "\n풍향 : " + wd + "\n습도 :  " + reh);
            }
        }
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        String[] providers = {LocationManager.GPS_PROVIDER,LocationManager.NETWORK_PROVIDER};
        //List<String> providers = locationManager.getProviders(true);
        //providers.add(LocationManager.GPS_PROVIDER);
        //providers.add(LocationManager.NETWORK_PROVIDER);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(WeatherActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                Location lastKnownLocation = getLastKnownLocation();

                //Toast.makeText(getApplicationContext(), "라스토논로케이션     " + lastKnownLocation, Toast.LENGTH_LONG).show();

                if (lastKnownLocation != null) {
                    double lat = lastKnownLocation.getLatitude();
                    double lng = lastKnownLocation.getLongitude();

                    addressInfo = getAddressArray(Double.parseDouble(String.format("%.2f", lat)),Double.parseDouble(String.format("%.2f", lng)));

                    address.setText(addressInfo.get(0) + " " + addressInfo.get(1) + " " + addressInfo.get(2));

                    new Weather().execute(addressInfo.get(0), addressInfo.get(1), addressInfo.get(2));

                    infoLayout.setVisibility(View.INVISIBLE);
                    mainLayout.setVisibility(View.VISIBLE);


                    new Weather().execute(addressInfo.get(0), addressInfo.get(1), addressInfo.get(2));

                }else{
                    infoLayout.setVisibility(View.VISIBLE);
                    mainLayout.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                //Toast.makeText(WeatherActivity.this, "권한 승인 안됨." + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("권한을 승인하지 않으시면 이 서비스를 사용하실 수 없습니다.\n\n권한을 승인해주세요.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
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
                    Intent intent = new Intent(WeatherActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {


                Intent intent = new Intent(WeatherActivity.this, LoginActivity.class);
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


    public ArrayList<String> getAddressArray(double latitude, double longitude) {
        ArrayList<String> addressInfo = new ArrayList<String>();

        //위치정보를 활용하기 위한 구글 API 객체
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        //주소 목록을 담기 위한 HashMap
        List<Address> list = null;

        try {
            list = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (list == null) {
            return null;
        }

        if (list.size() > 0) {
            Address addr = list.get(0);

            addressInfo.clear();

            addressInfo.add(addr.getAdminArea() + "");
            addressInfo.add(addr.getLocality() + "");
            addressInfo.add(addr.getThoroughfare() + "");
            addressInfo.add(addr.getFeatureName() + "");

            //Toast.makeText(getApplicationContext(),"postal = " +addr.getPostalCode() + " addr = " + addr.toString(),Toast.LENGTH_LONG).show();
        }

        return addressInfo;
    }

    public void onStop(){
        super.onStop();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionCheck();
        }
        locationManager.removeUpdates(locationListener);
    }


    public void onDestroy() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionCheck();
        }
        locationManager.removeUpdates(locationListener);

        super.onDestroy();
    }
}
