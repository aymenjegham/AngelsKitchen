package com.angelstudio.angelskitchen.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.angelstudio.angelskitchen.R;
import com.angelstudio.angelskitchen.models.Ingredient;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


public class IngredientHolder extends RecyclerView.ViewHolder {

    private ImageView ingredientImageView;
    private TextView measure,ingrd;



    public IngredientHolder(View ingredientCard) {
        super(ingredientCard);
        this.ingredientImageView=itemView.findViewById(R.id.ingredientIMG);
        this.measure=itemView.findViewById(R.id.ingredientMeasure);
        this.ingrd=itemView.findViewById(R.id.ingredientTitle);

    }

    public void updateUI(Ingredient ingredient){

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.angel_1);

        String urlpath ="https://www.themealdb.com/images/ingredients/"+ingredient.getIngred()+".png";

        Glide.with(ingredientImageView.getContext())
                .setDefaultRequestOptions(requestOptions)
                .load(urlpath)
                .into(ingredientImageView);

        ingrd.setText(ingredient.getIngred());
        measure.setText(ingredient.getMeasure());

    }
}
