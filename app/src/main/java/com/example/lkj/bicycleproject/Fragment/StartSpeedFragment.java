package com.example.lkj.bicycleproject.Fragment;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.example.lkj.bicycleproject.R;
import com.example.lkj.bicycleproject.StartActivity;
import com.github.anastr.speedviewlib.SpeedView;

public class StartSpeedFragment extends Fragment {

    public static SpeedView speedometer;
    private FloatingActionButton btMap;



    public StartSpeedFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_start_speed, container, false);

        speedometer = (SpeedView) view.findViewById(R.id.speedView);
        btMap = (FloatingActionButton)view.findViewById(R.id.btMap);

        btMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((StartActivity)getActivity()).getViewPager().setCurrentItem(1);
            }
        });





        return view;
    }

}
