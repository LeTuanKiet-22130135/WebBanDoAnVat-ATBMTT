package newdao;

import newmodel.Review;
import newmodel.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object for Review entities
 * Handles database operations related to product reviews
 */
public class ReviewDAO {
    
    private final UserDAO userDAO = new UserDAO();
    
    /**
     * Gets all reviews for a specific product
     * 
     * @param productId The ID of the product
     * @return A list of Review objects
     */
    public List<Review> getReviewsByProductId(int productId) {
        List<Review> reviews = new ArrayList<>();
        String query = "SELECT r.*, u.username FROM reviews r " +
                       "JOIN users u ON r.uId = u.id " +
                       "WHERE r.pId = ? " +
                       "ORDER BY r.id DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Review review = new Review();
                    review.setId(rs.getInt("id"));
                    review.setUId(rs.getInt("uId"));
                    review.setPId(rs.getInt("pId"));
                    review.setRating(rs.getInt("rating"));
                    review.setComment(rs.getString("comment"));
                    review.setUsername(rs.getString("username"));
                    
                    // Use placeholder image for avatar
                    review.setUserAvatar("https://placehold.co/45x45");
                    
                    // Format current date as placeholder
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                    review.setDate(sdf.format(new Date()));
                    
                    reviews.add(review);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reviews;
    }
    
    /**
     * Gets the average rating for a specific product
     * 
     * @param productId The ID of the product
     * @return The average rating (0.0 if no reviews)
     */
    public double getAverageRatingByProductId(int productId) {
        String query = "SELECT AVG(rating) as avg_rating FROM reviews WHERE pId = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avg_rating");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0.0;
    }
    
    /**
     * Gets the count of reviews for a specific product
     * 
     * @param productId The ID of the product
     * @return The number of reviews
     */
    public int getReviewCountByProductId(int productId) {
        String query = "SELECT COUNT(*) as count FROM reviews WHERE pId = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Adds a new review for a product
     * 
     * @param userId The ID of the user submitting the review
     * @param productId The ID of the product being reviewed
     * @param rating The rating (1-5)
     * @param comment The review comment
     * @return The ID of the newly created review, or -1 if creation failed
     */
    public int addReview(int userId, int productId, int rating, String comment) {
        String query = "INSERT INTO reviews (uId, pId, rating, comment) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            stmt.setInt(3, rating);
            stmt.setString(4, comment);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return -1;
    }
    
    /**
     * Checks if a user has already reviewed a product
     * 
     * @param userId The ID of the user
     * @param productId The ID of the product
     * @return true if the user has already reviewed the product, false otherwise
     */
    public boolean hasUserReviewedProduct(int userId, int productId) {
        String query = "SELECT 1 FROM reviews WHERE uId = ? AND pId = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Returns true if a record is found
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Updates an existing review
     * 
     * @param reviewId The ID of the review to update
     * @param rating The new rating
     * @param comment The new comment
     * @return true if the update was successful, false otherwise
     */
    public boolean updateReview(int reviewId, int rating, String comment) {
        String query = "UPDATE reviews SET rating = ?, comment = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, rating);
            stmt.setString(2, comment);
            stmt.setInt(3, reviewId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Deletes a review
     * 
     * @param reviewId The ID of the review to delete
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteReview(int reviewId) {
        String query = "DELETE FROM reviews WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, reviewId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
}