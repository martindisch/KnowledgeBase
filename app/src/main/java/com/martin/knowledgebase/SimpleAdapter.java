package com.martin.knowledgebase;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> implements View.OnClickListener {

    private JSONArray mEntries;

    public SimpleAdapter(JSONArray entries) {
        mEntries = entries;
    }

    @Override
    public SimpleAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        v.setOnClickListener(this);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            holder.tvText.setText(mEntries.getString(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mEntries.length();
    }

    @Override
    public void onClick(View v) {
        
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvText;

        public ViewHolder(View v) {
            super(v);
            tvText = (TextView) v.findViewById(android.R.id.text1);
        }
    }

}
