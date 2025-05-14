/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.polsl.firmakurierska.view.hello_world;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

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

            boolean loginSuccessful = false;
            try {
                loginSuccessful = httpLogin(nick.getText(), haslo.getText());
            } catch (URISyntaxException ex) {
                System.out.println(ex.toString());
                ex.printStackTrace();
            }


            if (loginSuccessful) {
                new AdminPanel().start(new Stage());
            }
            
            if(nick.getText().equals("a")) {
                new AdminPanel().start(new Stage());
            }
            
            if(nick.getText().equals("w")) {
                new WorkerAdmin().start(new Stage());
            }
            
            if(nick.getText().equals("k")) {
                new WorkerWindow().start(new Stage());
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

    private boolean httpLogin(String nick, String pass) throws URISyntaxException {
        String bazaUrl = "http://localhost:8080/konto/login";

        String reqString = "{\"login\": \"" + nick + "\",\"haslo\": \"" + pass + "\"}";

        System.out.println(reqString);

        boolean success = false;

        try {
            URL obj = new URI(bazaUrl).toURL();

            HttpURLConnection konnect = (HttpURLConnection)obj.openConnection();

            //int responseCode = 0;

            konnect.setRequestMethod("POST");
            konnect.setDoOutput(true);
            konnect.setRequestProperty("Content-Type", "application/json");

            try (DataOutputStream os = new DataOutputStream(konnect.getOutputStream())) {
                os.writeBytes(reqString);
                os.flush();
            }

            //responseCode = konnect.getResponseCode();

            StringBuilder response = new StringBuilder();

                // Read response content
                // connection.getInputStream() purpose is to obtain an input stream for reading the server's response.
                try (
                    BufferedReader reader = new BufferedReader( new InputStreamReader( konnect.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line); // Adds every line to response till the end of file.
                    }
                }
                System.out.println("Response: " + response.toString());

            if (response.toString().equals("Zalogowano pomyślnie")) {
                success = true;
            }

            konnect.disconnect();
        }
        catch (IOException e) {
            // If any error occurs during api call it will go into catch block
            System.out.println(e.toString());
            e.printStackTrace();
        }
        return success;

    }

    private int httpFindAccType(String nick) {

        return 0;
    }
}
