package com.example.lkj.bicycleproject.Kakao_Login;

/**
 * Created by hp on 2016-01-26.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.example.lkj.bicycleproject.Connection.Connect;
import com.example.lkj.bicycleproject.Connection.WebHook;
import com.example.lkj.bicycleproject.MainActivity;
import com.example.lkj.bicycleproject.R;
import com.example.lkj.bicycleproject.SignUpActivity;
import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;

public class KakaoSignupActivity extends Activity{
    String kakaoId;
    /**
     * Main으로 넘길지 가입 페이지를 그릴지 판단하기 위해 me를 호출한다.
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestMe();
    }

    /**
     * 사용자의 상태를 알아 보기 위해 me API 호출을 한다.
     */
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
                    redirectLoginActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {

                redirectLoginActivity();
            }

            @Override
            public void onNotSignedUp() {} // 카카오톡 회원이 아닐 시 showSignup(); 호출해야함

            @Override
            public void onSuccess(UserProfile userProfile) {  //성공 시 userProfile 형태로 반환
                kakaoId = userProfile.getId() + "";

                JSONObject json = new JSONObject();
                try {
                    json.put("kakao",kakaoId);
                    new checkLogin().execute(getResources().getString(R.string.server_ip) + "/CheckLogin.php", json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        });
    }

    protected void redirectLoginActivity() {
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    private class checkLogin extends AsyncTask<String, Void, JSONObject> {
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
                        Intent intent = new Intent(KakaoSignupActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }else if(result.getString("code").compareTo("1") == 0) {
                        Intent intent = new Intent(KakaoSignupActivity.this, SignUpActivity.class);
                        startActivity(intent);
                        finish();

                    }else{
                        Toast.makeText(KakaoSignupActivity.this,"서버 에러",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}