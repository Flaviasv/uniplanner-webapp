package uniplanner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    
    private static final String URL = "jdbc:hsqldb:file:database/uniplanner_db;shutdown=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("HSQLDB Driver not found!");
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    public static void createTables() {
        String userTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INT PRIMARY KEY,"
                + "username VARCHAR(50) UNIQUE NOT NULL,"
                + "password VARCHAR(255) NOT NULL,"
                + "email VARCHAR(100) UNIQUE NOT NULL"
                + ")";
        
        String taskTable = "CREATE TABLE IF NOT EXISTS tasks ("
                + "id INT PRIMARY KEY,"
                + "title VARCHAR(100) NOT NULL,"
                + "dueDate DATE,"
                + "priority VARCHAR(10) CHECK (priority IN ('High', 'Medium', 'Low')),"
                + "status VARCHAR(20) CHECK (status IN ('To Do', 'In Progress', 'Completed')),"
                + "course VARCHAR(50),"
                + "userId INT NOT NULL,"
                + "FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE"
                + ")";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(userTable);
            stmt.executeUpdate(taskTable);
            System.out.println("Tables 'users' and 'tasks' created or already exist.");
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
}