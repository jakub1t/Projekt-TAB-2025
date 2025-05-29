package com.polsl.firmakurierska.view.hello_world;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminPanel extends Application {

    private VBox kontaList;

    @Override
    public void start(Stage stage) {
        // ================= LEWY PANEL (KONTA) =================
        kontaList = new VBox(5);
        kontaList.setPadding(new Insets(5));

        String[] kontaArray = {"Konto Adama", "Konto Beaty", "Konto Celiny"};
        // Inicjalne wypełnienie listy
        for (String kontoName : kontaArray) {
            kontaList.getChildren().add(createKontoItem(kontoName));
        }

        // Pasek wyszukiwania
        TextField searchField = new TextField();
        searchField.setPromptText("Wyszukaj konto");
        Button searchButton = new Button("Szukaj");
        searchButton.setOnAction(e -> {
            String query = searchField.getText().toLowerCase();
            kontaList.getChildren().clear();
            for (String kontoName : kontaArray) {
                if (kontoName.toLowerCase().contains(query)) {
                    kontaList.getChildren().add(createKontoItem(kontoName));
                }
            }
        });
        HBox searchBox = new HBox(5, searchField, searchButton);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(0, 0, 10, 0));

        ScrollPane kontaScroll = new ScrollPane(kontaList);
        kontaScroll.setFitToWidth(true);
        kontaScroll.setPrefHeight(300);

        Button dodajKontoButton = new Button("Dodaj konto");
        dodajKontoButton.setOnAction(e -> {
            String name = "Nowe Konto #" + (kontaList.getChildren().size() + 1);
            kontaList.getChildren().add(createKontoItem(name));
            new AccountFormWindow().show();
        });

        HBox kontaLabel = new HBox(new Label("Konta"));
        kontaLabel.setAlignment(Pos.CENTER);
        kontaLabel.setPadding(new Insets(10));
        kontaLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        HBox dodajKontoBox = new HBox(dodajKontoButton);
        dodajKontoBox.setAlignment(Pos.CENTER);

        VBox leftPanel = new VBox(10,
            kontaLabel,
            searchBox,
            kontaScroll,
            dodajKontoBox
        );
        leftPanel.setPadding(new Insets(10));
        leftPanel.setPrefWidth(350);
        leftPanel.setStyle("-fx-background-color: #f4f4f4;");

        // ================= GŁÓWNY UKŁAD =================
        HBox root = new HBox(leftPanel);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 260, 450);
        stage.setScene(scene);
        stage.setTitle("Admin Panel - Konta");
        stage.show();
    }

    private HBox createKontoItem(String kontoName) {
        Button kontoButton = new Button(kontoName);
        kontoButton.setPrefWidth(200);
        kontoButton.setOnAction(e -> {
            System.out.println("Naciśnięto " + kontoName);
            new AccountDescription().show(
                "Adam", "Kowalski", "90010112345", "Administrator", "B2"
            );
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

    public static void main(String[] args) {
        launch(args);
    }
}
