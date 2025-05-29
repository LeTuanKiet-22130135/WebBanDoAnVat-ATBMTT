package dao;

import model.User;
import util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import newdao.DBConnection;

public class UserDAO {

	private final Connection connection;

	public UserDAO() {
		this.connection = DBConnection.getConnection();
	}

	// Fetch all users
	public List<User> getAllUsers() {
		List<User> users = new ArrayList<>();
		String query = "SELECT id, username FROM user WHERE isAdmin = false";

		try (PreparedStatement stmt = connection.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				User user = new User();
				user.setId(rs.getInt("id"));
				user.setUsername(rs.getString("username"));
				users.add(user);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return users;
	}

	// Delete a user by ID
	public boolean deleteUserById(int userId) {
		String query = "DELETE FROM user WHERE id = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, userId);
			int affectedRows = stmt.executeUpdate();
			return affectedRows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	// Update user's admin status
	public boolean updateUserRole(int userId, boolean isAdmin) {
		String query = "UPDATE user SET isAdmin = ? WHERE id = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setBoolean(1, isAdmin);
			stmt.setInt(2, userId);
			int affectedRows = stmt.executeUpdate();
			return affectedRows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	// Fetch a single user by ID
	public User getUserById(int userId) {
		String query = "SELECT id, username FROM user WHERE id = ?";
		User user = null;

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, userId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					user = new User();
					user.setId(rs.getInt("id"));
					user.setUsername(rs.getString("username"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return user;
	}

	// Fetch user ID by username
	public int getUserIdByUsername(String username) {
		String query = "SELECT id FROM user WHERE username = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
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

	public void addUserWithProfile(String username, String hashedPassword, String firstName, String lastName, String email) {
		String userQuery = "INSERT INTO users (username, password, isAdmin) VALUES (?, ?, ?)";
		String profileQuery = "INSERT INTO userprofile (user_id, first_name, last_name, email) VALUES (?, ?, ?, ?)";

		try {
			connection.setAutoCommit(false); // Begin transaction
			PreparedStatement userStmt = connection.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS);

			// Insert user with hashed password
			userStmt.setString(1, username);
			userStmt.setString(2, hashedPassword);
			userStmt.setBoolean(3, false); // Regular user, not admin
			userStmt.executeUpdate();

			// Get generated user ID
			ResultSet rs = userStmt.getGeneratedKeys();
			if (rs.next()) {
				int userId = rs.getInt(1);
				PreparedStatement profileStmt = connection.prepareStatement(profileQuery);

				// Insert profile
				profileStmt.setInt(1, userId);
				profileStmt.setString(2, firstName);
				profileStmt.setString(3, lastName);
				profileStmt.setString(4, email);
				profileStmt.executeUpdate();
			}

			connection.commit(); // Commit transaction
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback(); // Rollback transaction on error
			} catch (SQLException rollbackEx) {
				rollbackEx.printStackTrace();
			}
		}
	}

	// Method to update user password and salt
	public boolean updateUserPassword(int userId, String hashedPassword) {
		String query = "UPDATE user SET password = ? WHERE id = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, hashedPassword);
			stmt.setInt(2, userId);
			int affectedRows = stmt.executeUpdate();
			return affectedRows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	// Method to search users by username
	public List<User> searchUsersByName(String name) {
		List<User> users = new ArrayList<>();
		String query = "SELECT id, username FROM user WHERE LOWER(username) LIKE ? AND isAdmin = 0";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, "%" + name + "%");
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					User user = new User();
					user.setId(rs.getInt("id"));
					user.setUsername(rs.getString("username"));
					users.add(user);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return users;
	}

	// Method to check if a username already exists
	public boolean isUsernameTaken(String username) {
		String query = "SELECT 1 FROM user WHERE username = ?";
		try {
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			return rs.next(); // Returns true if a record is found
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Validate if the current password matches the stored password
	public boolean validateUserPassword(String username, String enteredPassword) throws Exception {
		String query = "SELECT password FROM user WHERE username = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setString(1, username);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			String storedPassword = rs.getString("password");
			return PasswordUtil.comparePassword(enteredPassword, storedPassword);
		}
		return false;
	}

	// Update user password
	public boolean updateUserPassword(String username, String newPassword) {
		String query = "UPDATE user SET password = ? WHERE username = ?";
		try {
			PreparedStatement stmt = connection.prepareStatement(query);
			stmt.setString(1, newPassword);
			stmt.setString(2, username);
			int affectedRows = stmt.executeUpdate();
			return affectedRows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


}
