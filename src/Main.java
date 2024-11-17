import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set the Swing UI look and feel for better user experience (Optional)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Entry point for the Interactive Learning Platform
        SwingUtilities.invokeLater(() -> {
            // Display the login window as the starting point
            /*RegistrationWindow registrationWindow = new RegistrationWindow();
            registrationWindow.setVisible(true);*/
            LoginWindow loginWindow = new LoginWindow();
            loginWindow.setVisible(true);
        });
    }
}
