package com.example.lkj.bicycleproject.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.example.lkj.bicycleproject.Connection.Connect;
import com.example.lkj.bicycleproject.R;
import com.example.lkj.bicycleproject.RecyclerView.MyRecyclerAdapter;
import com.example.lkj.bicycleproject.RecyclerView.RecyclerItemClickListener;
import com.example.lkj.bicycleproject.RecyclerView.RoadRecyclerAdapter;
import com.example.lkj.bicycleproject.RecyclerView.RoadRow;
import com.example.lkj.bicycleproject.RecyclerView.Row;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class RecommandRoadFragment extends Fragment {
    private RecyclerView recyclerView;
    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;
    private List<RoadRow> roadList;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommand_road, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        linearLayout = (LinearLayout)view.findViewById(R.id.mainLayout);
        relativeLayout = (RelativeLayout)view.findViewById(R.id.infoLayout);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Toast.makeText(getActivity().getApplicationContext(),roadList.get(position).getTitle()+"     "+position,Toast.LENGTH_LONG).show();
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        JSONObject json = new JSONObject();

        try {
            json.put("hoho", "hoho");
            new RoadInfo().execute(getResources().getString(R.string.server_ip) + "/RoadInfo.php", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return view;
    }

    private class RoadInfo extends AsyncTask<String, Void, JSONArray> {
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

                    if(jsonTmp.getString("code").compareTo("0") == 0){
                        linearLayout.setVisibility(View.VISIBLE);
                        relativeLayout.setVisibility(View.INVISIBLE);
                        initData(result);
                    }else{
                        Toast.makeText(getActivity().getApplicationContext(),"서버 에러",Toast.LENGTH_SHORT).show();
                        linearLayout.setVisibility(View.INVISIBLE);
                        relativeLayout.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initData(JSONArray jsonArray){

        //recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        roadList = new ArrayList<RoadRow>();

        for(int i = 1 ; i < jsonArray.length() ; i++){
            try {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                RoadRow roadRow = new RoadRow();

                roadRow.setTitle(jsonObject.getString("title"));
                roadRow.setId(jsonObject.getString("id"));
                roadRow.setImage("http://kirkee2.cafe24.com/roadImage/road"+jsonObject.getString("id")+".png");
                roadRow.setWatch(Integer.parseInt(jsonObject.getString("watch")));
                //new ImageFromUrl().execute("http://kirkee2.cafe24.com/roadImage/road"+jsonObject.getString("id")+".png");

                //album.setImage("http://kirkee2.cafe24.com/roadImage/road"+jsonObject.getString("id")+".png");
                //aquery.id( thumbNailImage ).image("http://kirkee2.cafe24.com/memberImage/"+kakaoId+".jpg" );

                roadList.add(roadRow);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        recyclerView.setAdapter(new RoadRecyclerAdapter(roadList,R.layout.road_row));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }
}
