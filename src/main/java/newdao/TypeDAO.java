package newdao;

import newmodel.Type;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for handling database operations related to product types.
 */
public class TypeDAO {

    public TypeDAO() {
        Connection conn = DBConnection.getConnection();
    }

    /**
     * Get all product types from the database.
     * 
     * @return List of all product types
     */
    public List<Type> getAllTypes() {
        List<Type> types = new ArrayList<>();
        String sql = "SELECT * FROM types";

        try (Connection conn = DBConnection.getConnection()) {
            assert conn != null;
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Type type = new Type();
                    type.setId(rs.getInt("id"));
                    type.setName(rs.getString("name"));
                    types.add(type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return types;
    }

    /**
     * Get a product type by its ID.
     * 
     * @param id The ID of the product type
     * @return The product type with the specified ID, or null if not found
     */
    public Type getTypeById(int id) {
        String sql = "SELECT * FROM types WHERE id = ?";
        Type type = null;

        try (Connection conn = DBConnection.getConnection()) {
            assert conn != null;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        type = new Type();
                        type.setId(rs.getInt("id"));
                        type.setName(rs.getString("name"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }
}