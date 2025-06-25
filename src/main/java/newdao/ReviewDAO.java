package newdao;

import newmodel.Review;
import repository.review.ReviewRepository;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReviewDAO {
    private final ReviewRepository reviewRepo;

    public ReviewDAO() {
        Jdbi jdbi = JdbiConfig.getJdbi();
        this.reviewRepo = jdbi.onDemand(ReviewRepository.class);
    }

    // Row Mapper with placeholder values
    public static class ReviewMapper implements RowMapper<Review> {
        @Override
        public Review map(ResultSet rs, StatementContext ctx) throws SQLException {
            Review review = new Review();
            review.setId(rs.getInt("id"));
            review.setUId(rs.getInt("uId"));
            review.setPId(rs.getInt("pId"));
            review.setRating(rs.getInt("rating"));
            review.setComment(rs.getString("comment"));
            review.setUsername(rs.getString("username"));

            // Set placeholders (not from database)
            review.setUserAvatar("https://placehold.co/45x45");

            // Format current date as placeholder
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            review.setDate(sdf.format(new Date()));

            return review;
        }
    }

    // Refactored DAO methods
    public List<Review> getReviewsByProductId(int productId) {
        return reviewRepo.getReviewsByProductId(productId);
    }

    public double getAverageRatingByProductId(int productId) {
        return reviewRepo.getAverageRating(productId).orElse(0.0);
    }

    public int getReviewCountByProductId(int productId) {
        return reviewRepo.getReviewCount(productId);
    }

    public int addReview(int userId, int productId, int rating, String comment) {
        return reviewRepo.addReview(userId, productId, rating, comment);
    }

    public boolean hasUserReviewedProduct(int userId, int productId) {
        return reviewRepo.hasUserReviewed(userId, productId).isPresent();
    }

    public boolean updateReview(int reviewId, int rating, String comment) {
        return reviewRepo.updateReview(reviewId, rating, comment);
    }

    public boolean deleteReview(int reviewId) {
        return reviewRepo.deleteReview(reviewId);
    }
}