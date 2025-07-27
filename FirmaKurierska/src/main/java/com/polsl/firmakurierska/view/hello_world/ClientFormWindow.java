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
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClientFormWindow {
    
    private final UIBuilder ui = UIBuilder.getUIBuilder();
    private final UIThemeManager theme = UIThemeManager.getUIThemeManager();
    private final RegexMaster rgx = RegexMaster.getRegexMaster();
    private Stage myStage = null;
    
    TextField nameField = new TextField();
    TextField surnameField = new TextField();

    public void show(ManagerWindow parentManagerWnd, Button parentRfshBtn) {
        nameField.setPromptText("Imię klienta, tylko litery");
        surnameField.setPromptText("Nazwisko klienta, tylko litery");

        VBox nameBox     = createInputCard("Imię:", nameField);
        VBox surnameBox   = createInputCard("Nazwisko:", surnameField);

        Button saveBtn = ui.createStylizedButton(theme.getThemeMode(), 160, "Dodaj klienta");
        saveBtn.setOnAction(e -> {
            if (!checkIfDataCorrect(nameField.getText(), surnameField.getText())) return;
            
            saveBtn.setDisable(true);

            handleButton();
            parentManagerWnd.refreshAllData(parentRfshBtn);

            myStage.close();
        });
        HBox btnBox = new HBox(saveBtn);
        btnBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(12,
            nameBox, surnameBox, btnBox
        );
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f8f8f8;");

        if (myStage == null) {
            myStage = new Stage();
            myStage.setTitle("Nowy klient");
            myStage.setScene(new Scene(root, 450, 450));
            myStage.show();
        }
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
    private boolean addKlient(String jsonData) {
        RequestController rq = new RequestController("/klient", 1);

        try {
            rq.sendJsonReq(jsonData);
        } catch (BadRequestException bre) {
            System.err.println("addKlient: " + bre.getMessage());
            return false;
        }
        
        return true;
    }

    private void handleButton() {
        String jsonToSend = "";
        String errorString = "Error overrides this string";
        boolean noError = true;

        try {
            jsonToSend = String.format("{\"imieK\": \"%s\", \"nazwiskoK\": \"%s\"}",
                nameField.getText(),
                surnameField.getText()
                );
        } catch (NullPointerException npe) {
            errorString = npe.getMessage();
            noError = false;
        } catch (IllegalFormatException ife) {
            errorString = ife.getMessage();
            noError = false;
        }

        if (noError) {
            if (addKlient(jsonToSend)) {
                myStage.close();
            } else {
                System.err.println("handleButton: Błąd dodawania klienta");
            }
        } else {
            System.err.println("handleButton: " + errorString);
        }
    }

    private boolean checkIfDataCorrect(String imie, String nazwisko) {

            if (!rgx.checkStringForNames(imie)) {
                ui.showAlertDialog("Błąd", "Niepoprawnie wprowadzone imię!", 
                "Imię nie może być puste, nie może być dłuższe niż 24 znaki, musi się składać tylko z liter oraz musi się zaczynać z dużej litery.");
                return false;
            }
            if (!rgx.checkStringForNames(nazwisko)) {
                ui.showAlertDialog("Błąd", "Niepoprawnie wprowadzone nazwisko!", 
                "Nazwisko nie może być puste, nie może być dłuższe niż 24 znaki, musi się składać tylko z liter oraz musi się zaczynać z dużej litery.");
                return false;
            }

        return true;
    }
}
