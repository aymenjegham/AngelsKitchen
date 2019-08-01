package com.angelstudio.angelskitchen.ui.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angelstudio.angelskitchen.R;
import com.angelstudio.angelskitchen.models.Meal;
import com.angelstudio.angelskitchen.models.Meals;
import com.angelstudio.angelskitchen.viewmodels.HomeFragmentViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;


public class HomeFragment extends Fragment {


    public HomeFragment() {
    }

    private HomeFragmentViewModel homeFragmentViewModel;

    private TextView recipeTitle ;
    private ImageView recipeIMG;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_home, container, false);

       recipeIMG=view.findViewById(R.id.recipeIMG);
        recipeTitle=view.findViewById(R.id.recipeTitle);



        homeFragmentViewModel = ViewModelProviders.of(this).get(HomeFragmentViewModel.class);
        homeFragmentViewModel.getRandomRecipe();
        subscribeObservers();


        return view;
    }

    private void subscribeObservers(){
        homeFragmentViewModel.getRecipes().observe(this, new Observer<List<Meal>>() {

            @Override
            public void onChanged(List<Meal> meals) {
                if(meals != null){
                    setRecipeProperties(meals.get(0));
                    homeFragmentViewModel.setRetrievedRecipe(true);



                }
            }


        });

        homeFragmentViewModel.isRecipeRequestTimedOut().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean && !homeFragmentViewModel.didRetrieveRecipe()){
                    Log.d("changed", "onChanged: timed out..");
                    //displayErrorScreen("Error retrieving data. Check network connection.");
                }
            }
        });
    }

    private void setRecipeProperties(Meal meal){
        if(meal != null){
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background);

            Glide.with(this)
                    .setDefaultRequestOptions(requestOptions)
                    .load(meal.getStrMealThumb())
                    .into(recipeIMG);

            recipeTitle.setText(meal.getStrMeal());

           // mRecipeIngredientsContainer.removeAllViews();

        }

       // showParent();
        //showProgressBar(false);
    }



}
