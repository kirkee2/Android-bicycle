package com.example.lkj.bicycleproject.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lkj.bicycleproject.R;
import com.github.anastr.speedviewlib.SpeedView;

public class StartSpeedFragment extends Fragment {

    private SpeedView speedometer;


    public StartSpeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);


        speedometer = (SpeedView) view.findViewById(R.id.speedView);

        return view;
    }

    public void setSpeed(){
        speedometer.speedTo(70, 4000);
    }

}
