package com.example.myuniversityexpensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class RegistrationActivity extends AppCompatActivity {
    
    private EditText edtTxtFirstName, edtTxtLastName, edtTxtPassword1, edtTxtPassword2;
    private Button btnRegister, btnReturnMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        
        edtTxtFirstName = findViewById(R.id.edtTxtFirstName);
        edtTxtLastName = findViewById(R.id.edtTxtLastName);
        edtTxtPassword1 = findViewById(R.id.edtTxtPassword1);
        edtTxtPassword2 = findViewById(R.id.edtTxtPassword2);
        btnRegister = findViewById(R.id.btnRegister);
        btnReturnMain = findViewById(R.id.btnReturnMain);

        btnReturnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, ChooseAccountActivity.class);
                startActivity(intent);
            }
        });
        
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Checks everything has been filled in and the passwords are equal
                Boolean ok = true;
                if (edtTxtFirstName.getText().toString().equals("")){
                    ok = false;
                }
                if (edtTxtLastName.getText().toString().equals("")){
                    ok = false;
                }
                if (edtTxtPassword1.getText().toString().equals("")){
                    ok = false;
                }
                if (!(edtTxtPassword1.getText().toString().equals(edtTxtPassword2.getText().toString()))){
                    ok = false;
                }

                if (ok) {
                    AccountsRecViewAdapter accountsRecViewAdapter = new AccountsRecViewAdapter(RegistrationActivity.this, "RegistrationActivity");
                    accountsRecViewAdapter.addAccount(edtTxtFirstName.getText().toString(), edtTxtLastName.getText().toString(), edtTxtPassword1.getText().toString());
                    Toast.makeText(RegistrationActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegistrationActivity.this, ChooseAccountActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegistrationActivity.this, "Please Enter All Required Fields Correctly", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}