package repository.product;

import newdao.TypeDAO;
import newmodel.Type;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;
import java.util.Optional;

public interface TypeRepository {
    @SqlQuery("SELECT * FROM types")
    @RegisterRowMapper(TypeDAO.TypeMapper.class)
    List<Type> getAllTypes();

    @SqlQuery("SELECT * FROM types WHERE id = :id")
    @RegisterRowMapper(TypeDAO.TypeMapper.class)
    Optional<Type> getTypeById(@Bind("id") int id);
}
