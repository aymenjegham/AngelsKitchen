package com.angelstudio.angelskitchen.ui.fragments;


 import android.app.Dialog;
 import android.graphics.Color;
 import android.graphics.drawable.ColorDrawable;
 import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
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
 import android.widget.Button;
 import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.angelstudio.angelskitchen.R;
import com.angelstudio.angelskitchen.adapters.IngredientsRecyclerAdapter;
import com.angelstudio.angelskitchen.models.Ingredient;
import com.angelstudio.angelskitchen.models.Meal;
import com.angelstudio.angelskitchen.viewmodels.HomeFragmentViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.List;

import static com.angelstudio.angelskitchen.R.layout.fragment_home;


public class HomeFragment extends Fragment {


    public HomeFragment() {
    }

    private HomeFragmentViewModel homeFragmentViewModel;
    private TextView recipeTitle,categoryTitle,ingredientTv,instructionsTv,instructionView,videoTv,srcTv,srcLink ;
    private ImageView recipeIMG,categoryIMG,countryIMG;
    private ProgressBar mProgressBar;
    private NestedScrollView mScrollView;
    private String mUrlCategory;
    private IngredientsRecyclerAdapter ingredientsRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<Ingredient> mIngredients= new ArrayList<>();
    private YouTubePlayerView youTubePlayerView;
    private String url,origin,mealCatrgory;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                final Dialog dialog = new Dialog(getContext());

                dialog.setContentView(R.layout.custom_dlg);
               Button btnYes  =  dialog.findViewById(R.id.button_yes);
                Button btnNo  =  dialog.findViewById(R.id.button_no);
                Button btrate  =  dialog.findViewById(R.id.button_rate);


                dialog.setTitle("Custom Alert Dialog");
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().finish();
                    }
                });

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });


            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(fragment_home, container, false);

       recipeIMG=view.findViewById(R.id.recipeIMG);
       categoryIMG=view.findViewById(R.id.categoryIMG);
       countryIMG=view.findViewById(R.id.flagImg);
       recipeTitle=view.findViewById(R.id.recipetitle);
       categoryTitle=view.findViewById(R.id.categoryTitle);
       mProgressBar = view.findViewById(R.id.progress_bar);
       mScrollView = view.findViewById(R.id.recyclerview_categories);
       ingredientTv=view.findViewById(R.id.ingreditTv);
       instructionsTv=view.findViewById(R.id.instructionsTv);
       instructionView=view.findViewById(R.id.instructionView);
       mRecyclerView=view.findViewById(R.id.ingredient_recyclerView);
       videoTv=view.findViewById(R.id.videoTv);
       youTubePlayerView =view.findViewById(R.id.youtube_player_view);
       youTubePlayerView.setVisibility(View.INVISIBLE);
       getLifecycle().addObserver( youTubePlayerView);
       srcLink=view.findViewById(R.id.srcLink);
       srcTv=view.findViewById(R.id.srcTv);


        homeFragmentViewModel = ViewModelProviders.of(this).get(HomeFragmentViewModel.class);
        mScrollView.smoothScrollTo(0,0);
        mProgressBar.bringToFront();
        recipeIMG.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        homeFragmentViewModel.getRandomRecipe();
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

    private void setRecipeProperties(final Meal meal){
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
            mealCatrgory=meal.getStrCategory();


            instructionsTv.setText("Instructions");
            ingredientTv.setText("Ingredients");
            videoTv.setText("Video");
            srcTv.setText("Source");
            srcLink.setText(meal.getStrSource());
            recipeTitle.setText(meal.getStrMeal());
            categoryTitle.setText(mealCatrgory);
            instructionView.setText(meal.getStrInstructions());

            mIngredients.clear();
            ingredientsRecyclerAdapter.setIngredient(meal.getIngredients());

            String s = meal.getStrYoutube();
            url = s.split("v=")[1];
            origin=meal.getStrArea();


            youTubePlayerView.setVisibility(View.VISIBLE);
            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
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
                    Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_countryFragment,bundle);
                }
            });

            categoryIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("ID", mealCatrgory);
                    Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_categoryFragment,bundle);
                }
            });

            categoryTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("ID", mealCatrgory);
                    Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_categoryFragment,bundle);
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
     }


}




