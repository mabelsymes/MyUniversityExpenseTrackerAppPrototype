package com.example.myuniversityexpensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.math.MathUtils;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityLogStuff";
    TextView txtFullName, txtTopIncome, txtSecondTopIncome, txtThirdTopIncome, txtTopOutgoing, txtSecondTopOutgoing, txtThirdTopOutgoing;
    TextView txtTotalBalance, txtTotalIncome, txtTotalOutgoing, txtBudget, budgetText, txtBudgetRemaining;
    Button btnSwitchAccount, btnEditShortEvents, btnEditLongEvents, btnChangeBudget;
    ArrayList<Double> incomes;
    ArrayList<Double> outgoings;
    ArrayList<Double> totalBalances;
    int accountId;
    private ArrayList<String> possibleIncomes = new ArrayList<String>();
    private ArrayList<String> possibleOutgoings = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setStartingPossibleIncomesAndOutgoings();

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

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

        btnSwitchAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChooseAccountActivity.class);
                startActivity(intent);
            }
        });

        btnEditShortEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShortEventsActivity.class);
                intent.putExtra("accountId", accountId);
                startActivity(intent);
            }
        });

        btnEditLongEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LongEventsActivity.class);
                intent.putExtra("accountId", accountId);
                startActivity(intent);
            }
        });

        btnChangeBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChangeBudgetActivity.class);
                intent.putExtra("accountId", accountId);
                startActivity(intent);
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

    private void setData(Account account) {

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        txtFullName.setText(account.getFirstName() + " " + account.getLastName());
        if (account.getBudgetDuration() == 0){
            budgetText.setText("No Budget");
            txtBudget.setText("");
        } else {
            budgetText.setText(String.valueOf(account.getBudgetDuration()) + " " + account.getBudgetTimePeriod() + " Budget: ");
            txtBudget.setText("£ " + String.valueOf(account.getBudgetAmount()));
        }

        String budgetTimePeriod = account.getBudgetTimePeriod();
        int budgetDuration = account.getBudgetDuration();
        double budgetMoney = account.getBudgetAmount();

        int budgetDay = account.getBudgetDay();
        int budgetMonth = account.getBudgetMonth();
        int budgetYear = account.getBudgetYear();

        // Calculates date of when budget begins (most recently)
        if (budgetTimePeriod.equals("Day(s)") || budgetTimePeriod.equals("Week(s)")) {
            int toAdd = 1;
            if (budgetTimePeriod.equals("Day(s)")) {
                toAdd = 1 * budgetDuration;
            } else {
                toAdd = 7 * budgetDuration;
            }
            // For calculating budget day
            while (true){
                 // Sets how many days are in the current budgetMonth
                int daysInCurrentMonth;
                if (budgetMonth == 1 || budgetMonth == 3 || budgetMonth == 5 || budgetMonth == 7 || budgetMonth == 8 || budgetMonth == 10) {
                    daysInCurrentMonth = 31;
                } else if (budgetMonth == 4 || budgetMonth == 6 || budgetMonth == 9 || budgetMonth == 11) {
                    daysInCurrentMonth = 30;
                } else {
                    if (budgetYear % 4 == 0) {
                        daysInCurrentMonth = 29;
                    } else {
                        daysInCurrentMonth = 28;
                    }
                }
                // Adds 1 (or 7) days and edits date
                if (budgetDay + toAdd <= daysInCurrentMonth) {
                    if (Utils.getInstance(MainActivity.this).isAfter(budgetDay + toAdd,budgetMonth,budgetYear,day,month+1,year)) {
                        break;
                    }
                    budgetDay += toAdd;
                } else {
                    // If it is december, the year will have to change as well
                    if (budgetMonth < 12) {
                        if (Utils.getInstance(MainActivity.this).isAfter(toAdd - (daysInCurrentMonth - budgetDay), budgetMonth + 1, budgetYear, day, month+1, year)) {
                            break;
                        }
                        budgetDay = toAdd - (daysInCurrentMonth - budgetDay);
                        budgetMonth += 1;
                    } else {
                        if (Utils.getInstance(MainActivity.this).isAfter(toAdd - (daysInCurrentMonth - budgetDay), 1, budgetYear + 1, day, month+1, year)) {
                            break;
                        }
                        budgetDay = toAdd - (daysInCurrentMonth - budgetDay);
                        budgetMonth = 1;
                        budgetYear += 1;
                    }
                }
            }
        } else if (budgetTimePeriod.equals("Month(s)")) {
            while (true) {
                if (budgetMonth + budgetDuration <= 12) {
                    if (Utils.getInstance(MainActivity.this).isAfter(budgetDay,budgetMonth + budgetDuration, budgetYear,day,month+1,year)){
                        break;
                    }
                    budgetMonth += budgetDuration;
                } else {
                    if (Utils.getInstance(MainActivity.this).isAfter(budgetDay,budgetDuration - (12 - budgetMonth),budgetYear + 1, day, month+1, year)){
                        break;
                    }
                    budgetMonth = budgetDuration - (12 - budgetMonth);
                    budgetYear += 1;
                }
            }
        } else if (budgetTimePeriod.equals("Year(s)")) {
            while (true) {
                if (Utils.getInstance(MainActivity.this).isAfter(budgetDay,budgetMonth,budgetYear+1,day,month+1,year)){
                    break;
                }
                budgetYear += 1;
            }
        }

        for (Event e: account.getLongEvents()) {
            if (!e.isIncome()) {
                budgetMoney -= moneyDuringBudget(budgetDay,budgetMonth,budgetYear,e.getDay(),e.getMonth(),e.getYear(),e.getRepeatPeriod(),e.getRepeatNum(),e.getMoney(),day, month, year);
            }
        }


        for (Event e: account.getShortEvents()) {
            if (!e.isIncome()) {
                if (Utils.getInstance(MainActivity.this).isAfter(e.getDay(),e.getMonth()+1,e.getYear(),budgetDay,budgetMonth,budgetYear)){
                    if (Utils.getInstance(MainActivity.this).isBefore(e.getDay(),e.getMonth()+1,e.getYear(),day,month+1,year)){
                        budgetMoney -= e.getMoney();
                    }
                }
            }
        }

        txtBudgetRemaining.setText(String.valueOf(budgetMoney));

        double highestIncome;
        String IncomeCat1;
        double highest2Income;
        String IncomeCat2;
        double highest3Income;
        String IncomeCat3;

        double highestOutgoing;
        String OutgoingCat1;
        double highest2Outgoing;
        String OutgoingCat2;
        double highest3Outgoing;
        String OutgoingCat3;

        int pos;

        incomes = account.getAllIncomes();
        outgoings = account.getAllOutgoings();

        // Only uses events up to the current date
        totalBalances = Utils.getInstance(MainActivity.this).getTotalBalances(account, day, month, year);

        txtTotalBalance.setText(String.valueOf(totalBalances.get(0)));
        txtTotalIncome.setText(String.valueOf(totalBalances.get(1)));
        txtTotalOutgoing.setText(String.valueOf(totalBalances.get(2)));

        ArrayList<String> incomePossibilities = Utils.getInstance(MainActivity.this).getPossibleIncomes();
        ArrayList<String> outgoingPossibilities = Utils.getInstance(MainActivity.this).getPossibleOutgoings();
        
        ArrayList<Double> currentIncomes = new ArrayList<>();
        ArrayList<Double> currentOutgoings = new ArrayList<>();
        int i = 22;
        while (i>0) {
            currentIncomes.add(0.0);
            currentOutgoings.add(0.0);
            i -= 1;
        }
        
        for (Event e: account.getShortEvents()) {
            int position = 0;
            if (e.isIncome()) {
                position = incomePossibilities.indexOf(e.getCategory());
                currentIncomes.set(position, currentIncomes.get(position) + e.getMoney());
            } else {
                position = outgoingPossibilities.indexOf(e.getCategory());
                currentOutgoings.set(position, currentOutgoings.get(position) + e.getMoney());
            }
        }

        for (Event e: account.getLongEvents()) {
            int position = 0;
            if (e.isIncome()) {
                position = incomePossibilities.indexOf(e.getCategory());
                currentIncomes.set(position, currentIncomes.get(position) + Utils.getInstance(MainActivity.this).getTotalAmount(e, e.getDay(),e.getMonth(),e.getYear(), day, month + 1, year));
            } else {
                position = outgoingPossibilities.indexOf(e.getCategory());
                currentOutgoings.set(position, currentOutgoings.get(position) + Utils.getInstance(MainActivity.this).getTotalAmount(e,e.getDay(),e.getMonth(),e.getYear(), day, month + 1, year));
            }
        }

        incomes = currentIncomes;
        outgoings = currentOutgoings;

        highestIncome = Collections.max(incomes);
        pos = incomes.indexOf(highestIncome);
        IncomeCat1 = possibleIncomes.get(pos);
        incomes.remove(pos);
        possibleIncomes.remove(pos);

        highest2Income = Collections.max(incomes);
        pos = incomes.indexOf(highest2Income);
        IncomeCat2 = possibleIncomes.get(pos);
        incomes.remove(pos);
        possibleIncomes.remove(pos);

        highest3Income = Collections.max(incomes);
        pos = incomes.indexOf(highest3Income);
        IncomeCat3 = possibleIncomes.get(pos);
        incomes.remove(pos);
        possibleIncomes.remove(pos);

        highestOutgoing = Collections.max(outgoings);
        pos = outgoings.indexOf(highestOutgoing);
        OutgoingCat1 = possibleOutgoings.get(pos);
        outgoings.remove(pos);
        possibleOutgoings.remove(pos);

        highest2Outgoing = Collections.max(outgoings);
        pos = outgoings.indexOf(highest2Outgoing);
        OutgoingCat2 = possibleOutgoings.get(pos);
        outgoings.remove(pos);
        possibleOutgoings.remove(pos);

        highest3Outgoing = Collections.max(outgoings);
        pos = outgoings.indexOf(highest3Outgoing);
        OutgoingCat3 = possibleOutgoings.get(pos);
        outgoings.remove(pos);
        possibleOutgoings.remove(pos);

        txtTopIncome.setText(IncomeCat1 + ": " + String.valueOf(highestIncome));
        txtSecondTopIncome.setText(IncomeCat2 + ": " + String.valueOf(highest2Income));
        txtThirdTopIncome.setText(IncomeCat3 + ": " + String.valueOf(highest3Income));
        txtTopOutgoing.setText(OutgoingCat1 + ": " + String.valueOf(highestOutgoing));
        txtSecondTopOutgoing.setText(OutgoingCat2 + ": " + String.valueOf(highest2Outgoing));
        txtThirdTopOutgoing.setText(OutgoingCat3 + ": " + String.valueOf(highest3Outgoing));
    }

    private void initViews() {
        txtFullName = findViewById(R.id.txtFullName);
        btnSwitchAccount = findViewById(R.id.btnSwitchAccount);
        btnEditShortEvents = findViewById(R.id.btnEditShortEvents);
        btnEditLongEvents = findViewById(R.id.btnEditLongEvents);
        txtTopIncome = findViewById(R.id.txtTopIncome);
        txtSecondTopIncome = findViewById(R.id.txtSecondTopIncome);
        txtThirdTopIncome = findViewById(R.id.txtThirdTopIncome);
        txtTopOutgoing = findViewById(R.id.txtTopOutgoing);
        txtSecondTopOutgoing = findViewById(R.id.txtSecondTopOutgoing);
        txtThirdTopOutgoing = findViewById(R.id.txtThirdTopOutgoing);

        txtTotalBalance = findViewById(R.id.txtTotalBalance);
        txtTotalIncome = findViewById(R.id.txtTotalIncomes);
        txtTotalOutgoing = findViewById(R.id.txtTotalOutgoings);
        btnChangeBudget = findViewById(R.id.btnChangeBudget);
        txtBudget = findViewById(R.id.txtBudget);
        budgetText = findViewById(R.id.budgetText);
        txtBudgetRemaining = findViewById(R.id.txtBudgetRemaining);
    }

    public void setStartingPossibleIncomesAndOutgoings () {

        ArrayList<String> incomePossibilities = new ArrayList<>();
        incomePossibilities.add("Salary");
        incomePossibilities.add("Wages");
        incomePossibilities.add("Part Time Job");
        incomePossibilities.add("Sales");
        incomePossibilities.add("Gifts");

        incomePossibilities.add("Groceries");
        incomePossibilities.add("Going out");
        incomePossibilities.add("Food");
        incomePossibilities.add("Travel");
        incomePossibilities.add("Clothes");
        incomePossibilities.add("Essentials");
        incomePossibilities.add("Phone");
        incomePossibilities.add("Accommodation");
        incomePossibilities.add("Tuition");
        incomePossibilities.add("Subscriptions");
        incomePossibilities.add("Holidays");
        incomePossibilities.add("Utility Bills");
        incomePossibilities.add("Washing");
        incomePossibilities.add("Entertainment");
        incomePossibilities.add("Electronics");
        incomePossibilities.add("Uni Supplies");
        incomePossibilities.add("Other");

        ArrayList<String> outgoingPossibilities = new ArrayList<>();
        outgoingPossibilities.add("Groceries");
        outgoingPossibilities.add("Going out");
        outgoingPossibilities.add("Food");
        outgoingPossibilities.add("Travel");
        outgoingPossibilities.add("Clothes");
        outgoingPossibilities.add("Essentials");
        outgoingPossibilities.add("Phone");
        outgoingPossibilities.add("Accommodation");
        outgoingPossibilities.add("Tuition");
        outgoingPossibilities.add("Subscriptions");
        outgoingPossibilities.add("Holidays");
        outgoingPossibilities.add("Utility Bills");
        outgoingPossibilities.add("Washing");
        outgoingPossibilities.add("Entertainment");
        outgoingPossibilities.add("Electronics");
        outgoingPossibilities.add("Uni Supplies");

        outgoingPossibilities.add("Salary");
        outgoingPossibilities.add("Wages");
        outgoingPossibilities.add("Part Time Job");
        outgoingPossibilities.add("Sales");
        outgoingPossibilities.add("Gifts");
        outgoingPossibilities.add("Other");

        possibleIncomes = incomePossibilities;
        possibleOutgoings = outgoingPossibilities;
    }

    public Double moneyDuringBudget(int budgetDay, int budgetMonth, int budgetYear, int eDay, int eMonth, int eYear, String eTimePeriod, int eDuration, double eMoney, int day, int month, int year) {

        double endTotal = 0.0;

        if (eTimePeriod.equals("Day(s)") || eTimePeriod.equals("Week(s)")) {
            int toAdd = 1;
            if (eTimePeriod.equals("Day(s)")) {
                toAdd = 1 * eDuration;
            } else {
                toAdd = 7 * eDuration;
            }

            // For calculating days which are after budgetDate but before current date
            while (true){
                // Sets how many days are in the current eMonth
                int daysInCurrentMonth;
                if (eMonth == 1 || eMonth == 3 || eMonth == 5 || eMonth == 7 || eMonth == 8 || eMonth == 10) {
                    daysInCurrentMonth = 31;
                } else if (eMonth == 4 || eMonth == 6 || eMonth == 9 || eMonth == 11) {
                    daysInCurrentMonth = 30;
                } else {
                    if (eYear % 4 == 0) {
                        daysInCurrentMonth = 29;
                    } else {
                        daysInCurrentMonth = 28;
                    }
                }
                // Adds 1 (or 7) days and edits date
                if (eDay + toAdd <= daysInCurrentMonth) {
                    if (Utils.getInstance(MainActivity.this).isAfter(eDay + toAdd, eMonth, eYear ,day,month+1,year)) {
                        break;
                    }
                    eDay += toAdd;
                    if (Utils.getInstance(MainActivity.this).isAfter(eDay, eMonth, eYear ,budgetDay, budgetMonth, budgetYear)) {
                       endTotal += eMoney;
                    }
                } else {
                    // If it is december, the year will have to change as well
                    if (eMonth < 12) {
                        if (Utils.getInstance(MainActivity.this).isAfter(toAdd - (daysInCurrentMonth - eDay), eMonth + 1, eYear, day, month+1, year)) {
                            break;
                        }
                        eDay = toAdd - (daysInCurrentMonth - eDay);
                        eMonth += 1;

                        if (Utils.getInstance(MainActivity.this).isAfter(eDay, eMonth, eYear ,budgetDay, budgetMonth, budgetYear)) {
                            endTotal += eMoney;
                        }
                    } else {
                        if (Utils.getInstance(MainActivity.this).isAfter(toAdd - (daysInCurrentMonth - eDay), 1, eYear + 1, day, month+1, year)) {
                            break;
                        }
                        eDay = toAdd - (daysInCurrentMonth - eDay);
                        eMonth = 1;
                        eYear += 1;

                        if (Utils.getInstance(MainActivity.this).isAfter(eDay, eMonth, eYear ,budgetDay, budgetMonth, budgetYear)) {
                            endTotal += eMoney;
                        }
                    }
                }
            }
        } else if (eTimePeriod.equals("Month(s)")) {
            while (true) {
                if (eMonth + eDuration <= 12) {
                    if (Utils.getInstance(MainActivity.this).isAfter(eDay,eMonth + eDuration, eYear,day,month+1,year)){
                        break;
                    }
                    eMonth += eDuration;

                    if (Utils.getInstance(MainActivity.this).isAfter(eDay, eMonth, eYear ,budgetDay, budgetMonth, budgetYear)) {
                        endTotal += eMoney;
                    }
                } else {
                    if (Utils.getInstance(MainActivity.this).isAfter(eDay,eDuration - (12 - eMonth),eYear + 1, day, month+1, year)){
                        break;
                    }
                    eMonth = eDuration - (12 - eMonth);
                    eYear += 1;

                    if (Utils.getInstance(MainActivity.this).isAfter(eDay, eMonth, eYear ,budgetDay, budgetMonth, budgetYear)) {
                        endTotal += eMoney;
                    }
                }
            }
        } else if (eTimePeriod.equals("Year(s)")) {
            while (true) {
                if (Utils.getInstance(MainActivity.this).isAfter(eDay,eMonth,eYear+1,day,month+1,year)){
                    break;
                }
                eYear += 1;
                if (Utils.getInstance(MainActivity.this).isAfter(eDay, eMonth, eYear ,budgetDay, budgetMonth, budgetYear)) {
                    endTotal += eMoney;
                }
            }
        }

    return endTotal;
}}