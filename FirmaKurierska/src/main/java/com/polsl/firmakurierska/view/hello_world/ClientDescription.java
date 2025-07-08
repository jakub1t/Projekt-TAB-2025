package com.polsl.firmakurierska.view.hello_world;

import com.polsl.firmakurierska.model.Klient;
import com.polsl.firmakurierska.view.UIThemeManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClientDescription {
    private UIThemeManager theme = UIThemeManager.getUIThemeManager();


    public void show(Klient klient) {

        VBox box = new VBox(8,
            new Label("Imie: " + klient.getImieK()),
            new Label("Nazwisko: " + klient.getNazwiskoK())
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
        stage.setTitle("Szczegóły klienta");
        stage.setScene(scene);
        stage.show();
    }
}
