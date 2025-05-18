package com.example.food_app;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private Context context;
    private EditText emailEditText;
    private EditText passwordEditText;
    private ImageView img;
    private Button loginButton;
    private TextView registerTextView;
    private FirebaseAuth mAuth;
    private GlobalVariable glob;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();
        glob = new GlobalVariable();

        emailEditText = view.findViewById(R.id.email);
        passwordEditText = view.findViewById(R.id.password);
        loginButton = view.findViewById(R.id.login);
        registerTextView = view.findViewById(R.id.openSignupFragment);
        img = view.findViewById(R.id.exitBtn);

        loginButton.setOnClickListener(this);
        registerTextView.setOnClickListener(this);

        img.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == loginButton) {
            loginUser();
        } else if (view == registerTextView) {
            dismiss();
            SignupFragment bottomSheetDialog = new SignupFragment();
            bottomSheetDialog.show(getParentFragmentManager(), "exampleBottomSheet");
        }
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(context, "Empty Credentials!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            if (user.isEmailVerified()) {
                                // User is logged in and email is verified, proceed to the app
                                handleLoginSuccess(user);
                            } else {
                                // Email is not verified, show a toast message
                                Toast.makeText(context, "Please verify your email before signing in.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void handleLoginSuccess(FirebaseUser user) {
        String userId = user.getUid();
        glob.isUserSignedIn = true;
        glob.currentUserId = userId;


        // Search for the user in the database
        DatabaseReference usersRef = FirebaseDatabase.getInstance("https://bitemate-c316c-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users");
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User found in the database, extract their information
                    String phoneNumber = snapshot.child("phoneNumber").getValue(String.class);
                    String name = snapshot.child("name").getValue(String.class);

                    Toast.makeText(context, "uid " + userId+"phone"+phoneNumber, Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    // Save the extracted information to shared preferences
                    editor.putString("uid", userId);
                    editor.putString("phoneNumber", phoneNumber);
                    editor.putString("name", name);
                    editor.apply();

                    // Proceed to the next screen or perform any necessary actions
                } else {
                    // User not found in the database
                    // Handle the case accordingly
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read the data from the database
                // Handle the error case accordingly
            }
        });

        dismiss();

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView2, HomeFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack("name") // name can be null
                .commit();
    }
}
