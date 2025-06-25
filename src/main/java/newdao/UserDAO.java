package newdao;

import newmodel.User;
import newmodel.UserProfile;
import repository.user.UserRepository;
import repository.user.UserProfileRepository;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.transaction.Transaction;
import util.PasswordUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDAO {
    private final UserRepository userRepo;
    private final UserProfileRepository profileRepo;

    public UserDAO() {
        Jdbi jdbi = JdbiConfig.getJdbi();
        this.userRepo = jdbi.onDemand(UserRepository.class);
        this.profileRepo = jdbi.onDemand(UserProfileRepository.class);
    }

    // Row Mappers
    public static class UserMapper implements RowMapper<User> {
        @Override
        public User map(ResultSet rs, StatementContext ctx) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setUsername(rs.getString("username"));
            user.setHashedPassword(rs.getString("hashed_password"));
            user.setEmail(rs.getString("email"));
            user.setStatus(rs.getInt("status"));
            user.setToken(rs.getString("token"));
            return user;
        }
    }

    public static class UserProfileMapper implements RowMapper<UserProfile> {
        @Override
        public UserProfile map(ResultSet rs, StatementContext ctx) throws SQLException {
            UserProfile profile = new UserProfile();
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

    // Refactored DAO methods
    public List<User> getAllUsers() {
        return userRepo.getAllUsers();
    }

    public boolean checkUser(String username) {
        return userRepo.checkUserExists(username);
    }

    public boolean checkPassword(String username, String password) {
        return userRepo.getHashedPassword(username)
                .map(hashedPassword -> PasswordUtil.comparePassword(password, hashedPassword))
                .orElse(false);
    }

    public User getUserByUsername(String username) {
        return userRepo.getUserByUsername(username).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepo.getUserByEmail(email).orElse(null);
    }

    public User getUserByToken(String token) {
        return userRepo.getUserByToken(token).orElse(null);
    }

    public boolean updateUserToken(int userId, String token) {
        return userRepo.updateToken(userId, token);
    }

    public int getUserIdByUsername(String username) {
        return userRepo.getUserIdByUsername(username).orElse(-1);
    }

    public UserProfile getUserProfileByUserId(int userId) {
        return profileRepo.getProfileByUserId(userId)
                .orElseGet(() -> createUserProfile(userId));
    }

    private UserProfile createUserProfile(int userId) {
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        // Initialize with empty values
        profile.setFirstName("");
        profile.setLastName("");
        profile.setEmail("");
        profile.setMobileNo("");
        profile.setAddressLine1("");
        profile.setAddressLine2("");
        profile.setCountry("");
        profile.setCity("");
        profile.setState("");
        profile.setZipCode("");

        try {
            int id = profileRepo.createProfile(profile);
            profile.setId(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return profile;
    }

    public boolean updateUserProfile(UserProfile profile) {
        return profileRepo.updateProfile(profile);
    }

    public boolean updateUserPassword(String username, String newHashedPassword) {
        return userRepo.updatePassword(username, newHashedPassword);
    }

    @Transaction
    public void addUserWithProfile(String username, String hashedPassword,
                                   String firstName, String lastName, String email) {
        int userId = userRepo.createUser(username, hashedPassword, email, 1);

        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setEmail(email);

        profileRepo.createProfile(profile);
    }

    @Transaction
    public int addUserWithOAuth(String email, String firstName, String lastName, String token) {
        // Generate unique username
        String baseUsername = email.split("@")[0];
        String username = baseUsername;
        int counter = 1;

        while (userRepo.checkUserExists(username)) {
            username = baseUsername + counter++;
        }

        int userId = userRepo.createUser(username, null, email, 1);

        // Create profile
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setEmail(email);
        profileRepo.createProfile(profile);

        // Update token
        userRepo.updateToken(userId, token);

        return userId;
    }
}