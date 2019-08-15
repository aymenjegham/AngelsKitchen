package com.angelstudio.angelskitchen.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.angelstudio.angelskitchen.R;
import com.angelstudio.angelskitchen.adapters.RecyclerViewClickListener;
import com.angelstudio.angelskitchen.models.Recipe;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


public class RecipetHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView recipeImageView;
    private TextView title;
    private RecyclerViewClickListener mListener;
    private String  id;




    public RecipetHolder(View recipeCard,RecyclerViewClickListener listener) {
        super(recipeCard);
        this.recipeImageView=itemView.findViewById(R.id.recipeIMG);
        this.title=itemView.findViewById(R.id.categoryTitle);

        mListener=listener;



    }

    public void updateUI(Recipe recipe){
        id=recipe.getIdMeal();

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.angel_1);

        String urlpath =recipe.getStrMealThumb();

        Glide.with(recipeImageView.getContext())
                .setDefaultRequestOptions(requestOptions)
                .load(urlpath)
                .into(recipeImageView);

        title.setText(recipe.getStrMeal());
        recipeImageView.setOnClickListener(this);



    }


    @Override
    public void onClick(View v) {
        mListener.onClick(v, getAdapterPosition(),id);

    }
}

