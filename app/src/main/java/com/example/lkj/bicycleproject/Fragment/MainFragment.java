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
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lkj.bicycleproject.Connection.Connect;
import com.example.lkj.bicycleproject.Connection.WebHook;
import com.example.lkj.bicycleproject.Kakao_Login.LoginActivity;
import com.example.lkj.bicycleproject.MainActivity;
import com.example.lkj.bicycleproject.R;
import com.example.lkj.bicycleproject.StartActivity;
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


public class MainFragment extends Fragment {

    private String kakaoId;

    private TextView todayDistance;
    private TextView todayTimeRecord;
    private TextView todayCalory;

    private ImageView start;

    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    */

    @Override
    public void onResume() {
        requestMe();

        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        todayDistance = (TextView) view.findViewById(R.id.todayDistance);
        todayTimeRecord = (TextView) view.findViewById(R.id.todayTimeRecord);
        todayCalory = (TextView) view.findViewById(R.id.todayCalory);

        start  = (ImageView) view.findViewById(R.id.start);

        requestMe();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), StartActivity.class));
            }
        });

        return view;
    }


    protected void requestMe() { //유저의 정보를 받아오는 함수
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    getActivity().finish();
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    getActivity().finish();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                getActivity().finish();
            }

            @Override
            public void onNotSignedUp() {

            } // 카카오톡 회원이 아닐 시 showSignup(); 호출해야함

            @Override
            public void onSuccess(UserProfile userProfile) {  //성공 시 userProfile 형태로 반환
                kakaoId = userProfile.getId() + "";

                JSONObject json = new JSONObject();

                try {
                    json.put("kakao", kakaoId);
                    new RunningInfo().execute(getResources().getString(R.string.server_ip) + "/RunningInfo.php", json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class RunningInfo extends AsyncTask<String, Void, JSONObject> {
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
                /*
                todayDistance.clearComposingText();
                todayTimeRecord.clearComposingText();
                todayCalory.clearComposingText();
                */

                Toast.makeText(getActivity().getApplicationContext(), "이동 정보를 받는 도중 에러가 났습니다. 다시 한번 확인해주세요.", Toast.LENGTH_LONG).show();
                todayDistance.setText("0m");
                todayTimeRecord.setText("00:00:00");
                todayCalory.setText("0Kcal");
            } else {
                try {
                    if(result.getString("code").compareTo("0") == 0) {

                        /*
                        todayDistance.clearComposingText();
                        todayTimeRecord.clearComposingText();
                        todayCalory.clearComposingText();
                        */

                        todayDistance.setText(result.getInt("todayDistance")+"m");

                        int hour = result.getInt("todayTimeRecord")/3600;
                        int restHour = result.getInt("todayTimeRecord")%3600;
                        int minute = restHour/60;
                        int restMinute = restHour%60;
                        int second = restMinute;

                        todayTimeRecord.setText(String.format("%02d", hour)+":"+String.format("%02d", minute)+":"+String.format("%02d", second));
                        int caloryInt = result.getInt("todayDistance") / 40;
                        todayCalory.setText(caloryInt+"Kcal");
                    }else if(result.getString("code").compareTo("1") == 0) {

                        todayDistance.clearComposingText();
                        todayTimeRecord.clearComposingText();
                        todayCalory.clearComposingText();

                        todayDistance.setText("0m");
                        todayTimeRecord.setText("00:00:00");
                        todayCalory.setText("0Kcal");
                    }else{
                        Toast.makeText(getActivity(),"서버 에러",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
