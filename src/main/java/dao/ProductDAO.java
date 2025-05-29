package dao;

import model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductDAO {

	private Connection conn;

	public ProductDAO() {
		this.conn = DBConnection.getConnection();
	}

	public void addProduct(Product product) {
		String query = "INSERT INTO Product (name, description, price, image, quantity) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, product.getName());
			stmt.setString(2, product.getDescription());
			stmt.setBigDecimal(3, product.getPrice());
			stmt.setString(4, product.getImageUrl());
			stmt.setInt(5, product.getQuantity());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateProduct(Product product) {
		String query = "UPDATE Product SET name = ?, description = ?, price = ?, image = ?, quantity = ? WHERE id = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, product.getName());
			stmt.setString(2, product.getDescription());
			stmt.setBigDecimal(3, product.getPrice());
			stmt.setString(4, product.getImageUrl());
			stmt.setInt(5, product.getQuantity());
			stmt.setInt(6, product.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteProduct(int id) {
		String query = "DELETE FROM Product WHERE id = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Product> getAllProducts() {
		List<Product> products = new ArrayList<>();
		String query = "SELECT * FROM Product";
		try (Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {
			while (rs.next()) {
				Product product = new Product();
				product.setId(rs.getInt("id"));
				product.setName(rs.getString("name"));
				product.setDescription(rs.getString("description"));
				product.setPrice(rs.getBigDecimal("price"));
				product.setImageUrl(rs.getString("image"));
				product.setQuantity(rs.getInt("quantity"));
				products.add(product);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return products;
	}

	public Product getProductById(int id) {
		Product product = null;
		String query = "SELECT * FROM Product WHERE id = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					product = new Product();
					product.setId(rs.getInt("id"));
					product.setName(rs.getString("name"));
					product.setDescription(rs.getString("description"));
					product.setPrice(rs.getBigDecimal("price"));
					product.setImageUrl(rs.getString("image"));
					product.setQuantity(rs.getInt("quantity"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return product;
	}

	// New method to fetch products by search term and price range
	public List<Product> getProductsByCriteria(String queryParam, String[] priceRanges) {
		List<Product> products = new ArrayList<>();
		StringBuilder productQuery = new StringBuilder("SELECT * FROM Product");
		List<String> conditions = new ArrayList<>();
		List<Object> parameters = new ArrayList<>();

		try {
			// Add condition for search term if provided
			if (queryParam != null && !queryParam.trim().isEmpty()) {
				conditions.add("LOWER(name) LIKE ?");
				parameters.add("%" + queryParam.toLowerCase() + "%");
			}

			// Add conditions for selected price ranges if applicable
			if (priceRanges != null && !Arrays.asList(priceRanges).contains("all")) {
				List<String> priceConditions = new ArrayList<>();
				for (String range : priceRanges) {
					switch (range) {
					case "0-10000":
						priceConditions.add("price BETWEEN 0 AND 10000");
						break;
					case "10001-20000":
						priceConditions.add("price BETWEEN 10001 AND 20000");
						break;
					case "20001-30000":
						priceConditions.add("price BETWEEN 20001 AND 30000");
						break;
					case "30001-40000":
						priceConditions.add("price BETWEEN 30001 AND 40000");
						break;
					case "40001-50000":
						priceConditions.add("price BETWEEN 40001 AND 50000");
						break;
					}
				}
				if (!priceConditions.isEmpty()) {
					conditions.add("(" + String.join(" OR ", priceConditions) + ")");
				}
			}

			// Combine conditions if any exist
			if (!conditions.isEmpty()) {
				productQuery.append(" WHERE ").append(String.join(" AND ", conditions));
			}

			// Prepare statement and set parameters
			PreparedStatement stmt = conn.prepareStatement(productQuery.toString());
			for (int i = 0; i < parameters.size(); i++) {
				stmt.setObject(i + 1, parameters.get(i));
			}

			// Execute query and build product list
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Product product = new Product();
				product.setId(rs.getInt("id"));
				product.setName(rs.getString("name"));
				product.setDescription(rs.getString("description"));
				product.setPrice(rs.getBigDecimal("price"));
				product.setImageUrl(rs.getString("image"));
				product.setQuantity(rs.getInt("quantity"));
				products.add(product);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return products;
	}

	// Method to search products by name
	public List<Product> searchProductsByName(String name) {
		List<Product> products = new ArrayList<>();
		String query = "SELECT id, name, description, price, image, quantity FROM product WHERE LOWER(name) LIKE ?";

		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, "%" + name.toLowerCase() + "%");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Product product = new Product();
				product.setId(rs.getInt("id"));
				product.setName(rs.getString("name"));
				product.setDescription(rs.getString("description"));
				product.setPrice(rs.getBigDecimal("price"));
				product.setImageUrl(rs.getString("image"));
				product.setQuantity(rs.getInt("quantity"));
				products.add(product);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return products;
	}

}
