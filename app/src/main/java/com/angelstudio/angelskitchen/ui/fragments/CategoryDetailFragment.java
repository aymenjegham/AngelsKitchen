package com.angelstudio.angelskitchen.ui.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
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
import android.widget.TextView;

import com.angelstudio.angelskitchen.R;
import com.angelstudio.angelskitchen.adapters.IngredientsRecyclerAdapter;
import com.angelstudio.angelskitchen.models.Ingredient;
import com.angelstudio.angelskitchen.models.Meal;
import com.angelstudio.angelskitchen.viewmodels.CountryDetailFragmentViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.List;

public class CategoryDetailFragment extends Fragment {


    public CategoryDetailFragment(){}

    private CountryDetailFragmentViewModel countryDetailFragmentViewModel;
    private TextView recipeTitle,categoryTitle,ingredientTv,instructionsTv,instructionView,videoTv,srcTv,srcLink ;
    private ImageView recipeIMG,categoryIMG,countryIMG;
    private ProgressBar mProgressBar;
    private NestedScrollView mScrollView;
    private String mUrlCategory;
    private IngredientsRecyclerAdapter ingredientsRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<Ingredient> mIngredients= new ArrayList<>();
    private YouTubePlayerView youTubePlayerView2;
    public String id;
    public String url,origin,mealCategory;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_category_detail, container, false);


        recipeIMG=view.findViewById(R.id.recipeIMG);
        categoryIMG=view.findViewById(R.id.categoryIMG);
        countryIMG=view.findViewById(R.id.flagImg);
        recipeTitle=view.findViewById(R.id.recipeTitledt);
        categoryTitle=view.findViewById(R.id.categoryTitle);
        mProgressBar = view.findViewById(R.id.progress_bar);
        mScrollView = view.findViewById(R.id.recyclerview_categories);
        ingredientTv=view.findViewById(R.id.ingreditTv);
        instructionsTv=view.findViewById(R.id.instructionsTv);
        instructionView=view.findViewById(R.id.instructionView);
        mRecyclerView=view.findViewById(R.id.ingredient_recyclerView);
        videoTv=view.findViewById(R.id.videoTv);
        youTubePlayerView2 =view.findViewById(R.id.youtube_player_view2);
        youTubePlayerView2.setVisibility(View.INVISIBLE);
        getLifecycle().addObserver( youTubePlayerView2);
        srcLink=view.findViewById(R.id.srcLink);
        srcTv=view.findViewById(R.id.srcTv);
        if (getArguments() != null) {
            id=getArguments().getString("ID");
        }else{
        }

        countryDetailFragmentViewModel = ViewModelProviders.of(this).get(CountryDetailFragmentViewModel.class);
        mScrollView.smoothScrollTo(0,0);
        mProgressBar.bringToFront();
        recipeIMG.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        countryDetailFragmentViewModel.getRecipeByid(id);
        initRecyclerView();
        subscribeObservers();

        return view;
    }

    private void initRecyclerView() {
        ingredientsRecyclerAdapter = new IngredientsRecyclerAdapter();
        mRecyclerView.setAdapter(ingredientsRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(),RecyclerView.HORIZONTAL,false));
    }

    private void subscribeObservers(){
        countryDetailFragmentViewModel.getRecipeID().observe(getViewLifecycleOwner(), new Observer<List<Meal>>() {

            @Override
            public void onChanged(List<Meal> meals) {
                if(meals != null){
                    setRecipeProperties(meals.get(0));
                    countryDetailFragmentViewModel.setRetrievedRecipe(true);




                }
            }



        });

        countryDetailFragmentViewModel.isRecipeRequestTimedOut().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean && !countryDetailFragmentViewModel.didRetrieveRecipe()){
                    Log.d("changed", "onChanged: timed out..");

                }
            }
        });
    }

    private void setRecipeProperties(Meal meal){
        if(meal != null){
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.angel_2);


            if(meal.getStrCategory().equals("Goat")){
                countryIMG.setImageResource(R.drawable.ic_category);
            }else if(meal.getStrCategory().equals("Breakfast")){
                countryIMG.setImageResource(R.drawable.ic_recipe2);
            }else{
                mUrlCategory="https://www.themealdb.com/images/category/"+meal.getStrCategory()+".png";
            }
            String mCountry = (meal.getStrArea()).toLowerCase();
            int iconResId = getResources().getIdentifier(mCountry, "drawable",this.getContext().getPackageName());


            recipeIMG.setVisibility(View.VISIBLE);
            recipeIMG.setClipToOutline(true);
            Glide.with(this)
                    .setDefaultRequestOptions(requestOptions)
                    .load(meal.getStrMealThumb())
                    .into(recipeIMG);

            Glide.with(this)
                    .setDefaultRequestOptions(requestOptions)
                    .load(mUrlCategory)
                    .into(categoryIMG);


            countryIMG.setImageResource(iconResId);

            mealCategory=meal.getStrCategory();

            instructionsTv.setText("Instructions");
            ingredientTv.setText("Ingredients");
            videoTv.setText("Video");
            srcTv.setText("Source");
            srcLink.setText(meal.getStrSource());
            recipeTitle.setText(meal.getStrMeal());
            categoryTitle.setText(mealCategory);
            instructionView.setText(meal.getStrInstructions());

            mIngredients.clear();
            ingredientsRecyclerAdapter.setIngredient(meal.getIngredients());


            String s = meal.getStrYoutube();
            url = s.split("v=")[1];
            origin=meal.getStrArea();



            youTubePlayerView2.setVisibility(View.VISIBLE);
            youTubePlayerView2.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {


                    youTubePlayer.loadVideo(url, 0);
                    youTubePlayer.pause();
                }
            });

            countryIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("COUNTRY", origin);
                    Navigation.findNavController(v).navigate(R.id.action_categoryDetailFragment_to_countryFragment,bundle);
                }
            });

            categoryIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("ID", mealCategory);
                    Navigation.findNavController(v).navigate(R.id.action_categoryDetailFragment_to_categoryFragment,bundle);
                }
            });

            categoryTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("ID", mealCategory);
                    Navigation.findNavController(v).navigate(R.id.action_categoryDetailFragment_to_categoryFragment,bundle);
                }
            });

        }

        showParent();
        mProgressBar.setVisibility(View.INVISIBLE);
    }
    private void showParent(){
        mScrollView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        youTubePlayerView2.release();
    }

}
