package com.example.isp291_folomeevstepan;

public class ServiceItem {
    String title;
    String description;
    int imageResId; // ID ресурса картинки
    Class<?> targetActivity; // Какую Activity открывать

    public ServiceItem(String title, String description, int imageResId, Class<?> targetActivity) {
        this.title = title;
        this.description = description;
        this.imageResId = imageResId;
        this.targetActivity = targetActivity;
    }
}