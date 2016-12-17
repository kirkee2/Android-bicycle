package com.example.lkj.bicycleproject.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.example.lkj.bicycleproject.R;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class RecordRecyclerAdapter extends RecyclerView.Adapter<RecordRecyclerAdapter.ViewHolder> {

    private List<RecordRow> recordRowList;
    private int itemLayout;
    private AQuery aquery;


    public RecordRecyclerAdapter(List<RecordRow> items, int itemLayout) {
        this.recordRowList = items;
        this.itemLayout = itemLayout;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(itemLayout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {


        final RecordRow item = recordRowList.get(position);
        viewHolder.textTitle.setText(item.getTitle());
        if(item.getAuthed()){
            viewHolder.check.setVisibility(View.VISIBLE);
        }else {
            viewHolder.check.setVisibility(View.INVISIBLE);
        }

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
                        Picasso.with(viewHolder.img.getContext()).load(item.getImage()).into(viewHolder.img);

                    }
                });
            }
        };
        countThread.start();

        viewHolder.itemView.setTag(item);

    }

    @Override
    public int getItemCount() {
        return recordRowList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView img;
        public TextView textTitle;
        public ImageView check;

        public ViewHolder(View itemView) {
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.image);
            textTitle = (TextView) itemView.findViewById(R.id.title);
            check = (ImageView) itemView.findViewById(R.id.check);

        }

    }

    public void clearData() {
        int size = this.recordRowList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.recordRowList.remove(0);
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