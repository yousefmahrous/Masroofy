/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author ahayf
 */
import java.nio.charset.StandardCharsets; // for getBytes() to make it standard on any system
import java.security.MessageDigest; // for SHA
import java.security.NoSuchAlgorithmException; // handle exception
 
public class AuthManager {
 
    private String storedHash;
 
    public AuthManager(String storedHash) {
        this.storedHash = storedHash;
    }
 
    // Hash a PIN using SHA-256
    private String hash(int pin) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");// implement SHA
            // byte array , imports the byte array into the hash, int to string, convert bytes to string 
            byte[] bytes = md.digest(String.valueOf(pin).getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(); // concatenates strings in loop 
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString(); // convert to string
        } catch (NoSuchAlgorithmException e) { // must have atleast 1 catch
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
 
    // Returns true if the PIN matches the stored hash
    public boolean authenticate(int pin) {
        return hash(pin).equals(storedHash);
    }
 
    // Update the stored PIN hash
    public void changePIN(int newPin) {
        this.storedHash = hash(newPin);
        System.out.println("PIN updated successfully.");
    }
 
    public String getStoredHash() {
        return storedHash;
    }
}
