package repository.product;

import newdao.ProductDAO;
import newmodel.Product;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    @SqlQuery("SELECT * FROM products")
    @RegisterRowMapper(ProductDAO.ProductMapper.class)
    List<Product> getAllProducts();

    @SqlQuery("SELECT * FROM products WHERE id = :id")
    @RegisterRowMapper(ProductDAO.ProductMapper.class)
    Optional<Product> getProductById(@Bind("id") int id);
}
