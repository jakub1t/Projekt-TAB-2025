/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.polsl.firmakurierska.view.hello_world;

import com.polsl.firmakurierska.controller.RequestController;
import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.view.UIBuilder;

import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Hello_world extends Application {
    private final UIBuilder ui = UIBuilder.getUIBuilder();

    @Override
    public void start(Stage stage) {

        Image firmaLogo = new Image(getClass().getResource("Placeholder_logo_v1.png").toExternalForm(), 300, 150, true, false);
        ImageView logo = new ImageView(firmaLogo);

        MFXTextField nick = new MFXTextField();
        nick.setPromptText("Nick");
        nick.setPrefWidth(300);     // szerokość pola nick
        nick.setStyle("-fx-border-color:rgb(95, 211, 141);");
        
        MFXPasswordField haslo = new MFXPasswordField();
        haslo.setPromptText("Hasło");
        haslo.setPrefWidth(300);    // szerokość pola hasła
        haslo.setStyle("-fx-border-color: rgb(95, 211, 141)");

        Button loguj = ui.createStylizedButton(false, 200, "Zaloguj");
        loguj.setOnAction(e -> {
            if (attemptLogin(nick.getText(), haslo.getText())) {
                int accType = -1;
                int accId = -1;
                accId = findAccId(nick.getText());
                accType = findAccType(accId);

                loguj.setText("Zalogowano!");

                if (accType == 1) new AdminPanel().open(accId);
                else if (accType == 2) new ManagerWindow().open(accId);
                else if (accType == 3) new DriverWindow().open(accId);
                else System.out.println("Bad account type...");
            } else {
                loguj.setText("Niepoprawne dane");
            }
        });

        VBox layout = new VBox(20, logo, nick, haslo, loguj);
        layout.setStyle("-fx-padding: 4; -fx-alignment: center;");

        stage.setScene(new Scene(layout, 350, 250));
        stage.setTitle("Firma kurierska");

        stage.show();
    }

    public void main(String[] args) {
        launch(args);
    }

    private boolean attemptLogin(String login, String pass) {
        RequestController rq = new RequestController("/konto/login", 1);
        boolean isSuccess = false;
        String reqString = "{\"login\": \"" + login + "\",\"haslo\": \"" + pass + "\"}";
        String response = ""; 
        try {
            response = rq.sendJsonReq(reqString);
        } catch (BadRequestException e) {
            System.out.println(e.getMessage());
            return false;
        }

        if (response.equals("Zalogowano pomyślnie")) { isSuccess = true; }
        else { isSuccess = false; } 
        
        return isSuccess;
    }

    private int findAccId(String login) {
        // find konto_id by login z tabeli Konto
        String accID = "";
        int actualId = 0;
        // Prepare request for account ID
        RequestController rq = new RequestController("/konto/getid?login=" + login, 1);

        // Get account ID
        try {
            accID = rq.sendPathReq();
            actualId = Integer.parseInt(accID);
        }
        catch (BadRequestException e) {
            System.out.println(e.getMessage());
            return -1;
        }
        catch (NumberFormatException ex) {
            System.out.println(ex.getMessage());
            return -1;
        }
        return actualId;
    }

    private int findAccType(int accID) {        
        // find stanowisko_id by konto_id z tabeli Pracownik
        String pracownikData = "";
        String accType = "";

        // Prepare request for worker data
        RequestController rq = new RequestController("/pracownik/" + Integer.toString(accID), 1);
        // Get worker data
        try {
            pracownikData = rq.sendPathReq();
        }
        catch (BadRequestException e) {
            System.out.println(e.getMessage());
            return -1;
        }

        accType = rq.getStanowisko(pracownikData);

        if (accType.equals("1") || accType.equals("2") || accType.equals("3")) 
            return Integer.parseInt(accType);
        else
            return -1;
    }
}
