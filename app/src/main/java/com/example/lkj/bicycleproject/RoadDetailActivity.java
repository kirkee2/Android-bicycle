package com.example.lkj.bicycleproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.example.lkj.bicycleproject.Connection.Connect;

import org.json.JSONException;
import org.json.JSONObject;

public class RoadDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String roadId;
    private String titleString;
    private String startLatitude;
    private String startLongitude;
    private String endLatitude;
    private String endLongitude;
    private String authCenter;
    private String detailString;

    private TextView title;
    private TextView detail;
    private TextView watch;
    private ImageView img;

    private AQuery aquery ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_road_detail);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView)findViewById(R.id.title);
        detail = (TextView)findViewById(R.id.detail);
        watch = (TextView)findViewById(R.id.watch);

        img = (ImageView)findViewById(R.id.imageView);

        aquery = new AQuery( this );


        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        Intent intent = getIntent();

        roadId = intent.getStringExtra("id");


        JSONObject json = new JSONObject();

        try {
            json.put("id", roadId);
            new RoadDetail().execute(getResources().getString(R.string.server_ip) + "/RoadDetail.php", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private class RoadDetail extends AsyncTask<String, Void, JSONObject> {
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
                    if(result.getString("code").compareTo("0") == 0) {
                        title.setText(result.getString("title"));
                        detail.setText(result.getString("detail"));
                        aquery.id( img ).image("http://kirkee2.cafe24.com/roadImage/road"+roadId+".png" );
                        watch.setText(result.getInt("watch")+"");
                    }else if(result.getString("code").compareTo("1") == 0) {
                    }else{
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }else{

        }
        return super.onOptionsItemSelected(item);
    }

    public void onStart(View view){
        Intent intent = new Intent(RoadDetailActivity.this,StartActivity.class);

        startActivity(intent);
    }
}
