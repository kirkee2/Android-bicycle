package com.example.lkj.bicycleproject.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.example.lkj.bicycleproject.Connection.Connect;
import com.example.lkj.bicycleproject.Connection.WebHook;
import com.example.lkj.bicycleproject.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by leegunjoon on 2016. 12. 10..
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

    private List<Row> rowList;
    private int itemLayout;
    private AQuery aquery;


    public MyRecyclerAdapter(List<Row> items, int itemLayout) {

        this.rowList = items;
        this.itemLayout = itemLayout;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(itemLayout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {


        final Row item = rowList.get(position);
        viewHolder.textTitle.setText(item.getTitle());

        final Handler mHandler = new Handler();

        Thread countThread = new Thread("Count Thread") {
            public void run() {

                try {
                    URL url = new URL(item.getImage());
                    URLConnection conn = url.openConnection();
                    conn.connect();
                    BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                    Bitmap bm = BitmapFactory.decodeStream(bis);
                    bis.close();
                } catch (Exception e) {
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
// 현재까지 카운트 된 수를 텍스트뷰에 출력한다.
                        Picasso.with(viewHolder.img.getContext()).load(item.getImage()).into(viewHolder.img);

                    }
                });
            }
        };
        countThread.start();

        /*
        try {
            URL url = new URL(item.getImage());
            URLConnection conn = url.openConnection();
            conn.connect();
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            Bitmap bm = BitmapFactory.decodeStream(bis);
            bis.close();
            viewHolder.img.getContext();

            Picasso.with(viewHolder.img.getContext()).load(item.getImage()).into(viewHolder.img);

            new WebHook().execute("2"+ item.getImage());
            //viewHolder.img.setImageBitmap(bm);
        } catch (Exception e) {

            new WebHook().execute("3" +e.toString());
        }
        */
        viewHolder.itemView.setTag(item);

    }

    @Override
    public int getItemCount() {
        return rowList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView img;
        public TextView textTitle;

        public ViewHolder(View itemView) {
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.image);
            textTitle = (TextView) itemView.findViewById(R.id.title);

        }

    }

    public void clearData() {
        int size = this.rowList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.rowList.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    private class ImageFromUrl extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                URLConnection conn = url.openConnection();
                conn.connect();
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                Bitmap bm = BitmapFactory.decodeStream(bis);
                bis.close();
                return bm;
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(Bitmap result) {

        }
    }
}