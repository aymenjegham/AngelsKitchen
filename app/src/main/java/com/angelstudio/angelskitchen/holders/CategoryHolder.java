package com.angelstudio.angelskitchen.holders;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.angelstudio.angelskitchen.R;
import com.angelstudio.angelskitchen.adapters.RecyclerViewClickListener;
import com.angelstudio.angelskitchen.models.Category;
import com.angelstudio.angelskitchen.models.Recipe;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


public class CategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView categoryImageView;
    private TextView title;
    private RecyclerViewClickListener mListener;
    private String  id;
    private CardView cardview;




    public CategoryHolder(View categoryCard, RecyclerViewClickListener listener) {
        super(categoryCard);
        this.categoryImageView=itemView.findViewById(R.id.category_image);
        this.title=itemView.findViewById(R.id.categoryTitle);
        this.cardview=itemView.findViewById(R.id.cardcategory);

        mListener=listener;




    }

    public void updateUI(Category category){
        id=category.getStrCategory();

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.angel_1);

        String urlpath =category.getStrCategoryThumb();

        Glide.with(categoryImageView.getContext())
                .setDefaultRequestOptions(requestOptions)
                .load(urlpath)
                .into(categoryImageView);

        title.setText(category.getStrCategory());
        cardview.setOnClickListener(this);




    }


    @Override
    public void onClick(View v) {

        mListener.onClick(v, getAdapterPosition(),id);

    }
}

