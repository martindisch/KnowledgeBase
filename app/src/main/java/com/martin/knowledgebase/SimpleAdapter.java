package com.martin.knowledgebase;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {

    private JSONArray mEntries;
    private OnClickListener mCallback;

    public SimpleAdapter(JSONArray entries, OnClickListener callback) {
        mEntries = entries;
        mCallback = callback;
    }

    public JSONArray getEntries() {
        return mEntries;
    }

    @Override
    public SimpleAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public int getItemCount() {
        return mEntries.length();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        try {
            holder.tvText.setText(mEntries.getString(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onClick(v, position);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mCallback.onLongClick(v, position);
                    return true;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static interface OnClickListener {
        public void onClick(View v, int position);

        public void onLongClick(View v, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvText;

        public ViewHolder(View v) {
            super(v);
            tvText = (TextView) v.findViewById(android.R.id.text1);
        }
    }

}