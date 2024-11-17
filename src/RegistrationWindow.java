import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegistrationWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public RegistrationWindow() {
        setTitle("Register");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Custom background panel with gradient
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color startColor = new Color(30, 60, 120); // Dark Blue
                Color endColor = new Color(173, 216, 230); // Light Blue
                GradientPaint gradient = new GradientPaint(0, 0, startColor, 0, getHeight(), endColor);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new GridBagLayout());

        // Define GridBagConstraints for component alignment
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding around components
        gbc.anchor = GridBagConstraints.WEST;

        // Create labels, text fields, and buttons
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);

        JButton registerButton = new JButton("Register");
        JButton loginButton = new JButton("Already Registered?Login");

        // Styling for text fields and buttons
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));

        // Set button background colors and borders
        registerButton.setBackground(new Color(173, 216, 230));
        registerButton.setForeground(new Color(30, 60, 120));
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createLineBorder(new Color(30, 60, 120), 2));
        registerButton.setBorder(BorderFactory.createCompoundBorder(
                registerButton.getBorder(),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        loginButton.setBackground(new Color(173, 216, 230));
        loginButton.setForeground(new Color(30, 60, 120));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createLineBorder(new Color(30, 60, 120), 2));
        loginButton.setBorder(BorderFactory.createCompoundBorder(
                loginButton.getBorder(),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        // Add components to the panel using GridBagLayout
        // Add components to the panel using GridBagLayout
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        panel.add(passwordField, gbc);

// Swap the buttons' positions
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(loginButton, gbc);  // Add login button first

        gbc.gridx = 1;
        panel.add(registerButton, gbc);  // Add register button second

        // Add panel to the JFrame
        add(panel);

        // Button actions
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close registration window
                new LoginWindow().setVisible(true); // Open login window
            }
        });

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Check if username is empty
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Username cannot be empty.");
                    return; // Stop further execution
                }

                // Check if password is empty
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Password cannot be empty.");
                    return; // Stop further execution
                }

                // Check password length (e.g., minimum of 6 characters)
                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(null, "Password must be at least 6 characters long.");
                    return; // Stop further execution
                }

                // Check password strength (contains at least one uppercase, one lowercase, and one digit)
                if (!isValidPassword(password)) {
                    JOptionPane.showMessageDialog(null, "Password must contain at least one uppercase letter, one lowercase letter, and one digit.");
                    return; // Stop further execution
                }

                // If all checks pass, proceed with registration
                if (registerUser(username, password)) {
                    JOptionPane.showMessageDialog(null, "Registration successful!");
                    dispose(); // Close the registration window
                    new LoginWindow().setVisible(true); // Open login window
                } else {
                    JOptionPane.showMessageDialog(null, "Registration failed. Username may already exist.");
                }
            }
        });

    }
    private boolean isValidPassword(String password) {
        // Check for at least one lowercase letter
        boolean hasLower = false;
        // Check for at least one uppercase letter
        boolean hasUpper = false;
        // Check for at least one digit
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                hasLower = true;
            }
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            }
            if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }

        return hasLower && hasUpper && hasDigit;
    }

    private boolean registerUser(String username, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        new RegistrationWindow().setVisible(true);
    }
}
