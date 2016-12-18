package com.example.lkj.bicycleproject.RecyclerView;

import com.skp.Tmap.TMapPoint;

/**
 * Created by leegunjoon on 2016. 12. 18..
 */
public class FindRoadRow {

    private String title;
    private String address;
    private TMapPoint tMapPoint;

    public TMapPoint getTMapPoint(){
        return tMapPoint;
    }

    public void setTMapPoint(TMapPoint tMapPoint){
        this.tMapPoint =tMapPoint;
    }

    public String getAddress(){
        return address;
    }

    public void setAddress(String address){
        this.address =address;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
