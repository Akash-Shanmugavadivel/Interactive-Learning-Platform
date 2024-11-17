import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class CourseSelectionWindow extends JFrame {
    private JTable courseTable;
    private String username;

    public CourseSelectionWindow(String username) {
        this.username = username;

        // Set frame properties
        setTitle("Course Selection");
        setSize(600, 400); // Full screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel backgroundPanel = new BackgroundPanel();


        ArrayList<Course> courses = fetchCourses();
        if (courses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No courses found for the user.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }


        String[] columnNames = {"Course Name", "Description"};
        Object[][] data = new Object[courses.size()][2];

        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            data[i][0] = course.getName();
            data[i][1] = course.getDescription();
        }

        // Set up the table with a non-editable table model
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make cells non-editable
            }
        };
        courseTable = new JTable(tableModel);
        courseTable.setFont(new Font("Arial", Font.PLAIN, 16));
        courseTable.setRowHeight(30);

        // Center align column headers
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < courseTable.getColumnCount(); i++) {
            courseTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // Center align table cells
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < courseTable.getColumnCount(); i++) {
            courseTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(courseTable);
        scrollPane.setPreferredSize(new Dimension(700, 400));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(30, 60, 120), 2));

        // Smaller Back to Profile button at the top-left corner
        JButton backButton = createStyledButton("Back to Profile", new Insets(5, 10, 5, 10));
        backButton.setPreferredSize(new Dimension(150, 30)); // Smaller size

        // Select button
        JButton selectButton = createStyledButton("Select Course", new Insets(10, 20, 10, 20));

        // Button actions
        backButton.addActionListener(e -> goBackToProfile());

        selectButton.addActionListener(e -> {
            int selectedRow = courseTable.getSelectedRow();
            if (selectedRow != -1) {
                String selectedCourse = (String) courseTable.getValueAt(selectedRow, 0);
                JOptionPane.showMessageDialog(null, "Selected: " + selectedCourse);
                int courseId = getCourseIdByName(selectedCourse);
                new LessonWindow(username, courseId).setVisible(true);
                dispose(); // Close this window
            } else {
                JOptionPane.showMessageDialog(null, "Please select a course to proceed.");
            }
        });

        // Layout setup
        JPanel tablePanel = new JPanel(new BorderLayout());
        JLabel headerLabel = new JLabel("Select a Course", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(new Color(30, 60, 120));
        tablePanel.add(headerLabel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Back button panel at the top-left corner
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(backButton, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(30, 60, 120));
        buttonPanel.add(selectButton);

        // Add components to frame
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.add(topPanel, BorderLayout.NORTH);       // Top-left corner for back button
        backgroundPanel.add(tablePanel, BorderLayout.CENTER);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(backgroundPanel); // Set the custom panel as the content pane
    }

    private JButton createStyledButton(String text, Insets padding) {
        JButton button = new JButton(text);
        button.setBackground(new Color(173, 216, 230));
        button.setForeground(new Color(30, 60, 120));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 60, 120), 2),
                BorderFactory.createEmptyBorder(padding.top, padding.left, padding.bottom, padding.right)
        ));
        return button;
    }

    private ArrayList<Course> fetchCourses() {
        ArrayList<Course> courses = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT name, description FROM courses";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                courses.add(new Course(rs.getString("name"), rs.getString("description")));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching courses. Please check your database connection.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return courses;
    }

    private void goBackToProfile() {
        ProfileWindow profileWindow = new ProfileWindow(username);
        profileWindow.setVisible(true);
        this.setVisible(false); // Close the current window
    }

    private int getCourseIdByName(String courseName) {
        int courseId = -1;
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT id FROM courses WHERE name = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, courseName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                courseId = rs.getInt("id");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return courseId;
    }

    private static class Course {
        private String name;
        private String description;

        public Course(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    private static class BackgroundPanel extends JPanel {
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
    }

    public static void main(String[] args) {
        new CourseSelectionWindow("testuser").setVisible(true);
    }
}
