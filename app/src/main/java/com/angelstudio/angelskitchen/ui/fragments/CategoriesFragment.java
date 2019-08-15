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
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.angelstudio.angelskitchen.R;
import com.angelstudio.angelskitchen.adapters.CategoriesRecyclerAdapter;
import com.angelstudio.angelskitchen.adapters.IngredientsRecyclerAdapter;
import com.angelstudio.angelskitchen.adapters.RecipesRecyclerAdapter;
import com.angelstudio.angelskitchen.adapters.RecyclerViewClickListener;
import com.angelstudio.angelskitchen.models.Category;
import com.angelstudio.angelskitchen.models.Meal;
import com.angelstudio.angelskitchen.viewmodels.CategoriesFragmentViewModel;
import com.angelstudio.angelskitchen.viewmodels.CountryFragmentViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;

import java.util.List;


public class CategoriesFragment extends Fragment {


    public CategoriesFragment(){}

    private CategoriesFragmentViewModel mCategoriesFragmentViewModel;
    private RecyclerView mRecyclerView;
    private CategoriesRecyclerAdapter categoriesRecyclerAdapter;
    private ProgressBar mProgressBar;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_categories, container, false);

        mCategoriesFragmentViewModel = ViewModelProviders.of(this).get(CategoriesFragmentViewModel.class);

        mRecyclerView=view.findViewById(R.id.recyclerview_categories);
        mProgressBar = view.findViewById(R.id.progress_bar);

        mProgressBar.bringToFront();
        mProgressBar.setVisibility(View.VISIBLE);

        mCategoriesFragmentViewModel.getCategories();
        initRecyclerView();
        subscribeObservers();

        return view;
    }

    private void initRecyclerView() {
        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position, String id) {
               Bundle bundle = new Bundle();
                bundle.putString("ID", id);
                Navigation.findNavController(view).navigate(R.id.action_categoriesFragment_to_categoryFragment,bundle);



            }
        };
        categoriesRecyclerAdapter = new CategoriesRecyclerAdapter(listener);
        mRecyclerView.setAdapter(categoriesRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(),RecyclerView.VERTICAL,false));
    }

    private void subscribeObservers(){
        mCategoriesFragmentViewModel.getCategorys().observe(this, new Observer<List<Category>>() {

            @Override
            public void onChanged(List<Category> categories) {
                if(categories != null){
                    setRecipeProperties(categories);
                    mCategoriesFragmentViewModel.setRetrievedRecipe(true);

                }
            }


        });

        mCategoriesFragmentViewModel.isRecipeRequestTimedOut().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean && !mCategoriesFragmentViewModel.didRetrieveRecipe()){
                    Log.d("changed", "onChanged: timed out..");
                    //displayErrorScreen("Error retrieving data. Check network connection.");
                }
            }
        });
    }
    private void setRecipeProperties(final List<Category> category){
        if(category != null){
            categoriesRecyclerAdapter.setCategories(category);
        }


        mProgressBar.setVisibility(View.INVISIBLE);
    }




}
