package com.angelstudio.angelskitchen.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.angelstudio.angelskitchen.models.Meal;
import com.angelstudio.angelskitchen.requests.RecipeApiClient;

import java.util.List;

public class SearchByIdRecipeRepository {

    private static SearchByIdRecipeRepository instance;
    private RecipeApiClient mRecipeApiClient;
    private String mQuery;
    private int mPageNumber;
    private MutableLiveData<Boolean> mIsQueryExhausted = new MutableLiveData<>();
    private MediatorLiveData<List<Meal>> mRecipeById = new MediatorLiveData<>();

    public static SearchByIdRecipeRepository getInstance(){
        if(instance == null){
            instance = new SearchByIdRecipeRepository();
        }
        return instance;
    }

    private SearchByIdRecipeRepository(){
        mRecipeApiClient = RecipeApiClient.getInstance();
        initMediators();
    }
    private void initMediators(){
        LiveData<List<Meal>> recipeListApiSource = mRecipeApiClient.getRecipeById();
        mRecipeById.addSource(recipeListApiSource, new Observer<List<Meal>>() {
            @Override
            public void onChanged(@Nullable List<Meal> recipes) {

                if(recipes != null){
                    mRecipeById.setValue(recipes);
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
        return mRecipeById;
    }

    public LiveData<List<Meal>> getRecipe(){
        return mRecipeApiClient.getRecipe();
    }

    public void getRecipebyId(String id){
        mRecipeApiClient.getRecipeBiId(id);
    }

    public void cancelRequest(){
        mRecipeApiClient.cancelRequest();
    }

    public LiveData<Boolean> isRecipeRequestTimedOut(){
        return mRecipeApiClient.isRecipeRequestTimedOut();
    }

}
