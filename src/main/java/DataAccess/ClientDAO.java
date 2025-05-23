package DataAccess;

import DataStructure.LinkedList;

import java.sql.*;

public class ClientDAO {
    private Connection connection;

    public ClientDAO() throws SQLException {
        String url = "jdbc:mysql://127.0.0.1:3306/croissanthouse";
        String username = "root";
        String password = "Trs@13081970";
        connection = DriverManager.getConnection(url, username, password);
    }

    public int getTotalClients() throws SQLException {
        String query = "SELECT COUNT(*) AS TotalClients FROM Client";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getInt("TotalClients");
            }
        }
        return 0;
    }


    // Close the database connection
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
