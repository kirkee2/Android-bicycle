package com.example.lkj.bicycleproject.ListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.lkj.bicycleproject.R;

import java.util.ArrayList;

public class AgeAdapter extends ArrayAdapter<AgeList> {
    private ArrayList<AgeList> items;
    private Context context;
    private int resId;

    public AgeAdapter(Context context, int textViewResourceId, ArrayList<AgeList> objects){
        super(context,textViewResourceId,objects);
        this.context = context;
        resId = textViewResourceId;
        this.items = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(resId,null);
        }

        AgeList aL = items.get(position);

        if(aL != null){
            TextView t1 = (TextView)convertView.findViewById(R.id.age);

            if(t1 !=null){
                t1.setText(aL.getAge());
            }
        }
        return convertView;
    }

}
