/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.polsl.firmakurierska.view.hello_world;

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
                new AdminPanel().start(stage);
            };
            
            /*
            if(nick.getText().equals("a")) {
                new AdminPanel().start(new Stage());
            }
            
            if(nick.getText().equals("w")) {
                new WorkerAdmin().start(new Stage());
            }
            
            if(nick.getText().equals("k")) {
                new WorkerWindow().start(new Stage());
            }
            */
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
            response = rq.sendReq(reqString);
        } catch (BadRequestException e) {
            System.out.println(e.getMessage());
            return false;
        }

        if (response.equals("NRP")) { isSuccess = false; }
        else { isSuccess = true; }

        return isSuccess;
    }
}
