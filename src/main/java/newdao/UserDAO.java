package newdao;

import newmodel.User;
import newmodel.UserProfile;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for User entities
 * Handles database operations related to users
 */
public class UserDAO {

    /**
     * Checks if a user exists in the database
     * 
     * @param username The username to check
     * @return true if the user exists, false otherwise
     */
    public boolean checkUser(String username) {
        String query = "SELECT 1 FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Returns true if a record is found
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Checks if the provided password matches the stored hashed password for a user
     * 
     * @param username The username of the user
     * @param password The plain text password to check
     * @return true if the password is correct, false otherwise
     */
    public boolean checkPassword(String username, String password) {
        String query = "SELECT hashed_password FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("hashed_password");
                    return PasswordUtil.comparePassword(password, hashedPassword);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Gets a user by username
     * 
     * @param username The username to search for
     * @return The User object if found, null otherwise
     */
    public User getUserByUsername(String username) {
        String query = "SELECT id, username, hashed_password, email, status FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setHashedPassword(rs.getString("hashed_password"));
                    user.setEmail(rs.getString("email"));
                    user.setStatus(rs.getInt("status"));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets a user ID by username
     * 
     * @param username The username to search for
     * @return The user ID if found, -1 otherwise
     */
    public int getUserIdByUsername(String username) {
        String query = "SELECT id FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Gets a user profile by user ID
     * If no profile exists, creates a new one with default values
     * 
     * @param userId The user ID to search for
     * @return The UserProfile object
     */
    public UserProfile getUserProfileByUserId(int userId) {
        String query = "SELECT * FROM userprofile WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UserProfile profile = new UserProfile();
                    profile.setId(rs.getInt("id"));
                    profile.setUserId(rs.getInt("user_id"));
                    profile.setFirstName(rs.getString("first_name"));
                    profile.setLastName(rs.getString("last_name"));
                    profile.setEmail(rs.getString("email"));
                    profile.setMobileNo(rs.getString("mobile_no"));
                    profile.setAddressLine1(rs.getString("address_line1"));
                    profile.setAddressLine2(rs.getString("address_line2"));
                    profile.setCountry(rs.getString("country"));
                    profile.setCity(rs.getString("city"));
                    profile.setState(rs.getString("state"));
                    profile.setZipCode(rs.getString("zip_code"));
                    return profile;
                } else {
                    // No profile found, create a new one
                    return createUserProfile(userId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // In case of error, still create a profile
        return createUserProfile(userId);
    }

    /**
     * Creates a new user profile with default values
     * 
     * @param userId The user ID to create a profile for
     * @return The newly created UserProfile object
     */
    private UserProfile createUserProfile(int userId) {
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setFirstName("");
        profile.setLastName("");
        profile.setEmail("");
        profile.setMobileNo("");
        profile.setAddressLine1("");
        profile.setAddressLine2("");
        profile.setCountry("");
        profile.setCity("");
        profile.setState("");
        profile.setZipCode("");

        // Insert the new profile into the database
        String query = "INSERT INTO userprofile (user_id, first_name, last_name, email, mobile_no, " +
                "address_line1, address_line2, country, city, state, zip_code) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, userId);
            stmt.setString(2, profile.getFirstName());
            stmt.setString(3, profile.getLastName());
            stmt.setString(4, profile.getEmail());
            stmt.setString(5, profile.getMobileNo());
            stmt.setString(6, profile.getAddressLine1());
            stmt.setString(7, profile.getAddressLine2());
            stmt.setString(8, profile.getCountry());
            stmt.setString(9, profile.getCity());
            stmt.setString(10, profile.getState());
            stmt.setString(11, profile.getZipCode());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        profile.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return profile;
    }

    /**
     * Updates a user profile in the database
     * 
     * @param profile The UserProfile object to update
     * @return true if the update was successful, false otherwise
     */
    public boolean updateUserProfile(UserProfile profile) {
        String query = "UPDATE userprofile SET first_name = ?, last_name = ?, email = ?, mobile_no = ?, " +
                "address_line1 = ?, address_line2 = ?, country = ?, city = ?, state = ?, zip_code = ? " +
                "WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, profile.getFirstName());
            stmt.setString(2, profile.getLastName());
            stmt.setString(3, profile.getEmail());
            stmt.setString(4, profile.getMobileNo());
            stmt.setString(5, profile.getAddressLine1());
            stmt.setString(6, profile.getAddressLine2());
            stmt.setString(7, profile.getCountry());
            stmt.setString(8, profile.getCity());
            stmt.setString(9, profile.getState());
            stmt.setString(10, profile.getZipCode());
            stmt.setInt(11, profile.getUserId());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Adds a new user with a profile
     * 
     * @param username The username of the new user
     * @param hashedPassword The hashed password of the new user
     * @param firstName The first name for the user's profile
     * @param lastName The last name for the user's profile
     * @param email The email for the user's profile
     */
    public void addUserWithProfile(String username, String hashedPassword, String firstName, String lastName, String email) {
        String userQuery = "INSERT INTO users (username, hashed_password, email, status) VALUES (?, ?, ?, ?)";
        String profileQuery = "INSERT INTO userprofile (user_id, first_name, last_name, email) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // Insert user with hashed password
            try (PreparedStatement userStmt = conn.prepareStatement(userQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, username);
                userStmt.setString(2, hashedPassword);
                userStmt.setString(3, email);
                userStmt.setInt(4, 1); // Active status
                userStmt.executeUpdate();

                // Get generated user ID
                try (ResultSet rs = userStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int userId = rs.getInt(1);

                        // Insert profile
                        try (PreparedStatement profileStmt = conn.prepareStatement(profileQuery)) {
                            profileStmt.setInt(1, userId);
                            profileStmt.setString(2, firstName);
                            profileStmt.setString(3, lastName);
                            profileStmt.setString(4, email);
                            profileStmt.executeUpdate();
                        }
                    }
                }
            }

            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction on error
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
