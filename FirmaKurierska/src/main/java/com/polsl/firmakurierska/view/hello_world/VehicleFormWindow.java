package com.polsl.firmakurierska.view.hello_world;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class VehicleFormWindow {

    /**
     * Otwiera formularz dodawania/edycji pojazdu.
     * Po kliknięciu "Zapisz", wypisuje dane w konsoli i zamyka okno.
     */
    public void show() {
        // Pole na nazwę pojazdu
        TextField nazwaField = new TextField();
        nazwaField.setPromptText("np. Ciężarówka X");

        // Pozostałe pola
        TextField typField       = new TextField();
        TextField markaField     = new TextField();
        TextField modelField     = new TextField();
        TextField pojemnoscField = new TextField();
        TextField numerRejField  = new TextField();

        // Karty wejściowe
        VBox nazwaBox    = createInputCard("Nazwa pojazdu:", nazwaField);
        VBox typBox       = createInputCard("Typ pojazdu:", typField);
        VBox markaBox     = createInputCard("Marka:", markaField);
        VBox modelBox     = createInputCard("Model:", modelField);
        VBox pojemnoscBox = createInputCard("Pojemność:", pojemnoscField);
        VBox numerBox     = createInputCard("Numer rejestracyjny:", numerRejField);

        // Przycisk zapisu
        Button saveBtn = new Button("Zapisz pojazd");
        saveBtn.setOnAction(e -> {
            System.out.println("Dodano/edytowano pojazd:");
            System.out.println("Nazwa: " + nazwaField.getText());
            System.out.println("Typ: " + typField.getText());
            System.out.println("Marka: " + markaField.getText());
            System.out.println("Model: " + modelField.getText());
            System.out.println("Pojemność: " + pojemnoscField.getText());
            System.out.println("Nr rej.: " + numerRejField.getText());
            Stage window = (Stage) saveBtn.getScene().getWindow();
            window.close();
        });
        HBox btnBox = new HBox(saveBtn);
        btnBox.setAlignment(Pos.CENTER);

        // Kontener wszystkich elementów
        VBox container = new VBox(15,
            nazwaBox,
            typBox, markaBox, modelBox, pojemnoscBox, numerBox,
            btnBox
        );
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane(container);
        root.setStyle("-fx-background-color: #f8f8f8;");

        Stage stage = new Stage();
        stage.setTitle("Formularz pojazdu");
        // Zwiększona wysokość, by pomieścić dodatkowe pole
        stage.setScene(new Scene(root, 400, 560));
        stage.show();
    }

    /**
     * Tworzy kartę wejściową z etykietą i polem
     */
    private VBox createInputCard(String labelText, Control inputField) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");
        VBox box = new VBox(5, label, inputField);
        box.setPadding(new Insets(10));
        box.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #dddddd;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 1);
        """);
        return box;
    }
}