package com.martin.knowledgebase;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.ViewHolder> implements View.OnClickListener {

    private ArrayList<Entry> mEntries;

    public EntryAdapter(ArrayList<Entry> entries) {
        mEntries = entries;
    }

    @Override
    public EntryAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_entry, parent, false);
        v.setOnClickListener(this);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.left.setText(mEntries.get(position).getTitle());
        holder.right.setText(mEntries.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return mEntries.size();
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(v.getContext(), EditActivity.class);
        int index = ((MainActivity) v.getContext()).mRecyclerView.getChildPosition(v);
        ArrayList<Entry> entries = PlainStorage.getInstance().getmEntries();
        Entry entry = entries.get(index);
        i.putExtra("index", index);
        i.putExtra("title", entry.getTitle());
        i.putExtra("text", entry.getText());
        v.getContext().startActivity(i);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView left, right;

        public ViewHolder(View v) {
            super(v);
            left = (TextView) v.findViewById(R.id.tvLeft);
            right = (TextView) v.findViewById(R.id.tvRight);
        }
    }

}
