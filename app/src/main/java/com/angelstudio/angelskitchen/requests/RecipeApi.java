package com.angelstudio.angelskitchen.requests;

import com.angelstudio.angelskitchen.models.Meals;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeApi {


    // GET Random RECIPE REQUEST
    @GET("api/json/v1/1/random.php")
    Call<Meals> getRecipe();
}
