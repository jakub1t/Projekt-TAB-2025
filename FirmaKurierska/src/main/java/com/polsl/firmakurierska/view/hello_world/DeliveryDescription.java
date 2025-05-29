package com.polsl.firmakurierska.view.hello_world;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class DeliveryDescription {

    /**
     * Pokazuje okno ze szczegółami dostawy:
     * – nazwa dostawy
     * – imię pracownika
     * – nazwisko pracownika
     * – nazwa auta
     * – data
     * – godzina
     * – punkt A
     * – punkt B
     * – lista dołączonych paczek (przewijana)
     */
    public void show(String nazwaDostawy,
                     String imiePracownika,
                     String nazwiskoPracownika,
                     String nazwaAuta,
                     String data,
                     String godzina,
                     String punktA,
                     String punktB,
                     List<String> attachedPackageIds) {

        // Karta z nazwą dostawy
        VBox nazwaBox = createCard("Nazwa dostawy:", nazwaDostawy);
        // Karta z imieniem pracownika
        VBox imieBox = createCard("Imię pracownika:", imiePracownika);
        // Karta z nazwiskiem pracownika
        VBox nazwiskoBox = createCard("Nazwisko pracownika:", nazwiskoPracownika);
        // Karta z nazwą auta
        VBox autoBox = createCard("Nazwa auta:", nazwaAuta);
        // Karta z datą
        VBox dateBox = createCard("Data:", data);
        // Karta z godziną
        VBox timeBox = createCard("Godzina:", godzina);
        // Karta z punktem A
        VBox pointABox = createCard("Punkt A:", punktA);
        // Karta z punktem B
        VBox pointBBox = createCard("Punkt B:", punktB);

        // Lista paczek w scrollu
        Label packagesLabel = new Label("Paczki w dostawie:");
        packagesLabel.setStyle("-fx-font-weight: bold;");
        VBox packagesList = new VBox(5);
        for (String pid : attachedPackageIds) {
            packagesList.getChildren().add(new Label(pid));
        }
        ScrollPane packagesScroll = new ScrollPane(packagesList);
        packagesScroll.setFitToWidth(true);
        packagesScroll.setPrefHeight(120);

        // Zbiór wszystkich pól
        VBox allFields = new VBox(15,
            nazwaBox,
            imieBox,
            nazwiskoBox,
            autoBox,
            dateBox,
            timeBox,
            pointABox,
            pointBBox,
            packagesLabel,
            packagesScroll
        );
        allFields.setPadding(new Insets(20));
        allFields.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane(allFields);
        root.setStyle("-fx-background-color: #f8f8f8;");
        BorderPane.setAlignment(allFields, Pos.CENTER);

        // Dostosowana wysokość sceny do nowej zawartości
        Scene scene = new Scene(root, 400, 800);
        Stage stage = new Stage();
        stage.setTitle("Szczegóły dostawy");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Tworzy prostą kartę z etykietą i wartością
     */
    private VBox createCard(String labelText, String valueText) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");
        Label value = new Label(valueText);

        VBox box = new VBox(5, label, value);
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
