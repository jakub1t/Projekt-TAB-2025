package com.polsl.firmakurierska.view.hello_world;

import java.util.IllegalFormatException;

import com.polsl.firmakurierska.controller.RequestController;
import com.polsl.firmakurierska.exception.BadRequestException;
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
            System.err.println("addDostawa: " + bre.getMessage());
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
                System.err.println("handleButton: Błąd dodawania pojazdu");
            }
        } else {
            System.err.println("handleButton: " + errorString);
        }
    }
}