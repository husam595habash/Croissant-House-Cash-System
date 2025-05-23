package ClassesOfTables;

import java.sql.Date;

public class Order {
    private int orderID;
    private int employeeID;
    private int clientID;
    private Date orderDate;
    private double totalAmount;

    public Order() {}

    public Order(int orderID, int employeeID, int clientID, Date orderDate, double totalAmount) {
        this.orderID = orderID;
        this.employeeID = employeeID;
        this.clientID = clientID;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}