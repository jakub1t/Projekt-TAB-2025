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
     * Otwiera okno formularza raportu z dwiema datami, miejscem zapisu i typem formularza.
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

        // Typ formularza (jednokrotny wybór)
        String[] formTypes = {"Trasy", "Paczki", "Całkowity"};
        ToggleGroup formTypeGroup = new ToggleGroup();
        VBox formTypeBox = createRadioInputCard("Typ formularza:", formTypes, formTypeGroup);

        // Przycisk generuj
        Button generateBtn = new Button("Generuj raport");
        generateBtn.setOnAction(e -> {
            RadioButton selected = (RadioButton) formTypeGroup.getSelectedToggle();
            String chosenType = selected != null ? selected.getText() : "";

            System.out.println("Raport:");
            System.out.println("Data od: " + startDatePicker.getValue());
            System.out.println("Data do: " + endDatePicker.getValue());
            System.out.println("Miejsce zapisu: " + saveLocationField.getText());
            System.out.println("Typ formularza: " + chosenType);
            ((Stage) generateBtn.getScene().getWindow()).close();
        });
        HBox btnBox = new HBox(generateBtn);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(10, 0, 0, 0));

        // Kontener główny
        VBox container = new VBox(12,
            startDateBox,
            endDateBox,
            saveLocationBox,
            formTypeBox,
            btnBox
        );
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.TOP_CENTER);

        BorderPane root = new BorderPane(container);
        root.setStyle("-fx-background-color: #f8f8f8;");

        Stage stage = new Stage();
        stage.setTitle("Formularz raportu");
        stage.setScene(new Scene(root, 400, 440));
        stage.show();
    }

    /**
     * Tworzy kartę z etykietą i polem wejściowym (tekst, data, itp.).
     */
    private VBox createInputCard(String labelText, Control inputField) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");
        VBox box = new VBox(5, label, inputField);
        box.setPadding(new Insets(10));
        box.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #dddddd;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 1);"
        );
        return box;
    }

    /**
     * Tworzy kartę z etykietą oraz grupą radio buttonów do wyboru jednej opcji.
     */
    private VBox createRadioInputCard(String labelText, String[] options, ToggleGroup group) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");
        VBox box = new VBox(5, label);
        for (String opt : options) {
            RadioButton rb = new RadioButton(opt);
            rb.setToggleGroup(group);
            box.getChildren().add(rb);
        }
        box.setPadding(new Insets(10));
        box.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #dddddd;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 1);"
        );
        return box;
    }
}
