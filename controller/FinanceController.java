/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author ahayf
 */
import DataAccess.IDatabase;
import model.*;
 
import java.time.LocalDate; // represents a date without timezone
import java.time.temporal.ChronoUnit; // date units for calculations
import java.util.List;
import java.util.stream.Collectors;
 
public class FinanceController {
 
    private final IDatabase db;
    private final Notificationmanager notificationManager;
 
    public FinanceController(IDatabase db, Notificationmanager notificationManager) {
        this.db = db;
        this.notificationManager = notificationManager;
    }
 
    // Add a new transaction and update the budget cycle's current spent
    public void addTransaction(Transaction t) {
        db.insert("transactions", t);
 
        // Update current_spent on the budget cycle
        List<Object> cycles = db.query("SELECT * FROM budget_cycle WHERE id = " + t.getBudgetCycleId());
        if (!cycles.isEmpty()) {
            BudgetCycle cycle = (BudgetCycle) cycles.get(0);
            if (t.getType() == TransactionType.EXPENSE) {
                cycle.setCurrentSpent(cycle.getCurrentSpent() + t.getAmount());
            } else {
                cycle.setTotalAllowance(cycle.getTotalAllowance() + t.getAmount());
            }
            db.update("budget_cycle", cycle.getId(), cycle);
            checkThresholds(cycle.getCurrentSpent(), cycle.getTotalAllowance());
        }
 
        // Update current_spent on the category
        List<Object> categories = db.query("SELECT * FROM categories WHERE id = " + t.getCategoryId());
        if (!categories.isEmpty()) {
            Category cat = (Category) categories.get(0);
            if (t.getType() == TransactionType.EXPENSE) {
                cat.setCurrentSpent(cat.getCurrentSpent() + t.getAmount());
            }
            db.update("categories", cat.getId(), cat);
            notificationManager.checkCategoryLimit(cat.getCurrentSpent(), cat.getBudgetLimit());
        }
    }
 
    // Calculate how much can be spent per day given end date and remaining budget
    public double calculateDailyLimit(BudgetCycle cycle) {
        long daysLeft = getRemainingDays(cycle.getEndDate());
        if (daysLeft <= 0) return 0;
        double remaining = cycle.getTotalAllowance() - cycle.getCurrentSpent();
        return remaining / daysLeft;
    }
 
    // How many days until the budget cycle ends
    public int getRemainingDays(LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(LocalDate.now(), endDate);
        return (int) Math.max(days, 0);
    }
 
    // Warn user if spending crosses the threshold percentage (e.g. 80%)
    public void checkThresholds(double spent, double total) {
        if (total <= 0) return;
        double percent = (spent / total) * 100;
        if (percent >= 80) {
            notificationManager.sendAlert(
                String.format("Warning: You have used %.1f%% of your budget!", percent)
            );
        }
    }
 
    // Reset budget cycle: save current one, start a fresh one
    public void resetCurrentCycle(BudgetCycle oldCycle, double newAllowance, LocalDate newEndDate) {
        BudgetCycle newCycle = new BudgetCycle(newAllowance, newEndDate);
        db.insert("budget_cycle", newCycle);
    }
 
    // Filter transactions by category name or type
    public List<Transaction> filterTransactions(String criteria) {
        List<Object> all = db.query("SELECT * FROM transactions");
        return all.stream()
            .map(o -> (Transaction) o)
            .filter(t -> {
                if (t.getCategory() != null && t.getCategory().getName() != null) {
                    if (t.getCategory().getName().equalsIgnoreCase(criteria)) return true;
                }
                return t.getType().name().equalsIgnoreCase(criteria);
            })
            .collect(Collectors.toList());
    }
 
    // Fetch all transactions
    public List<Transaction> getAllTransactions() {
        return db.query("SELECT * FROM transactions")
            .stream().map(o -> (Transaction) o).collect(Collectors.toList());
    }
 
    // Fetch all categories
    public List<Category> getAllCategories() {
        return db.query("SELECT * FROM categories")
            .stream().map(o -> (Category) o).collect(Collectors.toList());
    }
 
    // Fetch the active (latest) budget cycle
    public BudgetCycle getActiveCycle() {
        List<Object> results = db.query("SELECT * FROM budget_cycle ORDER BY id DESC LIMIT 1");
        if (results.isEmpty()) return null;
        return (BudgetCycle) results.get(0);
    }
}
 