package com.angelstudio.angelskitchen.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Recipes {
    @SerializedName("meals")
    @Expose
    private List<Recipe> recipes = null;

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setMeals(List<Recipe> recipes) {
        this.recipes = recipes;
    }
}
