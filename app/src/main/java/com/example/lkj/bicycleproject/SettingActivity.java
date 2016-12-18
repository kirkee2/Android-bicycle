package com.example.lkj.bicycleproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lkj.bicycleproject.Connection.Connect;
import com.example.lkj.bicycleproject.Connection.WebHook;
import com.example.lkj.bicycleproject.Kakao_Login.KakaoSignupActivity;
import com.example.lkj.bicycleproject.Kakao_Login.LoginActivity;
import com.example.lkj.bicycleproject.ListView.AgeAdapter;
import com.example.lkj.bicycleproject.ListView.AgeList;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {
    private String kakaoId;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        requestMe();

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
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
                    Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onNotSignedUp() {} // 카카오톡 회원이 아닐 시 showSignup(); 호출해야함

            @Override
            public void onSuccess(UserProfile userProfile) {  //성공 시 userProfile 형태로 반환
                kakaoId = userProfile.getId() + "";
            }
        });
    }

    /*

    public void logout(View v){
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
    */


    /*
    public void unlink(View v) {
        final String appendMessage = getString(R.string.com_kakao_confirm_unlink);
        new AlertDialog.Builder(this)
                .setMessage(appendMessage)
                .setPositiveButton(getString(R.string.com_kakao_ok_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserManagement.requestUnlink(new UnLinkResponseCallback() {
                                    @Override
                                    public void onFailure(ErrorResult errorResult) {
                                    }

                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) {
                                        startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                                        finish();
                                    }

                                    @Override
                                    public void onNotSignedUp() {
                                        startActivity(new Intent(SettingActivity.this, KakaoSignupActivity.class));
                                        finish();
                                    }

                                    @Override
                                    public void onSuccess(Long result) {
                                        JSONObject json = new JSONObject();
                                        try {
                                            json.put("kakao",kakaoId);
                                            new Unlink().execute(getResources().getString(R.string.server_ip) + "/Unlink.php", json.toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.com_kakao_cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

    }
    */

    public void logOut(View view){
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    public void unlink(View view){
        final String appendMessage = getString(R.string.com_kakao_confirm_unlink);
        new AlertDialog.Builder(SettingActivity.this)
                .setMessage(appendMessage)
                .setPositiveButton(getString(R.string.com_kakao_ok_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserManagement.requestUnlink(new UnLinkResponseCallback() {
                                    @Override
                                    public void onFailure(ErrorResult errorResult) {
                                    }

                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) {
                                        startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                                        Toast.makeText(getApplicationContext(),"세션이 종료되었습니다. 다시 로그인해주세요.",Toast.LENGTH_LONG).show();
                                        finish();
                                    }

                                    @Override
                                    public void onNotSignedUp() {
                                        startActivity(new Intent(SettingActivity.this, KakaoSignupActivity.class));
                                        Toast.makeText(getApplicationContext(),"세션이 종료되었습니다. 다시 로그인해주세요.",Toast.LENGTH_LONG).show();
                                        finish();
                                    }

                                    @Override
                                    public void onSuccess(Long result) {
                                        JSONObject json = new JSONObject();
                                        try {
                                            json.put("kakao",kakaoId);
                                            new Unlink().execute(getResources().getString(R.string.server_ip) + "/Unlink.php", json.toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.com_kakao_cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    private class Unlink extends AsyncTask<String, Void, JSONObject> {
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
                Toast.makeText(getApplicationContext(), "정보를 받는 도중 에러가 났습니다. 다시 한번 확인해주세요.", Toast.LENGTH_LONG).show();
            } else {
                try {
                    if(result.getString("code").compareTo("0") == 0) {
                        //finishAffinity();
                        Toast.makeText(SettingActivity.this, "탈퇴에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }else if(result.getString("code").compareTo("1") == 0) {
                        Toast.makeText(SettingActivity.this,"탈퇴에 실패하였습니다"+result.toString(),Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(SettingActivity.this,"서버 에러",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
