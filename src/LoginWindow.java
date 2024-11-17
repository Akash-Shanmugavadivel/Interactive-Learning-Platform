import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginWindow() {
        setTitle("Login");
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


        JButton registerButton = new JButton("New User? Register");
        JButton loginButton = new JButton("Login");


        // Styling for text fields and buttons
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));

// Set button background colors and borders
        loginButton.setBackground(new Color(173, 216, 230));
        loginButton.setForeground(new Color(30, 60, 120));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createLineBorder(new Color(30, 60, 120), 2)); // Create border
        loginButton.setBorder(BorderFactory.createCompoundBorder(
                loginButton.getBorder(),
                BorderFactory.createEmptyBorder(10, 20, 10, 20) // Padding inside button
        ));

        registerButton.setBackground(new Color(173, 216, 230));
        registerButton.setForeground(new Color(30, 60, 120));
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createLineBorder(new Color(30, 60, 120), 2)); // Create border
        registerButton.setBorder(BorderFactory.createCompoundBorder(
                registerButton.getBorder(),
                BorderFactory.createEmptyBorder(10, 20, 10, 20) // Padding inside button
        ));


        // Add components to the panel using GridBagLayout
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
//        gbc.weightx = 1.0; // Allow horizontal expansion
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(registerButton, gbc);


        gbc.gridx = 1;
        panel.add(loginButton, gbc);

        add(panel);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Username and password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    if (authenticateUser(username, password)) {
                        JOptionPane.showMessageDialog(null, "Login successful!");
                        dispose(); // Close the login window

                        ProfileWindow pw =new ProfileWindow(username);
                        pw.setVisible(true); // Open the profile window
                        pw.incrementStreak();
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close login window
                new RegistrationWindow().setVisible(true); // Open registration window
            }
        });
    }

    /*private boolean authenticateUser(String username, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // If a result is found, the user is authenticated
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }*/
    private boolean authenticateUser(String username, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            // Query to verify user credentials
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // If user authenticated successfully, update last_login_date
                String updateLoginDateQuery = "UPDATE users SET last_login_date = ? WHERE username = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateLoginDateQuery);
                updateStmt.setDate(1, java.sql.Date.valueOf(java.time.LocalDate.now())); // Set to today's date
                updateStmt.setString(2, username);
                updateStmt.executeUpdate();

                return true; // Authentication successful
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false; // Authentication failed
    }


    public static void main(String[] args) {
        new LoginWindow().setVisible(true);
    }
}
