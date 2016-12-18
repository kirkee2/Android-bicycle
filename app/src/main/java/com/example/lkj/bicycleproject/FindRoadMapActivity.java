package com.example.lkj.bicycleproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.androidquery.util.Common;
import com.example.lkj.bicycleproject.Connection.WebHook;
import com.example.lkj.bicycleproject.Controller.MarkerOverlay;
import com.example.lkj.bicycleproject.Fragment.FindRoadFragment;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapLabelInfo;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapMarkerItem2;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;

public class FindRoadMapActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    private RelativeLayout contentView = null;


    private FloatingActionButton restroom;
    private FloatingActionButton myLocation;



    private Toolbar toolbar;

    private TMapView mMapView = null;

    private Context mContext;

    private double startLat = 0;
    private double startLng = 0;
    private double endLat = 0;
    private double endLng = 0;


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

    public void getAct(){
        mContext = this;

        mMapView = new TMapView(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_road_map);

        contentView = (RelativeLayout)findViewById(R.id.contentView);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        getAct();

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

        restroom = (FloatingActionButton)findViewById(R.id.restroom);
        myLocation = (FloatingActionButton)findViewById(R.id.myLocation);


        restroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapView.setBicycleFacilityInfo(!mMapView.isBicycleFacilityInfo());

                if(mMapView.isBicycleFacilityInfo()){
                    restroom.setBackgroundTintList(ColorStateList.valueOf(Color
                            .parseColor("#f1fe652d")));
                }else{
                    restroom.setBackgroundTintList(ColorStateList.valueOf(Color
                            .parseColor("#f148eaff")));
                }
            }
        });


        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean bIsTracking = mMapView.getIsTracking();

                m_bTrackingMode = !m_bTrackingMode;
                mMapView.setTrackingMode(m_bTrackingMode);

                if(bIsTracking){
                    myLocation.setBackgroundTintList(ColorStateList.valueOf(Color
                            .parseColor("#f1fe652d")));

                    mMapView.setZoomLevel(18);
                }else{
                    myLocation.setBackgroundTintList(ColorStateList.valueOf(Color
                            .parseColor("#f148eaff")));

                }
            }

        });



        mMapView.setTMapLogoPosition(TMapView.TMapLogoPositon.POSITION_BOTTOMRIGHT);

        Intent intent = getIntent();

        Bundle bundleData = intent.getBundleExtra("point");
        if (bundleData == null) {
            Toast.makeText(this, "Bundle Data Null!", Toast.LENGTH_LONG).show();

        }else {
            startLat = bundleData.getDouble("startLat");
            startLng = bundleData.getDouble("startLng");
            endLat = bundleData.getDouble("endLat");
            endLng = bundleData.getDouble("endLng");

            drawBicyclePath();

            TMapPoint point = new TMapPoint((startLat + endLat)/2.0,(startLng+endLng)/2.0);
            mMapView.setCenterPoint(point.getLongitude(), point.getLatitude(), true);
            mMapView.setZoomLevel(14);
        }

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

    public TMapPoint randomTMapPoint() {
        double latitude = ((double)Math.random() ) * (37.575113-37.483086) + 37.483086;
        double longitude = ((double)Math.random() ) * (127.027359-126.878357) + 126.878357;

        latitude = Math.min(37.575113, latitude);
        latitude = Math.max(37.483086, latitude);

        longitude = Math.min(127.027359, longitude);
        longitude = Math.max(126.878357, longitude);

        TMapPoint point = new TMapPoint(latitude, longitude);

        return point;
    }

    public void drawBicyclePath() {
        TMapPoint point1 = new TMapPoint(startLat,startLng);
        TMapPoint point2 = new TMapPoint(endLat,endLng);
        //TMapPoint point1 = mMapView.getCenterPoint();
        //TMapPoint point2 = randomTMapPoint();



        TMapData tmapdata = new TMapData();

        tmapdata.findPathDataWithType(TMapData.TMapPathType.BICYCLE_PATH, point1, point2, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {
                mMapView.addTMapPath(polyLine);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }else{

        }
        return super.onOptionsItemSelected(item);
    }

    public void onStart(View view){
        Intent intent = new Intent(FindRoadMapActivity.this,StartActivity.class);

        Bundle bundleData = new Bundle();
        bundleData.putDouble("startLat", startLat);
        bundleData.putDouble("startLng", startLng);
        bundleData.putDouble("endLat", endLat);
        bundleData.putDouble("endLng", endLng);

        intent.putExtra("point",bundleData);

        startActivity(intent);
    }
}
