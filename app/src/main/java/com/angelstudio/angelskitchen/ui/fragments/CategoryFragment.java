package com.angelstudio.angelskitchen.ui.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
 import android.widget.ProgressBar;

import com.angelstudio.angelskitchen.R;
import com.angelstudio.angelskitchen.adapters.RecipesRecyclerAdapter;
import com.angelstudio.angelskitchen.adapters.RecyclerViewClickListener;
 import com.angelstudio.angelskitchen.models.Recipe;
import com.angelstudio.angelskitchen.viewmodels.CategoryFragmentViewModel;

import java.util.List;


public class CategoryFragment extends Fragment {


    public CategoryFragment() {}

    private CategoryFragmentViewModel mCategoryFragmentViewModel;
    private RecyclerView mRecyclerView;
    private RecipesRecyclerAdapter recipesRecyclerAdapter;
    private ProgressBar mProgressBar;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_category, container, false);

        mCategoryFragmentViewModel = ViewModelProviders.of(this).get(CategoryFragmentViewModel.class);


        mRecyclerView=view.findViewById(R.id.recyclerview_recipes_category);
        mProgressBar = view.findViewById(R.id.progress_bar);


        mProgressBar.bringToFront();
        mProgressBar.setVisibility(View.VISIBLE);

        if (getArguments() != null) {

            String id=getArguments().getString("ID");
            mCategoryFragmentViewModel.getRecipebyCategory(id);


        }else{
            //displayErrorScreen("Error retrieving data. Check network connection.");
        }

        initRecyclerView();
        subscribeObservers();
        return view;
    }

    private void subscribeObservers(){
        mCategoryFragmentViewModel.getRecipeByCat().observe(this, new Observer<List<Recipe>>() {

            @Override
            public void onChanged(List<Recipe> recipes) {
                if(recipes != null){
                    setRecipesProperties(recipes);
                    mCategoryFragmentViewModel.setRetrievedRecipe(true);

                }else{
                    initRecyclerView();
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        mCategoryFragmentViewModel.isRecipeRequestTimedOut().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean && !mCategoryFragmentViewModel.didRetrieveRecipe()){
                    Log.d("changed", "onChanged: timed out..");
                    //displayErrorScreen("Error retrieving data. Check network connection.");
                }
            }
        });
    }

    private void setRecipesProperties(List<Recipe> recipes) {
        if(recipes != null){
            recipesRecyclerAdapter.setRecipes(recipes);
        }
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void initRecyclerView() {
        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position, String id) {
                Bundle bundle = new Bundle();
                bundle.putString("ID", id);
                Navigation.findNavController(view).navigate(R.id.action_categoryFragment_to_categoryDetailFragment,bundle);


            }


        };
        recipesRecyclerAdapter = new RecipesRecyclerAdapter(listener);
        mRecyclerView.setAdapter(recipesRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL,false));
    }

}
