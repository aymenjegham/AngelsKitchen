package com.angelstudio.angelskitchen.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.angelstudio.angelskitchen.models.Category;
import com.angelstudio.angelskitchen.models.Recipe;
import com.angelstudio.angelskitchen.repository.ByAreaRecipesRepository;
import com.angelstudio.angelskitchen.repository.ByCategoryRecipesRepository;

import java.util.List;

public class CategoryFragmentViewModel extends ViewModel {

    private ByCategoryRecipesRepository mByCategoryRecipesRepository;
    private boolean mDidRetrieveRecipe;

    public CategoryFragmentViewModel() {
        mByCategoryRecipesRepository = ByCategoryRecipesRepository.getInstance();
        mDidRetrieveRecipe = false;

    }

    public LiveData<List<Recipe>> getRecipeByCat(){
        return mByCategoryRecipesRepository.getRecipesbycat();
    }

    public LiveData<Boolean> isRecipeRequestTimedOut(){
        return mByCategoryRecipesRepository.isRecipeRequestTimedOut();
    }
    public void getRecipebyCategory(String category){

        mByCategoryRecipesRepository.getRecipebyCategory(category);
    }




    public void setRetrievedRecipe(boolean retrievedRecipe){
        mDidRetrieveRecipe = retrievedRecipe;
    }

    public boolean didRetrieveRecipe(){
        return mDidRetrieveRecipe;
    }
}
