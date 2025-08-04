package com.networksensor.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.networksensor.app.R;
import com.networksensor.app.models.DashboardItem;

import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    private static final int VIEW_TYPE_FEATURE_CARD = 0;
    private static final int VIEW_TYPE_ACTIVITY_ITEM = 1;
    private static final int VIEW_TYPE_STATUS_CARD = 2;
    
    private Context context;
    private List<DashboardItem> items;
    private OnItemClickListener listener;
    
    public interface OnItemClickListener {
        void onItemClick(DashboardItem item, int position);
    }
    
    public DashboardAdapter(Context context, List<DashboardItem> items) {
        this.context = context;
        this.items = items;
    }
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    
    public void updateItems(List<DashboardItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        
        switch (viewType) {
            case VIEW_TYPE_FEATURE_CARD:
                View featureView = inflater.inflate(R.layout.item_feature_card, parent, false);
                return new FeatureCardViewHolder(featureView);
            case VIEW_TYPE_ACTIVITY_ITEM:
                View activityView = inflater.inflate(R.layout.item_activity, parent, false);
                return new ActivityItemViewHolder(activityView);
            case VIEW_TYPE_STATUS_CARD:
                View statusView = inflater.inflate(R.layout.item_status_card, parent, false);
                return new StatusCardViewHolder(statusView);
            default:
                View defaultView = inflater.inflate(R.layout.item_activity, parent, false);
                return new ActivityItemViewHolder(defaultView);
        }
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DashboardItem item = items.get(position);
        
        if (holder instanceof FeatureCardViewHolder) {
            ((FeatureCardViewHolder) holder).bind(item);
        } else if (holder instanceof ActivityItemViewHolder) {
            ((ActivityItemViewHolder) holder).bind(item);
        } else if (holder instanceof StatusCardViewHolder) {
            ((StatusCardViewHolder) holder).bind(item);
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item, position);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return items.size();
    }
    
    @Override
    public int getItemViewType(int position) {
        DashboardItem item = items.get(position);
        switch (item.getItemType()) {
            case FEATURE_CARD:
                return VIEW_TYPE_FEATURE_CARD;
            case ACTIVITY:
                return VIEW_TYPE_ACTIVITY_ITEM;
            case NETWORK_STATUS:
            case SENSOR_STATUS:
                return VIEW_TYPE_STATUS_CARD;
            default:
                return VIEW_TYPE_ACTIVITY_ITEM;
        }
    }
    
    // Feature Card ViewHolder
    public static class FeatureCardViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardView;
        private ImageView icon;
        private TextView title;
        private TextView description;
        private ImageView arrowIcon;
        
        public FeatureCardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            icon = itemView.findViewById(R.id.featureIcon);
            title = itemView.findViewById(R.id.featureTitle);
            description = itemView.findViewById(R.id.featureDescription);
            arrowIcon = itemView.findViewById(R.id.arrowIcon);
        }
        
        public void bind(DashboardItem item) {
            icon.setImageResource(item.getIconResId());
            title.setText(item.getTitle());
            description.setText(item.getDescription());
            
            // Set gradient background based on item type
            if (item.getItemType() == DashboardItem.ItemType.NETWORK_STATUS) {
                cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.gradient_blue_start));
            } else {
                cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.gradient_purple_start));
            }
        }
    }
    
    // Activity Item ViewHolder
    public static class ActivityItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView title;
        private TextView subtitle;
        private TextView time;
        
        public ActivityItemViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.activityIcon);
            title = itemView.findViewById(R.id.activityTitle);
            subtitle = itemView.findViewById(R.id.activitySubtitle);
            time = itemView.findViewById(R.id.activityTime);
        }
        
        public void bind(DashboardItem item) {
            icon.setImageResource(item.getIconResId());
            title.setText(item.getTitle());
            subtitle.setText(item.getSubtitle());
            
            // Set icon color based on item type
            if (item.getColorResId() != 0) {
                icon.setColorFilter(ContextCompat.getColor(itemView.getContext(), item.getColorResId()));
            }
        }
    }
    
    // Status Card ViewHolder
    public static class StatusCardViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardView;
        private ImageView icon;
        private TextView title;
        private TextView status;
        private TextView description;
        
        public StatusCardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            icon = itemView.findViewById(R.id.statusIcon);
            title = itemView.findViewById(R.id.statusTitle);
            status = itemView.findViewById(R.id.statusText);
            description = itemView.findViewById(R.id.statusDescription);
        }
        
        public void bind(DashboardItem item) {
            icon.setImageResource(item.getIconResId());
            title.setText(item.getTitle());
            status.setText(item.getSubtitle());
            description.setText(item.getDescription());
            
            // Set colors based on status
            if (item.getColorResId() != 0) {
                icon.setColorFilter(ContextCompat.getColor(itemView.getContext(), item.getColorResId()));
                status.setTextColor(ContextCompat.getColor(itemView.getContext(), item.getColorResId()));
            }
        }
    }
}