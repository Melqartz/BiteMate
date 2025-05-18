package com.example.food_app;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_app.models.CartItem;

import java.util.List;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<OrderHistory> orderHistoryList;

    public HistoryAdapter(List<OrderHistory> orderHistoryList) {
        this.orderHistoryList = orderHistoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (orderHistoryList == null || orderHistoryList.isEmpty()) {
            return;
        }

        int reversePosition = orderHistoryList.size() - 1 - position;
        OrderHistory order = orderHistoryList.get(reversePosition);

        // Bind the order data to the ViewHolder views
        holder.orderIdTextView.setText(order.getOrderId());
        holder.dateTextView.setText(order.getDate());
        holder.totalPriceTextView.setText(String.valueOf(order.getTotalPrice()) + "0$");
        holder.addressTextView.setText(String.valueOf(order.getAddress()));

        // Display the cart items for the order
        List<CartItem> cartItems = order.getCartItems();
        StringBuilder cartItemsText = new StringBuilder();
        for (CartItem item : cartItems) {
            cartItemsText.append(item.getName() + " x"+item.getItemQty()).append("\n");
        }
        holder.cartItemsTextView.setText(cartItemsText.toString());
    }


    @Override
    public int getItemCount() {
        return orderHistoryList != null ? orderHistoryList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView orderIdTextView;
        private TextView dateTextView;
        private TextView totalPriceTextView;
        private TextView addressTextView;
        private TextView cartItemsTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.text_order_id);
            dateTextView = itemView.findViewById(R.id.text_order_date);
            totalPriceTextView = itemView.findViewById(R.id.text_total_price);
            addressTextView = itemView.findViewById(R.id.text_address);
            cartItemsTextView = itemView.findViewById(R.id.text_order_items);
        }
    }
}



