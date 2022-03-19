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
                    System.out.println("First name not entered");
                }
                if (edtTxtLastName.getText().toString().equals("")){
                    ok = false;
                    System.out.println("Last name not entered");
                }
                if (edtTxtPassword1.getText().toString().equals("")){
                    ok = false;
                    System.out.println("Password not entered");
                }
                if (!(edtTxtPassword1.getText().toString().equals(edtTxtPassword2.getText().toString()))){
                    ok = false;
                    System.out.println("Passwords not equal");
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

    public EditText getEdtTxtFirstName() {
        return edtTxtFirstName;
    }

    public void setEdtTxtFirstName(EditText edtTxtFirstName) {
        this.edtTxtFirstName = edtTxtFirstName;
    }

    public EditText getEdtTxtLastName() {
        return edtTxtLastName;
    }

    public void setEdtTxtLastName(EditText edtTxtLastName) {
        this.edtTxtLastName = edtTxtLastName;
    }

    public EditText getEdtTxtPassword1() {
        return edtTxtPassword1;
    }

    public void setEdtTxtPassword1(EditText edtTxtPassword1) {
        this.edtTxtPassword1 = edtTxtPassword1;
    }

    public EditText getEdtTxtPassword2() {
        return edtTxtPassword2;
    }

    public void setEdtTxtPassword2(EditText edtTxtPassword2) {
        this.edtTxtPassword2 = edtTxtPassword2;
    }

    public Button getBtnRegister() {
        return btnRegister;
    }

    public void setBtnRegister(Button btnRegister) {
        this.btnRegister = btnRegister;
    }
}