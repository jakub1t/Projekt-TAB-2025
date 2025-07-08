/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.polsl.firmakurierska.view.hello_world;

import java.util.List;

import com.polsl.firmakurierska.view.UIBuilder;
import com.polsl.firmakurierska.view.UIThemeManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AccountDescription {

    private final UIBuilder ui = UIBuilder.getUIBuilder();
    private final UIThemeManager theme = UIThemeManager.getUIThemeManager();

    public void show(List<String> data) {
        // Tworzymy osobne boksy dla każdego pola
        VBox imieBox = ui.createStyledCard(theme.getThemeMode(), "Imię:", data.get(0));
        VBox nazwiskoBox = ui.createStyledCard(theme.getThemeMode(), "Nazwisko:", data.get(1));
        VBox peselBox = ui.createStyledCard(theme.getThemeMode(), "PESEL:", data.get(2));
        VBox stanowiskoBox = ui.createStyledCard(theme.getThemeMode(), "Stanowisko:", data.get(3));
        String kategoriePrawaJazdy = "";

        for (int i = 4; i < data.size(); i++) {
            kategoriePrawaJazdy += data.get(i) + " ";
        }

        VBox prawoJazdyBox = ui.createStyledCard(theme.getThemeMode(), "Prawo jazdy:", kategoriePrawaJazdy);

        VBox allFields = new VBox(15, imieBox, nazwiskoBox, peselBox, stanowiskoBox, prawoJazdyBox);
        allFields.setPadding(new Insets(20));
        allFields.setAlignment(Pos.CENTER);

        // Tło główne
        BorderPane root = new BorderPane(allFields);
        BorderPane.setAlignment(allFields, Pos.CENTER);

        if (theme.getThemeMode()) {
            allFields.setStyle("-fx-background-color: #2F2F2F");
        } else {
            allFields.setStyle("-fx-background-color: #F4F4F4");
        }

        Scene scene = new Scene(root, 400, 450);

        Stage stage = new Stage();
        stage.setTitle("Szczegóły konta");
        stage.setScene(scene);
        stage.show();
    }
}
