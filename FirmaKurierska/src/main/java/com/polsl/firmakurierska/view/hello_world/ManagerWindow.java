package com.polsl.firmakurierska.view.hello_world;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import com.polsl.firmakurierska.model.Paczka;
import com.polsl.firmakurierska.model.Pojazd;

public class ManagerWindow extends Application {

    private VBox paczkiList;
    private VBox pojazdyList;
    private VBox dostawyList;

    @Override
    public void start(Stage stage) {
        
        // ===== KOL 1: PACZKI =====
        paczkiList = new VBox(5);
        paczkiList.setPadding(new Insets(5));
        ScrollPane paczkiScroll = new ScrollPane(paczkiList);
        paczkiScroll.setFitToWidth(true);
        paczkiScroll.setPrefHeight(300);
        
        List<Paczka> paczki = getPaczki();

        paczki.forEach(paczka -> {
            paczkiList.getChildren().add(createPackageItem("ID paczki: " + paczka.getIdPaczki(), paczka));
        });

        Button dodajPaczkeBtn = new Button("Dodaj paczkę");
        dodajPaczkeBtn.setOnAction(e -> new PackageFormWindow().show());
        VBox paczkiCol = buildColumn("Paczki", paczkiScroll, dodajPaczkeBtn, "#f4f4f4");

        // ===== KOL 2: POJAZDY =====
        pojazdyList = new VBox(5);
        pojazdyList.setPadding(new Insets(5));
        ScrollPane pojazdyScroll = new ScrollPane(pojazdyList);
        pojazdyScroll.setFitToWidth(true);
        pojazdyScroll.setPrefHeight(300);

        List<Pojazd> pojazdy = getPojazdy();

        pojazdy.forEach(pojazd -> {
            pojazdyList.getChildren().add(createVehicleItem("ID pojazdu: " + pojazd.getIdPojazdu(), pojazd));
        });

        Button dodajPojazdBtn = new Button("Dodaj pojazd");
        dodajPojazdBtn.setOnAction(e -> new VehicleFormWindow().show());
        VBox pojazdyCol = buildColumn("Pojazdy", pojazdyScroll, dodajPojazdBtn, "#eaeaea");

        // ===== KOL 3: DOSTAWY =====
        dostawyList = new VBox(5);
        dostawyList.setPadding(new Insets(5));
        ScrollPane dostawyScroll = new ScrollPane(dostawyList);
        dostawyScroll.setFitToWidth(true);
        dostawyScroll.setPrefHeight(300);

        List<DostawaDTO> dostawy = getDostawy();

        dostawy.forEach(dostawa -> {
            dostawyList.getChildren().add(createDeliveryItem("ID dostawy: " + dostawa.getIdDostawy(), dostawa.getIdDostawy()));
        });

        Button dodajDostaweBtn = new Button("Dodaj dostawę");
        /*
        dodajDostaweBtn.setOnAction(e ->
            new DeliveryFormWindow().show(
                
            )
        ); */
        VBox dostawyCol = buildColumn("Dostawy", dostawyScroll, dodajDostaweBtn, "#f4f4f4");

        // ===== KOL 4: RAPORTY =====
        Label raportLabel = new Label("Raporty");
        raportLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        raportLabel.setMaxWidth(Double.MAX_VALUE);
        raportLabel.setAlignment(Pos.CENTER);

        Button generujRaportBtn = new Button("Generuj raport");
        generujRaportBtn.setOnAction(e -> new RaportFormWindow().show());
        HBox raportBox = new HBox(generujRaportBtn);
        raportBox.setAlignment(Pos.CENTER);
        raportBox.setPadding(new Insets(10));

        VBox raportCol = new VBox(10, raportLabel, raportBox);
        raportCol.setPadding(new Insets(10));
        raportCol.setPrefWidth(200);
        raportCol.setStyle("-fx-background-color: #eaeaea;");

        // ===== GŁÓWNY UKŁAD =====
        HBox root = new HBox(10, paczkiCol, pojazdyCol, dostawyCol, raportCol);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 850, 450);
        stage.setScene(scene);
        stage.setTitle("Administrator Panel");
        stage.show();
    }

    private VBox buildColumn(String title, ScrollPane content, Button addButton, String bgColor) {
        Label header = new Label(title);
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        header.setMaxWidth(Double.MAX_VALUE);
        HBox headerBox = new HBox(header);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(10));

        HBox addBox = new HBox(addButton);
        addBox.setAlignment(Pos.CENTER);
        addBox.setPadding(new Insets(5));

        VBox col = new VBox(10, headerBox, content, addBox);
        col.setPadding(new Insets(10));
        col.setPrefWidth(200);
        col.setStyle("-fx-background-color: " + bgColor + ";");
        return col;
    }

    private HBox createPackageItem(String name, Paczka paczkaData) {
        Button itemBtn = new Button(name);
        itemBtn.setPrefWidth(140);
        itemBtn.setOnAction(e -> {

            var produkty = List.of(
                new PackageDescription.Product("Mysz bezprzewodowa", "0.2 kg", "Elektronika", "SN12345", "LogiTech"),
                new PackageDescription.Product("Kabel HDMI", "0.15 kg", "Akcesoria", "SN67890", "KabelPro"),
                new PackageDescription.Product("Notebook A4", "0.5 kg", "Papier", "SN54321", "PapierPlus")
            );
            new PackageDescription().show(
                "",
                "",
                paczkaData.getWagaPaczki(),
                produkty
            );
        });
        Button delBtn = new Button("X");
        delBtn.setOnAction(e -> paczkiList.getChildren().removeIf(node -> {
            if (node instanceof HBox hbox) {
                Button b = (Button) hbox.getChildren().get(0);
                return b.getText().equals(name);
            }
            return false;
        }));
        HBox box = new HBox(5, itemBtn, delBtn);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private HBox createVehicleItem(String name, Pojazd pojazdData) {
        Button itemBtn = new Button(name);
        itemBtn.setPrefWidth(140);
        itemBtn.setOnAction(e -> {
            new VehicleDescription()
                .show(
                    name,
                    pojazdData.getTypPojazdu(),
                    pojazdData.getMarka(),
                    pojazdData.getModel(),
                    Double.toString(pojazdData.getPojemnosc()),
                    pojazdData.getNrRejestr()
                );
        });
        Button delBtn = new Button("X");
        delBtn.setOnAction(e -> pojazdyList.getChildren().removeIf(node -> {
            if (node instanceof HBox hbox) {
                Button b = (Button) hbox.getChildren().get(0);
                return b.getText().equals(name);
            }
            return false;
        }));
        HBox box = new HBox(5, itemBtn, delBtn);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private HBox createDeliveryItem(String name, int dostawaId) {
        Button itemBtn = new Button(name);
        itemBtn.setPrefWidth(140);
        itemBtn.setOnAction(e -> {
            new DeliveryDescription().open(dostawaId, "", "");
        });
        Button delBtn = new Button("X");
        delBtn.setOnAction(e -> { 
            if (deleteDelivery(dostawaId)){
                dostawyList.getChildren().removeIf(node -> {
                if (node instanceof HBox hbox) {
                    Button b = (Button) hbox.getChildren().get(0);
                    return b.getText().equals(name);
                }
                return false;
                });
            }
        });
        HBox box = new HBox(5, itemBtn, delBtn);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private List<DostawaDTO> getDostawy() {
        List<DostawaDTO> mojeDostawy = new ArrayList<>();
        RequestController rq = new RequestController("/dostawa", 0);
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
        return mojeDostawy;
    }

    private List<Pojazd> getPojazdy() {
        List<Pojazd> mojePojazdy = new ArrayList<>();
        RequestController rq = new RequestController("/pojazd", 0);
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
                mojePojazdy = mapper.readValue(response, new TypeReference<List<Pojazd>>(){});
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }       
        return mojePojazdy;
    }

    private List<Paczka> getPaczki() {
        List<Paczka> mojePaczki = new ArrayList<>();
        RequestController rq = new RequestController("/paczka", 0);
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
                mojePaczki = mapper.readValue(response, new TypeReference<List<Paczka>>(){});
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }       
        return mojePaczki;
    }

    private boolean deleteDelivery(int delId) {
        RequestController rq = new RequestController("/dostawa/delete/" + Integer.toString(delId), 3);
        String resp = "";

        try {
            resp = rq.sendPathReq();

            if (resp.equals("Dostawa o ID " + Integer.toString(delId) + " została usunięta.")) {
                System.out.println("Usunięto dostawę");
                return true;
            }
        } catch (BadRequestException bre) {
            System.out.println("deleteDelivery: " + bre.getMessage());
            return false;
        }

        return false;
    }
}