package com.angelstudio.angelskitchen.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

 import com.angelstudio.angelskitchen.models.Recipe;
import com.angelstudio.angelskitchen.repository.ByAreaRecipesRepository;

import java.util.List;

public class CountryFragmentViewModel  extends ViewModel {

    private ByAreaRecipesRepository mbyAreaRecipesRepository;
    private boolean mDidRetrieveRecipe;

    public CountryFragmentViewModel() {
        mbyAreaRecipesRepository = ByAreaRecipesRepository.getInstance();
        mDidRetrieveRecipe = false;

    }

    public LiveData<List<Recipe>> getRecipes(){
        return mbyAreaRecipesRepository.getRecipes();
    }

    public LiveData<Boolean> isRecipeRequestTimedOut(){
        return mbyAreaRecipesRepository.isRecipeRequestTimedOut();
    }
    public void getRecipebyCountry(String area){

        mbyAreaRecipesRepository.getRecipebyCountry(area);
    }




    public void setRetrievedRecipe(boolean retrievedRecipe){
        mDidRetrieveRecipe = retrievedRecipe;
    }

    public boolean didRetrieveRecipe(){
        return mDidRetrieveRecipe;
    }
}
