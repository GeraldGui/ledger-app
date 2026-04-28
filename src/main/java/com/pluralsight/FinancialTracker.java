package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/*
 * Capstone skeleton – personal finance tracker.
 * ------------------------------------------------
 * File format  (pipe-delimited)
 *     yyyy-MM-dd|HH:mm:ss|description|vendor|amount
 * A deposit has a positive amount; a payment is stored
 * as a negative amount.
 */
public class FinancialTracker {

    /* ------------------------------------------------------------------
       Shared data and formatters
       ------------------------------------------------------------------ */
    private static final ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern(TIME_PATTERN);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    /* ------------------------------------------------------------------
       Main menu
       ------------------------------------------------------------------ */
    public static void main(String[] args) {
        loadTransactions(FILE_NAME);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\nWelcome to TransactionApp");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "D" -> addDeposit(scanner);
                case "P" -> addPayment(scanner);
                case "L" -> ledgerMenu(scanner);
                case "X" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
        scanner.close();
    }

    /* ------------------------------------------------------------------
       File I/O
       ------------------------------------------------------------------ */

    /**
     * Load transactions from FILE_NAME.
     * • If the file doesn’t exist, create an empty one so that future writes succeed.
     * • Each line looks like: date|time|description|vendor|amount
     */
    public static void loadTransactions(String fileName) {
        // TODO: create file if it does not exist, then read each line,
        //       parse the five fields, build a Transaction object,
        //       and add it to the transactions list.

        try {
            // Checks if the file exist
            File fileExist = new File(fileName);

            if (fileExist.exists()) {
                // Read the file with fileReader in the BufferReader
                FileReader fileReader = new FileReader(fileName);
                BufferedReader bufferedReader = new BufferedReader(fileReader);

                String line;

                // Use while loop to use line to read each line until there is no more to read
                while ((line = bufferedReader.readLine()) != null) {

                    // Creates an array called tokens to then split the line from \\| then stores them into tokens
                    List<String> tokens = new ArrayList<>(Arrays.asList(line.split("\\|")));

                    // Getting the tokens and putting them into the new variables
                    LocalDate date = parseDate(tokens.get(0));
                    LocalTime time = LocalTime.parse(tokens.get(1));
                    String description = tokens.get(2);
                    String vendor = tokens.get(3);
                    double amount = Double.parseDouble(tokens.get(4));

                    // Creating the new objects for each line there is
                    transactions.add(new Transaction(date, time, description, vendor, amount));
                }
                // Closes the writer
                bufferedReader.close();
            } else {
                // Creates a new file
                fileExist.createNewFile();

            }

        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* ------------------------------------------------------------------
       Add new transactions
       ------------------------------------------------------------------ */

    /**
     * Prompt for ONE date+time string in the format
     * "yyyy-MM-dd HH:mm:ss", plus description, vendor, amount.
     * Validate that the amount entered is positive.
     * Store the amount as-is (positive) and append to the file.
     */
    private static void addDeposit(Scanner scanner) {
        try {
            // TODO
            // Writes in the file, made sure to make it append in the file
            FileWriter fileWriter = new FileWriter(FILE_NAME, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            boolean running = false;

            // Loop will continue to run as long as it is true
            while (!running) {
            System.out.print("Date & time (yyyy-MM-dd HH:mm:ss): ");
            String dateTime = scanner.nextLine();

            System.out.print("Description: ");
            String description = scanner.nextLine();

            System.out.print("Vendor: ");
            String vendor = scanner.nextLine();

            System.out.print("Amount (positive): ");
            double amount = scanner.nextDouble();
            scanner.nextLine();

            // Checks if it is a positive number
                if (amount > 0) {
                    LocalDate date;
                    LocalTime time;
                    try {
                        // Got the format for the date and then separated them into their own variables
                        LocalDateTime dateTimeFMT = LocalDateTime.parse(dateTime, DATETIME_FMT);
                        date = dateTimeFMT.toLocalDate();
                        time = dateTimeFMT.toLocalTime();
                    } catch (Exception e) {
                        System.out.println("Wrong Date and Time!\n");
                        continue;
                    }

                    // Adds the object to the memory and then appends the variables into the file
                    transactions.add(new Transaction(date, time, description, vendor, amount));

                    bufferedWriter.append(DATE_FMT.format(date)).append("|").append(TIME_FMT.format(time)).append("|").append(description).append("|").append(vendor).append("|").append(String.valueOf(amount)).append("\n");

                    System.out.print("Deposit recorded.\n\n");

                    // Makes running true to close the while loop
                    running = true;
                } else {
                    System.out.println("Invalid Amount!");
                    System.out.print("Would you like to try again? (Y/N): ");
                    String stillContinue = scanner.nextLine();

                    if (stillContinue.equalsIgnoreCase("y")) {
                        System.out.println("Enter your info again.");
                    } else if (stillContinue.equalsIgnoreCase("n")){
                        System.out.println("Thank you, sending you back to Menu.");
                        running = true;
                    }
                    else {
                        System.out.println("Invalid Input!");
                    }
                }
            }
            // Closes the writer
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Same prompts as addDeposit.
     * Amount must be entered as a positive number,
     * then converted to a negative amount before storing.
     */
    private static void addPayment(Scanner scanner) {
        try {
            // TODO
            // Writes in the file, made sure to make it append in the file
            FileWriter fileWriter = new FileWriter(FILE_NAME, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            boolean running = false;

            // Loop will continue to run as long as it is true
            while (!running) {
                System.out.print("Date & time (yyyy-MM-dd HH:mm:ss): ");
                String dateTime = scanner.nextLine();

                System.out.print("Description: ");
                String description = scanner.nextLine();

                System.out.print("Vendor: ");
                String vendor = scanner.nextLine();

                System.out.print("Amount (positive): ");
                double amount = scanner.nextDouble();
                scanner.nextLine();

                // Checks if it is a positive number
                if (amount > 0) {
                    LocalDate date;
                    LocalTime time;
                    try {
                        // Got the format for the date and then separated them into their own variables
                        LocalDateTime dateTimeFMT = LocalDateTime.parse(dateTime, DATETIME_FMT);
                        date = dateTimeFMT.toLocalDate();
                        time = dateTimeFMT.toLocalTime();
                    } catch (Exception e) {
                        System.out.println("Wrong Date and Time!\n");
                        continue;
                    }

                    // Adds the object to the memory and then appends the variables into the file
                    transactions.add(new Transaction(date, time, description, vendor, amount));

                    bufferedWriter.append(DATE_FMT.format(date)).append("|").append(TIME_FMT.format(time)).append("|").append(description).append("|").append(vendor).append("|-").append(String.valueOf(amount)).append("\n");

                    System.out.print("Payment recorded.\n\n");

                    // Makes running true to close the while loop
                    running = true;
                } else {
                    System.out.println("Invalid Amount!");
                    System.out.print("Would you like to try again? (Y/N): ");
                    String stillContinue = scanner.nextLine();

                    if (stillContinue.equalsIgnoreCase("y")) {
                        System.out.println("Enter your info again.");
                    } else if (stillContinue.equalsIgnoreCase("n")){
                        System.out.println("Thank you, sending you back to Menu.");
                        running = true;
                    }
                    else {
                        System.out.println("Invalid Input!");
                    }
                }
            }
            // Closes the writer
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /* ------------------------------------------------------------------
       Ledger menu
       ------------------------------------------------------------------ */
    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Ledger");
            System.out.println("Choose an option:");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "A" -> displayLedger();
                case "D" -> displayDeposits();
                case "P" -> displayPayments();
                case "R" -> reportsMenu(scanner);
                case "H" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ------------------------------------------------------------------
       Display helpers: show data in neat columns
       ------------------------------------------------------------------ */
    private static void displayLedger() {
        /* TODO – print all transactions in column format */
        System.out.println(String.format("%-10s| %-10s| %-30s| %-20s| %-6s" ,"Date", "Time", "Description", "Vendor", "Amount"));
        System.out.println("--------------------------------------------------------------------------------------" );
        for (Transaction displayAll : transactions) {
            System.out.println(displayAll);
        }
    }

    private static void displayDeposits() {
        /* TODO – only amount > 0               */
        System.out.println(String.format("%-10s| %-10s| %-30s| %-20s| %-6s" ,"Date", "Time", "Description", "Vendor", "Amount"));
        System.out.println("--------------------------------------------------------------------------------------" );
        for (Transaction displayPositive : transactions) {
            if (displayPositive.getAmount() > 0) {
                System.out.println(displayPositive);
            }
        }
    }

    private static void displayPayments() {
        /* TODO – only amount < 0               */
        System.out.println(String.format("%-10s| %-10s| %-30s| %-20s| %-6s" ,"Date", "Time", "Description", "Vendor", "Amount"));
        System.out.println("--------------------------------------------------------------------------------------" );
        for (Transaction displayNegative : transactions) {
            if (displayNegative.getAmount() < 0) {
                System.out.println(displayNegative);
            }
        }
    }

    /* ------------------------------------------------------------------
       Reports menu
       ------------------------------------------------------------------ */
    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Reports");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> {
                    /* TODO – month-to-date report */
                    LocalDate today = LocalDate.now();

                    System.out.println(String.format("%-10s| %-10s| %-30s| %-20s| %-6s" ,"Date", "Time", "Description", "Vendor", "Amount"));
                    System.out.println("--------------------------------------------------------------------------------------" );
                    for (Transaction monthToDate : transactions) {
                        // if statement checking for the data month is equal to today month and data year then equals to today year.
                        if (monthToDate.getDate().getMonth() == today.getMonth() && monthToDate.getDate().getYear() == today.getYear()) {
                            System.out.println(monthToDate);
                        }
                    }
                }
                case "2" -> {
                    /* TODO – previous month report */
                    LocalDate today = LocalDate.now();

                    System.out.println(String.format("%-10s| %-10s| %-30s| %-20s| %-6s" ,"Date", "Time", "Description", "Vendor", "Amount"));
                    System.out.println("--------------------------------------------------------------------------------------" );
                    for (Transaction previousMonth : transactions) {
                        // if statement checking for the data month number is less than today month number
                        if (previousMonth.getDate().getMonthValue() < today.getMonthValue()) {
                            System.out.println(previousMonth);
                        }
                    }
                }
                case "3" -> {
                    /* TODO – year-to-date report   */
                    LocalDate today = LocalDate.now();

                    System.out.println(String.format("%-10s| %-10s| %-30s| %-20s| %-6s" ,"Date", "Time", "Description", "Vendor", "Amount"));
                    System.out.println("--------------------------------------------------------------------------------------" );
                    for (Transaction yearToDate : transactions) {
                        // if statement checking for the data year is equal to today year
                        if (yearToDate.getDate().getYear() == today.getYear()) {
                            System.out.println(yearToDate);
                        }
                    }
                }
                case "4" -> {
                    /* TODO – previous year report  */
                    LocalDate today = LocalDate.now();

                    System.out.println(String.format("%-10s| %-10s| %-30s| %-20s| %-6s" ,"Date", "Time", "Description", "Vendor", "Amount"));
                    System.out.println("--------------------------------------------------------------------------------------" );
                    for (Transaction previousYear : transactions) {
                        // if statement checking for the data year is less than today year
                        if (previousYear.getDate().getYear() < today.getYear()) {
                            System.out.println(previousYear);
                        }
                    }
                }
                case "5" -> {/* TODO – prompt for vendor then report */ }
                case "6" -> customSearch(scanner);
                case "0" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ------------------------------------------------------------------
       Reporting helpers
       ------------------------------------------------------------------ */
    private static void filterTransactionsByDate(LocalDate start, LocalDate end) {
        // TODO – iterate transactions, print those within the range
    }

    private static void filterTransactionsByVendor(String vendor) {
        // TODO – iterate transactions, print those with matching vendor
    }

    private static void customSearch(Scanner scanner) {
        // TODO – prompt for any combination of date range, description,
        //        vendor, and exact amount, then display matches
    }

    /* ------------------------------------------------------------------
       Utility parsers (you can reuse in many places)
       ------------------------------------------------------------------ */
    private static LocalDate parseDate(String s) {
        /* TODO – return LocalDate or null */

        try {
            LocalDate dateTime = LocalDate.parse(s, DATE_FMT);
            return dateTime;
        } catch (Exception e) {
            return null;
        }
    }

    private static Double parseDouble(String s) {
        /* TODO – return Double   or null */
        return null;
    }
}
