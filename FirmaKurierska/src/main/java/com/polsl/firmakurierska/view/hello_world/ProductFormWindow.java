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
import javafx.stage.Stage;

import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** Okno do wprowadzania jednego produktu */
public class ProductFormWindow {

    private final UIBuilder ui = UIBuilder.getUIBuilder();
    private final UIThemeManager theme = UIThemeManager.getUIThemeManager();
    private final RegexMaster rgx = RegexMaster.getRegexMaster();
    private Stage myStage = null;
    
    TextField nameField = new TextField();
    TextField weightField = new TextField();
    TextField categoryField = new TextField();
    TextField serialField = new TextField();

    public void show(ManagerWindow parentManagerWnd, Button parentRfshBtn) {
        nameField.setPromptText("Nazwa produktu, tylko litery");
        weightField.setPromptText("Waga produktu w kg, cyfry, miejsca dziesiętne po kropce");
        categoryField.setPromptText("Kategoria produktu, tylko litery");
        serialField.setPromptText("Numer seryjny produktu, dowolne znaki");

        VBox nameBox     = createInputCard("Nazwa:", nameField);
        VBox weightBox   = createInputCard("Waga:", weightField);
        VBox categoryBox = createInputCard("Kategoria:", categoryField);
        VBox serialBox   = createInputCard("Nr seryjny:", serialField);

        Button saveBtn = ui.createStylizedButton(theme.getThemeMode(), 160, "Dodaj produkt");
        saveBtn.setOnAction(e -> {
            if (!checkIfDataCorrect(nameField.getText(), weightField.getText(), 
                categoryField.getText(), serialField.getText())) 
            return;
            
            saveBtn.setDisable(true);

            handleButton();
            parentManagerWnd.refreshAllData(parentRfshBtn);

            myStage.close();
        });
        HBox btnBox = new HBox(saveBtn);
        btnBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(12,
            nameBox, weightBox, categoryBox, serialBox, btnBox
        );
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f8f8f8;");

        if (myStage == null) {
            myStage = new Stage();
            myStage.setTitle("Nowy produkt");
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
    private boolean addProduct(String jsonData) {
        RequestController rq = new RequestController("/produkt/add", 1);

        try {
            rq.sendJsonReq(jsonData);
        } catch (BadRequestException bre) {
            System.err.println("addProduct: " + bre.getMessage());
            return false;
        }
        
        return true;
    }

    private void handleButton() {
        String jsonToSend = "";
        String errorString = "Error overrides this string";
        boolean noError = true;

        try {
            double weight = Double.parseDouble(weightField.getText());
            jsonToSend = String.format("{\"nrSeryjny\": \"%s\", \"waga\": %s, \"nazwaProduktu\": \"%s\", \"kategoriaProd\": \"%s\"}",
                serialField.getText(),
                weight,
                nameField.getText(),
                categoryField.getText()
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
            if (addProduct(jsonToSend)) {
                myStage.close();
            } else {
                System.err.println("handleButton: Błąd dodawania produktu");
            }
        } else {
            System.err.println("handleButton: " + errorString);
        }
    }
    
    private boolean checkIfDataCorrect(String nazwa, String waga, String kategoria, String nrSeryjny) {

            if (!rgx.checkStringForNames(nazwa)) {
                ui.showAlertDialog("Błąd", "Niepoprawnie wprowadzona nazwa produktu!", 
                "Nazwa nie może być pusta, nie może być dłuższa niż 24 znaki, musi się składać tylko z liter oraz musi się zaczynać z dużej litery.");
                return false;
            }
            if (!rgx.checkStringForDouble(waga)) {
                ui.showAlertDialog("Błąd", "Niepoprawnie wprowadzona waga!", 
                "Waga nie może być pusta, musi być liczbą dodatnią, przecinek musi być kropką, nie może mieć więcej niż 5 cyfr przed przecinkiem i po przecinku.");
                return false;
            }
            if (!rgx.checkStringForNames(kategoria)) {
                ui.showAlertDialog("Błąd", "Niepoprawnie wprowadzona kategoria produktu!", 
                "Kategoria nie może być pusta, nie może być dłuższa niż 24 znaki, musi się składać tylko z liter oraz musi się zaczynać z dużej litery.");
                return false;
            }
            if (!rgx.checkStringForLettersAndNumbers(nrSeryjny)) {
                ui.showAlertDialog("Błąd", "Niepoprawnie wprowadzony numer seryjny produktu!", 
                "Numer seryjny nie może być pusty, musi się składać wyłącznie z liter i cyfr, nie może przekraczać długości 32 znaków.");
                return false;
            }

        return true;
    }
}
