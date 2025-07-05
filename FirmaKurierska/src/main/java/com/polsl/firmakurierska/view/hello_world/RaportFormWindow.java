package com.polsl.firmakurierska.view.hello_world;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.polsl.firmakurierska.controller.RequestController;
import com.polsl.firmakurierska.dto.DostawaDTO;
import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.model.Konto;
import com.polsl.firmakurierska.model.Pojazd;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


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
        //List<Pracownik> workers = getAllWorkers();
        List<Pojazd> vehicles = getAllVehicles();

        List<DostawaDTO> selectedDeliveries = sortDeliveries(allDeliveries, startDate, endDate, raportType);

        writeToFile(fileLocation, selectedDeliveries, accounts, vehicles);
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

    private void writeToFile(String filePath, List<DostawaDTO> selectedDeliveries, List<Konto> accounts, List<Pojazd> vehicles){
        
        try {
        FileWriter writer = new FileWriter(filePath + "\\raport.txt");
        //header
        writer.write("***************" + "\nSUPER RAPORT\n" +"***************\n\n\n");

        //list the workers
        writer.write("Company workers list:\n\n");
        
        for(Konto elem : accounts){
            List<String> workerData = getWorkerData(elem.getIdKonta());
            writer.write("  " + elem.getIdKonta() + ". " + workerData.get(0) + " " + workerData.get(1) + "\n");
            writer.write("   Pesel: " + workerData.get(2) + "\n");
            writer.write("   Stanowisko: " + workerData.get(3) + "\n");
            writer.write("   Prawo jazdy: " + workerData.get(4)+ "\n\n");
        }

        writer.write("\nCompany vechicles list:\n\n");
        
        //list the vechicles 
       for(Pojazd elem : vehicles){

            writer.write("  " + elem.getIdPojazdu() + ". " + elem.getMarka() + " " + elem.getModel()+"\n");
            writer.write("   Typ: " + elem.getTypPojazdu() + "\n");
            writer.write("   Numer rejestracji: " + elem.getNrRejestr() + "\n");
            writer.write("   Pojemność: " + elem.getPojemnosc() + "\n\n");

       }

         //list the deliveries
       writer.write("\nCompany deliveries state:\n\n");

       int finishedDeliveries = 0;
       int activeDeliveries = 0;

       for (DostawaDTO elem : selectedDeliveries)
       {
            if(elem.getStatus().equals("W_TRAKCIE"))
            {
                activeDeliveries++;
            }
            else{
                finishedDeliveries++;
            }
       }
        writer.write("   Aktywne dostawy: "+ activeDeliveries +"\n");
        writer.write("   Ukończone dostawy: "+ finishedDeliveries+"\n\n");

        writer.write("   Lista dostaw: "+ finishedDeliveries+"\n\n");
        
        for (DostawaDTO elem : selectedDeliveries)
       {
            writer.write("  " + elem.getIdDostawy() + ". Z: " + elem.getPunktA() + "  Do: " + elem.getPunktB()+"\n");
            writer.write("   Termin, Od: " + elem.getDataWyruszenia()+ "  Do: " + elem.getTermin() + "\n");
            writer.write("   Status: " + elem.getStatus() + "\n\n");
       }


       
        writer.write("\n***************" + "\nend of SUPER RAPORT\n" +"***************\n\n\n");
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

    private List<String> getWorkerData(Integer accountID) {
            List<String> workerData = new ArrayList<>();
            String response = "";

            // Prepare request to get all accounts from db
            RequestController rq = new RequestController("/pracownik/" + accountID, 1);

            response = rq.sendPathReq();

            String stanowiskoID = rq.getStanowisko(response);

            List<String> kategoriePrawaJazdy = rq.getKategorie(response);

            try {
                JSONObject jsonData = new JSONObject(response);

                workerData.add(jsonData.getString("imie"));
                workerData.add(jsonData.getString("nazwisko"));
                workerData.add(jsonData.getString("pesel"));

                rq = new RequestController("/stanowisko/" + stanowiskoID, 0);

                response = new String(rq.sendPathReq());

                JSONObject stanowiskoJson = new JSONObject(response);
                workerData.add(stanowiskoJson.getString("nazwaStanowiska"));

                kategoriePrawaJazdy.forEach(kategoria -> {
                    RequestController tempRq = new RequestController("/prawojazdy/" + kategoria, 0);

                    String tempResponse = new String(tempRq.sendPathReq());
                    
                    try {
                        JSONObject prawoJazdyJson = new JSONObject(tempResponse);
                        workerData.add(prawoJazdyJson.getString("kategoria"));
                    }
                    catch (JSONException jex) {
                        System.out.println(jex.toString());
                        jex.printStackTrace();
                    }
                });
            }
            catch (JSONException jex) {
                System.out.println(jex.toString());
                jex.printStackTrace();
            }

            return workerData;
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