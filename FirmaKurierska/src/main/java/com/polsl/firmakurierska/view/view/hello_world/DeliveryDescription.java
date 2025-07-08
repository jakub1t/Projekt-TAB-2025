package com.polsl.firmakurierska.view.hello_world;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import com.polsl.firmakurierska.controller.RequestController;
import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.view.UIBuilder;
import com.polsl.firmakurierska.view.UIThemeManager;

public class DeliveryDescription {

    private final UIThemeManager theme = UIThemeManager.getUIThemeManager();
    private final UIBuilder ui = UIBuilder.getUIBuilder();

    private int idDostawy = 0;
    private String pracImie = "";
    private String pracNazw = "";
    private String dataStart = "0000-00-00";
    private String deadline = "9999-99-99";
    private String punktA = "";
    private String punktB = "";
    private String vehicleModel = ""; // "Rolvo" lmao
    private List<Integer> przypisanePaczki = new ArrayList<>();

    /**
     * Pokazuje okno ze szczegółami dostawy:
     * – numer dostawy
     * – imię pracownika
     * – nazwisko pracownika
     * – nazwa auta
     * – data
     * – Termin
     * – punkt A
     * – punkt B
     * – lista dołączonych paczek (przewijana)
     */
    public void show() {

        // Karta z nazwą dostawy
        VBox nazwaBox = ui.createStyledCard(theme.getThemeMode(), "Numer dostawy:", Integer.toString(idDostawy));
        // Karta z imieniem pracownika
        VBox imieBox = ui.createStyledCard(theme.getThemeMode(), "Imię pracownika:", pracImie);
        // Karta z nazwiskiem pracownika
        VBox nazwiskoBox = ui.createStyledCard(theme.getThemeMode(), "Nazwisko pracownika:", pracNazw);
        // Karta z nazwą auta
        VBox autoBox = ui.createStyledCard(theme.getThemeMode(), "Nazwa auta:", vehicleModel);
        // Karta z datą
        VBox dateBox = ui.createStyledCard(theme.getThemeMode(), "Data wyruszenia:", dataStart);
        // Karta z godziną
        VBox timeBox = ui.createStyledCard(theme.getThemeMode(), "Termin:", deadline);
        // Karta z punktem A
        VBox pointABox = ui.createStyledCard(theme.getThemeMode(), "Punkt A:", punktA);
        // Karta z punktem B
        VBox pointBBox = ui.createStyledCard(theme.getThemeMode(), "Punkt B:", punktB);

        HBox timeStuff = new HBox(dateBox, timeBox);
        HBox locationStuff = new HBox(pointABox, pointBBox);

        timeStuff.setSpacing(10);
        locationStuff.setSpacing(10);

        // Lista paczek w scrollu
        Label packagesLabel = new Label("Paczki w dostawie:");
        packagesLabel.setStyle("-fx-font-weight: bold;");
        VBox packagesList = new VBox(5);
        packagesList.setPadding(new Insets(5));
        if (przypisanePaczki.isEmpty() == false) {
            for (Integer box : przypisanePaczki) {
                String pid = Integer.toString(box);
                packagesList.getChildren().add(new Label(pid));
            }
        }
        ScrollPane packagesScroll = new ScrollPane(packagesList);
        packagesScroll.setFitToWidth(true);
        packagesScroll.setPrefHeight(120);

        // Zbiór wszystkich pól
        VBox allFields = new VBox(15,
            nazwaBox,
            imieBox,
            nazwiskoBox,
            autoBox,
            timeStuff,
            locationStuff,
            packagesLabel,
            packagesScroll
        );
        allFields.setPadding(new Insets(20));
        allFields.setAlignment(Pos.CENTER);

        ScrollPane mainScroll = new ScrollPane(allFields);
        mainScroll.setFitToWidth(true);

        VBox mainColumn = new VBox(allFields, mainScroll);

        BorderPane root = new BorderPane(mainColumn);
        BorderPane.setAlignment(mainColumn, Pos.CENTER);

        if (theme.getThemeMode()) {
            allFields.setStyle("-fx-background-color: #2F2F2F");
            packagesLabel.setTextFill(Color.web("#F4F4F4"));
        } else {
            allFields.setStyle("-fx-background-color: #F4F4F4");
        }

        // Dostosowana wysokość sceny do nowej zawartości
        Scene scene = new Scene(root, 400, 500);
        Stage stage = new Stage();
        stage.setTitle("Szczegóły dostawy");
        stage.setScene(scene);
        stage.show();
    }

    /*
     * Tworzy prostą kartę z etykietą i wartością
     *
    private VBox createCard(String labelText, String valueText) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");
        Label value = new Label(valueText);

        VBox box = new VBox(5, label, value);
        box.setPadding(new Insets(10));
        box.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #dddddd;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 1);"
        );
        return box;
    }*/

    public void open(Integer delivID, String assignedUserName, String assignedUserSurname) {
        this.pracImie = assignedUserName;
        this.pracNazw = assignedUserSurname;
        fetchDelivData(delivID);
        this.show();
    }

    private boolean fetchDelivData(Integer dID) {
        RequestController rq = new RequestController("/dostawa/" + Integer.toString(dID), 0);
        String response = "";
        Integer vehId = null;
        JSONObject jason = null;
        JSONArray packArray = null;
        
        try {
            response = rq.sendPathReq();    
        } catch (BadRequestException e) {
            System.out.println(e.getMessage());
            return false;
        } 
        
        try {
            jason = new JSONObject(response);
            this.idDostawy = dID;
            
            this.dataStart = jason.getString("dataWyruszenia");
            this.deadline = jason.getString("termin");
            this.punktA = jason.getString("punktA");
            this.punktB = jason.getString("punktB");
            packArray = jason.getJSONArray("paczki");
            vehId = jason.getInt("idPojazdu");
        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
            // return false;
        }

        if (packArray != null) {
            try {
                int totalPacks = packArray.length();
                for (int i = 0; i < totalPacks; ++i) {
                    this.przypisanePaczki.add(packArray.getInt(i));
                }
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        }

        if (vehId != null) {
            try {
                rq = new RequestController("/pojazd/" + Integer.toString(vehId), 0);
                response = rq.sendPathReq();
                jason = new JSONObject(response);

                this.vehicleModel = jason.getString("marka") + " " + jason.getString("model");

            } catch (BadRequestException e) {
                System.out.println("DeliveryDescription - Getting Vehicle info Error: " + e.getMessage());
                return false;
            }
            catch (JSONException ex) {
                System.out.println("DeliveryDescription - Getting Vehicle info Error: " + ex.getMessage());
                return false;
            }
        }

        return true;
    }
}
