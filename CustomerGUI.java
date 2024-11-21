import java.io.IOException;
import javax.swing.*;

public class CustomerGUI {
    private static BankingSystem bankingSystem;

    public static void main(String[] args) {
        bankingSystem = new BankingSystem();

        // Attempt to read accounts from the CSV file
        try {
            bankingSystem.readAccountsFromFile("accounts.csv");
            System.out.println("Accounts loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error reading accounts file: " + e.getMessage());
        }

        JFrame frame = new JFrame("Customer Banking System");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("User:");
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        JTextField userText = new JTextField(20);
        userText.setBounds(100, 20, 165, 25);
        panel.add(userText);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(100, 50, 165, 25);
        panel.add(passwordText);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(10, 80, 80, 25);
        panel.add(loginButton);

        loginButton.addActionListener(_e -> {
            String username = userText.getText();
            String password = new String(passwordText.getPassword());
            if (bankingSystem.authenticate(username, password)) {
                JOptionPane.showMessageDialog(null, "Login successful!");
                panel.removeAll();
                displayAccountInfo(panel);
                displayCustomerButtons(panel);
                panel.revalidate();
                panel.repaint();
            } else {
                JOptionPane.showMessageDialog(null, "Login failed!");
            }
        });
    }

    private static void displayAccountInfo(JPanel panel) {
        panel.setLayout(null);

        JLabel accountLabel = new JLabel("Accounts:");
        accountLabel.setBounds(10, 20, 80, 25);
        panel.add(accountLabel);

        JTextArea accountArea = new JTextArea(15, 40);
        accountArea.setBounds(100, 20, 450, 250);
        accountArea.setEditable(false);
        accountArea.setLineWrap(true);
        accountArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(accountArea);
        scrollPane.setBounds(100, 20, 450, 250);
        panel.add(scrollPane);

        for (Account account : bankingSystem.getAccounts()) {
            if (bankingSystem.canAccessAccount(account)) {
                accountArea.append("Account Number: " + account.getAccountNumber() +
                                   ", Holder: " + account.getAccountHolderName() +
                                   ", Balance: " + account.getBalance() + "\n");
            }
        }
    }

    private static void displayCustomerButtons(JPanel panel) {
        JButton depositButton = new JButton("Deposit");
        depositButton.setBounds(10, 290, 150, 25);
        panel.add(depositButton);

        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.setBounds(170, 290, 150, 25);
        panel.add(withdrawButton);

        JButton transferButton = new JButton("Transfer");
        transferButton.setBounds(330, 290, 150, 25);
        panel.add(transferButton);

        depositButton.addActionListener(_e -> {
            String accountNumber = JOptionPane.showInputDialog("Enter account number:");
            String amountStr = JOptionPane.showInputDialog("Enter deposit amount:");
            if (accountNumber != null && amountStr != null) {
                try {
                    double amount = Double.parseDouble(amountStr);
                    bankingSystem.deposit(accountNumber.trim(), amount);
                    panel.removeAll();
                    displayAccountInfo(panel);
                    displayCustomerButtons(panel);
                    panel.revalidate();
                    panel.repaint();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid amount entered.");
                }
            }
        });

        withdrawButton.addActionListener(_e -> {
            String accountNumber = JOptionPane.showInputDialog("Enter account number:");
            String amountStr = JOptionPane.showInputDialog("Enter withdrawal amount:");
            if (accountNumber != null && amountStr != null) {
                try {
                    double amount = Double.parseDouble(amountStr);
                    bankingSystem.withdraw(accountNumber.trim(), amount);
                    panel.removeAll();
                    displayAccountInfo(panel);
                    displayCustomerButtons(panel);
                    panel.revalidate();
                    panel.repaint();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid amount entered.");
                }
            }
        });

        transferButton.addActionListener(_e -> {
            String fromAccountNumber = JOptionPane.showInputDialog("Enter your account number:");
            String toAccountNumber = JOptionPane.showInputDialog("Enter recipient account number:");
            String amountStr = JOptionPane.showInputDialog("Enter transfer amount:");
            if (fromAccountNumber != null && toAccountNumber != null && amountStr != null) {
                try {
                    double amount = Double.parseDouble(amountStr);
                    bankingSystem.transfer(fromAccountNumber.trim(), toAccountNumber.trim(), amount);
                    panel.removeAll();
                    displayAccountInfo(panel);
                    displayCustomerButtons(panel);
                    panel.revalidate();
                    panel.repaint();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid amount entered.");
                }
            }
        });
    }
}
