package com.polsl.firmakurierska.view.hello_world;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import com.polsl.firmakurierska.controller.RequestController;
import com.polsl.firmakurierska.exception.BadRequestException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AccountFormWindow {

    public void show() {
        // Pola do wpisywania danych
        TextField imieField        = new TextField();
        TextField nazwiskoField    = new TextField();
        TextField peselField       = new TextField();
        TextField loginField       = new TextField();
        PasswordField hasloField   = new PasswordField();

        // Dostępne typy prawa jazdy
        List<String> licenseIDs = new ArrayList<>();
        licenseIDs = getAllLicensesIDs();

        List<String> licenseNames = new ArrayList<>();
        licenseNames = getAllLicensesNames(licenseIDs);

        VBox prawoJazdyBox = createCheckboxInputCard("Prawo jazdy:", licenseNames.toArray(new String[0]));

        // Dostępne typy stanowiska
        List<String> positionsIDs = new ArrayList<>();
        positionsIDs = getAllPositionsIDs();

        List<String> positionsNames = new ArrayList<>();
        positionsNames = getAllPositionsNames(licenseIDs);

        VBox stanowiskoBox = createCheckboxInputCard("Prawo jazdy:", positionsNames.toArray(new String[0]));


        VBox imieBox       = createInputCard("Imię:", imieField);
        VBox nazwiskoBox   = createInputCard("Nazwisko:", nazwiskoField);
        VBox peselBox      = createInputCard("PESEL:", peselField);
        //VBox stanowiskoBox = createInputCard("Stanowisko:", stanowiskoField);
        VBox loginBox      = createInputCard("Login:", loginField);
        VBox hasloBox      = createInputCard("Hasło:", hasloField);

        Button dodajButton = new Button("Dodaj");
        dodajButton.setOnAction(e -> {
            // Zbieranie wybranych typów prawa jazdy
            List<String> selectedLicenses = new ArrayList<>();
            for (Node node : prawoJazdyBox.getChildren()) {
                if (node instanceof CheckBox cb && cb.isSelected()) {
                    selectedLicenses.add(cb.getText());
                }
            }
        
            String selectedPosition = "";
            
            int temp_pos = 0;
            for (Node node : stanowiskoBox.getChildren()) {
                
                if (node instanceof CheckBox cb && cb.isSelected()) {
                    selectedPosition = cb.getText();
                    break;
                }
            }
            
            String imie = imieField.getText();
            String nazwisko = nazwiskoField.getText();
            String pesel = peselField.getText();
            //String stanowisko = stanowiskoField.getText();
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
            
            

            addNewWorker(imie, nazwisko, pesel, login, selectedPosition, haslo, selectedLicenses);
        });

        HBox dodajBox = new HBox(dodajButton);
        dodajBox.setAlignment(Pos.CENTER);

        VBox allFields = new VBox(15,
                imieBox, nazwiskoBox, peselBox, stanowiskoBox, prawoJazdyBox, loginBox, hasloBox, dodajBox);
        allFields.setPadding(new Insets(20));
        allFields.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane(allFields);
        root.setStyle("-fx-background-color: #f8f8f8;");
        BorderPane.setAlignment(allFields, Pos.CENTER);

        Scene scene = new Scene(root, 400, 750);

        Stage stage = new Stage();
        stage.setTitle("Dodawanie konta");
        stage.setScene(scene);
        stage.show();
    }

    // Tworzy kartę z etykietą i polem wejściowym
    private VBox createInputCard(String labelText, Control inputField) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");

        VBox box = new VBox(5, label, inputField);
        box.setPadding(new Insets(10));
        box.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #dddddd;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 1);"
        );
        return box;
    }

    // Tworzy kartę z etykietą oraz wielokrotną listą checkboxów
    private VBox createCheckboxInputCard(String labelText, String[] options) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");

        VBox box = new VBox(5);
        box.getChildren().add(label);

        for (String opt : options) {
            CheckBox cb = new CheckBox(opt);
            box.getChildren().add(cb);
        }

        box.setPadding(new Insets(10));
        box.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #dddddd;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 1);"
        );
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
                    JSONObject prawoJazdyJson = new JSONObject(tempResponse);
                    positions_names.add(prawoJazdyJson.getString("nazwaStanowiska"));
                }
                catch (JSONException jex) {
                    System.out.println(jex.toString());
                    jex.printStackTrace();
                }
            });

            return positions_names;
        }

    //new add 
    private void addNewWorker(String imie, String nazwisko, String pesel, String login, String stanowisko, String haslo, List<String> prawoJazdy) {
        
        String kontoResp = "";
        String pracownikResp = "";
        String kontoJson = String.format("{\"login\": \"%s\", \"haslo\": \"%s\"}", login, haslo);
        
        //try to create konto
        RequestController rqKonto = new RequestController("/konto/add", 1);
        try {
            
            kontoResp = rqKonto.sendJsonReq(kontoJson);

        } catch (BadRequestException ex) {
            System.out.println("Błąd podczas dodawania konta: " + ex.getMessage());
            return ;
        }
        System.out.println("Konto response: " + kontoResp);

        int kontoId = 0;
                            
        try {
           kontoId = new JSONObject(kontoResp).getInt("idKonta");
        }
        catch (JSONException jex) {
            System.out.println(jex.toString());
            jex.printStackTrace();
        }


        String licenseJsonArray = prawoJazdy.toString().replace("[", "[\"").replace("]", "\"]").replace(", ", "\", \"");
        String pracownikJson = String.format(
            "{\"imie\": \"%s\", \"nazwisko\": \"%s\", \"pesel\": \"%s\", \"stanowisko\": 2, \"prawo_jazdy\": %s, \"konto_id\": \"%s\"}",
            imie, nazwisko, pesel, stanowisko, licenseJsonArray, kontoId
        );

        // try to create pracownik
        RequestController rqPracownik = new RequestController("/pracownik", 1);
        try{

            pracownikResp = rqPracownik.sendJsonReq(pracownikJson);

        }catch(BadRequestException ex){
            System.out.println("Błąd podczas dodawania konta lub pracownika: " + ex.getMessage());
            return ;
        }

        
        System.out.println("Pracownik response: " + pracownikResp);
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
}

