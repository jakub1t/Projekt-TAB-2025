package com.polsl.firmakurierska.view.hello_world;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VehicleDescription {

    /**
     * Pokazuje okno ze szczegółami pojazdu:
     * – nazwa pojazdu
     * – typ pojazdu
     * – marka
     * – model
     * – pojemność
     * – numer rejestracyjny
     */
    public void show(String nazwa, String typ, String marka, String model, String pojemnosc, String numerRej) {
        // Karta z nazwą pojazdu
        VBox nazwaBox = createCard("Nazwa pojazdu:", nazwa);
        // Karta z typem pojazdu
        VBox typBox = createCard("Typ pojazdu:", typ);
        // Karta z marką pojazdu
        VBox markaBox = createCard("Marka:", marka);
        // Karta z modelem
        VBox modelBox = createCard("Model:", model);
        // Karta z pojemnością
        VBox pojemnoscBox = createCard("Pojemność:", pojemnosc);
        // Karta z numerem rejestracyjnym
        VBox numerBox = createCard("Numer rejestracyjny:", numerRej);

        // Wszystkie karty w pionie
        VBox allFields = new VBox(15,
            nazwaBox,
            typBox,
            markaBox,
            modelBox,
            pojemnoscBox,
            numerBox
        );
        allFields.setPadding(new Insets(20));
        allFields.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane(allFields);
        root.setStyle("-fx-background-color: #f8f8f8;");
        BorderPane.setAlignment(allFields, Pos.CENTER);

        Scene scene = new Scene(root, 400, 500);
        Stage stage = new Stage();
        stage.setTitle("Szczegóły pojazdu");
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
