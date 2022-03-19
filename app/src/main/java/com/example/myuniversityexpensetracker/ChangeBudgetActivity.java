package com.example.myuniversityexpensetracker;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class ChangeBudgetActivity extends AppCompatActivity {

    int accountId, selectedDay, selectedMonth, selectedYear;
    Button btnReturnMain, btnCancelEditingBudget, btnChangeBudgetConfirmed, btnSelectBudgetDate;
    TextView budgetAmountText, budgetDurationText, txtBudgetDate;
    EditText edtTxtBudgetDuration, edtTxtBudgetAmount;
    Spinner budgetTimePeriodSpinner;
    private ArrayAdapter<String> budgetTimePeriodAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_budget);

        initData();

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        Account incomingAccount = null;
        Intent intent = getIntent();
        if (null != intent) {
            accountId = intent.getIntExtra("accountId", -1);
            if (accountId != -1) {
                incomingAccount = Utils.getInstance(this).getAccountByID(accountId);
                if (null != incomingAccount) {
                    setData(incomingAccount);
                    selectedDay = incomingAccount.getBudgetDay();
                    selectedMonth  = incomingAccount.getBudgetMonth();
                    selectedYear = incomingAccount.getBudgetYear();
                    txtBudgetDate.setText(selectedDay+"/"+selectedMonth+"/"+selectedYear);
                }
            }
        }

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        btnSelectBudgetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        ChangeBudgetActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        selectedDay = day;
                        selectedMonth = month;
                        selectedYear = year;
                        month = month+1;
                        String date = day+"/"+month+"/"+year;
                        txtBudgetDate.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        btnReturnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangeBudgetActivity.this, MainActivity.class);
                intent.putExtra("accountId", accountId);
                startActivity(intent);
            }
        });

        btnCancelEditingBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangeBudgetActivity.this, MainActivity.class);
                intent.putExtra("accountId", accountId);
                startActivity(intent);
            }
        });

        ArrayList<String> budgetTimeDurationOptions = new ArrayList<>();
        budgetTimeDurationOptions.add("Day(s)");
        budgetTimeDurationOptions.add("Week(s)");
        budgetTimeDurationOptions.add("Month(s)");
        budgetTimeDurationOptions.add("Year(s)");

        budgetTimePeriodAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                budgetTimeDurationOptions
        );

        budgetTimePeriodSpinner.setAdapter(budgetTimePeriodAdapter);

        budgetTimePeriodSpinner.setSelection(budgetTimePeriodAdapter.getPosition(incomingAccount.getBudgetTimePeriod()));

        btnChangeBudgetConfirmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timePeriod = budgetTimePeriodSpinner.getSelectedItem().toString();
                int timeDuration = Integer.valueOf(edtTxtBudgetDuration.getText().toString());
                double budgetAmount = Double.valueOf(edtTxtBudgetAmount.getText().toString());

                Utils.getInstance(ChangeBudgetActivity.this).ChangeBudget(timePeriod,timeDuration,budgetAmount,accountId,selectedDay,selectedMonth + 1,selectedYear);

                Toast.makeText(ChangeBudgetActivity.this, "Budget successfully changed", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ChangeBudgetActivity.this, MainActivity.class);
                intent.putExtra("accountId", accountId);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        btnReturnMain = findViewById(R.id.btnReturnMain);
        btnCancelEditingBudget = findViewById(R.id.btnCancelEditingBudget);
        btnChangeBudgetConfirmed = findViewById(R.id.btnChangeBudgetConfirmed);
        budgetDurationText = findViewById(R.id.budgetDurationText);
        budgetAmountText = findViewById(R.id.budgetAmountText);
        edtTxtBudgetAmount = findViewById(R.id.edtTxtBudgetAmount);
        edtTxtBudgetDuration = findViewById(R.id.edtTxtBudgetDuration);
        budgetTimePeriodSpinner = findViewById(R.id.budgetTimePeriodSpinner);
        btnSelectBudgetDate = findViewById(R.id.btnSelectBudgetDate);
        txtBudgetDate = findViewById(R.id.txtBudgetDate);
    }

    private void setData(Account incomingAccount) {
        edtTxtBudgetAmount.setText(String.valueOf(incomingAccount.getBudgetAmount()));
        edtTxtBudgetDuration.setText(String.valueOf(incomingAccount.getBudgetDuration()));
    }
}