package com.example.food_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.food_app.ui.AccountFragment;
import com.google.android.material.button.MaterialButton;

public class HomeFragment extends Fragment {
    private ViewPager2 imageSlider;
    private LinearLayout indicatorLayout;
    private ImageSliderAdapter imageSliderAdapter;
    private ImageView[] indicators;
    private int[] imageList = {R.drawable.burgerback2, R.drawable.girleating ,R.drawable.backgrounddesign,R.drawable.chef};

    GlobalVariable glob;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        glob = new GlobalVariable();
        // Initialize views
        imageSlider = view.findViewById(R.id.imageSlider);
        indicatorLayout = view.findViewById(R.id.indicatorLayout);

        // Set up image slider
        imageSliderAdapter = new ImageSliderAdapter(getActivity(), imageList);
        imageSlider.setAdapter(imageSliderAdapter);

        imageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateIndicators(position);
            }
        });

        // Create indicators
        createIndicators();

        // Set click listener on menu ImageView
        MaterialButton menuImageView = view.findViewById(R.id.menuCard);
        menuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment("menuCard");
            }
        });

        // Set click listener on menu ImageView
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) MaterialButton menuImageView1 = view.findViewById(R.id.rewardsCard);
        menuImageView1.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick (View v){
                openFragment("rewardsCard");
            }

        });

        // Set click listener on menu ImageView
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) MaterialButton menuImageView2 = view.findViewById(R.id.orderCard);
        menuImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment("orderCard");
            }
        });

        // Set click listener on menu ImageView
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) MaterialButton menuImageView3 = view.findViewById(R.id.accountCard);
        menuImageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment("accountCard");
            }
        });

        return view;
    }

    private void createIndicators() {
        indicators = new ImageView[imageList.length];

        for (int i = 0; i < imageList.length; i++) {
            indicators[i] = new ImageView(getActivity());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.indicator_unselected));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(8, 0, 8, 0);
            layoutParams.gravity = Gravity.CENTER_VERTICAL;

            indicatorLayout.addView(indicators[i], layoutParams);
        }
    }

    private void updateIndicators(int position) {
        for (int i = 0; i < imageList.length; i++) {
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getActivity(), i == position ? R.drawable.indicator_selected : R.drawable.indicator_unselected));
        }
    }

    private void openFragment(String frag) {
        Fragment Fragment = null;
        if(frag.equals("menuCard")){
            Fragment= new MenuFragment();
        }else if(frag.equals("rewardsCard")){
            if(glob.isUserSignedIn){
                Fragment=new RewardsFragment();
            }else{
                Toast.makeText(requireContext(), "Please Sign in to Order", Toast.LENGTH_SHORT).show();
            }
        }else if(frag.equals("orderCard")){
            if(glob.isUserSignedIn){
                Fragment=new HistoryFragment();
            }else{
                Toast.makeText(requireContext(), "Please Sign in to Order", Toast.LENGTH_SHORT).show();
            }
        }else if(frag.equals("accountCard")){
            if(glob.isUserSignedIn){
                Fragment=new AccountFragment();
            }else{
                Toast.makeText(requireContext(), "Please Sign in to Order", Toast.LENGTH_SHORT).show();
            }
        }

        if(glob.isUserSignedIn || Fragment instanceof MenuFragment) {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainerView2, Fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }


}
