package Interface;

import ClassesOfTables.OrderDetails;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.sql.*;

public class ItemsInterface {
    private Connection connection;

    // Sweet Treats
    private Button SweetTreatsbackButton, brownieButton, donutButton, englishCakeButton, cheeseCakeButton, cookieButton, trillicaCakeButton, muffinButton;

    // Soft Drinks
    private Button SoftDrinksbackButton, colaButton, cappyButton, spriteButton, xlButton, waterButton, sodaButton;

    // Snacks
    private Button SnacksbackButton, thymeFingersButton, brazicButton, aniseFingersButton, qarshalaButton, coffeeBiscuitsButton;

    // Hot Drinks
    private Button HotDrinksbackButton, americanoButton, espressoButton, coffeeLatteButton, coffeeMochaButton, spanishLatteButton, macchiatoButton,
            hotChocolateButton, frenchVanillaButton, chaiLatteButton, saltedCaramelButton, teaButton, nescafeButton;

    // Frozen Boxes
    private Button FrozenBoxesbackButton, cheeseBallsButton, shishbarakButton, kibbehButton, potatoBorekButton, pizzaRollsButton, cheeseBorekButton;

    // Cold Drinks
    private Button ColdDrinksbackButton, lemonButton, orangeButton, mojitoButton, iceChocolateButton, iceVanillaButton, iceCaramelButton, iceCoffeeButton,
            iceLatteButton, iceMochaButton, iceAmericanoButton, spanishLatteColdButton, iceTeaButton;

    // Croissant
    private Button CroissantbackButton, enterButton;
    private TextField croissantAmountField;

    // Cake
    private Button CakebackButton, smallCakeButton, largeCakeButton;

    public ItemsInterface(){
        try {
            String url = "jdbc:mysql://127.0.0.1:3306/croissanthouse";
            String username = "root";
            String password = "Trs@13081970";
            connection = DriverManager.getConnection(url, username, password);
        }catch (SQLException e){
            System.out.println(e);
        }
    }
    // Create Sweet Treats Pane
    public AnchorPane createSweetTreatsPane() {
        AnchorPane pane = new AnchorPane();
        pane.setPrefSize(305, 645);
        pane.setStyle("-fx-background-color: white;");

        brownieButton = createButton("Brownie", 153, 116, 90, 42, "primary-button");
        donutButton = createButton("Donut", 54, 116, 90, 42, "primary-button");
        englishCakeButton = createButton("English Cake", 252, 116, 90, 42, "primary-button");
        cheeseCakeButton = createButton("Cheese Cake", 153, 186, 90, 42, "primary-button");
        cookieButton = createButton("Cookie", 54, 186, 90, 42, "primary-button");
        trillicaCakeButton = createButton("Trillica Cake", 252, 186, 90, 42, "primary-button");
        muffinButton = createButton("Muffin", 54, 257, 90, 42, "primary-button");
        SweetTreatsbackButton = createButton("Back", 54, 54, 90, 42, "primary-button");


        pane.getChildren().addAll(brownieButton, donutButton, englishCakeButton, cheeseCakeButton, cookieButton, trillicaCakeButton, muffinButton, SweetTreatsbackButton);
        return pane;
    }


    private void addItemToOrder(String itemName) {
        String query = "SELECT I.ItemID, I.Name, I.Price, C.Name AS CategoryName " +
                "FROM Items I, Category C " +
                "WHERE I.CategoryID = C.CategoryID " +
                "AND I.Name = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, itemName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Retrieve item details from the ResultSet
                int itemId = resultSet.getInt("ItemID");
                String name = resultSet.getString("Name");
                double price = resultSet.getDouble("Price");
                String categoryName = resultSet.getString("CategoryName");

                // Check if the item already exists in the ObservableList
                OrderDetails existingOrder = null;
                for (OrderDetails orderDetails : CashTable.list) {
                    if (orderDetails.getItemId() == itemId) {
                        existingOrder = orderDetails;
                        break;
                    }
                }

                if (existingOrder != null) {
                    // If item exists, increment its quantity
                    existingOrder.setQuantity(existingOrder.getQuantity() + 1);

                    // Refresh the TableView by triggering a data change
                    int index = CashTable.list.indexOf(existingOrder);
                    CashTable.list.set(index, existingOrder);
                } else {
                    // If item does not exist, create a new OrderDetails object
                    OrderDetails newOrderDetails = new OrderDetails(itemId, name, categoryName, price, 1);
                    CashTable.list.add(newOrderDetails);
                }

                CashTable.calculateTotalAmount();
            }
        } catch (SQLException e) {
            System.out.println("Error fetching item: " + e.getMessage());
        }
    }

    // Create Soft Drinks Pane
    public AnchorPane createSoftDrinksPane() {
        AnchorPane pane = new AnchorPane();
        pane.setPrefSize(305.0, 536.0);
        pane.setStyle("-fx-background-color: white;");

        colaButton = createButton("Cola", 153, 116, 90, 42, "primary-button");
        cappyButton = createButton("Cappy", 54, 116, 90, 42, "primary-button");
        spriteButton = createButton("Sprite", 252, 116, 90, 42, "primary-button");
        xlButton = createButton("XL", 153, 186, 90, 42, "primary-button");
        waterButton = createButton("Water", 54, 186, 90, 42, "primary-button");
        sodaButton = createButton("Soda", 252, 186, 90, 42, "primary-button");
        SoftDrinksbackButton = createButton("Back", 54, 54, 90, 42, "primary-button");


        pane.getChildren().addAll(colaButton, cappyButton, spriteButton, xlButton, waterButton, sodaButton, SoftDrinksbackButton);
        return pane;
    }

    // Create Snacks Pane
    public AnchorPane createSnacksPane() {
        AnchorPane pane = new AnchorPane();
        pane.setPrefSize(305.0, 536.0);
        pane.setStyle("-fx-background-color: white;");

        thymeFingersButton = createButton("Thyme Fingers", 153, 116, 90, 42, "primary-button");
        brazicButton = createButton("Brazic", 54, 116, 90, 42, "primary-button");
        aniseFingersButton = createButton("Anise Fingers", 252, 116, 90, 42, "primary-button");
        qarshalaButton = createButton("Qarshala", 153, 186, 90, 42, "primary-button");
        coffeeBiscuitsButton = createButton("Coffee Biscuits", 54, 186, 90, 42, "primary-button");
        SnacksbackButton = createButton("Back", 54, 54, 90, 42, "primary-button");
        pane.getChildren().addAll(thymeFingersButton, brazicButton, aniseFingersButton, qarshalaButton, coffeeBiscuitsButton, SnacksbackButton);
        return pane;
    }

    // Create Hot Drinks Pane
    public AnchorPane createHotDrinksPane() {
        AnchorPane pane = new AnchorPane();
        pane.setPrefSize(305.0, 536.0);
        pane.setStyle("-fx-background-color: white;");

        americanoButton = createButton("Americano", 153, 116, 90, 42, "primary-button");
        americanoButton = createButton("Americano", 153, 116, 90, 42, "primary-button");
        espressoButton = createButton("Espresso", 54, 116, 90, 42, "primary-button");
        coffeeLatteButton = createButton("Coffee Latte", 252, 116, 90, 42, "primary-button");
        coffeeMochaButton = createButton("Coffee Mocha", 153, 186, 90, 42, "primary-button");
        spanishLatteButton = createButton("Spanish Latte", 54, 186, 90, 42, "primary-button");
        macchiatoButton = createButton("Macchiato", 252, 186, 90, 42, "primary-button");
        hotChocolateButton = createButton("Hot Chocolate", 54, 257.0, 90, 42, "primary-button");
        frenchVanillaButton = createButton("French Vanilla", 154, 257.0, 90, 42, "primary-button");
        chaiLatteButton = createButton("Chai Latte", 252, 257.0, 90, 42, "primary-button");
        saltedCaramelButton = createButton("Salted Caramel", 54, 319.0, 90, 42, "primary-button");
        teaButton = createButton("Tea", 154, 319.0, 90, 42, "primary-button");
        nescafeButton = createButton("Nescafe", 252, 319.0, 90, 42, "primary-button");
        HotDrinksbackButton = createButton("Back", 54, 54, 90, 42, "primary-button");
        pane.getChildren().addAll(americanoButton, espressoButton, coffeeLatteButton, coffeeMochaButton, spanishLatteButton, macchiatoButton, hotChocolateButton, frenchVanillaButton, chaiLatteButton, saltedCaramelButton, teaButton, nescafeButton, HotDrinksbackButton);
        return pane;
    }

    public AnchorPane createFrozenBoxesPane() {
        AnchorPane pane = new AnchorPane();
        pane.setPrefSize(305.0, 536.0);
        pane.setStyle("-fx-background-color: white;");

        cheeseBallsButton = createButton("Cheese Balls", 153, 116, 90, 42, "primary-button");
        shishbarakButton = createButton("Shishbarak", 54, 116, 90, 42, "primary-button");
        kibbehButton = createButton("Kibbeh", 252, 116, 90, 42, "primary-button");
        potatoBorekButton = createButton("Potato Borek", 153, 186, 90, 42, "primary-button");
        pizzaRollsButton = createButton("Pizza Rolls", 54, 186, 90, 42, "primary-button");
        cheeseBorekButton = createButton("Cheese Borek", 252, 186, 90, 42, "primary-button");
        FrozenBoxesbackButton = createButton("Back", 54, 54, 90, 42, "primary-button");

        pane.getChildren().addAll(cheeseBallsButton, shishbarakButton, kibbehButton, potatoBorekButton, pizzaRollsButton, cheeseBorekButton, FrozenBoxesbackButton);
        return pane;
    }

    // Create Cold Drinks Pane
    public AnchorPane createColdDrinksPane() {
        AnchorPane pane = new AnchorPane();
        pane.setPrefSize(305.0, 536.0);
        pane.setStyle("-fx-background-color: white;");

        lemonButton = createButton("Lemon", 153, 116, 90, 42, "primary-button");
        orangeButton = createButton("Orange", 54, 116, 90, 42, "primary-button");
        mojitoButton = createButton("Mojito", 252, 116, 90, 42, "primary-button");
        iceChocolateButton = createButton("Ice Chocolate", 153, 186, 90, 42, "primary-button");
        iceVanillaButton = createButton("Ice Vanilla", 54, 186, 90, 42, "primary-button");
        iceCaramelButton = createButton("Ice Caramel", 252, 186, 90, 42, "primary-button");
        iceCoffeeButton = createButton("Ice Coffee", 54, 257.0, 90, 42, "primary-button");
        iceLatteButton = createButton("Ice Latte", 154, 257.0, 90, 42, "primary-button");
        iceMochaButton = createButton("Ice Mocha", 252, 257.0, 90, 42, "primary-button");
        iceAmericanoButton = createButton("Ice Americano", 54, 319.0, 90, 42, "primary-button");
        spanishLatteColdButton = createButton("Spanish Latte", 154, 319.0, 90, 42, "primary-button");
        iceTeaButton = createButton("Ice Tea", 252, 319, 90, 42, "primary-button");
        ColdDrinksbackButton = createButton("Back", 54, 54, 90, 42, "primary-button");

        pane.getChildren().addAll(lemonButton, orangeButton, mojitoButton, iceChocolateButton, iceVanillaButton, iceCaramelButton, iceCoffeeButton, iceLatteButton, iceMochaButton, iceAmericanoButton, spanishLatteColdButton, iceTeaButton, ColdDrinksbackButton);
        return pane;
    }

    // Create Croissant Pane
    public AnchorPane createCroissantPane() {
        AnchorPane pane = new AnchorPane();
        pane.setPrefSize(343.0, 165.0);
        pane.setStyle("-fx-background-color: white;");

        Label amountLabel = new Label("Enter the amount:");
        amountLabel.setLayoutX(25);
        amountLabel.setLayoutY(190);
        amountLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        croissantAmountField = new TextField();
        croissantAmountField.setLayoutX(158);
        croissantAmountField.setLayoutY(185);
        croissantAmountField.setPrefWidth(161);
        croissantAmountField.setStyle("-fx-background-radius: 15; -fx-background-color: white; -fx-text-fill: black; -fx-font-size: 15; -fx-border-color: #e60000; -fx-border-radius: 15;");

        enterButton = createButton("Enter", 220, 158, 160.0, 40.0, "primary-button");
        CroissantbackButton = createButton("Back", 60, 158, 90, 40,"primary-button");
        pane.getChildren().addAll(amountLabel, croissantAmountField, enterButton, CroissantbackButton);
        return pane;
    }

    // Create Cake Pane
    public AnchorPane createCakePane() {
        AnchorPane pane = new AnchorPane();
        pane.setPrefSize(305, 535);
        pane.setStyle("-fx-background-color: white;");

        smallCakeButton = createButton("Small Cake", 153, 116, 90, 42, "primary-button");
        largeCakeButton = createButton("Large Cake", 54, 116, 90, 42, "primary-button");
        Button whiteButton = createButton("",252 , 116 , 90 , 42 , "primary-button" );
        whiteButton.setStyle("-fx-background-color: white");
        CakebackButton = createButton("Back", 54, 54, 90, 42, "primary-button");

        pane.getChildren().addAll(smallCakeButton, largeCakeButton, CakebackButton , whiteButton);
        return pane;
    }

    private Button createButton(String text, double layoutX, double layoutY, double width, double height , String styleClass) {
        Button button = new Button(text);
        button.setLayoutX(layoutX);
        button.setLayoutY(layoutY+100);
        button.setPrefSize(width, height);
        button.getStyleClass().add(styleClass);
        return button;
    }

    

    // Sweet Treats
    public void setupSweetTreatsActions() {
        brownieButton.setOnAction(event -> addItemToOrder("Brownie"));
        donutButton.setOnAction(event -> addItemToOrder("Donut"));
        englishCakeButton.setOnAction(event -> addItemToOrder("English Cake"));
        cheeseCakeButton.setOnAction(event -> addItemToOrder("Cheese Cake"));
        cookieButton.setOnAction(event -> addItemToOrder("Cookie"));
        trillicaCakeButton.setOnAction(event -> addItemToOrder("Trillica Cake"));
        muffinButton.setOnAction(event -> addItemToOrder("Muffin"));
    }

    // Soft Drinks
    public void setupSoftDrinksActions() {
        colaButton.setOnAction(event -> addItemToOrder("Cola"));
        cappyButton.setOnAction(event -> addItemToOrder("Cappy"));
        spriteButton.setOnAction(event -> addItemToOrder("Sprite"));
        xlButton.setOnAction(event -> addItemToOrder("XL"));
        waterButton.setOnAction(event -> addItemToOrder("Water"));
        sodaButton.setOnAction(event -> addItemToOrder("Soda"));
    }

    // Snacks
    public void setupSnacksActions() {
        thymeFingersButton.setOnAction(event -> addItemToOrder("Thyme Fingers"));
        brazicButton.setOnAction(event -> addItemToOrder("Brazic"));
        aniseFingersButton.setOnAction(event -> addItemToOrder("Anise Fingers"));
        qarshalaButton.setOnAction(event -> addItemToOrder("Qarshala"));
        coffeeBiscuitsButton.setOnAction(event -> addItemToOrder("Coffee Biscuits"));
    }

    // Hot Drinks
    public void setupHotDrinksActions() {
        americanoButton.setOnAction(event -> addItemToOrder("Americano"));
        espressoButton.setOnAction(event -> addItemToOrder("Espresso"));
        coffeeLatteButton.setOnAction(event -> addItemToOrder("Coffee Latte"));
        coffeeMochaButton.setOnAction(event -> addItemToOrder("Coffee Mocha"));
        spanishLatteButton.setOnAction(event -> addItemToOrder("Spanish Latte"));
        macchiatoButton.setOnAction(event -> addItemToOrder("Macchiato"));
        hotChocolateButton.setOnAction(event -> addItemToOrder("Hot Chocolate"));
        frenchVanillaButton.setOnAction(event -> addItemToOrder("French Vanilla"));
        chaiLatteButton.setOnAction(event -> addItemToOrder("Chai Latte"));
        saltedCaramelButton.setOnAction(event -> addItemToOrder("Salted Caramel"));
        teaButton.setOnAction(event -> addItemToOrder("Tea"));
        nescafeButton.setOnAction(event -> addItemToOrder("Nescafe"));
    }

    // Frozen Boxes
    public void setupFrozenBoxesActions() {
        cheeseBallsButton.setOnAction(event -> addItemToOrder("Cheese Balls"));
        shishbarakButton.setOnAction(event -> addItemToOrder("Shishbarak"));
        kibbehButton.setOnAction(event -> addItemToOrder("Kibbeh"));
        potatoBorekButton.setOnAction(event -> addItemToOrder("Potato Borek"));
        pizzaRollsButton.setOnAction(event -> addItemToOrder("Pizza Rolls"));
        cheeseBorekButton.setOnAction(event -> addItemToOrder("Cheese Borek"));
    }

    // Cold Drinks
    public void setupColdDrinksActions() {
        lemonButton.setOnAction(event -> addItemToOrder("Lemon"));
        orangeButton.setOnAction(event -> addItemToOrder("Orange"));
        mojitoButton.setOnAction(event -> addItemToOrder("Mojito"));
        iceChocolateButton.setOnAction(event -> addItemToOrder("Ice Chocolate"));
        iceVanillaButton.setOnAction(event -> addItemToOrder("Ice Vanilla"));
        iceCaramelButton.setOnAction(event -> addItemToOrder("Ice Caramel"));
        iceCoffeeButton.setOnAction(event -> addItemToOrder("Ice Coffee"));
        iceLatteButton.setOnAction(event -> addItemToOrder("Ice Latte"));
        iceMochaButton.setOnAction(event -> addItemToOrder("Ice Mocha"));
        iceAmericanoButton.setOnAction(event -> addItemToOrder("Ice Americano"));
        spanishLatteColdButton.setOnAction(event -> addItemToOrder("Spanish Latte Cold"));
        iceTeaButton.setOnAction(event -> addItemToOrder("Ice Tea"));
    }

    // Croissant
    public void setupCroissantActions() {
        enterButton.setOnAction(event -> {
            String priceText = croissantAmountField.getText();
            try {
                // Parse the price entered in the croissantAmountField
                double price = Double.parseDouble(priceText);

                if (price > 0) {
                    // Add a new Croissant item to the ObservableList
                    OrderDetails newCroissant = new OrderDetails(51, "Croissant", "Croissants", price, 1);
                    CashTable.list.add(newCroissant);

                    // Recalculate the total amount
                    CashTable.calculateTotalAmount();

                    // Clear the text field after adding the item
                    croissantAmountField.clear();
                } else {
                    // Show an error alert if the price is not positive
                    showErrorAlert("Price must be greater than 0.");
                }
            } catch (NumberFormatException e) {
                // Show an error alert if the input is not a valid number
                showErrorAlert("Please enter a valid price.");
            }
        });

    }
    
    // Cake
    public void setupCakeActions() {
        smallCakeButton.setOnAction(event -> addItemToOrder("Small Cake"));
        largeCakeButton.setOnAction(event -> addItemToOrder("Large Cake"));
    }
    

    private void showErrorAlert(String message) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }
    
    public Button getCakebackButton() {
        return CakebackButton;
    }

    public Button getCroissantbackButton() {
        return CroissantbackButton;
    }

    public Button getSweetTreatsbackButton() {
        return SweetTreatsbackButton;
    }

    public Button getFrozenBoxesbackButton() {
        return FrozenBoxesbackButton;
    }

    public Button getColdDrinksbackButton() {
        return ColdDrinksbackButton;
    }

    public Button getHotDrinksbackButton() {
        return HotDrinksbackButton;
    }

    public Button getSoftDrinksbackButton() {
        return SoftDrinksbackButton;
    }

    public Button getSnacksbackButton() {
        return SnacksbackButton;
    }
}
