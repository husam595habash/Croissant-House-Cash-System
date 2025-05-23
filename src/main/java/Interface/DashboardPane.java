package Interface;

import DataAccess.ClientDAO;
import DataAccess.EmployeeDAO;
import DataAccess.OrderDAO;
import DataAccess.SupplierDAO;
import DataStructure.LinkedList;
import DataStructure.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class DashboardPane {
    private GridPane gridPane;
    private static Label totalRevenueLabel;
    private static Label totalEmployeesLabel;
    private static Label totalClientsLabel;
    private static Label totalSuppliersLabel;
    private static Label highRevenueCategoriesLabel;
    private static Label topClientLabel;
    private BarChart<String, Number> barChart;
    private PieChart pieChart;
    private LineChart<String, Number> lineChart;


    private static ObservableList<PieChart.Data> pieChartData;
    private static XYChart.Series<String, Number> barChartSeries;
    private XYChart.Series<String, Number> lineChartSeries;

    public DashboardPane() {
        gridPane = new GridPane();
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        gridPane.setStyle("-fx-background-color: white");

        // Initialize observable data structures
        pieChartData = FXCollections.observableArrayList();
        barChartSeries = new XYChart.Series<>();
        lineChartSeries = new XYChart.Series<>();

        // Initialize charts
        barChart = createBarChart();
        pieChart = createPieChart();
        lineChart = createLineChart();

        // Add charts to the grid pane
        gridPane.add(barChart, 0, 0);
        gridPane.add(pieChart, 1, 0);
        gridPane.add(lineChart, 0, 1, 2, 1);

        VBox statsBox = createStatsBox();
        gridPane.add(statsBox, 2, 0, 1, 2); // Spans 2 rows

        gridPane.setHgap(50);
        gridPane.setAlignment(Pos.CENTER);


        // Load initial data
        refreshBarChart();
        refreshPieChart();
        refreshLineChart();
    }

    private VBox createStatsBox() {
        VBox statsBox = new VBox(30);
        statsBox.setPadding(new Insets(20));
        statsBox.setAlignment(Pos.TOP_CENTER);

        // Total Revenue
        totalRevenueLabel = createStatLabel("Total Revenue", "0");

        // Total Employees
        totalEmployeesLabel = createStatLabel("Number of Employees", "0");

        // Total Clients
        totalClientsLabel = createStatLabel("Number of Clients", "0");

        // Total Suppliers
        totalSuppliersLabel = createStatLabel("Number of Suppliers", "0");

        // High Revenue Categories
        highRevenueCategoriesLabel = createStatLabel("High Revenue Categories", "");

        // Top Client
        topClientLabel = createStatLabel("Top Client", "");

        // Add all labels to the VBox
        statsBox.getChildren().addAll(
                totalRevenueLabel,
                totalEmployeesLabel,
                totalClientsLabel,
                totalSuppliersLabel,
                highRevenueCategoriesLabel,
                topClientLabel
        );


        // Load statistics data
        refreshStats();

        return statsBox;
    }

    private Label createStatLabel(String title, String value) {
        Label label = new Label(title + "\n" + value);
        label.getStyleClass().add("statistics-square");
        label.setTextAlignment(TextAlignment.CENTER);
        return label;
    }

    public static void refreshStats() {
        try {
            // Total Revenue
            OrderDAO orderDAO = new OrderDAO();
            double totalRevenue = orderDAO.getTotalRevenue();
            totalRevenueLabel.setText("Total Revenue\n" + String.format("%.2f", totalRevenue));

            // Total Employees
            EmployeeDAO employeeDAO = new EmployeeDAO();
            int totalEmployees = employeeDAO.getTotalEmployees();
            totalEmployeesLabel.setText("Number of Employees\n" + totalEmployees);

            // Total Clients
            ClientDAO clientDAO = new ClientDAO();
            int totalClients = clientDAO.getTotalClients();
            totalClientsLabel.setText("Number of Clients\n" + totalClients);

            // Total Suppliers
            SupplierDAO supplierDAO = new SupplierDAO();
            int totalSuppliers = supplierDAO.getTotalSuppliers();
            totalSuppliersLabel.setText("Number of Suppliers\n" + totalSuppliers);

            // High Revenue Categories
            String highRevenueCategories = orderDAO.getHighRevenueCategories(); // Now a String
            highRevenueCategoriesLabel.setText("High Revenue Categories\n" + highRevenueCategories);

            // Top Client
            String topClient = orderDAO.getTopClient();
            topClientLabel.setText("Top Client\n" + topClient);

            orderDAO.closeConnection();
            employeeDAO.closeConnection();
            clientDAO.closeConnection();
            supplierDAO.closeConnection();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public GridPane getGridPane() {
        return gridPane;
    }

    private BarChart<String, Number> createBarChart() {
        BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        chart.setTitle("Revenue per Employee");
        barChartSeries.setName("Revenue");
        chart.getData().add(barChartSeries); // Add the series to the chart
        return chart;
    }

    private PieChart createPieChart() {
        PieChart chart = new PieChart(pieChartData); // Use the observable list
        chart.setTitle("Category Contribution to Revenue");
        return chart;
    }

    private LineChart<String, Number> createLineChart() {
        LineChart<String, Number> chart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        chart.setTitle("Daily Revenue Trends");
        lineChartSeries.setName("Daily Revenue");
        chart.getData().add(lineChartSeries); // Add the series to the chart
        return chart;
    }

    public static void refreshBarChart() {
        barChartSeries.getData().clear(); // Clear existing data

        try {
            OrderDAO orderDAO = new OrderDAO();
            LinkedList data = orderDAO.getRevenuePerEmployee();
            Node current = data.getFront();

            while (current != null) {
                String[] employeeData = (String[]) current.getElement();
                barChartSeries.getData().add(new XYChart.Data<>(employeeData[0], Double.parseDouble(employeeData[1])));
                current = current.getNext();
            }

            orderDAO.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void refreshPieChart() {
        pieChartData.clear(); // Clear existing data

        try {
            OrderDAO orderDAO = new OrderDAO();
            LinkedList data = orderDAO.getCategoryRevenue();
            Node current = data.getFront();

            while (current != null) {
                String[] categoryData = (String[]) current.getElement();
                pieChartData.add(new PieChart.Data(categoryData[0], Double.parseDouble(categoryData[1])));
                current = current.getNext();
            }

            orderDAO.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshLineChart() {
        lineChartSeries.getData().clear(); // Clear existing data

        try {
            OrderDAO orderDAO = new OrderDAO();
            LinkedList data = orderDAO.getDailyRevenue();
            Node current = data.getFront();

            while (current != null) {
                Object[] dailyData = (Object[]) current.getElement();
                lineChartSeries.getData().add(new XYChart.Data<>((String) dailyData[0], (Double) dailyData[1]));
                current = current.getNext();
            }

            orderDAO.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
