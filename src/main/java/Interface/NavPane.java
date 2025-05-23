package Interface;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;


public class NavPane {
    private AnchorPane topPane;
    private Button addCustomerButton, orderHistoryButton,
            cashSystemButton;

    public NavPane() {
        // Initialize the AnchorPane
        topPane = new AnchorPane();
        topPane.setPrefSize(1200, 90);
        topPane.setStyle("-fx-background-color: white;");

        // Add Customer Button
        addCustomerButton = new Button("Manage Customer");
        addCustomerButton.setLayoutX(335);
        addCustomerButton.setLayoutY(30);
        addCustomerButton.getStyleClass().add("secondary-button");

        // Order History Button
        orderHistoryButton = new Button("Customer's Order History");
        orderHistoryButton.setLayoutX(600);
        orderHistoryButton.setLayoutY(30);
        orderHistoryButton.setPrefSize(320, 30);
        orderHistoryButton.getStyleClass().add("secondary-button");

        // Cash System Button
        cashSystemButton = new Button("Cash System");
        cashSystemButton.setLayoutX(150);
        cashSystemButton.setLayoutY(30);
        cashSystemButton.setPrefSize(200, 30);
        cashSystemButton.getStyleClass().add("secondary-button");



        // Logo Image
        ImageView logoImageView = new ImageView(new Image(getClass().getResource("/com/example/database/croissant house logo.png").toExternalForm()));
        logoImageView.setFitWidth(120);
        logoImageView.setFitHeight(100);
        logoImageView.setLayoutX(10);
        logoImageView.setPickOnBounds(true);
        logoImageView.setPreserveRatio(true);

        // Add components to the topPane
        topPane.getChildren().addAll(addCustomerButton, orderHistoryButton, cashSystemButton, logoImageView);
    }

    // Getters for NavPane components
    public AnchorPane getTopPane() {
        return topPane;
    }

    public Button getAddCustomerButton() {
        return addCustomerButton;
    }

    public Button getOrderHistoryButton() {
        return orderHistoryButton;
    }

    public Button getCashSystemButton() {
        return cashSystemButton;
    }


}
