package Interface;

import DataAccess.EmployeeDAO;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.SQLException;

public class Login {
    private AnchorPane root;
    private Label incorrectLabel;
    private Button loginButton;
    private BorderPane borderPane = new BorderPane();
    private BorderPane employeeBorderPane = new BorderPane();
    private ManagerNavPane navPane = new ManagerNavPane();
    private NavPane employeeNavPane = new NavPane();
    private EmployeePane managerView = new EmployeePane();
    private CashTable employeeView = new CashTable();
    private CustomerPane customerView = new CustomerPane();
    private SupplierPane supplierView = new SupplierPane();
    private OrderHistoryPane orderHistoryPane = new OrderHistoryPane();
    private DashboardPane dashboardPane = new DashboardPane();

    public Login() {
        buildUI();
    }

    private void buildUI() {
        // Root AnchorPane
        root = new AnchorPane();
        root.setPrefSize(600, 400);
        root.setStyle("-fx-background-color: #e60000;");

        // Left-side AnchorPane
        AnchorPane leftPane = new AnchorPane();
        leftPane.setPrefSize(300, 400);
        leftPane.setStyle("-fx-background-color: white;");
        AnchorPane.setLeftAnchor(leftPane, 0.0);

        // Logo ImageView
        ImageView logoImageView = new ImageView(
                new Image(getClass().getResource("/com/example/database/croissant house logo.png").toExternalForm()));
        logoImageView.setFitHeight(282);
        logoImageView.setFitWidth(266);
        logoImageView.setLayoutX(16);
        logoImageView.setLayoutY(59);
        logoImageView.setPreserveRatio(true);
        leftPane.getChildren().add(logoImageView);

        // Right-side AnchorPane
        AnchorPane rightPane = new AnchorPane();
        rightPane.setLayoutX(309);
        rightPane.setLayoutY(10);
        rightPane.setPrefSize(264, 583);

        // User Login Text
        Text userLoginText = new Text("User Login");
        userLoginText.setFill(Color.WHITE);
        userLoginText.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        userLoginText.setLayoutX(65);
        userLoginText.setLayoutY(65);

        // Username Label
        Text usernameLabel = new Text("Username");
        usernameLabel.setFill(Color.WHITE);
        usernameLabel.setFont(Font.font("Arial" , FontWeight.BOLD, 16));
        usernameLabel.setLayoutX(32);
        usernameLabel.setLayoutY(110);

        // Username TextField
        TextField usernameTextField = new TextField();
        usernameTextField.setPromptText("Enter your username");
        usernameTextField.setLayoutX(32);
        usernameTextField.setLayoutY(121);
        usernameTextField.setPrefSize(232, 25);
        usernameTextField.getStyleClass().add("text-field-login");

        // User Icon
        ImageView userIcon = new ImageView(
                new Image(getClass().getResource("/com/example/database/user.png").toExternalForm()));
        userIcon.setFitHeight(28);
        userIcon.setFitWidth(28);
        userIcon.setLayoutX(238);
        userIcon.setLayoutY(116);
        userIcon.setPreserveRatio(true);

        // Password Label
        Text passwordLabel = new Text("Password");
        passwordLabel.setFill(Color.WHITE);
        passwordLabel.setFont(Font.font("Arial" , FontWeight.BOLD, 16));
        passwordLabel.setLayoutX(32);
        passwordLabel.setLayoutY(185);

        // PasswordField
        PasswordField passwordTextField = new PasswordField();
        passwordTextField.setPromptText("Enter your password");
        passwordTextField.setLayoutX(32);
        passwordTextField.setLayoutY(196);
        passwordTextField.setPrefSize(232, 25);
        passwordTextField.getStyleClass().add("text-field-login");

        // Key Icon
        ImageView keyIcon = new ImageView(
                new Image(getClass().getResource("/com/example/database/key.png").toExternalForm()));
        keyIcon.setFitHeight(28);
        keyIcon.setFitWidth(32);
        keyIcon.setLayoutX(238);
        keyIcon.setLayoutY(193);
        keyIcon.setPreserveRatio(true);

        // Login Button
        loginButton = new Button("Login");
        loginButton.setLayoutX(95);
        loginButton.setLayoutY(292);
        loginButton.setPrefSize(102, 34);
        loginButton.getStyleClass().add("third-button");
        loginButton.setOnAction(e -> handleLogin(usernameTextField, passwordTextField));

        // Lines
        Line line1 = new Line(-116, 0, 116, 0);
        line1.setLayoutX(148);
        line1.setLayoutY(149);
        line1.setStroke(Color.WHITE);
        line1.setStrokeWidth(3);

        Line line2 = new Line(-116, 0, 116, 0);
        line2.setLayoutX(148);
        line2.setLayoutY(224);
        line2.setStroke(Color.WHITE);
        line2.setStrokeWidth(3);

        // Incorrect Label
        incorrectLabel = new Label("Incorrect Username or Password");
        incorrectLabel.setLayoutX(31);
        incorrectLabel.setLayoutY(234);
        incorrectLabel.setPrefSize(235, 18);
        incorrectLabel.setTextFill(Color.WHITE);
        incorrectLabel.setVisible(false);

        // Add elements to rightPane
        rightPane.getChildren().addAll(
                userLoginText, usernameLabel, usernameTextField, userIcon,
                passwordLabel, passwordTextField, keyIcon, loginButton,
                line1, line2, incorrectLabel
        );

        // Add panes to root
        root.getChildren().addAll(leftPane, rightPane);
    }

    private void handleLogin(TextField usernameTextField, PasswordField passwordTextField) {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            incorrectLabel.setText("Username or Password is empty");
            incorrectLabel.setVisible(true);
            return;
        }

        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();

            // check if the user account exists or not
            if (employeeDAO.authenticate(username, password)) {
                incorrectLabel.setVisible(false);


                String role = employeeDAO.getUserRole(username);
                afterLogin(role , username);
            } else {
                incorrectLabel.setText("Incorrect Username or Password");
                incorrectLabel.setVisible(true);
            }
            employeeDAO.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            incorrectLabel.setText("Database error occurred!");
            incorrectLabel.setVisible(true);
        }
    }

    private void afterLogin(String role, String username) {
        Stage stage = new Stage();
        Scene scene;

        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();
            int employeeID = employeeDAO.getEmployeeID(username);
            employeeDAO.closeConnection();

            if (role.equalsIgnoreCase("manager")) {
                borderPane.setCenter(dashboardPane.getGridPane());
                borderPane.setTop(navPane.getTopPane());

                handleManagerNavigation();
                scene = new Scene(borderPane);
            } else {
                employeeBorderPane.setCenter(employeeView.getBorderPane());
                employeeBorderPane.setTop(employeeNavPane.getTopPane());

                // Pass employee ID to CashTable
                employeeView.setEmployeeID(employeeID);
                employeeView.getHandledByLabel().setText("Handled by : " + username);

                handleEmployeeNavigation();
                scene = new Scene(employeeBorderPane);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            incorrectLabel.setText("Database error occurred!");
            incorrectLabel.setVisible(true);
            return;
        }

        Stage currentStage = (Stage) loginButton.getScene().getWindow();
        currentStage.close();

        scene.getStylesheets().add(getClass().getResource("/com/example/database/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }


    private void handleManagerNavigation() {
        navPane.getAddEmployee().setOnAction(e -> borderPane.setCenter(managerView.getPane()));
        navPane.getDashboard().setOnAction(e -> {
            borderPane.setCenter(dashboardPane.getGridPane());
        });
        navPane.getAddSupplier().setOnAction(e -> borderPane.setCenter(supplierView.getPane()));
    }

    private void handleEmployeeNavigation() {
        employeeNavPane.getAddCustomerButton().setOnAction(e -> {
            employeeBorderPane.setCenter(customerView.getPane());
        });
        employeeNavPane.getOrderHistoryButton().setOnAction(e -> {
            employeeBorderPane.setCenter(orderHistoryPane.getPane());
        });
        employeeNavPane.getCashSystemButton().setOnAction(e -> employeeBorderPane.setCenter(employeeView.getBorderPane()));
    }




    public AnchorPane getPane() {
        return root;
    }


}
