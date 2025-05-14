/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.polsl.firmakurierska.view.hello_world;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class PackageDescription {

    /** Prosty model produktu w paczce */
    public static class Product {
        public final String nazwa;
        public final String waga;
        public final String kategoria;
        public final String numerSeryjny;
        public final String producent;

        public Product(String nazwa, String waga, String kategoria, String numerSeryjny, String producent) {
            this.nazwa = nazwa;
            this.waga = waga;
            this.kategoria = kategoria;
            this.numerSeryjny = numerSeryjny;
            this.producent = producent;
        }
    }

    /**
     * Pokazuje okno ze szczegółami paczki:
     * – waga paczki
     * – przewijana lista produktów
     */
    public void show(double wagaPaczki, List<Product> produkty) {
        // Karta z wagą paczki
        VBox wagaBox = createCard("Waga paczki:", wagaPaczki + " kg");

        // Kontener na karty produktów
        VBox produktyList = new VBox(10);
        produktyList.setPadding(new Insets(5));
        for (Product p : produkty) {
            produktyList.getChildren().add(createProductCard(p));
        }

        ScrollPane produktyScroll = new ScrollPane(produktyList);
        produktyScroll.setFitToWidth(true);
        produktyScroll.setPrefHeight(300);

        // Wszystkie elementy w pionie
        VBox allFields = new VBox(15, wagaBox, produktyScroll);
        allFields.setPadding(new Insets(20));
        allFields.setAlignment(Pos.CENTER);

        // Główne tło
        BorderPane root = new BorderPane(allFields);
        root.setStyle("-fx-background-color: #f8f8f8;");
        BorderPane.setAlignment(allFields, Pos.CENTER);

        Scene scene = new Scene(root, 450, 500);
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
    private VBox createProductCard(Product p) {
        VBox box = new VBox(8,
            new Label("Nazwa: " + p.nazwa),
            new Label("Waga: " + p.waga),
            new Label("Kategoria: " + p.kategoria),
            new Label("Nr seryjny: " + p.numerSeryjny),
            new Label("Producent: " + p.producent)
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
}
