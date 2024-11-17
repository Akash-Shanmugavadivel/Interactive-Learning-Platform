import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProgressTracker {
    private String username;

    public ProgressTracker(String username) {
        this.username = username;
    }

    public void updateProgress(double completionPercentage) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "UPDATE users SET progress_percentage = ?, streak = streak + 1 WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDouble(1, completionPercentage);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void displayProgress() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT progress_percentage, streak FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double progress = rs.getDouble("progress_percentage");
                int streak = rs.getInt("streak");
                System.out.println("Progress: " + progress + "%");
                System.out.println("Streak: " + streak + " days");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
