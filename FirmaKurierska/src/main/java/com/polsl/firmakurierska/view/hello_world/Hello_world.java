/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.polsl.firmakurierska.view.hello_world;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import com.polsl.firmakurierska.controller.RequestController;
import com.polsl.firmakurierska.exception.BadRequestException;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Hello_world extends Application {
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

        MFXButton loguj = new MFXButton("Loguj");
        loguj.setOnAction(e -> {
            System.out.println("Nick: " + nick.getText());
            System.out.println("Hasło: " + haslo.getText());
            if (attemptLogin(nick.getText(), haslo.getText())) {
                int accType = -1;

                accType = findAccType(nick.getText());

                if (accType == 1) new AdminPanel().start(new Stage());
                else if (accType == 2) new WorkerAdmin().start(new Stage());
                else if (accType == 3) new WorkerWindow().start(new Stage());
                else System.out.println("Bad account type...");
            };
        });
        loguj.setPrefWidth(200);    // szerokość przycisku (opcjonalnie)


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
        System.out.println(response);

        return isSuccess;
    }

    private int findAccType(String login) {
        // find konto_id by login z tabeli Konto
        // find stanowisko_id by konto_id z tabeli Pracownik

        String accID = "";
        String pracownikData = "";
        String accType = "";

        // Prepare request for account ID
        RequestController rq = new RequestController("/konto/getid?login=" + login, 1);

        // Get account ID
        try {
            accID = rq.sendPathReq();
        }
        catch (BadRequestException e) {
            System.out.println(e.getMessage());
            return -1;
        }

        // Prepare request for worker data
        rq = new RequestController("/pracownik/" + accID, 1);

        // Get worker data
        try {
            pracownikData = rq.sendPathReq();
        }
        catch (BadRequestException e) {
            System.out.println(e.getMessage());
            return -1;
        }

        try {
            JSONObject pracownikDataJSON = new JSONObject(pracownikData);
            String links = pracownikDataJSON.getString("_links");
            JSONObject linksData = new JSONObject(links);
            String stanowisko = linksData.getString("stanowisko");
            JSONObject stanowiskoData = new JSONObject(stanowisko);
            String href = stanowiskoData.getString("href");

            String[] hrefTokens = href.split("/");
            boolean stupidFlag = false;
            for (String t : hrefTokens) {
                if (stupidFlag == true) {
                    accType = new String(t);
                    break;
                }
                if (t.equals("stanowisko")) stupidFlag = true;
            }
        }
        catch (JSONException jex) {
            System.out.println(jex.toString());
            jex.printStackTrace();
        }


        if (accType.equals("1") || accType.equals("2") || accType.equals("3")) 
            return Integer.parseInt(accType);
        else
            return -1;
    }
}
