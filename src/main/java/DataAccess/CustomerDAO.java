package DataAccess;


import ClassesOfTables.Customer;
import DataStructure.LinkedList;


import java.sql.*;

public class CustomerDAO {
    private Connection connection;

    public CustomerDAO() throws SQLException {
        String url = "jdbc:mysql://127.0.0.1:3306/croissanthouse";
        String username = "root";
        String password = "Trs@13081970";
        connection = DriverManager.getConnection(url, username, password);
    }

    public LinkedList getAllCustomers() throws SQLException {
        LinkedList customerList = new LinkedList();
        String query = "SELECT * FROM Client";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                // Create a new Customer object from the result set
                Customer customer = new Customer(
                        resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Email"),
                        resultSet.getString("Phone"),
                        resultSet.getString("Address")
                );
                customerList.addLast(customer);
            }
        }
        return customerList;
    }

    public void deleteCustomerById(int id) throws SQLException {
        String query = "DELETE FROM Client WHERE ID = " + id;

        try (Statement statement = connection.createStatement()) {

            // executeUpdate used for update,delete and insert
            statement.executeUpdate(query);
        }
    }

    public void addCustomer(Customer customer) throws SQLException {
        String query = "INSERT INTO Client (ID, Name, Email, Phone, Address) VALUES ("
                + customer.getId() + ", '"
                + customer.getName() + "', '"
                + customer.getEmail() + "', '"
                + customer.getPhone() + "', '"
                + customer.getAddress() + "')";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            String message = e.getMessage();
            System.out.println(message+"      hello");
            if (e.getMessage().contains("Duplicate entry")) {
                if (e.getMessage().contains("for key 'client.PRIMARY'")) {
                    throw new SQLException("The ID already exists. Please use a unique ID.");
                } else if (e.getMessage().contains("for key 'Email'")) {
                    throw new SQLException("The Email already exists. Please use a unique Email.");
                } else if (e.getMessage().contains("for key 'Phone'")) {
                    throw new SQLException("The Phone number already exists. Please use a unique Phone number.");
                }
            }
            throw e;
        }
    }


    public void updateCustomer(Customer customer) throws SQLException {
        String query = "UPDATE Client SET Name = ?, Email = ?, Phone = ?, Address = ? WHERE ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, customer.getName());
            preparedStatement.setString(2, customer.getEmail());
            preparedStatement.setString(3, customer.getPhone());
            preparedStatement.setString(4, customer.getAddress());
            preparedStatement.setInt(5, customer.getId());
            preparedStatement.executeUpdate();
        }
    }

    // Check if a customer exists by ID
    public boolean isCustomerExists(int clientID) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM Client WHERE ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, clientID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("count") > 0;
                }
            }
        }
        return false;
    }

    // Get a customer by ID
    public Customer getCustomerById(int clientID) throws SQLException {
        String query = "SELECT * FROM Client WHERE ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, clientID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Customer(
                            resultSet.getInt("ID"),
                            resultSet.getString("Name"),
                            resultSet.getString("Email"),
                            resultSet.getString("Phone"),
                            resultSet.getString("Address")
                    );
                }
            }
        }
        return null;
    }

    // Get customers by name (supports partial matches)
    public LinkedList getCustomersByName(String name) throws SQLException {
        LinkedList customerList = new LinkedList();
        String query = "SELECT * FROM Client WHERE Name LIKE ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + name + "%"); // Partial match
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Customer customer = new Customer(
                            resultSet.getInt("ID"),
                            resultSet.getString("Name"),
                            resultSet.getString("Email"),
                            resultSet.getString("Phone"),
                            resultSet.getString("Address")
                    );
                    customerList.addLast(customer);
                }
            }
        }
        return customerList;
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
