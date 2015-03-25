package com.martin.knowledgebase;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.ViewHolder> implements View.OnClickListener, View.OnCreateContextMenuListener {

    private ArrayList<Entry> mEntries;

    public EntryAdapter(ArrayList<Entry> entries) {
        mEntries = entries;
    }

    @Override
    public EntryAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_entry, parent, false);
        v.setOnClickListener(this);
        v.setOnCreateContextMenuListener(this);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.left.setText(mEntries.get(position).getTitle());
        holder.right.setText(mEntries.get(position).getDate());
        // Setting the unique identifier as tag on the left textView
        holder.left.setTag(mEntries.get(position).getUid());
    }

    @Override
    public int getItemCount() {
        return mEntries.size();
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(v.getContext(), ViewActivity.class);
        // Getting uid from tag
        i.putExtra("uid", (int) v.findViewById(R.id.tvLeft).getTag());
        v.getContext().startActivity(i);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        // Quite the hacky approach, I'm adding the uid of each item as the groupId
        menu.add((int) v.findViewById(R.id.tvLeft).getTag(), 0, 0, "Edit");
        menu.add((int) v.findViewById(R.id.tvLeft).getTag(), 1, 1, "Delete");
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
