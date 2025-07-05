package com.polsl.firmakurierska.view.hello_world;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.polsl.firmakurierska.controller.RequestController;
import com.polsl.firmakurierska.dto.ProduktDTO;
import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.model.Klient;

public class PackageFormWindow {

    private List<ProduktDTO> produkty = null;
    private List<Klient> klienci = null;

    private VBox productsList;
    private VBox klientsList;

    private TextField weightField = null;

    private Integer selectedKlientId = 1;
    private double currentWeight = 0;
    private List<Integer> selectedProductIDs = null;

    private ManagerWindow myManager = null;
    private Button myRfsh = null;
    private Stage myStage = null;

    /** Pokazuje główne okno dodawania paczki */
    public void show(ManagerWindow parentWindow, Button parentRfsh) {

        myManager = parentWindow;
        myRfsh = parentRfsh;

        klienci = getKlients();
        produkty = getProdukts();
        selectedProductIDs = new ArrayList<>();
        
        myStage = new Stage();
        myStage.setTitle("Formularz paczki");

        klientsList = createKlientRadioList();
        klientsList.setStyle("-fx-background-color: white");
        klientsList.setMinHeight(75);

        ScrollPane klientScroll = new ScrollPane(klientsList);
        klientScroll.setFitToWidth(true);
        klientScroll.prefHeight(100);

        // --- Waga powinna być obliczana na podstawie produktów
        weightField = new TextField();
        weightField.setPromptText("Uzupełnione automatycznie");
        weightField.setEditable(false);
        weightField.setDisable(true);
        VBox weightBox = createInputCard("Waga paczki:", weightField);

        // --- Lista produktów do wyboru ---
        productsList = new VBox(8);
        productsList.setPadding(new Insets(5));
        productsList.setStyle("-fx-background-color: white");
        ScrollPane productsScroll = new ScrollPane(productsList);
        productsScroll.setFitToWidth(true);
        productsScroll.setPrefHeight(250);

        RequestController rq_helper = new RequestController(null, 0);

        for (ProduktDTO pr : produkty) {
            boolean packageless = false;

            // Get paczka id from ProduktDTO
            packageless = rq_helper.checkIfProductAssignedToPackage(pr);

            if (packageless) {
                CheckBox cb = new CheckBox();

                cb.setOnMouseClicked(e -> {
                    calculateWeight(cb.isSelected(), pr.getWaga());
                    addOrRemoveProductId(cb.isSelected(), pr.getIdProduktu());
                });

                VBox pDetails = createProductCard(pr);
                HBox prBox = new HBox(cb, pDetails);
                prBox.setSpacing(5);
                prBox.setAlignment(Pos.CENTER_LEFT);
                productsList.getChildren().add(prBox);
            }
        }

        // --- Przycisk dodawania nowego produktu ---
        Button addProductBtn = new Button("Dodaj nowy produkt");
        addProductBtn.setOnMouseClicked(e -> {
            //new ProductFormWindow().show(product -> {
            //    products.add(product);
            //    productsList.getChildren().add(createProductCard(product));
            //});
        });
        HBox addProdBox = new HBox(addProductBtn);
        addProdBox.setAlignment(Pos.CENTER);

        // --- Przycisk zapisania całej paczki ---
        Button savePackageBtn = new Button("Dodaj paczkę");
        savePackageBtn.setOnMouseClicked(e -> {
            handleButton();
        });
        HBox saveBox = new HBox(savePackageBtn);
        saveBox.setAlignment(Pos.CENTER);

        // --- Układ wszystkich elementów ---
        VBox container = new VBox(15,
            klientsList, klientScroll,
            weightBox,
            new Label("Produkty:"), productsScroll, addProdBox,
            saveBox
        );
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.TOP_CENTER);

        BorderPane root = new BorderPane(container);
        root.setStyle("-fx-background-color: #f8f8f8;");

        myStage.setScene(new Scene(root, 400, 600));
        myStage.show();
    }

    /** Karta wejściowa z etykietą i polem */
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

    /** Karta wyświetlająca dane produktu na liście */
    private VBox createProductCard(ProduktDTO p) {
        VBox box = new VBox(5,
            new Label("Nazwa: " + p.getNazwaProduktu()),
            new Label("Waga: " + p.getWaga()),
            new Label("Kategoria: " + p.getKategoriaProd()),
            new Label("Nr seryjny: " + p.getNrSeryjny())
        );
        box.setPadding(new Insets(8));
        box.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #cccccc;
            -fx-border-radius: 6;
            -fx-background-radius: 6;
        """);
        return box;
    }

    private VBox createKlientRadioList() {
        VBox klientContainer = new VBox();
        ToggleGroup kGroup = new ToggleGroup();

        for (Klient kl : klienci) {
            String klLabel = kl.getIdKlienta().toString() + " | " + kl.getImieK() + ' ' + kl.getNazwiskoK();
            RadioButton rb = new RadioButton(klLabel);
            rb.setToggleGroup(kGroup);
            rb.setPrefHeight(25);
            rb.setOnMouseClicked(e -> {
                this.selectedKlientId = kl.getIdKlienta();
                System.out.println("Selected client: " + this.selectedKlientId.toString());
            });
            klientContainer.getChildren().add(rb);
        }

        return klientContainer;
    }

    /**
     * 
     * @param jsonData - already formatted data
     * @return True when successful / False otherwise
     */
    private boolean addPackage(String jsonData) {
        RequestController rq = new RequestController("/paczka", 1);
        System.out.println(jsonData);
        try {
            rq.sendJsonReq(jsonData);
        } catch (BadRequestException bre) {
            System.out.println("addPackage: " + bre.getMessage());
            return false;
        }

        return true;
    }

    private List<Klient> getKlients() {
        List<Klient> clients = new ArrayList<>();
        RequestController rq = new RequestController("/klient", 0);
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
                clients = mapper.readValue(response, new TypeReference<List<Klient>>(){});
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }       
        return clients;
    }

    private List<ProduktDTO> getProdukts() {
        List<ProduktDTO> produkts = new ArrayList<>();
        RequestController rq = new RequestController("/produkt", 0);
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
                produkts = mapper.readValue(response, new TypeReference<List<ProduktDTO>>(){});
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }       
        return produkts;
    }

    private void calculateWeight(boolean isAddition, double wg) {
        if (isAddition) {
            this.currentWeight += wg;
        } else {
            this.currentWeight -= wg;
        }
        this.weightField.setText(String.format("%f", this.currentWeight));
    }

    private void addOrRemoveProductId(boolean isSelected, int productId) {
        if (isSelected) {
            this.selectedProductIDs.add(productId);
        } else {
            this.selectedProductIDs.remove(this.selectedProductIDs.indexOf(productId));
        }
    }

    private void handleButton() {
        String jasonToSend = "";
        String errorString = "Error overrides this string";
        boolean noError = true;
        
        try {
            jasonToSend = String.format("{\"wagaPaczki\": %s, \"klientId\": %s, \"produktIds\": [",
                this.currentWeight, this.selectedKlientId
            );
            int howManyProdukts = this.selectedProductIDs.size();

            for (int a = 0; a < howManyProdukts; ++a) {
                jasonToSend += selectedProductIDs.get(a).toString();
                if (a < (howManyProdukts - 1)) {
                    jasonToSend += ",";
                }
            }

            jasonToSend += "]}";

        }  catch (NullPointerException npe) {
            errorString = npe.getMessage();
            noError = false;
        } catch (NumberFormatException nfe) {
            errorString = nfe.getMessage();
            noError = false;
        } catch (IllegalFormatException ife) {
            errorString = ife.getMessage();
            noError = false;
        }

        
        if (noError) {
            if (addPackage(jasonToSend)) {
                myManager.refreshAllData(myRfsh);
                myStage.close();
            } else {
                System.err.println("handleButton: Błąd dodawania paczki");
            }
        } else {
            System.err.println("handleButton: " + errorString);
        }
    }
}
