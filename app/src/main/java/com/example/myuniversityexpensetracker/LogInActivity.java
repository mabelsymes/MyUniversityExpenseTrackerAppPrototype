package com.example.myuniversityexpensetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LogInActivity extends AppCompatActivity {

    int accountId;
    Button btnLogIn;
    EditText edtTxtCheckPassword;
    TextView txtAccountName;
    String realPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        initViews();

        Intent intent = getIntent();
        if (null != intent) {
            accountId = intent.getIntExtra("accountId", -1);
            if (accountId != -1) {
                Account incomingAccount = Utils.getInstance(this).getAccountByID(accountId);
                if (null != incomingAccount) {
                    setData(incomingAccount);
                }
            }
        }

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtTxtCheckPassword.getText().toString().equals(realPassword)) {
                    Intent intent = new Intent(LogInActivity.this,MainActivity.class);
                    intent.putExtra("accountId", accountId);
                    startActivity(intent);
                } else {
                    Toast.makeText(LogInActivity.this, "Password is Incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setData(Account incomingAccount) {
        txtAccountName.setText(incomingAccount.getFirstName() + " " + incomingAccount.getLastName() + "'s Account:");
        realPassword = incomingAccount.getPassword();
    }

    private void initViews() {
        btnLogIn = findViewById(R.id.btnLogIn);
        edtTxtCheckPassword = findViewById(R.id.edtTxtCheckPassword);
        txtAccountName = findViewById(R.id.txtAccountName);
    }


}