package com.polsl.firmakurierska.view.hello_world;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class WorkerWindow {

    /**
     * Pokazuje okno dla pracownika z listą pojazdów i dostaw.
     * Pojazdy: nazwy jako przyciski do podglądu szczegółów.
     * Dostawy: checkbox + nazwa jako przycisk do podglądu, przy zazn./odzn. wypisuje stan.
     */
    public void start(Stage stage) {
        // ===== POJAZDY =====
        Label vehLabel = new Label("Pojazdy:");
        vehLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        String[] vehicles = {"Pojazd A", "Pojazd B", "Pojazd C"};
        VBox vehContainer = new VBox(5);
        for (String v : vehicles) {
            Button vehBtn = new Button(v);
            vehBtn.setPrefWidth(200);
            vehBtn.setOnAction(e -> new VehicleDescription().show(
                v, "Ciężarowy", "Volvo", "FH16", "16 L", "ABC-1234"
            ));
            HBox h = new HBox(10, vehBtn);
            h.setAlignment(Pos.CENTER_LEFT);
            vehContainer.getChildren().add(h);
        }
        ScrollPane vehScroll = new ScrollPane(vehContainer);
        vehScroll.setFitToWidth(true);
        vehScroll.setPrefHeight(200);

        // ===== DOSTAWY =====
        Label delLabel = new Label("Dostawy:");
        delLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        String[] deliveries = {"Dostawa 1", "Dostawa 2", "Dostawa 3"};
        VBox delContainer = new VBox(5);
        for (String d : deliveries) {
            CheckBox cb = new CheckBox();
            cb.setOnAction(e -> {
                String status = cb.isSelected() ? "wykonana" : "nie wykonana";
                System.out.println(d + " " + status);
            });
            Button delBtn = new Button(d);
            delBtn.setPrefWidth(180);
            delBtn.setOnAction(e -> new DeliveryDescription().show(
                d,
                "Anna", "Nowak", "Samochód X",
                "2025-04-17", "2025-04-20",
                "Magazyn A", "Magazyn B",
                List.of("Paczka 1", "Paczka 2")
            ));
            HBox h = new HBox(10, delBtn, cb);
            h.setAlignment(Pos.CENTER_LEFT);
            delContainer.getChildren().add(h);
        }
        ScrollPane delScroll = new ScrollPane(delContainer);
        delScroll.setFitToWidth(true);
        delScroll.setPrefHeight(200);

        // ===== UKŁAD =====
        VBox colVeh = new VBox(10, vehLabel, vehScroll);
        colVeh.setPadding(new Insets(10));
        colVeh.setStyle("-fx-background-color: #f4f4f4;");
        colVeh.setPrefWidth(250);

        VBox colDel = new VBox(10, delLabel, delScroll);
        colDel.setPadding(new Insets(10));
        colDel.setStyle("-fx-background-color: #eaeaea;");
        colDel.setPrefWidth(250);

        HBox root = new HBox(20, colVeh, colDel);
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 520, 280);
        stage.setTitle("Worker Panel");
        stage.setScene(scene);
        stage.show();
    }
}
