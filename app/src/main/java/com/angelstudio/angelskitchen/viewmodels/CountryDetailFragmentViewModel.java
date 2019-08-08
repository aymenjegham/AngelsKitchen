package com.angelstudio.angelskitchen.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.angelstudio.angelskitchen.models.Meal;
import com.angelstudio.angelskitchen.repository.SearchByIdRecipeRepository;

import java.util.List;

public class CountryDetailFragmentViewModel extends ViewModel {

    private SearchByIdRecipeRepository mSearchByIdRecipeRepository;
     private boolean mDidRetrieveRecipe;

    public CountryDetailFragmentViewModel() {
        mSearchByIdRecipeRepository = SearchByIdRecipeRepository.getInstance();
        mDidRetrieveRecipe = false;

    }

    public LiveData<List<Meal>> getRecipeID(){
        return mSearchByIdRecipeRepository.getRecipes();
    }

    public LiveData<Boolean> isRecipeRequestTimedOut(){
        return mSearchByIdRecipeRepository.isRecipeRequestTimedOut();
    }
    public void getRecipeByid(String id ){

        mSearchByIdRecipeRepository.getRecipebyId(id);
    }



    public void setRetrievedRecipe(boolean retrievedRecipe){
        mDidRetrieveRecipe = retrievedRecipe;
    }

    public boolean didRetrieveRecipe(){
        return mDidRetrieveRecipe;
    }



}
