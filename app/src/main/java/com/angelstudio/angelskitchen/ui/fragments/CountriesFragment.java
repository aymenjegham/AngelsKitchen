package com.angelstudio.angelskitchen.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.angelstudio.angelskitchen.R;
import com.angelstudio.angelskitchen.databinding.FragmentCountriesBinding;


public class CountriesFragment extends Fragment {


    public CountriesFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FragmentCountriesBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_countries,container,false);
        View view = binding.getRoot();

        MyClickHandlers handlers = new MyClickHandlers();
        binding.setHandlers(handlers);

        initAnimation(getContext(),view);

        return view;
    }

    private void initAnimation(Context context,View view) {
        Animation RightSwipe = AnimationUtils.loadAnimation(context, R.anim.right_swipe);
        Animation LeftSwipe = AnimationUtils.loadAnimation(context, R.anim.left_swipe);

//right swipe
        CardView usa = view.findViewById(R.id.Card_usa);
        usa.startAnimation(RightSwipe);

        CardView fr = view.findViewById(R.id.Card_fr);
        fr.startAnimation(RightSwipe);

        CardView jam = view.findViewById(R.id.Card_jam);
        jam.startAnimation(RightSwipe);

        CardView dutch = view.findViewById(R.id.Card_dutch);
        dutch.startAnimation(RightSwipe);

        CardView gr = view.findViewById(R.id.Card_gr);
        gr.startAnimation(RightSwipe);

        CardView irl = view.findViewById(R.id.Card_irl);
        irl.startAnimation(RightSwipe);

        CardView jap = view.findViewById(R.id.Card_jap);
        jap.startAnimation(RightSwipe);

        CardView mal = view.findViewById(R.id.Card_mal);
        mal.startAnimation(RightSwipe);

        CardView mo = view.findViewById(R.id.Card_mor);
        mo.startAnimation(RightSwipe);

        CardView nor = view.findViewById(R.id.Card_nor);
        nor.startAnimation(RightSwipe);

        CardView ru = view.findViewById(R.id.Card_ru);
        ru.startAnimation(RightSwipe);

        CardView sp = view.findViewById(R.id.Card_sp);
        sp.startAnimation(RightSwipe);

        CardView thai = view.findViewById(R.id.Card_thai);
        thai.startAnimation(RightSwipe);

        CardView tr = view.findViewById(R.id.Card_tr);
        tr.startAnimation(RightSwipe);

        CardView alg = view.findViewById(R.id.Card_alg);
        alg.startAnimation(RightSwipe);




//left swipe
        CardView uk = view.findViewById(R.id.Card_uk);
        uk.startAnimation(LeftSwipe);

        CardView ca = view.findViewById(R.id.Card_ca);
        ca.startAnimation(LeftSwipe);

        CardView chn = view.findViewById(R.id.Card_ch);
        chn.startAnimation(LeftSwipe);

        CardView egy = view.findViewById(R.id.Card_egy);
        egy.startAnimation(LeftSwipe);

        CardView in = view.findViewById(R.id.Card_in);
        in.startAnimation(LeftSwipe);

        CardView it = view.findViewById(R.id.Card_it);
        it.startAnimation(LeftSwipe);

        CardView ken = view.findViewById(R.id.Card_ken);
        ken.startAnimation(LeftSwipe);

        CardView mex = view.findViewById(R.id.Card_mex);
        mex.startAnimation(LeftSwipe);

        CardView cr = view.findViewById(R.id.Card_cr);
        cr.startAnimation(LeftSwipe);

        CardView por = view.findViewById(R.id.Card_por);
        por.startAnimation(LeftSwipe);

        CardView ar = view.findViewById(R.id.Card_arg);
        ar.startAnimation(LeftSwipe);

        CardView sl = view.findViewById(R.id.Card_sl);
        sl.startAnimation(LeftSwipe);

        CardView viet = view.findViewById(R.id.Card_viet);
        viet.startAnimation(LeftSwipe);

        CardView sr = view.findViewById(R.id.Card_syr);
        sr.startAnimation(LeftSwipe);

        CardView tn = view.findViewById(R.id.Card_tun);
        tn.startAnimation(LeftSwipe);

    }

    public class MyClickHandlers {

        public void onClicked(View view) {
            int id = view.getId();
            String country = null;

            if (id == R.id.Card_ca) {
                country = "canadian";
            } else if (id == R.id.Card_uk) {
                country = "british";
            } else if (id == R.id.Card_usa) {
                country = "american";
            } else if (id == R.id.Card_jam) {
                country = "jamaican";
            } else if (id == R.id.Card_ch) {
                country = "chinese";
            } else if (id == R.id.Card_dutch) {
                country = "dutch";
            } else if (id == R.id.Card_egy) {
                country = "egyptian";
            } else if (id == R.id.Card_fr) {
                country = "french";
            } else if (id == R.id.Card_gr) {
                country = "greek";
            } else if (id == R.id.Card_in) {
                country = "indian";
            } else if (id == R.id.Card_irl) {
                country = "irish";
            } else if (id == R.id.Card_it) {
                country = "italian";
            } else if (id == R.id.Card_jap) {
                country = "japanese";
            } else if (id == R.id.Card_ken) {
                country = "kenyan";
            } else if (id == R.id.Card_mal) {
                country = "malaysian";
            } else if (id == R.id.Card_mex) {
                country = "mexican";
            } else if (id == R.id.Card_mor) {
                country = "moroccan";
            } else if (id == R.id.Card_cr) {
                country = "croatian";
            } else if (id == R.id.Card_nor) {
                country = "norwegian";
            } else if (id == R.id.Card_por) {
                country = "portuguese";
            } else if (id == R.id.Card_ru) {
                country = "russian";
            } else if (id == R.id.Card_arg) {
                country = "argentinian";
            } else if (id == R.id.Card_sp) {
                country = "spanish";
            } else if (id == R.id.Card_sl) {
                country = "slovakian";
            } else if (id == R.id.Card_thai) {
                country = "thai";
            } else if (id == R.id.Card_viet) {
                country = "vietnamese";
            } else if (id == R.id.Card_tr) {
                country = "turkish";
            } else if (id == R.id.Card_syr) {
                country = "syrian";
            } else if (id == R.id.Card_alg) {
                country = "algerian";
            } else if (id == R.id.Card_tun) {
                country = "tunisian";
            }

            if (country != null) {
                Bundle bundle = new Bundle();
                bundle.putString("COUNTRY", country);
                Navigation.findNavController(view).navigate(
                        R.id.action_countriesFragment_to_countryFragment, bundle);
            }
        }
    }



}


