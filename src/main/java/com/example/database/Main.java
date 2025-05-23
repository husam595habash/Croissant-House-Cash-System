package com.example.database;

import Interface.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    private Login login = new Login();

    @Override
    public void start(Stage stage) throws IOException {

        Scene scene = new Scene(login.getPane() , 600 , 400);
        stage.setTitle("Hello!");
        scene.getStylesheets().add(getClass().getResource("/com/example/database/style.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}