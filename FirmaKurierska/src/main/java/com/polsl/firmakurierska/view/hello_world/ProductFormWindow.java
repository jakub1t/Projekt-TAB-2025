package com.polsl.firmakurierska.view.hello_world;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** Okno do wprowadzania jednego produktu */
public class ProductFormWindow {

    public void show() {
        TextField nameField = new TextField();
        TextField weightField = new TextField();
        TextField categoryField = new TextField();
        TextField serialField = new TextField();
        TextField producerField = new TextField();

        VBox nameBox     = createInputCard("Nazwa:", nameField);
        VBox weightBox   = createInputCard("Waga:", weightField);
        VBox categoryBox = createInputCard("Kategoria:", categoryField);
        VBox serialBox   = createInputCard("Nr seryjny:", serialField);
        VBox prodBox     = createInputCard("Producent:", producerField);

        Button saveBtn = new Button("Zapisz produkt");
        saveBtn.setOnAction(e -> {/*
            PackageFormWindow.Product p = new PackageFormWindow.Product(
                nameField.getText(),
                weightField.getText(),
                categoryField.getText(),
                serialField.getText(),
                producerField.getText()
            ); 
            onSave.accept(p);*/
            ((Stage) saveBtn.getScene().getWindow()).close();
        });
        HBox btnBox = new HBox(saveBtn);
        btnBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(12,
            nameBox, weightBox, categoryBox, serialBox, prodBox, btnBox
        );
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f8f8f8;");

        Stage stage = new Stage();
        stage.setTitle("Nowy produkt");
        stage.setScene(new Scene(root, 450, 450));
        stage.show();
    }

    private VBox createInputCard(String labelText, Control inputField) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");
        VBox box = new VBox(5, label, inputField);
        box.setPadding(new Insets(8));
        box.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #dddddd;
            -fx-border-radius: 6;
            -fx-background-radius: 6;
        """);
        return box;
    }
}
