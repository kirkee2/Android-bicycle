package com.example.lkj.bicycleproject.Fragment;

import android.content.Context;
import android.graphics.PointF;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lkj.bicycleproject.Connection.WebHook;
import com.example.lkj.bicycleproject.R;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapLabelInfo;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;


public class FindRoadFragment extends Fragment implements TMapGpsManager.onLocationChangedCallback{

    private RelativeLayout contentView = null;


    private TMapView mMapView = null;
    private Context mContext;
    public static String mApiKey = "399b7639-7158-353d-8f24-e8ff3c376ade";
    private int m_nCurrentZoomLevel = 0;
    private double m_Latitude = 0;
    private double m_Longitude = 0;
    private boolean m_bShowMapIcon = false;
    private Button start;
    private Button end;
    private TextView startText;
    private TextView endText;


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
    public void onLocationChange(Location location) {
        if(m_bTrackingMode) {
            mMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }

    public FindRoadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_find_road, container, false);

        contentView = (RelativeLayout)view.findViewById(R.id.contentView);

        //contentView.removeAllViews();

        //contentView.addView(view, new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));


        handler = new android.os.Handler();

        mContext = getActivity();

        mMapView = new TMapView(mContext);

        start = (Button)view.findViewById(R.id.start);
        end = (Button)view.findViewById(R.id.end);
        startText = (TextView) view.findViewById(R.id.startText);
        endText = (TextView) view.findViewById(R.id.endText);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String strStart = startText.getText().toString();
                TMapData tmapdata = new TMapData();

                tmapdata.findAllPOI(strStart, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
                        for (int i = 0; i < poiItem.size(); i++) {
                            TMapPOIItem  item = poiItem.get(i);

                            new WebHook().execute("POI Name: " + item.getPOIName().toString() + ", " +
                                    "Address: " + item.getPOIAddress().replace("null", "")  + ", " +
                                    "Point: " + item.getPOIPoint().toString());
                        }
                    }
                });
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String strStart = startText.getText().toString();
                TMapData tmapdata = new TMapData();

                tmapdata.findAllPOI(strStart, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
                        for (int i = 0; i < poiItem.size(); i++) {
                            TMapPOIItem  item = poiItem.get(i);

                            new WebHook().execute("POI Name: " + item.getPOIName().toString() + ", " +
                                    "Address: " + item.getPOIAddress().replace("null", "")  + ", " +
                                    "Point: " + item.getPOIPoint().toString());
                        }
                    }
                });
            }
        });

        //contentView.removeAllViews();
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
