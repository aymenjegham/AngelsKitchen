package com.angelstudio.angelskitchen.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.angelstudio.angelskitchen.R;
import com.angelstudio.angelskitchen.holders.IngredientHolder;
import com.angelstudio.angelskitchen.models.Ingredient;
import java.util.ArrayList;
import java.util.Arrays;


public class IngredientsRecyclerAdapter extends RecyclerView.Adapter<IngredientHolder> {


    private ArrayList<Ingredient> mIngredient;
    private ArrayList<Ingredient> mIngredientfinal=new ArrayList<>();



    public IngredientsRecyclerAdapter() {

    }

    public void setIngredient(ArrayList<Ingredient> mIngredient) {


        mIngredientfinal.clear();
        for(int i=0;i<mIngredient.size();i++){

            if( mIngredient.get(i).getIngred() != null){
                if(!mIngredient.get(i).getIngred().isEmpty() ){
                    mIngredientfinal.add(mIngredient.get(i));

                }


            }
        }

        this.mIngredient = mIngredientfinal;
        notifyDataSetChanged();
        mIngredient.clear();

    }

    @Override
    public IngredientHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View IngredientCard= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ingredient_item,viewGroup,false);
        return new IngredientHolder(IngredientCard);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientHolder holder, int position) {
        final Ingredient ingredient =mIngredient.get(position);
        holder.updateUI(ingredient);

    }

    @Override
    public int getItemCount() {
        if(mIngredient != null){

            return mIngredient.size();
        }
        return 0;
    }
}















