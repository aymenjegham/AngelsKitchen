package com.angelstudio.angelskitchen.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.angelstudio.angelskitchen.R;
import com.angelstudio.angelskitchen.holders.RecipetHolder;
import com.angelstudio.angelskitchen.models.Recipe;

import java.util.List;


public class RecipesRecyclerAdapter extends RecyclerView.Adapter<RecipetHolder> {


    private List<Recipe> recipes;
    private RecyclerViewClickListener mListener;




    public RecipesRecyclerAdapter(RecyclerViewClickListener listener) {
        mListener = listener;
     }

    public void setRecipes(List<Recipe> recipes) {

        this.recipes=recipes;
        notifyDataSetChanged();

    }

    @Override
    public RecipetHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View RecipeCard= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recipe_item,viewGroup,false);
        return new RecipetHolder(RecipeCard,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipetHolder holder, int position) {
        final Recipe recipe =recipes.get(position);
        holder.updateUI(recipe);

    }


    @Override
    public int getItemCount() {
        if(recipes != null){

            return recipes.size();
        }
        return 0;
    }




}















