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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Calendar;

// Will use for editing a short activity as well

public class AddNewShortEventActivity extends AppCompatActivity {

    private static final String TAG = "AddNewShortEvent";
    Button btnAddShortActivityConfirmed, btnCancelAddingShortEvent, btnSelectShortDate;
    Spinner shortCategoriesSpinner, shortIncomeOutcomeSpinner;
    EditText edtTxtShortEventName, edtTxtShortEventMoney, edtTxtShortEventDesc;
    TextView txtShortDate;
    DatePickerDialog.OnDateSetListener setListener;
    private int accountId, newId, selectedDay, selectedMonth, selectedYear;
    private double money;
    private Account incomingAccount;
    private String category, eventName, incomeInput, description;
    private Boolean income, allEntered;
    private ArrayAdapter<String> shortCategoriesAdapter, incomeOutcomeAdapter;

    // For when the event is being edited
    private int shortEventId;
    private Boolean editEvent;
    private Event currentShortEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_short_event);

        initViews();

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        btnSelectShortDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddNewShortEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        selectedDay = day;
                        selectedMonth = month;
                        selectedYear = year;
                        month = month+1;
                        String date = day+"/"+month+"/"+year;
                        txtShortDate.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        ArrayList<String> shortCategories = new ArrayList<>();
        shortCategories.add("Groceries");
        shortCategories.add("Going out");
        shortCategories.add("Food");
        shortCategories.add("Travel");
        shortCategories.add("Clothes");
        shortCategories.add("Essentials");
        shortCategories.add("Phone");
        shortCategories.add("Holidays");
        shortCategories.add("Utility Bills");
        shortCategories.add("Washing");
        shortCategories.add("Entertainment");
        shortCategories.add("Electronics");
        shortCategories.add("Uni Supplies");
        shortCategories.add("Sales");
        shortCategories.add("Gifts");
        shortCategories.add("Salary");
        shortCategories.add("Wages");
        shortCategories.add("Part Time Job");
        shortCategories.add("Accommodation");
        shortCategories.add("Tuition");
        shortCategories.add("Subscriptions");
        shortCategories.add("Other");

        shortCategoriesAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                shortCategories
        );

        shortCategoriesSpinner.setAdapter(shortCategoriesAdapter);

        ArrayList<String> incomeOutcome = new ArrayList<>();
        incomeOutcome.add("Outgoing");
        incomeOutcome.add("Income");

        incomeOutcomeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                incomeOutcome
        );

        shortIncomeOutcomeSpinner.setAdapter(incomeOutcomeAdapter);

        //gets incoming account
        Intent intent = getIntent();
        if (null != intent) {
            accountId = intent.getIntExtra("accountId", -1);
            if (accountId != -1) {
                incomingAccount = Utils.getInstance(this).getAccountByID(accountId);
            }
            editEvent = intent.getBooleanExtra("editEvent", false);
            if (editEvent) {
                shortEventId = intent.getIntExtra("shortEventId", -1);
                currentShortEvent = Utils.getInstance(AddNewShortEventActivity.this).getShortEventByID(incomingAccount, shortEventId);

                // Sets the data according to the short event selected
                shortCategoriesSpinner.setSelection(shortCategoriesAdapter.getPosition(currentShortEvent.getCategory()));
                if (currentShortEvent.isIncome()) {
                    shortIncomeOutcomeSpinner.setSelection(1);
                }
                edtTxtShortEventName.setText(currentShortEvent.getEventName());
                edtTxtShortEventMoney.setText(String.valueOf(currentShortEvent.getMoney()));
                edtTxtShortEventDesc.setText(currentShortEvent.getDescription());
                String date = String.valueOf(currentShortEvent.getDay()) + "/" + String.valueOf(currentShortEvent.getMonth()) + "/" + String.valueOf(currentShortEvent.getYear());
                txtShortDate.setText(date);
                selectedDay = currentShortEvent.getDay();
                selectedMonth = currentShortEvent.getMonth();
                selectedYear = currentShortEvent.getYear();
            }
        }

        btnCancelAddingShortEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewShortEventActivity.this, ShortEventsActivity.class);
                intent.putExtra("accountId", accountId);
                startActivity(intent);
            }
        });

        // Adds the new event
        btnAddShortActivityConfirmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEntered()) {
                     // Chooses new Id for short event
                     int position = incomingAccount.getShortEvents().size();
                     if (position == 0 || position == -1) {
                         newId = 1;
                     } else {
                         position = position - 1;
                         newId = incomingAccount.getShortEvents().get(position).getId() + 1;
                     }

                     // Sets data for short event
                     category = shortCategoriesSpinner.getSelectedItem().toString();

                     eventName = edtTxtShortEventName.getText().toString();
                     incomeInput = shortIncomeOutcomeSpinner.getSelectedItem().toString();
                     if (incomeInput.equals("Income")) {
                         income = true;
                     } else {
                         income = false;
                     }
                     money = Double.valueOf(edtTxtShortEventMoney.getText().toString());
                     money = Math.round(money * 100.0) / 100.0;
                     description = edtTxtShortEventDesc.getText().toString();

                     if (editEvent) {
                         Event event = new Event(newId,selectedDay,selectedMonth,selectedYear,category,eventName,income,money,description,false, 0, "None");
                         EventsRecViewAdapter eventsRecViewAdapter = new EventsRecViewAdapter(AddNewShortEventActivity.this, "AddNewShortEvent", incomingAccount, accountId);
                         eventsRecViewAdapter.editShortEvent(event, incomingAccount, shortEventId);
                     } else {
                         // Adds short event
                         Event event = new Event(newId,selectedDay,selectedMonth,selectedYear,category,eventName,income,money,description,false, 0, "None");
                         EventsRecViewAdapter eventsRecViewAdapter = new EventsRecViewAdapter(AddNewShortEventActivity.this, "AddNewShortEvent", incomingAccount, accountId);
                         eventsRecViewAdapter.addShortEvent(event, incomingAccount);
                         Toast.makeText(AddNewShortEventActivity.this, "New Short Event Successfully added", Toast.LENGTH_SHORT).show();
                     }

                     // Moves back to ShortEventsActivity
                     Intent intent = new Intent(AddNewShortEventActivity.this, ShortEventsActivity.class);
                     intent.putExtra("accountId", accountId);
                     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                     startActivity(intent);

                 } else {
                     Toast.makeText(AddNewShortEventActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                 }
            }

            private Boolean checkEntered() {
                allEntered = true;
                if (edtTxtShortEventName.equals("")) {
                    allEntered = false;
                }
                if (edtTxtShortEventMoney.equals("")) {
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

    public Boolean inList(int newId, ArrayList longIds) {
        Boolean found = false;
        for (Object o: longIds){
            int i = Integer.valueOf(String.valueOf(o));
            if (newId == i){
                found = true;
            }
        }
        return found;
    }

    private void initViews() {
        btnAddShortActivityConfirmed = findViewById(R.id.btnAddShortActivityConfirmed);
        btnCancelAddingShortEvent = findViewById(R.id.btnCancelAddingShortEvent);
        shortCategoriesSpinner = findViewById(R.id.shortCategoriesSpinner);
        shortIncomeOutcomeSpinner = findViewById(R.id.shortIncomeOutcomeSpinner);
        edtTxtShortEventName = findViewById(R.id.edtTxtShortEventName);
        edtTxtShortEventMoney = findViewById(R.id.edtTxtShortEventMoney);
        edtTxtShortEventDesc = findViewById(R.id.edtTxtShortEventDesc);
        txtShortDate = findViewById(R.id.txtShortDate);
        btnSelectShortDate = findViewById(R.id.btnSelectShortDate);
    }
}