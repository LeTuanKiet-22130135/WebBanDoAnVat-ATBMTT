package repository.review;

import newdao.ReviewDAO;
import newmodel.Review;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {
    @SqlQuery("SELECT r.*, u.username FROM reviews r " +
            "JOIN users u ON r.uId = u.id " +
            "WHERE r.pId = :productId " +
            "ORDER BY r.id DESC")
    @RegisterRowMapper(ReviewDAO.ReviewMapper.class)
    List<Review> getReviewsByProductId(@Bind("productId") int productId);

    @SqlQuery("SELECT AVG(rating) FROM reviews WHERE pId = :productId")
    Optional<Double> getAverageRating(@Bind("productId") int productId);

    @SqlQuery("SELECT COUNT(*) FROM reviews WHERE pId = :productId")
    int getReviewCount(@Bind("productId") int productId);

    @SqlUpdate("INSERT INTO reviews (uId, pId, rating, comment) " +
            "VALUES (:userId, :productId, :rating, :comment)")
    @GetGeneratedKeys("id")
    int addReview(
            @Bind("userId") int userId,
            @Bind("productId") int productId,
            @Bind("rating") int rating,
            @Bind("comment") String comment
    );

    @SqlQuery("SELECT 1 FROM reviews WHERE uId = :userId AND pId = :productId")
    Optional<Integer> hasUserReviewed(
            @Bind("userId") int userId,
            @Bind("productId") int productId
    );

    @SqlUpdate("UPDATE reviews SET rating = :rating, comment = :comment " +
            "WHERE id = :reviewId")
    boolean updateReview(
            @Bind("reviewId") int reviewId,
            @Bind("rating") int rating,
            @Bind("comment") String comment
    );

    @SqlUpdate("DELETE FROM reviews WHERE id = :reviewId")
    boolean deleteReview(@Bind("reviewId") int reviewId);
}
