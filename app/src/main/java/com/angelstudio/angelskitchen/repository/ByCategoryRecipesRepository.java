package com.angelstudio.angelskitchen.repository;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.angelstudio.angelskitchen.models.Recipe;
import com.angelstudio.angelskitchen.requests.RecipeApiClient;

import java.util.List;

public class ByCategoryRecipesRepository {

    private static ByCategoryRecipesRepository instance;
    private RecipeApiClient mRecipeApiClient;
    private String mQuery;
    private int mPageNumber;
    private MutableLiveData<Boolean> mIsQueryExhausted = new MutableLiveData<>();

    public static ByCategoryRecipesRepository getInstance(){
        if(instance == null){
            instance = new ByCategoryRecipesRepository();
        }
        return instance;
    }

    private ByCategoryRecipesRepository(){
        mRecipeApiClient = RecipeApiClient.getInstance();
    }




    public LiveData<Boolean> isQueryExhausted(){
        return mIsQueryExhausted;
    }

    public LiveData<List<Recipe>> getRecipesbycat(){
        return mRecipeApiClient.getRecipesByCat();
    }




    public void getRecipebyCategory(String category){

        mRecipeApiClient.getRecipebyCategory(category);
    }

    public void cancelRequest(){
        mRecipeApiClient.cancelRequest();
    }

    public LiveData<Boolean> isRecipeRequestTimedOut(){
        return mRecipeApiClient.isRecipeRequestTimedOut();
    }
}
