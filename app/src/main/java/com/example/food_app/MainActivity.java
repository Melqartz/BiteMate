package com.example.food_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.example.food_app.ui.AccountFragment;
import com.example.food_app.ui.CartFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ImageView menuIcon;
    ImageView shoppingCart;
    GlobalVariable glob;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        glob = new GlobalVariable();

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        String uid = sharedPreferences.getString("uid", "");

        if(uid != null && !uid.isEmpty()){
            glob.isUserSignedIn=true;
            glob.currentUserId=uid;
            //updateMenuItemsVisibility();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        menuIcon = findViewById(R.id.menuIcon);
        shoppingCart = findViewById(R.id.shoppingCart);

        shoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (glob.isUserSignedIn) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView2, CartFragment.class, null)
                            .setReorderingAllowed(true)
                            .addToBackStack("name") // name can be null
                            .commit();
                } else {
                    Toast.makeText(MainActivity.this, "Please Sign in to Order", Toast.LENGTH_SHORT).show();
                }
            }
        });

        toolbar = findViewById(R.id.toolbar);

        /* to remove any settings by default on the toolbar */
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.getOverflowIcon().setVisible(false, false);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMenuItemsVisibility();
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        View headerView = navigationView.getHeaderView(0);
        ImageView menuIcon1 = headerView.findViewById(R.id.menuIcon1);

        menuIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(this);

        updateMenuItemsVisibility();
    }

    private void updateMenuItemsVisibility() {
        Menu menu = navigationView.getMenu();
        MenuItem signInItem = menu.findItem(R.id.nav_signIn);
        MenuItem myAccountItem = menu.findItem(R.id.nav_my_account);
        MenuItem logOutItem = menu.findItem(R.id.nav_logout);
        MenuItem historyItem=menu.findItem(R.id.nav_history);
        MenuItem rewardsItem=menu.findItem(R.id.nav_rewards);

        signInItem.setVisible(!glob.isUserSignedIn);
        myAccountItem.setVisible(glob.isUserSignedIn);
        logOutItem.setVisible(glob.isUserSignedIn);
        historyItem.setVisible(glob.isUserSignedIn);
        rewardsItem.setVisible(glob.isUserSignedIn);
    }

    public void updateCartBadge(int count) {
        TextView cartBadge = findViewById(R.id.cartBadge);
        if (cartBadge != null) {
            if (count > 0) {
                cartBadge.setVisibility(View.VISIBLE);
                cartBadge.setText(String.valueOf(count));
            } else {
                cartBadge.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (item.getItemId()) {
            case R.id.nav_home:
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView2, HomeFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("name") // name can be null
                        .commit();
                break;

            case R.id.nav_menu:
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView2, MenuFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("name") // name can be null
                        .commit();
                break;

            case R.id.nav_my_account:
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView2, AccountFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("name") // name can be null
                        .commit();
                break;

            case R.id.nav_history:
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView2, HistoryFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("name") // name can be null
                        .commit();
                break;

            case R.id.nav_rewards:
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView2, RewardsFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("name") // name can be null
                        .commit();
                break;

            case R.id.nav_logout:
                glob.isUserSignedIn = false;
                glob.currentUserId="";
                updateMenuItemsVisibility();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView2, HomeFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("name") // name can be null
                        .commit();
                break;

            case R.id.nav_signIn:
                LoginFragment bottomSheetDialog = new LoginFragment();
                bottomSheetDialog.show(getSupportFragmentManager(), "exampleBottomSheet");
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}