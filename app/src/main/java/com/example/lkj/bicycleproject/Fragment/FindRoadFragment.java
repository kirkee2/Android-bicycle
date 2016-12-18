package com.example.lkj.bicycleproject.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lkj.bicycleproject.Connection.WebHook;
import com.example.lkj.bicycleproject.FindRoadMapActivity;
import com.example.lkj.bicycleproject.R;
import com.example.lkj.bicycleproject.RecyclerView.FindRoadRecyclerAdapter;
import com.example.lkj.bicycleproject.RecyclerView.FindRoadRow;
import com.example.lkj.bicycleproject.RecyclerView.RecyclerItemClickListener;
import com.example.lkj.bicycleproject.RecyclerView.RoadRecyclerAdapter;
import com.example.lkj.bicycleproject.RecyclerView.RoadRow;
import com.example.lkj.bicycleproject.RoadDetailActivity;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapLabelInfo;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FindRoadFragment extends Fragment{

    private TMapView mMapView = null;


    private Handler handler;
    private Button start;
    private Button end;
    private TextView startText;
    private TextView endText;
    private boolean isStart = false;
    private boolean isEnd = false;
    private TMapPoint startPoint = null;
    private TMapPoint endPoint = null;

    private Context mContext;



    private RecyclerView recyclerView;
    private List<FindRoadRow> findRoadList;

    public static String mApiKey = "399b7639-7158-353d-8f24-e8ff3c376ade"; // 발급받은 appKey

    /*
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
    */

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = getActivity();
        mMapView = new TMapView(mContext);
    }

    public void getAct(){
        mContext = getActivity();
        mMapView = new TMapView(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_road, container, false);


        start = (Button)view.findViewById(R.id.start);
        end = (Button)view.findViewById(R.id.end);
        startText = (TextView)view.findViewById(R.id.startText);
        endText = (TextView)view.findViewById(R.id.endText);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);


        getAct();

        configureMapView();

        handler = new Handler();

        initView();


        startText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    final String strEnd = startText.getText().toString();
                    TMapData tmapdata = new TMapData();

                    tmapdata.findAllPOI(strEnd, new TMapData.FindAllPOIListenerCallback() {
                        @Override
                        public void onFindAllPOI(final ArrayList<TMapPOIItem> poiItem) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    initData(poiItem);
                                }
                            });

                            isStart = true;
                            isEnd = false;
                        }
                    });
                    handled = true;

                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(startText.getWindowToken(), 0);
                }
                return handled;
            }
        });

        endText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    final String strEnd = endText.getText().toString();
                    TMapData tmapdata = new TMapData();

                    tmapdata.findAllPOI(strEnd, new TMapData.FindAllPOIListenerCallback() {
                        @Override
                        public void onFindAllPOI(final ArrayList<TMapPOIItem> poiItem) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    initData(poiItem);
                                }
                            });
                            isEnd = true;
                            isStart = false;
                        }
                    });
                    handled = true;

                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(endText.getWindowToken(), 0);
                }


                return handled;
            }
        });


        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity().getApplicationContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                        if(isStart){
                            startPoint = findRoadList.get(position).getTMapPoint();

                            startText.setText(findRoadList.get(position).getTitle());
                        }

                        if(isEnd){
                            endPoint = findRoadList.get(position).getTMapPoint();

                            endText.setText(findRoadList.get(position).getTitle());
                            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(endText.getWindowToken(), 0);
                        }

                        if(endPoint != null && startPoint != null){
                            Intent intent = new Intent(getActivity(), FindRoadMapActivity.class);

                            Bundle bundleData = new Bundle();
                            bundleData.putDouble("startLat", startPoint.getLatitude());
                            bundleData.putDouble("startLng", startPoint.getLongitude());
                            bundleData.putDouble("endLat", endPoint.getLatitude());
                            bundleData.putDouble("endLng", endPoint.getLongitude());

                            intent.putExtra("point",bundleData);

                            startActivity(intent);
                        }
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );


        TMapGpsManager gps = null;

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String strEnd = startText.getText().toString();
                TMapData tmapdata = new TMapData();

                tmapdata.findAllPOI(strEnd, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(final ArrayList<TMapPOIItem> poiItem) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                initData(poiItem);
                            }
                        });

                        isStart = true;
                        isEnd = false;
                    }
                });

                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(startText.getWindowToken(), 0);
            }
        });


        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String strEnd = endText.getText().toString();
                TMapData tmapdata = new TMapData();

                tmapdata.findAllPOI(strEnd, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(final ArrayList<TMapPOIItem> poiItem) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                initData(poiItem);
                            }
                        });
                        isEnd = true;
                        isStart = false;
                    }

                });

                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(endText.getWindowToken(), 0);
            }
        });


        return view;
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
    }


    private void configureMapView() {
        mMapView.setSKPMapApiKey(mApiKey);
        //mMapView.setSKPMapBizappId(mBizAppID);
    }



    private void initData(ArrayList<TMapPOIItem> poiItem){
        findRoadList = new ArrayList<FindRoadRow>();

        for(int i = 0 ; i < poiItem.size() ; i++){

            TMapPOIItem  item = poiItem.get(i);

            FindRoadRow findRoadRow = new FindRoadRow();

            findRoadRow.setTitle(item.getPOIName().toString());
            findRoadRow.setAddress(item.getPOIAddress().replace("null", ""));
            findRoadRow.setTMapPoint(item.getPOIPoint());

            findRoadList.add(findRoadRow);
        }

        recyclerView.setAdapter(new FindRoadRecyclerAdapter(findRoadList,R.layout.find_road_row));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}
