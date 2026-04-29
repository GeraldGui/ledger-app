package com.pluralsight;

import java.time.LocalDate;
import java.time.LocalTime;

public class Transaction {
    private LocalDate date;
    private LocalTime time;
    private String description;
    private String vendor;
    private double amount;

    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[36m";
    private static final String RESET = "\u001B[0m";

    public Transaction(LocalDate date, LocalTime time, String description, String vendor, double amount) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        if (amount > 0) {
            return String.format(BLUE + "%-10s" + RESET + "|" + BLUE + "%-10s" + RESET + "|" + BLUE + "%-30s" + RESET + "|" + BLUE + " %-20s" + RESET + "|" + GREEN + "%-6s" + RESET, date, time, description, vendor, amount);
        } else {
            return String.format(BLUE + "%-10s" + RESET + "|" + BLUE + "%-10s" + RESET + "|" + BLUE + "%-30s" + RESET + "|" + BLUE + " %-20s" + RESET + "|" + RED + "%-6s" + RESET, date, time, description, vendor, amount);
        }
    }
}
