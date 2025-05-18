package com.example.food_app.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.food_app.R;
import com.example.food_app.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangeProfileFragment extends Fragment {

    private EditText editFullName, editPhoneNumber;
    private Button btnSaveChanges;

    public ChangeProfileFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_profile, container, false);

        editFullName = view.findViewById(R.id.editFullName);
        editPhoneNumber = view.findViewById(R.id.editPhoneNumber);
        btnSaveChanges = view.findViewById(R.id.btnSaveChanges);

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileChanges();
            }
        });

        return view;
    }


    private void saveProfileChanges() {
        String fullName = editFullName.getText().toString();
        String phoneNumber = editPhoneNumber.getText().toString();

        // Get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the user's data in the Firebase Realtime Database
        DatabaseReference usersRef = FirebaseDatabase.getInstance("https://bitemate-c316c-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");
        String userId = user.getUid();

        User updatedUser = new User(fullName, userId, phoneNumber,20);
        usersRef.child(userId).setValue(updatedUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Profile changes saved successfully", Toast.LENGTH_SHORT).show();
                        editFullName.setText("");
                        editPhoneNumber.setText("");
                    } else {
                        Toast.makeText(getActivity(), "Failed to save profile changes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
