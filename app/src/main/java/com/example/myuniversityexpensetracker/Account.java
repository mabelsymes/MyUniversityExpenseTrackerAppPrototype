package com.example.myuniversityexpensetracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Account{

    private static final String TAG = "Account Stuff";

    private int id;
    private String firstName;
    private String lastName;
    private String password;
    private ArrayList<Event> shortEvents;
    private ArrayList<Event> longEvents;
    private ArrayList<Double> allIncomes;
    private ArrayList<Double> allOutgoings;
    private ArrayList<ArrayList> newIncomes;
    private ArrayList<ArrayList> newOutgoings;
    private Boolean incomeNull;
    private Boolean outgoingNull;
    private String budgetTimePeriod;
    private int budgetDuration;
    private double budgetAmount;
    private int budgetDay, budgetMonth, budgetYear;

    public Account(int id, String firstName, String lastName, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.shortEvents = new ArrayList<>();
        this.longEvents = new ArrayList<>();
        this.incomeNull = true;
        this.outgoingNull = true;
        this.budgetTimePeriod = "Week(s)";
        this.budgetDuration = 1;
        this.budgetAmount = 0.0;
        this.budgetDay = 1;
        this.budgetMonth = 1;
        this.budgetYear = 1;

        ArrayList<ArrayList> tempNewIncomes = new ArrayList<>();
        ArrayList<ArrayList> tempNewOutgoings = new ArrayList<>();

        int i = 9;
        while (i>0) {
            tempNewIncomes.add(new ArrayList<Integer>());
            tempNewOutgoings.add(new ArrayList<Integer>());
            i -= 1;
        }

        this.newIncomes = tempNewIncomes;
        this.newOutgoings = tempNewOutgoings;

        ArrayList<Double> tempAllIncomes = new ArrayList<>();
        ArrayList<Double> tempAllOutgoings = new ArrayList<>();
        i = 22;
        while (i>0) {
            tempAllIncomes.add(0.0);
            tempAllOutgoings.add(0.0);
            i -= 1;
        }

        this.allIncomes = tempAllIncomes;
        this.allOutgoings = tempAllOutgoings;
    }

    public Event getShortEventByID(int id) {
        ArrayList<Event> shortEvents = getShortEvents();
        if (null != shortEvents) {
            for (Event e: shortEvents) {
                if (e.getId() == id) {
                    return(e);
                }
            }
        }
        return null;
    }

    public Event getLongEventByID(int id) {
        ArrayList<Event> longEvents = getLongEvents();
        if (null != longEvents) {
            for (Event e: longEvents) {
                if (e.getId() == id) {
                    return(e);
                }
            }
        }
        return null;
    }

    public int getBudgetDay() {
        return budgetDay;
    }

    public void setBudgetDay(int budgetDay) {
        this.budgetDay = budgetDay;
    }

    public int getBudgetMonth() {
        return budgetMonth;
    }

    public void setBudgetMonth(int budgetMonth) {
        this.budgetMonth = budgetMonth;
    }

    public int getBudgetYear() {
        return budgetYear;
    }

    public void setBudgetYear(int budgetYear) {
        this.budgetYear = budgetYear;
    }

    public double getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(double budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public String getBudgetTimePeriod() {
        return budgetTimePeriod;
    }

    public void setBudgetTimePeriod(String budgetTimePeriod) {
        this.budgetTimePeriod = budgetTimePeriod;
    }

    public int getBudgetDuration() {
        return budgetDuration;
    }

    public void setBudgetDuration(int budgetDuration) {
        this.budgetDuration = budgetDuration;
    }

    public ArrayList<Double> getAllIncomes() {
        return allIncomes;
    }

    public void setAllIncomes(ArrayList<Double> allIncomes) {
        this.allIncomes = allIncomes;
    }

    public ArrayList<Double> getAllOutgoings() {
        return allOutgoings;
    }

    public void setAllOutgoings(ArrayList<Double> allOutgoings) {
        this.allOutgoings = allOutgoings;
    }

    public ArrayList<Event> getShortEvents() {
        return shortEvents;
    }

    public void setShortEvents(ArrayList<Event> shortEvents) {
        this.shortEvents = shortEvents;
    }

    public ArrayList<Event> getLongEvents() {
        return longEvents;
    }

    public void setLongEvents(ArrayList<Event> longEvents) {
        this.longEvents = longEvents;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }
}
