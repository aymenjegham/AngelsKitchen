package com.angelstudio.angelskitchen.ui.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.angelstudio.angelskitchen.R;


public class CountryFragment extends Fragment {


    public CountryFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_country, container, false);
        TextView tv = view.findViewById(R.id.textViewAmount);
        tv.setText(getArguments().getString("COUNTRY"));

        return view;
    }

}
