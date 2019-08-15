package com.angelstudio.angelskitchen.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.angelstudio.angelskitchen.models.Category;
import com.angelstudio.angelskitchen.models.Meal;
import com.angelstudio.angelskitchen.requests.RecipeApiClient;

import java.util.List;

public class CategoriesRepository {

    private static CategoriesRepository instance;
    private RecipeApiClient mRecipeApiClient;
    private String mQuery;
    private int mPageNumber;
    private MutableLiveData<Boolean> mIsQueryExhausted = new MutableLiveData<>();
    private MediatorLiveData<List<Category>> mCategories = new MediatorLiveData<>();

    public static CategoriesRepository getInstance(){
        if(instance == null){
            instance = new CategoriesRepository();
        }
        return instance;
    }

    private CategoriesRepository(){
        mRecipeApiClient = RecipeApiClient.getInstance();
        initMediators();
    }
    private void initMediators(){
        LiveData<List<Category>> recipeListApiSource = mRecipeApiClient.getCategory();
        mCategories.addSource(recipeListApiSource, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable List<Category> categories) {

                if(categories != null){
                    mCategories.setValue(categories);
                    doneQuery(categories);
                }
                else{
                    // search database cache
                    doneQuery(null);
                }
            }
        });
    }

    private void doneQuery(List<Category> list){
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

    public LiveData<List<Category>> getCategories(){
        return mCategories;
    }

    public LiveData<List<Category>> getCategory(){
        return mRecipeApiClient.getCategory();
    }

    public void getCategorys(){
        mRecipeApiClient.getCategories();
    }

    public void cancelRequest(){
        mRecipeApiClient.cancelRequest();
    }

    public LiveData<Boolean> isRecipeRequestTimedOut(){
        return mRecipeApiClient.isRecipeRequestTimedOut();
    }

}
