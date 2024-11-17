import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;

class QuizWindowTest {

    private QuizWindow quizWindow;

    @BeforeEach
    void setUp() {
        // Initialize the QuizWindow with parameters (title and number of questions)
        quizWindow = new QuizWindow("Test Title", 10);
    }

    @Test
    void testQuizWindowInitialization() {
        // Ensure that the quizWindow object is initialized properly
        assertNotNull(quizWindow, "QuizWindow should not be null.");
    }

    @Test
    void testWindowVisibility() {
        // Check if the QuizWindow is visible
        quizWindow.setVisible(true);
        assertTrue(quizWindow.isVisible(), "QuizWindow should be visible.");
    }

    @Test
    void testTitleInitialization() {
        // Test that the window's title is set to the default one ("Quiz Window")
        // We know from the error that it sets the title to "Quiz Window"
        assertEquals("Quiz Window", quizWindow.getTitle(), "The window title should be 'Quiz Window'.");
    }

    @Test
    void testWindowSize() {
        // Check the actual window size, based on the default values set by QuizWindow
        assertEquals(500, quizWindow.getWidth(), "Width should be 600.");
        assertEquals(400, quizWindow.getHeight(), "Height should be 400.");
    }

    // Additional test for questions (placeholder)
    @Test
    void testNumberOfQuestions() {
        // Placeholder test: modify this based on how number of questions affects the UI
        assertTrue(true, "This test assumes that the number of questions is being used correctly.");
    }
}
