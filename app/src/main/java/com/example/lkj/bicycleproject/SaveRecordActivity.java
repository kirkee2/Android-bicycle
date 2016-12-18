package com.example.lkj.bicycleproject;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class SaveRecordActivity extends AppCompatActivity {

    private Button yes;
    private Button no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_record);

        yes = (Button)findViewById(R.id.yes);
        no = (Button)findViewById(R.id.no);


    }

    public void yes(View view){
        setResult(1);
        finish();
    }

    public void no(View view){
        setResult(0);
        finish();
    }

}
