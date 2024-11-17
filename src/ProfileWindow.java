import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

public class ProfileWindow extends JFrame {
    private JLabel usernameLabel, streakLabel, dailyGoalLabel, progressLabel;
    private JTextField usernameField, streakField, dailyGoalField, progressField;
    private JButton updateButton, logoutButton, courseSelectionButton;
    private String username;

    public ProfileWindow(String username) {
        this.username = username;
        setTitle("LearnIt - Profile");
        setSize(600, 400); // Make window full screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set an aesthetic gradient background using a custom JPanel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color startColor = new Color(30, 60, 120);
                Color endColor = new Color(173, 216, 230);
                GradientPaint gradient = new GradientPaint(0, 0, startColor, 0, getHeight(), endColor);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);  // Set the background panel

        // Initialize labels and fields
        usernameLabel = new JLabel("Username: ");
        streakLabel = new JLabel("Learning Streak: ");
        dailyGoalLabel = new JLabel("Daily Goal (Minutes): ");
        progressLabel = new JLabel("Overall Progress (%): ");

        usernameField = new JTextField(20);
        streakField = new JTextField(20);
        dailyGoalField = new JTextField(20);
        progressField = new JTextField(20);

        // Set fields as non-editable except for dailyGoalField
        usernameField.setEditable(false);
        streakField.setEditable(false);
        progressField.setEditable(false);

        updateButton = new JButton("Update Daily Goal");
        courseSelectionButton = new JButton("Select Course");
        logoutButton = new JButton("Logout");

// Customize buttons for modern aesthetic with larger font and size
        updateButton.setBackground(new Color(173, 216, 230));
        updateButton.setForeground(new Color(30, 60, 120));
        updateButton.setFont(new Font("Arial", Font.BOLD, 18)); // Increase font size
        updateButton.setFocusPainted(false);
        updateButton.setBorder(BorderFactory.createLineBorder(new Color(30, 60, 120), 2));
        updateButton.setPreferredSize(new Dimension(250, 50)); // Set preferred button size

        courseSelectionButton.setBackground(new Color(173, 216, 230));
        courseSelectionButton.setForeground(new Color(30, 60, 120));
        courseSelectionButton.setFont(new Font("Arial", Font.BOLD, 18)); // Increase font size
        courseSelectionButton.setFocusPainted(false);
        courseSelectionButton.setBorder(BorderFactory.createLineBorder(new Color(30, 60, 120), 2));
        courseSelectionButton.setPreferredSize(new Dimension(250, 50)); // Set preferred button size

        logoutButton.setBackground(new Color(173, 216, 230));
        logoutButton.setForeground(new Color(30, 60, 120));
        logoutButton.setFont(new Font("Arial", Font.BOLD, 18)); // Increase font size
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createLineBorder(new Color(30, 60, 120), 2));
        logoutButton.setPreferredSize(new Dimension(250, 50)); // Set preferred button size


        Font labelFont = new Font("Arial", Font.PLAIN, 18);
        usernameLabel.setFont(labelFont);
        streakLabel.setFont(labelFont);
        dailyGoalLabel.setFont(labelFont);
        progressLabel.setFont(labelFont);

        // Set font for text fields
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);
        usernameField.setFont(fieldFont);
        streakField.setFont(fieldFont);
        dailyGoalField.setFont(fieldFont);
        progressField.setFont(fieldFont);

        // Define GridBagLayout constraints with padding
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Add padding for spacing
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add components to the frame with GridBagLayout
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(streakLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(streakField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(dailyGoalLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(dailyGoalField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(progressLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(progressField, gbc);

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBackground(new Color(30, 60, 120));
        buttonPanel.add(updateButton);
        buttonPanel.add(courseSelectionButton);
        buttonPanel.add(logoutButton);

        // Add form and button panels to the frame
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load user data from the database
        loadUserData();

        // Button action to update daily goal
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateDailyGoal();
            }
        });

        // Button action to open the Course Selection window
        courseSelectionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openCourseSelectionWindow();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginWindow().setVisible(true);
            }
        });

        setLocationRelativeTo(null); // Center window
    }

    // Load user data from the database
    private void loadUserData() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    usernameField.setText(rs.getString("username"));
                    streakField.setText(String.valueOf(rs.getInt("streak")));
                    dailyGoalField.setText(String.valueOf(rs.getInt("daily_goal_minutes")));
                    progressField.setText(String.format("%.2f", rs.getDouble("progress_percentage")));

                    // Update the streak based on the last login date
                    updateStreak(rs.getDate("last_login_date"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Update streak based on last login date
    private void updateStreak(Date lastLoginDate) {
        // Get today's date
        LocalDate today = LocalDate.now();
        LocalDate lastLogin = lastLoginDate.toLocalDate();

        if (lastLogin.plusDays(1).isEqual(today)) {
            // Increment streak if last login was yesterday
            incrementStreak();
        } else if (!lastLogin.isEqual(today)) {
            // Reset streak if there is a break in consecutive days
            resetStreak();
        }
    }

    // Increment streak
    public void incrementStreak() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "UPDATE users SET streak = streak + 1 WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    loadUserData(); // Refresh data
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Reset streak
    private void resetStreak() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "UPDATE users SET streak = 0 WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    loadUserData(); // Refresh data
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Update daily goal in the database
    private void updateDailyGoal() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "UPDATE users SET daily_goal_minutes = ? WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, Integer.parseInt(dailyGoalField.getText()));
                stmt.setString(2, username);
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Daily goal updated successfully!");
                    loadUserData(); // Refresh data
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Open Course Selection window
    private void openCourseSelectionWindow() {
        CourseSelectionWindow courseSelectionWindow = new CourseSelectionWindow(username);
        courseSelectionWindow.setVisible(true);
        this.setVisible(false); // Close the profile window
        System.out.println("Open Course Selection Window");
    }

    public static void main(String[] args) {
        // Assuming the logged-in user is passed to this window
        new ProfileWindow("testUser").setVisible(true);
    }
}
