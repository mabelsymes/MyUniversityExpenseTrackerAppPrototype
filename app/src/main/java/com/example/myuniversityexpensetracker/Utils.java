package com.example.myuniversityexpensetracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.time.temporal.ChronoUnit;

// categories: Salary, Wages, Sales, Gifts, Groceries, Going out, Clothes, Essentials, Other

public class Utils {

    private static final String ALL_ACCOUNTS_KEY = "accounts_list";

    private static final String TAG = "Utils Stuff";

    private ArrayList<String> possibleIncomes = new ArrayList<String>();
    private ArrayList<String> possibleOutgoings = new ArrayList<String>();

    private static Utils instance;
    private SharedPreferences sharedPreferences;

    // Sets sharedPreferences and Editor
    private Utils(Context context) {

        setStartingPossibleIncomesAndOutgoings();
        sharedPreferences = context.getSharedPreferences("database", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        if (null == getAccounts()) {
            editor.putString(ALL_ACCOUNTS_KEY, gson.toJson(new ArrayList<Account>(5)));
            editor.commit();
        }
    }

    // Gets instance
    public static synchronized Utils getInstance(Context context) {

        if (null != instance) {
            return instance;
        } else {
            instance = new Utils(context);
            return instance;
        }
    }

    // Gets accounts using database Gson
    public ArrayList<Account> getAccounts() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Account>>() {}.getType();
        ArrayList<Account> accounts = gson.fromJson(sharedPreferences.getString(ALL_ACCOUNTS_KEY, null), type);
        return accounts;
    }

    public ArrayList<Event> getShortEvents(Account account) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Account>>() {}.getType();
        ArrayList<Account> accounts = gson.fromJson(sharedPreferences.getString(ALL_ACCOUNTS_KEY, null), type);

        // Gets the account
        int id = account.getId();
        Account toReturnAccount = null;
        if (null != accounts) {
            for (Account a: accounts) {
                if (a.getId() == id) {
                    toReturnAccount = a;
                }
            }
        }

        ArrayList<Event> shortEvents = toReturnAccount.getShortEvents();
        return shortEvents;
    }

    public ArrayList<Event> getLongEvents(Account account) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Account>>() {}.getType();
        ArrayList<Account> accounts = gson.fromJson(sharedPreferences.getString(ALL_ACCOUNTS_KEY, null), type);

        // Gets the account
        int id = account.getId();
        Account toReturnAccount = null;
        if (null != accounts) {
            for (Account a: accounts) {
                if (a.getId() == id) {
                    toReturnAccount = a;
                }
            }
        }

        ArrayList<Event> longEvents = toReturnAccount.getLongEvents();
        return longEvents;
    }

    public Event getShortEventByID(Account account, int shortEventId) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Account>>() {}.getType();
        ArrayList<Account> accounts = gson.fromJson(sharedPreferences.getString(ALL_ACCOUNTS_KEY, null), type);

        // Gets the account
        int id = account.getId();
        Account toUseAccount = null;
        if (null != accounts) {
            for (Account a: accounts) {
                if (a.getId() == id) {
                    toUseAccount = a;
                }
            }
        }

        Event toReturnShortEvent = toUseAccount.getShortEventByID(shortEventId);
        return toReturnShortEvent;
    }

    public Event getLongEventByID(Account account, int longEventId) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Account>>() {}.getType();
        ArrayList<Account> accounts = gson.fromJson(sharedPreferences.getString(ALL_ACCOUNTS_KEY, null), type);

        // Gets the account
        int id = account.getId();
        Account toUseAccount = null;
        if (null != accounts) {
            for (Account a: accounts) {
                if (a.getId() == id) {
                    toUseAccount = a;
                }
            }
        }

        Event toReturnLongEvent = toUseAccount.getLongEventByID(longEventId);
        return toReturnLongEvent;
    }

    // Gets accounts by the id
    public Account getAccountByID(int id) {
        ArrayList<Account> accounts = getAccounts();
        if (null != accounts) {
            for (Account a: accounts) {
                if (a.getId() == id) {
                    return(a);
                }
            }
        }
        return null;
    }

    public void addAccount(String firstName, String lastName, String password) {

        ArrayList<Account> accounts = getAccounts();
        int position = getAccounts().size();
        position = position - 1;
        if (position != -1) {
            accounts.add(new Account(accounts.get(position).getId() + 1, firstName,lastName,password));
        } else {
            accounts.add(new Account(1,firstName,lastName,password));
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString(ALL_ACCOUNTS_KEY, gson.toJson(accounts));
        editor.commit();
    }

    public void editShortEvent(Event newShortEvent, Account account, int shortEventId) {

        // Changes event
        ArrayList<Event> shortEvents = getShortEvents(account);

        Event toUseShortEvent = null;
        if (null != shortEvents) {
            for (Event e: shortEvents) {
                if (e.getId() == shortEventId) {
                    toUseShortEvent = e;
                }
            }
        }

        int eventPosition = shortEvents.indexOf(toUseShortEvent);

        // Subtracts money from correct category
        Event oldShortEvent = shortEvents.get(eventPosition);
        String category = oldShortEvent.getCategory();
        ArrayList<Double> updatedIncomes;
        ArrayList<Double> updatedOutgoings;
        updatedIncomes = account.getAllIncomes();
        updatedOutgoings = account.getAllOutgoings();

        int incomePosition = -1;
        int outgoingPosition = -1;
        if (oldShortEvent.isIncome()) {
            if (possibleIncomes.contains(category)) {
                incomePosition = possibleIncomes.indexOf(category);
            }
        } else {
            if (possibleOutgoings.contains(category)) {
                outgoingPosition = possibleOutgoings.indexOf(category);
            }
        }

        if (incomePosition >= 0)    {
            double currentAmount = updatedIncomes.get(incomePosition);
            currentAmount = currentAmount - oldShortEvent.getMoney();
            updatedIncomes.set(incomePosition, currentAmount);
        } else if (outgoingPosition >= 0) {
            double currentAmount = updatedOutgoings.get(outgoingPosition);
            currentAmount = currentAmount - oldShortEvent.getMoney();
            updatedOutgoings.set(outgoingPosition, currentAmount);
        }

        // Changes event
        shortEvents.set(eventPosition, newShortEvent);

        // Adds money to correct category
        category = newShortEvent.getCategory();
        incomePosition = -1;
        outgoingPosition = -1;

        if (newShortEvent.isIncome()) {
            if (possibleIncomes.contains(category)) {
                incomePosition = possibleIncomes.indexOf(category);
            }
        } else {
            if (possibleOutgoings.contains(category)) {
                outgoingPosition = possibleOutgoings.indexOf(category);
            }
        }

        if (incomePosition >= 0)    {
            double currentAmount = updatedIncomes.get(incomePosition);
            currentAmount = currentAmount + newShortEvent.getMoney();
            updatedIncomes.set(incomePosition, currentAmount);
        } else if (outgoingPosition >= 0) {
            double currentAmount = updatedOutgoings.get(outgoingPosition);
            currentAmount = currentAmount + newShortEvent.getMoney();
            updatedOutgoings.set(outgoingPosition, currentAmount);
        }

        // Changes this in the account
        ArrayList<Account> accounts = getAccounts();
        int id = account.getId();

        Account toChangeAccount = null;
        if (null != accounts) {
            for (Account a: accounts) {
                if (a.getId() == id) {
                    toChangeAccount = a;
                }
            }
        }

        int position = accounts.indexOf(toChangeAccount);
        accounts.get(position).setShortEvents(shortEvents);
        accounts.get(position).setAllIncomes(updatedIncomes);
        accounts.get(position).setAllOutgoings(updatedOutgoings);

        // Updates Shared Preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString(ALL_ACCOUNTS_KEY, gson.toJson(accounts));
        editor.commit();
    }

    public void editLongEvent(Event newLongEvent, Account account, int longEventId) {

        // Changes event
        ArrayList<Event> longEvents = getLongEvents(account);

        Event toUseLongEvent = null;
        if (null != longEvents) {
            for (Event e: longEvents) {
                if (e.getId() == longEventId) {
                    toUseLongEvent = e;
                }
            }
        }

        int eventPosition = longEvents.indexOf(toUseLongEvent);

        // Subtracts money from correct category
        Event oldLongEvent = longEvents.get(eventPosition);
        String category = oldLongEvent.getCategory();
        ArrayList<Double> updatedIncomes;
        ArrayList<Double> updatedOutgoings;
        updatedIncomes = account.getAllIncomes();
        updatedOutgoings = account.getAllOutgoings();

        int incomePosition = -1;
        int outgoingPosition = -1;
        if (oldLongEvent.isIncome()) {
            if (possibleIncomes.contains(category)) {
                incomePosition = possibleIncomes.indexOf(category);
            }
        } else {
            if (possibleOutgoings.contains(category)) {
                outgoingPosition = possibleOutgoings.indexOf(category);
            }
        }

        if (incomePosition >= 0)    {
            double currentAmount = updatedIncomes.get(incomePosition);
            currentAmount = currentAmount - oldLongEvent.getMoney();
            updatedIncomes.set(incomePosition, currentAmount);
        } else if (outgoingPosition >= 0) {
            double currentAmount = updatedOutgoings.get(outgoingPosition);
            currentAmount = currentAmount - oldLongEvent.getMoney();
            updatedOutgoings.set(outgoingPosition, currentAmount);
        }

        // Changes event
        longEvents.set(eventPosition, newLongEvent);

        // Adds money to correct category
        category = newLongEvent.getCategory();

        incomePosition = -1;
        outgoingPosition = -1;
        if (newLongEvent.isIncome()) {
            if (possibleIncomes.contains(category)) {
                incomePosition = possibleIncomes.indexOf(category);
            }
        } else {
            if (possibleOutgoings.contains(category)) {
                outgoingPosition = possibleOutgoings.indexOf(category);
            }
        }

        if (incomePosition >= 0)    {
            double currentAmount = updatedIncomes.get(incomePosition);
            currentAmount = currentAmount + newLongEvent.getMoney();
            updatedIncomes.set(incomePosition, currentAmount);
        } else if (outgoingPosition >= 0) {
            double currentAmount = updatedOutgoings.get(outgoingPosition);
            currentAmount = currentAmount + newLongEvent.getMoney();
            updatedOutgoings.set(outgoingPosition, currentAmount);
        }

        // Changes this in the account
        ArrayList<Account> accounts = getAccounts();
        int id = account.getId();

        Account toChangeAccount = null;
        if (null != accounts) {
            for (Account a: accounts) {
                if (a.getId() == id) {
                    toChangeAccount = a;
                }
            }
        }

        int position = accounts.indexOf(toChangeAccount);
        accounts.get(position).setLongEvents(longEvents);
        accounts.get(position).setAllIncomes(updatedIncomes);
        accounts.get(position).setAllOutgoings(updatedOutgoings);

        // Updates Shared Preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString(ALL_ACCOUNTS_KEY, gson.toJson(accounts));
        editor.commit();
    }

    public void addShortEvent(Event shortEvent, Account account) {

        // Makes a new list of updated ShortEvents
        ArrayList<Event> shortEvents = account.getShortEvents();
        shortEvents.add(shortEvent);

        ArrayList<Double> updatedIncomes;
        ArrayList<Double> updatedOutgoings;
        updatedIncomes = account.getAllIncomes();
        updatedOutgoings = account.getAllOutgoings();

        ArrayList<Account> accounts = getAccounts();
        int id = account.getId();

        Account toChangeAccount = null;
        if (null != accounts) {
            for (Account a: accounts) {
                if (a.getId() == id) {
                    toChangeAccount = a;
                }
            }
        }

        int position = accounts.indexOf(toChangeAccount);

        // Adds money to correct category
        String category = shortEvent.getCategory();
        int incomePosition = -1;
        int outgoingPosition = -1;
        if (shortEvent.isIncome()) {
            if (possibleIncomes.contains(category)) {
                incomePosition = possibleIncomes.indexOf(category);
            }
        } else {
            if (possibleOutgoings.contains(category)) {
                outgoingPosition = possibleOutgoings.indexOf(category);
            }
        }

        if (incomePosition >= 0)    {
            double currentAmount = updatedIncomes.get(incomePosition);
            currentAmount = currentAmount + shortEvent.getMoney();
            updatedIncomes.set(incomePosition, currentAmount);
            // Incomes has been updated, so it later needs to be updated in the account
        } else if (outgoingPosition >= 0) {
            double currentAmount = updatedOutgoings.get(outgoingPosition);
            currentAmount = currentAmount + shortEvent.getMoney();
            updatedOutgoings.set(outgoingPosition, currentAmount);
            // Outgoings has been updated as well
        }

        // Changes short events in the account
        accounts.get(position).setShortEvents(shortEvents);
        accounts.get(position).setAllIncomes(updatedIncomes);
        accounts.get(position).setAllOutgoings(updatedOutgoings);

        // Updates Shared Preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString(ALL_ACCOUNTS_KEY, gson.toJson(accounts));
        editor.commit();
    }

    public void addLongEvent(Event longEvent, Account account) {

        // Makes a new list of updated LongEvents
        ArrayList<Event> longEvents = account.getLongEvents();
        longEvents.add(longEvent);
        ArrayList<Double> updatedIncomes;
        ArrayList<Double> updatedOutgoings;
        updatedIncomes = account.getAllIncomes();
        updatedOutgoings = account.getAllOutgoings();

        // Adds money to correct category
        String category = longEvent.getCategory();
        int incomePosition = -1;
        int outgoingPosition = -1;
        if (longEvent.isIncome()) {
            if (possibleIncomes.contains(category)) {
                incomePosition = possibleIncomes.indexOf(category);
            }
        } else {
            if (possibleOutgoings.contains(category)) {
                outgoingPosition = possibleOutgoings.indexOf(category);
            }
        }

        if (incomePosition >= 0)    {
            double currentAmount = updatedIncomes.get(incomePosition);
            currentAmount = currentAmount + longEvent.getMoney();
            updatedIncomes.set(incomePosition, currentAmount);
            // Incomes has been updated
        } else if (outgoingPosition >= 0) {
            double currentAmount = updatedOutgoings.get(outgoingPosition);
            currentAmount = currentAmount + longEvent.getMoney();
            updatedOutgoings.set(outgoingPosition, currentAmount);
            // Outgoings has been updated as well
        }

        // Changes long events in the account
        ArrayList<Account> accounts = getAccounts();
        int id = account.getId();

        Account toChangeAccount = null;
        if (null != accounts) {
            for (Account a: accounts) {
                if (a.getId() == id) {
                    toChangeAccount = a;
                }
            }
        }

        int position = accounts.indexOf(toChangeAccount);
        accounts.get(position).setLongEvents(longEvents);
        accounts.get(position).setAllIncomes(updatedIncomes);
        accounts.get(position).setAllOutgoings(updatedOutgoings);

        // Updates Shared Preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString(ALL_ACCOUNTS_KEY, gson.toJson(accounts));
        editor.commit();
    }

    public void removeAccount(Account account) {
        ArrayList<Account> accounts = getAccounts();
        if (null != accounts) {
            for (Account a: accounts) {
                if (a.getId() == account.getId()) {
                    if (accounts.remove(a)) {
                        Gson gson = new Gson();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(ALL_ACCOUNTS_KEY, gson.toJson(accounts));
                        editor.commit();
                        break;
                    }
                }
            }
        }
    }

    public void removeShortEvent(Event shortEvent, Account account) {
        // Makes a new list of updated ShortEvents
        ArrayList<Event> shortEvents = account.getShortEvents();

        // Gets right shortEvent
        int shortEventId = shortEvent.getId();
        Event toRemoveShortEvent = null;
        if (null != shortEvents) {
            for (Event e: shortEvents) {
                if (e.getId() == shortEventId) {
                    toRemoveShortEvent = e;
                }
            }
        }

        // Subtracts money from correct category
        Event oldShortEvent = toRemoveShortEvent;
        String category = oldShortEvent.getCategory();
        ArrayList<Double> updatedIncomes;
        ArrayList<Double> updatedOutgoings;
        updatedIncomes = account.getAllIncomes();
        updatedOutgoings = account.getAllOutgoings();

        int incomePosition = -1;
        int outgoingPosition = -1;
        if (oldShortEvent.isIncome()) {
            if (possibleIncomes.contains(category)) {
                incomePosition = possibleIncomes.indexOf(category);
            }
        } else {
            if (possibleOutgoings.contains(category)) {
                outgoingPosition = possibleOutgoings.indexOf(category);
            }
        }

        if (incomePosition >= 0)    {
            double currentAmount = updatedIncomes.get(incomePosition);
            currentAmount = currentAmount - oldShortEvent.getMoney();
            updatedIncomes.set(incomePosition, currentAmount);
        } else if (outgoingPosition >= 0) {
            double currentAmount = updatedOutgoings.get(outgoingPosition);
            currentAmount = currentAmount - oldShortEvent.getMoney();
            updatedOutgoings.set(outgoingPosition, currentAmount);
        }

        // Removes the short event
        shortEvents.remove(toRemoveShortEvent);

        // Changes this in the account
        ArrayList<Account> accounts = getAccounts();
        int id = account.getId();

        Account toChangeAccount = null;
        if (null != accounts) {
            for (Account a: accounts) {
                if (a.getId() == id) {
                    toChangeAccount = a;
                }
            }
        }

        int position = accounts.indexOf(toChangeAccount);
        accounts.get(position).setShortEvents(shortEvents);
        accounts.get(position).setAllIncomes(updatedIncomes);
        accounts.get(position).setAllOutgoings(updatedOutgoings);

        // Updates Shared Preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString(ALL_ACCOUNTS_KEY, gson.toJson(accounts));
        editor.commit();
    }

    public void removeLongEvent(Event longEvent, Account account) {
        // Makes a new list of updated LongEvents
        ArrayList<Event> longEvents = account.getLongEvents();

        // Gets right longEvent
        int longEventId = longEvent.getId();
        Event toRemoveLongEvent = null;
        if (null != longEvents) {
            for (Event e: longEvents) {
                if (e.getId() == longEventId) {
                    toRemoveLongEvent = e;
                }
            }
        }

        // Subtracts money from correct category
        Event oldLongEvent = toRemoveLongEvent;
        String category = oldLongEvent.getCategory();
        ArrayList<Double> updatedIncomes;
        ArrayList<Double> updatedOutgoings;
        updatedIncomes = account.getAllIncomes();
        updatedOutgoings = account.getAllOutgoings();

        int incomePosition = -1;
        int outgoingPosition = -1;
        if (oldLongEvent.isIncome()) {
            if (possibleIncomes.contains(category)) {
                incomePosition = possibleIncomes.indexOf(category);
            }
        } else {
            if (possibleOutgoings.contains(category)) {
                outgoingPosition = possibleOutgoings.indexOf(category);
            }
        }
        if (incomePosition >= 0)    {
            double currentAmount = updatedIncomes.get(incomePosition);
            currentAmount = currentAmount - oldLongEvent.getMoney();
            updatedIncomes.set(incomePosition, currentAmount);
        } else if (outgoingPosition >= 0) {
            double currentAmount = updatedOutgoings.get(outgoingPosition);
            currentAmount = currentAmount - oldLongEvent.getMoney();
            updatedOutgoings.set(outgoingPosition, currentAmount);
        }

        // Removes the long event
        longEvents.remove(toRemoveLongEvent);

        // Changes this in the account
        ArrayList<Account> accounts = getAccounts();
        int id = account.getId();

        Account toChangeAccount = null;
        if (null != accounts) {
            for (Account a: accounts) {
                if (a.getId() == id) {
                    toChangeAccount = a;
                }
            }
        }

        int position = accounts.indexOf(toChangeAccount);
        accounts.get(position).setLongEvents(longEvents);
        accounts.get(position).setAllIncomes(updatedIncomes);
        accounts.get(position).setAllOutgoings(updatedOutgoings);

        // Updates Shared Preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString(ALL_ACCOUNTS_KEY, gson.toJson(accounts));
        editor.commit();
    }

    public void setStartingPossibleIncomesAndOutgoings () {

        ArrayList<String> incomePossibilities = new ArrayList<>();
        incomePossibilities.add("Salary");
        incomePossibilities.add("Wages");
        incomePossibilities.add("Part Time Job");
        incomePossibilities.add("Sales");
        incomePossibilities.add("Gifts");
        //
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
        setPossibleIncomes(incomePossibilities);


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
        setPossibleOutgoings(outgoingPossibilities);
    }

    public Boolean isBefore(int day, int month, int year, int maxDay, int maxMonth, int maxYear) {

        Boolean before = false;

        if (year < maxYear) {
            before = true;
        } else if (year == maxYear) {
            if (month < maxMonth) {
                before = true;
            } else if (month == maxMonth) {
                if (day <= maxDay) {
                    before = true;
                }
            }
        }

        return before;
    }

    public Boolean isAfter(int day, int month, int year, int minDay, int minMonth, int minYear){
        Boolean after = false;

        if (year > minYear) {
            after = true;
        } else if (year == minYear) {
            if (month > minMonth) {
                after = true;
            } else if (month == minMonth) {
                if (day > minDay) {
                    after = true;
                }
            }
        }
        return after;
    }

    public double getTotalAmount(Event e, int eDay, int eMonth, int eYear, int day, int month, int year){
       if (isBefore(eDay, eMonth, eYear, day, month, year)) {
            double add = 0;
            double times = 0;

            if (e.getRepeatPeriod().equals("Day(s)") || e.getRepeatPeriod().equals("Week(s)")) {
                Calendar cal1 = Calendar.getInstance();
                Calendar cal2 = Calendar.getInstance();

                cal1.set(eDay, eMonth, eYear);
                cal2.set(year, month, day);
                LocalDate dateBefore = LocalDate.of(2005,Month.NOVEMBER,27);

                if (eMonth == 1) {
                    dateBefore = LocalDate.of(eYear, Month.JANUARY, eDay);
                } else if (eMonth == 2) {
                    dateBefore = LocalDate.of(eYear, Month.FEBRUARY, eDay);
                } else if (eMonth == 3) {
                    dateBefore = LocalDate.of(eYear, Month.MARCH, eDay);
                } else if (eMonth == 4) {
                    dateBefore = LocalDate.of(eYear, Month.APRIL, eDay);
                } else if (eMonth == 5) {
                    dateBefore = LocalDate.of(eYear, Month.MAY, eDay);
                } else if (eMonth == 6) {
                    dateBefore = LocalDate.of(eYear, Month.JUNE, eDay);
                } else if (eMonth == 7) {
                    dateBefore = LocalDate.of(eYear, Month.JULY, eDay);
                } else if (eMonth == 8) {
                    dateBefore = LocalDate.of(eYear, Month.AUGUST, eDay);
                } else if (eMonth == 9) {
                    dateBefore = LocalDate.of(eYear, Month.SEPTEMBER, eDay);
                } else if (eMonth == 10) {
                    dateBefore = LocalDate.of(eYear, Month.OCTOBER, eDay);
                } else if (eMonth == 11) {
                    dateBefore = LocalDate.of(eYear, Month.NOVEMBER, eDay);
                } else if (eMonth == 12) {
                    dateBefore = LocalDate.of(eYear, Month.DECEMBER, eDay);
                }

                LocalDate dateAfter = LocalDate.of(2005,Month.NOVEMBER,27);

                if (month == 1) {
                    dateAfter = LocalDate.of(year, Month.JANUARY, day);
                } else if (month == 2) {
                    dateAfter = LocalDate.of(year, Month.FEBRUARY, day);
                } else if (month == 3) {
                    dateAfter = LocalDate.of(year, Month.MARCH, day);
                } else if (month == 4) {
                    dateAfter = LocalDate.of(year, Month.APRIL, day);
                } else if (month == 5) {
                    dateAfter = LocalDate.of(year, Month.MAY, day);
                } else if (month == 6) {
                    dateAfter = LocalDate.of(year, Month.JUNE, day);
                } else if (month == 7) {
                    dateAfter = LocalDate.of(year, Month.JULY, day);
                } else if (month == 8) {
                    dateAfter = LocalDate.of(year, Month.AUGUST, day);
                } else if (month == 9) {
                    dateAfter = LocalDate.of(year, Month.SEPTEMBER, day);
                } else if (month == 10) {
                    dateAfter = LocalDate.of(year, Month.OCTOBER, day);
                } else if (month == 11) {
                    dateAfter = LocalDate.of(year, Month.NOVEMBER, day);
                } else if (month == 12) {
                    dateAfter = LocalDate.of(year, Month.DECEMBER, day);
                }

                long chronoDays = ChronoUnit.DAYS.between(dateBefore,dateAfter);
                chronoDays = Integer.valueOf(String.valueOf(chronoDays));
                times = chronoDays / e.getRepeatNum();

                if (e.getRepeatPeriod().equals("Week(s)")) {
                    times = (times / 7) + 1;
                    times = (int) times;
                } else {
                    if ((e.getRepeatNum() * times) != daysBetween(cal1.getTime(), cal2.getTime())) {
                        times = times + 1;
                    }
                }
                add = e.getMoney() * times;
            }

            if (e.getRepeatPeriod().equals("Month(s)")) {
                if (year > eYear) {
                    times += 12 * (year - eYear);
                }
                if (month > eMonth) {
                    times += month - eMonth + 1;
                }
                if (eDay > day) {
                    times -= 1;
                }

                add = e.getMoney() * times;
            }

            if (e.getRepeatPeriod().equals("Year(s)")) {
                times = year - eYear + 1;
                if (eMonth > month) {
                    times -= 1;
                }
                add = e.getMoney() * times;
            }

            return(add);
        }
        return(0.0);
    }

    public ArrayList<Double> getTotalBalances(Account account, int day, int month, int year) {
        month = month + 1;
        // total balance, total income, then total outgoing
        ArrayList<Double> totalBalances = new ArrayList<>();
        totalBalances.add(0.0);
        totalBalances.add(0.0);
        totalBalances.add(0.0);
        ArrayList<Event> shortEvents = account.getShortEvents();
        for (Event e: shortEvents) {
            // Only edits money if date is less than or equal to given date
            if (isBefore(e.getDay(), e.getMonth()+1, e.getYear(), day, month, year)) {
                if (e.isIncome()) {
                    totalBalances.set(1, totalBalances.get(1) + e.getMoney());
                } else {
                    totalBalances.set(2, totalBalances.get(2) + e.getMoney());
                }
            }
        }

        ArrayList<Event> longEvents = account.getLongEvents();
        for (Event e: longEvents) {
            // Only edits money if date is less than or equal to given date
            double add = getTotalAmount(e,e.getDay(),e.getMonth(),e.getYear(),day,month,year);

            if (e.isIncome()) {
                totalBalances.set(1, totalBalances.get(1) + add);
            } else {
                totalBalances.set(2, totalBalances.get(2) + add);
            }

        }

        totalBalances.set(0, totalBalances.get(1) - totalBalances.get(2));
        return totalBalances;
    }

    public void ChangeBudget(String timePeriod, int timeDuration, double money, int accountId, int budgetDay, int budgetMonth, int budgetYear) {

        ArrayList<Account> accounts = getAccounts();
        int id = accountId;

        Account toChangeAccount = null;
        if (null != accounts) {
            for (Account a: accounts) {
                if (a.getId() == id) {
                    toChangeAccount = a;
                }
            }
        }

        int position = accounts.indexOf(toChangeAccount);
        toChangeAccount.setBudgetAmount(money);
        toChangeAccount.setBudgetDuration(timeDuration);
        toChangeAccount.setBudgetTimePeriod(timePeriod);
        toChangeAccount.setBudgetDay(budgetDay);
        toChangeAccount.setBudgetMonth(budgetMonth);
        toChangeAccount.setBudgetYear(budgetYear);

        accounts.set(position, toChangeAccount);

        // Updates Shared Preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString(ALL_ACCOUNTS_KEY, gson.toJson(accounts));
        editor.commit();
    }

    public ArrayList<String> getPossibleIncomes() {
        return possibleIncomes;
    }

    public void setPossibleIncomes(ArrayList<String> possibleIncomes) {
        this.possibleIncomes = possibleIncomes;
    }

    public ArrayList<String> getPossibleOutgoings() {
        return possibleOutgoings;
    }

    public void setPossibleOutgoings(ArrayList<String> possibleOutgoings) {
        this.possibleOutgoings = possibleOutgoings;
    }

    public int daysBetween(Date d1, Date d2) {
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24) + 1);
    }

}
