package com.example.myuniversityexpensetracker;

import java.util.ArrayList;

public class Event {

    private int id, day, month, year, repeatNum;
    private String category, description, eventName, repeatPeriod;
    private boolean income, longTerm, isExpanded;
    private double money;

    public Event(int id, int day, int month, int year, String category, String eventName, boolean income, double money, String description, boolean longTerm, int repeatNum, String repeatPeriod) {
        this.id = id;
        this.day = day;
        this.month = month;
        this.year = year;
        this.eventName = eventName;
        this.category = category;
        this.income = income;
        this.money = money;
        this.description = description;
        this.longTerm = longTerm;
        this.repeatNum = repeatNum;
        this.repeatPeriod = repeatPeriod;
        isExpanded = false;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public boolean isLongTerm() {
        return longTerm;
    }

    public void setLongTerm(boolean longTerm) {
        this.longTerm = longTerm;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isIncome() {
        return income;
    }

    public void setIncome(boolean income) {
        this.income = income;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRepeatNum() {
        return repeatNum;
    }

    public void setRepeatNum(int repeatNum) {
        this.repeatNum = repeatNum;
    }

    public String getRepeatPeriod() {
        return repeatPeriod;
    }

    public void setRepeatPeriod(String repeatPeriod) {
        this.repeatPeriod = repeatPeriod;
    }
}
