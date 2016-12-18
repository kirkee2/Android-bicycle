package com.example.lkj.bicycleproject.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.lkj.bicycleproject.Connection.Connect;
import com.example.lkj.bicycleproject.Kakao_Login.LoginActivity;
import com.example.lkj.bicycleproject.MainActivity;
import com.example.lkj.bicycleproject.R;
import com.example.lkj.bicycleproject.RecyclerView.MyRecyclerAdapter;
import com.example.lkj.bicycleproject.RecyclerView.RecordRecyclerAdapter;
import com.example.lkj.bicycleproject.RecyclerView.RecordRow;
import com.example.lkj.bicycleproject.RecyclerView.RecyclerItemClickListener;
import com.example.lkj.bicycleproject.RecyclerView.RoadRecyclerAdapter;
import com.example.lkj.bicycleproject.RecyclerView.RoadRow;
import com.example.lkj.bicycleproject.RecyclerView.Row;
import com.example.lkj.bicycleproject.StartActivity;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecordFragment extends Fragment {

    private static RecyclerView recyclerView;
    private static String kakaoId;
    private static ArrayList<Boolean> auth;
    private static List<RecordRow> recordList;
    private ImageButton qrCode;
    static FragmentActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        activity = (FragmentActivity)view.getContext();

        auth = new ArrayList<Boolean>();

        auth.add(false);
        auth.add(false);
        auth.add(false);

        requestMe();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        qrCode = (ImageButton) view.findViewById(R.id.qrCode);

        qrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);

                // QR Code Capture Activity Orientation Set : degree unit
                integrator.setOrientation(90);

                // Scan View finder size : pixel unit
                integrator.addExtra(Intents.Scan.HEIGHT, 600);
                integrator.addExtra(Intents.Scan.WIDTH, 600);

                // Capture View Start
                integrator.initiateScan();
            }
        });


        return view;
    }


    static public void Refresh(){
        JSONObject json = new JSONObject();

        try {
            json.put("kakao", kakaoId);
            new UserAuthInfo().execute("http://kirkee2.cafe24.com/UserAuthInfo.php", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void requestMe() { //유저의 정보를 받아오는 함수
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {

                Toast.makeText(getActivity().getApplicationContext(),"세션이 종료됬습니다. 다시 로그인 해주세요.",Toast.LENGTH_LONG).show();
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {


                Toast.makeText(getActivity().getApplicationContext(),"세션이 종료됬습니다. 다시 로그인 해주세요.",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);

            }

            @Override
            public void onNotSignedUp() {

                Toast.makeText(getActivity().getApplicationContext(),"세션이 종료됬습니다. 다시 로그인 해주세요.",Toast.LENGTH_LONG).show();
            } // 카카오톡 회원이 아닐 시 showSignup(); 호출해야함

            @Override
            public void onSuccess(UserProfile userProfile) {  //성공 시 userProfile 형태로 반환

                kakaoId = userProfile.getId() + "";

                JSONObject json = new JSONObject();


                try {
                    json.put("kakao", kakaoId);
                    new UserAuthInfo().execute(getResources().getString(R.string.server_ip) + "/UserAuthInfo.php", json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
                    new UserAuthInfo().execute(getResources().getString(R.string.server_ip) + "/UserAuthInfo.php", json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class AuthInfo extends AsyncTask<String, Void, JSONArray> {
        protected JSONArray doInBackground(String... urls) {

            try {
                JSONObject jsonObj = new JSONObject(urls[1]);

                Connect con = new Connect(urls[0]);

                return con.postJsonArray(con.getURL(), jsonObj);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(JSONArray result) {
            if (result == null) {
            } else {
                try {
                    JSONObject jsonTmp = (JSONObject) result.get(0);

                    if (jsonTmp.getString("code").compareTo("0") == 0) {
                        JSONArray jsonArray = new JSONArray();
                        for (int i = 1; i < result.length(); i++) {
                            JSONObject tmp = (JSONObject) result.get(i);

                            tmp.put("auth", auth.get(i - 1));
                            jsonArray.put(tmp);
                        }

                        initData(result);
                    } else {
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class UserAuthInfo extends AsyncTask<String, Void, JSONObject> {
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
                try {
                    if (result.getString("code").compareTo("0") == 0) {
                        if (result.getString("auth1").compareTo("1") == 0) {
                            auth.set(0, true);
                        }
                        if (result.getString("auth2").compareTo("1") == 0) {
                            auth.set(1, true);
                        }
                        if (result.getString("auth3").compareTo("1") == 0) {
                            auth.set(2, true);
                        }
                    } else if (result.getString("code").compareTo("1") == 0) {
                    }

                    JSONObject json = new JSONObject();

                    try {
                        json.put("hoho", "hoho");
                        new AuthInfo().execute("http://kirkee2.cafe24.com/AuthInfo.php", json.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }
    }

    /*
    private void initData(){
        albumList = new ArrayList<Row>();

        for (int i =0; i<4; i ++){

            Row album = new Row();
            album.setTitle(i+"번째 제목");
            //album.setImage(R.drawable.splash_screen);
            albumList.add(album);
        }

        myRecyclerAdapter= new MyRecyclerAdapter(albumList,R.layout.row);

        recyclerView.setAdapter(myRecyclerAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 2));
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }
    */

    private static void initData(JSONArray jsonArray) {

        //recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        recordList = new ArrayList<RecordRow>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                RecordRow recordRow = new RecordRow();

                recordRow.setTitle(jsonObject.getString("title"));
                recordRow.setId(jsonObject.getString("id"));
                recordRow.setImage("http://kirkee2.cafe24.com/authImage/auth" + jsonObject.getString("id") + ".png");
                recordRow.setAuthed(jsonObject.getBoolean("auth"));
                recordList.add(recordRow);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        recyclerView.setAdapter(new RecordRecyclerAdapter(recordList, R.layout.record_row));
        recyclerView.setLayoutManager(new GridLayoutManager(activity.getApplicationContext(), 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    /*
    private void changeData(){
        albumList = new ArrayList<Row>();

        for (int i =0; i<4; i ++){

            Row album = new Row();
            album.setTitle(i+"메롱");
            //album.setImage(R.drawable.splash_screen);
            albumList.add(album);
        }

        myRecyclerAdapter= new MyRecyclerAdapter(albumList,R.layout.row);

        recyclerView.setAdapter(myRecyclerAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 2));
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }
    */
}
