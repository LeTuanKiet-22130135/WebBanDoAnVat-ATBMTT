package newdao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import newdao.DBConnection;
import newmodel.Variant;

public class VariantDAO {

    public Variant getFirstVariantByProductId(int productId) {
        String sql = "SELECT * FROM variants WHERE pid = ? ORDER BY id ASC LIMIT 1";

        try (Connection conn = DBConnection.getConnection()) {
            assert conn != null;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, productId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Variant variant = new Variant();
                        variant.setId(rs.getInt("id"));
                        variant.setPid(rs.getInt("pid"));
                        variant.setName(rs.getString("name"));
                        variant.setPrice(rs.getBigDecimal("price"));
                        return variant;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Variant> getVariantsByProductId(int productId) {
        List<Variant> variants = new ArrayList<>();
        String sql = "SELECT * FROM variants WHERE pid = ? ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Variant variant = new Variant();
                    variant.setId(rs.getInt("id"));
                    variant.setPid(rs.getInt("pid"));
                    variant.setName(rs.getString("name"));
                    variant.setPrice(rs.getBigDecimal("price"));
                    variants.add(variant);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return variants;
    }

}
