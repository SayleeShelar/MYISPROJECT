public class Account {
    private final String accountNumber;
    private String accountHolderName;
    private final String username;
    private double balance;

    public Account(String accountNumber, String accountHolderName, String username, double balance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.username = username;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getUsername() {
        return username;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public void withdraw(double amount) {
        if (balance >= amount) {
            balance -= amount;
        } else {
            System.out.println("Insufficient balance.");
        }
    }

    public void transfer(Account toAccount, double amount) {
        if (balance >= amount) {
            withdraw(amount);
            toAccount.deposit(amount);
        } else {
            System.out.println("Insufficient balance.");
        }
    }
}
