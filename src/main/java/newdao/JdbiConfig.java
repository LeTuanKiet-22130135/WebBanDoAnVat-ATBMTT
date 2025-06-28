package newdao;

import newmodel.Pubkey;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.jdbi.v3.core.statement.SqlLogger;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuration class for JDBI
 * Provides a singleton Jdbi instance for database operations
 */
public class JdbiConfig {
    private static final Logger LOGGER = Logger.getLogger(JdbiConfig.class.getName());
    private static Jdbi jdbi;

    /**
     * Gets a configured Jdbi instance
     * @return The Jdbi instance
     */
    public static Jdbi getJdbi() {
        if (jdbi == null) {
            // Create Jdbi instance that uses the database connection
            jdbi = Jdbi.create(() -> {
                try {
                    // Use the same connection mechanism as DBConnection
                    return DBConnection.getConnection();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to get database connection", e);
                    throw new SQLException("Failed to get database connection", e);
                }
            });

            // Configure JDBI
            jdbi.installPlugin(new SqlObjectPlugin());

            // Add SQL logger for debugging (optional)
            jdbi.setSqlLogger(new SqlLogger() {
                @Override
                public void logAfterExecution(StatementContext context) {
                    LOGGER.log(Level.FINE, 
                        String.format("Executed SQL: %s, Execution time: %d ms", 
                            context.getRenderedSql(),
                            context.getElapsedTime(ChronoUnit.MILLIS)));

            jdbi.registerRowMapper(ConstructorMapper.factory(CartDAO.CartItemInfo.class));
            // Register custom row mapper for Pubkey
            jdbi.registerRowMapper(newmodel.Pubkey.class, new PubkeyDAO.PubkeyMapper());
                }
            });
        }
        return jdbi;
    }
}
