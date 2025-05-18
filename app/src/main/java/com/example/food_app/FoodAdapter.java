package com.example.food_app;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private Context mContext;
    private List<Food> mFoods;
    private DatabaseHelper dbHelper;
    private GlobalVariable glob= new GlobalVariable();


    public FoodAdapter(Context context, List<Food> foods, DatabaseHelper dbHelper) {
        mContext = context;
        mFoods = foods;
        this.dbHelper = dbHelper;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.burger_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = mFoods.get(position);
        holder.nameTextView.setText(food.name);
        holder.priceTextView.setText(String.format("$%.2f", food.price));
        String imageName = food.imageUrl.replace(".jpg", "").replace(".png", "").replace(".jpeg", "");

        int resId = mContext.getResources().getIdentifier(imageName, "drawable", mContext.getPackageName());
        if (resId != 0) {
            holder.imageView.setImageResource(resId);
        } else {
            // fallback image if not found
            holder.imageView.setImageResource(R.drawable.americanburger);
        }

        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!glob.isUserSignedIn){
                    Toast.makeText(mContext, "Please Sign in first!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(mContext, "Added " + food.name + " to cart", Toast.LENGTH_SHORT).show();
                    addToCart(food);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mFoods.size();
    }

    private void addToCart(Food food) {
        dbHelper.insertCartItem(glob.currentUserId, food.name, food.price);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView priceTextView;
        public ImageView imageView;
        public ImageButton addButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.itemName);
            priceTextView = itemView.findViewById(R.id.price);
            imageView = itemView.findViewById(R.id.imageView);
            addButton = itemView.findViewById(R.id.button123);
        }
    }
}

