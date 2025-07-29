package com.polsl.firmakurierska.view.hello_world;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.Link;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.polsl.firmakurierska.controller.RequestController;
import com.polsl.firmakurierska.dto.DostawaDTO;
import com.polsl.firmakurierska.dto.PaczkaDTO;
import com.polsl.firmakurierska.dto.ProduktDTO;
import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.exception.ResourceNotFoundException;
import com.polsl.firmakurierska.model.Klient;
import com.polsl.firmakurierska.model.Pojazd;
import com.polsl.firmakurierska.model.Pracownik;
import com.polsl.firmakurierska.view.UIBuilder;
import com.polsl.firmakurierska.view.UIThemeManager;

public class ManagerWindow extends Application {

    private final UIBuilder ui = UIBuilder.getUIBuilder();
    private final UIThemeManager theme = UIThemeManager.getUIThemeManager();
    private Stage myStage = null;

    private final int initHeight = 450;
    private final int initWidth = 900;

    private int loggedUserId = 0;
    private String loggedUserName = "Imię";
    private String loggedUserSurname = "Nazwisko";

    private List<ProduktDTO> produkty = null;
    private List<PaczkaDTO> paczki = null;
    private List<Pojazd> pojazdy = null;
    private List<DostawaDTO> dostawy = null;
    private List<Klient> klienci = null;

    private VBox produktyList;
    private VBox paczkiList;
    private VBox pojazdyList;
    private VBox dostawyList;
    private VBox klienciList;

    // Selection options for filtering deliveries: 
    // 0 - all, 1 - completed, 2 - not completed, any other value - none
    private int filterDeliveriesOption = 0; 

    public void open(int userId) {
        this.loggedUserId = userId;
        getMyName();

        if (myStage == null) {
            myStage = new Stage();
        }

        this.start(myStage);
    }

    @Override
    public void start(Stage stage) {
        Label welcomeLabel = new Label("Witaj " + loggedUserName + " " + loggedUserSurname + "!");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Button generujRaportBtn = ui.createStylizedButton(theme.getThemeMode(), 150, "Generuj raport");
        generujRaportBtn.setOnAction(e -> new RaportFormWindow().show());

        Button refreshBtn = ui.createStylizedButton(theme.getThemeMode(), 150, "Odśwież dane");

        refreshBtn.setOnAction(e -> {
            refreshAllData(refreshBtn);
        });

        Button themeSwitch = ui.createStylizedButton(theme.getThemeMode(), 70, "Motyw");
        themeSwitch.setOnAction(e -> {
            theme.setThemeMode(!theme.getThemeMode());
            this.start(myStage);
        });

        HBox welBox = new HBox(20, welcomeLabel, refreshBtn, generujRaportBtn, themeSwitch);
        welBox.setAlignment(Pos.CENTER);
        welBox.setMinHeight(60);
        welBox.setMaxHeight(60);
        
        // ===== KOL 1: PRODUKTY =====

        produktyList = ui.createListContainer(theme.getThemeMode());
        ScrollPane produktyScroll = new ScrollPane(produktyList);
        produktyScroll.setContent(produktyList);
        produktyScroll.setFitToWidth(true);
        produktyScroll.setPrefHeight(300);

        produkty = getProdukty();

        produkty.forEach(produkt -> {
            produktyList.getChildren().add(createProduktItem(produkt, refreshBtn));
        });

        Button dodajProduktBtn = ui.createStylizedButton(theme.getThemeMode(), 120, "Dodaj produkt");
        dodajProduktBtn.setOnAction(e -> new ProductFormWindow().show(this, refreshBtn));

        VBox produktyCol = ui.createStylizedColumn(theme.getThemeMode(), "Produkty", Integer.MAX_VALUE, produktyScroll, dodajProduktBtn);

        // ===== KOL 2: PACZKI =====
        paczkiList = ui.createListContainer(theme.getThemeMode());
        ScrollPane paczkiScroll = new ScrollPane(paczkiList);
        paczkiScroll.setFitToWidth(true);
        paczkiScroll.setPrefHeight(300);
        
        paczki = getPaczki();

        paczki.forEach(paczka -> {
            paczkiList.getChildren().add(createPackageItem(paczka.getIdPaczki(), paczka, refreshBtn));
        });

        Button dodajPaczkeBtn = ui.createStylizedButton(theme.getThemeMode(), 120, "Dodaj paczkę");
        dodajPaczkeBtn.setOnAction(e -> new PackageFormWindow().show(this, refreshBtn));
        VBox paczkiCol = ui.createStylizedColumn(theme.getThemeMode(), "Paczki", Integer.MAX_VALUE, paczkiScroll, dodajPaczkeBtn);

        // ===== KOL 3: POJAZDY =====
        pojazdyList = ui.createListContainer(theme.getThemeMode());
        ScrollPane pojazdyScroll = new ScrollPane(pojazdyList);
        pojazdyScroll.setFitToWidth(true);
        pojazdyScroll.setPrefHeight(300);

        pojazdy = getPojazdy();

        pojazdy.forEach(pojazd -> {
            pojazdyList.getChildren().add(createVehicleItem(pojazd.getIdPojazdu(), pojazd, refreshBtn));
        });

        Button dodajPojazdBtn = ui.createStylizedButton(theme.getThemeMode(), 120, "Dodaj pojazd");
        dodajPojazdBtn.setOnAction(e -> new VehicleFormWindow().show(this, refreshBtn));
        VBox pojazdyCol = ui.createStylizedColumn(theme.getThemeMode(), "Pojazdy", Integer.MAX_VALUE, pojazdyScroll, dodajPojazdBtn);

        // ===== KOL 4: DOSTAWY =====
        ObservableList<String> options = 
            FXCollections.observableArrayList(
                "Wszystkie",
                "Wykonane",
                "Niewykonane"
            );
        final ComboBox<String> comboBox = new ComboBox<String>(options);

        comboBox.setOnAction(event -> {
            String selectedOption = new String(comboBox.getValue());

            if (selectedOption.equals("Wszystkie")) {
                this.filterDeliveriesOption = 0;
            } else if (selectedOption.equals("Wykonane")) {
                this.filterDeliveriesOption = 1;
            } else if (selectedOption.equals("Niewykonane")) {
                this.filterDeliveriesOption = 2;
            } else {
                this.filterDeliveriesOption = -1;
            }

            refreshAllData(refreshBtn);
        });
        comboBox.setValue("Wszystkie");

        dostawyList = ui.createListContainer(theme.getThemeMode());
        ScrollPane dostawyScroll = new ScrollPane(dostawyList);
        dostawyScroll.setFitToWidth(true);
        dostawyScroll.setPrefHeight(300);

        dostawy = getDostawy();

        dostawy.forEach(dostawa -> {
            dostawyList.getChildren().add(createDeliveryItem(dostawa.getIdDostawy(), dostawa, refreshBtn));
        });

        Button dodajDostaweBtn = ui.createStylizedButton(theme.getThemeMode(), 120, "Dodaj dostawę");
        dodajDostaweBtn.setOnAction(e -> {
            new DeliveryFormWindow().show(this, refreshBtn, pojazdy, paczki);
        });

        VBox dostawyCol = ui.createStylizedColumn(theme.getThemeMode(), "Dostawy", Integer.MAX_VALUE, comboBox, dostawyScroll, dodajDostaweBtn);

        // ===== KOL 5: KLIENCI =====

        klienciList = ui.createListContainer(theme.getThemeMode());
        ScrollPane klienciScroll = new ScrollPane(klienciList);
        klienciScroll.setFitToWidth(true);
        klienciScroll.setPrefHeight(300);

        klienci = getKlienci();

        klienci.forEach(klient -> {
            klienciList.getChildren().add(createKlientItem(klient, refreshBtn));
        });

        Button dodajKlientaBtn = ui.createStylizedButton(theme.getThemeMode(), 120, "Dodaj klienta");
        dodajKlientaBtn.setOnAction(e -> {
            new ClientFormWindow().show(this, refreshBtn);
        });
        
        VBox klienciCol = ui.createStylizedColumn(theme.getThemeMode(), "Klienci", Integer.MAX_VALUE, klienciList, klienciScroll, dodajKlientaBtn);

        // ===== GŁÓWNY UKŁAD =====

        // Nieco jasniejszy / ciemniejszy kolor
        if (theme.getThemeMode()) {
            paczkiCol.setBackground(
                new Background(new BackgroundFill(Color.web("#3A3A3A"), null, null))
            );
            dostawyCol.setBackground(
                new Background(new BackgroundFill(Color.web("#3A3A3A"), null, null))
            );
        } else {
            paczkiCol.setBackground(
                new Background(new BackgroundFill(Color.web("#F4F4F4"), null, null))
            );
            dostawyCol.setBackground(
                new Background(new BackgroundFill(Color.web("#F4F4F4"), null, null))
            );
        }

        HBox dataBox = new HBox(0, produktyCol, paczkiCol, pojazdyCol, dostawyCol, klienciCol);
        dataBox.setAlignment(Pos.CENTER);
        dataBox.setStyle("-fx-background-color: #FFFFFF");

        VBox root = new VBox(10, welBox, dataBox);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(10);
        
        if (theme.getThemeMode()) {
            welBox.setStyle("-fx-background-color: #2F2F2F");
            root.setStyle("-fx-background-color: #BBBBBB");
            welcomeLabel.setTextFill(Color.web("#BBBBBB"));
        } else {
            welBox.setStyle("-fx-background-color: #FFFFFF");
            root.setStyle("-fx-background-color: #C4C4C4");
        }

        Scene scene = new Scene(root, initWidth, initHeight);
        stage.setScene(scene);
        stage.setTitle("Administrator Panel");
        stage.show();
    }

    private HBox createProduktItem(ProduktDTO produktData, Button refreshBtn) {
        String name = String.format("ID: %d | %s", produktData.getIdProduktu(), produktData.getNazwaProduktu());

        Button itemBtn = ui.createStyledListItem(name, Integer.MAX_VALUE);
        itemBtn.setOnAction(e -> {
            new ProductDescription().show(produktData);
        });

        Button delBtn = ui.createStyledDeleteButton();
        delBtn.setOnAction(e -> {
            if (deleteProduct(produktData.getIdProduktu())) {
                produktyList.getChildren().removeIf(node -> {
                if (node instanceof HBox hbox) {
                    Button b = (Button) hbox.getChildren().get(0);
                    return b.getText().equals(name);
                }
                return false;
                });
                refreshAllData(refreshBtn);
            }
        });

        HBox box = new HBox(5, itemBtn, delBtn);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private HBox createPackageItem(int paczkaId, PaczkaDTO paczkaData, Button refreshBtn) {
        String name = "ID paczki: " + paczkaId;

        Button itemBtn = ui.createStyledListItem(name, Integer.MAX_VALUE);
        itemBtn.setOnAction(e -> {
            
            Integer klientId = paczkaData.getKlientId();

            Klient client = new Klient();
            
            if (klientId == null) client = null;
            else client = getKlientById(klientId);

            new PackageDescription().show(
                paczkaData, client
            );
        });
        Button delBtn = ui.createStyledDeleteButton();
        delBtn.setOnAction(e -> {
            if (deletePackage(paczkaId)) {
                paczkiList.getChildren().removeIf(node -> {
                if (node instanceof HBox hbox) {
                    Button b = (Button) hbox.getChildren().get(0);
                    return b.getText().equals(name);
                }
                return false;
                });
                refreshAllData(refreshBtn);
            }
        });
        Button editBtn = ui.createStyledEditButton();
        editBtn.setOnAction(e -> {
            List<ProduktDTO> productsInCurrentPackage = new ArrayList<>();

            for (ProduktDTO pr : produkty) {
                if (paczkaData.getProduktIds().contains(pr.getIdProduktu())) {
                    productsInCurrentPackage.add(pr);
                }
            }

            new EditPackages().show(this, refreshBtn, paczkaData, productsInCurrentPackage);
        });

        HBox box = new HBox(5, itemBtn, editBtn, delBtn);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private HBox createVehicleItem(int pojazdId, Pojazd pojazdData, Button refreshBtn) {
        String name = String.format("ID: %d | %s", pojazdId, pojazdData.getMarka());

        Button itemBtn = ui.createStyledListItem(name, Integer.MAX_VALUE);
        itemBtn.setOnAction(e -> {
            new VehicleDescription()
                .show(
                    name,
                    pojazdData.getTypPojazdu(),
                    pojazdData.getMarka(),
                    pojazdData.getModel(),
                    Double.toString(pojazdData.getPojemnosc()),
                    pojazdData.getNrRejestr()
                );
        });
        Button delBtn = ui.createStyledDeleteButton();
        delBtn.setOnAction(e ->{
            if (deleteVehicle(pojazdId)) {;    
                pojazdyList.getChildren().removeIf(node -> {
                if (node instanceof HBox hbox) {
                    Button b = (Button) hbox.getChildren().get(0);
                    return b.getText().equals(name);
                }
                return false;
                });
                refreshAllData(refreshBtn);
            }
        });
        Button editBtn = ui.createStyledEditButton();
        editBtn.setOnAction(e -> {
            new EditVehicle().show(this, refreshBtn, pojazdData);
        });

        HBox box = new HBox(5, itemBtn, editBtn, delBtn);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private HBox createDeliveryItem(int dostawaId, DostawaDTO dostawaData, Button refreshBtn) {
        String name = "ID dostawy: " + dostawaId;
        RequestController rq_helper = new RequestController("", 0);


        Button itemBtn = ui.createStyledListItem(name, Integer.MAX_VALUE);
        itemBtn.setOnAction(e -> {
            Optional<Link> pracownikWrappedLink = dostawaData.getLink("pracownik");

            if (pracownikWrappedLink.isPresent()) {
                Link pracownikLink = pracownikWrappedLink.get();

                String pracownikHref = pracownikLink.getHref();

                String pracownikId = rq_helper.returnValueFromHref("pracownik", pracownikHref);

                Pracownik pracownikData = getPracownikById(Integer.parseInt(pracownikId));

                new DeliveryDescription().open(dostawaId, pracownikData.getImie(), pracownikData.getNazwisko());
            } else {
                new DeliveryDescription().open(dostawaId, "Nie przypisano", "Nie przypisano");
            }
        });
        Button delBtn = ui.createStyledDeleteButton();
        delBtn.setOnAction(e -> { 
            if (deleteDelivery(dostawaId)){
                dostawyList.getChildren().removeIf(node -> {
                if (node instanceof HBox hbox) {
                    Button b = (Button) hbox.getChildren().get(0);
                    return b.getText().equals(name);
                }
                return false;
                });
                refreshAllData(refreshBtn);
            }
        });

        Button editBtn = ui.createStyledEditButton();
        editBtn.setOnAction(e -> {

            List<PaczkaDTO> packsInCurrentDelivery = new ArrayList<>();

            for (PaczkaDTO pk : paczki) {
                if (dostawaData.getPaczki().contains(pk.getIdPaczki())) {
                    packsInCurrentDelivery.add(pk);
                }
            }

            Optional<Link> pracownikWrappedLink = dostawaData.getLink("pracownik");

            if (pracownikWrappedLink.isPresent())
            {
                Link pracownikLink = pracownikWrappedLink.get();

                String pracownikHref = pracownikLink.getHref();

                String pracownikId = rq_helper.returnValueFromHref("pracownik", pracownikHref);

                new EditDelivery().show(this, refreshBtn, dostawaData, pojazdy, paczki, packsInCurrentDelivery, Integer.parseInt(pracownikId));
            } else {
                new EditDelivery().show(this, refreshBtn, dostawaData, pojazdy, paczki, packsInCurrentDelivery, null);
            }
        });

        if (dostawaData.getStatus().equals("ZREALIZOWANA")) {
            itemBtn.setBackground(ui.buttonCompletedDeliveryInactive);
            itemBtn.setOnMouseEntered(e-> { itemBtn.setBackground(ui.buttonCompletedDeliveryActive);});
            itemBtn.setOnMouseExited(e-> { itemBtn.setBackground(ui.buttonCompletedDeliveryInactive);});
            editBtn.setBackground(ui.buttonCompletedDeliveryInactive);
            editBtn.setDisable(true);
            delBtn.setBackground(ui.buttonCompletedDeliveryInactive);
            delBtn.setDisable(true);
        } else {
            editBtn.setDisable(false);
            delBtn.setDisable(false);
        }

        HBox box = new HBox(5, itemBtn, editBtn, delBtn);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private HBox createKlientItem(Klient klientData, Button refreshBtn) {
        String name = String.format("ID: %d | %s %s", klientData.getIdKlienta(), klientData.getImieK(), klientData.getNazwiskoK());

        Button itemBtn = ui.createStyledListItem(name, Integer.MAX_VALUE);
        itemBtn.setOnAction(e -> {
            new ClientDescription().show(klientData);
        });

        Button delBtn = ui.createStyledDeleteButton();
        delBtn.setOnAction(e -> {
            if (deleteClient(klientData.getIdKlienta())){
                dostawyList.getChildren().removeIf(node -> {
                if (node instanceof HBox hbox) {
                    Button b = (Button) hbox.getChildren().get(0);
                    return b.getText().equals(name);
                }
                return false;
                });
                refreshAllData(refreshBtn);
            }
        });

        HBox box = new HBox(5, itemBtn, delBtn);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private List<DostawaDTO> getDostawy() {
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
        // wyświetlanie przefiltrowanych dostaw 
        List<DostawaDTO> wybraneDostawy = filterDeliveries(mojeDostawy, filterDeliveriesOption);

        return wybraneDostawy;
    }

    private List<DostawaDTO> filterDeliveries(List<DostawaDTO> deliveries, int filterOption) {

        final List<DostawaDTO> filteredDeliveries = new ArrayList<>();

        switch (filterOption) {
            case 0:
                return deliveries;
            case 1:
                deliveries.forEach(delivery -> {
                    if (delivery.getStatus().equals("ZREALIZOWANA")){
                        filteredDeliveries.add(delivery);
                    } 
                });
                break;
            case 2:
                deliveries.forEach(delivery -> {
                    if (delivery.getStatus().equals("W_TRAKCIE")){
                        filteredDeliveries.add(delivery);
                    } 
                });
                break;
        
            default:
                System.out.println("Incorrect filter option...");
                break;
        }
        return filteredDeliveries;
    }

    private List<Pojazd> getPojazdy() {
        List<Pojazd> mojePojazdy = new ArrayList<>();
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
                mojePojazdy = mapper.readValue(response, new TypeReference<List<Pojazd>>(){});
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }       
        return mojePojazdy;
    }

    private List<PaczkaDTO> getPaczki() {
        List<PaczkaDTO> mojePaczki = new ArrayList<>();
        RequestController rq = new RequestController("/paczka", 0);
        String response = "";
        boolean goFurther = true;

        try {
            response = rq.sendPathReq();

        } catch (ResourceNotFoundException rex) {
            System.out.println(rex.getMessage());
            goFurther = false;
        } catch (BadRequestException e) {
            System.out.println(e.getMessage());
            goFurther = false;
        }
        if (goFurther) {
            ObjectMapper mapper = new ObjectMapper().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.registerModule(new JavaTimeModule());
            try {
                mojePaczki = mapper.readValue(response, new TypeReference<List<PaczkaDTO>>(){});
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }       
        return mojePaczki;
    }

    private Pracownik getPracownikById(int workerId) {
        Pracownik pracownik = new Pracownik();
        String response = "";
        RequestController rq = new RequestController("/pracownik/" + workerId, 1);

        try {
            response = rq.sendPathReq();
        } catch (BadRequestException e) {
            System.out.println(e.getMessage());
        }
        
        ObjectMapper mapper = new ObjectMapper().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule());
        try {
            pracownik = mapper.readValue(response, new TypeReference<Pracownik>(){});
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return pracownik;
    }

    private Klient getKlientById(int clientId) {
        Klient klient = new Klient();
        String response = "";
        RequestController rq = new RequestController("/klient/" + clientId, 0);

        try {
            response = rq.sendPathReq();
        } catch (BadRequestException e) {
            System.out.println(e.getMessage());
        }
        
        ObjectMapper mapper = new ObjectMapper().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule());
        try {
            klient = mapper.readValue(response, new TypeReference<Klient>(){});
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return klient;
    }

    private List<Klient> getKlienci() {
        List<Klient> moiKlienci = new ArrayList<>();
        RequestController rq = new RequestController("/klient", 0);

        String response = "";
        boolean goFurther = true;

        try {
            response = rq.sendPathReq();

        } catch (ResourceNotFoundException rex) {
            System.out.println(rex.getMessage());
            goFurther = false;
        } catch (BadRequestException e) {
            System.out.println(e.getMessage());
            goFurther = false;
        }
        if (goFurther) {
            ObjectMapper mapper = new ObjectMapper().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.registerModule(new JavaTimeModule());
            try {
                moiKlienci = mapper.readValue(response, new TypeReference<List<Klient>>(){});
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }       
        return moiKlienci;
    }

    private List<ProduktDTO> getProdukty() {
        List<ProduktDTO> mojeProdukty = new ArrayList<>();
        RequestController rq = new RequestController("/produkt", 0);

        String response = "";
        boolean goFurther = true;

        try {
            response = rq.sendPathReq();

        } catch (ResourceNotFoundException rex) {
            System.out.println(rex.getMessage());
            goFurther = false;
        } catch (BadRequestException e) {
            System.out.println(e.getMessage());
            goFurther = false;
        }
        if (goFurther) {
            ObjectMapper mapper = new ObjectMapper().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.registerModule(new JavaTimeModule());
            try {
                mojeProdukty = mapper.readValue(response, new TypeReference<List<ProduktDTO>>(){});
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }       
        return mojeProdukty;
    }
    
    private boolean deleteDelivery(int delId) {
        RequestController rq = new RequestController("/dostawa/delete/" + Integer.toString(delId), 3);
        String resp = "";

        try {
            resp = rq.sendPathReq();

            if (resp.equals("Dostawa o ID " + Integer.toString(delId) + " została usunięta.")) {
                return true;
            }
        } catch (BadRequestException bre) {
            System.out.println("deleteDelivery: " + bre.getMessage());
            return false;
        }

        return false;
    }

    private boolean deletePackage(int packId) {
        RequestController rq = new RequestController("/paczka/delete/" + packId, 3);

        try {
            rq.sendPathReq();

        } catch (ResourceNotFoundException rex) {
            System.out.println("deleteDelivery: " + rex.getMessage()); 
        } catch (BadRequestException bre) {
            System.out.println("deleteDelivery: " + bre.getMessage());
            return false;
        }

        return true;
    }

    private boolean deleteClient(int clientId) {
        RequestController rq = new RequestController("/klient/" + clientId, 3);

        try {
            rq.sendPathReq();

        } catch (ResourceNotFoundException rex) {
            System.out.println("deleteDelivery: " + rex.getMessage()); 
        } catch (BadRequestException bre) {
            System.out.println("deleteDelivery: " + bre.getMessage());
            return false;
        }

        return true;
    }

    private boolean deleteProduct(int prdId) {
        RequestController rq = new RequestController("/produkt/" + prdId, 3);

        try {
            rq.sendPathReq();

        } catch (ResourceNotFoundException rex) {
            System.out.println("deleteDelivery: " + rex.getMessage()); 
        } catch (BadRequestException bre) {
            System.out.println("deleteDelivery: " + bre.getMessage());
            return false;
        }

        return true;
    }

    private boolean deleteVehicle(int vehicleId) {
        RequestController rq = new RequestController("/pojazd/" + vehicleId, 3);

        try {
            rq.sendPathReq();
            
        } catch (ResourceNotFoundException rex) {
            System.out.println("deleteVehicle: " + rex.getMessage());
        } catch (BadRequestException bre) {
            System.out.println("deleteVehicle: " + bre.getMessage());
            return false;
        }

        return true;
    }

    public void refreshAllData(Button refreshButton) {

        refreshButton.setDisable(true);

        this.produktyList.setDisable(true);
        this.paczkiList.setDisable(true);
        this.pojazdyList.setDisable(true);
        this.dostawyList.setDisable(true);
        this.klienciList.setDisable(true);

        this.produktyList.getChildren().clear();
        this.paczkiList.getChildren().clear();
        this.pojazdyList.getChildren().clear();
        this.dostawyList.getChildren().clear();
        this.klienciList.getChildren().clear();

        List<ProduktDTO> updatedProdukty = getProdukty();
        List<PaczkaDTO> updatedPaczki = getPaczki();
        List<Pojazd> updatedPojazdy = getPojazdy();
        List<DostawaDTO> updatedDostawy = getDostawy();
        List<Klient> updatedKlienci = getKlienci();

        this.produkty = updatedProdukty;
        this.paczki = updatedPaczki;
        this.pojazdy = updatedPojazdy;
        this.dostawy = updatedDostawy;
        this.klienci = updatedKlienci;

        produkty.forEach(produkt -> {
            produktyList.getChildren().add(createProduktItem(produkt, refreshButton));
        });

        paczki.forEach(paczka -> {
            paczkiList.getChildren().add(createPackageItem(paczka.getIdPaczki(), paczka, refreshButton));
        });

        pojazdy.forEach(pojazd -> {
            pojazdyList.getChildren().add(createVehicleItem(pojazd.getIdPojazdu(), pojazd, refreshButton));
        });

        dostawy.forEach(dostawa -> {
            dostawyList.getChildren().add(createDeliveryItem(dostawa.getIdDostawy(), dostawa, refreshButton));
        });

        klienci.forEach(klient -> {
            klienciList.getChildren().add(createKlientItem(klient, refreshButton));
        });

        this.produktyList.setDisable(false);
        this.paczkiList.setDisable(false);
        this.pojazdyList.setDisable(false);
        this.dostawyList.setDisable(false);
        this.klienciList.setDisable(false);

        refreshButton.setDisable(false);
    }

    private boolean getMyName() {
        String response = "";
        RequestController rq = new RequestController("/pracownik/" + Integer.toString(loggedUserId), 1);
        
        try {
            response = rq.sendPathReq();    
        } catch (BadRequestException e) {
            System.out.println("getMyName: " + e.getMessage());
            return false;
        }
        ObjectMapper mapper = new ObjectMapper().configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            Pracownik tmp = mapper.readValue(response, new TypeReference<Pracownik>(){});
            this.loggedUserName = tmp.getImie();
            this.loggedUserSurname = tmp.getNazwisko();
        } catch (IOException ex) {
            System.out.println("getMyName: " + ex.getMessage());
        }
        
        return true;
    }
}