package com.polsl.firmakurierska.view.hello_world;

import javafx.application.Application;
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

public class WorkerWindow extends Application {

    /**
     * Pokazuje okno dla pracownika z listą tras (dostaw).
     */
    @Override
    public void start(Stage stage) {
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
                "2025-04-17",
                "12:00",
                "Magazyn A", "Magazyn B",
                List.of("Paczka 1", "Paczka 2")
            ));

            HBox h = new HBox(10, delBtn, cb);
            h.setAlignment(Pos.CENTER_LEFT);
            delContainer.getChildren().add(h);
        }

        ScrollPane delScroll = new ScrollPane(delContainer);
        delScroll.setFitToWidth(true);
        delScroll.setPrefHeight(400);

        VBox colDel = new VBox(10, delLabel, delScroll);
        colDel.setPadding(new Insets(10));
        colDel.setStyle("-fx-background-color: #eaeaea;");
        colDel.setPrefWidth(400);

        HBox root = new HBox(colDel);
        root.setPadding(new Insets(15));

        Scene scene = new Scene(root, 270, 300);
        stage.setTitle("Worker Panel - Dostawy");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
