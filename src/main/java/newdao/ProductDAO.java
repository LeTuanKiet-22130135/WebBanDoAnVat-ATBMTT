package newdao;

import newdao.DBConnection;
import newmodel.Product;
import newmodel.Variant;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductDAO {

    private final VariantDAO variantDAO = new VariantDAO();

    public ProductDAO() {
        Connection conn = DBConnection.getConnection();
    }

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products";
        VariantDAO variantDAO = new VariantDAO();

        try (Connection conn = DBConnection.getConnection()) {
            assert conn != null;
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setCode(rs.getString("code"));
                    product.setName(rs.getString("name"));
                    product.setDesc(rs.getString("desc"));
                    product.setImg(rs.getString("img"));
                    product.setTypeId(rs.getInt("typeId"));
                    product.setVariantId(rs.getInt("variantId"));
                    product.setCreateAt(rs.getDate("createAt"));

                    Variant variant = variantDAO.getFirstVariantByProductId(product.getId());
                    if (variant != null) {
                        product.setPrice(variant.getPrice()); // Add price to model
                    }

                    list.add(product);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Product getProductById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        Product product = null;
        VariantDAO variantDAO = new VariantDAO();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setCode(rs.getString("code"));
                    product.setName(rs.getString("name"));
                    product.setDesc(rs.getString("desc"));
                    product.setImg(rs.getString("img"));
                    product.setTypeId(rs.getInt("typeId"));
                    product.setVariantId(rs.getInt("variantId"));
                    product.setCreateAt(rs.getDate("createAt"));

                    // Set variants
                    List<Variant> variants = variantDAO.getVariantsByProductId(id);
                    product.setVariants(variants);

                    // Set price from the first variant if available
                    if (!variants.isEmpty()) {
                        product.setPrice(variants.get(0).getPrice());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return product;
    }

    public List<Product> getProductsByCriteria(String query, String[] priceRanges) {
        List<Product> products = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM products");
        List<String> conditions = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            conditions.add("name LIKE ?");
            parameters.add("%" + query + "%");
        }

        if (priceRanges != null && priceRanges.length > 0 && !Arrays.asList(priceRanges).contains("all")) {
            List<String> priceConditions = new ArrayList<>();
            for (String range : priceRanges) {
                String[] bounds = range.split("-");
                if (bounds.length == 2) {
                    priceConditions.add("EXISTS (SELECT 1 FROM variants v WHERE v.pid = products.id AND v.price BETWEEN ? AND ?)");
                    parameters.add(new BigDecimal(bounds[0]));
                    parameters.add(new BigDecimal(bounds[1]));
                } else if (range.equals("over50000")) {
                    priceConditions.add("EXISTS (SELECT 1 FROM variants v WHERE v.pid = products.id AND v.price > ?)");
                    parameters.add(new BigDecimal("50000"));
                }
            }
            if (!priceConditions.isEmpty()) {
                conditions.add("(" + String.join(" OR ", priceConditions) + ")");
            }
        }

        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                Object param = parameters.get(i);
                if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                } else if (param instanceof BigDecimal) {
                    ps.setBigDecimal(i + 1, (BigDecimal) param);
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                VariantDAO variantDAO = new VariantDAO();
                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setCode(rs.getString("code"));
                    product.setName(rs.getString("name"));
                    product.setDesc(rs.getString("desc"));
                    product.setImg(rs.getString("img"));
                    product.setTypeId(rs.getInt("typeId"));
                    product.setVariantId(rs.getInt("variantId"));
                    product.setCreateAt(rs.getDate("createAt"));
                    // Set the lowest variant price
                    Variant lowestVariant = variantDAO.getFirstVariantByProductId(product.getId());
                    if (lowestVariant != null) {
                        product.setPrice(lowestVariant.getPrice());
                    }
                    products.add(product);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return products;
    }

}
