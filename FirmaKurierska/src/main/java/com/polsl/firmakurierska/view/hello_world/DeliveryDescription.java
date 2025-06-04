package com.polsl.firmakurierska.view.hello_world;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
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
import com.polsl.firmakurierska.model.Paczka;

public class DeliveryDescription {

    private int idDostawy = 0;
    private String pracImie = "Imie";
    private String pracNazw = "Nazwisko";
    private String dataStart = "0000-00-00";
    private String deadline = "9999-99-99";
    private String punktA = "Punkt A";
    private String punktB = "Punkt B";
    private List<Paczka> przypisanePaczki = new ArrayList<>();

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
    public void show() {

        // Karta z nazwą dostawy
        VBox nazwaBox = createCard("Numer dostawy:", Integer.toString(idDostawy));
        // Karta z imieniem pracownika
        VBox imieBox = createCard("Imię pracownika:", pracImie);
        // Karta z nazwiskiem pracownika
        VBox nazwiskoBox = createCard("Nazwisko pracownika:", pracNazw);
        // Karta z nazwą auta
        VBox autoBox = createCard("Nazwa auta:", "Rolvo");
        // Karta z datą
        VBox dateBox = createCard("Data wyruszenia:", dataStart);
        // Karta z godziną
        VBox timeBox = createCard("Termin:", deadline);
        // Karta z punktem A
        VBox pointABox = createCard("Punkt A:", punktA);
        // Karta z punktem B
        VBox pointBBox = createCard("Punkt B:", punktB);

        // Lista paczek w scrollu
        Label packagesLabel = new Label("Paczki w dostawie:");
        packagesLabel.setStyle("-fx-font-weight: bold;");
        VBox packagesList = new VBox(5);
        if (przypisanePaczki != null) {
            for (Paczka box : przypisanePaczki) {
                String pid = Integer.toString(box.getIdPaczki());
                packagesList.getChildren().add(new Label(pid));
            }
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

    public void open(Integer delivID, String assignedUserName, String assignedUserSurname) {
        this.pracImie = assignedUserName;
        this.pracNazw = assignedUserSurname;
        if (fetchDelivData(delivID)) {
            this.show();
        } else {
            System.out.println("Failed to load delivery information!");
        }
    }

    private boolean fetchDelivData(Integer dID) {
        RequestController rq = new RequestController("/dostawa/" + Integer.toString(dID), 0);
        String response = "";
        try {
            response = rq.sendPathReq();    
        } catch (BadRequestException e) {
            System.out.println(e.getMessage());
            return false;
        } 
        ObjectMapper mapper = new ObjectMapper().configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule());
        try {
            Dostawa delivery = mapper.readValue(response, new TypeReference<Dostawa>(){});
            this.idDostawy = dID;
            
            this.dataStart = delivery.getDataWyruszenia().toString();
            this.deadline = delivery.getTermin().toString();
            this.punktA = delivery.getPunktA();
            this.punktB = delivery.getPunktB();

            this.przypisanePaczki = delivery.getPaczki();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
        return true;
    }
}
