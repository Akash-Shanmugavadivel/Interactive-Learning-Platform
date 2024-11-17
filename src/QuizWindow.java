
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class QuizWindow extends JFrame {
    private String username;
    private int courseId;
    private ArrayList<Question> questions;
    private ArrayList<JRadioButton> options;
    private ButtonGroup buttonGroup;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private JButton submitButton;

    public QuizWindow(String username, int courseId) {
        this.username = username;
        this.courseId = courseId;
        this.questions = new ArrayList<>();
        this.options = new ArrayList<>();
        this.buttonGroup = new ButtonGroup();

        // Set window properties
        setTitle("Quiz Window");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create the panel for displaying questions and options
        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS)); // Vertical layout

        // Submit button, initially disabled
        submitButton = new JButton("Submit Quiz");
        submitButton.setEnabled(false);
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                evaluateQuiz();
            }
        });

        // Add the question panel and the submit button to the frame
        add(new JScrollPane(questionPanel), BorderLayout.CENTER);
        add(submitButton, BorderLayout.SOUTH);

        // Fetch questions from the database
        questions = fetchQuestions(courseId);
        loadQuestion(questionPanel);

        // Center the window
        setLocationRelativeTo(null);
    }

    // Fetch questions from the database
    private ArrayList<Question> fetchQuestions(int courseId) {
        ArrayList<Question> questions = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT * FROM quizzes WHERE lesson_id IN (SELECT id FROM lessons WHERE course_id = ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                questions.add(new Question(
                        rs.getString("question"),
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d"),
                        rs.getString("correct_option").charAt(0)
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return questions;
    }

    // Load the current question and options to the panel
    private void loadQuestion(JPanel panel) {
        if (currentQuestionIndex < questions.size()) {
            Question currentQuestion = questions.get(currentQuestionIndex);
            panel.removeAll(); // Clear the previous question's UI elements

            // Create the question label
            JLabel questionLabel = new JLabel((currentQuestionIndex + 1) + ". " + currentQuestion.getQuestion());
            questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
            panel.add(questionLabel);

            // Create radio buttons for options
            options.clear(); // Clear the previous options
            buttonGroup.clearSelection(); // Clear any previous selections
            String[] optionTexts = {currentQuestion.getOptionA(), currentQuestion.getOptionB(), currentQuestion.getOptionC(), currentQuestion.getOptionD()};
            for (String optionText : optionTexts) {
                JRadioButton optionButton = new JRadioButton(optionText);
                options.add(optionButton);
                buttonGroup.add(optionButton);
                panel.add(optionButton);
            }

            // Add the next button for navigation
            JButton nextButton = new JButton("Next");
            // Add the next button for navigation

            nextButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Check if an option is selected before proceeding
                    boolean isOptionSelected = options.stream().anyMatch(JRadioButton::isSelected);

                    if (!isOptionSelected) {
                        JOptionPane.showMessageDialog(panel, "Please select an option before proceeding.");
                    } else {
                        checkAnswer(); // Check answer for the current question
                        currentQuestionIndex++; // Move to the next question
                        if (currentQuestionIndex < questions.size()) {
                            loadQuestion(panel); // Load the next question
                        } else {
                            // End of quiz, show the submit button
                            submitButton.setEnabled(true);
                            nextButton.setEnabled(false);
                        }
                    }
                }
            });


            panel.add(nextButton);
            revalidate(); // Ensure the UI updates
            repaint();
        }
    }

    // Check the selected answer for the current question
    private void checkAnswer() {
        char correctOption = questions.get(currentQuestionIndex).getCorrectOption();
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).isSelected() && (char) ('A' + i) == correctOption) {
                score++;
                break;
            }
        }
    }

    // Evaluate the quiz and update the user's progress
    /*private void evaluateQuiz() {
        double progressPercentage = ((double) score / questions.size()) * 100;

        try (Connection conn = DBConnection.getConnection()) {
            String query = "UPDATE users SET progress_percentage = ? WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setDouble(1, progressPercentage);
            stmt.setString(2, username);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Quiz Submitted! Your score: " + score + "/" + questions.size());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        // Navigate back to the course selection window after submitting the quiz
        goToCourseSelection();
    }*/
    private void evaluateQuiz() {
        // Calculate the quiz progress as a percentage
        double progressPercentage = ((double) score / questions.size()) * 100;

        try (Connection conn = DBConnection.getConnection()) {
            // Step 1: Get the current progress from the database
            String query = "SELECT progress_percentage FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            double currentProgress = 0;
            if (rs.next()) {
                currentProgress = rs.getDouble("progress_percentage");
            }

            // Step 2: Add the quiz progress to the existing progress
            // You can either add it directly, average it, or use another logic
            double newProgress = currentProgress + progressPercentage; // For cumulative
            // Alternatively, to average:
            // double newProgress = (currentProgress + progressPercentage) / 2;

            // Step 3: Update the progress in the database
            String updateQuery = "UPDATE users SET progress_percentage = ? WHERE username = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setDouble(1, newProgress);
            updateStmt.setString(2, username);
            updateStmt.executeUpdate();

            // Inform the user
            JOptionPane.showMessageDialog(this, "Quiz Submitted! Your score: " + score + "/" + questions.size());

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Navigate back to the course selection window after submitting the quiz
        goToCourseSelection();
    }

    // Navigate back to the Course Selection window
    private void goToCourseSelection() {
        CourseSelectionWindow courseSelectionWindow = new CourseSelectionWindow(username);
        courseSelectionWindow.setVisible(true);
        dispose(); // Close the current quiz window
    }

    // Inner class to represent a quiz question
    private static class Question {
        private String question, optionA, optionB, optionC, optionD;
        private char correctOption;

        public Question(String question, String optionA, String optionB, String optionC, String optionD, char correctOption) {
            this.question = question;
            this.optionA = optionA;
            this.optionB = optionB;
            this.optionC = optionC;
            this.optionD = optionD;
            this.correctOption = correctOption;
        }

        public String getQuestion() {
            return question;
        }

        public String getOptionA() {
            return optionA;
        }

        public String getOptionB() {
            return optionB;
        }

        public String getOptionC() {
            return optionC;
        }

        public String getOptionD() {
            return optionD;
        }

        public char getCorrectOption() {
            return correctOption;
        }
    }
}


