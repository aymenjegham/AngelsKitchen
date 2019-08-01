package com.angelstudio.angelskitchen.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.angelstudio.angelskitchen.models.Meal;
import com.angelstudio.angelskitchen.models.Meals;
import com.angelstudio.angelskitchen.requests.RecipeApiClient;

import java.util.List;

public class RandomRecipeRepository {

    private static RandomRecipeRepository instance;
    private RecipeApiClient mRecipeApiClient;
    private String mQuery;
    private int mPageNumber;
    private MutableLiveData<Boolean> mIsQueryExhausted = new MutableLiveData<>();
    private MediatorLiveData<List<Meal>> mRecipes = new MediatorLiveData<>();

    public static RandomRecipeRepository getInstance(){
        if(instance == null){
            instance = new RandomRecipeRepository();
        }
        return instance;
    }

    private RandomRecipeRepository(){
        mRecipeApiClient = RecipeApiClient.getInstance();
        initMediators();
    }
    private void initMediators(){
        LiveData<List<Meal>> recipeListApiSource = mRecipeApiClient.getRecipe();
        mRecipes.addSource(recipeListApiSource, new Observer<List<Meal>>() {
            @Override
            public void onChanged(@Nullable List<Meal> recipes) {

                if(recipes != null){
                    mRecipes.setValue(recipes);
                    doneQuery(recipes);
                }
                else{
                    // search database cache
                    doneQuery(null);
                }
            }
        });
    }

    private void doneQuery(List<Meal> list){
        if(list != null){
            if (list.size() % 30 != 0) {
                mIsQueryExhausted.setValue(true);
            }
        }
        else{
            mIsQueryExhausted.setValue(true);
        }
    }


    public LiveData<Boolean> isQueryExhausted(){
        return mIsQueryExhausted;
    }

    public LiveData<List<Meal>> getRecipes(){
        return mRecipes;
    }

    public LiveData<List<Meal>> getRecipe(){
        return mRecipeApiClient.getRecipe();
    }

    public void getRandom(){
        mRecipeApiClient.getRandomRecipe();
    }

    public void cancelRequest(){
        mRecipeApiClient.cancelRequest();
    }

    public LiveData<Boolean> isRecipeRequestTimedOut(){
        return mRecipeApiClient.isRecipeRequestTimedOut();
    }

}
