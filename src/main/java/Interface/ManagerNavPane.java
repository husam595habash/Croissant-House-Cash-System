package Interface;


import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;


public class ManagerNavPane {
    private AnchorPane topPane;
    private Button addEmployee, dashboard , addSupplier;

    public ManagerNavPane() {
        // Initialize the AnchorPane
        topPane = new AnchorPane();
        topPane.setPrefSize(1200, 90);
        topPane.setStyle("-fx-background-color: white;");

        // Add Customer Button
        addEmployee = new Button("Manage Employee");
        addEmployee.setLayoutX(350);
        addEmployee.setLayoutY(30);
        addEmployee.getStyleClass().add("secondary-button");

        // Order History Button
        dashboard = new Button("Dashboard");
        dashboard.setLayoutX(185);
        dashboard.setLayoutY(30);
        dashboard.getStyleClass().add("secondary-button");

        // Add Supplier Button
        addSupplier = new Button("Manage Supplier");
        addSupplier.setLayoutX(590);
        addSupplier.setLayoutY(30);
        addSupplier.getStyleClass().add("secondary-button");

        // Logo Image
        ImageView logoImageView = new ImageView(new Image(getClass().getResource("/com/example/database/croissant house logo.png").toExternalForm()));
        logoImageView.setFitWidth(120);
        logoImageView.setFitHeight(100);
        logoImageView.setLayoutX(10);
        logoImageView.setPickOnBounds(true);
        logoImageView.setPreserveRatio(true);

        // Add components to the topPane
        topPane.getChildren().addAll(addEmployee, dashboard, logoImageView , addSupplier);
    }


    // Getters for NavPane components
    public AnchorPane getTopPane() {
        return topPane;
    }

    public Button getAddEmployee() {
        return addEmployee;
    }

    public Button getDashboard() {
        return dashboard;
    }

    public Button getAddSupplier(){
        return addSupplier;
    }
}
