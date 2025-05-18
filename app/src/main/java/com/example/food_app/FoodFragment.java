package com.example.food_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FoodFragment extends Fragment {

    private RecyclerView recyclerView;
    private FoodAdapter adapter;
    private DatabaseHelper dbHelper;
    private String category;

    public static FoodFragment newInstance(String category) {
        FoodFragment fragment = new FoodFragment();
        Bundle args = new Bundle();
        args.putString("category", category);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString("category");
        }

        FirebaseApp.initializeApp(getContext());
    }




    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food, container, false);

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Create an instance of DatabaseHelper
        dbHelper = new DatabaseHelper(getActivity());

        // Create a reference to the "foods" node in the Firebase database
        DatabaseReference foodsRef = FirebaseDatabase.getInstance("https://bitemate-c316c-default-rtdb.europe-west1.firebasedatabase.app/").getReference("foods");

//        // Query the database for foods in the selected category
        foodsRef.orderByChild("category").equalTo(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Food> foods = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.d("FoodEntry", dataSnapshot.getValue().toString());
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    Double price = dataSnapshot.child("price").getValue(Double.class);
                    String category = dataSnapshot.child("category").getValue(String.class);
                    String path = dataSnapshot.child("location").getValue(String.class);
                    if (name != null && price != null && category != null && path != null) {
                        Food food = new Food(name, price, category, path);
                        foods.add(food);
                    }
                }
                //adapter = new FoodAdapter(getActivity(), foods, dbHelper);
                adapter = new FoodAdapter(getActivity(), foods,dbHelper);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.e("FirebaseError", "Read failed: " + error.getMessage());
            }
        });

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Close the database connection
        if (dbHelper != null) {
            dbHelper.close();
        }
    }


}
