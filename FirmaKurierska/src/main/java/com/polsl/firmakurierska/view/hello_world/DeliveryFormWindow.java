package com.polsl.firmakurierska.view.hello_world;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polsl.firmakurierska.controller.RequestController;
import com.polsl.firmakurierska.dto.PaczkaDTO;
import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.model.Pojazd;
import com.polsl.firmakurierska.model.Pracownik;

public class DeliveryFormWindow {

    private List<PaczkaDTO> availablePackages = null;
    private List<Pojazd> availableVehicles = null;
    private List<Pracownik> availableDrivers = null;

    private DatePicker startDatePicker = null;
    private DatePicker endDatePicker = null;
    private TextField pointAField = null;
    private TextField pointBField = null;

    private Stage myStage = null;
    private ManagerWindow myManager = null;
    private Button myRfsh = null;

    private Integer selectedVehicleId = 1;
    private Integer selectedDriverId = 1;
    private List<Integer> selectedPackagesIds = new ArrayList<>();
    
    /**
     * Otwiera formularz dodawania nowej dostawy.
     */
    public void show(ManagerWindow managerWindow, Button rfshBtn, List<Pojazd> pojazdy, List<PaczkaDTO> paczki) {

        boolean noDrivers = false;
        myStage = new Stage();
        myManager = managerWindow;
        myRfsh = rfshBtn;
        availableVehicles = pojazdy;
        availablePackages = paczki;
        availableDrivers = getDrivers();
        if (availableDrivers.isEmpty()){
            System.out.println("Nie ma dostępnych kierowców!!!");
            noDrivers = true;
        }

        // Data startu
        startDatePicker = new DatePicker();
        VBox startDateBox = createInputCard("Data wyjazdu:", startDatePicker);

        // Termin
        endDatePicker = new DatePicker();
        endDatePicker.setPromptText("Przewidywana data zakończenia dostawy");
        VBox endDateBox = createInputCard("Termin:", endDatePicker);

        HBox datesRow = new HBox(startDateBox, endDateBox);
        datesRow.setAlignment(Pos.CENTER);

        // Punkty A i B
        pointAField = new TextField();
        pointBField = new TextField();
        VBox pointABox = createInputCard("Punkt A:", pointAField);
        VBox pointBBox = createInputCard("Punkt B:", pointBField);

        HBox pointsRow = new HBox(pointABox, pointBBox);
        pointsRow.setAlignment(Pos.CENTER);

        // Checkboxy paczek (wielokrotny wybór)
        Label pkgLabel = new Label("Wybierz paczki do dostawy:");
        pkgLabel.setStyle("-fx-font-weight: bold;");
        VBox pkgContainer = new VBox(5);
        pkgContainer.setStyle("-fx-background-color: white;");

        List<PaczkaDTO> freePacks = new ArrayList<>();
        for (PaczkaDTO pk : availablePackages) {
            if (pk.getDostawaId() == null) {
                freePacks.add(pk);
            }
        }
        availablePackages = freePacks;
        
        if (availablePackages.size() == 0) {
            Label emptyPkgAlert = new Label("Wszystkie paczki mają określoną dostawę");
            pkgContainer.getChildren().add(emptyPkgAlert);
        } else {
            for (PaczkaDTO pk : availablePackages) {    
                CheckBox cb = new CheckBox(pk.getIdPaczki().toString() + "| Waga: " + pk.getWagaPaczki().toString());
                cb.setPrefHeight(25);
                cb.setOnMouseClicked(e -> {
                    addOrRemovePackageId(cb.isSelected(), pk.getIdPaczki());
                });

                pkgContainer.getChildren().add(cb);
            }
        } 

        ScrollPane pkgScroll = new ScrollPane(pkgContainer);
        pkgScroll.setFitToWidth(true);
        pkgScroll.setPrefHeight(120);

        // RadioButtons pojazdów (pojedynczy wybór)
        Label vehLabel = new Label("Wybierz pojazd:");
        vehLabel.setStyle("-fx-font-weight: bold;");
        ToggleGroup vehGroup = new ToggleGroup();
        VBox vehContainer = new VBox(5);
        vehContainer.setStyle("-fx-background-color: white;");

        for (Pojazd v : availableVehicles) {
            String vLabel = v.getIdPojazdu().toString() + ' ' + v.getMarka();
            RadioButton rb = new RadioButton(vLabel);
            rb.setToggleGroup(vehGroup);
            rb.setPrefHeight(25);
            rb.setOnMouseClicked(e -> {
                this.selectedVehicleId = v.getIdPojazdu();
            });
            vehContainer.getChildren().add(rb);
        }
        ScrollPane vehScroll = new ScrollPane(vehContainer);
        vehScroll.setFitToWidth(true);
        vehScroll.setPrefHeight(120);

        // RadioButtons pracowników (pojedynczy wybór)
        Label empLabel = new Label("Wybierz kierowcę:");
        empLabel.setStyle("-fx-font-weight: bold;");
        ToggleGroup empGroup = new ToggleGroup();
        VBox empContainer = new VBox(5);
        empContainer.setStyle("-fx-background-color: white;");

        for (Pracownik dr : availableDrivers) {
            String drLabel = dr.getIdOsoby().toString() + ' ' + dr.getNazwisko();
            RadioButton rb = new RadioButton(drLabel);
            rb.setToggleGroup(empGroup);
            rb.setPrefHeight(25);
            rb.setOnMouseClicked(e -> {
                this.selectedDriverId = dr.getIdOsoby();
            });
            empContainer.getChildren().add(rb);
        }
        ScrollPane empScroll = new ScrollPane(empContainer);
        empScroll.setFitToWidth(true);
        empScroll.setPrefHeight(60);

        // Przycisk zapisu
        Button saveBtn = new Button("Zapisz dostawę");
        saveBtn.setOnMouseClicked(e -> {
            handleButton();
        });
        HBox btnBox = new HBox(saveBtn);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(10, 0, 0, 0));

        VBox formContainer = new VBox(12,
            datesRow, pointsRow,
            pkgLabel, pkgScroll,
            vehLabel, vehScroll,
            empLabel, empScroll,
            btnBox
        );

        ScrollPane mainScroll = new ScrollPane(formContainer);
        mainScroll.setFitToWidth(true);

        formContainer.setPadding(new Insets(20));
        formContainer.setAlignment(Pos.TOP_CENTER);

        VBox mainContainer = new VBox(formContainer, mainScroll);

        BorderPane root = new BorderPane(mainContainer);
        root.setStyle("-fx-background-color: #f8f8f8;");

        myStage.setTitle("Formularz dostawy");
        myStage.setScene(new Scene(root, 400, 500));
        if (noDrivers == false) myStage.show();
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

    private List<Pracownik> getDrivers() {
        List<Pracownik> theDrivers = new ArrayList<>();
        RequestController rq = new RequestController("/pracownik", 0);

        String resp = "";
        boolean goFurther = false;

        try {
            resp = rq.sendPathReq();
            goFurther = true;
        } catch (BadRequestException bre) {
            System.err.println(bre.getMessage());
            goFurther = false;
        }

        if (goFurther) {
            List<Pracownik> allEmployees = new ArrayList<>();
            List<Integer> employeeRoles = null;
            ObjectMapper mapper = new ObjectMapper().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            goFurther = false;

            try {
                allEmployees = mapper.readValue(resp, new TypeReference<List<Pracownik>>(){});

                employeeRoles = extractEmployeeRoles(allEmployees.size(), resp);

                goFurther = true;
            } catch (IOException ioe) {
                System.err.println(ioe.getMessage());
            }

            if (goFurther) {
                for (int i = 0; i < employeeRoles.size(); ++i) {

                    if (employeeRoles.get(i) == 3) {
                        theDrivers.add(allEmployees.get(i));
                    }
                }
            }
        }
        return theDrivers;
    }

    /**
     * 
     * @param jsonData - already formatted data
     * @return True when successful / False otherwise
     */
    private boolean addDostawa(String jsonData) {
        RequestController rq = new RequestController("/dostawa/add", 1);

        try {
            rq.sendJsonReq(jsonData);

        } catch (BadRequestException bre) {
            System.out.println("addDostawa: " + bre.getMessage());
            return false;
        }

        return true;
    }

    private void handleButton() {
        String startDay = startDatePicker.getValue().toString();
        String endDay = endDatePicker.getValue().toString();

        String jason = String.format("{\"dataWyruszenia\": \"%s\", \"termin\": \"%s\", \"punktA\": \"%s\", \"punktB\": \"%s\", \"idPojazdu\": %d, \"driverId\": %d, \"paczki\": [",
                 startDay, endDay, pointAField.getText(), pointBField.getText(), this.selectedVehicleId, this.selectedDriverId);
    
        
            int howManyPackages = this.selectedPackagesIds.size();

        for (int a = 0; a < howManyPackages; ++a) {
            jason += selectedPackagesIds.get(a).toString();
            if (a < (howManyPackages - 1)) {
                jason += ",";
            }
        }

        jason += "]}";

        // System.out.println(jason);
        if (addDostawa(jason)) {
            myManager.refreshAllData(myRfsh);
            myStage.close();
        }
    }

    /**
     * Extracts Roles of employees from links in JSON response
     * @param baseList
     * @param jsonData
     * @return List of IDs for each employee
     */
    private List<Integer> extractEmployeeRoles(int numberOfEmployees, String jsonData) {
        List<Integer> roles = new ArrayList<>();
        try {
            JSONArray jsonEmployees = new JSONArray(jsonData);
            
            for (int i = 0; i < numberOfEmployees; ++i) {
                JSONObject singleEmpl = jsonEmployees.getJSONObject(i);
                JSONArray links = singleEmpl.getJSONArray("links");
                JSONObject stanowiskoLink = links.getJSONObject(1);
                
                String href = stanowiskoLink.getString("href");
                String[] hrefTokens = href.split("/");

                roles.add(Integer.parseInt(hrefTokens[hrefTokens.length - 1]));
            }

        } catch (NumberFormatException nfe) {
            System.err.println(nfe.getMessage());
        } catch (JSONException js) {
            System.err.println(js.getMessage());
        }

        return roles;
    }

    private void addOrRemovePackageId(boolean isSelected, int packageId) {
        if (isSelected) {
            this.selectedPackagesIds.add(packageId);
        } else {
            this.selectedPackagesIds.remove(this.selectedPackagesIds.indexOf(packageId));
        }
    }
}
