package newdao;

import newmodel.Type;
import repository.product.TypeRepository;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TypeDAO {
    private final TypeRepository typeRepo;

    public TypeDAO() {
        Jdbi jdbi = JdbiConfig.getJdbi();
        this.typeRepo = jdbi.onDemand(TypeRepository.class);
    }

    // Row Mapper
    public static class TypeMapper implements RowMapper<Type> {
        @Override
        public Type map(ResultSet rs, StatementContext ctx) throws SQLException {
            Type type = new Type();
            type.setId(rs.getInt("id"));
            type.setName(rs.getString("name"));
            return type;
        }
    }

    // Refactored DAO methods
    public List<Type> getAllTypes() {
        return typeRepo.getAllTypes();
    }

    public Type getTypeById(int id) {
        return typeRepo.getTypeById(id).orElse(null);
    }
}