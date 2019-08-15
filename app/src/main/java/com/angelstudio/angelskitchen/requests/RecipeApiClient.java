package com.angelstudio.angelskitchen.requests;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.angelstudio.angelskitchen.AppExecutors;
import com.angelstudio.angelskitchen.models.Categories;
import com.angelstudio.angelskitchen.models.Category;
import com.angelstudio.angelskitchen.models.Meal;
import com.angelstudio.angelskitchen.models.Meals;
import com.angelstudio.angelskitchen.models.Recipe;
import com.angelstudio.angelskitchen.models.Recipes;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

import static com.angelstudio.angelskitchen.util.Constants.NETWORK_TIMEOUT;

public class RecipeApiClient {

    private static RecipeApiClient instance;
    private MutableLiveData<List<Recipe>> mRecipes;
    private MutableLiveData<List<Meal>> mRecipe;
    private MutableLiveData<List<Category>> mCategory;
    private MutableLiveData<List<Meal>> mRecipeById;
    private RetrieveRecipeRunnable mRetrieveRecipeRunnable;
    private RetrieveRecipesRunnable mRetrieveRecipesRunnable;
    private RetrieveRecipeByIdRunnable mRetrieveRecipeByIdRunnable;
    private RetrieveCategoriesRunnable mRetrieveCategoriesRunnable;
    private RetrieveRecipeByCategoryRunnable mRetrieveRecipeByCategoryRunnable;

    private MutableLiveData<Boolean> mRecipeRequestTimeout = new MutableLiveData<>();

    public static RecipeApiClient getInstance(){
        if(instance == null){
            instance = new RecipeApiClient();
        }
        return instance;
    }

    private RecipeApiClient(){
         mRecipe = new MutableLiveData<>();
         mRecipes=new MutableLiveData<>();
         mRecipeById=new MutableLiveData<>();
         mCategory=new MutableLiveData<>();
    }

    public LiveData<List<Meal>> getRecipe(){
        return mRecipe;
    }
    public LiveData<List<Recipe>> getRecipes(){
        return mRecipes;
    }
    public LiveData<List<Meal>> getRecipeById(){
        return mRecipeById;
    }
    public LiveData<List<Category>> getCategory(){
        return mCategory;
    }
    public LiveData<List<Recipe>> getRecipesByCat(){
        return mRecipes;
    }

    public LiveData<Boolean> isRecipeRequestTimedOut(){
        return mRecipeRequestTimeout;
    }

    public void cancelRequest(){

        if(mRetrieveRecipeRunnable != null){
            mRetrieveRecipeRunnable.cancelRequest();
        }
        if(mRetrieveRecipesRunnable != null){
            mRetrieveRecipesRunnable.cancelRequest();
        }
        if(mRetrieveRecipeByIdRunnable != null){
            mRetrieveRecipeByIdRunnable.cancelRequest();
        }

        if(mRetrieveCategoriesRunnable != null){
            mRetrieveCategoriesRunnable.cancelRequest();
        }

        if(mRetrieveRecipeByCategoryRunnable != null){
            mRetrieveRecipeByCategoryRunnable.cancelRequest();
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

    private class RetrieveRecipesRunnable implements Runnable{


        boolean cancelRequest;
        private String area;

        public RetrieveRecipesRunnable(String area) {
            cancelRequest = false;
            this.area=area;
        }

        @Override
        public void run() {
            try {
                Response response = getRecipes(area).execute();


                if(cancelRequest){

                    return;
                }
                if(response.code() == 200){


                    List<Recipe> recipes = ((Recipes)response.body()).getRecipes();



                     mRecipes.postValue(recipes);
                }
                else{
                    String error = response.errorBody().string();
                    Log.e("LogRun", "run: " + error );
                    mRecipes.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();

                mRecipes.postValue(null);
            }

        }

        private Call<Recipes> getRecipes(String area){
            return ServiceGenerator.getRecipeApi().getRecipes(area);
        }

        private void cancelRequest(){
            Log.d("LOGCAncelRequest", "cancelRequest: canceling the search request.");
            cancelRequest = true;
        }
    }

    private class RetrieveRecipeByIdRunnable implements Runnable{


        boolean cancelRequest;
        private String id;

        public RetrieveRecipeByIdRunnable(String id) {
            cancelRequest = false;
            this.id=id;
        }

        @Override
        public void run() {
            try {
                Response response = getRecipeById(id).execute();
                if(cancelRequest){
                    return;
                }
                if(response.code() == 200){

                    List<Meal> meals = ((Meals)response.body()).getMeals();

                    mRecipeById.postValue(meals);
                }
                else{
                    String error = response.errorBody().string();
                    Log.e("LogRun", "run: " + error );
                    mRecipeById.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mRecipeById.postValue(null);
            }

        }



        private Call<Meals> getRecipeById(String id){
            return ServiceGenerator.getRecipeApi().getRecipeById(id);
        }

        private void cancelRequest(){
            Log.d("LOGCAncelRequest", "cancelRequest: canceling the search request.");
            cancelRequest = true;
        }
    }

    private class RetrieveCategoriesRunnable implements Runnable{


        boolean cancelRequest;

        public RetrieveCategoriesRunnable() {
            cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getCategories().execute();
                if(cancelRequest){
                    return;
                }
                if(response.code() == 200){

                    List<Category> categories = ((Categories)response.body()).getCategories();


                    mCategory.postValue(categories);
                }
                else{
                    String error = response.errorBody().string();
                    Log.e("LogRun", "run: " + error );
                    mCategory.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mCategory.postValue(null);
            }

        }



        private Call<Categories> getCategories(){
            return ServiceGenerator.getRecipeApi().getCategories();
        }

        private void cancelRequest(){
            Log.d("LOGCAncelRequest", "cancelRequest: canceling the search request.");
            cancelRequest = true;
        }
    }
    private class RetrieveRecipeByCategoryRunnable implements Runnable{


        boolean cancelRequest;
        private String category;

        public RetrieveRecipeByCategoryRunnable(String category) {
            cancelRequest = false;
            this.category=category;
        }

        @Override
        public void run() {
            try {
                Response response = getRecipeByCategory(category).execute();
                if(cancelRequest){
                    return;
                }
                if(response.code() == 200){

                    List<Recipe> recipes = ((Recipes)response.body()).getRecipes();



                    mRecipes.postValue(recipes);
                }
                else{
                    String error = response.errorBody().string();
                    Log.e("LogRun", "run: " + error );
                    mRecipes.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mRecipes.postValue(null);
            }

        }



        private Call<Recipes> getRecipeByCategory(String category){
            return ServiceGenerator.getRecipeApi().getRecipesbyCategory(category);
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

    public void getCategories(){
        if(mRetrieveCategoriesRunnable != null){
            mRetrieveCategoriesRunnable = null;
        }
        mRetrieveCategoriesRunnable = new RetrieveCategoriesRunnable();

        final Future handler = AppExecutors.getInstance().networkIO().submit(mRetrieveCategoriesRunnable);

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

    public void getRecipebyCategory(String category){
        if(mRetrieveRecipeByCategoryRunnable != null){
            mRetrieveRecipeByCategoryRunnable = null;
        }
        mRetrieveRecipeByCategoryRunnable = new RetrieveRecipeByCategoryRunnable(category);

        final Future handler = AppExecutors.getInstance().networkIO().submit(mRetrieveRecipeByCategoryRunnable);

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


    public void getRecipebyCountry(String area){
        if(mRetrieveRecipesRunnable != null){
            mRetrieveRecipesRunnable = null;
        }
        mRetrieveRecipesRunnable = new RetrieveRecipesRunnable(area);

        final Future handler = AppExecutors.getInstance().networkIO().submit(mRetrieveRecipesRunnable);

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

    public void getRecipeBiId(String id){
        if(mRetrieveRecipeByIdRunnable != null){
            mRetrieveRecipeByIdRunnable = null;
        }
        mRetrieveRecipeByIdRunnable = new RetrieveRecipeByIdRunnable(id);

        final Future handler = AppExecutors.getInstance().networkIO().submit(mRetrieveRecipeByIdRunnable);

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
