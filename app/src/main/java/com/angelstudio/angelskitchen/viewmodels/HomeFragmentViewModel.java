package com.angelstudio.angelskitchen.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.angelstudio.angelskitchen.models.Meal;
import com.angelstudio.angelskitchen.repository.RandomRecipeRepository;

import java.util.List;

public class HomeFragmentViewModel extends ViewModel {

    private RandomRecipeRepository mRecipeRepository;
    private String mRecipeId;
    private boolean mDidRetrieveRecipe;

    public HomeFragmentViewModel() {
        mRecipeRepository = RandomRecipeRepository.getInstance();
        mDidRetrieveRecipe = false;

    }

    public LiveData<List<Meal>> getRecipes(){
        return mRecipeRepository.getRecipes();
    }

    public LiveData<Boolean> isRecipeRequestTimedOut(){
        return mRecipeRepository.isRecipeRequestTimedOut();
    }
    public void getRandomRecipe(){

        mRecipeRepository.getRandom();
    }



    public void setRetrievedRecipe(boolean retrievedRecipe){
        mDidRetrieveRecipe = retrievedRecipe;
    }

    public boolean didRetrieveRecipe(){
        return mDidRetrieveRecipe;
    }



}
