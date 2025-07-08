package com.polsl.firmakurierska.view.hello_world;

import com.polsl.firmakurierska.view.UIBuilder;
import com.polsl.firmakurierska.view.UIThemeManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VehicleDescription {
    
    private final UIThemeManager theme = UIThemeManager.getUIThemeManager();
    private final UIBuilder ui = UIBuilder.getUIBuilder();

    /**
     * Pokazuje okno ze szczegółami pojazdu:
     * – nazwa pojazdu
     * – typ pojazdu
     * – marka
     * – model
     * – pojemność
     * – numer rejestracyjny
     */
    public void show(String nazwa, String typ, String marka, String model, String pojemnosc, String numerRej) {
        // Karta z nazwą pojazdu
        VBox nazwaBox = ui.createStyledCard(theme.getThemeMode(), "Nazwa pojazdu:", nazwa);
        // Karta z typem pojazdu
        VBox typBox = ui.createStyledCard(theme.getThemeMode(), "Typ pojazdu:", typ);
        // Karta z marką pojazdu
        VBox markaBox = ui.createStyledCard(theme.getThemeMode(), "Marka:", marka);
        // Karta z modelem
        VBox modelBox = ui.createStyledCard(theme.getThemeMode(), "Model:", model);
        // Karta z pojemnością
        VBox pojemnoscBox = ui.createStyledCard(theme.getThemeMode(), "Pojemność:", pojemnosc);
        // Karta z numerem rejestracyjnym
        VBox numerBox = ui.createStyledCard(theme.getThemeMode(), "Numer rejestracyjny:", numerRej);

        HBox carModel = new HBox(markaBox, modelBox);
        HBox carMoreInfo = new HBox(pojemnoscBox, numerBox);

        carModel.setSpacing(10);
        carMoreInfo.setSpacing(10);

        // Wszystkie karty w pionie
        VBox allFields = new VBox(15,
            nazwaBox,
            typBox,
            carModel,
            carMoreInfo
        );
        allFields.setPadding(new Insets(20));
        allFields.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane(allFields);
        BorderPane.setAlignment(allFields, Pos.CENTER);

        if (theme.getThemeMode()) {
            allFields.setStyle("-fx-background-color: #2F2F2F");
        } else {
            allFields.setStyle("-fx-background-color: #F4F4F4");
        }

        Scene scene = new Scene(root, 400, 360);
        Stage stage = new Stage();
        stage.setTitle("Szczegóły pojazdu");
        stage.setScene(scene);
        stage.show();
    }
}
