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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.polsl.firmakurierska.controller.RequestController;
import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.model.Dostawa;
import com.polsl.firmakurierska.model.Pracownik;

public class WorkerWindow extends Application {

    private int loggedUserId = 0;
    private String loggedUserName = "Imię";
    private String loggedUserSurname = "Nazwisko";
    /**
     * Pokazuje okno dla pracownika z listą tras (dostaw).
     */
    @Override
    public void start(Stage stage) {
        // ===== DOSTAWY =====
        Label welcomeLabel = new Label("Witaj " + loggedUserName + " " + loggedUserSurname + "!");
        welcomeLabel.setStyle("-fx-font-size: 12px;");
        VBox welBox = new VBox(5, welcomeLabel);
        welBox.setAlignment(Pos.CENTER);

        Label delLabel = new Label("Moje dostawy:");
        delLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        List<Dostawa> dostawy = getDeliveries();

        VBox delContainer = new VBox(5);

        for (Dostawa dv : dostawy) {
            CheckBox cb = new CheckBox();
            cb.setOnAction(e -> {
                String status = cb.isSelected() ? "Wykonana" : "Nie wykonana";
                System.out.println(Integer.toString(dv.getIdDostawy()) + ": " + status);
            });

            Button dvBtn = new Button(
                Integer.toString(dv.getIdDostawy()) 
                + ": " + dv.getPunktA() 
                + " do " + dv.getPunktB());
            dvBtn.setPrefWidth(200);
            /*
            dvBtn.setOnAction(e -> new DeliveryDescription().show(
                Integer.toString(dv.getIdDostawy()),
                loggedUserName, loggedUserSurname, "Pojazd",
                dv.getTermin().toString(),
                dv.getDataWyruszenia().toString(),
                dv.getPunktA(), dv.getPunktB(),
                dv.getPaczki()
            )); */
            dvBtn.setOnAction(e -> new DeliveryDescription().open(
                dv.getIdDostawy(), loggedUserName, loggedUserSurname
            ));

            HBox h = new HBox(10, dvBtn, cb);
            h.setAlignment(Pos.CENTER_LEFT);
            delContainer.getChildren().add(h);
        }

        ScrollPane delScroll = new ScrollPane(delContainer);
        delScroll.setFitToWidth(true);
        delScroll.setPrefHeight(400);

        VBox colDel = new VBox(10, welBox, delLabel, delScroll);
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
        getMyName();
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
            ObjectMapper mapper = new ObjectMapper().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.registerModule(new JavaTimeModule());
            try {
                mojeDostawy = mapper.readValue(response, new TypeReference<List<Dostawa>>(){});
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }       
        return mojeDostawy;
    }

    private boolean getMyName() {
        String response = "";
        RequestController rq = new RequestController("/pracownik/" + Integer.toString(loggedUserId), 1);
        
        try {
            response = rq.sendPathReq();    
        } catch (BadRequestException e) {
            System.out.println("getMyName: " + e.getMessage());
            return false;
        }
        ObjectMapper mapper = new ObjectMapper().configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            Pracownik tmp = mapper.readValue(response, new TypeReference<Pracownik>(){});
            this.loggedUserName = tmp.getImie();
            this.loggedUserSurname = tmp.getNazwisko();
        } catch (IOException ex) {
            System.out.println("getMyName: " + ex.getMessage());
        }
        
        return true;
    }
}
