package com.polsl.firmakurierska.view.hello_world;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
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
import com.polsl.firmakurierska.dto.DostawaDTO;
import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.model.Pracownik;

/**
 * Panel for Delivery Drivers
 */
public class DriverWindow extends Application {

    private int loggedUserId = 0;
    private String loggedUserName = "Imię";
    private String loggedUserSurname = "Nazwisko";

    // Selection options for filtering deliveries: 
    // 0 - all, 1 - completed, 2 - not completed, any other value - none
    private int filterDeliveriesOption = 0; 

    public void open(int userId) {
        this.loggedUserId = userId;
        getMyName();
        this.start(new Stage());
    }

    /**
     * Pokazuje okno dla pracownika z listą tras (dostaw).
     */
    @Override
    public void start(Stage stage) {
        // ===== DOSTAWY =====
        Label welcomeLabel = new Label("Witaj " + loggedUserName + " " + loggedUserSurname + "!");
        welcomeLabel.setStyle("-fx-font-size: 12px;");
        Button refreshBtn = new Button("Odśwież Dane");
        refreshBtn.setPrefWidth(200);

        ObservableList<String> options = 
            FXCollections.observableArrayList(
                "Wszystkie",
                "Wykonane",
                "Niewykonane"
            );
        final ComboBox<String> comboBox = new ComboBox<String>(options);

        Label delLabel = new Label("Moje dostawy:");
        delLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        List<DostawaDTO> dostawy = getDeliveries();

        VBox delContainer = new VBox(5);

        VBox welBox = new VBox(5, welcomeLabel, refreshBtn, comboBox);
        welBox.setAlignment(Pos.CENTER);
        
        comboBox.setOnAction(event -> {
            String selectedOption = new String(comboBox.getValue());

            if (selectedOption.equals("Wszystkie")) {
                this.filterDeliveriesOption = 0;
            } else if (selectedOption.equals("Wykonane")) {
                this.filterDeliveriesOption = 1;
            } else if (selectedOption.equals("Niewykonane")) {
                this.filterDeliveriesOption = 2;
            } else {
                this.filterDeliveriesOption = -1;
            }

            refreshAllData(delContainer, refreshBtn);
        });
        comboBox.setValue("Wszystkie");

        createDeliveriesButtons(dostawy, delContainer, refreshBtn);

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

    private List<DostawaDTO> getDeliveries() {
        List<DostawaDTO> mojeDostawy = new ArrayList<>();
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
                mojeDostawy = mapper.readValue(response, new TypeReference<List<DostawaDTO>>(){});
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }      

        // wyświetlanie przefiltrowanych dostaw 
        List<DostawaDTO> wybraneDostawy = filterDeliveries(mojeDostawy, filterDeliveriesOption);

        return wybraneDostawy;
    }

    private List<DostawaDTO> filterDeliveries(List<DostawaDTO> deliveries, int filterOption) {

        final List<DostawaDTO> filteredDeliveries = new ArrayList<>();

        switch (filterOption) {
            case 0:
                return deliveries;
            case 1:
                deliveries.forEach(delivery -> {
                    if (delivery.getStatus().equals("ZREALIZOWANA")){
                        filteredDeliveries.add(delivery);
                    } 
                });
                break;
            case 2:
                deliveries.forEach(delivery -> {
                    if (delivery.getStatus().equals("W_TRAKCIE")){
                        filteredDeliveries.add(delivery);
                    } 
                });
                break;
        
            default:
                System.out.println("Incorrect filter option...");
                break;
        }
        

        return filteredDeliveries;
    }

    private boolean updateDeliveryStatus(boolean wasCompleted, Integer delId) {
        String resp = "";
        String jsonData = "{\"status\": \"" + (wasCompleted ? "ZREALIZOWANA\"}" : "W TRAKCIE\"}");
        RequestController rq = new RequestController("/dostawa/update/" + Integer.toString(delId), 2);

        try {
            resp = rq.sendJsonReq(jsonData);
        } catch (BadRequestException ex) {
            System.out.println("updateDeliveryStatus: " + ex.getMessage());
            return false;
        }

        if (resp.contains("Dostawa o ID " + Integer.toString(delId) + " została zaktualizowana.")) {

            System.out.println("Status dostawy zmieniony na: " + (
                wasCompleted ? "ZREALIZOWANA" : "W TRAKCIE"
            ));
            return true;
        } else {
            System.out.println("Nie zaktualizowano dostawy o podanym ID!");
            return false;
        }
    }

    private boolean refreshAllData(VBox targetContainer, Button refreshBtn) {

        refreshBtn.setDisable(true);
        targetContainer.setDisable(true);

        List<DostawaDTO> deliveries = getDeliveries();

        targetContainer.getChildren().clear();

        createDeliveriesButtons(deliveries, targetContainer, refreshBtn);

        targetContainer.setDisable(false);
        refreshBtn.setDisable(false);

        return true;
    }

    // Helper method to create delivery buttons
    private void createDeliveriesButtons(List<DostawaDTO> deliveries, VBox targetContainer, Button refreshBtn) {

        for (DostawaDTO dv : deliveries) {
            CheckBox cb = new CheckBox();
            cb.setSelected(dv.getStatus().contains("ZREALIZOWANA") ? true : false);
            cb.setOnAction(e -> {
                updateDeliveryStatus(cb.isSelected(), dv.getIdDostawy());
            });

            Button dvBtn = new Button(
                Integer.toString(dv.getIdDostawy()) 
                + ": " + dv.getPunktA() 
                + " do " + dv.getPunktB());

            dvBtn.setPrefWidth(200);

            dvBtn.setOnAction(e -> new DeliveryDescription().open(
                dv.getIdDostawy(), loggedUserName, loggedUserSurname
            ));

            HBox h = new HBox(10, dvBtn, cb);
            h.setAlignment(Pos.CENTER_LEFT);
            targetContainer.getChildren().add(h);

            refreshBtn.setOnAction(e -> {
                refreshAllData(targetContainer, refreshBtn);
            });
        }
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
