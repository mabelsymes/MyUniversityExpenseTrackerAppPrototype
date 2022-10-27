package com.example.myuniversityexpensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class AddNewLongEventActivity extends AppCompatActivity {

    private static final String TAG = "AddNewLongEvent";
    Button btnAddLongActivityConfirmed, btnCancelAddingLongEvent, btnSelectLongDate;
    Spinner longCategoriesSpinner, longIncomeOutcomeSpinner, repetitionOptionsSpinner;
    EditText edtTxtLongEventName, edtTxtLongEventMoney, edtTxtLongEventDesc, edtTxtLongRepetition;
    TextView txtLongDate;
    DatePickerDialog.OnDateSetListener setListener;
    private int accountId, newId, selectedDay, selectedMonth, selectedYear, repeatNum;
    private double money;
    private Account incomingAccount;
    private String category, eventName, incomeInput, description, repeatPeriod;
    private Boolean income, allEntered;
    private ArrayAdapter<String> longCategoriesAdapter, incomeOutcomeAdapter, repetitionPeriodsAdapter;

    // For when an event is being edited
    private int longEventId;
    private Boolean editEvent;
    private Event currentLongEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_long_event);

        initViews();

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        btnSelectLongDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddNewLongEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        selectedDay = day;
                        selectedMonth = month;
                        selectedYear = year;
                        month = month+1;
                        selectedMonth = selectedMonth + 1;
                        String date = day+"/"+month+"/"+year;
                        txtLongDate.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        ArrayList<String> LongCategories = new ArrayList<>();
        LongCategories.add("Salary");
        LongCategories.add("Wages");
        LongCategories.add("Part Time Job");
        LongCategories.add("Accommodation");
        LongCategories.add("Tuition");
        LongCategories.add("Subscriptions");
        LongCategories.add("Groceries");
        LongCategories.add("Going out");
        LongCategories.add("Clothes");
        LongCategories.add("Essentials");
        LongCategories.add("Sales");
        LongCategories.add("Gifts");
        LongCategories.add("Food");
        LongCategories.add("Travel");
        LongCategories.add("Phone");
        LongCategories.add("Holidays");
        LongCategories.add("Utility Bills");
        LongCategories.add("Washing");
        LongCategories.add("Entertainment");
        LongCategories.add("Electronics");
        LongCategories.add("Uni Supplies");
        LongCategories.add("Other");

        longCategoriesAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                LongCategories
        );

        longCategoriesSpinner.setAdapter(longCategoriesAdapter);

        ArrayList<String> incomeOutcome = new ArrayList<>();
        incomeOutcome.add("Outgoing");
        incomeOutcome.add("Income");

        incomeOutcomeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                incomeOutcome
        );

        longIncomeOutcomeSpinner.setAdapter(incomeOutcomeAdapter);

        ArrayList<String> repetitionPeriods = new ArrayList<>();
        repetitionPeriods.add("Day(s)");
        repetitionPeriods.add("Week(s)");
        repetitionPeriods.add("Month(s)");
        repetitionPeriods.add("Year(s)");

        repetitionPeriodsAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                repetitionPeriods
        );

        repetitionOptionsSpinner.setAdapter(repetitionPeriodsAdapter);

        //gets incoming account
        Intent intent = getIntent();
        if (null != intent) {
            accountId = intent.getIntExtra("accountId", -1);
            if (accountId != -1) {
                incomingAccount = Utils.getInstance(this).getAccountByID(accountId);
            }
            editEvent = intent.getBooleanExtra("editEvent", false);
            if (editEvent) {
                longEventId = intent.getIntExtra("longEventId", -1);
                currentLongEvent = Utils.getInstance(AddNewLongEventActivity.this).getLongEventByID(incomingAccount, longEventId);

                // Sets the data according to the Long event selected
                longCategoriesSpinner.setSelection(longCategoriesAdapter.getPosition(currentLongEvent.getCategory()));
                repetitionOptionsSpinner.setSelection(repetitionPeriodsAdapter.getPosition(currentLongEvent.getRepeatPeriod()));
                if (currentLongEvent.isIncome()) {
                    longIncomeOutcomeSpinner.setSelection(1);
                }
                edtTxtLongEventName.setText(currentLongEvent.getEventName());
                edtTxtLongEventMoney.setText(String.valueOf(currentLongEvent.getMoney()));
                edtTxtLongRepetition.setText(String.valueOf(currentLongEvent.getRepeatNum()));
                edtTxtLongEventDesc.setText(currentLongEvent.getDescription());
                String date = String.valueOf(currentLongEvent.getDay()) + "/" + String.valueOf(currentLongEvent.getMonth()) + "/" + String.valueOf(currentLongEvent.getYear());
                txtLongDate.setText(date);
                selectedDay = currentLongEvent.getDay();
                selectedMonth = currentLongEvent.getMonth();
                selectedYear = currentLongEvent.getYear();
                Log.d(TAG, "onCreate: Finished setting log stuff");
            }
        }

        btnCancelAddingLongEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewLongEventActivity.this, LongEventsActivity.class);
                intent.putExtra("accountId", accountId);
                startActivity(intent);
            }
        });

        // Adds the new event
        btnAddLongActivityConfirmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEntered()) {
                    // Chooses new Id for Long event
                    int position = incomingAccount.getLongEvents().size();
                    if (position == 0 || position == -1) {
                        newId = 1;
                    } else {
                        position = position - 1;
                        newId = incomingAccount.getLongEvents().get(position).getId() + 1;
                    }

                    // Sets data for Long event
                    category = longCategoriesSpinner.getSelectedItem().toString();

                    eventName = edtTxtLongEventName.getText().toString();
                    incomeInput = longIncomeOutcomeSpinner.getSelectedItem().toString();
                    if (incomeInput.equals("Income")) {
                        income = true;
                    } else {
                        income = false;
                    }
                    money = Double.valueOf(edtTxtLongEventMoney.getText().toString());
                    money = Math.round(money * 100.0) / 100.0;
                    description = edtTxtLongEventDesc.getText().toString();

                    repeatNum = Integer.valueOf(edtTxtLongRepetition.getText().toString());
                    repeatPeriod = repetitionOptionsSpinner.getSelectedItem().toString();

                    if (editEvent) {
                        Event event = new Event(newId,selectedDay,selectedMonth,selectedYear,category,eventName,income,money,description,true, repeatNum, repeatPeriod);
                        EventsRecViewAdapter eventsRecViewAdapter = new EventsRecViewAdapter(AddNewLongEventActivity.this, "AddNewLongEvent", incomingAccount, accountId);
                        eventsRecViewAdapter.editLongEvent(event, incomingAccount, longEventId);
                    } else {
                        Event event = new Event(newId,selectedDay,selectedMonth,selectedYear,category,eventName,income,money,description,true, repeatNum, repeatPeriod);
                        EventsRecViewAdapter eventsRecViewAdapter = new EventsRecViewAdapter(AddNewLongEventActivity.this, "AddNewLongEvent", incomingAccount, accountId);
                        eventsRecViewAdapter.addLongEvent(event, incomingAccount);
                        Toast.makeText(AddNewLongEventActivity.this, "New Long Event Successfully added", Toast.LENGTH_SHORT).show();
                    }

                    // Moves back to LongEventsActivity
                    Intent intent = new Intent(AddNewLongEventActivity.this, LongEventsActivity.class);
                    intent.putExtra("accountId", accountId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else {
                    Toast.makeText(AddNewLongEventActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }

            private Boolean checkEntered() {
                allEntered = true;
                if (edtTxtLongEventName.equals("")) {
                    allEntered = false;
                }
                if (edtTxtLongEventMoney.equals("")) {
                    allEntered = false;
                }
                return(allEntered);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public Boolean inList(int newId, ArrayList shortIds) {
        Boolean found = false;
        for (Object o: shortIds){
            int i = Integer.valueOf(String.valueOf(o));
            if (newId == i){
                found = true;
            }
        }
        return found;
    }

    private void initViews() {
        btnAddLongActivityConfirmed = findViewById(R.id.btnAddLongActivityConfirmed);
        btnCancelAddingLongEvent = findViewById(R.id.btnCancelAddingLongEvent);
        longCategoriesSpinner = findViewById(R.id.longCategoriesSpinner);
        longIncomeOutcomeSpinner = findViewById(R.id.longIncomeOutcomeSpinner);
        edtTxtLongEventName = findViewById(R.id.edtTxtLongEventName);
        edtTxtLongEventMoney = findViewById(R.id.edtTxtLongEventMoney);
        edtTxtLongEventDesc = findViewById(R.id.edtTxtLongEventDesc);
        txtLongDate = findViewById(R.id.txtLongDate);
        btnSelectLongDate = findViewById(R.id.btnSelectLongDate);
        repetitionOptionsSpinner = findViewById(R.id.repetitionOptionsSpinner);
        edtTxtLongRepetition = findViewById(R.id.edtTxtLongRepetition);
    }
}