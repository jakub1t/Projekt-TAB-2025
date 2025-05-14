package com.polsl.firmakurierska.view.hello_world;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminPanel extends Application {

    private VBox kontaList;
    private VBox pracownicyList;

    @Override
    public void start(Stage stage) {
        // ================= LEWY PANEL (KONTA) =================
        kontaList = new VBox(5);
        kontaList.setPadding(new Insets(5));

        ScrollPane kontaScroll = new ScrollPane(kontaList);
        kontaScroll.setFitToWidth(true);
        kontaScroll.setPrefHeight(300);

        String[] kontaArray = {"Konto Adama", "Konto Beaty", "Konto Celiny"};
        for (String kontoName : kontaArray) {
            kontaList.getChildren().add(createKontoItem(kontoName));
        }

        Button dodajKontoButton = new Button("Dodaj konto");
        dodajKontoButton.setOnAction(e -> {
            String name = "Nowe Konto #" + (kontaList.getChildren().size() + 1);
            kontaList.getChildren().add(createKontoItem(name));
            AccountFormWindow form = new AccountFormWindow();
            form.show();

        });

        HBox kontaLabel = new HBox(new javafx.scene.control.Label("Konta"));
        kontaLabel.setAlignment(Pos.CENTER);
        kontaLabel.setPadding(new Insets(10));
        kontaLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        HBox dodajKontoBox = new HBox(dodajKontoButton);
        dodajKontoBox.setAlignment(Pos.CENTER);

        VBox leftPanel = new VBox(10, kontaLabel, kontaScroll, dodajKontoBox);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setPrefWidth(350);
        leftPanel.setStyle("-fx-background-color: #f4f4f4;");

        // ================= PRAWY PANEL (PRACOWNICY) =================
        pracownicyList = new VBox(5);
        pracownicyList.setPadding(new Insets(5));

        ScrollPane pracownicyScroll = new ScrollPane(pracownicyList);
        pracownicyScroll.setFitToWidth(true);
        pracownicyScroll.setPrefHeight(300);

        String[] pracownicyArray = {"Pracownik Adama", "Pracownik Beaty", "Pracownik Celiny"};
        for (String pracName : pracownicyArray) {
            pracownicyList.getChildren().add(createPracownikItem(pracName));
        }

        Button dodajPracownikaButton = new Button("Dodaj pracownika");
        dodajPracownikaButton.setOnAction(e -> {
            String name = "Nowy Pracownik #" + (pracownicyList.getChildren().size() + 1);
            pracownicyList.getChildren().add(createPracownikItem(name));
        });

        HBox pracLabel = new HBox(new javafx.scene.control.Label("Pracownicy"));
        pracLabel.setAlignment(Pos.CENTER);
        pracLabel.setPadding(new Insets(10));
        pracLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        HBox dodajPracBox = new HBox(dodajPracownikaButton);
        dodajPracBox.setAlignment(Pos.CENTER);

        VBox rightPanel = new VBox(10, pracLabel, pracownicyScroll, dodajPracBox);
        rightPanel.setPadding(new Insets(10));
        rightPanel.setPrefWidth(350);
        rightPanel.setStyle("-fx-background-color: #eaeaea;");

        // ================= GŁÓWNY UKŁAD =================
        HBox root = new HBox(10, leftPanel, rightPanel);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 600, 450);
        stage.setScene(scene);
        stage.setTitle("Admin Panel - Konta i Pracownicy");
        stage.show();
    }

    private HBox createKontoItem(String kontoName) {
        Button kontoButton = new Button(kontoName);
        kontoButton.setPrefWidth(200);
        kontoButton.setOnAction(e -> {
            System.out.println("Naciśnięto " + kontoName);
            

            AccountDescription info = new AccountDescription();
            info.show("Adam", "Kowalski", "90010112345", "Administrator", "B2");


            
        });

        Button deleteButton = new Button("X");
        deleteButton.setOnAction(e -> kontaList.getChildren().removeIf(node -> {
            if (node instanceof HBox hbox) {
                Button btn = (Button) hbox.getChildren().get(0);
                return btn.getText().equals(kontoName);
            }
            return false;
        }));

        HBox hbox = new HBox(10, kontoButton, deleteButton);
        hbox.setAlignment(Pos.CENTER_LEFT);
        return hbox;
    }

    private HBox createPracownikItem(String pracName) {
        Button pracButton = new Button(pracName);
        pracButton.setPrefWidth(200);
        pracButton.setOnAction(e -> {
            System.out.println("Naciśnięto " + pracName);
        });

        Button deleteButton = new Button("X");
        deleteButton.setOnAction(e -> pracownicyList.getChildren().removeIf(node -> {
            if (node instanceof HBox hbox) {
                Button btn = (Button) hbox.getChildren().get(0);
                return btn.getText().equals(pracName);
            }
            return false;
        }));

        HBox hbox = new HBox(10, pracButton, deleteButton);
        hbox.setAlignment(Pos.CENTER_LEFT);
        return hbox;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
