package com.example.food_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private List<OrderHistory> orderHistoryList;
    private DatabaseReference ordersRef;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.historyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        orderHistoryList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(orderHistoryList);
        recyclerView.setAdapter(historyAdapter);

        // Get the user's UID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get a reference to the "orders" node in Firebase Realtime Database
        ordersRef = FirebaseDatabase.getInstance("https://bitemate-c316c-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("orders");

        // Retrieve the order history based on the user's UID
        Query userOrdersQuery = ordersRef.orderByChild("userId").equalTo(userId);
        userOrdersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderHistoryList.clear();
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    OrderHistory order = orderSnapshot.getValue(OrderHistory.class);
                    if (order != null && order.getCartItems() != null) {
                        orderHistoryList.add(order);
                    }
                }
                historyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });

        return view;
    }
}
