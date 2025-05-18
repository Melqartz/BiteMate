package com.example.food_app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.food_app.R;

public class AccountFragment extends Fragment implements View.OnClickListener {

    private Button btnChangeName, btnChangeEmail, btnChangePassword;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        btnChangeName = view.findViewById(R.id.btnChangeName);
        btnChangeEmail = view.findViewById(R.id.btnChangeEmail);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);

        btnChangeName.setOnClickListener(this);
        btnChangeEmail.setOnClickListener(this);
        btnChangePassword.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment;
        String fragmentTag;

        switch (v.getId()) {
            case R.id.btnChangePassword:
                fragment = new ChangePasswordFragment();
                fragmentTag = "ChangePasswordFragment";
                break;

            case R.id.btnChangeEmail:
                fragment = new ChangeEmailFragment();
                fragmentTag = "ChangeEmailFragment";
                break;

            case R.id.btnChangeName:
                fragment = new ChangeProfileFragment();
                fragmentTag = "ChangeNameFragment";
                break;


            default:
                return;
        }

        // Replace the current fragment with the selected fragment
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment, fragmentTag)
                .addToBackStack(null)
                .commit();
    }
}
