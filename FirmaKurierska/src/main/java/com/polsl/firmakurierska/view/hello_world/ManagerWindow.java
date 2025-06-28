package com.polsl.firmakurierska.view.hello_world;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.hateoas.Link;

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
import com.polsl.firmakurierska.model.Producent;
import com.polsl.firmakurierska.view.hello_world.PackageDescription.Product;

public class ManagerWindow extends Application {

    private int loggedUserId = 0;
    private String loggedUserName = "Imię";
    private String loggedUserSurname = "Nazwisko";

    private VBox paczkiList;
    private VBox pojazdyList;
    private VBox dostawyList;

    // Selection options for filtering deliveries: 
    // 0 - all, 1 - completed, 2 - not completed, any other value - none
    private int filterDeliveriesOption = 0; 

    public void open(int userId) {
        this.loggedUserId = userId;
        getMyName();
        this.start(new Stage());
    }

    @Override
    public void start(Stage stage) {
        Label welcomeLabel = new Label("Witaj " + loggedUserName + " " + loggedUserSurname + "!");
        welcomeLabel.setStyle("-fx-font-size: 12px;");
        Button refreshBtn = new Button("Odśwież Dane");
        refreshBtn.setPrefWidth(200);

        VBox welBox = new VBox(5, welcomeLabel, refreshBtn);
        welBox.setAlignment(Pos.CENTER);

        refreshBtn.setOnAction(e -> {
            refreshAllData(refreshBtn);
        });
        
        // ===== KOL 1: PACZKI =====
        paczkiList = new VBox(5);
        paczkiList.setPadding(new Insets(5));
        ScrollPane paczkiScroll = new ScrollPane(paczkiList);
        paczkiScroll.setFitToWidth(true);
        paczkiScroll.setPrefHeight(300);
        
        List<PaczkaDTO> paczki = getPaczki();

        paczki.forEach(paczka -> {
            paczkiList.getChildren().add(createPackageItem(paczka.getIdPaczki(), paczka));
        });

        Button dodajPaczkeBtn = new Button("Dodaj paczkę");
        dodajPaczkeBtn.setOnAction(e -> new PackageFormWindow().show());
        VBox paczkiCol = buildColumn("Paczki", paczkiScroll, dodajPaczkeBtn, "#f4f4f4");

        // ===== KOL 2: POJAZDY =====
        pojazdyList = new VBox(5);
        pojazdyList.setPadding(new Insets(5));
        ScrollPane pojazdyScroll = new ScrollPane(pojazdyList);
        pojazdyScroll.setFitToWidth(true);
        pojazdyScroll.setPrefHeight(300);

        List<Pojazd> pojazdy = getPojazdy();

        pojazdy.forEach(pojazd -> {
            pojazdyList.getChildren().add(createVehicleItem(pojazd.getIdPojazdu(), pojazd));
        });

        Button dodajPojazdBtn = new Button("Dodaj pojazd");
        dodajPojazdBtn.setOnAction(e -> new VehicleFormWindow().show(this, refreshBtn));
        VBox pojazdyCol = buildColumn("Pojazdy", pojazdyScroll, dodajPojazdBtn, "#eaeaea");

        // ===== KOL 3: DOSTAWY =====
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

        dostawyList = new VBox(5);
        dostawyList.setPadding(new Insets(5));
        ScrollPane dostawyScroll = new ScrollPane(dostawyList);
        dostawyScroll.setFitToWidth(true);
        dostawyScroll.setPrefHeight(300);

        List<DostawaDTO> dostawy = getDostawy();

        dostawy.forEach(dostawa -> {
            dostawyList.getChildren().add(createDeliveryItem(dostawa.getIdDostawy(), dostawa));
        });

        Button dodajDostaweBtn = new Button("Dodaj dostawę");

        VBox dostawyCol = buildColumn("Dostawy", dostawyScroll, dodajDostaweBtn, "#f4f4f4", comboBox);

        // ===== KOL 4: RAPORTY =====
        Label raportLabel = new Label("Raporty");
        raportLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        raportLabel.setMaxWidth(Double.MAX_VALUE);
        raportLabel.setAlignment(Pos.CENTER);

        Button generujRaportBtn = new Button("Generuj raport");
        generujRaportBtn.setOnAction(e -> new RaportFormWindow().show());
        HBox raportBox = new HBox(generujRaportBtn);
        raportBox.setAlignment(Pos.CENTER);
        raportBox.setPadding(new Insets(10));

        VBox raportCol = new VBox(10, raportLabel, raportBox);
        raportCol.setPadding(new Insets(10));
        raportCol.setPrefWidth(200);
        raportCol.setStyle("-fx-background-color: #eaeaea;");

        // ===== GŁÓWNY UKŁAD =====
        HBox dataBox = new HBox(10, paczkiCol, pojazdyCol, dostawyCol, raportCol);
        VBox root = new VBox(10, welBox, dataBox);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 850, 450);
        stage.setScene(scene);
        stage.setTitle("Administrator Panel");
        stage.show();
    }

    private VBox buildColumn(String title, ScrollPane content, Button addButton, String bgColor) {
        Label header = new Label(title);
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        header.setMaxWidth(Double.MAX_VALUE);
        HBox headerBox = new HBox(header);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(10));

        HBox addBox = new HBox(addButton);
        addBox.setAlignment(Pos.CENTER);
        addBox.setPadding(new Insets(5));

        VBox col = new VBox(10, headerBox, content, addBox);
        col.setPadding(new Insets(10));
        col.setPrefWidth(200);
        col.setStyle("-fx-background-color: " + bgColor + ";");
        return col;
    }

    private VBox buildColumn(String title, ScrollPane content, Button addButton, String bgColor, ComboBox<String> comboBox) {
        Label header = new Label(title);
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        header.setMaxWidth(Double.MAX_VALUE);
        HBox headerBox = new HBox(header, comboBox);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(10));

        HBox addBox = new HBox(addButton);
        addBox.setAlignment(Pos.CENTER);
        addBox.setPadding(new Insets(5));

        VBox col = new VBox(10, headerBox, content, addBox);
        col.setPadding(new Insets(10));
        col.setPrefWidth(200);
        col.setStyle("-fx-background-color: " + bgColor + ";");
        return col;
    }

    private HBox createPackageItem(int paczkaId, PaczkaDTO paczkaData) {
        String name = "ID paczki: " + paczkaId;

        Button itemBtn = new Button(name);
        itemBtn.setPrefWidth(140);
        itemBtn.setOnAction(e -> {

            int klientId = paczkaData.getKlientId();
            List<Integer> procuktIds = paczkaData.getProduktIds();
            final List<Product> productsForDisplay = new ArrayList<>();
            RequestController rq_helper = new RequestController("", 0);

            Klient client = getKlientById(klientId);

            procuktIds.forEach(id -> {
                ProduktDTO tempProdukt = getProduktById(id);

                Link producentLink = tempProdukt.getLink("producent").get();

                String producentHref = producentLink.getHref();

                String producentId = rq_helper.returnValueFromHref("producent", producentHref);

                Producent producentData = getProducentById(Integer.parseInt(producentId));
                    
                
                productsForDisplay.add(new Product(tempProdukt.getNazwaProduktu(), 
                    Double.toString(tempProdukt.getWaga()) + " kg", 
                    tempProdukt.getKategoriaProd(), 
                    tempProdukt.getNrSeryjny(), 
                    producentData.getNazwaProducenta()
                ));
            });

            new PackageDescription().show(
                client.getImieK(),
                client.getNazwiskoK(),
                paczkaData.getWagaPaczki(),
                productsForDisplay
            );
        });
        Button delBtn = new Button("X");
        delBtn.setOnAction(e -> {
            if (deletePackage(paczkaId)) {
                paczkiList.getChildren().removeIf(node -> {
                if (node instanceof HBox hbox) {
                    Button b = (Button) hbox.getChildren().get(0);
                    return b.getText().equals(name);
                }
                return false;
                });
            }
        });
        HBox box = new HBox(5, itemBtn, delBtn);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private HBox createVehicleItem(int pojazdId, Pojazd pojazdData) {
        String name = String.format("ID: %d| ", pojazdId, pojazdData.getMarka());

        Button itemBtn = new Button(name);
        itemBtn.setPrefWidth(140);
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
        Button delBtn = new Button("X");
        delBtn.setOnAction(e ->{
            if (deleteVehicle(pojazdId)) {;    
                pojazdyList.getChildren().removeIf(node -> {
                if (node instanceof HBox hbox) {
                    Button b = (Button) hbox.getChildren().get(0);
                    return b.getText().equals(name);
                }
                return false;
                });
            }
        });
        HBox box = new HBox(5, itemBtn, delBtn);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private HBox createDeliveryItem(int dostawaId, DostawaDTO dostawaData) {
        String name = "ID dostawy: " + dostawaId;
        RequestController rq_helper = new RequestController("", 0);


        Button itemBtn = new Button(name);
        itemBtn.setPrefWidth(140);
        itemBtn.setOnAction(e -> {
            Link pracownikLink = dostawaData.getLink("pracownik").get();

            String pracownikHref = pracownikLink.getHref();

            String pracownikId = rq_helper.returnValueFromHref("pracownik", pracownikHref);

            Pracownik pracownikData = getPracownikById(Integer.parseInt(pracownikId));

            new DeliveryDescription().open(dostawaId, pracownikData.getImie(), pracownikData.getNazwisko());
        });
        Button delBtn = new Button("X");
        delBtn.setOnAction(e -> { 
            if (deleteDelivery(dostawaId)){
                dostawyList.getChildren().removeIf(node -> {
                if (node instanceof HBox hbox) {
                    Button b = (Button) hbox.getChildren().get(0);
                    return b.getText().equals(name);
                }
                return false;
                });
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

    private ProduktDTO getProduktById(int productId) {
        ProduktDTO produkt = new ProduktDTO();
        String response = "";
        RequestController rq = new RequestController("/produkt/" + productId, 0);

        try {
            response = rq.sendPathReq();
        } catch (BadRequestException e) {
            System.out.println(e.getMessage());
        }
        
        try {
            // Object mapper doesnt work so this thing below is used instead...

            // Map response to JSON
            JSONObject produktJSON = new JSONObject(response);

            // Set produkt object fields that are easy to get
            produkt.setIdProduktu(produktJSON.getInt("idProduktu"));
            produkt.setNrSeryjny(produktJSON.getString("nrSeryjny"));
            produkt.setKategoriaProd(produktJSON.getString("kategoriaProd"));
            produkt.setNazwaProduktu(produktJSON.getString("nazwaProduktu"));
            produkt.setWaga(produktJSON.getDouble("waga"));

            // The fun part of adding DTO hrefs to the produkt object
            String temp = produktJSON.getString("_links");
            JSONObject linksJ = new JSONObject(temp);

            JSONObject hrefJ1 = new JSONObject(linksJ.getString("self"));
            produkt.add(Link.of(hrefJ1.getString("href"), "self"));

            JSONObject hrefJ2 = new JSONObject(linksJ.getString("paczka"));
            produkt.add(Link.of(hrefJ2.getString("href"), "paczka"));

            JSONObject hrefJ3 = new JSONObject(linksJ.getString("producent"));
            produkt.add(Link.of(hrefJ3.getString("href"), "producent"));

        } catch (JSONException jex) {
            System.out.println(jex.toString());
            jex.printStackTrace();
        }

        return produkt;
    }

    private Producent getProducentById(int producentId) {
        Producent producent = new Producent();
        String response = "";
        RequestController rq = new RequestController("/producent/" + producentId, 0);

        try {
            response = rq.sendPathReq();
        } catch (BadRequestException e) {
            System.out.println(e.getMessage());
        }
        
        ObjectMapper mapper = new ObjectMapper().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule());
        try {
            producent = mapper.readValue(response, new TypeReference<Producent>(){});
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return producent;
    }

    private boolean deleteDelivery(int delId) {
        RequestController rq = new RequestController("/dostawa/delete/" + Integer.toString(delId), 3);
        String resp = "";

        try {
            resp = rq.sendPathReq();

            if (resp.equals("Dostawa o ID " + Integer.toString(delId) + " została usunięta.")) {
                System.out.println("Usunięto dostawę");
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

            System.out.println("Usunięto paczkę");
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
            
            System.out.println("Usunięto pojazd");
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

        this.paczkiList.setDisable(true);
        this.pojazdyList.setDisable(true);
        this.dostawyList.setDisable(true);

        this.paczkiList.getChildren().clear();
        this.pojazdyList.getChildren().clear();
        this.dostawyList.getChildren().clear();

        List<PaczkaDTO> paczki = getPaczki();

        paczki.forEach(paczka -> {
            paczkiList.getChildren().add(createPackageItem(paczka.getIdPaczki(), paczka));
        });

        List<Pojazd> pojazdy = getPojazdy();

        pojazdy.forEach(pojazd -> {
            pojazdyList.getChildren().add(createVehicleItem(pojazd.getIdPojazdu(), pojazd));
        });

        List<DostawaDTO> dostawy = getDostawy();

        dostawy.forEach(dostawa -> {
            dostawyList.getChildren().add(createDeliveryItem(dostawa.getIdDostawy(), dostawa));
        });

        this.paczkiList.setDisable(false);
        this.pojazdyList.setDisable(false);
        this.dostawyList.setDisable(false);

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