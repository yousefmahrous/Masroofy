package model;

import java.util.ArrayList;

public class Category {
    private int id;
    private String name;
    private double budgetLimit;
    private double currentSpent;
    private ArrayList<Transaction> transactions = new ArrayList();

    public Category(String name, double budgetLimit) {
        this.name = name;
        this.budgetLimit = budgetLimit;
        this.currentSpent = 0.0;
    }

    public Category(int id, String name, double budgetLimit, double currentSpent) {
        this.id = id;
        this.name = name;
        this.budgetLimit = budgetLimit;
        this.currentSpent = currentSpent;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getBudgetLimit() {
        return budgetLimit;
    }

    public double getCurrentSpent() {
        return currentSpent;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBudgetLimit(double budgetLimit) {
        this.budgetLimit = budgetLimit;
    }

    public void setCurrentSpent(double currentSpent) {
        this.currentSpent = currentSpent;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }
}