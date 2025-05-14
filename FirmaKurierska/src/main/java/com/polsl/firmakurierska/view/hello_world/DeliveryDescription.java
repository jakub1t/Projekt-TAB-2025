/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.polsl.firmakurierska.view.hello_world;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DeliveryDescription {

    /**
     * Pokazuje okno ze szczegółami dostawy:
     * – id_dostawy
     * – data wyruszenia
     * – termin
     * – punkt A
     * – punkt B
     * – lista dołączonych paczek
     */
    public void show(String idDostawy,
                     String dataWyruszenia,
                     String termin,
                     String punktA,
                     String punktB,
                     java.util.List<String> attachedPackageIds) {

        VBox idBox        = createCard("ID dostawy:", idDostawy);
        VBox departureBox = createCard("Data wyruszenia:", dataWyruszenia);
        VBox dueBox       = createCard("Termin:", termin);
        VBox pointABox    = createCard("Punkt A:", punktA);
        VBox pointBBox    = createCard("Punkt B:", punktB);

        VBox packagesBox = createCard(
            "Paczki w dostawie:",
            attachedPackageIds.isEmpty()
                ? "brak"
                : String.join(", ", attachedPackageIds)
        );

        VBox allFields = new VBox(15,
            idBox,
            departureBox,
            dueBox,
            pointABox,
            pointBBox,
            packagesBox
        );
        allFields.setPadding(new Insets(20));
        allFields.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane(allFields);
        root.setStyle("-fx-background-color: #f8f8f8;");
        BorderPane.setAlignment(allFields, Pos.CENTER);

        Scene scene = new Scene(root, 400, 450);
        Stage stage = new Stage();
        stage.setTitle("Szczegóły dostawy");
        stage.setScene(scene);
        stage.show();
    }

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

