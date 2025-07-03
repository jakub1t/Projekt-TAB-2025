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
import com.polsl.firmakurierska.dto.PaczkaDTO;
import com.polsl.firmakurierska.dto.ProduktDTO;
import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.model.Klient;

public class PackageDescription {

    private String imieKlienta = "Imie";
    private String nazwiskoKlienta = "Nazwisko";
    private String wagaPaczki = "[404]";

    private List<ProduktDTO> zawarteProdukty = null;
    
    /**
     * Pokazuje okno ze szczegółami paczki:
     * – imię i nazwisko klienta
     * – waga paczki
     * – przewijana lista produktów
     */
    public void show(PaczkaDTO paczka, Klient klient) {
        
        List<Integer> produktIds = new ArrayList<>();

        produktIds = paczka.getProduktIds();
        wagaPaczki = paczka.getWagaPaczki().toString();

        imieKlienta = klient.getImieK();
        nazwiskoKlienta = klient.getNazwiskoK();

        zawarteProdukty = getPackageProducts(produktIds);

        // Karta z imieniem klienta
        VBox imieBox = createCard("Imię klienta:", imieKlienta);

        // Karta z nazwiskiem klienta
        VBox nazwiskoBox = createCard("Nazwisko klienta:", nazwiskoKlienta);

        // Karta z wagą paczki
        VBox wagaBox = createCard("Waga paczki:", wagaPaczki + " kg");

        // Kontener na karty produktów
        VBox produktyList = new VBox(10);
        produktyList.setPadding(new Insets(5));
        for (ProduktDTO p : zawarteProdukty) {
            produktyList.getChildren().add(createProductCard(p));
        }

        ScrollPane produktyScroll = new ScrollPane(produktyList);
        produktyScroll.setFitToWidth(true);
        produktyScroll.setPrefHeight(300);

        // Układ wszystkich elementów
        VBox allFields = new VBox(15,
            imieBox,
            nazwiskoBox,
            wagaBox,
            produktyScroll
        );
        allFields.setPadding(new Insets(20));
        allFields.setAlignment(Pos.CENTER);

        // Główne tło
        BorderPane root = new BorderPane(allFields);
        root.setStyle("-fx-background-color: #f8f8f8;");
        BorderPane.setAlignment(allFields, Pos.CENTER);

        Scene scene = new Scene(root, 450, 600);
        Stage stage = new Stage();
        stage.setTitle("Szczegóły paczki");
        stage.setScene(scene);
        stage.show();
    }

    /** Tworzy prostą kartę z etykietą i wartością */
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

    /** Tworzy kartę z danymi pojedynczego produktu */
    private VBox createProductCard(ProduktDTO p) {
        VBox box = new VBox(8,
            new Label("Nazwa: " + p.getNazwaProduktu()),
            new Label("Waga: " + p.getWaga()),
            new Label("Kategoria: " + p.getKategoriaProd()),
            new Label("Nr seryjny: " + p.getNrSeryjny())
        );
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

    private List<ProduktDTO> getPackageProducts(List<Integer> ids) {
        List<ProduktDTO> myContents = new ArrayList<>();
        String resp = "";

        if (ids.isEmpty()) {
            System.err.println("No products inside...?");
        }

        for (Integer i : ids) {
            ProduktDTO tmp = new ProduktDTO();
            try {  
                RequestController rq = new RequestController("/produkt/dto/" + i.toString(), 0);

                resp = rq.sendPathReq();
            } catch (BadRequestException bre) {
                System.err.println("getPackageProducts: " + bre.getMessage());
            }

            System.out.println(resp);

            ObjectMapper mapper = new ObjectMapper().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.registerModule(new JavaTimeModule());
            try {
                tmp = mapper.readValue(resp, new TypeReference<ProduktDTO>(){});
                myContents.add(tmp);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        
        return myContents;
    }
}