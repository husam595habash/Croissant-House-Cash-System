package DataAccess;

import ClassesOfTables.Order;
import ClassesOfTables.OrderDetails;
import DataStructure.LinkedList;

import java.sql.*;

public class OrderDAO {
    private Connection connection;

    public OrderDAO() throws SQLException {
        String url = "jdbc:mysql://127.0.0.1:3306/croissanthouse";
        String username = "root";
        String password = "Trs@13081970";
        connection = DriverManager.getConnection(url, username, password);
    }

    public int getNextOrderID() throws SQLException {
        String query = "SELECT MAX(OrderID) AS MaxOrderID FROM Orders";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getInt("MaxOrderID") + 1;
            }
        }
        return 1; // Default to 1 if no orders exist
    }

    public void addOrder(int employeeID, int clientID, Date orderDate, double totalAmount) throws SQLException {
        String query = "INSERT INTO Orders (Employee_ID, Client_ID, OrderDate, TotalAmount) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, employeeID);
            preparedStatement.setInt(2, clientID);
            preparedStatement.setDate(3, orderDate);
            preparedStatement.setDouble(4, totalAmount);
            preparedStatement.executeUpdate();
        }
    }

    public int fetchClientID(String phoneNumber) throws SQLException {
        String query = "SELECT ID FROM Client WHERE Phone = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, phoneNumber);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("ID");
            }
        }
        return -1; // Client not found
    }

    public void addOrderDetails(int orderID, int itemID, int quantity, double price) throws SQLException {
        String query = "INSERT INTO OrderDetails (OrderID, ItemID, Quantity, Price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderID);
            statement.setInt(2, itemID);
            statement.setInt(3, quantity);
            statement.setDouble(4, price);
            statement.executeUpdate();
        }
    }

    // Fetch orders by Client ID
    public LinkedList getOrdersByClientID(int clientID) throws SQLException {
        LinkedList orderList = new LinkedList();
        String query = "SELECT * FROM Orders WHERE Client_ID = ? ORDER BY OrderDate DESC";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, clientID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Order order = new Order(
                            resultSet.getInt("OrderID"),
                            resultSet.getInt("Employee_ID"),
                            resultSet.getInt("Client_ID"),
                            resultSet.getDate("OrderDate"),
                            resultSet.getDouble("TotalAmount")
                    );
                    orderList.addLast(order);
                }
            }
        }
        return orderList;
    }

    // Fetch order details by Order ID
    public LinkedList getOrderDetails(int orderID) throws SQLException {
        LinkedList orderDetailsList = new LinkedList();
        String query = "SELECT OD.ItemID, I.Name AS ItemName, C.Name AS CategoryName, OD.Price AS PricePerUnit, OD.Quantity, (OD.Price * OD.Quantity) AS TotalPrice " +
                "FROM OrderDetails OD " +
                "JOIN Items I ON OD.ItemID = I.ItemID " +
                "JOIN Category C ON I.CategoryID = C.CategoryID " +
                "WHERE OD.OrderID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, orderID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    OrderDetails orderDetails = new OrderDetails(
                            resultSet.getInt("ItemID"),
                            resultSet.getString("ItemName"),
                            resultSet.getString("CategoryName"),
                            resultSet.getDouble("PricePerUnit"),
                            resultSet.getInt("Quantity")
                    );
                    orderDetailsList.addLast(orderDetails);
                }
            }
        }
        return orderDetailsList;
    }

    public LinkedList getCategoryRevenue() throws SQLException {
        LinkedList categoryRevenueList = new LinkedList();
        String query = "SELECT C.Name AS CategoryName, SUM(OD.Price * OD.Quantity) AS TotalRevenue " +
                "FROM OrderDetails OD, Items I, Category C " +
                "WHERE OD.ItemID = I.ItemID " +
                "AND I.CategoryID = C.CategoryID " +
                "GROUP BY C.Name";


        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String categoryName = resultSet.getString("CategoryName");
                double totalRevenue = resultSet.getDouble("TotalRevenue");
                categoryRevenueList.addLast(new String[]{categoryName, String.valueOf(totalRevenue)});
            }
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
        return categoryRevenueList;
    }


    public LinkedList getDailyRevenue() throws SQLException {
        LinkedList dailyRevenueList = new LinkedList();
        String query = "SELECT OrderDate, SUM(TotalAmount) AS DailyRevenue " +
                "FROM Orders " +
                "GROUP BY OrderDate " +
                "ORDER BY OrderDate";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Date date = resultSet.getDate("OrderDate");
                double revenue = resultSet.getDouble("DailyRevenue");
                dailyRevenueList.addLast(new Object[]{date.toString(), revenue});
            }
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
        return dailyRevenueList;
    }


    public LinkedList getRevenuePerEmployee() throws SQLException {
        LinkedList revenuePerEmployeeList = new LinkedList();
        String query = "SELECT E.Name AS EmployeeName, SUM(O.TotalAmount) AS TotalRevenue " +
                "FROM Orders O, Employee E " +
                "WHERE O.Employee_ID = E.ID " +
                "GROUP BY E.Name;";


        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String employeeName = resultSet.getString("EmployeeName");
                double totalRevenue = resultSet.getDouble("TotalRevenue");
                revenuePerEmployeeList.addLast(new String[]{employeeName, String.valueOf(totalRevenue)});
            }
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
        return revenuePerEmployeeList;
    }

    public double getTotalRevenue() throws SQLException {
        String query = "SELECT SUM(TotalAmount) AS TotalRevenue FROM Orders";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getDouble("TotalRevenue");
            }
        }
        return 0;
    }

    public String getHighRevenueCategories() throws SQLException {
        StringBuilder result = new StringBuilder();
        String query = "SELECT C.Name AS CategoryName, SUM(OD.Price * OD.Quantity) AS TotalRevenue\n" +
                "FROM OrderDetails OD, Items I, Category C\n" +
                "WHERE OD.ItemID = I.ItemID\n" +
                "  AND I.CategoryID = C.CategoryID\n" +
                "GROUP BY C.Name\n" +
                "HAVING SUM(OD.Price * OD.Quantity) = (\n" +
                "    SELECT Max(TotalRevenue)\n" +
                "    FROM (\n" +
                "        SELECT SUM(OD2.Price * OD2.Quantity) AS TotalRevenue\n" +
                "        FROM OrderDetails OD2, Items I2, Category C2\n" +
                "        WHERE OD2.ItemID = I2.ItemID\n" +
                "          AND I2.CategoryID = C2.CategoryID\n" +
                "        GROUP BY C2.Name\n" +
                "    ) AS SubQuery\n" +
                ");";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String categoryName = resultSet.getString("CategoryName");
                double totalRevenue = resultSet.getDouble("TotalRevenue");
                result.append("Category: ").append(categoryName)
                        .append(", Revenue: ").append(String.format("%.2f", totalRevenue))
                        .append("\n");
            }
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
        return result.toString().isEmpty() ? "No high-revenue categories found." : result.toString();
    }


    public String getTopClient() throws SQLException {
        String result = "No data available.";
        String query = "SELECT C.Name AS ClientName, COUNT(O.OrderID) AS TotalOrders " +
                "FROM Orders O, Client C " +
                "WHERE O.Client_ID = C.ID " +
                "GROUP BY C.Name " +
                "HAVING COUNT(O.OrderID) = ( " +
                "    SELECT MAX(TotalOrders) " +
                "    FROM ( " +
                "        SELECT COUNT(O2.OrderID) AS TotalOrders " +
                "        FROM Orders O2, Client C2 " +
                "        WHERE O2.Client_ID = C2.ID " +
                "        GROUP BY C2.Name " +
                "    ) AS SubQuery " +
                ");";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String clientName = resultSet.getString("ClientName");
                int totalOrders = resultSet.getInt("TotalOrders");
                result = "Client: " + clientName + ", Orders: " + totalOrders;
            }
        } finally {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        }
        return result;
    }


    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
