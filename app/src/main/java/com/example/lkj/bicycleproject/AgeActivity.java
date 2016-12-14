package com.example.lkj.bicycleproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lkj.bicycleproject.ListView.AgeAdapter;
import com.example.lkj.bicycleproject.ListView.AgeList;

import java.util.ArrayList;

public class AgeActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<AgeList> ageLists;
    private AgeAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age);

        final Intent intent = getIntent();

        listView = (ListView)findViewById(R.id.listView);

        ageLists = new ArrayList<AgeList>();

        ageLists.clear();

        for(int i = 0 ; i <101 ; i++){
            ageLists.add(i, new AgeList((i+7)+""));
        }

        adapter = new AgeAdapter(this, R.layout.age_item, ageLists);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent.putExtra("RESULT_AGE",ageLists.get(position).getAge());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
