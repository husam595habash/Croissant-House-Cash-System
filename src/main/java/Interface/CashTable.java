package Interface;

import ClassesOfTables.OrderDetails;
import DataAccess.OrderDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.*;

public class CashTable {
    private BorderPane borderPane;
    private Label handledByLabel;
    private ImageView exitButton;
    private Button coldDrinksButton ;
    private Button hotDrinksButton ;
    private Button snacksButton ;
    private Button sweetTreatsButton ;
    private Button cakeButton ;
    private Button frozenBoxesButton ;
    private Button softDrinksButton ;
    private Button croissantButton;
    private Button payButton;
    private static Label totalOrderAmountLabel;
    private AnchorPane menuPane;
    private ItemsInterface itemsInterface = new ItemsInterface();
    public static ObservableList<OrderDetails> list = FXCollections.observableArrayList();
    private OrderDAO orderDAO;
    private int EmployeeID;
    private TextField customerPhoneNumber;
    private Label orderIdLabel;

    public CashTable() {
        try {
            orderDAO = new OrderDAO();
        } catch (SQLException e) {
            System.out.println(e);
        }
        // Main BorderPane
        borderPane = new BorderPane();
        borderPane.setPrefSize(1500, 680);

        // Left Menu Pane
        menuPane = new AnchorPane();
        menuPane.setPrefSize(305, 645);
        menuPane.setStyle("-fx-background-color: white;");

        // Buttons for menu
        coldDrinksButton = createButton("Cold Drinks", 153, 175, 90, 42, "primary-button");
        hotDrinksButton = createButton("Hot Drinks", 54, 175, 90, 40, "primary-button");
        snacksButton = createButton("Snacks", 252, 175, 90, 42, "primary-button");
        sweetTreatsButton = createButton("Sweet Treats", 153, 245, 90, 42, "primary-button");
        cakeButton = createButton("Cake", 54, 245, 90, 40, "primary-button");
        frozenBoxesButton = createButton("Frozen Boxes", 252, 245, 90, 42, "primary-button");
        softDrinksButton = createButton("Soft Drinks", 54, 316, 90, 40, "primary-button");
        croissantButton = createButton("Croissants", 153, 316, 90, 40, "primary-button");
        payButton = createButton("Pay", 54, 461, 193, 26, "primary-button2");


        // TextField for customer phone number
        Label customerNumberLabel = new Label("Customer number:");
        customerNumberLabel.setLayoutX(55);
        customerNumberLabel.setLayoutY(411);
        customerNumberLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");

        customerPhoneNumber = new TextField();
        customerPhoneNumber.setPrefWidth(161);
        customerPhoneNumber.setLayoutX(172);
        customerPhoneNumber.setLayoutY(406);
        customerPhoneNumber.getStyleClass().add("text-field-Primary");
        Insets padding = new Insets(3, 15, 3, 15);
        customerPhoneNumber.setPadding(padding);

        menuPane.getChildren().addAll(coldDrinksButton, hotDrinksButton, snacksButton, sweetTreatsButton,
                cakeButton, frozenBoxesButton, softDrinksButton, croissantButton, payButton,
                customerNumberLabel, customerPhoneNumber);
        borderPane.setLeft(menuPane);

        // Center AnchorPane
        AnchorPane centerPane = new AnchorPane();
        centerPane.setPrefSize(970, 644);
        centerPane.setStyle("-fx-background-color: white;");

        // TableView for order details
        TableView tableViewOrderDetails = new TableView();
        tableViewOrderDetails.setLayoutX(121);
        tableViewOrderDetails.setLayoutY(112);
        tableViewOrderDetails.setPrefSize(634, 450);

        TableColumn<OrderDetails, String> itemNameColumn = new TableColumn<>("Item Name");
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        itemNameColumn.setPrefWidth(180.8);

        TableColumn<OrderDetails, String> itemCategoryColumn = new TableColumn<>("Item Category");
        itemCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        itemCategoryColumn.setPrefWidth(100.8);

        TableColumn<OrderDetails, Double> pricePerUnitColumn = new TableColumn<>("Price Per Unit");
        pricePerUnitColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerUnit"));
        pricePerUnitColumn.setPrefWidth(115.2);

        TableColumn<OrderDetails, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityColumn.setPrefWidth(120);

        TableColumn<OrderDetails, Double> totalPriceColumn = new TableColumn<>("Total Price");
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        totalPriceColumn.setPrefWidth(112.8);

        tableViewOrderDetails.setItems(list);
        tableViewOrderDetails.getColumns().addAll(itemNameColumn, itemCategoryColumn, pricePerUnitColumn, quantityColumn, totalPriceColumn);

        // Labels in center pane
        handledByLabel = createLabel("Handled by :", 774, 179, 208, 27, 16);
        totalOrderAmountLabel = createLabel("Total Amount :", 774, 256, 162, 27, 16);
        orderIdLabel = createLabel("Order ID: " + fetchNextOrderID(), 774, 218, 93, 27, 16);

        // Buttons for actions
        Button deleteOnePieceButton = createButton("Delete one piece", 121, 598, 190, 42, "primary-button2");
        Button deleteAllButton = createButton("Delete All", 270, 598, 90, 42, "primary-button");

        deleteAllButton.setOnAction(event -> {
            OrderDetails selectedItem = (OrderDetails) tableViewOrderDetails.getSelectionModel().getSelectedItem();

            if (selectedItem != null) {
                list.remove(selectedItem); // Remove the selected item from the list
                tableViewOrderDetails.refresh(); // Refresh the TableView to reflect changes
                calculateTotalAmount(); // Update the total amount label
            }
        });

        deleteOnePieceButton.setOnAction(event -> {
            OrderDetails selectedItem = (OrderDetails) tableViewOrderDetails.getSelectionModel().getSelectedItem();

            if (selectedItem != null) {
                int newQuantity = selectedItem.getQuantity() - 1;

                if (newQuantity > 0) {
                    selectedItem.setQuantity(newQuantity); // Update the quantity
                } else {
                    list.remove(selectedItem); // Remove the row if quantity becomes 0
                }

                tableViewOrderDetails.refresh(); // Refresh the TableView to reflect changes
                calculateTotalAmount(); // Update the total amount label
            } else {
                showAlert("Warning", "Please select an item to delete.", Alert.AlertType.WARNING);
            }
        });



        // Exit button with ImageView
        exitButton = new ImageView(new Image(getClass().getResource("/com/example/database/exit.png").toExternalForm()));
        exitButton.setFitHeight(55);
        exitButton.setFitWidth(70);
        exitButton.setLayoutX(1097);
        exitButton.setLayoutY(620);
        exitButton.setOnMouseClicked(event -> exitAction());


        centerPane.getChildren().addAll(tableViewOrderDetails, handledByLabel, totalOrderAmountLabel, orderIdLabel,
                deleteOnePieceButton, deleteAllButton, exitButton);

        borderPane.setCenter(centerPane);

        // Set button actions
        cakeButton.setOnAction(event -> handleCake());
        coldDrinksButton.setOnAction(event -> handleColdDrinks());
        hotDrinksButton.setOnAction(event -> handleHotDrinks());
        snacksButton.setOnAction(event -> handleSnacks());
        sweetTreatsButton.setOnAction(event -> handleSweetTreats());
        frozenBoxesButton.setOnAction(event -> handleFrozenBoxes());
        softDrinksButton.setOnAction(event -> handleSoftDrinks());
        croissantButton.setOnAction(event -> handleCroissant());
        payButton.setOnAction(event -> {
            processOrder();
            customerPhoneNumber.clear();
        });

    }

    private void processOrder() {
        try {
            String phoneNumber = customerPhoneNumber.getText().trim();
            if (phoneNumber.isEmpty()) {
                showAlert("Error", "Please enter a customer phone number.", Alert.AlertType.ERROR);
                return;
            }

            int clientID = orderDAO.fetchClientID(phoneNumber);
            if (clientID == -1) {
                showAlert("Error", "Client not found.", Alert.AlertType.ERROR);
                return;
            }

            if (list.isEmpty()) {
                showAlert("Error", "No items in the order.", Alert.AlertType.ERROR);
                return;
            }

            double totalAmount = calculateTotalAmount();
            Date date =  new Date(System.currentTimeMillis());
            orderDAO.addOrder(this.EmployeeID, clientID , date , totalAmount);

            int orderID = orderDAO.getNextOrderID() - 1;
            for (OrderDetails order : list) {
                orderDAO.addOrderDetails(orderID, order.getItemId(), order.getQuantity(), order.getPricePerUnit());
            }

            list.clear();
            orderIdLabel.setText("Order ID: " + fetchNextOrderID());
            calculateTotalAmount();
            showAlert("Success", "Order processed successfully.", Alert.AlertType.INFORMATION);

        } catch (SQLException e) {
            showAlert("Error", "Order processing failed: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    private int fetchNextOrderID() {
        try {
            return orderDAO.getNextOrderID();
        } catch (SQLException e) {
            System.out.println(e);
            return -1;
        }
    }

    private Button createButton(String text, double layoutX, double layoutY, double width, double height, String styleClass) {
        Button button = new Button(text);
        button.setLayoutX(layoutX);
        button.setLayoutY(layoutY);
        button.setPrefSize(width, height);
        button.getStyleClass().add(styleClass);
        return button;
    }

    private Label createLabel(String text, double layoutX, double layoutY, double width, double height, int fontSize) {
        Label label = new Label(text);
        label.setLayoutX(layoutX);
        label.setLayoutY(layoutY);
        label.setPrefSize(width, height);
        label.setFont(Font.font(fontSize));
        return label;
    }

    private void handleCake() {
        borderPane.setLeft(itemsInterface.createCakePane());
        itemsInterface.getCakebackButton().setOnAction(e -> borderPane.setLeft(menuPane));
        itemsInterface.setupCakeActions();
    }

    private void handleColdDrinks() {
        borderPane.setLeft(itemsInterface.createColdDrinksPane());
        itemsInterface.getColdDrinksbackButton().setOnAction(e -> borderPane.setLeft(menuPane));
        itemsInterface.setupColdDrinksActions();
    }

    private void handleHotDrinks() {
        borderPane.setLeft(itemsInterface.createHotDrinksPane());
        itemsInterface.getHotDrinksbackButton().setOnAction(e -> borderPane.setLeft(menuPane));
        itemsInterface.setupHotDrinksActions();
    }

    private void handleSnacks() {
        borderPane.setLeft(itemsInterface.createSnacksPane());
        itemsInterface.getSnacksbackButton().setOnAction(e -> borderPane.setLeft(menuPane));
        itemsInterface.setupSnacksActions();
    }

    private void handleSweetTreats() {
        borderPane.setLeft(itemsInterface.createSweetTreatsPane());
        itemsInterface.getSweetTreatsbackButton().setOnAction(e -> borderPane.setLeft(menuPane));
        itemsInterface.setupSweetTreatsActions();
    }

    private void handleFrozenBoxes() {
        borderPane.setLeft(itemsInterface.createFrozenBoxesPane());
        itemsInterface.getFrozenBoxesbackButton().setOnAction(e -> borderPane.setLeft(menuPane));
        itemsInterface.setupFrozenBoxesActions();
    }

    private void handleSoftDrinks() {
        borderPane.setLeft(itemsInterface.createSoftDrinksPane());
        itemsInterface.getSoftDrinksbackButton().setOnAction(e -> borderPane.setLeft(menuPane));
        itemsInterface.setupSoftDrinksActions();
    }

    private void handleCroissant() {
        borderPane.setLeft(itemsInterface.createCroissantPane());
        itemsInterface.getCroissantbackButton().setOnAction(e -> borderPane.setLeft(menuPane));
        itemsInterface.setupCroissantActions();
    }

    private void showAlert(String title, String message , Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Label getHandledByLabel() {
        return handledByLabel;
    }

    public BorderPane getBorderPane() {
        return borderPane;
    }

    private void exitAction() {
        // Create an alert of type CONFIRMATION
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Confirmation");
        alert.setHeaderText("Are you sure you want to exit?");

        // Customize button options
        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);

        // Show the dialog and wait for user response
        alert.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                // Close the current stage
                Stage currentStage = (Stage) exitButton.getScene().getWindow();
                currentStage.close();

                // Create a new Login instance and stage
                Login newLogin = new Login(); // Create a fresh instance of Login
                Stage loginStage = new Stage();
                Scene loginScene = new Scene(newLogin.getPane(), 600, 400);
                loginScene.getStylesheets().add(getClass().getResource("/com/example/database/style.css").toExternalForm());
                loginStage.setScene(loginScene);
                loginStage.show();
            }
        });
    }

    public static double calculateTotalAmount() {
        double total = 0;
        for (OrderDetails order : list) {
            total += order.getTotalPrice();
        }
        totalOrderAmountLabel.setText("Total Amount: " + String.format("%.2f", total));
        return total;
    }


    public void setEmployeeID(int employeeID) {
        EmployeeID = employeeID;
    }
}