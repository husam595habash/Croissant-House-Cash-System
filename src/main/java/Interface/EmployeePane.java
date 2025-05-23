package Interface;

import ClassesOfTables.Employee;
import DataAccess.EmployeeDAO;
import DataStructure.LinkedList;
import DataStructure.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

public class EmployeePane {
    private GridPane pane;
    private TableView<Employee> tableView;
    private TextField inputField;
    private ObservableList<Employee> employeeObservableList;

    public EmployeePane() {
        pane = new GridPane();
        pane.setStyle("-fx-background-color: white;");
        employeeObservableList = FXCollections.observableArrayList();

        initializeTableView();
        setupRowSelectionHandler();
        loadEmployees();
    }

    private void initializeTableView() {
        tableView = new TableView<>();
        tableView.setPrefSize(760, 500);

        TableColumn<Employee, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(60);

        TableColumn<Employee, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(95);

        TableColumn<Employee, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailColumn.setPrefWidth(175);

        TableColumn<Employee, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneColumn.setPrefWidth(120);

        TableColumn<Employee, String> passwordColumn = new TableColumn<>("Password");
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        passwordColumn.setPrefWidth(120);

        TableColumn<Employee, Double> salaryColumn = new TableColumn<>("Salary");
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
        salaryColumn.setPrefWidth(90);

        TableColumn<Employee, Integer> managerIdColumn = new TableColumn<>("Manager ID");
        managerIdColumn.setCellValueFactory(new PropertyValueFactory<>("managerId"));
        managerIdColumn.setPrefWidth(90);

        tableView.getColumns().addAll(idColumn, nameColumn, emailColumn, phoneColumn, passwordColumn, salaryColumn, managerIdColumn);
        tableView.setItems(employeeObservableList);

        HBox tableViewBox = new HBox(20, tableView);

        inputField = new TextField();
        inputField.setPrefSize(165, 39);
        inputField.getStyleClass().add("text-field-Primary");

        inputField.textProperty().addListener((observable, oldValue, newValue) -> filterTableView(newValue));

        Label label = new Label("Search by ID or Name:");
        label.setPrefSize(200, 35);

        HBox inputBox = new HBox(20, label, inputField);
        inputBox.setAlignment(Pos.CENTER);

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button removeButton = new Button("Remove");
        removeButton.getStyleClass().add("primary-button");
        removeButton.setOnAction(e -> removeEmployee());

        Button addNewEmployeeButton = new Button("Add New Employee");
        addNewEmployeeButton.getStyleClass().add("primary-button2");
        addNewEmployeeButton.setOnAction(e -> addEmployee());

        Button updateEmployeeButton = new Button("Update");
        updateEmployeeButton.getStyleClass().add("primary-button");
        updateEmployeeButton.setOnAction(e -> updateEmployee());

        buttonBox.getChildren().addAll(removeButton, addNewEmployeeButton, updateEmployeeButton);

        VBox vBox = new VBox(20, inputBox, buttonBox);
        vBox.setPadding(new Insets(50, 0, 0, 50));

        pane.add(tableViewBox, 0, 0);
        pane.add(vBox, 1, 0);
        pane.setAlignment(Pos.CENTER);
    }


    private void loadEmployees() {
        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();
            LinkedList employees = employeeDAO.readEmployees();

            Node current = employees.getFront();
            while (current != null) {
                employeeObservableList.add((Employee) current.getElement());
                current = current.getNext();
            }
            employeeDAO.closeConnection();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not load employee data.");
        }
    }

    private void filterTableView(String newValue) {
        FilteredList<Employee> filteredData = new FilteredList<>(employeeObservableList, p -> true);

        filteredData.setPredicate(employee -> {
            if (newValue == null || newValue.isEmpty()) {
                return true; // Show all employees if query is empty
            }
            String lowerCaseFilter = newValue.toLowerCase();

            // Filter by ID or Name
            return String.valueOf(employee.getId()).contains(lowerCaseFilter) ||
                    (employee.getName() != null && employee.getName().toLowerCase().contains(lowerCaseFilter));
        });

        tableView.setItems(filteredData); // Set the filtered data in the TableView
    }


    private void removeEmployee() {
        Employee selectedEmployee = tableView.getSelectionModel().getSelectedItem();

        if (selectedEmployee != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Deletion");
            confirmationAlert.setHeaderText(null);
            confirmationAlert.setContentText("Are you sure you want to delete this employee?");
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        EmployeeDAO employeeDAO = new EmployeeDAO();
                        employeeDAO.deleteEmployeeById(selectedEmployee.getId());
                        employeeDAO.closeConnection();

                        employeeObservableList.remove(selectedEmployee);
                        tableView.setItems(employeeObservableList);

                        showAlert(Alert.AlertType.INFORMATION, "Success", "Employee successfully deleted!");
                        DashboardPane.refreshBarChart();
                        DashboardPane.refreshStats();
                        inputField.clear();
                    } catch (SQLException e) {
                        showAlert(Alert.AlertType.ERROR, "Database Error", "Could not delete employee.");
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an employee to remove.");
        }
    }

    private void addEmployee() {
        Stage addEmployeeStage = new Stage();
        addEmployeeStage.setTitle("Add New Employee");

        // Create input fields
        TextField idField = new TextField();
        idField.setPromptText("ID");
        idField.getStyleClass().add("text-field-Primary");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.getStyleClass().add("text-field-Primary");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("text-field-Primary");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("text-field-Primary");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");
        phoneField.getStyleClass().add("text-field-Primary");

        TextField salaryField = new TextField();
        salaryField.setPromptText("Salary");
        salaryField.getStyleClass().add("text-field-Primary");

        TextField managerIdField = new TextField();
        managerIdField.setPromptText("Manager ID");
        managerIdField.getStyleClass().add("text-field-Primary");

        // Create labels
        Label idLabel = new Label("ID:");
        Label nameLabel = new Label("Name:");
        Label emailLabel = new Label("Email:");
        Label passwordLabel = new Label("Password:");
        Label phoneLabel = new Label("Phone:");
        Label salaryLabel = new Label("Salary:");
        Label managerIdLabel = new Label("Manager ID:");

        // Arrange labels and fields in a GridPane
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(emailLabel, 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(passwordLabel, 0, 3);
        grid.add(passwordField, 1, 3);
        grid.add(phoneLabel, 0, 4);
        grid.add(phoneField, 1, 4);
        grid.add(salaryLabel, 0, 5);
        grid.add(salaryField, 1, 5);
        grid.add(managerIdLabel, 0, 6);
        grid.add(managerIdField, 1, 6);

        // Create buttons
        Button addButton = new Button("Add");
        addButton.getStyleClass().add("primary-button");

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("third-button");

        // Button actions
        addButton.setOnAction(event -> {
            try {
                // Validate fields
                if (!validateFields(idField, nameField, emailField, passwordField, phoneField, salaryField , managerIdField)) {
                    showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please fill in all fields.");
                    return;
                }

                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                String email = emailField.getText();
                String password = passwordField.getText();
                String phone = phoneField.getText();
                double salary = Double.parseDouble(salaryField.getText());
                Integer managerId = managerIdField.getText().isEmpty() ? null : Integer.parseInt(managerIdField.getText());

                // Validate ManagerID
                if (managerId != null) {
                    EmployeeDAO employeeDAO = new EmployeeDAO();
                    if (!employeeDAO.isManager(managerId)) {
                        showAlert(Alert.AlertType.WARNING, "Invalid Input", "Manager ID does not belong to a manager.");
                        employeeDAO.closeConnection();
                        return;
                    }
                    employeeDAO.closeConnection();
                }

                Employee newEmployee = new Employee(id, name, email, password, phone, salary, managerId);

                // Add employee to the database
                EmployeeDAO employeeDAO = new EmployeeDAO();
                employeeDAO.addEmployee(newEmployee);
                employeeDAO.closeConnection();

                // Update UI
                employeeObservableList.add(newEmployee);
                tableView.setItems(employeeObservableList);

                DashboardPane.refreshStats();
                DashboardPane.refreshBarChart();


                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee added successfully!");
                addEmployeeStage.close();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter valid numeric values for ID, Salary, and Manager ID.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Error adding employee: " + e.getMessage());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Unexpected Error", "An unexpected error occurred: " + e.getMessage());
            }
        });

        cancelButton.setOnAction(event -> addEmployeeStage.close());

        // Arrange buttons in an HBox
        HBox buttonBox = new HBox(10, addButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Arrange everything in a VBox
        VBox layout = new VBox(10, grid, buttonBox);
        layout.setStyle("-fx-background-color: white;");
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 400, 400);
        scene.getStylesheets().add(getClass().getResource("/com/example/database/style.css").toExternalForm());
        addEmployeeStage.setScene(scene);
        addEmployeeStage.show();
    }




    private void updateEmployee() {
        Employee selectedEmployee = tableView.getSelectionModel().getSelectedItem();

        if (selectedEmployee == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an employee to update.");
            return;
        }

        Stage updateEmployeeStage = new Stage();
        updateEmployeeStage.setTitle("Update Employee");

        // Pre-fill input fields
        TextField idField = new TextField(String.valueOf(selectedEmployee.getId()));
        idField.setEditable(false);
        idField.getStyleClass().add("text-field-Primary");

        TextField nameField = new TextField(selectedEmployee.getName());
        nameField.getStyleClass().add("text-field-Primary");

        TextField emailField = new TextField(selectedEmployee.getEmail());
        emailField.getStyleClass().add("text-field-Primary");

        PasswordField passwordField = new PasswordField();
        passwordField.setText(selectedEmployee.getPassword()); // Set current password
        passwordField.getStyleClass().add("text-field-Primary");

        TextField phoneField = new TextField(selectedEmployee.getPhone());
        phoneField.getStyleClass().add("text-field-Primary");

        TextField salaryField = new TextField(String.valueOf(selectedEmployee.getSalary()));
        salaryField.getStyleClass().add("text-field-Primary");

        TextField managerIdField = new TextField(
                selectedEmployee.getManagerId() == null ? "" : String.valueOf(selectedEmployee.getManagerId())
        );
        managerIdField.getStyleClass().add("text-field-Primary");

        // Create labels and arrange them in a grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.add(new Label("ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Password:"), 0, 3);
        grid.add(passwordField, 1, 3);
        grid.add(new Label("Phone:"), 0, 4);
        grid.add(phoneField, 1, 4);
        grid.add(new Label("Salary:"), 0, 5);
        grid.add(salaryField, 1, 5);
        grid.add(new Label("Manager ID:"), 0, 6);
        grid.add(managerIdField, 1, 6);

        Button updateButton = new Button("Update");
        updateButton.getStyleClass().add("primary-button");

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("third-button");

        updateButton.setOnAction(event -> {
            try {
                // Validate fields
                if (!validateFields(nameField, emailField, passwordField, phoneField, salaryField)) {
                    showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please fill in all fields.");
                    return;
                }

                String name = nameField.getText();
                String email = emailField.getText();
                String password = passwordField.getText();
                String phone = phoneField.getText();
                double salary = Double.parseDouble(salaryField.getText());
                Integer managerId = managerIdField.getText().isEmpty() ? null : Integer.parseInt(managerIdField.getText());

                // Validate ManagerID
                if (managerId != null) {
                    EmployeeDAO employeeDAO = new EmployeeDAO();
                    if (!employeeDAO.isManager(managerId)) {
                        showAlert(Alert.AlertType.WARNING, "Invalid Input", "Manager ID does not belong to a manager.");
                        employeeDAO.closeConnection();
                        return;
                    }
                    employeeDAO.closeConnection();
                }

                // Update employee object
                selectedEmployee.setName(name);
                selectedEmployee.setEmail(email);
                selectedEmployee.setPassword(password);
                selectedEmployee.setPhone(phone);
                selectedEmployee.setSalary(salary);
                selectedEmployee.setManagerId(managerId);

                // Update in the database
                EmployeeDAO employeeDAO = new EmployeeDAO();
                employeeDAO.updateEmployee(selectedEmployee);
                employeeDAO.closeConnection();

                // Refresh table view
                // Refresh table view
                tableView.refresh();
                DashboardPane.refreshStats();
                DashboardPane.refreshBarChart();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee updated successfully!");
                updateEmployeeStage.close();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter valid numeric values for Salary and Manager ID.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Error updating employee: " + e.getMessage());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Unexpected Error", "An unexpected error occurred: " + e.getMessage());
            }
        });

        cancelButton.setOnAction(event -> updateEmployeeStage.close());

        // Arrange buttons in an HBox
        HBox buttonBox = new HBox(10, updateButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10, grid, buttonBox);
        layout.setStyle("-fx-background-color: white;");
        layout.setPadding(new Insets(20));

        DashboardPane.refreshPieChart();
        DashboardPane.refreshBarChart();

        Scene scene = new Scene(layout, 400, 450);
        scene.getStylesheets().add(getClass().getResource("/com/example/database/style.css").toExternalForm());
        updateEmployeeStage.setScene(scene);
        updateEmployeeStage.show();
    }



    private boolean validateFields(TextField... fields) {
        for (TextField field : fields) {
            if (field.getText().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void setupRowSelectionHandler() {
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Employee selectedEmployee = tableView.getSelectionModel().getSelectedItem();
                if (selectedEmployee != null) {
                    inputField.setText(String.valueOf(selectedEmployee.getId()));
                }
            }
        });
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public GridPane getPane() {
        return pane;
    }

    public TableView<Employee> getTableView() {
        return tableView;
    }
}
