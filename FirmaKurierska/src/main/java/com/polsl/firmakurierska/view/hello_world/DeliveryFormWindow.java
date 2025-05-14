/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.polsl.firmakurierska.view.hello_world;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.HBox;


public class DeliveryFormWindow {

    /**
     * Otwiera formularz dodawania nowej dostawy.
     * @param availablePackageIds lista dostępnych ID paczek, które można dołączyć
     */
    public void show(List<String> availablePackageIds) {
        // Pola podstawowe
        TextField idField = new TextField();
        DatePicker departurePicker = new DatePicker();
        DatePicker duePicker = new DatePicker();
        TextField pointAField = new TextField();
        TextField pointBField = new TextField();

        VBox idBox        = createInputCard("ID dostawy:", idField);
        VBox departureBox = createInputCard("Data wyruszenia:", departurePicker);
        VBox dueBox       = createInputCard("Termin:", duePicker);
        VBox pointABox    = createInputCard("Punkt A:", pointAField);
        VBox pointBBox    = createInputCard("Punkt B:", pointBField);

        // Lista dostępnych paczek (checkboxy)
        Label chooseLabel = new Label("Wybierz paczki do dostawy:");
        chooseLabel.setStyle("-fx-font-weight: bold;");
        VBox checkBoxContainer = new VBox(5);
        checkBoxContainer.setPadding(new Insets(5));
        for (String pid : availablePackageIds) {
            checkBoxContainer.getChildren().add(new CheckBox(pid));
        }
        ScrollPane packagesScroll = new ScrollPane(checkBoxContainer);
        packagesScroll.setFitToWidth(true);
        packagesScroll.setPrefHeight(150);

        // Przycisk zapisu
        Button saveBtn = new Button("Zapisz dostawę");
        saveBtn.setOnAction(e -> {
            // Zbieramy zaznaczone paczki
            List<String> selected = new ArrayList<>();
            for (var node : checkBoxContainer.getChildren()) {
                if (node instanceof CheckBox cb && cb.isSelected()) {
                    selected.add(cb.getText());
                }
            }

            // Wyświetlamy lub zapisujemy
            System.out.println("Nowa dostawa:");
            System.out.println("ID: " + idField.getText());
            System.out.println("Data wyruszenia: " + departurePicker.getValue());
            System.out.println("Termin: " + duePicker.getValue());
            System.out.println("Punkt A: " + pointAField.getText());
            System.out.println("Punkt B: " + pointBField.getText());
            System.out.println("Dołączone paczki: " + selected);

            // Zamknięcie okna
            ((Stage) saveBtn.getScene().getWindow()).close();
        });
        HBox btnBox = new HBox(saveBtn);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(10, 0, 0, 0));

        // Cały układ
        VBox container = new VBox(12,
            idBox,
            departureBox,
            dueBox,
            pointABox,
            pointBBox,
            chooseLabel,
            packagesScroll,
            btnBox
        );
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.TOP_CENTER);

        BorderPane root = new BorderPane(container);
        root.setStyle("-fx-background-color: #f8f8f8;");

        Stage stage = new Stage();
        stage.setTitle("Formularz dostawy");
        stage.setScene(new Scene(root, 400, 550));
        stage.show();
    }

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
