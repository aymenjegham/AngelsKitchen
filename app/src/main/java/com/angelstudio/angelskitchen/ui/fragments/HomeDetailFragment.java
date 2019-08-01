package com.angelstudio.angelskitchen.ui.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.angelstudio.angelskitchen.R;


public class HomeDetailFragment extends Fragment {


    public HomeDetailFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home_detail, container, false);
    }

}
