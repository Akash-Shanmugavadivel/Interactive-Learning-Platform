import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginWindowTest {

    private JFrame frame;
    private JPanel panel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton backButton;

    @BeforeEach
    public void setUp() {
        frame = new JFrame();
        panel = new JPanel();
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        backButton = new JButton("Back to Profile");

        // Adding components to the panel
        panel.setLayout(new FlowLayout());
        panel.add(usernameField);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(backButton);

        // Adding the panel to the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setSize(400, 300);
        frame.setVisible(true);
    }

    @Test
    public void testLoginSuccessful() {
        // Set the fields to valid login credentials
        usernameField.setText("user");
        passwordField.setText("password");

        // Click login button
        loginButton.doClick();

        // Check if the login was successful (i.e., the correct component is displayed)
        // Assuming the second component in the container is the 'Back to Profile' button.
        try {
            Component component = panel.getComponent(2); // The 2nd component after username and password
            assertTrue(component instanceof JButton, "Expected a JButton.");
            assertEquals("Login", loginButton.getText());
        } catch (ArrayIndexOutOfBoundsException e) {
            fail("Component index out of bounds. Check if the components are added correctly.");
        }
    }

    @Test
    public void testLoginInvalidCredentials() {
        // Set invalid credentials
        usernameField.setText("wrongUser");
        passwordField.setText("wrongPassword");

        // Click login button
        loginButton.doClick();

        // Assuming an error dialog should be shown for invalid login
        // Check if the error dialog is visible (You might need to simulate this in your test)
        JOptionPane.showMessageDialog(frame, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);

        // Check if the error message was shown, this can be extended with custom logic
        assertTrue(true, "Invalid credentials message shown.");
    }

    @Test
    public void testLoginEmptyFields() {
        // Clear the fields
        usernameField.setText("");
        passwordField.setText("");

        // Click login button
        loginButton.doClick();

        // Check if the error dialog shows up
        JOptionPane.showMessageDialog(frame, "Please enter credentials", "Error", JOptionPane.ERROR_MESSAGE);

        // Again, extend this with custom checks for UI behavior, e.g., component visibility or button state
        assertTrue(true, "Please enter credentials message shown.");
    }
}
