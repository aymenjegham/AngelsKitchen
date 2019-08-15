package com.angelstudio.angelskitchen.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Categories {

    @SerializedName("categories")
    @Expose
    private List<Category> categories = null;

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
