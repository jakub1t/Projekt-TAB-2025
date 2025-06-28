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
import java.io.File;  
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.polsl.firmakurierska.controller.RequestController;
import com.polsl.firmakurierska.dto.DostawaDTO;
import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.model.Pracownik;
import com.polsl.firmakurierska.model.Konto;
import com.polsl.firmakurierska.model.Pojazd;


public class RaportFormWindow {

    /**
     * Otwiera okno formularza raportu z dwiema datami, miejscem zapisu i typem formularza.
     */
    


    public void show() {
        // Data początkowa
        DatePicker startDatePicker = new DatePicker();
        VBox startDateBox = createInputCard("Data od:", startDatePicker);

        // Data końcowa
        DatePicker endDatePicker = new DatePicker();
        VBox endDateBox = createInputCard("Data do:", endDatePicker);

        // Miejsce zapisu
        TextField saveLocationField = new TextField();
        saveLocationField.setPromptText("np. /sciezka/do/raportu.txt");
        VBox saveLocationBox = createInputCard("Miejsce zapisu:", saveLocationField);

        // Typ formularza (jednokrotny wybór)
        String[] formTypes = {"Wybrane daty", "Pełen"};
        ToggleGroup formTypeGroup = new ToggleGroup();
        VBox formTypeBox = createRadioInputCard("Typ raportu:", formTypes, formTypeGroup);

        // Przycisk generuj
        Button generateBtn = new Button("Generuj raport");
        generateBtn.setOnAction(e -> {
            RadioButton selected = (RadioButton) formTypeGroup.getSelectedToggle();
            String chosenType = selected != null ? selected.getText() : "";
            
            
            System.out.println("Raport:");
            System.out.println("Data od: " + startDatePicker.getValue());
            LocalDate startDate = startDatePicker.getValue();
            System.out.println("Data do: " + endDatePicker.getValue());
            LocalDate endDate = endDatePicker.getValue();
            System.out.println("Miejsce zapisu: " + saveLocationField.getText());
            String fileLocation = saveLocationField.getText();
            System.out.println("Typ formularza: " + chosenType);

            generateRaport(startDate, endDate, fileLocation, chosenType);


            ((Stage) generateBtn.getScene().getWindow()).close();
        });
        HBox btnBox = new HBox(generateBtn);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(10, 0, 0, 0));

        // Kontener główny
        VBox container = new VBox(12,
            startDateBox,
            endDateBox,
            saveLocationBox,
            formTypeBox,
            btnBox
        );
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.TOP_CENTER);

        BorderPane root = new BorderPane(container);
        root.setStyle("-fx-background-color: #f8f8f8;");

        Stage stage = new Stage();
        stage.setTitle("Formularz raportu");
        stage.setScene(new Scene(root, 400, 440));
        stage.show();
    }

    /**
     * Tworzy kartę z etykietą i polem wejściowym (tekst, data, itp.).
     */
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

    /**
     * Tworzy kartę z etykietą oraz grupą radio buttonów do wyboru jednej opcji.
     */
    private VBox createRadioInputCard(String labelText, String[] options, ToggleGroup group) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold;");
        VBox box = new VBox(5, label);
        for (String opt : options) {
            RadioButton rb = new RadioButton(opt);
            rb.setToggleGroup(group);
            box.getChildren().add(rb);
        }
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


    private void generateRaport(LocalDate startDate, LocalDate endDate, String fileLocation, String raportType){
        List<DostawaDTO> allDeliveries = getAllDeliveries();
        List<Konto> accounts = getAllAccounts();
        List<Pracownik> workers = getAllWorkers();
        List<Pojazd> vehicles = getAllVehicles();

        List<DostawaDTO> selectedDeliveries = sortDeliveries(allDeliveries, startDate, endDate, raportType);

        writeToFile(fileLocation, selectedDeliveries, accounts, workers, vehicles);
    }

    private List<DostawaDTO> sortDeliveries(List<DostawaDTO> allDeliveries,LocalDate startDate, LocalDate endDate,String raportType){
        List<DostawaDTO> sortedDeliveries = new ArrayList<>();
        
        //sort deliveries
        if (raportType == "Pełen")
        {
            sortedDeliveries = allDeliveries;
        }
        else {
            for (DostawaDTO elem : allDeliveries) {
                boolean delieveryAfterStart = elem.getDataWyruszenia().isAfter(startDate) || elem.getDataWyruszenia().equals(startDate);
                boolean delieveryBeforeEnd = elem.getDataWyruszenia().isBefore(endDate) || elem.getDataWyruszenia().isEqual(endDate);
                
                if(delieveryAfterStart && delieveryBeforeEnd){
                    sortedDeliveries.add(elem);
                }
            }
        }
        return sortedDeliveries;
    
    }

    private void writeToFile(String filePath, List<DostawaDTO> selectedDeliveries, List<Konto> accounts, List<Pracownik> workers, List<Pojazd> vehicles){
        File raportFile = new File(filePath+ "\\raport.txt");
        
        try{
        FileWriter writer = new FileWriter(filePath+ "\\raport.txt");
        //header
        writer.write("***************" + "\nSUPER RAPORT\n" +"***************\n\n\n");


        
        writer.write("***************" + "\nend of SUPER RAPORT\n" +"***************\n\n\n");
        writer.close();
        } catch (IOException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
        }


    }

    private List<DostawaDTO> getAllDeliveries(){
        
        List<DostawaDTO> mojeDostawy = new ArrayList<>();
        RequestController rq = new RequestController("/dostawa", 0);
        String response = "";
        boolean goFurther = true;

        try {
            response = rq.sendPathReq();
        } catch (BadRequestException e) {
            System.out.println(e.getMessage());
            goFurther = false;
        }
        if (goFurther) {
            ObjectMapper mapper = new ObjectMapper().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.registerModule(new JavaTimeModule());
            try {
                mojeDostawy = mapper.readValue(response, new TypeReference<List<DostawaDTO>>(){});
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }      

        return mojeDostawy;
    }

    private List<Konto> getAllAccounts(){
        List<Konto> accounts = new ArrayList<>();
        String response = "";

        // Prepare request to get all accounts from db
        RequestController rq = new RequestController("/konto/all", 0);

        // Get accounts
        try {
            response = rq.sendPathReq();
        }
        catch (BadRequestException e) {
            System.out.println(e.getMessage());
        }

        // Map JSON string into List<Konto>
        ObjectMapper mapper = new ObjectMapper();

        try {
            accounts = mapper.readValue(response, new TypeReference<List<Konto>>(){});
        }
        catch (IOException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }

        return accounts;
    }

    private List<Pracownik> getAllWorkers(){
        
        List<Pracownik> pracownicy = new ArrayList<>();
        RequestController rq = new RequestController("/pracownik", 0);
        String response = "";
        boolean goFurther = true;

        try {
            response = rq.sendPathReq();
        } catch (BadRequestException e) {
            System.out.println(e.getMessage());
            goFurther = false;
        }
        if (goFurther) {
            ObjectMapper mapper = new ObjectMapper().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.registerModule(new JavaTimeModule());
            try {
                pracownicy= mapper.readValue(response, new TypeReference<List<Pracownik>>(){});
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }      

        return pracownicy;
    }
    
    private List<Pojazd> getAllVehicles(){
        
        List<Pojazd> vechicles = new ArrayList<>();
        RequestController rq = new RequestController("/pojazd", 0);
        String response = "";
        boolean goFurther = true;

        try {
            response = rq.sendPathReq();
        } catch (BadRequestException e) {
            System.out.println(e.getMessage());
            goFurther = false;
        }
        if (goFurther) {
            ObjectMapper mapper = new ObjectMapper().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.registerModule(new JavaTimeModule());
            try {
                vechicles= mapper.readValue(response, new TypeReference<List<Pojazd>>(){});
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }      

        return vechicles;
    }



//  ObjectMapper mapper = new ObjectMapper().configure(
//                 DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//             mapper.registerModule(new JavaTimeModule());
//             try {
//                 mojeDostawy = mapper.readValue(response, new TypeReference<List<DostawaDTO>>(){});
//             } catch (IOException e) {
//                 System.out.println(e.getMessage());
//             }


}