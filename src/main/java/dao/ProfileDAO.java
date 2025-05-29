package dao;

import model.Profile;
import java.sql.*;

public class ProfileDAO {
    private Connection conn;

    public ProfileDAO() {
        this.conn = DBConnection.getConnection();
    }

    public Profile getProfileByUserId(int userId) {
        String query = "SELECT * FROM userProfile WHERE user_id = ?";
        try {
        	PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Profile profile = new Profile();
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
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateProfile(Profile profile) {
        String query = "UPDATE userProfile SET first_name = ?, last_name = ?, email = ?, mobile_no = ?, " +
                "address_line1 = ?, address_line2 = ?, country = ?, city = ?, state = ?, zip_code = ? " +
                "WHERE user_id = ?";
        try {
        	PreparedStatement stmt = conn.prepareStatement(query);
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
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
