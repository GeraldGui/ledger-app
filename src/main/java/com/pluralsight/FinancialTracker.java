package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    private static final String GOLD = "\u001B[1m\u001B[33m";
    private static final String RESET = "\u001B[0m";

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

    public static void loadTransactions(String fileName) {
        try {
            // Checks if the file exist
            File fileExist = new File(fileName);

            if (fileExist.exists()) {
                // Read the file with fileReader in the BufferReader
                BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));

                String line;

                // Use while loop to use line to read each line until there is no more to read
                while ((line = bufferedReader.readLine()) != null) {

                    if (line.isEmpty()) continue;

                    // Creates an array called tokens to then split the line from \\| then stores them into tokens
                    String[] tokens = line.split("\\|");

                    if (tokens.length != 5) continue;

                    // Getting the tokens and putting them into the new variables
                    LocalDate date = parseDate(tokens[0]);
                    LocalTime time = LocalTime.parse(tokens[1], TIME_FMT);
                    String description = tokens[2];
                    String vendor = tokens[3];
                    Double amount = parseDouble(tokens[4]);

                    // Creating the new objects for each line there is
                    if (date != null && amount != null) {
                        transactions.add(new Transaction(date, time, description, vendor, amount));
                    } else {
                        System.out.println("Skipping invalid transaction.");
                    }
                }
                // Closes the writer
                bufferedReader.close();
            } else {
                // Creates a new file
                fileExist.createNewFile();

            }

        } catch (IOException e) {
            System.out.println("File not reading!");
        }
    }

    /* ------------------------------------------------------------------
       Add new transactions
       ------------------------------------------------------------------ */

    private static void addDeposit(Scanner scanner) {
        Transaction deposit = getTransactionsInput(scanner, false);

        transactions.add(deposit);
        saveTransactions(deposit);

        System.out.println("Deposit recorded!");
    }

    private static void addPayment(Scanner scanner) {
        Transaction payment = getTransactionsInput(scanner, true);

        transactions.add(payment);
        saveTransactions(payment);

        System.out.println("Payment recorded!");
    }

    /* ------------------------------------------------------------------
       Ledger menu
       ------------------------------------------------------------------ */
    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("\nLedger");
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
        printLedgerHeaderAndSort();
        for (Transaction displayAll : transactions) {
            System.out.println(displayAll);
        }
    }

    private static void displayDeposits() {
        printLedgerHeaderAndSort();
        for (Transaction displayPositive : transactions) {
            if (displayPositive.getAmount() > 0) {
                System.out.println(displayPositive);
            }
        }
    }

    private static void displayPayments() {
        printLedgerHeaderAndSort();
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
            System.out.println("\nReports");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back");

            String input = scanner.nextLine().trim();
            LocalDate today = LocalDate.now();

            switch (input) {
                case "1" -> {
                    // Month to Date
                    LocalDate start = today.withDayOfMonth(1);
                    LocalDate end = today;

                    printLedgerHeaderAndSort();
                    filterTransactionsByDate(start, end);
                }
                case "2" -> {

                    // Previous Month
                    LocalDate start = today.minusMonths(1).withDayOfMonth(1);
                    LocalDate end = today.withDayOfMonth(1).minusDays(1);

                    printLedgerHeaderAndSort();
                    filterTransactionsByDate(start, end);
                }
                case "3" -> {

                    // Year to Date
                    LocalDate start = today.withDayOfYear(1);
                    LocalDate end = today;

                    printLedgerHeaderAndSort();
                    filterTransactionsByDate(start, end);
                }
                case "4" -> {

                    // Previous Year
                    LocalDate start = today.minusYears(1).withDayOfYear(1);
                    LocalDate end = today.withDayOfYear(1).minusDays(1);

                    printLedgerHeaderAndSort();
                    filterTransactionsByDate(start, end);
                }
                case "5" -> {
                    System.out.print("Vendor name: ");
                    String userInput = scanner.nextLine();

                    printLedgerHeaderAndSort();
                    filterTransactionsByVendor(userInput);

                }
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
        for (Transaction monthToDate : transactions) {
            if (!monthToDate.getDate().isBefore(start) && !monthToDate.getDate().isAfter(end)) {
                System.out.println(monthToDate);
            }
        }

    }

    private static void filterTransactionsByVendor(String vendor) {
        for (Transaction searchVendor : transactions) {
            // if statement that gets vendor data and then checks if that is equals to the userInput
            if (searchVendor.getVendor().equalsIgnoreCase(vendor)) {
                System.out.println(searchVendor);
            }
        }
    }

    private static void customSearch(Scanner scanner) {

        System.out.print("Start date  (yyyy-MM-dd, blank = none): ");
        String startDate = scanner.nextLine();

        System.out.print("End date    (yyyy-MM-dd, blank = none): ");
        String endDate = scanner.nextLine();

        System.out.print("Description (blank = none): ");
        String description = scanner.nextLine();

        System.out.print("Vendor      (blank = none): ");
        String vendor = scanner.nextLine();

        System.out.print("Amount      (blank = none): ");
        String amount = scanner.nextLine();
        Double parseAmount = parseDouble(amount);

        printLedgerHeaderAndSort();

        LocalDate parseStartDate = parseDate(startDate);
        LocalDate parseEndDate = parseDate(endDate);

        for (Transaction customSearch : transactions) {
            // Stores all the data in the variables
            String aboutDescription = customSearch.getDescription();
            String whoIsVendor = customSearch.getVendor();
            double money = customSearch.getAmount();

            boolean matches = true;

            // Checks if not blank and if data is before userInput date
            if (parseStartDate != null && customSearch.getDate().isBefore(parseStartDate)) {
                matches = false;
            }
            // Checks if not blank and if data is after userInput date
            if (parseEndDate != null && customSearch.getDate().isAfter(parseEndDate)) {
                matches = false;
            }
            // Checks if not empty and if data is not equal to userInput description
            if (!description.isEmpty() && !aboutDescription.equalsIgnoreCase(description)) {
                matches = false;
            }
            // Checks if not empty and if data is not equal to userInput vendor
            if (!vendor.isEmpty() && !whoIsVendor.equalsIgnoreCase(vendor)) {
                matches = false;
            }
            // Checks if not empty and amount is the same
            if (!amount.isEmpty() && parseAmount != null && Math.abs(money - parseAmount) > 0.001) {
                matches = false;
            }
            // Checks all that match then prints it out
            if (matches) {
                System.out.println(customSearch);
            }
        }
    }

    /* ------------------------------------------------------------------
       Utility parsers (you can reuse in many places)
       ------------------------------------------------------------------ */
    private static LocalDate parseDate(String s) {
        try {
            LocalDate dateTime = LocalDate.parse(s, DATE_FMT);
            return dateTime;
        } catch (Exception e) {
            return null;
        }
    }


    private static Double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static void printLedgerHeaderAndSort() {
        System.out.println(String.format(GOLD + "%-10s" + RESET + "|" + GOLD + "%-10s" + RESET + "|" + GOLD + "%-30s" + RESET + "|" + GOLD + "%-20s" + RESET + "|" + GOLD + "%-6s" + RESET, "Date", "Time", "Description", "Vendor", "Amount"));
        System.out.println("--------------------------------------------------------------------------------------");

        // Sort Transaction from the dates, newest to oldest by reversing it.
        Collections.sort(transactions, Comparator.comparing(Transaction::getDate).thenComparing(Transaction::getTime).reversed());
    }

    private static Transaction getTransactionsInput(Scanner scanner, boolean inputAmount) {
        LocalDate date;
        LocalTime time;
        String description;
        String vendor;
        Double amount;

        while (true) {
            System.out.print("Date & time (yyyy-MM-dd HH:mm:ss): ");
            String input = scanner.nextLine();

            try {
                // Got the format for the date and then separated them into their own variables
                LocalDateTime dateTimeFMT = LocalDateTime.parse(input, DATETIME_FMT);
                date = dateTimeFMT.toLocalDate();
                time = dateTimeFMT.toLocalTime();
                break;
            } catch (Exception e) {
                System.out.println("Wrong Date and Time!\n");
            }
        }
        do {
            System.out.print("Description: ");
            description = scanner.nextLine();
        } while (description.isEmpty());

        do {
            System.out.print("Vendor: ");
            vendor = scanner.nextLine();
        } while (vendor.isEmpty());

        while (true) {
            System.out.print("Amount (positive): ");
            String input = scanner.nextLine();

            amount = parseDouble(input);

            if (amount != null  && amount > 0) {
                break;
            } else {
                System.out.println("Invalid amount");
            }
        }
        if (inputAmount) {
            amount = -amount;
        }
        return new Transaction(date, time, description, vendor, amount);
    }

    private static void saveTransactions(Transaction saveObject) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            bufferedWriter.write(saveObject.getDate() + "|" + saveObject.getTime() + "|" + saveObject.getDescription() + "|" + saveObject.getVendor() + "|"
            + saveObject.getAmount());
            bufferedWriter.newLine();
        } catch (Exception e) {
            System.out.println("Couldn't save transaction!");
        }
    }
}