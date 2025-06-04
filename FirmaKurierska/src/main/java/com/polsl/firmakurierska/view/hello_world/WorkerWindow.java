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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polsl.firmakurierska.controller.RequestController;
import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.model.Dostawa;

public class WorkerWindow extends Application {

    private int loggedUserId = 0;
    /**
     * Pokazuje okno dla pracownika z listą tras (dostaw).
     */
    @Override
    public void start(Stage stage) {
        // ===== DOSTAWY =====
        Label delLabel = new Label("Dostawy:");
        delLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        List<Dostawa> dostawy = getDeliveries();

        VBox delContainer = new VBox(5);
        /*
        String[] deliveries = {"Dostawa 1", "Dostawa 2", "Dostawa 3"};
         
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
        } */

        for (Dostawa dv : dostawy) {
            CheckBox cb = new CheckBox();
            cb.setOnAction(e -> {
                String status = cb.isSelected() ? "Wykonana" : "Nie wykonana";
            });

            Button dvBtn = new Button(dv.getPunktA() + " do " + dv.getPunktB());
            dvBtn.setPrefWidth(200);
            dvBtn.setOnAction(e -> new DeliveryDescription().show(
                Integer.toString(dv.getIdDostawy()),
                "", "", "",
                dv.getTermin().toString(),
                dv.getDataWyruszenia()
            ));
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

    public void open(int userId) {
        this.loggedUserId = userId;
        this.start(new Stage());
    }

    private List<Dostawa> getDeliveries() {
        List<Dostawa> mojeDostawy = new ArrayList<>();
        RequestController rq = new RequestController("/dostawa/pracownik/" + Integer.toString(loggedUserId), 0);
        String response = "";
        boolean goFurther = true;

        try {
            response = rq.sendPathReq();
        } catch (BadRequestException e) {
            System.out.println(e.getMessage());
            goFurther = false;
        }
        if (goFurther) {
            ObjectMapper mapper = new ObjectMapper();

            try {
                mojeDostawy = mapper.readValue(response, new TypeReference<List<Dostawa>>(){});
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }       
        return mojeDostawy;
    }
}
