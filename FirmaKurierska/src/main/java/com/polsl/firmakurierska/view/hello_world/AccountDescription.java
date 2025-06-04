/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.polsl.firmakurierska.view.hello_world;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AccountDescription {

    public void show(List<String> data) {
        // Tworzymy osobne boksy dla każdego pola
        VBox imieBox = createCard("Imię:", data.get(0));
        VBox nazwiskoBox = createCard("Nazwisko:", data.get(1));
        VBox peselBox = createCard("PESEL:", data.get(2));
        VBox stanowiskoBox = createCard("Stanowisko:", data.get(3));
        String kategoriePrawaJazdy = "";

        for (int i = 4; i < data.size(); i++) {
            kategoriePrawaJazdy += data.get(i) + " ";
        }

        VBox prawoJazdyBox = createCard("Prawo jazdy:", kategoriePrawaJazdy);

        VBox allFields = new VBox(15, imieBox, nazwiskoBox, peselBox, stanowiskoBox, prawoJazdyBox);
        allFields.setPadding(new Insets(20));
        allFields.setAlignment(Pos.CENTER);

        // Tło główne
        BorderPane root = new BorderPane(allFields);
        root.setStyle("-fx-background-color: #f8f8f8;");
        BorderPane.setAlignment(allFields, Pos.CENTER);

        Scene scene = new Scene(root, 400, 400);

        Stage stage = new Stage();
        stage.setTitle("Szczegóły konta");
        stage.setScene(scene);
        stage.show();
    }

    // Tworzy osobną "kartę" z tłem, obramowaniem i odstępem
    private VBox createCard(String labelText, String valueText) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");

        Label value = new Label(valueText);

        VBox box = new VBox(5, label, value);
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
