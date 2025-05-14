/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.polsl.firmakurierska.view.hello_world;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class VehicleFormWindow {

    /**
     * Otwiera formularz dodawania/edycji pojazdu.
     * Po kliknięciu "Zapisz", wypisuje dane w konsoli i zamyka okno.
     */
    public void show() {
        TextField typField = new TextField();
        TextField markaField = new TextField();
        TextField modelField = new TextField();
        TextField pojemnoscField = new TextField();
        TextField numerRejField = new TextField();

        VBox typBox       = createInputCard("Typ pojazdu:", typField);
        VBox markaBox     = createInputCard("Marka:", markaField);
        VBox modelBox     = createInputCard("Model:", modelField);
        VBox pojemnoscBox = createInputCard("Pojemność:", pojemnoscField);
        VBox numerBox     = createInputCard("Numer rejestracyjny:", numerRejField);

        Button saveBtn = new Button("Zapisz pojazd");
        saveBtn.setOnAction(e -> {
            System.out.println("Dodano/edytowano pojazd:");
            System.out.println("Typ: " + typField.getText());
            System.out.println("Marka: " + markaField.getText());
            System.out.println("Model: " + modelField.getText());
            System.out.println("Pojemność: " + pojemnoscField.getText());
            System.out.println("Nr rej.: " + numerRejField.getText());
            // zamknięcie okna
            Stage window = (Stage) saveBtn.getScene().getWindow();
            window.close();
        });

        HBox btnBox = new HBox(saveBtn);
        btnBox.setAlignment(Pos.CENTER);

        VBox container = new VBox(15,
            typBox, markaBox, modelBox, pojemnoscBox, numerBox, btnBox
        );
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane(container);
        root.setStyle("-fx-background-color: #f8f8f8;");

        Stage stage = new Stage();
        stage.setTitle("Formularz pojazdu");
        stage.setScene(new Scene(root, 400, 500));
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
