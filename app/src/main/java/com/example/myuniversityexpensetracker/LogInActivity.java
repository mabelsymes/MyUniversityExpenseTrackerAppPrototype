package com.example.myuniversityexpensetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class LogInActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    int accountId;
    Button btnLogIn;
    EditText edtTxtCheckPassword;
    TextView txtAccountName;
    String realPassword;
    Account account;
    Boolean deleteAccount;

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
                    deleteAccount = intent.getBooleanExtra("deleteAccount",false);
                    setData(incomingAccount, deleteAccount);
                    account = incomingAccount;
                }
            }
        }

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtTxtCheckPassword.getText().toString().equals(realPassword)) {
                    if (deleteAccount == false){
                        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                        intent.putExtra("accountId", accountId);
                        startActivity(intent);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
                        builder.setMessage("Are you sure you want to delete " + account.getFirstName() + "'s account?");
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Utils.getInstance(LogInActivity.this).removeAccount(account);
                                Toast.makeText(LogInActivity.this, "Account Removed", Toast.LENGTH_SHORT).show();
                                ArrayList<Account> accounts = Utils.getInstance(LogInActivity.this).getAccounts();
                                Intent intent = new Intent(LogInActivity.this, ChooseAccountActivity.class);
                                startActivity(intent);
                            }
                        });

                        builder.create().show();

                    }
                } else {
                    Toast.makeText(LogInActivity.this, "Password is Incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setData(Account incomingAccount, Boolean deleteAccount) {
        txtAccountName.setText(incomingAccount.getFirstName() + " " + incomingAccount.getLastName() + "'s");
        realPassword = incomingAccount.getPassword();
        if (deleteAccount == true) {
            btnLogIn.setText("Delete Account");
        }
    }

    private void initViews() {
        btnLogIn = findViewById(R.id.btnLogIn);
        edtTxtCheckPassword = findViewById(R.id.edtTxtCheckPassword);
        txtAccountName = findViewById(R.id.txtAccountName);
    }


}