package DataAccess;

import ClassesOfTables.Supplier;
import DataStructure.LinkedList;

import java.sql.*;

public class SupplierDAO {
    private Connection connection;

    // Constructor to establish a connection to the database
    public SupplierDAO() throws SQLException {
        String url = "jdbc:mysql://127.0.0.1:3306/croissanthouse";
        String username = "root";
        String password = "Trs@13081970";
        connection = DriverManager.getConnection(url, username, password);
    }

    // Retrieve all supplier from the database
    public LinkedList getAllSuppliers() throws SQLException {
        LinkedList supplierList = new LinkedList();
        String query = "SELECT * FROM supplier";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                // Create a new Supplier object from the result set
                Supplier supplier = new Supplier(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("phone"),
                        resultSet.getString("email")
                );
                supplierList.addLast(supplier);
            }
        }
        return supplierList;
    }

    // Delete a supplier by their ID
    public void deleteSupplierById(int id) throws SQLException {
        String query = "DELETE FROM supplier WHERE id = " + id;

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        }
    }

    // Add a new supplier to the database
    public void addSupplier(Supplier supplier) throws SQLException {
        String query = "INSERT INTO supplier (id, name, phone, email) VALUES ("
                + supplier.getId() + ", '"
                + supplier.getName() + "', '"
                + supplier.getPhone() + "', '"
                + supplier.getEmail() + "')";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                if (e.getMessage().contains("for key 'suppliers.PRIMARY'")) {
                    throw new SQLException("The ID already exists. Please use a unique ID.");
                } else if (e.getMessage().contains("for key 'email'")) {
                    throw new SQLException("The Email already exists. Please use a unique Email.");
                } else if (e.getMessage().contains("for key 'phone'")) {
                    throw new SQLException("The Phone number already exists. Please use a unique Phone number.");
                }
            }
            throw e;
        }
    }

    public void updateSupplier(Supplier supplier) throws SQLException {
        String query = "UPDATE supplier SET name = ?, phone = ?, email = ? WHERE id = ?" ;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, supplier.getName());
            statement.setString(2, supplier.getPhone());
            statement.setString(3, supplier.getEmail());
            statement.setInt(4, supplier.getId());
            statement.executeUpdate();
        }
    }

    public int getTotalSuppliers() throws SQLException {
        String query = "SELECT COUNT(*) AS TotalSuppliers FROM Supplier";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getInt("TotalSuppliers");
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
