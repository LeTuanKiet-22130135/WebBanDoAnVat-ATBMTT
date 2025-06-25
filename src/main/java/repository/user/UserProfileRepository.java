package repository.user;

import newdao.UserDAO;
import newmodel.UserProfile;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Optional;

public interface UserProfileRepository {
    @SqlQuery("SELECT * FROM userprofile WHERE user_id = :userId")
    @RegisterRowMapper(UserDAO.UserProfileMapper.class)
    Optional<UserProfile> getProfileByUserId(@Bind("userId") int userId);

    @SqlUpdate("INSERT INTO userprofile (user_id, first_name, last_name, email, mobile_no, " +
            "address_line1, address_line2, country, city, state, zip_code) " +
            "VALUES (:userId, :firstName, :lastName, :email, :mobileNo, " +
            ":addressLine1, :addressLine2, :country, :city, :state, :zipCode)")
    @GetGeneratedKeys("id")
    int createProfile(@BindBean UserProfile profile);

    @SqlUpdate("UPDATE userprofile SET first_name = :firstName, last_name = :lastName, " +
            "email = :email, mobile_no = :mobileNo, address_line1 = :addressLine1, " +
            "address_line2 = :addressLine2, country = :country, city = :city, " +
            "state = :state, zip_code = :zipCode WHERE user_id = :userId")
    boolean updateProfile(@BindBean UserProfile profile);
}
