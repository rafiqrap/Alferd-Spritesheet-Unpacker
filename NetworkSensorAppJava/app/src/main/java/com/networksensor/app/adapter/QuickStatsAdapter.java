package com.networksensor.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.networksensor.app.R;
import com.networksensor.app.fragment.DashboardFragment.QuickStatItem;

import java.util.ArrayList;
import java.util.List;

public class QuickStatsAdapter extends RecyclerView.Adapter<QuickStatsAdapter.ViewHolder> {
    
    private List<QuickStatItem> items = new ArrayList<>();

    public void updateData(List<QuickStatItem> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuickStatItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView text1;
        private final TextView text2;

        ViewHolder(View view) {
            super(view);
            text1 = view.findViewById(android.R.id.text1);
            text2 = view.findViewById(android.R.id.text2);
        }

        void bind(QuickStatItem item) {
            text1.setText(item.value);
            text2.setText(item.label);
        }
    }
}