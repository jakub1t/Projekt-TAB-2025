/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.polsl.firmakurierska.view.hello_world;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Hello_world extends Application {
    @Override
    public void start(Stage stage) {
        MFXTextField nick = new MFXTextField();
        nick.setPromptText("Nick");
        nick.setPrefWidth(300);     // szerokość pola nick



        MFXPasswordField haslo = new MFXPasswordField();
        haslo.setPromptText("Hasło");
        haslo.setPrefWidth(300);    // szerokość pola hasła

        MFXButton loguj = new MFXButton("Loguj");
        loguj.setOnAction(e -> {
            System.out.println("Nick: " + nick.getText());
            System.out.println("Hasło: " + haslo.getText());
            
            if(nick.getText().equals("a")) {
                new AdminPanel().start(new Stage());
            }
            
            if(nick.getText().equals("w")) {
                new WorkerAdmin().start(new Stage());
            }

            
            
        });
        loguj.setPrefWidth(200);    // szerokość przycisku (opcjonalnie)


        VBox layout = new VBox(20, nick, haslo, loguj);
        layout.setStyle("-fx-padding: 4; -fx-alignment: center;");

        stage.setScene(new Scene(layout, 350, 250));
        stage.setTitle("Firma kurierska");

        stage.show();
    }

    public void main(String[] args) {
        launch(args);
    }

    //private boolean httpLogin() {

    //}
}
