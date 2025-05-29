package com.polsl.firmakurierska.view.hello_world;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class AccountFormWindow {

    public void show() {
        // Pola do wpisywania danych
        TextField imieField        = new TextField();
        TextField nazwiskoField    = new TextField();
        TextField peselField       = new TextField();
        TextField stanowiskoField  = new TextField();
        PasswordField hasloField   = new PasswordField();

        // Dostępne typy prawa jazdy
        String[] licenseTypes = {"A", "B", "C", "D", "T"};
        VBox prawoJazdyBox = createCheckboxInputCard("Prawo jazdy:", licenseTypes);

        VBox imieBox       = createInputCard("Imię:", imieField);
        VBox nazwiskoBox   = createInputCard("Nazwisko:", nazwiskoField);
        VBox peselBox      = createInputCard("PESEL:", peselField);
        VBox stanowiskoBox = createInputCard("Stanowisko:", stanowiskoField);
        VBox hasloBox      = createInputCard("Hasło:", hasloField);

        Button dodajButton = new Button("Dodaj");
        dodajButton.setOnAction(e -> {
            // Zbieranie wybranych typów prawa jazdy
            List<String> selectedLicenses = new ArrayList<>();
            for (Node node : prawoJazdyBox.getChildren()) {
                if (node instanceof CheckBox cb && cb.isSelected()) {
                    selectedLicenses.add(cb.getText());
                }
            }

            // Wypisanie danych do terminala
            System.out.println("Dodano konto:");
            System.out.println("Imię: " + imieField.getText());
            System.out.println("Nazwisko: " + nazwiskoField.getText());
            System.out.println("PESEL: " + peselField.getText());
            System.out.println("Stanowisko: " + stanowiskoField.getText());
            System.out.println("Prawo jazdy: " + selectedLicenses);
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

        Scene scene = new Scene(root, 400, 650);

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
        box.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #dddddd;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 1);"
        );
        return box;
    }

    // Tworzy kartę z etykietą oraz wielokrotną listą checkboxów
    private VBox createCheckboxInputCard(String labelText, String[] options) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");

        VBox box = new VBox(5);
        box.getChildren().add(label);

        for (String opt : options) {
            CheckBox cb = new CheckBox(opt);
            box.getChildren().add(cb);
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