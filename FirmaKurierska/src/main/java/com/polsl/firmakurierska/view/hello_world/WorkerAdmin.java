package com.polsl.firmakurierska.view.hello_world;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class WorkerAdmin extends Application {

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

        final String[] paczkiArray = { "Paczka 1", "Paczka 2", "Paczka 3" };
        for (String name : paczkiArray) {
            paczkiList.getChildren().add(createPackageItem(name));
        }

        Button dodajPaczkeBtn = new Button("Dodaj paczkę");
        dodajPaczkeBtn.setOnAction(e -> new PackageFormWindow().show());

        VBox paczkiCol = buildColumn("Paczki", paczkiScroll, dodajPaczkeBtn, "#f4f4f4");

        // ===== KOL 2: POJAZDY =====
        pojazdyList = new VBox(5);
        pojazdyList.setPadding(new Insets(5));
        ScrollPane pojazdyScroll = new ScrollPane(pojazdyList);
        pojazdyScroll.setFitToWidth(true);
        pojazdyScroll.setPrefHeight(300);

        String[] pojazdyArray = { "Pojazd A", "Pojazd B", "Pojazd C" };
        for (String name : pojazdyArray) {
            pojazdyList.getChildren().add(createVehicleItem(name));
        }

        Button dodajPojazdBtn = new Button("Dodaj pojazd");
        dodajPojazdBtn.setOnAction(e -> new VehicleFormWindow().show());

        VBox pojazdyCol = buildColumn("Pojazdy", pojazdyScroll, dodajPojazdBtn, "#eaeaea");

        // ===== KOL 3: DOSTAWY =====
        dostawyList = new VBox(5);
        dostawyList.setPadding(new Insets(5));
        ScrollPane dostawyScroll = new ScrollPane(dostawyList);
        dostawyScroll.setFitToWidth(true);
        dostawyScroll.setPrefHeight(300);

        String[] dostawyArray = { "Dostawa 1", "Dostawa 2", "Dostawa 3" };
        for (String name : dostawyArray) {
            dostawyList.getChildren().add(createDeliveryItem(name));
        }

        Button dodajDostaweBtn = new Button("Dodaj dostawę");
        // przekazujemy listę dostępnych ID paczek
        dodajDostaweBtn.setOnAction(e ->
            new DeliveryFormWindow().show(List.of(paczkiArray))
        );

        VBox dostawyCol = buildColumn("Dostawy", dostawyScroll, dodajDostaweBtn, "#f4f4f4");

        // ===== KOL 4: RAPORTY =====
        Label raportLabel = new Label("Raporty");
        raportLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        raportLabel.setMaxWidth(Double.MAX_VALUE);
        raportLabel.setAlignment(Pos.CENTER);

        Button generujRaportBtn = new Button("Generuj raport");
        generujRaportBtn.setOnAction(e -> System.out.println("Generowanie raportu..."));
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

    private HBox createPackageItem(String name) {
        Button itemBtn = new Button(name);
        itemBtn.setPrefWidth(140);
        itemBtn.setOnAction(e -> {
            var produkty = List.of(
                new PackageDescription.Product("Mysz bezprzewodowa", "0.2 kg", "Elektronika", "SN12345", "LogiTech"),
                new PackageDescription.Product("Kabel HDMI", "0.15 kg", "Akcesoria", "SN67890", "KabelPro"),
                new PackageDescription.Product("Notebook A4", "0.5 kg", "Papier", "SN54321", "PapierPlus")
            );
            new PackageDescription().show(1.2, produkty);
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

    private HBox createVehicleItem(String name) {
        Button itemBtn = new Button(name);
        itemBtn.setPrefWidth(140);
        itemBtn.setOnAction(e -> {
            new VehicleDescription()
                .show("Ciężarowy", "Volvo", "FH16", "16 L", "ABC-1234");
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

    private HBox createDeliveryItem(String name) {
        Button itemBtn = new Button(name);
        itemBtn.setPrefWidth(140);
        itemBtn.setOnAction(e -> {
            // przykładowe dane wyświetlane w DeliveryDescription
            new DeliveryDescription().show(
                name,
                "2025-04-17",
                "2025-04-20",
                "Magazyn A",
                "Magazyn B",
                List.of("Paczka 1", "Paczka 2")
            );
        });

        Button delBtn = new Button("X");
        delBtn.setOnAction(e -> dostawyList.getChildren().removeIf(node -> {
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

    public static void main(String[] args) {
        launch(args);
    }
}
