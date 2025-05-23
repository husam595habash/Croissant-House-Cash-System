package Interface;
import ClassesOfTables.Customer;
import ClassesOfTables.Order;
import ClassesOfTables.OrderDetails;
import DataAccess.CustomerDAO;
import DataAccess.OrderDAO;
import DataStructure.LinkedList;
import DataStructure.Node;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class OrderHistoryPane {
    private BorderPane pane;
    private TextField searchField;
    private Button searchButton;
    private TableView<Order> tableView;
    private ObservableList<Order> orderList;

    public OrderHistoryPane() {
        pane = new BorderPane();
        pane.setPadding(new Insets(20));
        pane.setStyle("-fx-background-color: white;");

        // Top Section: Search Bar
        HBox searchBox = createSearchBox();
        pane.setTop(searchBox);

        // Center Section: TableView
        tableView = createTableView();
        pane.setCenter(tableView);

        // Bottom Section: Additional Controls (if needed)
        // Currently unused, but you can add buttons or info here in the future
    }

    private HBox createSearchBox() {
        Label searchLabel = new Label("Search by Customer ID or Name:");
        searchLabel.setFont(Font.font(14));

        searchField = new TextField();
        searchField.setPromptText("Enter Customer ID or Name");
        searchField.setPrefWidth(250);
        searchField.getStyleClass().add("text-field-Primary");

        searchButton = new Button("Search");
        searchButton.getStyleClass().add("primary-button");
        searchButton.setOnAction(e -> performSearch());

        // Enable pressing Enter to search
        searchField.setOnAction(e -> performSearch());

        HBox searchBox = new HBox(10, searchLabel, searchField, searchButton);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10, 0, 20, 0)); // Top, Right, Bottom, Left
        return searchBox;
    }

    private TableView<Order> createTableView() {
        TableView<Order> table = new TableView<>();
        table.setPrefSize(640, 600);
        table.setStyle("-fx-background-color: white; -fx-border-color: #cccccc;");

        // Define table columns
        TableColumn<Order, Integer> orderIdCol = new TableColumn<>("OrderID");
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderID"));
        orderIdCol.setPrefWidth(100);

        TableColumn<Order, Integer> employeeIdCol = new TableColumn<>("Employee_ID");
        employeeIdCol.setCellValueFactory(new PropertyValueFactory<>("employeeID"));
        employeeIdCol.setPrefWidth(100);

        TableColumn<Order, Integer> clientIdCol = new TableColumn<>("Client_ID");
        clientIdCol.setCellValueFactory(new PropertyValueFactory<>("clientID"));
        clientIdCol.setPrefWidth(100);

        TableColumn<Order, java.sql.Date> orderDateCol = new TableColumn<>("OrderDate");
        orderDateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        orderDateCol.setPrefWidth(120);

        TableColumn<Order, Double> totalAmountCol = new TableColumn<>("TotalAmount");
        totalAmountCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        totalAmountCol.setPrefWidth(120);

        // Print Bill Button Column
        TableColumn<Order, Void> printBillCol = new TableColumn<>("Print Bill");
        printBillCol.setPrefWidth(100);
        printBillCol.setStyle("-fx-alignment: CENTER;"); // Center align the column header

        printBillCol.setCellFactory(col -> new TableCell<Order, Void>() {
            private final Button printButton = new Button("Print");

            {
                printButton.getStyleClass().add("primary-button");
                printButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    printBill(order);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(printButton);
                }
            }
        });

        table.getColumns().addAll(orderIdCol, employeeIdCol, clientIdCol, orderDateCol, totalAmountCol, printBillCol);

        // Set TableView properties
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No orders to display"));

        // Initialize data
        orderList = FXCollections.observableArrayList();
        table.setItems(orderList);

        // Add alternating row colors for better readability
        table.setRowFactory(tv -> new TableRow<Order>() {
            @Override
            protected void updateItem(Order item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setStyle("");
                } else {
                    if (getIndex() % 2 == 0) {
                        setStyle("-fx-background-color: #ffffff;");
                    } else {
                        setStyle("-fx-background-color: #f9f9f9;");
                    }
                }
            }
        });

        return table;
    }

    private void performSearch() {
        String query = searchField.getText().trim();

        if (query.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please enter a Customer ID or Name to search.");
            return;
        }

        try {
            CustomerDAO customerDAO = new CustomerDAO();
            LinkedList customers;

            int clientId = -1;
            if (query.matches("\\d+")) { // If query is all digits, treat as ID
                clientId = Integer.parseInt(query);
                if (!customerDAO.isCustomerExists(clientId)) {
                    showAlert(Alert.AlertType.ERROR, "Not Found", "No customer found with ID: " + clientId);
                    return;
                }
                customers = new LinkedList();
                Customer customer = customerDAO.getCustomerById(clientId);
                if (customer != null) {
                    customers.addLast(customer);
                }
            } else { // Treat as name
                customers = customerDAO.getCustomersByName(query);
                if (customers.getSize() == 0) {
                    showAlert(Alert.AlertType.ERROR, "Not Found", "No customer found with name: " + query);
                    return;
                }
            }

            customerDAO.closeConnection();

            // Now, for each customer, fetch their orders
            OrderDAO orderDAO = new OrderDAO();
            orderList.clear();

            Node current = customers.getFront();
            while (current != null) {
                Customer customer = (Customer) current.getElement();
                LinkedList orders = orderDAO.getOrdersByClientID(customer.getId());
                Node orderNode = orders.getFront();
                while (orderNode != null) {
                    Order order = (Order) orderNode.getElement();
                    orderList.add(order);
                    orderNode = orderNode.getNext();
                }
                current = current.getNext();
            }
            orderDAO.closeConnection();

            if (orderList.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Orders", "This customer has no orders.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while fetching data.");
        }
    }

    private void printBill(Order order) {
        try {
            // Define the file name and path
            String directoryPath = System.getProperty("user.home") + "/Desktop";
            String fileName = "Bill_Order_" + order.getOrderID() + ".pdf";
            String filePath = directoryPath + "/" + fileName;

            // Ensure the directory exists
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Initialize PDF writer and document
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Add company logo
            try {
                Image logo = new Image(ImageDataFactory.create(getClass().getResource("/com/example/database/croissant house logo.png").toExternalForm()));
                logo.setWidth(100);
                logo.setHeight(100);
                logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
                document.add(logo);
            } catch (Exception e) {
                System.out.println("Logo not found. Skipping logo in PDF.");
            }

            // Add header and order details
            document.add(new Paragraph("Croissant House")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.DARK_GRAY));
            document.add(new Paragraph("Bill for Order ID: " + order.getOrderID())
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY));
            document.add(new Paragraph(" ")); // Empty line

            // Add order details
            Table detailsTable = new Table(new float[]{1, 2});
            detailsTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
            detailsTable.addCell(new Cell().add(new Paragraph("Employee ID:")));
            detailsTable.addCell(new Cell().add(new Paragraph(String.valueOf(order.getEmployeeID()))));
            detailsTable.addCell(new Cell().add(new Paragraph("Client ID:")));
            detailsTable.addCell(new Cell().add(new Paragraph(String.valueOf(order.getClientID()))));
            detailsTable.addCell(new Cell().add(new Paragraph("Order Date:")));
            detailsTable.addCell(new Cell().add(new Paragraph(order.getOrderDate().toString())));
            detailsTable.addCell(new Cell().add(new Paragraph("Total Amount:")));
            detailsTable.addCell(new Cell().add(new Paragraph("$" + String.format("%.2f", order.getTotalAmount()))));
            document.add(detailsTable);

            // Add items
            OrderDAO orderDAO = new OrderDAO();
            LinkedList orderDetailsList = orderDAO.getOrderDetails(order.getOrderID());
            orderDAO.closeConnection();

            if (orderDetailsList.getSize() > 0) {
                document.add(new Paragraph("Items:")
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                        .setFontSize(14)
                        .setFontColor(ColorConstants.DARK_GRAY));

                Table itemsTable = new Table(new float[]{1, 3, 1, 1, 1});
                itemsTable.setWidth(400);
                itemsTable.setMarginTop(10);
                itemsTable.setMarginBottom(20);
                itemsTable.setHorizontalAlignment(HorizontalAlignment.CENTER);

                // Add table headers
                itemsTable.addHeaderCell(new Cell().add(new Paragraph("Item ID").setBold()));
                itemsTable.addHeaderCell(new Cell().add(new Paragraph("Item Name").setBold()));
                itemsTable.addHeaderCell(new Cell().add(new Paragraph("Category").setBold()));
                itemsTable.addHeaderCell(new Cell().add(new Paragraph("Quantity").setBold()));
                itemsTable.addHeaderCell(new Cell().add(new Paragraph("Price").setBold()));

                // Add items
                boolean alternate = false;
                Node current = orderDetailsList.getFront();
                while (current != null) {
                    OrderDetails od = (OrderDetails) current.getElement();
                    addRowToTable(itemsTable, od, alternate);
                    alternate = !alternate;
                    current = current.getNext();
                }
                document.add(itemsTable);
            } else {
                document.add(new Paragraph("No items found for this order.")
                        .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE))
                        .setFontSize(12)
                        .setFontColor(ColorConstants.GRAY));
            }

            // Footer
            document.add(new Paragraph("Thank you for your purchase!")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE))
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY));

            // Close the document
            document.close();

            // Open the PDF
            File pdfFile = new File(filePath);
            if (pdfFile.exists()) {
                Desktop.getDesktop().open(pdfFile);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "The PDF bill could not be created.");
            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while generating the PDF.");
        }
    }

    private void addRowToTable(Table table, OrderDetails od, boolean alternate) {
        Color backgroundColor = alternate ? ColorConstants.LIGHT_GRAY : ColorConstants.WHITE;

        table.addCell(new Cell().add(new Paragraph(String.valueOf(od.getItemId()))).setBackgroundColor(backgroundColor));
        table.addCell(new Cell().add(new Paragraph(od.getItemName())).setBackgroundColor(backgroundColor));
        table.addCell(new Cell().add(new Paragraph(od.getCategoryName())).setBackgroundColor(backgroundColor));
        table.addCell(new Cell().add(new Paragraph(String.valueOf(od.getQuantity()))).setBackgroundColor(backgroundColor));
        table.addCell(new Cell().add(new Paragraph("$" + String.format("%.2f", od.getPricePerUnit()))).setBackgroundColor(backgroundColor));
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public BorderPane getPane() {
        return pane;
    }
}