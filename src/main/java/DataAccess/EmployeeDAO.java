package DataAccess;

import ClassesOfTables.Employee;
import DataStructure.LinkedList;

import java.sql.*;

public class EmployeeDAO {
    private Connection connection;

    public EmployeeDAO() throws SQLException {
        String url = "jdbc:mysql://127.0.0.1:3306/croissanthouse";
        String username = "root";
        String password = "Trs@13081970";
        connection = DriverManager.getConnection(url, username, password);
    }

    // LOGIN
    // Authenticate employee using username and password
    public boolean authenticate(String username, String password) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM Employee WHERE Name = ? AND Password = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("count") > 0;
                }
            }
        }
        return false;
    }

    // LOGIN
    // Check if the user is a Manager or Employee
    public String getUserRole(String username) throws SQLException {
        String query = "SELECT ManagerID FROM Employee WHERE Name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {

                    // The getObject method is used because it can return null if the column value is NULL
                    // Integer.class tells the method to return the value as an Integer object.
                    Integer managerID = resultSet.getObject("ManagerID", Integer.class);
                    return (managerID == null) ? "Manager" : "Employee";
                }
            }
        }
        return "Unknown";
    }


    // LOGIN
    public int getEmployeeID(String username) throws SQLException {
        String query = "SELECT ID FROM Employee WHERE Name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("ID");
            }
        }
        return -1; // Return -1 if the employee is not found
    }


    // Read employees from the database
    public LinkedList readEmployees() throws SQLException {
        LinkedList employeeList = new LinkedList();
        String query = "SELECT * FROM Employee";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Employee employee = new Employee(
                        resultSet.getInt("ID"),
                        resultSet.getString("Name"),
                        resultSet.getString("Email"),
                        resultSet.getString("Password"),
                        resultSet.getString("Phone"),
                        resultSet.getDouble("Salary"),
                        resultSet.getObject("ManagerID") == null ? null : resultSet.getInt("ManagerID")
                );
                employeeList.addLast(employee);
            }
        } catch (SQLException e) {
            System.err.println("Error in readEmployees: " + e.getMessage());
            throw e; // Rethrow to trigger the alert
        }
        return employeeList;
    }


    // Add a new employee
    public void addEmployee(Employee employee) throws SQLException {
        String query = "INSERT INTO Employee (ID, Name, Email, Password, Phone, Salary, ManagerID) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, employee.getId());
            preparedStatement.setString(2, employee.getName());
            preparedStatement.setString(3, employee.getEmail());
            preparedStatement.setString(4, employee.getPassword());
            preparedStatement.setString(5, employee.getPhone());
            preparedStatement.setDouble(6, employee.getSalary());
            if (employee.getManagerId() == null) {
                preparedStatement.setNull(7, Types.INTEGER);
            } else {
                preparedStatement.setInt(7, employee.getManagerId());
            }
            preparedStatement.executeUpdate();
        }
    }

    // Update an existing employee
    public void updateEmployee(Employee employee) throws SQLException {
        String query = "UPDATE Employee SET Name = ?, Email = ?, Password = ?, Phone = ?, Salary = ?, ManagerID = ? WHERE ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, employee.getName());
            preparedStatement.setString(2, employee.getEmail());
            preparedStatement.setString(3, employee.getPassword());
            preparedStatement.setString(4, employee.getPhone());
            preparedStatement.setDouble(5, employee.getSalary());
            if (employee.getManagerId() == null) {
                preparedStatement.setNull(6, Types.INTEGER); // This tells the database to insert a NULL value into the ManagerID column, which is of type INTEGER in your SQL table.
            } else {
                preparedStatement.setInt(6, employee.getManagerId());
            }
            preparedStatement.setInt(7, employee.getId());
            preparedStatement.executeUpdate();
        }
    }

    // Delete an employee by ID
    public void deleteEmployeeById(int id) throws SQLException {
        String query = "DELETE FROM Employee WHERE ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    public boolean isManager(int id) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM Employee WHERE ID = ? AND ManagerID IS NULL";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("count") > 0; // Returns true if the ID belongs to a manager
                }
            }
        }
        return false; // If no result is found, the ID does not belong to a manager
    }




    public int getTotalEmployees() throws SQLException {
        String query = "SELECT COUNT(*) AS TotalEmployees FROM Employee";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getInt("TotalEmployees");
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
