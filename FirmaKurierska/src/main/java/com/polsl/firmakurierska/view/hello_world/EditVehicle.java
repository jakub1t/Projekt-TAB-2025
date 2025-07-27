package com.polsl.firmakurierska.view.hello_world;

import java.util.IllegalFormatException;

import com.polsl.firmakurierska.controller.RequestController;
import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.model.Pojazd;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class EditVehicle {

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
    public void show(ManagerWindow managerWindow, Button rfshBtn, Pojazd vehicleData) {

        // Pola do wypełnienia
        typField       = new TextField(vehicleData.getTypPojazdu());
        markaField     = new TextField(vehicleData.getMarka());
        modelField     = new TextField(vehicleData.getModel());
        pojemnoscField = new TextField(Double.toString(vehicleData.getPojemnosc()));
        numerRejField  = new TextField(vehicleData.getNrRejestr());

        typField.setPromptText("Typ pojazdu, tylko litery");
        markaField.setPromptText("Marka pojazdu, tylko litery");
        modelField.setPromptText("Model pojazdu, litery + cyfry");
        pojemnoscField.setPromptText("Pojemność pojazdu w litrach, cyfry, miejsca dziesiętne po kropce");
        numerRejField.setPromptText("Numer rejestracyjny pojazdu, litery + cyfry");

        // Karty wejściowe
        VBox typBox       = createInputCard("Typ pojazdu:", typField);
        VBox markaBox     = createInputCard("Marka:", markaField);
        VBox modelBox     = createInputCard("Model:", modelField);
        VBox pojemnoscBox = createInputCard("Pojemność:", pojemnoscField);
        VBox numerBox     = createInputCard("Numer rejestracyjny:", numerRejField);

        // Przycisk zapisu
        Button saveBtn = new Button("Zapisz pojazd");
        saveBtn.setOnMouseClicked(e -> {
            handleButton(vehicleData.getIdPojazdu());
            managerWindow.refreshAllData(rfshBtn);
        });
        HBox btnBox = new HBox(saveBtn);
        btnBox.setAlignment(Pos.CENTER);

        // Kontener wszystkich elementów
        VBox container = new VBox(15,
            typBox, markaBox, modelBox, pojemnoscBox, numerBox,
            btnBox
        );
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane(container);
        root.setStyle("-fx-background-color: #f8f8f8;");

        myStage = new Stage();
        myStage.setTitle("Formularz pojazdu");
        // Zwiększona wysokość, by pomieścić dodatkowe pole
        myStage.setScene(new Scene(root, 400, 560));
        myStage.show();
    }

    /**
     * Tworzy kartę wejściową z etykietą i polem
     */
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

    /**
     * 
     * @param jsonData - already formatted data
     * @return True when successful / False otherwise
     */
    private boolean editVehicle(Integer pojazdId, String jsonData) {
        RequestController rq = new RequestController("/pojazd/" + pojazdId, 2);

        try {
            rq.sendJsonReq(jsonData);
        } catch (BadRequestException bre) {
            System.err.println("addDostawa: " + bre.getMessage());
            return false;
        }
        
        return true;
    }

    private void handleButton(Integer pojazdId) {
        String jsonToSend = "";
        String errorString = "Error overrides this string";
        boolean noError = true;

        try {
            double capacity = Double.parseDouble(pojemnoscField.getText());
            if (typField.getText().isEmpty() || markaField.getText().isEmpty() 
                || modelField.getText().isEmpty() || numerRejField.getText().isEmpty()
                || capacity < 0) {
                    errorString = "Fields should contain correct values!!!";
                    noError = false;
                }

            jsonToSend = String.format("{\"typPojazdu\": \"%s\", \"pojemnosc\": %s, \"marka\": \"%s\", \"model\": \"%s\", \"nrRejestr\": \"%s\"}",
                typField.getText(),
                capacity,
                markaField.getText(),
                modelField.getText(),
                numerRejField.getText()
                );
        
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
            if (editVehicle(pojazdId, jsonToSend)) {
                myStage.close();
            } else {
                System.err.println("handleButton: Błąd dodawania pojazdu");
            }
        } else {
            System.err.println("handleButton: " + errorString);
        }
    }
}