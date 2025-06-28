package newdao;

import newmodel.Pubkey;
import repository.user.PubkeyRepository;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class PubkeyDAO {
    private final PubkeyRepository pubkeyRepo;

    public PubkeyDAO() {
        Jdbi jdbi = JdbiConfig.getJdbi();
        this.pubkeyRepo = jdbi.onDemand(PubkeyRepository.class);
    }

    // Row Mapper
    public static class PubkeyMapper implements RowMapper<Pubkey> {
        @Override
        public Pubkey map(ResultSet rs, StatementContext ctx) throws SQLException {
            Pubkey pubkey = new Pubkey();
            pubkey.setId(rs.getInt("id"));
            pubkey.setUserId(rs.getInt("user_id"));
            pubkey.setPubkey(rs.getBytes("pubkey"));
            pubkey.setAvailable(rs.getBoolean("avalible"));
            return pubkey;
        }
    }

    // DAO methods
    public Pubkey getPubkeyByUserId(int userId) {
        return pubkeyRepo.getPubkeyByUserId(userId).orElse(null);
    }

    public boolean hasPubkey(int userId) {
        return pubkeyRepo.getPubkeyByUserId(userId).isPresent();
    }

    public int createPubkey(Pubkey pubkey) {
        return pubkeyRepo.createPubkey(pubkey);
    }

    public boolean updatePubkey(Pubkey pubkey) {
        return pubkeyRepo.updatePubkey(pubkey);
    }

    public boolean deletePubkey(int userId) {
        return pubkeyRepo.deletePubkey(userId);
    }

    // Helper method to create a new pubkey for a user
    public Pubkey createNewPubkey(int userId, byte[] pubkeyData) {
        Pubkey pubkey = new Pubkey();
        pubkey.setUserId(userId);
        pubkey.setPubkey(pubkeyData);
        pubkey.setAvailable(true);
        
        int id = pubkeyRepo.createPubkey(pubkey);
        pubkey.setId(id);
        
        return pubkey;
    }
}