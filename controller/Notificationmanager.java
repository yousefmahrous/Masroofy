/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author ahayf
 */
public class Notificationmanager {
    
    // Send a general alert message (prints to console; swap for UI alert later)
    public void sendAlert(String msg) {
        System.out.println("[ALERT] " + msg);
    }

    // Warn if a category's spending is at or over its limit
    public void checkCategoryLimit(double catSpent, double catLimit) {
        if (catLimit <= 0) return;
        double percent = (catSpent / catLimit) * 100;
        if (catSpent >= catLimit) {
            sendAlert("Category budget exceeded! Spent: " + catSpent + " / Limit: " + catLimit);
        } else if (percent >= 80) {
            sendAlert(String.format("Category is at "+ percent + " of its budget limit."));
        }
    }
}