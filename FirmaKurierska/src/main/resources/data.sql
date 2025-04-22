-- Klient
INSERT INTO klient (imiek, nazwiskok) VALUES ('Piotr', 'Zielinski');	
INSERT INTO klient (imiek, nazwiskok) VALUES ('Anna', 'Nowak');
INSERT INTO klient (imiek, nazwiskok) VALUES ('Gabriel', 'Dziubak');

-- Procudent
INSERT INTO producent (nazwa_producenta) VALUES ('Samsung');
INSERT INTO producent (nazwa_producenta) VALUES ('LG');
INSERT INTO producent (nazwa_producenta) VALUES ('Sony');
INSERT INTO producent (nazwa_producenta) VALUES ('Lenovo');
INSERT INTO producent (nazwa_producenta) VALUES ('Asus');

-- Stanowisko
INSERT INTO stanowisko (nazwa_stanowiska) VALUES ('Kierowca');
INSERT INTO stanowisko (nazwa_stanowiska) VALUES ('Magazynier');
INSERT INTO stanowisko (nazwa_stanowiska) VALUES ('Kurjer');
INSERT INTO stanowisko (nazwa_stanowiska) VALUES ('Pracownik biurowy');
INSERT INTO stanowisko (nazwa_stanowiska) VALUES ('Manager');

-- Prawojazdy
INSERT INTO prawojazdy (kategoria) VALUES ('B');
INSERT INTO prawojazdy (kategoria) VALUES ('C');
INSERT INTO prawojazdy (kategoria) VALUES ('D');
INSERT INTO prawojazdy (kategoria) VALUES ('A');
INSERT INTO prawojazdy (kategoria) VALUES ('E');

-- Pojazd
INSERT INTO pojazd (pojemnosc, marka, model, nr_rejestr, typ_pojazdu) 
VALUES (2500.5, 'Mercedes', 'Sprinter', 'WAW12345', 'Dostawczy');
INSERT INTO pojazd (pojemnosc, marka, model, nr_rejestr, typ_pojazdu) 
VALUES (2000.0, 'Volkswagen', 'Transporter', 'WAW54321', 'Dostawczy');
INSERT INTO pojazd (pojemnosc, marka, model, nr_rejestr, typ_pojazdu) 
VALUES (1500.0, 'Ford', 'Transit', 'KRA11111', 'Dostawczy');
INSERT INTO pojazd (pojemnosc, marka, model, nr_rejestr, typ_pojazdu) 
VALUES (1600.0, 'Fiat', 'Ducato', 'KRA22222', 'Dostawczy');
INSERT INTO pojazd (pojemnosc, marka, model, nr_rejestr, typ_pojazdu) 
VALUES (1200.0, 'Renault', 'Master', 'POZ33333', 'Dostawczy');

-- Konto
INSERT INTO konto (haslo, login) VALUES ('password123', 'user1');
INSERT INTO konto (haslo, login) VALUES ('password456', 'user2');
INSERT INTO konto (haslo, login) VALUES ('password789', 'user3');
INSERT INTO konto (haslo, login) VALUES ('securepass', 'admin1');
INSERT INTO konto (haslo, login) VALUES ('adminpass', 'admin2');

-- Pracownik
INSERT INTO pracownik (konto_id, stanowisko_id, imie, nazwisko, pesel) 
VALUES (1, 1, 'Jan', 'Kowalski', '12345678901');  -- Kierowca
INSERT INTO pracownik (konto_id, stanowisko_id, imie, nazwisko, pesel) 
VALUES (2, 2, 'Anna', 'Nowak', '98765432109');  -- Magazynier
INSERT INTO pracownik (konto_id, stanowisko_id, imie, nazwisko, pesel) 
VALUES (3, 3, 'Marek', 'Wiśniewski', '45612378901');  -- Kurjer
INSERT INTO pracownik (konto_id, stanowisko_id, imie, nazwisko, pesel) 
VALUES (4, 4, 'Katarzyna', 'Zielińska', '32165498701');  -- Pracownik biurowy
INSERT INTO pracownik (konto_id, stanowisko_id, imie, nazwisko, pesel) 
VALUES (5, 5, 'Piotr', 'Wójcik', '65498732109');  -- Manager

-- Dostawa
INSERT INTO dostawa (data_wyruszenia, pojazd_id, pracownik_id, termin, punkta, punktb, status)
VALUES ('2025-04-10', 1, 1, '2025-04-11', 'Katowice', 'Warszawa', 'W_TRAKCIE'),
('2025-04-09', 2, 2, '2025-04-10', 'Kraków', 'Wrocław', 'ZREALIZOWANA'),
('2025-04-15', 1, 1, '2025-04-16', 'Gliwice', 'Poznań', 'PLANOWANA'),
('2025-04-14', 2, 2, '2025-04-15', 'Warszawa', 'Łódź', 'W_TRAKCIE'),
('2025-04-16', 1, 1, '2025-04-17', 'Zabrze', 'Gdańsk', 'ZREALIZOWANA');

--Paczka
INSERT INTO paczka (waga_paczki, fk_dostawa_id, fk_klient_id) VALUES (5.2, 1, 1);
INSERT INTO paczka (waga_paczki, fk_dostawa_id, fk_klient_id) VALUES (3.8, 2, 1);
INSERT INTO paczka (waga_paczki, fk_dostawa_id, fk_klient_id) VALUES (10.3,3, 2);
INSERT INTO paczka (waga_paczki, fk_dostawa_id, fk_klient_id) VALUES (4.5, 4, 2);
INSERT INTO paczka (waga_paczki, fk_dostawa_id, fk_klient_id) VALUES (6.5, 5, 2);
INSERT INTO paczka (waga_paczki, fk_dostawa_id, fk_klient_id) VALUES (7.8, 1, 3);

--Produkty
INSERT INTO produkt (fk_paczka_id,fk_producent_id, waga, kategoria_prod, nazwa_produktu, nr_seryjny)
VALUES (1,2, 2.5, 'Elektronika', 'Słuchawki bezprzewodowe', 'SN12345');
INSERT INTO produkt (fk_paczka_id,fk_producent_id, waga, kategoria_prod, nazwa_produktu, nr_seryjny)
VALUES (1,3, 1.2, 'Książki', 'Harry Potter', 'SN67890');
INSERT INTO produkt (fk_paczka_id,fk_producent_id, waga, kategoria_prod, nazwa_produktu, nr_seryjny)
VALUES (1,4, 0.5, 'Kosmetyki', 'Krem do rąk', 'SN54321');
INSERT INTO produkt (fk_paczka_id,fk_producent_id, waga, kategoria_prod, nazwa_produktu, nr_seryjny)
VALUES (1,1, 5.0, 'Jedzenie', 'Czekolada', 'Wawel');
INSERT INTO produkt (fk_paczka_id,fk_producent_id, waga, kategoria_prod, nazwa_produktu, nr_seryjny)
VALUES (1,5, 3, 'Zabawka', 'Pluszak', 'Toys');


--pracownik_prawojazdy
INSERT INTO pracownik_prawojazdy (pracownik_id, prawojazdy_id) VALUES (1, 1);
INSERT INTO pracownik_prawojazdy (pracownik_id, prawojazdy_id) VALUES (1, 2);
INSERT INTO pracownik_prawojazdy (pracownik_id, prawojazdy_id) VALUES (2, 1);
INSERT INTO pracownik_prawojazdy (pracownik_id, prawojazdy_id) VALUES (2, 4);
INSERT INTO pracownik_prawojazdy (pracownik_id, prawojazdy_id) VALUES (3, 1);
INSERT INTO pracownik_prawojazdy (pracownik_id, prawojazdy_id) VALUES (4, 2);
INSERT INTO pracownik_prawojazdy (pracownik_id, prawojazdy_id) VALUES (4, 1);
INSERT INTO pracownik_prawojazdy (pracownik_id, prawojazdy_id) VALUES (4, 5);
INSERT INTO pracownik_prawojazdy (pracownik_id, prawojazdy_id) VALUES (5, 3);
INSERT INTO pracownik_prawojazdy (pracownik_id, prawojazdy_id) VALUES (5, 2);

