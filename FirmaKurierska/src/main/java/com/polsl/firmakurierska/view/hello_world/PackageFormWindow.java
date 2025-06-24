package com.polsl.firmakurierska.view.hello_world;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.polsl.firmakurierska.controller.RequestController;
import com.polsl.firmakurierska.exception.BadRequestException;

public class PackageFormWindow {

    /** Model pojedynczego produktu */
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

    private final List<Product> products = new ArrayList<>();
    private VBox productsList;

    /** Pokazuje główne okno dodawania paczki */
    public void show() {
        // --- Pole na imię klienta ---
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("np. Jan");
        VBox firstNameBox = createInputCard("Imię klienta:", firstNameField);

        // --- Pole na nazwisko klienta ---
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("np. Kowalski");
        VBox lastNameBox = createInputCard("Nazwisko klienta:", lastNameField);

        // --- Pole na nazwę paczki ---
        TextField packageNameField = new TextField();
        packageNameField.setPromptText("np. Paczka nr 1");
        VBox packageNameBox = createInputCard("Nazwa paczki:", packageNameField);

        // --- Pole na wagę paczki ---
        TextField weightField = new TextField();
        weightField.setPromptText("np. 2.5 kg");
        VBox weightBox = createInputCard("Waga paczki:", weightField);

        // --- Lista produktów (pusta na start) ---
        productsList = new VBox(8);
        productsList.setPadding(new Insets(5));
        ScrollPane productsScroll = new ScrollPane(productsList);
        productsScroll.setFitToWidth(true);
        productsScroll.setPrefHeight(250);

        // --- Przycisk dodawania nowego produktu ---
        Button addProductBtn = new Button("Dodaj produkt");
        addProductBtn.setOnAction(e -> {
            new ProductFormWindow().show(product -> {
                products.add(product);
                productsList.getChildren().add(createProductCard(product));
            });
        });
        HBox addProdBox = new HBox(addProductBtn);
        addProdBox.setAlignment(Pos.CENTER);

        // --- Przycisk zapisania całej paczki ---
        Button savePackageBtn = new Button("Dodaj paczkę");
        savePackageBtn.setOnAction(e -> {
            System.out.println("Nowa paczka:");
            System.out.println("Imię klienta: " + firstNameField.getText());
            System.out.println("Nazwisko klienta: " + lastNameField.getText());
            System.out.println("Nazwa: " + packageNameField.getText());
            System.out.println("Waga: " + weightField.getText());
            for (Product p : products) {
                System.out.printf(" - %s | %s | %s | %s | %s%n",
                    p.nazwa, p.waga, p.kategoria, p.numerSeryjny, p.producent);
            }
        });
        HBox saveBox = new HBox(savePackageBtn);
        saveBox.setAlignment(Pos.CENTER);

        // --- Układ wszystkich elementów ---
        VBox container = new VBox(15,
            firstNameBox,
            lastNameBox,
            packageNameBox,
            weightBox,
            new Label("Produkty:"), productsScroll, addProdBox,
            saveBox
        );
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.TOP_CENTER);

        BorderPane root = new BorderPane(container);
        root.setStyle("-fx-background-color: #f8f8f8;");

        Stage stage = new Stage();
        stage.setTitle("Formularz paczki");
        stage.setScene(new Scene(root, 400, 740));
        stage.show();
    }

    /** Karta wejściowa z etykietą i polem */
    private VBox createInputCard(String labelText, Control inputField) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");
        VBox box = new VBox(5, label, inputField);
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

    /** Karta wyświetlająca dane produktu na liście */
    private VBox createProductCard(Product p) {
        VBox box = new VBox(5,
            new Label("Nazwa: " + p.nazwa),
            new Label("Waga: " + p.waga),
            new Label("Kategoria: " + p.kategoria),
            new Label("Nr seryjny: " + p.numerSeryjny),
            new Label("Producent: " + p.producent)
        );
        box.setPadding(new Insets(8));
        box.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #cccccc;
            -fx-border-radius: 6;
            -fx-background-radius: 6;
        """);
        return box;
    }
}

/** Okno do wprowadzania jednego produktu */
class ProductFormWindow {

    public void show(Consumer<PackageFormWindow.Product> onSave) {
        TextField nameField = new TextField();
        TextField weightField = new TextField();
        TextField categoryField = new TextField();
        TextField serialField = new TextField();
        TextField producerField = new TextField();

        VBox nameBox     = createInputCard("Nazwa:", nameField);
        VBox weightBox   = createInputCard("Waga:", weightField);
        VBox categoryBox = createInputCard("Kategoria:", categoryField);
        VBox serialBox   = createInputCard("Nr seryjny:", serialField);
        VBox prodBox     = createInputCard("Producent:", producerField);

        Button saveBtn = new Button("Zapisz produkt");
        saveBtn.setOnAction(e -> {
            PackageFormWindow.Product p = new PackageFormWindow.Product(
                nameField.getText(),
                weightField.getText(),
                categoryField.getText(),
                serialField.getText(),
                producerField.getText()
            );
            onSave.accept(p);
            ((Stage) saveBtn.getScene().getWindow()).close();
        });
        HBox btnBox = new HBox(saveBtn);
        btnBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(12,
            nameBox, weightBox, categoryBox, serialBox, prodBox, btnBox
        );
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f8f8f8;");

        Stage stage = new Stage();
        stage.setTitle("Nowy produkt");
        stage.setScene(new Scene(root, 450, 450));
        stage.show();
    }

    private VBox createInputCard(String labelText, Control inputField) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");
        VBox box = new VBox(5, label, inputField);
        box.setPadding(new Insets(8));
        box.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #dddddd;
            -fx-border-radius: 6;
            -fx-background-radius: 6;
        """);
        return box;
    }

    /**
     * 
     * @param jsonData - already formatted data
     * @return True when successful / False otherwise
     */
    private boolean addPackage(String jsonData) {
        RequestController rq = new RequestController("/paczka", 1);
        String resp = "";

        try {
            resp = rq.sendJsonReq(jsonData);

        } catch (BadRequestException bre) {
            System.out.println("addPackage: " + bre.getMessage());
            return false;
        }

        return true;
    }
}
