package com.example.food_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RewardsFragment extends Fragment {

    private TextView pointsTextView;
    private Button redeemBurgerButton;
    private Button redeemFriesButton;
    private Button redeemDrinkButton;
    private GlobalVariable glob;

    private DatabaseReference userRef;
    DatabaseHelper dbHelper;
    private int userPoints;

    public RewardsFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rewards, container, false);

        dbHelper=new DatabaseHelper(this.getContext());
        glob= new GlobalVariable();

        pointsTextView = view.findViewById(R.id.currentPoints);
        redeemBurgerButton = view.findViewById(R.id.redeemBurgerButton);
        redeemFriesButton = view.findViewById(R.id.redeemFriesButton);
        redeemDrinkButton = view.findViewById(R.id.redeemDrinkButton);

        userRef = FirebaseDatabase.getInstance("https://bitemate-c316c-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        loadUserPoints();

        redeemBurgerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redeemReward("American Burger (Reward)", 100);
            }
        });

        redeemFriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redeemReward("French Fries (Reward)", 50);
            }
        });

        redeemDrinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redeemReward("Pepsi (Reward)", 30);
            }
        });

        return view;
    }

    private void loadUserPoints() {
        userRef.child("points").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userPoints = dataSnapshot.getValue(Integer.class);
                    pointsTextView.setText(String.valueOf(userPoints));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void redeemReward(String rewardName, int rewardPoints) {
        if (userPoints >= rewardPoints) {
            int remainingPoints = userPoints - rewardPoints;
            userRef.child("points").setValue(remainingPoints)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Reward redeemed: " + rewardName, Toast.LENGTH_SHORT).show();
                            dbHelper.insertCartItem(glob.currentUserId, rewardName, 0);
                            pointsTextView.setText(String.valueOf(remainingPoints));
                        } else {
                            Toast.makeText(getActivity(), "Failed to redeem reward", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "Insufficient points", Toast.LENGTH_SHORT).show();
        }
    }
}


