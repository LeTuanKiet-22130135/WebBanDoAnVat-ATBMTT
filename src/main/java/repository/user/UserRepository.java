package repository.user;

import newdao.UserDAO;
import newmodel.User;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    @SqlQuery("SELECT id FROM users WHERE username = :username")
    Optional<Integer> findUserIdByUsername(@Bind("username") String username);

    @SqlQuery("SELECT id, username, email, status FROM users")
    @RegisterRowMapper(UserDAO.UserMapper.class)
    List<User> getAllUsers();

    @SqlQuery("SELECT 1 FROM users WHERE username = :username")
    boolean checkUserExists(@Bind("username") String username);

    @SqlQuery("SELECT hashed_password FROM users WHERE username = :username")
    Optional<String> getHashedPassword(@Bind("username") String username);

    @SqlQuery("SELECT * FROM users WHERE username = :username")
    @RegisterRowMapper(UserDAO.UserMapper.class)
    Optional<User> getUserByUsername(@Bind("username") String username);

    @SqlQuery("SELECT * FROM users WHERE email = :email")
    @RegisterRowMapper(UserDAO.UserMapper.class)
    Optional<User> getUserByEmail(@Bind("email") String email);

    @SqlQuery("SELECT * FROM users WHERE token = :token")
    @RegisterRowMapper(UserDAO.UserMapper.class)
    Optional<User> getUserByToken(@Bind("token") String token);

    @SqlUpdate("UPDATE users SET token = :token WHERE id = :userId")
    boolean updateToken(@Bind("userId") int userId, @Bind("token") String token);

    @SqlQuery("SELECT id FROM users WHERE username = :username")
    Optional<Integer> getUserIdByUsername(@Bind("username") String username);

    @SqlUpdate("UPDATE users SET hashed_password = :password WHERE username = :username")
    boolean updatePassword(@Bind("username") String username, @Bind("password") String newHashedPassword);

    @SqlUpdate("INSERT INTO users (username, hashed_password, email, status) " +
            "VALUES (:username, :password, :email, :status)")
    @GetGeneratedKeys("id")
    int createUser(
            @Bind("username") String username,
            @Bind("password") String hashedPassword,
            @Bind("email") String email,
            @Bind("status") int status
    );
}
