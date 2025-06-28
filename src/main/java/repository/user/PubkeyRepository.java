package repository.user;

import newmodel.Pubkey;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Optional;

/**
 * Repository interface for pubkey operations.
 * This interface defines SQL operations for the 'pubkey' table.
 */
public interface PubkeyRepository {
    @SqlQuery("SELECT * FROM pubkey WHERE user_id = :userId")
    Optional<Pubkey> getPubkeyByUserId(@Bind("userId") int userId);

    @SqlUpdate("INSERT INTO pubkey (user_id, pubkey, avalible) VALUES (:userId, :pubkey, :available)")
    @GetGeneratedKeys("id")
    int createPubkey(@BindBean Pubkey pubkey);

    @SqlUpdate("UPDATE pubkey SET pubkey = :pubkey, avalible = :available WHERE user_id = :userId")
    boolean updatePubkey(@BindBean Pubkey pubkey);

    @SqlUpdate("DELETE FROM pubkey WHERE user_id = :userId")
    boolean deletePubkey(@Bind("userId") int userId);
}
