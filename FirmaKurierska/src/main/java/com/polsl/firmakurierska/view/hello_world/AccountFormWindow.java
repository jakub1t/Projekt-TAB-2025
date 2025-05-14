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

public class AccountFormWindow {

    public void show() {
        // Pola do wpisywania danych
        TextField imieField = new TextField();
        TextField nazwiskoField = new TextField();
        TextField peselField = new TextField();
        TextField stanowiskoField = new TextField();
        TextField prawoJazdyField = new TextField();
        PasswordField hasloField = new PasswordField();

        VBox imieBox = createInputCard("Imię:", imieField);
        VBox nazwiskoBox = createInputCard("Nazwisko:", nazwiskoField);
        VBox peselBox = createInputCard("PESEL:", peselField);
        VBox stanowiskoBox = createInputCard("Stanowisko:", stanowiskoField);
        VBox prawoJazdyBox = createInputCard("Prawo jazdy:", prawoJazdyField);
        VBox hasloBox = createInputCard("Hasło:", hasloField);

        Button dodajButton = new Button("Dodaj");
        dodajButton.setOnAction(e -> {
            // Wypisanie danych do terminala
            System.out.println("Dodano konto:");
            System.out.println("Imię: " + imieField.getText());
            System.out.println("Nazwisko: " + nazwiskoField.getText());
            System.out.println("PESEL: " + peselField.getText());
            System.out.println("Stanowisko: " + stanowiskoField.getText());
            System.out.println("Prawo jazdy: " + prawoJazdyField.getText());
            System.out.println("Hasło: " + hasloField.getText());
        });

        HBox dodajBox = new HBox(dodajButton);
        dodajBox.setAlignment(Pos.CENTER);

        VBox allFields = new VBox(15,
                imieBox, nazwiskoBox, peselBox, stanowiskoBox, prawoJazdyBox, hasloBox, dodajBox);
        allFields.setPadding(new Insets(20));
        allFields.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane(allFields);
        root.setStyle("-fx-background-color: #f8f8f8;");
        BorderPane.setAlignment(allFields, Pos.CENTER);

        Scene scene = new Scene(root, 400, 570);

        Stage stage = new Stage();
        stage.setTitle("Dodawanie konta");
        stage.setScene(scene);
        stage.show();
    }

    // Tworzy kartę z etykietą i polem wejściowym
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
