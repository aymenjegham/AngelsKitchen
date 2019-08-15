package com.angelstudio.angelskitchen.requests;

import com.angelstudio.angelskitchen.models.Categories;
import com.angelstudio.angelskitchen.models.Meals;
import com.angelstudio.angelskitchen.models.Recipes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeApi {


    // GET Random RECIPE REQUEST
    @GET("api/json/v1/1/random.php")
    Call<Meals> getRecipe();

    // GET area recipes
    @GET("api/json/v1/1/filter.php")
    Call<Recipes> getRecipes(
            @Query("a") String area

    );

    @GET("api/json/v1/1/lookup.php")
    Call<Meals> getRecipeById(
            @Query("i") String id

    );

    //get categories
    @GET("api/json/v1/1/categories.php")
    Call<Categories> getCategories();


    // GET category recipes
    @GET("api/json/v1/1/filter.php")
    Call<Recipes> getRecipesbyCategory(
            @Query("c") String category

    );
}
