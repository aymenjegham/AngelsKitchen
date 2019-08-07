package com.angelstudio.angelskitchen.models;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Meal {

    @SerializedName("idMeal")
    @Expose
    private String idMeal;
    @SerializedName("strMeal")
    @Expose
    private String strMeal;
    @SerializedName("strDrinkAlternate")
    @Expose
    private Object strDrinkAlternate;
    @SerializedName("strCategory")
    @Expose
    private String strCategory;
    @SerializedName("strArea")
    @Expose
    private String strArea;
    @SerializedName("strInstructions")
    @Expose
    private String strInstructions;
    @SerializedName("strMealThumb")
    @Expose
    private String strMealThumb;
    @SerializedName("strTags")
    @Expose
    private Object strTags;
    @SerializedName("strYoutube")
    @Expose
    private String strYoutube;
    @SerializedName("strIngredient1")
    @Expose
    private String strIngredient1;
    @SerializedName("strIngredient2")
    @Expose
    private String strIngredient2;
    @SerializedName("strIngredient3")
    @Expose
    private String strIngredient3;
    @SerializedName("strIngredient4")
    @Expose
    private String strIngredient4;
    @SerializedName("strIngredient5")
    @Expose
    private String strIngredient5;
    @SerializedName("strIngredient6")
    @Expose
    private String strIngredient6;
    @SerializedName("strIngredient7")
    @Expose
    private String strIngredient7;
    @SerializedName("strIngredient8")
    @Expose
    private String strIngredient8;
    @SerializedName("strIngredient9")
    @Expose
    private String strIngredient9;
    @SerializedName("strIngredient10")
    @Expose
    private String strIngredient10;
    @SerializedName("strIngredient11")
    @Expose
    private String strIngredient11;
    @SerializedName("strIngredient12")
    @Expose
    private String strIngredient12;
    @SerializedName("strIngredient13")
    @Expose
    private String strIngredient13;
    @SerializedName("strIngredient14")
    @Expose
    private String strIngredient14;
    @SerializedName("strIngredient15")
    @Expose
    private String strIngredient15;
    @SerializedName("strIngredient16")
    @Expose
    private String strIngredient16;
    @SerializedName("strIngredient17")
    @Expose
    private String strIngredient17;
    @SerializedName("strIngredient18")
    @Expose
    private String strIngredient18;
    @SerializedName("strIngredient19")
    @Expose
    private String strIngredient19;
    @SerializedName("strIngredient20")
    @Expose
    private String strIngredient20;
    @SerializedName("strMeasure1")
    @Expose
    private String strMeasure1;
    @SerializedName("strMeasure2")
    @Expose
    private String strMeasure2;
    @SerializedName("strMeasure3")
    @Expose
    private String strMeasure3;
    @SerializedName("strMeasure4")
    @Expose
    private String strMeasure4;
    @SerializedName("strMeasure5")
    @Expose
    private String strMeasure5;
    @SerializedName("strMeasure6")
    @Expose
    private String strMeasure6;
    @SerializedName("strMeasure7")
    @Expose
    private String strMeasure7;
    @SerializedName("strMeasure8")
    @Expose
    private String strMeasure8;
    @SerializedName("strMeasure9")
    @Expose
    private String strMeasure9;
    @SerializedName("strMeasure10")
    @Expose
    private String strMeasure10;
    @SerializedName("strMeasure11")
    @Expose
    private String strMeasure11;
    @SerializedName("strMeasure12")
    @Expose
    private String strMeasure12;
    @SerializedName("strMeasure13")
    @Expose
    private String strMeasure13;
    @SerializedName("strMeasure14")
    @Expose
    private String strMeasure14;
    @SerializedName("strMeasure15")
    @Expose
    private String strMeasure15;
    @SerializedName("strMeasure16")
    @Expose
    private String strMeasure16;
    @SerializedName("strMeasure17")
    @Expose
    private String strMeasure17;
    @SerializedName("strMeasure18")
    @Expose
    private String strMeasure18;
    @SerializedName("strMeasure19")
    @Expose
    private String strMeasure19;
    @SerializedName("strMeasure20")
    @Expose
    private String strMeasure20;
    @SerializedName("strSource")
    @Expose
    private String strSource;
    @SerializedName("dateModified")
    @Expose
    private Object dateModified;


    private ArrayList<Ingredient> ingredient= new ArrayList<>();

     public ArrayList<Ingredient> getIngredients(){

         ingredient.add(new Ingredient(strIngredient1,strMeasure1));
         ingredient.add(new Ingredient(strIngredient2,strMeasure2));
         ingredient.add(new Ingredient(strIngredient3,strMeasure3));
         ingredient.add(new Ingredient(strIngredient4,strMeasure4));
         ingredient.add(new Ingredient(strIngredient5,strMeasure5));
         ingredient.add(new Ingredient(strIngredient6,strMeasure6));
         ingredient.add(new Ingredient(strIngredient7,strMeasure7));
         ingredient.add(new Ingredient(strIngredient8,strMeasure8));
         ingredient.add(new Ingredient(strIngredient9,strMeasure9));
         ingredient.add(new Ingredient(strIngredient10,strMeasure10));
         ingredient.add(new Ingredient(strIngredient11,strMeasure11));
         ingredient.add(new Ingredient(strIngredient12,strMeasure12));
         ingredient.add(new Ingredient(strIngredient13,strMeasure13));
         ingredient.add(new Ingredient(strIngredient14,strMeasure14));
         ingredient.add(new Ingredient(strIngredient15,strMeasure15));
         ingredient.add(new Ingredient(strIngredient16,strMeasure16));
         ingredient.add(new Ingredient(strIngredient17,strMeasure17));
         ingredient.add(new Ingredient(strIngredient18,strMeasure18));
         ingredient.add(new Ingredient(strIngredient19,strMeasure19));
         ingredient.add(new Ingredient(strIngredient20,strMeasure20));

         return ingredient;
    }

    public String getIdMeal() {
        return idMeal;
    }

    public String getStrMeal() {
        return strMeal;
    }


    public String getStrCategory() {
        return strCategory;
    }

    public String getStrArea() {
        return strArea;
    }


    public String getStrInstructions() {
        return strInstructions;
    }


    public String getStrMealThumb() {
        return strMealThumb;
    }

    public Object getStrTags() {
        return strTags;
    }

    public String getStrYoutube() {
        return strYoutube;
    }


    public String getStrSource() {
        return strSource;
    }


    public Object getDateModified() {
        return dateModified;
    }



}
