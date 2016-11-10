package com.example.lkj.bicycleproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lkj.bicycleproject.Connection.Connect;
import com.example.lkj.bicycleproject.Connection.WebHook;
import com.example.lkj.bicycleproject.Kakao_Login.LoginActivity;
import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;
    Uri mImageCaptureUri;

    private ImageButton imageButton;
    private Spinner spinnerAge;
    private Spinner spinnerWeight;
    private int weightIndex = 0;
    private int ageIndex = 0;
    private RadioGroup rg;
    private RadioButton radioMan;
    private RadioButton radioWoman;
    private EditText editId;
    private Boolean idChecked;
    private String lastId = null;
    private String encodedImage;
    private String kakaoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        imageButton = (ImageButton) findViewById(R.id.imageAdd);
        //imageButton.setImageURI(Uri.parse(tmp[0]));
        spinnerAge = (Spinner) findViewById(R.id.spinnerAge);
        spinnerWeight = (Spinner) findViewById(R.id.spinnerWeight);
        rg = (RadioGroup) findViewById(R.id.rg);
        radioMan = (RadioButton) findViewById(R.id.radioMan);
        radioWoman = (RadioButton) findViewById(R.id.radioWoman);
        editId = (EditText) findViewById(R.id.editId);

        idChecked = false;

        Intent intent = getIntent();

        kakaoId = intent.getStringExtra("kakao");

        ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.spinnerAgeArray));

        spinnerAge.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        ageAdapter,
                        R.layout.contact_spinner_age_row_nothing_selected,
                        this));

        spinnerAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ageIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ageIndex = 0;
            }
        });

        ArrayAdapter<String> weightAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.spinnerWeightArray));

        spinnerWeight.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        weightAdapter,
                        R.layout.contact_spinner_weight_row_nothing_selected,
                        this));

        spinnerWeight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                weightIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                weightIndex =0;
            }
        });
    }

    public void onImageClick(View view) {
        DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doTakePhotoAction();
            }
        };

        DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doTakeAlbumAction();
            }
        };

        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };

        new AlertDialog.Builder(this)
                .setTitle("이미지 선택")
                .setPositiveButton("사진촬영", cameraListener)
                .setNeutralButton("앨범선택", albumListener)
                .setNegativeButton("취소", cancelListener)
                .show();
    }

    public void doTakePhotoAction() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    public void doTakeAlbumAction() {
        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, PICK_FROM_ALBUM);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(getBaseContext(), "resultCode : " + resultCode, Toast.LENGTH_SHORT).show();

        String encodedImageTmp = null;

        if (requestCode == PICK_FROM_CAMERA) {
            /*
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(mImageCaptureUri, "image/*");

            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);

            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

            startActivityForResult(intent, CROP_FROM_IMAGE);
            */
            if (data == null) {

            } else {
                mImageCaptureUri = data.getData();
            }

            try {
                Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                imageButton.setImageBitmap(image_bitmap);

                ByteArrayOutputStream baos0 = new ByteArrayOutputStream();

                image_bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos0);
                byte[] imageBytes0 = baos0.toByteArray();

                encodedImageTmp = Base64.encodeToString(imageBytes0, Base64.DEFAULT);

                encodedImage = encodedImageTmp;
            }catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (requestCode == PICK_FROM_ALBUM) {
            if (data == null) {

            } else {
                mImageCaptureUri = data.getData();
            }

            try {
                Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                imageButton.setImageBitmap(image_bitmap);


                ByteArrayOutputStream baos0 = new ByteArrayOutputStream();

                image_bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos0);
                byte[] imageBytes0 = baos0.toByteArray();

                encodedImageTmp = Base64.encodeToString(imageBytes0, Base64.DEFAULT);

                encodedImage = encodedImageTmp;

                //Toast.makeText(getBaseContext(), "name_Str : "+name_Str , Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void userId(View v) {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    finish();
                } else {
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }

            @Override
            public void onNotSignedUp() {
            } // 카카오톡 회원이 아닐 시 showSignup(); 호출해야함

            @Override
            public void onSuccess(final UserProfile userProfile) {  //성공 시 userProfile 형태로 반환
                Toast.makeText(getApplicationContext(), "id : " + userProfile.getId() + " user nickname : " + userProfile.getNickname() + " user thumbnail photo : " + userProfile.getThumbnailImagePath() + " user profile image : " + userProfile.getProfileImagePath(), Toast.LENGTH_LONG).show();


                //new DownloadImageTask().execute(userProfile.getProfileImagePath());


            }
        });
    }

    public void onDuplicationCheck(View view) {
        JSONObject json = new JSONObject();
        JSONObject result = null;

        try {
            if (editId.getText().toString().length() < 5 || editId.getText().toString().length() > 16) {
                Toast.makeText(SignUpActivity.this, "아이디는 5자 이상 16자 이하로 입력해주세요", Toast.LENGTH_SHORT).show();
                idChecked = false;
                return;
            } else if (!Pattern.matches("^.*[a-zA-Z]+.*$", editId.getText().toString())) {
                Toast.makeText(SignUpActivity.this, "아이디는 영문자를 1개 이상 포함해야 합니다", Toast.LENGTH_SHORT).show();
                idChecked = false;
                return;
            } else if (!Pattern.matches("^[a-zA-Z0-9!_]*$", editId.getText().toString())) {
                Toast.makeText(SignUpActivity.this, "사용할 수 없는 문자가 있습니다?", Toast.LENGTH_SHORT).show();
                idChecked = false;
                return;
            }

            if (Pattern.matches("^[!^_]*$", editId.getText().toString())) {
                Toast.makeText(SignUpActivity.this, "아이디는 특수문자로 구성할 수 없습니다", Toast.LENGTH_SHORT).show();
                return;
            }

            json.put("id", editId.getText().toString());

            new IdCheck().execute(getResources().getString(R.string.server_ip) + "/IdCheck.php", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls) {
            try {
                return BitmapFactory.decodeStream(new URL(urls[0]).openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Bitmap result) {
            Toast.makeText(getApplicationContext(), "testing attention please     " + result.toString(), Toast.LENGTH_LONG).show();
            imageButton.setImageBitmap(result);
        }
    }

    public void onSignUp(View view){
        String sex = null;

        if(!idChecked || editId.getText().toString().compareTo(lastId)!=0){
            Toast.makeText(SignUpActivity.this,"아이디 중복체크를 해주세요",Toast.LENGTH_SHORT).show();
            return;
        }else if(ageIndex == 0){
            Toast.makeText(SignUpActivity.this, "나이를 선택해주세요", Toast.LENGTH_SHORT).show();
            return;
        }else if(weightIndex == 0){
            Toast.makeText(SignUpActivity.this, "몸무게를 선택해주세요", Toast.LENGTH_SHORT).show();
            return;
        }else if(encodedImage == null){
            Toast.makeText(SignUpActivity.this, "사진을 선택해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if(rg.getCheckedRadioButtonId() == radioMan.getId()){
            sex = "man";
        }else if(rg.getCheckedRadioButtonId() == radioWoman.getId()){
            sex = "woman";
        }else{
            sex = "danger";
        }

        try {
            JSONObject json = new JSONObject();
            json.put("kakao",kakaoId);
            json.put("id",editId.getText().toString());
            json.put("age",""+ageIndex);
            json.put("weight",""+weightIndex);
            if(rg.getCheckedRadioButtonId() == radioMan.getId()){
                json.put("sex",sex);
            }else if(rg.getCheckedRadioButtonId() == radioWoman.getId()){
                json.put("sex",sex);
            }
            json.put("image",encodedImage+"");

            new signUp().execute(getResources().getString(R.string.server_ip) + "/SignUp.php", json.toString());
        }catch(JSONException e){
            e.printStackTrace();
        }

    }

    private class IdCheck extends AsyncTask<String, Void, JSONObject> {
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

            //Toast.makeText(getApplicationContext(),"id = " + editId.getText().toString() + " age = " + ageIndex + " weight = " + weightIndex + " sex = " + sex + " image = " + encodedImage ,Toast.LENGTH_LONG).show();
            if (result == null) {
                Toast.makeText(getApplicationContext(), "정보를 받는 도중 에러가 났습니다. 다시 한번 확인해주세요.", Toast.LENGTH_LONG).show();
            } else {
                try {
                    if (result.getString("code").compareTo("0") == 0) {
                        Toast.makeText(SignUpActivity.this, "사용할 수 있는 아이디입니다", Toast.LENGTH_SHORT).show();
                        lastId = editId.getText().toString();
                        idChecked = true;
                    } else if (result.getString("code").compareTo("1") == 0) {
                        if (result.getString("error").compareTo("0") == 0) {
                            Toast.makeText(SignUpActivity.this, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
                            idChecked = false;
                        } else if (result.getString("error").compareTo("1") == 0) {
                            Toast.makeText(SignUpActivity.this, "이미 존재하는 아이디입니다", Toast.LENGTH_SHORT).show();
                            idChecked = false;
                        } else if (result.getString("error").compareTo("2") == 0) {
                            Toast.makeText(SignUpActivity.this, "사용할 수 없는 문자가 있습니다", Toast.LENGTH_SHORT).show();
                            idChecked = false;
                        } else if (result.getString("error").compareTo("3") == 0) {
                            Toast.makeText(SignUpActivity.this, "아이디는 16자를 넘을수 없습니다.", Toast.LENGTH_SHORT).show();
                            idChecked = false;
                        } else if (result.getString("error").compareTo("4") == 0) {
                            Toast.makeText(SignUpActivity.this, "아이디는 영문자를 1개 이상 포함해야 합니다", Toast.LENGTH_SHORT).show();
                            idChecked = false;
                        } else {
                            Toast.makeText(SignUpActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class signUp extends AsyncTask<String, Void, JSONObject> {
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
                        Toast.makeText(SignUpActivity.this, "회원가입에 성공했습니다", Toast.LENGTH_SHORT).show();
                        Intent in = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(in);
                        finish();
                    }else if(result.getString("code").compareTo("1") == 0) {
                        Toast.makeText(SignUpActivity.this,"회원가입 실패"+result.toString(),Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(SignUpActivity.this,"서버 에러",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
