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
import com.polsl.firmakurierska.dto.PaczkaDTO;
import com.polsl.firmakurierska.dto.ProduktDTO;
import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.exception.ResourceNotFoundException;
import com.polsl.firmakurierska.model.Klient;
import com.polsl.firmakurierska.view.UIBuilder;
import com.polsl.firmakurierska.view.UIThemeManager;

public class EditPackages {

    private final UIThemeManager theme = UIThemeManager.getUIThemeManager();
    private final UIBuilder ui = UIBuilder.getUIBuilder();

    private List<ProduktDTO> produkty = null;
    private List<Klient> klienci = null;

    private VBox productsList;
    private VBox klientsList;

    private TextField weightField = null;

    private Integer selectedKlientId = -1;
    private double currentWeight = 0;
    private List<Integer> selectedProductIDs = null;

    private ManagerWindow myManager = null;
    private Button myRfsh = null;
    private Stage myStage = null;

    /** Pokazuje główne okno dodawania paczki */
    public void show(ManagerWindow parentWindow, Button parentRfsh, PaczkaDTO paczkaData, 
        List<ProduktDTO> productsInCurrentPackage) {

        myManager = parentWindow;
        myRfsh = parentRfsh;

        klienci = getKlients();
        produkty = getProdukts();
        selectedProductIDs = new ArrayList<>();
        selectedKlientId = paczkaData.getKlientId();
        
        myStage = new Stage();
        myStage.setTitle("Formularz paczki");

        klientsList = createKlientRadioList();
        klientsList.setMinHeight(75);

        ScrollPane klientScroll = new ScrollPane(klientsList);
        klientScroll.setFitToWidth(true);
        klientScroll.prefHeight(100);

        // --- Waga powinna być obliczana na podstawie produktów
        weightField = new TextField();
        weightField.setPromptText("Uzupełnione automatycznie");
        weightField.setEditable(false);
        weightField.setDisable(true);
        VBox weightBox = ui.createFormInputCard(theme.getThemeMode(), "Waga paczki:", weightField);

        // --- Lista produktów do wyboru ---
        productsList = ui.createListContainer(theme.getThemeMode());
        productsList.setPadding(new Insets(5));

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
                    Integer prId = pr.getIdProduktu();
                    if (cb.isSelected()) {
                        this.selectedProductIDs.add(prId);
                    } else {
                        this.selectedProductIDs.remove(this.selectedProductIDs.indexOf(prId));
                    }
                });

                VBox pDetails = createProductCard(pr);
                HBox prBox = new HBox(cb, pDetails);
                prBox.setSpacing(5);
                prBox.setAlignment(Pos.CENTER_LEFT);
                productsList.getChildren().add(prBox);
            }
        }
        for (ProduktDTO pr : productsInCurrentPackage) {

            CheckBox cb = new CheckBox();
            cb.setSelected(true);
            this.selectedProductIDs.add(pr.getIdProduktu());
            calculateWeight(true, pr.getWaga());

            cb.setOnMouseClicked(e -> {
                calculateWeight(cb.isSelected(), pr.getWaga());
                Integer prId = pr.getIdProduktu();
                if (cb.isSelected()) {
                    this.selectedProductIDs.add(prId);
                } else {
                    this.selectedProductIDs.remove(this.selectedProductIDs.indexOf(prId));
                }
            });

            VBox pDetails = createProductCard(pr);
            HBox prBox = new HBox(cb, pDetails);
            prBox.setSpacing(5);
            prBox.setAlignment(Pos.CENTER_LEFT);
            productsList.getChildren().add(prBox);
        }

        // --- Przycisk zapisania całej paczki ---
        Button savePackageBtn = ui.createStylizedButton(theme.getThemeMode(), 200, "Zapisz paczkę");
        savePackageBtn.setOnMouseClicked(e -> {
            if(!checkIfDataCorrect()) return;

            savePackageBtn.setDisable(true);

            handleButton(paczkaData.getIdPaczki(), paczkaData.getProduktIds());
        });
        HBox saveBox = new HBox(savePackageBtn);
        saveBox.setAlignment(Pos.CENTER);

        // --- Układ wszystkich elementów ---
        VBox selectProdBox = ui.createStylizedColumn(theme.getThemeMode(), "Produkty", Integer.MAX_VALUE, productsScroll, saveBox);

        VBox container = new VBox(15,
            klientsList, klientScroll,
            weightBox,
            selectProdBox
        );
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.TOP_CENTER);

        BorderPane root = new BorderPane(container);

        if (theme.getThemeMode()) {
            root.setBackground(ui.unifiedRootBgDark);
            container.setStyle("-fx-background-color: #3A3A3A");
        } else {
            root.setBackground(ui.unifiedRootBgLight);
        }

        myStage.setScene(new Scene(root, 400, 600));
        myStage.show();
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
        VBox klientContainer = ui.createListContainer(theme.getThemeMode());
        ToggleGroup kGroup = new ToggleGroup();

        for (Klient kl : klienci) {
            String klLabel = kl.getIdKlienta().toString() + " | " + kl.getImieK() + ' ' + kl.getNazwiskoK();
            RadioButton rb = ui.createFancyRadioButton(theme.getThemeMode(), klLabel);
            rb.setToggleGroup(kGroup);
            rb.setPrefHeight(25);
            if (kl.getIdKlienta() == selectedKlientId) {
                rb.setSelected(true);
            }
            rb.setOnMouseClicked(e -> {
                this.selectedKlientId = kl.getIdKlienta();
                System.out.println("Selected client: " + this.selectedKlientId.toString());
            });
            klientContainer.getChildren().add(rb);
        }

        return klientContainer;
    }

    private List<Klient> getKlients() {
        List<Klient> clients = new ArrayList<>();
        RequestController rq = new RequestController("/klient", 0);
        String response = "";
        boolean goFurther = true;

        try {
            response = rq.sendPathReq();
        } catch (BadRequestException e) {
            ui.showAlertDialog("Błąd", "Błąd podczas ładowania klientów!", e.getMessage());
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
            ui.showAlertDialog("Błąd", "Błąd podczas ładowania produktów!", e.getMessage());
            goFurther = false;
        } catch (ResourceNotFoundException rex) {
            ui.showAlertDialog("Błąd", "Błąd podczas ładowania produktów!", rex.getMessage());
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

    /**
     * 
     * @param jsonData - already formatted data
     * @return True when successful / False otherwise
     */
    private boolean editPackage(Integer paczkaId, String jsonData) {
        RequestController rq = new RequestController("/paczka/update/" + paczkaId, 2);
        System.out.println(jsonData);
        try {
            rq.sendJsonReq(jsonData);
        } catch (BadRequestException bre) {
            ui.showAlertDialog("Błąd", "Błąd podczas edycji paczki!", bre.getMessage());
            return false;
        } catch (ResourceNotFoundException rex) {
            ui.showAlertDialog("Błąd", "Błąd podczas edycji paczki!", rex.getMessage());
            return false;
        }

        return true;
    }

    private void handleButton(Integer paczkaId, List<Integer> usedProducts) {
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

            jasonToSend += "], \"usedProduktIds\": [";

            int howManyUsedProdukts = usedProducts.size();

            for (int a = 0; a < howManyUsedProdukts; ++a) {
                jasonToSend += usedProducts.get(a).toString();
                if (a < (howManyUsedProdukts - 1)) {
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
            if (editPackage(paczkaId, jasonToSend)) {
                myManager.refreshAllData(myRfsh);
                myStage.close();
            } else {
                System.err.println("handleButton: Błąd aktualizacji paczki");
            }
        } else {
            System.err.println("handleButton: " + errorString);
        }
    }

    private boolean checkIfDataCorrect() {

        if(selectedKlientId == -1) {
            ui.showAlertDialog("Błąd", "Nie wybrano żadnego klienta!", 
            "Należy wybrać klienta dla paczki.");
            return false;
        }

        if(selectedProductIDs.size() == 0) {
            ui.showAlertDialog("Błąd", "Nie wybrano żadnych produktów!", 
            "Paczka musi zawierać przynajmniej jeden produkt.");
            return false;
        }

        return true;
    }
}
