package com.polsl.firmakurierska.view.hello_world;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class DeliveryFormWindow {

    /**
     * Otwiera formularz dodawania nowej dostawy.
     * @param availablePackageIds lista dostępnych ID paczek
     * @param availableVehicleNames lista dostępnych nazw pojazdów
     * @param availableEmployees lista dostępnych imion i nazwisk pracowników
     */
    public void show(
            List<String> availablePackageIds,
            List<String> availableVehicleNames,
            List<String> availableEmployees
    ) {
        // Pole na nazwę dostawy
        TextField nameField = new TextField();
        nameField.setPromptText("np. Dostawa 123");
        VBox nameBox = createInputCard("Nazwa dostawy:", nameField);

        // Data
        DatePicker datePicker = new DatePicker();
        VBox dateBox = createInputCard("Data:", datePicker);

        // Godzina
        TextField timeField = new TextField();
        timeField.setPromptText("np. 14:30");
        VBox timeBox = createInputCard("Godzina:", timeField);

        // Punkty A i B
        TextField pointAField = new TextField();
        TextField pointBField = new TextField();
        VBox pointABox = createInputCard("Punkt A:", pointAField);
        VBox pointBBox = createInputCard("Punkt B:", pointBField);

        // Checkboxy paczek (wielokrotny wybór)
        Label pkgLabel = new Label("Wybierz paczki do dostawy:");
        pkgLabel.setStyle("-fx-font-weight: bold;");
        VBox pkgContainer = new VBox(5);
        for (String pid : availablePackageIds) {
            CheckBox cb = new CheckBox(pid);
            cb.setPrefHeight(25);
            pkgContainer.getChildren().add(cb);
        }
        ScrollPane pkgScroll = new ScrollPane(pkgContainer);
        pkgScroll.setFitToWidth(true);
        pkgScroll.setPrefHeight(120);

        // RadioButtons pojazdów (pojedynczy wybór)
        Label vehLabel = new Label("Wybierz pojazd:");
        vehLabel.setStyle("-fx-font-weight: bold;");
        ToggleGroup vehGroup = new ToggleGroup();
        VBox vehContainer = new VBox(5);
        for (String v : availableVehicleNames) {
            RadioButton rb = new RadioButton(v);
            rb.setToggleGroup(vehGroup);
            rb.setPrefHeight(25);
            vehContainer.getChildren().add(rb);
        }
        ScrollPane vehScroll = new ScrollPane(vehContainer);
        vehScroll.setFitToWidth(true);
        vehScroll.setPrefHeight(120);

        // RadioButtons pracowników (pojedynczy wybór)
        Label empLabel = new Label("Wybierz pracownika:");
        empLabel.setStyle("-fx-font-weight: bold;");
        ToggleGroup empGroup = new ToggleGroup();
        VBox empContainer = new VBox(5);
        for (String emp : availableEmployees) {
            RadioButton rb = new RadioButton(emp);
            rb.setToggleGroup(empGroup);
            rb.setPrefHeight(25);
            empContainer.getChildren().add(rb);
        }
        ScrollPane empScroll = new ScrollPane(empContainer);
        empScroll.setFitToWidth(true);
        empScroll.setPrefHeight(120);

        // Przycisk zapisu
        Button saveBtn = new Button("Zapisz dostawę");
        saveBtn.setOnAction(e -> {
            // Zbieramy dane
            String name = nameField.getText();
            String date = datePicker.getValue() != null ? datePicker.getValue().toString() : "";
            String time = timeField.getText();

            List<String> selectedPkgs = new ArrayList<>();
            pkgContainer.getChildren().forEach(node -> {
                if (node instanceof CheckBox cb && cb.isSelected()) selectedPkgs.add(cb.getText());
            });
            RadioButton selectedVeh = (RadioButton) vehGroup.getSelectedToggle();
            String veh = selectedVeh != null ? selectedVeh.getText() : "";
            RadioButton selectedEmp = (RadioButton) empGroup.getSelectedToggle();
            String emp = selectedEmp != null ? selectedEmp.getText() : "";

            System.out.println("Nowa dostawa:");
            System.out.println("Nazwa: " + name);
            System.out.println("Data: " + date);
            System.out.println("Godzina: " + time);
            System.out.println("Punkt A: " + pointAField.getText());
            System.out.println("Punkt B: " + pointBField.getText());
            System.out.println("Paczki: " + selectedPkgs);
            System.out.println("Pojazd: " + veh);
            System.out.println("Pracownik: " + emp);

            ((Stage) saveBtn.getScene().getWindow()).close();
        });
        HBox btnBox = new HBox(saveBtn);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(10, 0, 0, 0));

        // Główny kontener
        VBox container = new VBox(12,
            nameBox,
            dateBox,
            timeBox,
            pointABox, pointBBox,
            pkgLabel, pkgScroll,
            vehLabel, vehScroll,
            empLabel, empScroll,
            btnBox
        );
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.TOP_CENTER);

        BorderPane root = new BorderPane(container);
        root.setStyle("-fx-background-color: #f8f8f8;");

        Stage stage = new Stage();
        stage.setTitle("Formularz dostawy");
        stage.setScene(new Scene(root, 400, 950));
        stage.show();
    }

    private VBox createInputCard(String labelText, Control inputField) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");
        VBox box = new VBox(5, label, inputField);
        box.setPadding(new Insets(10));
        box.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #dddddd;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 1);"
        );
        return box;
    }
}
