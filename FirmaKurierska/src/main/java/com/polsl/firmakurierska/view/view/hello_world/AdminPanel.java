package com.polsl.firmakurierska.view.hello_world;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polsl.firmakurierska.controller.RequestController;
import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.exception.ResourceNotFoundException;
import com.polsl.firmakurierska.model.Konto;
import com.polsl.firmakurierska.model.Pracownik;
import com.polsl.firmakurierska.view.UIBuilder;
import com.polsl.firmakurierska.view.UIThemeManager;

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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AdminPanel extends Application {

    private final UIBuilder ui = UIBuilder.getUIBuilder();
    private final UIThemeManager theme = UIThemeManager.getUIThemeManager();
    private Stage myStage = null;

    private int loggedUserId = 0;
    private String loggedUserName = "Imię";
    private String loggedUserSurname = "Nazwisko";
    private VBox kontaList;

    public void open(int userId) {
        this.loggedUserId = userId;
        getMyName();
        if (myStage == null) {
            myStage = new Stage();
        }

        this.start(myStage);
    }

    @Override
    public void start(Stage stage) {
        // ================= LEWY PANEL (KONTA) =================

        Label welcomeLabel = new Label("Witaj " + loggedUserName + " " + loggedUserSurname + "!");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Button refreshBtn = ui.createStylizedButton(theme.getThemeMode(), 150, "Odśwież dane");
        
        Button themeSwitch = ui.createStylizedButton(theme.getThemeMode(), 70, "Motyw");
        themeSwitch.setOnAction(e -> {
            theme.setThemeMode(!theme.getThemeMode());
            this.start(myStage);
        });

        VBox welBox = new VBox(5, welcomeLabel, themeSwitch, refreshBtn);
        welBox.setAlignment(Pos.CENTER);
        welBox.setSpacing(10);

        kontaList = new VBox(5);
        kontaList.setPadding(new Insets(5));
        
        refreshBtn.setOnAction(e -> {
            refreshAllData(kontaList, refreshBtn);
        });

        List<Konto> accounts = getAllAccounts();

        List<String> workerNames = new ArrayList<>();
        
        accounts.forEach(account -> {
            List<String> listData = getWorkerData(account.getIdKonta());
            workerNames.add(listData.getFirst());

            kontaList.getChildren().add(createKontoItem(account, listData, refreshBtn));
        });

        // Pasek wyszukiwania
        TextField searchField = new TextField();
        searchField.setPromptText("Wyszukaj konto");
        Button searchButton = ui.createStylizedButton(theme.getThemeMode(), 80, "Szukaj");
        searchButton.setOnAction(e -> {
            String query = searchField.getText().toLowerCase();
            kontaList.getChildren().clear();

            accounts.forEach(account -> {
                List<String> listData = getWorkerData(account.getIdKonta());
                if (workerNames.get(accounts.indexOf(account)).toLowerCase().contains(query)) {
                    kontaList.getChildren().add(createKontoItem(account, listData, refreshBtn));
                }
            });
        });
        HBox searchBox = new HBox(5, searchField, searchButton);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(0, 0, 10, 0));

        ScrollPane kontaScroll = new ScrollPane(kontaList);
        kontaScroll.setFitToWidth(true);
        kontaScroll.setPrefHeight(300);

        Button dodajKontoButton = ui.createStylizedButton(theme.getThemeMode(), 150, "Dodaj Konto");
        dodajKontoButton.setOnAction(e -> {
            new AccountFormWindow().show(kontaList, refreshBtn, this);
        });

        HBox dodajKontoBox = new HBox(dodajKontoButton);
        dodajKontoBox.setAlignment(Pos.CENTER);

        VBox mainPanel = ui.createStylizedColumn(theme.getThemeMode(), "Konta", 260,
            searchBox,
            kontaScroll,
            dodajKontoBox
        );

        // ================= GŁÓWNY UKŁAD =================
        VBox root = new VBox(welBox, mainPanel);
        root.setPadding(new Insets(10));
        root.setSpacing(10);
        
        if (theme.getThemeMode()) {
            root.setStyle("-fx-background-color: #202020");
            welcomeLabel.setTextFill(Color.web("BBBBBB"));
        } else {
            root.setStyle("-fx-background-color: #F0F0F0");
        }

        Scene scene = new Scene(root, 260, 520);
        stage.setScene(scene);
        stage.setTitle("Admin Panel - Konta");
        stage.show();
    }

    private HBox createKontoItem(Konto account, List<String> data, Button rfshBtn) {
        String kontoName = data.getFirst();
        Integer acId = account.getIdKonta();

        Button kontoButton = ui.createStyledListItem(acId.toString() + ": " + kontoName, Integer.MAX_VALUE);
        kontoButton.setOnAction(e -> {
            System.out.println("Naciśnięto " + kontoName);
            new AccountDescription().show(
                data
            );
        });

        Button deleteButton = ui.createStyledDeleteButton();

        if (acId != loggedUserId) {
            deleteButton.setDisable(false);
            deleteButton.setOnAction(e -> {
                if (deleteAcc(acId)) {
                    kontaList.getChildren().removeIf(acc -> {
                        if (acc instanceof HBox hbox) {
                            Button btn = (Button) hbox.getChildren().get(0);
                            return btn.getText().equals(acId.toString() + ": " + kontoName);
                        }
                        return false;
                    });
                }
            });
        } else {
            deleteButton.setDisable(true);
        }

        Button editButton = ui.createStyledEditButton();
        editButton.setOnMouseClicked(e -> {
            System.out.println(data);
            new EditAccount().show(this, rfshBtn, kontaList, account, data);
        });;

        HBox hbox = new HBox(10, kontoButton, editButton, deleteButton);
        hbox.setAlignment(Pos.CENTER_LEFT);
        return hbox;
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

    /**
     * Gets worker data for specified account ID as a list of strings.
     * @param accountID account ID
     * @return list of strings, data order: imie, nazwisko, pesel, stanowisko, kategoria prawa jazdy
     */
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

    private boolean deleteAcc(int acId) {
        RequestController rq = new RequestController("/pracownik/delete/" + Integer.toString(acId), 3);
        String resp = "";
        
        try {
            resp = rq.sendPathReq();

        } catch (ResourceNotFoundException rex) {
            System.out.println("Nie udało się usunąć konta");
            return false;
        } catch (BadRequestException bre) {
            System.out.println("Niepoprawny request!");
            return false;
        }

        try {
            rq = new RequestController("/konto/delete/" + Integer.toString(acId), 3);
            resp = rq.sendPathReq();

        } catch (ResourceNotFoundException rex) {
            System.out.println("Nie udało się usunąć konta");
            return false;
        } catch (BadRequestException bre) {
            System.out.println("Niepoprawny request!");
            return false;
        }

        if (resp == "NRP") {
            return false;
        }

        System.out.println("Konto zostało usunięte!");
        return true;
    }

    public boolean refreshAllData(VBox targetContainer, Button refreshBtn) {
        targetContainer.setDisable(true);
        refreshBtn.setDisable(true);

        List<Konto> accounts = getAllAccounts();
        List<String> workerNames = new ArrayList<>();

        targetContainer.getChildren().clear();

        accounts.forEach(account -> {
            List<String> listData = getWorkerData(account.getIdKonta());
            workerNames.add(listData.getFirst());

            targetContainer.getChildren().add(createKontoItem(account, listData, refreshBtn));
        });



        targetContainer.setDisable(false);
        refreshBtn.setDisable(false);

        return true;
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
