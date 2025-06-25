package util;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryBuilder {
    private final StringBuilder sql = new StringBuilder("SELECT * FROM products");
    private final StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM products");
    private final List<Object> parameters = new ArrayList<>();
    private boolean hasConditions = false;

    public QueryBuilder withSearchQuery(String query) {
        if (query != null && !query.trim().isEmpty()) {
            addCondition("name LIKE ?");
            parameters.add("%" + query + "%");
        }
        return this;
    }

    public QueryBuilder withTypeFilters(String[] typeIds) {
        if (typeIds != null && typeIds.length > 0 && !Arrays.asList(typeIds).contains("all")) {
            List<String> typeConditions = new ArrayList<>();
            for (String typeId : typeIds) {
                typeConditions.add("typeId = ?");
                parameters.add(Integer.parseInt(typeId));
            }
            addCondition("(" + String.join(" OR ", typeConditions) + ")");
        }
        return this;
    }

    public QueryBuilder withPriceRanges(String[] priceRanges) {
        if (priceRanges != null && priceRanges.length > 0 && !Arrays.asList(priceRanges).contains("all")) {
            List<String> priceConditions = new ArrayList<>();
            for (String range : priceRanges) {
                String[] bounds = range.split("-");
                if (bounds.length == 2) {
                    priceConditions.add("EXISTS (SELECT 1 FROM variants v WHERE v.pid = products.id AND v.price BETWEEN ? AND ?)");
                    parameters.add(new BigDecimal(bounds[0]));
                    parameters.add(new BigDecimal(bounds[1]));
                } else if (range.equals("over50000")) {
                    priceConditions.add("EXISTS (SELECT 1 FROM variants v WHERE v.pid = products.id AND v.price > ?)");
                    parameters.add(new BigDecimal("50000"));
                }
            }
            addCondition("(" + String.join(" OR ", priceConditions) + ")");
        }
        return this;
    }

    public QueryBuilder withPagination(int page, int pageSize) {
        sql.append(" LIMIT ? OFFSET ?");
        parameters.add(pageSize);
        parameters.add(page * pageSize);
        return this;
    }

    private void addCondition(String condition) {
        if (!hasConditions) {
            sql.append(" WHERE ");
            countSql.append(" WHERE ");
            hasConditions = true;
        } else {
            sql.append(" AND ");
            countSql.append(" AND ");
        }
        sql.append(condition);
        countSql.append(condition);
    }

    public String getSql() {
        return sql.toString();
    }

    public String getCountSql() {
        return countSql.toString();
    }

    public List<Object> getParameters() {
        return parameters;
    }
}
