package com.example.food_app.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_app.models.CartItem;
import com.example.food_app.DatabaseHelper;
import com.example.food_app.R;

import java.util.List;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartItem> mCartItems;
    private CartAdapterListener cartAdapterListener;

    public interface CartAdapterListener {
        void onCartItemChanged();
    }

    public CartAdapter(List<CartItem> cartItems, CartAdapterListener listener) {
        mCartItems = cartItems;
        cartAdapterListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shoppingcart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem cartItem = mCartItems.get(position);

        // Bind the cart item data to the views
        holder.bindCartItem(cartItem);
    }

    @Override
    public int getItemCount() {
        return mCartItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemNameTextView;
        public TextView itemPriceTextView;
        private TextView itemQtyTextView;
        public ImageView deleteButton;
        public ImageView plus;
        public ImageView minus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemName1);
            itemPriceTextView = itemView.findViewById(R.id.price1);
            itemQtyTextView = itemView.findViewById(R.id.quantity);
            plus = itemView.findViewById(R.id.plus);
            minus = itemView.findViewById(R.id.minus);
            deleteButton = itemView.findViewById(R.id.remove);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeCartItem();
                }
            });

            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    decreaseQuantity();
                }
            });

            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    increaseQuantity();
                }
            });
        }

        private void bindCartItem(CartItem cartItem) {
            itemNameTextView.setText(cartItem.getItemName());
            itemPriceTextView.setText(String.format("$%.2f", cartItem.getItemPrice()));
            if(cartItem.getItemPrice()==0){
                itemNameTextView.setTextColor(Color.MAGENTA);
                itemPriceTextView.setTextColor(Color.MAGENTA);
                minus.setClickable(false);
                plus.setClickable(false);
            }
            itemQtyTextView.setText(String.valueOf(cartItem.getItemQty()));
        }

        private void removeCartItem() {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                CartItem cartItem = mCartItems.get(position);
                mCartItems.remove(position);
                notifyItemRemoved(position);
                DatabaseHelper dbHelper = new DatabaseHelper(itemView.getContext());
                dbHelper.deleteCartItem(cartItem.getItemId());
                cartAdapterListener.onCartItemChanged();
            }
        }

        private void decreaseQuantity() {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                CartItem cartItem = mCartItems.get(position);
                DatabaseHelper dbHelper = new DatabaseHelper(itemView.getContext());
                dbHelper.decrementItemQuantity(cartItem.getItemId());
                // Refresh list from DB
                mCartItems = dbHelper.getCartItems();
                notifyDataSetChanged();
                cartAdapterListener.onCartItemChanged();
            }
        }

        private void increaseQuantity() {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                CartItem cartItem = mCartItems.get(position);
                DatabaseHelper dbHelper = new DatabaseHelper(itemView.getContext());
                dbHelper.incrementItemQuantity(cartItem.getItemId());
                // Refresh list from DB
                mCartItems = dbHelper.getCartItems();
                notifyDataSetChanged();
                cartAdapterListener.onCartItemChanged();
            }
        }

    }
}
