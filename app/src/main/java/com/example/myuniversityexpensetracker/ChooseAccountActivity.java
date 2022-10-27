package com.example.myuniversityexpensetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class ChooseAccountActivity extends AppCompatActivity {

    private static final String TAG = "ChooseAccountActivity";
    private RecyclerView accountsRecView;
    private AccountsRecViewAdapter adapter;
    private Button btnAddNewAccount;
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_account);

        btnAddNewAccount = findViewById(R.id.btnAddNewAccount);
        welcomeText = findViewById(R.id.welcomeText);

        adapter = new AccountsRecViewAdapter(this, "chooseAccount");
        accountsRecView = findViewById(R.id.accountsRecView);

        accountsRecView.setAdapter(adapter);
        accountsRecView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setAccounts(Utils.getInstance(this).getAccounts());

        btnAddNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseAccountActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }
}