import java.io.IOException;
import javax.swing.*;

public class AdminGUI {
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

        JFrame frame = new JFrame("Admin Banking System");
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
                displayAdminButtons(panel);
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
            accountArea.append("Account Number: " + account.getAccountNumber() +
                               ", Holder: " + account.getAccountHolderName() +
                               ", Balance: " + account.getBalance() + "\n");
        }
    }

    private static void displayAdminButtons(JPanel panel) {
        JButton updateButton = new JButton("Update Account Holder Name");
        updateButton.setBounds(10, 290, 250, 25);
        panel.add(updateButton);

        JButton closeButton = new JButton("Close Account");
        closeButton.setBounds(270, 290, 150, 25);
        panel.add(closeButton);

        updateButton.addActionListener(_event -> {
            String accountNumber = JOptionPane.showInputDialog("Enter account number:");
            String newHolderName = JOptionPane.showInputDialog("Enter new account holder name:");
            if (accountNumber != null && newHolderName != null) {
                bankingSystem.updateAccount(accountNumber.trim(), newHolderName.trim());
                panel.removeAll();
                displayAccountInfo(panel);
                displayAdminButtons(panel);
                panel.revalidate();
                panel.repaint();
            }
        });

        closeButton.addActionListener(_event -> {
            String accountNumber = JOptionPane.showInputDialog("Enter account number:");
            if (accountNumber != null) {
                bankingSystem.closeAccount(accountNumber.trim());
                panel.removeAll();
                displayAccountInfo(panel);
                displayAdminButtons(panel);
                panel.revalidate();
                panel.repaint();
            }
        });
    }
}
