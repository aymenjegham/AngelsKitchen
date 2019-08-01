package com.angelstudio.angelskitchen.requests;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.angelstudio.angelskitchen.AppExecutors;
import com.angelstudio.angelskitchen.models.Meal;
import com.angelstudio.angelskitchen.models.Meals;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

import static com.angelstudio.angelskitchen.util.Constants.NETWORK_TIMEOUT;

public class RecipeApiClient {

    private static RecipeApiClient instance;
    private MutableLiveData<List<Meal>> mRecipes;
    private MutableLiveData<List<Meal>> mRecipe;
    private RetrieveRecipeRunnable mRetrieveRecipeRunnable;
    private MutableLiveData<Boolean> mRecipeRequestTimeout = new MutableLiveData<>();

    public static RecipeApiClient getInstance(){
        if(instance == null){
            instance = new RecipeApiClient();
        }
        return instance;
    }

    private RecipeApiClient(){
         mRecipe = new MutableLiveData<>();
    }

    public LiveData<List<Meal>> getRecipe(){
        return mRecipe;
    }

    public LiveData<Boolean> isRecipeRequestTimedOut(){
        return mRecipeRequestTimeout;
    }

    public void cancelRequest(){

        if(mRetrieveRecipeRunnable != null){
            mRetrieveRecipeRunnable.cancelRequest();
        }
    }

    private class RetrieveRecipeRunnable implements Runnable{


        boolean cancelRequest;

        public RetrieveRecipeRunnable() {
            cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getRecipe().execute();
                if(cancelRequest){
                    return;
                }
                if(response.code() == 200){

                    List<Meal> meals = ((Meals)response.body()).getMeals();
                    Log.v("response", "response: " + meals.get(0).getStrMeal() );

                    mRecipe.postValue(meals);
                }
                else{
                    String error = response.errorBody().string();
                    Log.e("LogRun", "run: " + error );
                    mRecipe.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mRecipe.postValue(null);
            }

        }

        private Call<Meals> getRecipe(){
            return ServiceGenerator.getRecipeApi().getRecipe();
        }

        private void cancelRequest(){
            Log.d("LOGCAncelRequest", "cancelRequest: canceling the search request.");
            cancelRequest = true;
        }
    }

    public void getRandomRecipe(){
        if(mRetrieveRecipeRunnable != null){
            mRetrieveRecipeRunnable = null;
        }
        mRetrieveRecipeRunnable = new RetrieveRecipeRunnable();

        final Future handler = AppExecutors.getInstance().networkIO().submit(mRetrieveRecipeRunnable);

        mRecipeRequestTimeout.setValue(false);
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                // let the user know it's timed out
                mRecipeRequestTimeout.postValue(true);
                handler.cancel(true);
            }
        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);

    }

}
