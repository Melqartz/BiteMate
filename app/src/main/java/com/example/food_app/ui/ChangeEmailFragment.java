package com.example.food_app.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.food_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangeEmailFragment extends Fragment {

    private EditText editOldEmail, editPassword, editNewEmail;
    private Button btnSaveEmail;

    public ChangeEmailFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_email, container, false);

        editOldEmail = view.findViewById(R.id.editOldEmail);
        editPassword = view.findViewById(R.id.editPassword);
        editNewEmail = view.findViewById(R.id.editNewEmail);
        btnSaveEmail = view.findViewById(R.id.btnSaveEmail);

        btnSaveEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEmailChanges();
            }
        });

        return view;
    }

    private void saveEmailChanges() {
        String oldEmail = editOldEmail.getText().toString();
        String password = editPassword.getText().toString();
        final String newEmail = editNewEmail.getText().toString();

        // Perform security verifications here
        if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            Toast.makeText(getActivity(), "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Re-authenticate the user before changing the email
        AuthCredential credential = EmailAuthProvider.getCredential(oldEmail, password);
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Change the email
                            user.updateEmail(newEmail)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getActivity(), "Email changed successfully", Toast.LENGTH_SHORT).show();
                                                user.sendEmailVerification();
                                                editOldEmail.setText("");
                                                editPassword.setText("");
                                                editNewEmail.setText("");
                                            } else {
                                                Toast.makeText(getActivity(), "Failed to change email", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(getActivity(), "Invalid credentials", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
