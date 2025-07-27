package com.polsl.firmakurierska.view.hello_world;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import com.polsl.firmakurierska.controller.RequestController;
import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.view.UIBuilder;
import com.polsl.firmakurierska.view.UIThemeManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AccountFormWindow {

    private final UIThemeManager theme = UIThemeManager.getUIThemeManager();
    private final UIBuilder ui = UIBuilder.getUIBuilder();
    private Stage myStage = null;

    private String selectedPosition = "";

    public void show(VBox accountContainer, Button rfshBtn, AdminPanel parent) {
        // Pola do wpisywania danych
        TextField imieField        = new TextField();
        TextField nazwiskoField    = new TextField();
        TextField peselField       = new TextField();
        TextField loginField       = new TextField();
        PasswordField hasloField   = new PasswordField();

        imieField.setPromptText("Imię pracownika, tylko litery");
        nazwiskoField.setPromptText("Nazwisko pracownika, tylko litery");
        peselField.setPromptText("PESEL pracownika, tylko cyfry");
        loginField.setPromptText("Login dla pracownika, litery + cyfry");
        hasloField.setPromptText("Hasło dla pracownika, dowolne znaki");

        ui.showAlertDialog(AlertType.INFORMATION, "Test", "Header", "Some message...");

        // Dostępne typy prawa jazdy
        List<String> licenseIDs = getAllLicensesIDs();
        
        List<String> licenseNames = getAllLicensesNames(licenseIDs);


        VBox prawoJazdyBox = createCheckboxInputCard("Prawo jazdy:", licenseNames.toArray(new String[0]));

        // Dostępne typy stanowiska
        List<String> positionsIDs = new ArrayList<>();
        positionsIDs = getAllPositionsIDs();

        List<String> positionsNames = new ArrayList<>();
        positionsNames = getAllPositionsNames(positionsIDs);

        VBox stanowiskoBox = createRadioInputCard("Stanowisko:", positionsNames.toArray(new String[0]));

        VBox imieBox       = ui.createFormInputCard(theme.getThemeMode(), "Imię:", imieField);
        VBox nazwiskoBox   = ui.createFormInputCard(theme.getThemeMode(), "Nazwisko:", nazwiskoField);
        VBox peselBox      = ui.createFormInputCard(theme.getThemeMode(), "PESEL:", peselField);
        VBox loginBox      = ui.createFormInputCard(theme.getThemeMode(), "Login:", loginField);
        VBox hasloBox      = ui.createFormInputCard(theme.getThemeMode(), "Hasło:", hasloField);

        Button dodajButton = ui.createStylizedButton(theme.getThemeMode(), 160, "Dodaj konto");
        dodajButton.setOnAction(e -> {
            // Zbieranie wybranych typów prawa jazdy
            List<String> selectedLicenses = new ArrayList<>();
            for (Node node : prawoJazdyBox.getChildren()) {
                if (node instanceof CheckBox cb && cb.isSelected()) {
                    selectedLicenses.add(cb.getText());
                }
            }
        
            for (Node node : stanowiskoBox.getChildren()) {
                
                if (node instanceof CheckBox cb && cb.isSelected()) {
                    selectedPosition = cb.getText();
                    break;
                }
            }
            
            String imie = imieField.getText();
            String nazwisko = nazwiskoField.getText();
            String pesel = peselField.getText();
            String login = loginField.getText();
            String haslo = hasloField.getText();

            // Wypisanie danych do terminala
           
            System.out.println("Imię: " + imieField.getText());
            System.out.println("Nazwisko: " + nazwiskoField.getText());
            System.out.println("PESEL: " + peselField.getText());
            System.out.println("Stanowisko: " + selectedPosition);
            System.out.println("Prawo jazdy: " + selectedLicenses);
            System.out.println("Login: " + loginField.getText());
            System.out.println("Hasło: " + hasloField.getText());
            
            String selectedPositionId = getSelectedPositionsId(selectedPosition);

            List<Integer> selectedLicensesIDs = new ArrayList<>();
            for (String selected : selectedLicenses) {
                int index = licenseNames.indexOf(selected);
                if (index != -1) { // Make sure the selected license exists in licenseNames
                    selectedLicensesIDs.add(index+1);
                }
            }
            
            addNewWorker(imie, nazwisko, pesel, login, selectedPositionId, haslo, selectedLicensesIDs);
            
            
            parent.refreshAllData(accountContainer, rfshBtn);
            myStage.close();
            
        });

        HBox dodajBox = new HBox(dodajButton);
        dodajBox.setAlignment(Pos.CENTER);

        VBox allFields = new VBox(15,
                imieBox, nazwiskoBox, peselBox, stanowiskoBox, prawoJazdyBox, loginBox, hasloBox, dodajBox);
        allFields.setPadding(new Insets(20));
        allFields.setAlignment(Pos.CENTER);

        ScrollPane mainScroll = new ScrollPane(allFields);
        mainScroll.setFitToWidth(true);
        VBox mainContainer = new VBox(allFields, mainScroll);

        BorderPane root = new BorderPane(mainContainer);
        BorderPane.setAlignment(mainContainer, Pos.CENTER);

        Scene scene = new Scene(root, 400, 550);

        if (theme.getThemeMode()) {
            allFields.setBackground(ui.unifiedRootBgDark);
            root.setBackground(ui.unifiedRootBgDark);
        } else {
            allFields.setBackground(ui.unifiedRootBgLight);
            root.setBackground(ui.unifiedRootBgLight);
        }

        myStage = new Stage();
        myStage.setTitle("Dodawanie konta");
        myStage.setScene(scene);
        myStage.show();
    }

    // Tworzy kartę z etykietą oraz wielokrotną listą checkboxów
    private VBox createCheckboxInputCard(String labelText, String[] options) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");

        VBox box = ui.createListContainer(theme.getThemeMode());
        box.getChildren().add(label);

        for (String opt : options) {
            CheckBox cb = ui.createFancyCheckBox(theme.getThemeMode(), opt);
            box.getChildren().add(cb);
        }

        box.setPadding(new Insets(10));
        
        if (theme.getThemeMode()) {
            label.setTextFill(Color.web("#BBBBBB"));
            box.setStyle(
            "-fx-background-color: #424242;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 1);"
        );
        } else {
            box.setStyle(
            "-fx-background-color: #f4f4f4;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 1);"
            );
        }
        return box;
    }

    private VBox createRadioInputCard(String labelText, String[] options) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");
        VBox box = ui.createListContainer(theme.getThemeMode());
        box.getChildren().add(label);
        ToggleGroup sGroup = new ToggleGroup();

        for (String option : options) {
            RadioButton rb = ui.createFancyRadioButton(theme.getThemeMode(), option);
            rb.setToggleGroup(sGroup);
            rb.setPrefHeight(25);
            rb.setOnMouseClicked(e -> {
                this.selectedPosition = rb.getText();
                System.out.println("Selected position: " + this.selectedPosition);
            });
            box.getChildren().add(rb);
        }

        return box;
    }

    private List<String> getAllLicensesIDs(){

        List<String> licenses_id = new ArrayList<>();
        
        String response = "";

        // Prepare request to get all accounts from db
        RequestController rq = new RequestController("/prawojazdy", 0);
        

        response = rq.sendPathReq();
        // Get licenses
        
        licenses_id = extractID(response);
        return licenses_id;
        
    }

    private List<String> getAllLicensesNames(List<String> licenses_id){

        List<String> licenses_names = new ArrayList<>();

        licenses_id.forEach(kategoria -> {
            RequestController tempRq = new RequestController("/prawojazdy/" + kategoria, 0);
            String tempResponse = new String(tempRq.sendPathReq());
               
            try {
                JSONObject prawoJazdyJson = new JSONObject(tempResponse);
                   licenses_names.add(prawoJazdyJson.getString("kategoria"));
            }
            catch (JSONException jex) {
                System.out.println(jex.toString());
                jex.printStackTrace();
            }
        });

        return licenses_names;
    }

      private List<String> getAllPositionsIDs(){ // do poprawy

        List<String> positions_id = new ArrayList<>();
        
        String response = "";

        // Prepare request to get all accounts from db
        RequestController rq = new RequestController("/stanowisko", 0);
        
        response = rq.sendPathReq();
        // Get licenses
        
        positions_id = extractID(response);
        return positions_id;
    }

    private List<String> getAllPositionsNames(List<String> positions_id){

            List<String> positions_names = new ArrayList<>();

            positions_id.forEach(kategoria -> {
                RequestController tempRq = new RequestController("/stanowisko/" + kategoria, 0);
                String tempResponse = new String(tempRq.sendPathReq());
                
                try {
                    JSONObject stanowiskoJson = new JSONObject(tempResponse);
                    positions_names.add(stanowiskoJson.getString("nazwaStanowiska"));
                }
                catch (JSONException jex) {
                    System.out.println(jex.toString());
                    jex.printStackTrace();
                }
            });

            return positions_names;
        }

    //new add 
    private void addNewWorker(String imie, String nazwisko, String pesel, String login, String stanowiskoId, String haslo, List<Integer> prawoJazdy) {
        
        String kontoJson = String.format("{\"login\": \"%s\",\"haslo\": \"%s\"}", login, haslo);
      
        String licenseJsonArray = prawoJazdy.toString(); // assume prawoJazdy is a List<Integer>
        String pracownikJson = String.format(
            "{\"imie\":\"%s\",\"nazwisko\":\"%s\",\"pesel\":\"%s\",\"konto\":%s,\"stanowiskoId\": %s,\"prawaJazdyIds\": %s}",
            imie, nazwisko, pesel, kontoJson, stanowiskoId, licenseJsonArray
        );

        // try to create pracownik
        RequestController rqPracownik = new RequestController("/pracownik/create", 1);
        try{
            rqPracownik.sendJsonReq(pracownikJson);
        }catch(BadRequestException ex){
            System.out.println("Błąd podczas dodawania konta lub pracownika: " + ex.getMessage());
            return ;
        }

        System.out.println("Dodano konto i pracownika.");    
    }

    public List<String> extractID(String jsonData) {
        List<String> categoriesID = new ArrayList<>();

        try {
            JSONArray categoriesArray = new JSONArray(jsonData);

            for (int i = 0; i < categoriesArray.length(); i++) {
                JSONObject categoryObj = categoriesArray.getJSONObject(i);
                
                if (categoryObj.has("idKat")) {
                    // Convert idKat to String and add to list
                    categoriesID.add(String.valueOf(categoryObj.getInt("idKat")));
                }
                else if (categoryObj.has("idStanowisko")){

                    categoriesID.add(String.valueOf(categoryObj.getInt("idStanowisko")));
                }
            }

        } catch (JSONException ex) {
            System.out.println("Error parsing JSON: " + ex.getMessage());
            ex.printStackTrace();
        }

        return categoriesID;
    }

    private String getSelectedPositionsId(String selectedPosition) {
        String response = "";

        RequestController rq = new RequestController("/stanowisko/szukaj?nazwa=" + selectedPosition, 0);
        try {
            response = rq.sendPathReq();
            System.out.println(response);

        } catch (BadRequestException ex) {
            System.out.println("getSelectedPositionsId: " + ex.getMessage());
        }

        String positionId = "";
        try {
            JSONArray stanowiskoJArray = new JSONArray(response);
            
            positionId = stanowiskoJArray.getJSONObject(0).getString("idStanowisko");
                
        } catch (JSONException ex) {
            System.out.println("Error parsing JSON: " + ex.getMessage());
            ex.printStackTrace();
        }

        return positionId;
    }
}

