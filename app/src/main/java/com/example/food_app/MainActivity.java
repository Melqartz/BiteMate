package com.example.food_app;

import static com.example.food_app.GlobalVariable.baseUrl;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ImageView menuIcon;
    ImageView shoppingCart;
    GlobalVariable glob;
    SharedPreferences sharedPreferences;
    private final OkHttpClient client = new OkHttpClient();

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
                    LoginFragment bottomSheetDialog = new LoginFragment();
                    bottomSheetDialog.show(getSupportFragmentManager(), "exampleBottomSheet");
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
                sendSecureApiRequest();
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

    private void sendSecureApiRequest() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        user.getIdToken(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String idToken = task.getResult().getToken();

                Request request = new Request.Builder()
                        .url(baseUrl + "/api/admin/secure")
                        .addHeader("Authorization", "Bearer " + idToken)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "API failed", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String responseData = response.body().string();
                        runOnUiThread(() -> {
                            if (response.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "✅ Success: " + responseData, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(MainActivity.this, "❌ Error: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            } else {
                Toast.makeText(this, "Failed to get Firebase token", Toast.LENGTH_SHORT).show();
            }
        });
    }

}