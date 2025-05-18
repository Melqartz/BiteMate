package com.example.food_app;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MenuFragment extends Fragment {
    private Button btnBurger;
    private Button btnDrinks;
    private Button btnSides;
    private Button btnOffers;
    private FragmentManager childFragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        childFragmentManager = getChildFragmentManager();

        btnBurger = view.findViewById(R.id.btnBurger);
        btnDrinks = view.findViewById(R.id.btnDrinks);
        btnSides = view.findViewById(R.id.btnSides);
        btnOffers = view.findViewById(R.id.btnOffers);


        btnBurger.setSelected(true);
        btnDrinks.setSelected(false);
        btnSides.setSelected(false);
        btnOffers.setSelected(false);

//        btnBurger.setBackgroundColor(Color.BLACK);
        setupButtonOnClickListeners();

        replaceFragment(FoodFragment.newInstance("burger"));

        return view;
    }

    private void setupButtonOnClickListeners() {
        btnBurger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectButton(btnBurger);
                replaceFragment(FoodFragment.newInstance("burger"));
            }
        });

        btnDrinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectButton(btnDrinks);
                replaceFragment(FoodFragment.newInstance("drink"));
            }
        });

        btnSides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectButton(btnSides);
                replaceFragment(FoodFragment.newInstance("appetizers"));
            }
        });

        btnOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectButton(btnOffers);
                replaceFragment(FoodFragment.newInstance("offer"));
            }
        });
    }

    private void selectButton(Button button) {


        btnBurger.setSelected(false);
        btnDrinks.setSelected(false);
        btnSides.setSelected(false);
        btnOffers.setSelected(false);
        button.setSelected(true);
//        btnBurger.setBackgroundResource(R.drawable.btn_unselected);
//        btnDrinks.setBackgroundResource(R.drawable.btn_unselected);
//        btnSides.setBackgroundResource(R.drawable.btn_unselected);
//        btnOffers.setBackgroundResource(R.drawable.btn_unselected);
//        button.setBackgroundResource(R.drawable.btn_selected);
    }

    private void replaceFragment(Fragment fragment) {
        childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}
