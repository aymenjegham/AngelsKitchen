package com.angelstudio.angelskitchen.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Ingredient {

    String Ingred;
    String measure;

    public Ingredient(String ingred, String measure) {
        Ingred = ingred;
        this.measure = measure;
    }

    public Ingredient() {

    }


    public String getIngred() {
        return Ingred;
    }

    public void setIngred(String ingred) {
        Ingred = ingred;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }
}
