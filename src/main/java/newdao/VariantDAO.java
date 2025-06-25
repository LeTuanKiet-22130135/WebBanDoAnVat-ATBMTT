package newdao;

import newmodel.Variant;
import repository.product.VariantRepository;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class VariantDAO {
    private final VariantRepository variantRepo;

    public VariantDAO() {
        Jdbi jdbi = JdbiConfig.getJdbi();
        this.variantRepo = jdbi.onDemand(VariantRepository.class);
    }

    // Row Mapper
    public static class VariantMapper implements RowMapper<Variant> {
        @Override
        public Variant map(ResultSet rs, StatementContext ctx) throws SQLException {
            Variant variant = new Variant();
            variant.setId(rs.getInt("id"));
            variant.setPid(rs.getInt("pid"));
            variant.setName(rs.getString("name"));
            variant.setPrice(rs.getBigDecimal("price"));
            return variant;
        }
    }

    // Refactored DAO methods
    public Variant getFirstVariantByProductId(int productId) {
        return variantRepo.getFirstVariantByProductId(productId).orElse(null);
    }

    public List<Variant> getVariantsByProductId(int productId) {
        return variantRepo.getVariantsByProductId(productId);
    }
}