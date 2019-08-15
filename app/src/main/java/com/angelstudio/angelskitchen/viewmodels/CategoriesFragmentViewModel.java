package com.angelstudio.angelskitchen.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.angelstudio.angelskitchen.models.Category;
import com.angelstudio.angelskitchen.models.Recipe;
import com.angelstudio.angelskitchen.repository.ByAreaRecipesRepository;
import com.angelstudio.angelskitchen.repository.CategoriesRepository;

import java.util.List;

public class CategoriesFragmentViewModel extends ViewModel {

    private CategoriesRepository mCategoriesRepository;
    private boolean mDidRetrieveRecipe;

    public CategoriesFragmentViewModel() {
        mCategoriesRepository = CategoriesRepository.getInstance();
        mDidRetrieveRecipe = false;

    }

    public LiveData<List<Category>> getCategorys(){
        return mCategoriesRepository.getCategories();
    }

    public LiveData<Boolean> isRecipeRequestTimedOut(){
        return mCategoriesRepository.isRecipeRequestTimedOut();
    }
    public void getCategories(){

        mCategoriesRepository.getCategorys();
    }




    public void setRetrievedRecipe(boolean retrievedRecipe){
        mDidRetrieveRecipe = retrievedRecipe;
    }

    public boolean didRetrieveRecipe(){
        return mDidRetrieveRecipe;
    }
}
