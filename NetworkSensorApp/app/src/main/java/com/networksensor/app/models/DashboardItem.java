package com.networksensor.app.models;

public class DashboardItem {
    
    public enum ItemType {
        NETWORK_STATUS,
        SENSOR_STATUS,
        ACTIVITY,
        FEATURE_CARD
    }
    
    private String title;
    private String subtitle;
    private String description;
    private int iconResId;
    private ItemType itemType;
    private int colorResId;
    
    public DashboardItem(String title, String subtitle, String description, int iconResId, ItemType itemType) {
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.iconResId = iconResId;
        this.itemType = itemType;
    }
    
    public DashboardItem(String title, String subtitle, String description, int iconResId, ItemType itemType, int colorResId) {
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.iconResId = iconResId;
        this.itemType = itemType;
        this.colorResId = colorResId;
    }
    
    // Getters
    public String getTitle() {
        return title;
    }
    
    public String getSubtitle() {
        return subtitle;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getIconResId() {
        return iconResId;
    }
    
    public ItemType getItemType() {
        return itemType;
    }
    
    public int getColorResId() {
        return colorResId;
    }
    
    // Setters
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }
    
    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }
    
    public void setColorResId(int colorResId) {
        this.colorResId = colorResId;
    }
}