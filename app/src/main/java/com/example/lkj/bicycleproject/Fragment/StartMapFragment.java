package com.example.lkj.bicycleproject.Fragment;


import android.content.Context;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.lkj.bicycleproject.Connection.WebHook;
import com.example.lkj.bicycleproject.R;
import com.example.lkj.bicycleproject.StartActivity;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapLabelInfo;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;

public class StartMapFragment extends Fragment implements TMapGpsManager.onLocationChangedCallback{

    private RelativeLayout contentView = null;

    private FloatingActionButton btSpeed;

    static View tmp;
    public double startLat = 0;
    public double startLng = 0;
    public double endLat = 0;
    public double endLng = 0;

    private static TMapView mMapView = null;
    private  Context mContext;
    public static String mApiKey = "399b7639-7158-353d-8f24-e8ff3c376ade"; // 발급받은 appKey
    private int m_nCurrentZoomLevel = 0;
    private double m_Latitude = 0;
    private double m_Longitude = 0;
    private boolean m_bShowMapIcon = false;

    private boolean m_bTrafficeMode = false;
    private boolean m_bSightVisible = false;
    private boolean m_bTrackingMode = false;

    private boolean m_bOverlayMode = false;

    ArrayList<String> mArrayID;

    ArrayList<String> mArrayCircleID;
    private static int mCircleID;

    ArrayList<String> mArrayLineID;
    private static int mLineID;

    ArrayList<String> mArrayPolygonID;
    private static int mPolygonID;

    ArrayList<String> mArrayMarkerID;
    private static int mMarkerID;

    TMapGpsManager gps = null;

    android.os.Handler handler;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
        mMapView = new TMapView(mContext);
    }

    @Override
    public void onLocationChange(Location location) {
        if(m_bTrackingMode) {
            mMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }

    public StartMapFragment() {
        // Required empty public constructor
    }

    public  void getAct(){
        mContext = getActivity();

        mMapView = new TMapView(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_start_map, container, false);

        contentView = (RelativeLayout)view.findViewById(R.id.contentView);

        tmp = view.getRootView();

        //contentView.removeAllViews();

        //contentView.addView(view, new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        btSpeed = (FloatingActionButton)view.findViewById(R.id.btSpeed);

        btSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((StartActivity)getActivity()).getViewPager().setCurrentItem(0);
            }
        });

        handler = new android.os.Handler();

        getAct();

        //contentView.removeAllViews();
        contentView.addView(mMapView, new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        configureMapView();

        initView();

        StartActivity startActivity = (StartActivity) getActivity();

        if(startActivity.getIsBundle()){
            startLat = startActivity.getStartLat();
            startLng = startActivity.getStartLng();
            endLat = startActivity.getEndLat();
            endLng = startActivity.getEndLng();
            drawBicyclePath();
        }else{

        }

        mArrayID = new ArrayList<String>();

        mArrayCircleID = new ArrayList<String>();
        mCircleID = 0;

        mArrayLineID = new ArrayList<String>();
        mLineID = 0;

        mArrayPolygonID = new ArrayList<String>();
        mPolygonID = 0;

        mArrayMarkerID = new ArrayList<String>();
        mMarkerID = 0;

        gps = new TMapGpsManager(getActivity());
        gps.setMinTime(1000);
        gps.setMinDistance(5);
        gps.setProvider(gps.NETWORK_PROVIDER);
        gps.OpenGps();



        return view;
    }

    private void configureMapView() {
        mMapView.setSKPMapApiKey(mApiKey);
        //mMapView.setSKPMapBizappId(mBizAppID);
    }

    private void initView() {
        /*

        for (int btnMapView : mArrayMapButton) {
            Button ViewButton = (Button)findViewById(btnMapView);
            ViewButton.setOnClickListener(this);
        }
        */

        mMapView.setOnApiKeyListener(new TMapView.OnApiKeyListenerCallback() {
            @Override
            public void SKPMapApikeySucceed() {
                /*
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(), "성공", Toast.LENGTH_LONG).show();
                    }
                });
                */
            }
            //Toast.makeText(getApplicationContext(), "싶패", Toast.LENGTH_LONG).show();
            //LogManager.printLog("MainActivity SKPMapApikeySucceed");


            @Override
            public void SKPMapApikeyFailed(String errorMsg) {
                /*
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(), "실패", Toast.LENGTH_LONG).show();
                    }
                });
                */
                //Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_LONG).show();
                //LogManager.printLog("MainActivity SKPMapApikeyFailed " + errorMsg);
            }
        });

        mMapView.setOnEnableScrollWithZoomLevelListener(new TMapView.OnEnableScrollWithZoomLevelCallback() {
            @Override
            public void onEnableScrollWithZoomLevelEvent(final float zoom, final TMapPoint centerPoint) {
                /*
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(), "MainActivity onEnableScrollWithZoomLevelEvent " + zoom + " " + centerPoint.getLatitude() + " " + centerPoint.getLongitude(), Toast.LENGTH_LONG).show();
                    }
                });
                */
                //LogManager.printLog("MainActivity onEnableScrollWithZoomLevelEvent " + zoom + " " + centerPoint.getLatitude() + " " + centerPoint.getLongitude());
            }
        });

        mMapView.setOnDisableScrollWithZoomLevelListener(new TMapView.OnDisableScrollWithZoomLevelCallback() {
            @Override
            public void onDisableScrollWithZoomLevelEvent(final float zoom, final TMapPoint centerPoint) {
                /*
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(), "MainActivity onDisableScrollWithZoomLevelEvent " + zoom + " " + centerPoint.getLatitude() + " " + centerPoint.getLongitude(), Toast.LENGTH_LONG).show();
                    }
                });
                */
                //LogManager.printLog("MainActivity onDisableScrollWithZoomLevelEvent " + zoom + " " + centerPoint.getLatitude() + " " + centerPoint.getLongitude());
            }
        });

        mMapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
            @Override
            public boolean onPressUpEvent(final ArrayList<TMapMarkerItem> markerlist, ArrayList<TMapPOIItem> poilist, TMapPoint
                    point, PointF pointf) {
                /*
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(), "MainActivity onPressUpEvent " + markerlist.size(), Toast.LENGTH_LONG).show();
                    }
                });
                */
                //LogManager.printLog("MainActivity onPressUpEvent " + markerlist.size());
                return false;
            }

            @Override
            public boolean onPressEvent(final ArrayList<TMapMarkerItem> markerlist, ArrayList<TMapPOIItem> poilist, TMapPoint point, PointF pointf) {
                //LogManager.printLog("MainActivity onPressEvent " + markerlist.size());

                /*
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(), "MainActivity onPressEvent " + markerlist.size(), Toast.LENGTH_LONG).show();
                    }
                });
                */

                for (int i = 0; i < markerlist.size(); i++) {
                    TMapMarkerItem item = markerlist.get(i);
                    //LogManager.printLog("MainActivity onPressEvent " + item.getName() + " " + item.getTMapPoint().getLatitude() + " " + item.getTMapPoint().getLongitude());
                }
                return false;
            }
        });

        mMapView.setOnLongClickListenerCallback(new TMapView.OnLongClickListenerCallback() {
            @Override
            public void onLongPressEvent(final ArrayList<TMapMarkerItem> markerlist, ArrayList<TMapPOIItem> poilist, TMapPoint point) {
                /*
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(), "MainActivity onLongPressEvent " + markerlist.size(), Toast.LENGTH_LONG).show();
                    }
                });
                */
                //LogManager.printLog("MainActivity onLongPressEvent " + markerlist.size());
            }
        });

        mMapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem markerItem) {
                String strMessage = "";
                strMessage = "ID: " + markerItem.getID() + " " + "Title " + markerItem.getCalloutTitle();

                /*
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),"Callout Right Button", Toast.LENGTH_LONG).show();
                    }
                });
                */
                //Common.showAlertDialog(MainActivity.this, "Callout Right Button", strMessage);
            }
        });

        mMapView.setOnClickReverseLabelListener(new TMapView.OnClickReverseLabelListenerCallback() {
            @Override
            public void onClickReverseLabelEvent(final TMapLabelInfo findReverseLabel) {
                /*
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(), "MainActivity setOnClickReverseLabelListener " + findReverseLabel.id + " / " + findReverseLabel.labelLat + " / " + findReverseLabel.labelLon + " / " + findReverseLabel.labelName, Toast.LENGTH_LONG).show();
                    }
                });
                if(findReverseLabel != null) {
                    //LogManager.printLog("MainActivity setOnClickReverseLabelListener " + findReverseLabel.id + " / " + findReverseLabel.labelLat + " / " + findReverseLabel.labelLon + " / " + findReverseLabel.labelName);

                }
                */
            }
        });

        m_nCurrentZoomLevel = -1;
        m_bShowMapIcon = false;
        m_bTrafficeMode = false;
        m_bSightVisible = false;
        m_bTrackingMode = false;
    }

    public void drawBicyclePath() {

        TMapPoint point1 = new TMapPoint(startLat,startLng);
        TMapPoint point2 = new TMapPoint(endLat,endLng);
        //TMapPoint point1 = mMapView.getCenterPoint();
        //TMapPoint point2 = randomTMapPoint();

        Log.d("asd","asdsadasdasas  asdasdsad "+startLat + " " + startLng + " " + endLat + " " + endLng);

        TMapData tmapdata = new TMapData();

        Log.d("asd","3");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tmapdata.findPathDataWithType(TMapData.TMapPathType.BICYCLE_PATH, point1, point2, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine polyLine) {

                Log.d("asd","4");

                mMapView.addTMapPath(polyLine);

                TMapPoint point = new TMapPoint((startLat + endLat)/2.0,(startLng+endLng)/2.0);
                mMapView.setCenterPoint(point.getLongitude(), point.getLatitude(), true);
                mMapView.setZoomLevel(14);
                //((StartActivity)tmp.getContext()).getViewPager().setCurrentItem(1);
            }
        });


        Log.d("asd","5");

    }



    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gps.CloseGps();
    }

}
