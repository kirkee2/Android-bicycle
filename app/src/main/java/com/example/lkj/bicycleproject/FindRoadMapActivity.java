package com.example.lkj.bicycleproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.androidquery.util.Common;
import com.example.lkj.bicycleproject.Fragment.FindRoadFragment;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapLabelInfo;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;

public class FindRoadMapActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    private RelativeLayout contentView = null;


    private TMapView mMapView = null;

    private Context mContext;

    private 	int 		m_nCurrentZoomLevel = 0;
    private 	double 		m_Latitude  = 0;
    private     double  	m_Longitude = 0;
    private 	boolean 	m_bShowMapIcon = false;

    private 	boolean 	m_bTrafficeMode = false;
    private 	boolean 	m_bSightVisible = false;
    private 	boolean 	m_bTrackingMode = false;

    private 	boolean 	m_bOverlayMode = false;

    ArrayList<String>		mArrayID;

    ArrayList<String>		mArrayCircleID;
    private static 	int 	mCircleID;

    ArrayList<String>		mArrayLineID;
    private static 	int 	mLineID;

    ArrayList<String>		mArrayPolygonID;
    private static  int 	mPolygonID;

    ArrayList<String>       mArrayMarkerID;
    private static int 		mMarkerID;

    TMapGpsManager gps = null;

    public static String mApiKey = "399b7639-7158-353d-8f24-e8ff3c376ade"; // 발급받은 appKey
    public static String mBizAppID; // 발급받은 BizAppID (TMapTapi로 TMap앱 연동을 할 때 BizAppID 꼭 필요)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_road_map);

        contentView = (RelativeLayout)findViewById(R.id.contentView);


        mContext = this;

        mMapView = new TMapView(this);

        contentView.addView(mMapView, new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));


        configureMapView();

        initView();

        mArrayID = new ArrayList<String>();

        mArrayCircleID = new ArrayList<String>();
        mCircleID = 0;

        mArrayLineID = new ArrayList<String>();
        mLineID = 0;

        mArrayPolygonID = new ArrayList<String>();
        mPolygonID = 0;

        mArrayMarkerID	= new ArrayList<String>();
        mMarkerID = 0;

        gps = new TMapGpsManager(FindRoadMapActivity.this);
        gps.setMinTime(1000);
        gps.setMinDistance(5);
        gps.setProvider(gps.NETWORK_PROVIDER);
        gps.OpenGps();

        mMapView.setTMapLogoPosition(TMapView.TMapLogoPositon.POSITION_BOTTOMRIGHT);
    }

    private void configureMapView() {
        mMapView.setSKPMapApiKey(mApiKey);
        //mMapView.setSKPMapBizappId(mBizAppID);
    }


    private void initView() {


        mMapView.setOnApiKeyListener(new TMapView.OnApiKeyListenerCallback() {
            @Override
            public void SKPMapApikeySucceed() {
            }

            @Override
            public void SKPMapApikeyFailed(String errorMsg) {
            }
        });

        mMapView.setOnEnableScrollWithZoomLevelListener(new TMapView.OnEnableScrollWithZoomLevelCallback() {
            @Override
            public void onEnableScrollWithZoomLevelEvent(float zoom, TMapPoint centerPoint) {
            }
        });

        mMapView.setOnDisableScrollWithZoomLevelListener(new TMapView.OnDisableScrollWithZoomLevelCallback() {
            @Override
            public void onDisableScrollWithZoomLevelEvent(float zoom, TMapPoint centerPoint) {
            }
        });

        mMapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
            @Override
            public boolean onPressUpEvent(ArrayList<TMapMarkerItem> markerlist, ArrayList<TMapPOIItem> poilist, TMapPoint point, PointF pointf) {
                return false;
            }

            @Override
            public boolean onPressEvent(ArrayList<TMapMarkerItem> markerlist,ArrayList<TMapPOIItem> poilist, TMapPoint point, PointF pointf) {

                for (int i = 0; i < markerlist.size(); i++) {
                    TMapMarkerItem item = markerlist.get(i);
                }
                return false;
            }
        });

        mMapView.setOnLongClickListenerCallback(new TMapView.OnLongClickListenerCallback() {
            @Override
            public void onLongPressEvent(ArrayList<TMapMarkerItem> markerlist,ArrayList<TMapPOIItem> poilist, TMapPoint point) {
            }
        });

        mMapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem markerItem) {
                String strMessage = "";
                strMessage = "ID: " + markerItem.getID() + " " + "Title " + markerItem.getCalloutTitle();
            }
        });

        mMapView.setOnClickReverseLabelListener(new TMapView.OnClickReverseLabelListenerCallback() {
            @Override
            public void onClickReverseLabelEvent(TMapLabelInfo findReverseLabel) {
                if(findReverseLabel != null) {

                }
            }
        });

        m_nCurrentZoomLevel = -1;
        m_bShowMapIcon = false;
        m_bTrafficeMode = false;
        m_bSightVisible = false;
        m_bTrackingMode = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gps.CloseGps();
    }

    @Override
    public void onLocationChange(Location location) {
        if(m_bTrackingMode) {
            mMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }
}
