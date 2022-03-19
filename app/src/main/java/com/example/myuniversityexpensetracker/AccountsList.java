package com.example.myuniversityexpensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

public class AccountsList extends AppCompatActivity {

    private View txtFullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_list);

        txtFullName = findViewById(R.id.txtFullName);
    }

    public View getTxtFullName() {
        return txtFullName;
    }

    public void setTxtFullName(View txtFullName) {
        this.txtFullName = txtFullName;
    }
}