package repository.product;

import newdao.VariantDAO;
import newmodel.Variant;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;
import java.util.Optional;

public interface VariantRepository {
    @SqlQuery("SELECT * FROM variants WHERE pid = :productId ORDER BY id ASC LIMIT 1")
    @RegisterRowMapper(VariantDAO.VariantMapper.class)
    Optional<Variant> getFirstVariantByProductId(@Bind("productId") int productId);

    @SqlQuery("SELECT * FROM variants WHERE pid = :productId ORDER BY id ASC")
    @RegisterRowMapper(VariantDAO.VariantMapper.class)
    List<Variant> getVariantsByProductId(@Bind("productId") int productId);
}
