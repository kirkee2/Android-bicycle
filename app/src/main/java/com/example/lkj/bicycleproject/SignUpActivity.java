package com.example.lkj.bicycleproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lkj.bicycleproject.Connection.WebHook;
import com.example.lkj.bicycleproject.Kakao_Login.LoginActivity;
import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

public class SignUpActivity extends AppCompatActivity {
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;
    Uri mImageCaptureUri;

    private ImageButton imageButton;
    private Spinner spinnerAge;
    private Spinner spinnerWeight;
    int weightIndex = 0;
    int ageIndex = 0;
    RadioGroup rg;
    RadioButton radioMan;
    RadioButton radioWoman;
    Handler handler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        imageButton = (ImageButton) findViewById(R.id.imageAdd);
        //imageButton.setImageURI(Uri.parse(tmp[0]));
        spinnerAge = (Spinner)findViewById(R.id.spinnerAge);
        spinnerWeight = (Spinner)findViewById(R.id.spinnerWeight);
        rg = (RadioGroup) findViewById(R.id.rg);
        radioMan = (RadioButton) findViewById(R.id.radioMan);
        radioWoman = (RadioButton) findViewById(R.id.radioWoman);

        ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.spinnerAgeArray));

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
            }
        });

        ArrayAdapter<String> weightAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.spinnerWeightArray));

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
            }
        });


        handler = new Handler() {
            public void handleMessage(Message msg) {
                String message = (String) msg.obj;
                try {
                    imageButton.setImageBitmap(BitmapFactory.decodeStream(new URL(message).openConnection().getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        };
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

            try {
                Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                imageButton.setImageBitmap(image_bitmap);


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


        } else if (requestCode == PICK_FROM_ALBUM) {
            if (data == null) {

            } else {
                mImageCaptureUri = data.getData();
            }

            try {
                Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                imageButton.setImageBitmap(image_bitmap);


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

    public void userId(View v){
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
            public void onNotSignedUp() {} // 카카오톡 회원이 아닐 시 showSignup(); 호출해야함

            @Override
            public void onSuccess(final UserProfile userProfile) {  //성공 시 userProfile 형태로 반환
                Toast.makeText(getApplicationContext(),"id : " + userProfile.getId() +" user nickname : " + userProfile.getNickname() + " user thumbnail photo : " + userProfile.getThumbnailImagePath() + " user profile image : " + userProfile.getProfileImagePath(),Toast.LENGTH_LONG).show();


                //new DownloadImageTask().execute(userProfile.getProfileImagePath());




            }
        });
    }

    private class DownloadImageTask extends AsyncTask<String,Void,Bitmap> {
        protected Bitmap doInBackground(String... urls) {
            try {
                return BitmapFactory.decodeStream(new URL(urls[0]).openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Bitmap result) {
            Toast.makeText(getApplicationContext(),"testing attention please     " +result.toString(),Toast.LENGTH_LONG).show();
            imageButton.setImageBitmap(result);
        }
    }

}
