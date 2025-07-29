package com.polsl.firmakurierska.view.hello_world;

import java.util.IllegalFormatException;

import com.polsl.firmakurierska.controller.RequestController;
import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.view.RegexMaster;
import com.polsl.firmakurierska.view.UIBuilder;
import com.polsl.firmakurierska.view.UIThemeManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class VehicleFormWindow {

    private final UIThemeManager theme = UIThemeManager.getUIThemeManager();
    private final UIBuilder ui = UIBuilder.getUIBuilder();
    private final RegexMaster rgx = RegexMaster.getRegexMaster();

    private TextField typField = null;
    private TextField markaField = null;
    private TextField modelField = null;
    private TextField pojemnoscField = null;
    private TextField numerRejField = null;
    private Stage myStage = null;

    /**
     * Otwiera formularz dodawania/edycji pojazdu.
     * Po kliknięciu "Zapisz", wypisuje dane w konsoli i zamyka okno.
     */
    public void show(ManagerWindow managerWindow, Button rfshBtn) {

        // Pola do wypełnienia
        typField       = new TextField();
        markaField     = new TextField();
        modelField     = new TextField();
        pojemnoscField = new TextField();
        numerRejField  = new TextField();
        
        typField.setPromptText("Typ pojazdu, tylko litery");
        markaField.setPromptText("Marka pojazdu, tylko litery");
        modelField.setPromptText("Model pojazdu, litery + cyfry");
        pojemnoscField.setPromptText("Pojemność pojazdu w litrach, cyfry, miejsca dziesiętne po kropce");
        numerRejField.setPromptText("Numer rejestracyjny pojazdu, litery + cyfry");

        // Karty wejściowe
        VBox typBox       = ui.createFormInputCard(theme.getThemeMode(), "Typ pojazdu:", typField);
        VBox markaBox     = ui.createFormInputCard(theme.getThemeMode(), "Marka:", markaField);
        VBox modelBox     = ui.createFormInputCard(theme.getThemeMode(), "Model:", modelField);
        VBox pojemnoscBox = ui.createFormInputCard(theme.getThemeMode(), "Pojemność:", pojemnoscField);
        VBox numerBox     = ui.createFormInputCard(theme.getThemeMode(), "Numer rejestracyjny:", numerRejField);

        // Przycisk zapisu
        Button saveBtn = ui.createStylizedButton(theme.getThemeMode(), 150, "Zapisz pojazd");
        saveBtn.setOnMouseClicked(e -> {
            if(!checkIfDataCorrect(typField.getText(), 
                markaField.getText(),
                modelField.getText(),
                pojemnoscField.getText(),
                numerRejField.getText()
                )
            ) return;
            
            saveBtn.setDisable(true);

            handleButton();
            managerWindow.refreshAllData(rfshBtn);
        });
        HBox btnBox = new HBox(saveBtn);
        btnBox.setAlignment(Pos.CENTER);

        // Kontener wszystkich elementów
        VBox container = new VBox(15,
            typBox, markaBox, modelBox, pojemnoscBox, numerBox,
            btnBox
        );
        container.setPadding(new Insets(10));
        container.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane(container);
        
        if (theme.getThemeMode()) {
            root.setBackground(ui.unifiedRootBgDark);
        } else {
            root.setBackground(ui.unifiedRootBgLight);
        }

        myStage = new Stage();
        myStage.setTitle("Formularz pojazdu");
        // Zwiększona wysokość, by pomieścić dodatkowe pole
        myStage.setScene(new Scene(root, 400, 560));
        myStage.show();
    }

    /**
     * 
     * @param jsonData - already formatted data
     * @return True when successful / False otherwise
     */
    private boolean addVehicle(String jsonData) {
        RequestController rq = new RequestController("/pojazd", 1);
        String resp = "";

        try {
            resp = rq.sendJsonReq(jsonData);
            if (resp.contains("Dodano pojazd")) { 
                System.out.println("Dodano nowy pojazd");
                return true; 
            }
        } catch (BadRequestException bre) {
            ui.showAlertDialog("Błąd", "Błąd podczas dodawania pojazdu!", bre.getMessage());
            return false;
        }
        
        return false;
    }

    private void handleButton() {
        String jsonToSend = "";
        String errorString = "Error overrides this string";
        boolean noError = true;

        try {
            double capacity = Double.parseDouble(pojemnoscField.getText());
            jsonToSend = String.format("{\"typPojazdu\": \"%s\", \"pojemnosc\": %s, \"marka\": \"%s\", \"model\": \"%s\", \"nrRejestr\": \"%s\"}",
                typField.getText(),
                capacity,
                markaField.getText(),
                modelField.getText(),
                numerRejField.getText()
                );
            //System.out.println(jsonToSend);
        } catch (NullPointerException npe) {
            errorString = npe.getMessage();
            noError = false;
        } catch (NumberFormatException nfe) {
            errorString = nfe.getMessage();
            noError = false;
        } catch (IllegalFormatException ife) {
            errorString = ife.getMessage();
            noError = false;
        }

        if (noError) {
            if (addVehicle(jsonToSend)) {
                myStage.close();
            } else {
                System.err.println("handleButton: Błąd dodawania pojazdu, prawdopodobnie duplikat numeru rejestracyjnego.");
            }
        } else {
            System.err.println("handleButton: " + errorString);
        }
    }

    private boolean checkIfDataCorrect(String typ, String marka, String model, String pojemnosc, String rej) {

            if (!rgx.checkStringForNames(typ)) {
                ui.showAlertDialog("Błąd", "Niepoprawnie wprowadzony typ pojazdu!", 
                "Typ pojazdu nie może być pusty, nie może być dłuższy niż 24 znaki, musi się składać tylko z liter oraz musi się zaczynać z dużej litery.");
                return false;
            }
            if (!rgx.checkStringForNames(marka)) {
                ui.showAlertDialog("Błąd", "Niepoprawnie wprowadzona marka pojazdu!", 
                "Marka pojazdu nie może być pusta, nie może być dłuższa niż 24 znaki, musi się składać tylko z liter oraz musi się zaczynać z dużej litery.");
                return false;
            }
            if (!rgx.checkStringForLettersAndNumbers(model)) {
                ui.showAlertDialog("Błąd", "Niepoprawnie wprowadzony model pojazdu!", 
                "Model pojazdu nie może być pusty, musi się składać wyłącznie z liter i cyfr, nie może przekraczać długości 32 znaków.");
                return false;
            }
            if (!rgx.checkStringForDouble(pojemnosc)) {
                ui.showAlertDialog("Błąd", "Niepoprawnie wprowadzona pojemnosc pojazdu!", 
                "Pojemność pojazdu nie może być pusta, musi być liczbą dodatnią, przecinek musi być kropką, nie może mieć więcej niż 5 cyfr przed przecinkiem i po przecinku.");
                return false;
            }
            if (!rgx.checkStringForLettersAndNumbers(rej)) {
                ui.showAlertDialog("Błąd", "Niepoprawnie wprowadzony numer rejestracyjny pojazdu!", 
                "Numer rejestracyjny nie może być pusty, musi się składać wyłącznie z liter i cyfr, nie może przekraczać długości 32 znaków.");
                return false;
            }

        return true;
    }
}