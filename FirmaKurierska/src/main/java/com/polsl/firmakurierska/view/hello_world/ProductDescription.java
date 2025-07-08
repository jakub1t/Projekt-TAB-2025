package com.polsl.firmakurierska.view.hello_world;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.polsl.firmakurierska.dto.ProduktDTO;
import com.polsl.firmakurierska.view.UIThemeManager;

public class ProductDescription {
    private UIThemeManager theme = UIThemeManager.getUIThemeManager();


    public void show(ProduktDTO produktData) {

        VBox box = new VBox(8,
            new Label("Nazwa: " + produktData.getNazwaProduktu()),
            new Label("Waga: " + produktData.getWaga()),
            new Label("Kategoria: " + produktData.getKategoriaProd()),
            new Label("Nr seryjny: " + produktData.getNrSeryjny())
        );
        box.setPadding(new Insets(10));
        
        if (theme.getThemeMode()) {
            box.setStyle("""
            -fx-background-color: #dddddd;
            -fx-border-color: #bbbbbb;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
        """);
        } else {
            box.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #dddddd;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 1);
        """);
        }

        BorderPane root = new BorderPane(box);
        BorderPane.setAlignment(box, Pos.CENTER);

        Scene scene = new Scene(root, 400, 160);
        Stage stage = new Stage();
        stage.setTitle("Szczegóły pojazdu");
        stage.setScene(scene);
        stage.show();
    }
}
