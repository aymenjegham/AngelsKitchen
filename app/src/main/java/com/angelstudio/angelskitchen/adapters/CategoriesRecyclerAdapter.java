package com.angelstudio.angelskitchen.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.angelstudio.angelskitchen.R;
import com.angelstudio.angelskitchen.holders.CategoryHolder;
 import com.angelstudio.angelskitchen.models.Category;

import java.util.List;


public class CategoriesRecyclerAdapter extends RecyclerView.Adapter<CategoryHolder> {


    private List<Category> categories;
    private RecyclerViewClickListener mListener;




    public CategoriesRecyclerAdapter(RecyclerViewClickListener listener) {
        mListener = listener;
     }

    public void setCategories(List<Category> categories) {

        this.categories=categories;
        notifyDataSetChanged();

    }

    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View CategoryCard= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_item,viewGroup,false);
        return new CategoryHolder(CategoryCard,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        final Category category =categories.get(position);
        holder.updateUI(category);
    }

    @Override
    public int getItemCount() {
        if(categories != null){

            return categories.size();
        }
        return 0;
    }




}















