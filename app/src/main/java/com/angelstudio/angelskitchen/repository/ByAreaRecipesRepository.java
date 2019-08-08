package com.angelstudio.angelskitchen.repository;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.angelstudio.angelskitchen.models.Recipe;
import com.angelstudio.angelskitchen.requests.RecipeApiClient;

import java.util.List;

public class ByAreaRecipesRepository {

    private static ByAreaRecipesRepository instance;
    private RecipeApiClient mRecipeApiClient;
    private String mQuery;
    private int mPageNumber;
    private MutableLiveData<Boolean> mIsQueryExhausted = new MutableLiveData<>();

    public static ByAreaRecipesRepository getInstance(){
        if(instance == null){
            instance = new ByAreaRecipesRepository();
        }
        return instance;
    }

    private ByAreaRecipesRepository(){
        mRecipeApiClient = RecipeApiClient.getInstance();
    }




    public LiveData<Boolean> isQueryExhausted(){
        return mIsQueryExhausted;
    }

    public LiveData<List<Recipe>> getRecipes(){
        return mRecipeApiClient.getRecipes();
    }




    public void getRecipebyCountry(String area){

        mRecipeApiClient.getRecipebyCountry(area);
    }

    public void cancelRequest(){
        mRecipeApiClient.cancelRequest();
    }

    public LiveData<Boolean> isRecipeRequestTimedOut(){
        return mRecipeApiClient.isRecipeRequestTimedOut();
    }
}
