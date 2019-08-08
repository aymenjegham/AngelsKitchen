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
import com.angelstudio.angelskitchen.adapters.RecipesRecyclerAdapter;
import com.angelstudio.angelskitchen.adapters.RecyclerViewClickListener;
import com.angelstudio.angelskitchen.models.Recipe;
import com.angelstudio.angelskitchen.viewmodels.CountryFragmentViewModel;
import java.util.List;

import static com.angelstudio.angelskitchen.R.layout.fragment_country;


public class CountryFragment extends Fragment {



    public CountryFragment(){}
    private CountryFragmentViewModel mCountryFragmentViewModel;
    private RecyclerView mRecyclerView;
    private RecipesRecyclerAdapter recipesRecyclerAdapter;
    private ProgressBar mProgressBar;
    private ImageView imageView;




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(fragment_country, container, false);

        mCountryFragmentViewModel = ViewModelProviders.of(this).get(CountryFragmentViewModel.class);


        mRecyclerView=view.findViewById(R.id.recyclerview_recipes_area);
        mProgressBar = view.findViewById(R.id.progress_bar);
        imageView=view.findViewById(R.id.imageView);


        mProgressBar.bringToFront();
        mProgressBar.setVisibility(View.VISIBLE);


        if (getArguments() != null) {

            String area=getArguments().getString("COUNTRY");


            mCountryFragmentViewModel.getRecipebyCountry(area);
        }else{
            //displayErrorScreen("Error retrieving data. Check network connection.");
        }
        initRecyclerView();
        subscribeObservers();



        return view;
    }

    private void subscribeObservers(){
        mCountryFragmentViewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {

            @Override
            public void onChanged(List<Recipe> recipes) {
                if(recipes != null){
                    setRecipesProperties(recipes);
                    mCountryFragmentViewModel.setRetrievedRecipe(true);

                }else{
                    initRecyclerView();
                    imageView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        mCountryFragmentViewModel.isRecipeRequestTimedOut().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean && !mCountryFragmentViewModel.didRetrieveRecipe()){
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
        imageView.setVisibility(View.INVISIBLE);



    }

    private void initRecyclerView() {
        RecyclerViewClickListener listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position, String id) {
                Bundle bundle = new Bundle();
                bundle.putString("ID", id);
                Navigation.findNavController(view).navigate(R.id.action_countryFragment_to_countryDetailFragment,bundle);


            }


        };
        recipesRecyclerAdapter = new RecipesRecyclerAdapter(listener);
        mRecyclerView.setAdapter(recipesRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL,false));
    }


}


