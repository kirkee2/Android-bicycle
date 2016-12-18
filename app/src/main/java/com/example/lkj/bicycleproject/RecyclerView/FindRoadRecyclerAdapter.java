package com.example.lkj.bicycleproject.RecyclerView;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lkj.bicycleproject.R;

import java.util.List;

public class FindRoadRecyclerAdapter extends RecyclerView.Adapter<FindRoadRecyclerAdapter.ViewHolder> {

    private List<FindRoadRow> findRoadRowList;
    private int itemLayout;


    public FindRoadRecyclerAdapter(List<FindRoadRow> items, int itemLayout) {
        this.findRoadRowList = items;
        this.itemLayout = itemLayout;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(itemLayout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {


        final FindRoadRow item = findRoadRowList.get(position);
        viewHolder.textTitle.setText(item.getTitle());
        viewHolder.textAddress.setText(item.getAddress());

        viewHolder.itemView.setTag(item);

    }

    @Override
    public int getItemCount() {
        return findRoadRowList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textTitle;
        public TextView textAddress;

        public ViewHolder(View itemView) {
            super(itemView);

            textTitle = (TextView) itemView.findViewById(R.id.title);
            textAddress = (TextView) itemView.findViewById(R.id.address);
        }
    }

    public void clearData() {
        int size = this.findRoadRowList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.findRoadRowList.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }
}