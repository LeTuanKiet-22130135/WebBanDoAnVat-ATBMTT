package newdao;

import newmodel.Product;
import newmodel.Variant;
import util.QueryBuilder;
import repository.product.ProductRepository;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.Query;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProductDAO {
    private final ProductRepository productRepo;
    private final VariantDAO variantDAO;

    public ProductDAO() {
        Jdbi jdbi = JdbiConfig.getJdbi();
        this.productRepo = jdbi.onDemand(ProductRepository.class);
        this.variantDAO = new VariantDAO();
    }

    // Row Mapper
    public static class ProductMapper implements RowMapper<Product> {
        @Override
        public Product map(ResultSet rs, StatementContext ctx) throws SQLException {
            Product product = new Product();
            product.setId(rs.getInt("id"));
            product.setCode(rs.getString("code"));
            product.setName(rs.getString("name"));
            product.setDesc(rs.getString("desc"));
            product.setImg(rs.getString("img"));
            product.setTypeId(rs.getInt("typeId"));
            product.setVariantId(rs.getInt("variantId"));
            product.setCreateAt(rs.getDate("createAt"));
            return product;
        }
    }

    // Refactored DAO methods
    public List<Product> getAllProducts() {
        List<Product> products = productRepo.getAllProducts();
        products.forEach(product -> {
            Variant variant = variantDAO.getFirstVariantByProductId(product.getId());
            if (variant != null) {
                product.setPrice(variant.getPrice());
            }
        });
        return products;
    }

    public Product getProductById(int id) {
        return productRepo.getProductById(id)
                .map(product -> {
                    List<Variant> variants = variantDAO.getVariantsByProductId(id);
                    product.setVariants(variants);

                    if (!variants.isEmpty()) {
                        product.setPrice(variants.get(0).getPrice());
                    }
                    return product;
                })
                .orElse(null);
    }

    public List<Product> getProductsByCriteria(String query, String[] priceRanges, String[] typeIds) {
        return getProductsByCriteriaWithPagination(query, priceRanges, typeIds, 0, Integer.MAX_VALUE);
    }

    public List<Product> getProductsByCriteriaWithPagination(
            String query, String[] priceRanges, String[] typeIds, int page, int pageSize
    ) {
        return JdbiConfig.getJdbi().withHandle(handle -> {
            QueryBuilder builder = new QueryBuilder()
                    .withSearchQuery(query)
                    .withTypeFilters(typeIds)
                    .withPriceRanges(priceRanges)
                    .withPagination(page, pageSize);

            // Create query and bind parameters positionally
            Query q = handle.createQuery(builder.getSql());
            List<Object> params = builder.getParameters();
            for (int i = 0; i < params.size(); i++) {
                q.bind(i, params.get(i));
            }

            return q.map((rs, ctx) -> {
                Product product = new ProductMapper().map(rs, ctx);
                Variant variant = variantDAO.getFirstVariantByProductId(product.getId());
                if (variant != null) {
                    product.setPrice(variant.getPrice());
                }
                return product;
            }).list();
        });
    }

    public int getTotalProductCount(String query, String[] priceRanges, String[] typeIds) {
        return JdbiConfig.getJdbi().withHandle(handle -> {
            QueryBuilder builder = new QueryBuilder()
                    .withSearchQuery(query)
                    .withTypeFilters(typeIds)
                    .withPriceRanges(priceRanges);

            // Create query and bind parameters positionally
            Query q = handle.createQuery(builder.getCountSql());
            List<Object> params = builder.getParameters();
            for (int i = 0; i < params.size(); i++) {
                q.bind(i, params.get(i));
            }

            return q.mapTo(Integer.class).one();
        });
    }


}