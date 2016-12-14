package com.example.lkj.bicycleproject.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lkj.bicycleproject.R;
import com.example.lkj.bicycleproject.RecyclerView.MyRecyclerAdapter;
import com.example.lkj.bicycleproject.RecyclerView.Row;

import java.util.ArrayList;
import java.util.List;

public class RecommandRoadFragment extends Fragment {

    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommand_road, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);

        initData();

        return view;
    }

    private void initData(){

        //recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        List<Row> albumList = new ArrayList<Row>();

        for (int i =0; i<20; i ++){

            Row album = new Row();
            album.setTitle("어느 멋진 날");
            album.setImage(R.drawable.splash_screen);
            albumList.add(album);
        }

        recyclerView.setAdapter(new MyRecyclerAdapter(albumList,R.layout.row));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }
}
