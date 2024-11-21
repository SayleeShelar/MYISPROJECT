import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class BankingSystem {
    private final List<Account> accounts;
    private final Map<String, User> users;
    private User loggedInUser; // Track the currently logged-in user

    public BankingSystem() {
        this.accounts = new ArrayList<>();
        this.users = new HashMap<>();
        users.put("admin", new User("admin", "password123", Role.ADMIN));
        users.put("customer1", new User("customer1", "password123", Role.CUSTOMER));
    }

    public boolean authenticate(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            loggedInUser = user; // Set the logged-in user
            return true;
        }
        return false;
    }

    public void readAccountsFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true; // Skip the header line
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length != 4) {
                    System.out.println("Skipping line due to invalid format: " + line);
                    continue;
                }
                String accountNumber = parts[0].trim();
                String accountHolderName = parts[1].trim();
                String username = parts[2].trim(); // Read the username
                double balance;
                try {
                    balance = Double.parseDouble(parts[3].trim());
                } catch (NumberFormatException e) {
                    System.out.println("Skipping line due to invalid balance format: " + line);
                    continue;
                }
                Account account = new Account(accountNumber, accountHolderName, username, balance);
                accounts.add(account);
                // Debugging print statement
                System.out.println("Loaded account: " + accountNumber + ", Holder: " + accountHolderName + ", Username: " + username + ", Balance: " + balance);
            }
        }
    }

    public void saveAccountsToFile(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("accountNumber,accountHolderName,username,balance\n"); // CSV header
            for (Account account : accounts) {
                writer.write(account.getAccountNumber() + "," +
                             account.getAccountHolderName() + "," +
                             account.getUsername() + "," +
                             account.getBalance() + "\n");
            }
        }
    }

    public void listAllAccounts() {
        for (Account account : accounts) {
            System.out.println(account.getAccountNumber() + " | " + account.getAccountHolderName() + " | " + account.getBalance());
        }
    }

    public void closeAccount(String accountNumber) {
        if (loggedInUser.getRole() == Role.ADMIN) {
            Account account = findAccount(accountNumber);
            if (account != null) {
                accounts.remove(account);
                System.out.println("Closed account " + accountNumber);
                try {
                    saveAccountsToFile("accounts.csv");
                } catch (IOException e) {
                    System.out.println("Error saving accounts file: " + e.getMessage());
                }
            } else {
                System.out.println("Account not found");
            }
        } else {
            System.out.println("Unauthorized: Only admin can close accounts");
        }
    }

    public void updateAccount(String accountNumber, String newAccountHolderName) {
        if (loggedInUser.getRole() == Role.ADMIN) {
            Account account = findAccount(accountNumber);
            if (account != null) {
                account.setAccountHolderName(newAccountHolderName);
                System.out.println("Updated account holder name for account " + accountNumber);
                try {
                    saveAccountsToFile("accounts.csv");
                } catch (IOException e) {
                    System.out.println("Error saving accounts file: " + e.getMessage());
                }
            } else {
                System.out.println("Account not found");
            }
        } else {
            System.out.println("Unauthorized: Only admin can update account holder names");
        }
    }

    public void deposit(String accountNumber, double amount) {
        Account account = findAccount(accountNumber);
        if (account != null && canAccessAccount(account)) {
            account.deposit(amount);
            System.out.println("Deposited " + amount + " to account " + accountNumber);
            try {
                saveAccountsToFile("accounts.csv");
            } catch (IOException e) {
                System.out.println("Error saving accounts file: " + e.getMessage());
            }
        } else {
            System.out.println("Unauthorized or account not found");
        }
    }

    public void withdraw(String accountNumber, double amount) {
        Account account = findAccount(accountNumber);
        if (account != null && canAccessAccount(account)) {
            account.withdraw(amount);
            System.out.println("Withdrew " + amount + " from account " + accountNumber);
            try {
                saveAccountsToFile("accounts.csv");
            } catch (IOException e) {
                System.out.println("Error saving accounts file: " + e.getMessage());
            }
        } else {
            System.out.println("Unauthorized or account not found");
        }
    }

    public void transfer(String fromAccountNumber, String toAccountNumber, double amount) {
        Account fromAccount = findAccount(fromAccountNumber);
        Account toAccount = findAccount(toAccountNumber);
        if (fromAccount != null && toAccount != null && canAccessAccount(fromAccount)) {
            fromAccount.transfer(toAccount, amount);
            System.out.println("Transferred " + amount + " from account " + fromAccountNumber + " to account " + toAccountNumber);
            try {
                saveAccountsToFile("accounts.csv");
            } catch (IOException e) {
                System.out.println("Error saving accounts file: " + e.getMessage());
            }
        } else {
            System.out.println("Unauthorized or account not found");
        }
    }

    private Account findAccount(String accountNumber) {
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    public boolean canAccessAccount(Account account) {
        return account.getUsername().equals(loggedInUser.getUsername());
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public static void main(String[] args) {
        BankingSystem bankingSystem = new BankingSystem();

        // Add a simple login check
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter username:");
            String username = scanner.nextLine();
            System.out.println("Enter password:");
            String password = scanner.nextLine();

            if (!bankingSystem.authenticate(username, password)) {
                System.out.println("Authentication failed. Exiting.");
                return;
            }
        }

        try {
            bankingSystem.readAccountsFromFile("accounts.csv");
        } catch (IOException e) {
            System.out.println("Error reading accounts file: " + e.getMessage());
            return;
        }

        bankingSystem.listAllAccounts();

        // Example operations
        bankingSystem.updateAccount("1001", "New John Doe");
        bankingSystem.closeAccount("1002");
        bankingSystem.deposit("1001", 1000.00);
        bankingSystem.withdraw("1001", 500.00);
        bankingSystem.transfer("1001", "1002", 500.00);
    }
}
