package com.polsl.firmakurierska.view.hello_world;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polsl.firmakurierska.controller.RequestController;
import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.model.Konto;

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

        List<Konto> accounts = new ArrayList<>();

        accounts = getAllAccounts();

        List<List<String>> workerData = new ArrayList<>();
        
        accounts.forEach(account -> {
            List<String> listData = getWorkerData(account.getIdKonta());
            workerData.add(listData);
        });

        workerData.forEach(data -> {kontaList.getChildren().add(createKontoItem(data));});

        // Pasek wyszukiwania
        TextField searchField = new TextField();
        searchField.setPromptText("Wyszukaj konto");
        Button searchButton = new Button("Szukaj");
        searchButton.setOnAction(e -> {
            String query = searchField.getText().toLowerCase();
            kontaList.getChildren().clear();

            workerData.forEach(data -> {
                if (data.getFirst().toLowerCase().contains(query)) {
                    kontaList.getChildren().add(createKontoItem(data));
                }
            });
        });
        HBox searchBox = new HBox(5, searchField, searchButton);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(0, 0, 10, 0));

        ScrollPane kontaScroll = new ScrollPane(kontaList);
        kontaScroll.setFitToWidth(true);
        kontaScroll.setPrefHeight(300);

        Button dodajKontoButton = new Button("Dodaj konto");
        dodajKontoButton.setOnAction(e -> {
            // String name = "Nowe Konto #" + (kontaList.getChildren().size() + 1);
            kontaList.getChildren().add(createKontoItem(Arrays.asList(
                "Imię", "Nazwisko", "12345678901", "Stanowisko", "Kategoria prawa jazdy"
                )));
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

    private HBox createKontoItem(List<String> data) {
        String kontoName = data.getFirst();

        Button kontoButton = new Button(kontoName);
        kontoButton.setPrefWidth(200);
        kontoButton.setOnAction(e -> {
            System.out.println("Naciśnięto " + kontoName);
            new AccountDescription().show(
                data
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

    private List<Konto> getAllAccounts(){
        List<Konto> accounts = new ArrayList<>();
        String response = "";

        // Prepare request to get all accounts from db
        RequestController rq = new RequestController("/konto/all", 0);

        // Get accounts
        try {
            response = rq.sendPathReq();
        }
        catch (BadRequestException e) {
            System.out.println(e.getMessage());
        }

        // Map JSON string into List<Konto>
        ObjectMapper mapper = new ObjectMapper();

        try {
            accounts = mapper.readValue(response, new TypeReference<List<Konto>>(){});
        }
        catch (IOException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }

        return accounts;
    }

    private List<String> getWorkerData(Integer accountID) {
        List<String> workerData = new ArrayList<>();
        String response = "";

        // Prepare request to get all accounts from db
        RequestController rq = new RequestController("/pracownik/" + accountID, 1);

        response = rq.sendPathReq();

        String stanowiskoID = rq.getStanowisko(response);

        List<String> kategoriePrawaJazdy = rq.getKategorie(response);

        try {
            JSONObject jsonData = new JSONObject(response);

            workerData.add(jsonData.getString("imie"));
            workerData.add(jsonData.getString("nazwisko"));
            workerData.add(jsonData.getString("pesel"));

            rq = new RequestController("/stanowisko/" + stanowiskoID, 0);

            response = new String(rq.sendPathReq());

            JSONObject stanowiskoJson = new JSONObject(response);
            workerData.add(stanowiskoJson.getString("nazwaStanowiska"));

            kategoriePrawaJazdy.forEach(kategoria -> {
                RequestController tempRq = new RequestController("/prawojazdy/" + kategoria, 0);

                String tempResponse = new String(tempRq.sendPathReq());
                
                try {
                    JSONObject prawoJazdyJson = new JSONObject(tempResponse);
                    workerData.add(prawoJazdyJson.getString("kategoria"));
                }
                catch (JSONException jex) {
                    System.out.println(jex.toString());
                    jex.printStackTrace();
                }
            });
        }
        catch (JSONException jex) {
            System.out.println(jex.toString());
            jex.printStackTrace();
        }

        return workerData;
    }
}
