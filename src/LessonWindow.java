
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LessonWindow extends JFrame {
    private String username;
    private int courseId;
    private int lessonId;
    private JLabel lessonTitleLabel;
    private JTextArea lessonContentArea;
    private JButton nextButton, previousButton, quizButton, backButton;
    private int currentLessonIndex = 0; // Start at the first lesson

    public LessonWindow(String username, int courseId) {
        this.username = username;
        this.courseId = courseId;

        // Set window properties
        setTitle("Lesson Window");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create the header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        lessonTitleLabel = new JLabel("Lesson Title", JLabel.CENTER);
        lessonTitleLabel.setFont(new Font("Arial", Font.BOLD, 23));
        headerPanel.add(lessonTitleLabel, BorderLayout.CENTER);

        // Create lesson content area (scrollable)
        // Create lesson content area (scrollable)
        lessonContentArea = new JTextArea();
        lessonContentArea.setEditable(false);
        lessonContentArea.setLineWrap(true);
        lessonContentArea.setWrapStyleWord(true);
        lessonContentArea.setFont(new Font("Arial", Font.PLAIN, 16)); // Set desired font size for content
        JScrollPane scrollPane = new JScrollPane(lessonContentArea);
        scrollPane.setPreferredSize(new Dimension(580, 200)); // Set scrollable content area size
        // Set scrollable content area size

        // Create bottom panel for navigation buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Center the buttons

        previousButton = new JButton("Previous Lesson");
        nextButton = new JButton("Next Lesson");
        quizButton = new JButton("Take Quiz");
        backButton = new JButton("Back to Course Selection");

        // Set custom button style
        customizeButton(previousButton);
        customizeButton(nextButton);
        customizeButton(quizButton);
        customizeButton(backButton);

        // Initially disable the next button if no more lessons, quiz is disabled initially
        quizButton.setEnabled(false);

        // Add action listeners for the buttons
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                completeLesson();  // Mark this lesson as completed
                loadNextLesson();  // Move to the next lesson
            }
        });

        previousButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadPreviousLesson();  // Move to the previous lesson
            }
        });

        quizButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                takeQuiz();  // Handle taking the quiz
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goBackToCourseSelection();  // Go back to course selection
            }
        });

        // Add buttons to the bottom panel
        bottomPanel.add(previousButton);
        bottomPanel.add(nextButton);
        bottomPanel.add(quizButton);

        // Add components to the frame
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(backButton, BorderLayout.NORTH);

        setLocationRelativeTo(null); // Center window

        // Load the first lesson
        loadLesson(courseId, currentLessonIndex);
    }

    // Customizes button style
    private void customizeButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(173, 216, 230));
        button.setForeground(new Color(30, 60, 120));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(30, 60, 120), 2));
        button.setBorder(BorderFactory.createCompoundBorder(
                button.getBorder(),
                BorderFactory.createEmptyBorder(10, 20, 10, 20) // Padding
        ));
    }

    // Load lesson content from the database
    private void loadLesson(int courseId, int lessonIndex) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM lessons WHERE course_id = ? LIMIT 1 OFFSET ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, courseId);
            stmt.setInt(2, lessonIndex);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                lessonId = rs.getInt("id");
                lessonTitleLabel.setText(rs.getString("title"));
                lessonContentArea.setText(rs.getString("content"));
            } else {
                lessonContentArea.setText("Course is Completed.");
                nextButton.setEnabled(false); // Disable next button if no more lessons
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            lessonContentArea.setText("Error loading lesson.");
        }

        // Disable previous button if it's the first lesson
        previousButton.setEnabled(lessonIndex > 0);
    }

    // Load the next lesson
    private void loadNextLesson() {
        currentLessonIndex++;
        loadLesson(courseId, currentLessonIndex);
        checkLessonCompletion();
    }

    // Load the previous lesson
    private void loadPreviousLesson() {
        if (currentLessonIndex > 0) {
            currentLessonIndex--;
            loadLesson(courseId, currentLessonIndex);
        }
    }

    // Mark lesson as completed
    private void completeLesson() {
        // Mark lesson as completed for this user
        updateLessonProgress();
    }

    // Update lesson progress
    private void updateLessonProgress() {
        try (Connection conn = DBConnection.getConnection()) {
            String updateProgressQuery = "INSERT INTO lesson_progress (username, lesson_id, completed) VALUES (?, ?, 1) " +
                    "ON DUPLICATE KEY UPDATE completed = 1";
            PreparedStatement stmt = conn.prepareStatement(updateProgressQuery);
            stmt.setString(1, username);
            stmt.setInt(2, lessonId);
            stmt.executeUpdate();
            checkLessonCompletion();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Check if the user has completed all lessons in the course
    private void checkLessonCompletion() {
        try (Connection conn = DBConnection.getConnection()) {
            String countLessonsQuery = "SELECT COUNT(*) FROM lessons WHERE course_id = ?";
            PreparedStatement stmt = conn.prepareStatement(countLessonsQuery);
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int totalLessons = rs.getInt(1);

            String countCompletedQuery = "SELECT COUNT(*) FROM lesson_progress WHERE lesson_id IN (SELECT id FROM lessons WHERE course_id = ?) AND username = ? AND completed = 1";
            stmt = conn.prepareStatement(countCompletedQuery);
            stmt.setInt(1, courseId);
            stmt.setString(2, username);
            rs = stmt.executeQuery();
            rs.next();
            int completedLessons = rs.getInt(1);

            double progressPercentage = (completedLessons / (double) totalLessons) * 100;

            updateUserProgress(progressPercentage);

            // If all lessons are completed, enable the quiz button
            if (completedLessons == totalLessons) {
                quizButton.setEnabled(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Update user's progress
    private void updateUserProgress(double progressPercentage) {
        try (Connection conn = DBConnection.getConnection()) {
            String updateUserQuery = "UPDATE users SET progress_percentage = ? WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(updateUserQuery);
            stmt.setDouble(1, progressPercentage);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Go back to course selection window
    private void goBackToCourseSelection() {
        CourseSelectionWindow courseSelectionWindow = new CourseSelectionWindow(username);
        courseSelectionWindow.setVisible(true);
        this.setVisible(false); // Close the current window
    }

    // Start quiz for the course
    private void takeQuiz() {
        QuizWindow quizWindow = new QuizWindow(username, courseId);
        quizWindow.setVisible(true);
        this.setVisible(false); // Close the lesson window
    }
}
