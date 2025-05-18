package com.example.food_app;


import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupFragment extends BottomSheetDialogFragment {

    private ImageView img;
    private EditText fullNameEditText, emailEditText, passwordEditText, confirmPasswordEditText, phoneNumberEditText;
    private Button signupButton;
    private TextView loginButton;
    private FirebaseAuth mAuth;
    private GlobalVariable glob;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        mAuth = FirebaseAuth.getInstance();
        glob = new GlobalVariable();

        img = view.findViewById(R.id.exitBtn);
        img.setOnClickListener(view1 -> dismiss());

        fullNameEditText = view.findViewById(R.id.fullNameEditText);
        emailEditText = view.findViewById(R.id.email);
        passwordEditText = view.findViewById(R.id.password);
        confirmPasswordEditText = view.findViewById(R.id.confirmPassword);
        phoneNumberEditText = view.findViewById(R.id.phoneNumber);
        signupButton = view.findViewById(R.id.signup);
        loginButton = view.findViewById(R.id.openLoginFragment);

        signupButton.setOnClickListener(view12 -> signUpUser());

        loginButton.setOnClickListener(view13 -> {
            dismiss();
            LoginFragment bottomSheetDialog = new LoginFragment();
            bottomSheetDialog.show(getParentFragmentManager(), "exampleBottomSheet");
        });

        return view;
    }

    private void signUpUser() {
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();

        if (!fullName.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()) {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(getActivity(), "Invalid email format", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 8) {
                Toast.makeText(getActivity(), "Password is too short", Toast.LENGTH_SHORT).show();
            } else if (!password.matches(".*\\d.*")) {
                Toast.makeText(getActivity(), "Password must contain at least one number", Toast.LENGTH_SHORT).show();
            } else if (!password.matches(".*[A-Z].*")) {
                Toast.makeText(getActivity(), "Password must contain at least one capital letter", Toast.LENGTH_SHORT).show();
            } else {
                if (password.equals(confirmPassword)) {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        user.sendEmailVerification()
                                                .addOnCompleteListener(emailVerificationTask -> {
                                                    if (emailVerificationTask.isSuccessful()) {
                                                        // User sign up successful and verification email sent
                                                        Toast.makeText(getActivity(), "Verification email sent. Please verify your email before signing in.", Toast.LENGTH_SHORT).show();
                                                        handleSignupSuccess(user, fullName, phoneNumber);
                                                    } else {
                                                        // Failed to send verification email
                                                        Toast.makeText(getActivity(), "Failed to send verification email", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                } else {
                                    // User sign up failed
                                    Toast.makeText(getActivity(), "Sign up failed. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }



    private void handleSignupSuccess(FirebaseUser user, String fullName, String phoneNumber) {
        String userId = user.getUid();

        DatabaseReference usersRef = FirebaseDatabase.getInstance("https://bitemate-c316c-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");
        User newUser = new User(fullName, userId, phoneNumber,100);

        usersRef.child(userId).setValue(newUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "User registered successfully. Please Sign in and verify account.", Toast.LENGTH_SHORT).show();
                        // Dismiss the dialog fragment only if it is currently visible
                        if (isAdded() && isVisible()) {
                            dismiss();
                        }
                        LoginFragment bottomSheetDialog = new LoginFragment();
                        bottomSheetDialog.show(getParentFragmentManager(), "exampleBottomSheet");

                    } else {
                        Toast.makeText(getActivity(), "Failed to save user information", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
