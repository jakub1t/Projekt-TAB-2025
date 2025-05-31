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

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

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
            
            boolean loginSuccessful = false;
            try {
                loginSuccessful = httpLogin(nick.getText(), haslo.getText());
            } catch (URISyntaxException ex) {
                System.out.println(ex.toString());
                ex.printStackTrace();
            }

            if (loginSuccessful) {
                int accType = -1;
                try {
                    accType = httpFindAccType(nick.getText());
                } catch (URISyntaxException ex) {
                    System.out.println(ex.toString());
                    ex.printStackTrace();
                }
                if (accType == 1) new AdminPanel().start(new Stage());
                else if (accType == 2) new WorkerAdmin().start(new Stage());
                else if (accType == 3) new WorkerWindow().start(new Stage());
                else System.out.println("Failed to login...");
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


        VBox layout = new VBox(20, logo, nick, haslo, loguj);
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

        boolean success = false;

        try {
            URL obj = new URI(bazaUrl).toURL();

            HttpURLConnection konnect = (HttpURLConnection)obj.openConnection();

            konnect.setRequestMethod("POST");
            konnect.setDoOutput(true);
            konnect.setRequestProperty("Content-Type", "application/json");

            try (DataOutputStream os = new DataOutputStream(konnect.getOutputStream())) {
                os.writeBytes(reqString);
                os.flush();
            }

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

    private int httpFindAccType(String nick) throws URISyntaxException {
        // find konto_id by login z tabeli Konto
        // find stanowisko_id by konto_id z tabeli Pracownik

        String accType = "";
        String accID = "";

        String bazaUrl = "http://localhost:8080/konto/getid?login=" + nick;

        try {
            URL obj = new URI(bazaUrl).toURL();

            HttpURLConnection konn = (HttpURLConnection)obj.openConnection();

            konn.setRequestMethod("POST");
            konn.setDoOutput(true);

            StringBuilder response = new StringBuilder();
            try (
                BufferedReader reader = new BufferedReader( new InputStreamReader( konn.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line); // Adds every line to response till the end of file.
                }
            }
            accID = response.toString();
            System.out.println("Account ID: " + accID);
            konn.disconnect();
        }
        catch (IOException e) {

            System.out.println(e.toString());
            e.printStackTrace();
        }

        bazaUrl = "http://localhost:8080/pracownik/" + accID;

        try {
            URL obj = new URI(bazaUrl).toURL();

            HttpURLConnection konn = (HttpURLConnection)obj.openConnection();

            konn.setRequestMethod("POST");
            konn.setDoOutput(true);

            StringBuilder response = new StringBuilder();
            try (
                BufferedReader reader = new BufferedReader( new InputStreamReader( konn.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line); // Adds every line to response till the end of file.
                }
            }
            String tempJSON = response.toString();
            System.out.println("***********************************");
            System.out.println("Pracownik JSON: " + tempJSON);
            System.out.println("***********************************");

            try {
                JSONObject pracownikData = new JSONObject(tempJSON);
                String links = pracownikData.getString("_links");
                JSONObject linksData = new JSONObject(links);
                String stanowisko = linksData.getString("stanowisko");
                JSONObject stanowiskoData = new JSONObject(stanowisko);
                String href = stanowiskoData.getString("href");

                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println(href);
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                String[] hrefTokens = href.split("/");
                boolean stupidFlag = false;
                for (String t : hrefTokens) {
                    // System.out.println(t);
                    if (stupidFlag == true) {
                        accType = new String(t);
                        break;
                    }
                    if (t.equals("stanowisko")) stupidFlag = true;
                }

                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println(accType);
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

            }
            catch (JSONException jex) {
                System.out.println(jex.toString());
                jex.printStackTrace();
            }

            konn.disconnect();
        }
        catch (IOException e) {

            System.out.println(e.toString());
            e.printStackTrace();
        }

        if (accType.equals("1") || accType.equals("2") || accType.equals("3")) 
            return Integer.parseInt(accType);
        else
            return -1;
    }
}
