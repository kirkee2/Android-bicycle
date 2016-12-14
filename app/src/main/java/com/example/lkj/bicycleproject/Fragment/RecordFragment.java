package com.example.lkj.bicycleproject.Fragment;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.lkj.bicycleproject.R;
import com.example.lkj.bicycleproject.RecyclerView.MyRecyclerAdapter;
import com.example.lkj.bicycleproject.RecyclerView.RecyclerItemClickListener;
import com.example.lkj.bicycleproject.RecyclerView.Row;
import com.example.lkj.bicycleproject.StartActivity;

import java.util.ArrayList;
import java.util.List;

public class RecordFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Row> albumList;
    private MyRecyclerAdapter myRecyclerAdapter;
    private ImageButton imageButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);


        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        //imageButton = (ImageButton)view.findViewById(R.id.imageButton2);

        initData();

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Toast.makeText(getActivity().getApplicationContext(),albumList.get(position).getTitle()+"     "+position,Toast.LENGTH_LONG).show();
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        /*
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumList.clear(); //clear list
                myRecyclerAdapter.notifyDataSetChanged();
                changeData();
            }
        });
        */

        return view;
    }

    private void initData(){
        albumList = new ArrayList<Row>();

        for (int i =0; i<4; i ++){

            Row album = new Row();
            album.setTitle(i+"번째 제목");
            album.setImage(R.drawable.splash_screen);
            albumList.add(album);
        }

        myRecyclerAdapter= new MyRecyclerAdapter(albumList,R.layout.row);

        recyclerView.setAdapter(myRecyclerAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 2));
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private void changeData(){
        albumList = new ArrayList<Row>();

        for (int i =0; i<4; i ++){

            Row album = new Row();
            album.setTitle(i+"메롱");
            album.setImage(R.drawable.splash_screen);
            albumList.add(album);
        }

        myRecyclerAdapter= new MyRecyclerAdapter(albumList,R.layout.row);

        recyclerView.setAdapter(myRecyclerAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 2));
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }
}
