package Interface;

import ClassesOfTables.Supplier;
import DataAccess.SupplierDAO;
import DataStructure.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.SQLException;

public class SupplierPane {
    private GridPane pane;
    private TableView<Supplier> tableView;
    private TextField inputField;
    private ObservableList<Supplier> supplierObservableList;

    public SupplierPane() {
        pane = new GridPane();
        pane.setStyle("-fx-background-color: white;");
        supplierObservableList = FXCollections.observableArrayList();

        initializeTableView();
        setupRowSelectionHandler();
        loadSuppliers();
    }

    private void setupRowSelectionHandler() {
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) { // Single click
                Supplier selectedSupplier = tableView.getSelectionModel().getSelectedItem();
                if (selectedSupplier != null) {
                    inputField.setText(String.valueOf(selectedSupplier.getId())); // Display the ID in the input field
                }
            }
        });
    }


    private void initializeTableView() {
        tableView = new TableView<>();
        tableView.setPrefSize(500, 500);

        TableColumn<Supplier, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(60);

        TableColumn<Supplier, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(95);

        TableColumn<Supplier, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneColumn.setPrefWidth(120);

        TableColumn<Supplier, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailColumn.setPrefWidth(215);

        tableView.getColumns().addAll(idColumn, nameColumn, phoneColumn, emailColumn);
        tableView.setItems(supplierObservableList);

        HBox tableViewBox = new HBox(20, tableView);

        inputField = new TextField();
        inputField.setPrefSize(165, 39);
        inputField.setPromptText("Search by ID or Phone");
        inputField.textProperty().addListener((observable, oldValue, newValue) -> filterTableView(newValue));
        inputField.getStyleClass().add("text-field-Primary");

        Label label = new Label("Search:");
        label.setFont(Font.font("System", FontWeight.BOLD, 14));


        HBox inputBox = new HBox(20, label, inputField);
        inputBox.setAlignment(Pos.CENTER);

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(20.0);
        buttonBox.setAlignment(Pos.CENTER);

        Button removeButton = new Button("Remove");
        removeButton.setOnAction(e -> removeSupplier());
        removeButton.getStyleClass().add("primary-button");

        Button updateButton = new Button("Update");
        updateButton.setOnAction(e -> updateSupplier());
        updateButton.getStyleClass().add("primary-button");

        Button addButton = new Button("Add Supplier");
        addButton.setOnAction(e -> addSupplier());
        addButton.getStyleClass().add("primary-button");

        buttonBox.getChildren().addAll(removeButton, updateButton, addButton);

        VBox vBox = new VBox(20, inputBox, buttonBox);
        vBox.setPadding(new Insets(50, 0, 0, 50));

        pane.add(tableViewBox, 0, 0);
        pane.add(vBox, 1, 0);
        pane.setAlignment(Pos.CENTER);
    }

    private void loadSuppliers() {
        try {
            SupplierDAO supplierDAO = new SupplierDAO();
            LinkedList suppliers = supplierDAO.getAllSuppliers();

            Node current = suppliers.getFront();
            while (current != null) {
                supplierObservableList.add((Supplier) current.getElement());
                current = current.getNext();
            }
            supplierDAO.closeConnection();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not load supplier data. Please try again later.");
        }
    }

    private void filterTableView(String query) {
        ObservableList<Supplier> filteredList = FXCollections.observableArrayList();

        for (Supplier supplier : supplierObservableList) {
            if (String.valueOf(supplier.getId()).contains(query) ||
                    (supplier.getPhone() != null && supplier.getPhone().contains(query))) {
                filteredList.add(supplier);
            }
        }
        tableView.setItems(filteredList);
    }

    private void removeSupplier() {
        Supplier selectedSupplier = tableView.getSelectionModel().getSelectedItem();

        if (selectedSupplier != null) {
            Alert confirmationAlert = showAlert(Alert.AlertType.CONFIRMATION, "Confirm Deletion",
                    "Are you sure you want to delete this supplier?");

            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        SupplierDAO supplierDAO = new SupplierDAO();
                        supplierDAO.deleteSupplierById(selectedSupplier.getId());
                        supplierDAO.closeConnection();

                        supplierObservableList.remove(selectedSupplier);
                        tableView.setItems(supplierObservableList);

                        showAlert(Alert.AlertType.INFORMATION, "Supplier Deleted",
                                "Supplier successfully deleted!");

                        DashboardPane.refreshStats();
                        DashboardPane.refreshBarChart();
                        inputField.clear();
                    } catch (SQLException e) {
                        showAlert(Alert.AlertType.ERROR, "Database Error", "Could not delete supplier. Please try again later.");
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a supplier to remove.");
        }
    }

    private void addSupplier() {
        Stage addSupplierStage = new Stage();
        addSupplierStage.setTitle("Add New Supplier");

        // Create input fields
        TextField idField = new TextField();
        idField.setPromptText("ID");
        idField.getStyleClass().add("text-field-Primary");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.getStyleClass().add("text-field-Primary");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");
        phoneField.getStyleClass().add("text-field-Primary");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("text-field-Primary");

        Button addButton = new Button("Add");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(event -> {
            if (validateFields(idField, nameField, phoneField, emailField)) {
                try {
                    Supplier newSupplier = new Supplier(
                            Integer.parseInt(idField.getText()),
                            nameField.getText(),
                            phoneField.getText(),
                            emailField.getText()
                    );

                    SupplierDAO supplierDAO = new SupplierDAO();
                    supplierDAO.addSupplier(newSupplier);

                    supplierObservableList.add(newSupplier);
                    tableView.setItems(supplierObservableList);
                    supplierDAO.closeConnection();

                    showAlert(Alert.AlertType.INFORMATION, "Success", "Supplier added successfully!");
                    addSupplierStage.close();
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "ID must be a valid integer.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "All fields must be filled.");
            }
        });

        VBox layout = new VBox(10, idField, nameField, phoneField, emailField, addButton);
        layout.setPadding(new Insets(20));

        DashboardPane.refreshStats();
        DashboardPane.refreshBarChart();

        Scene scene = new Scene(layout, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/com/example/database/style.css").toExternalForm());
        addSupplierStage.setScene(scene);
        addSupplierStage.show();
    }

    private void updateSupplier() {
        Supplier selectedSupplier = tableView.getSelectionModel().getSelectedItem();

        if (selectedSupplier != null) {
            Stage updateStage = new Stage();
            updateStage.setTitle("Update Supplier");

            TextField nameField = new TextField(selectedSupplier.getName());
            nameField.getStyleClass().add("text-field-Primary");

            TextField phoneField = new TextField(selectedSupplier.getPhone());
            phoneField.getStyleClass().add("text-field-Primary");

            TextField emailField = new TextField(selectedSupplier.getEmail());
            emailField.getStyleClass().add("text-field-Primary");

            Button updateButton = new Button("Update");
            updateButton.getStyleClass().add("primary-button");
            updateButton.setOnAction(event -> {
                try {
                    selectedSupplier.setName(nameField.getText());
                    selectedSupplier.setPhone(phoneField.getText());
                    selectedSupplier.setEmail(emailField.getText());

                    SupplierDAO supplierDAO = new SupplierDAO();
                    supplierDAO.updateSupplier(selectedSupplier);
                    supplierDAO.closeConnection();

                    tableView.refresh();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Customer updated successfully!");
                    updateStage.close();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Update Error", "Could not update supplier.");
                }
            });

            VBox layout = new VBox(10, nameField, phoneField, emailField, updateButton);
            layout.setPadding(new Insets(20));

            Scene scene = new Scene(layout, 400, 300);
            scene.getStylesheets().add(getClass().getResource("/com/example/database/style.css").toExternalForm());
            updateStage.setScene(scene);
            updateStage.show();
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a supplier to update.");
        }
    }

    private boolean validateFields(TextField... fields) {
        for (TextField field : fields) {
            if (field.getText().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private Alert showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert;
    }

    public GridPane getPane() {
        return pane;
    }

    public TableView<Supplier> getTableView() {
        return tableView;
    }
}
