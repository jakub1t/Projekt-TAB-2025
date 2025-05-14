package com.polsl.firmakurierska.view.hello_world;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class RaportFormWindow {

    /**
     * Otwiera okno formularza raportu z dwoma datami oraz miejscem zapisu.
     */
    public void show() {
        // Data początkowa
        DatePicker startDatePicker = new DatePicker();
        VBox startDateBox = createInputCard("Data od:", startDatePicker);
        // Data końcowa
        DatePicker endDatePicker = new DatePicker();
        VBox endDateBox = createInputCard("Data do:", endDatePicker);
        // Miejsce zapisu
        TextField saveLocationField = new TextField();
        saveLocationField.setPromptText("np. /sciezka/do/raportu.txt");
        VBox saveLocationBox = createInputCard("Miejsce zapisu:", saveLocationField);

        // Przycisk generuj
        Button generateBtn = new Button("Generuj raport");
        generateBtn.setOnAction(e -> {
            System.out.println("Raport:");
            System.out.println("Data od: " + startDatePicker.getValue());
            System.out.println("Data do: " + endDatePicker.getValue());
            System.out.println("Miejsce zapisu: " + saveLocationField.getText());
            ((Stage)generateBtn.getScene().getWindow()).close();
        });
        HBox btnBox = new HBox(generateBtn);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(10, 0, 0, 0));

        // Kontener główny
        VBox container = new VBox(12,
            startDateBox,
            endDateBox,
            saveLocationBox,
            btnBox
        );
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.TOP_CENTER);

        BorderPane root = new BorderPane(container);
        root.setStyle("-fx-background-color: #f8f8f8;");

        Stage stage = new Stage();
        stage.setTitle("Formularz raportu");
        stage.setScene(new Scene(root, 400, 310));
        stage.show();
    }

    /**
     * Tworzy kartę z etykietą i polem wejściowym.
     */
    private VBox createInputCard(String labelText, Control inputField) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");
        VBox box = new VBox(5, label, inputField);
        box.setPadding(new Insets(10));
        box.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #dddddd;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 1);
        """);
        return box;
    }
}
