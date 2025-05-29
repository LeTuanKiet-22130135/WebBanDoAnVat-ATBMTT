package dao;

import java.sql.Connection;
import org.apache.commons.dbcp2.BasicDataSource;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/webanvat";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    private static BasicDataSource dataSource;

    static {
        // Initialize the connection pool
        dataSource = new BasicDataSource();
        dataSource.setUrl(URL);
        dataSource.setUsername(USER);
        dataSource.setPassword(PASSWORD);
        
        // Pool settings
        dataSource.setMinIdle(0);         // Minimum number of idle connections
        dataSource.setMaxIdle(10);        // Maximum number of idle connections
        dataSource.setMaxTotal(20);       // Maximum total connections
    }

    public static Connection getConnection() {
        try {
        	Class.forName("com.mysql.cj.jdbc.Driver");
            return dataSource.getConnection(); // Get a connection from the pool
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
