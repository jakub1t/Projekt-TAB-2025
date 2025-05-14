package com.polsl.firmakurierska;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.ResponseEntity;

import com.polsl.firmakurierska.controller.*;
import com.polsl.firmakurierska.dto.*;

import com.polsl.firmakurierska.exception.BadRequestException;
import com.polsl.firmakurierska.model.Konto;
import com.polsl.firmakurierska.model.Pojazd;
import com.polsl.firmakurierska.model.Stanowisko;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "com.polsl.firmakurierska")
@EnableJpaRepositories("com.polsl.firmakurierska.repository")
@EntityScan("com.polsl.firmakurierska.model")
@ComponentScan({"com.polsl.firmakurierska.repository","com.polsl.firmakurierska.controller","com.polsl.firmakurierska.exception"})
public class FirmaKurierskaApplication implements CommandLineRunner {

    @Autowired
    private KontoController kontoController;
    @Autowired
    private PracownikController pracownikController;
    @Autowired
    private PrawoJazdyController prawoJazdyController;
    @Autowired
    private StanowiskoController stanowiskoController;

    @Autowired
    private PojazdController pojazdController;
    @Autowired
    private DostawaController dostawaController;
    @Autowired
    private PaczkaController paczkaController;

    /*@Autowired
    private KlientController klientController;
    @Autowired
    private ProducentController producentController;
    @Autowired
    private ProduktController produktController;*/

	public static void main(String[] args) {
		// SpringApplication.run(FirmaKurierskaApplication.class, args);
		new SpringApplicationBuilder(FirmaKurierskaApplication.class).headless(false).run(args);
	}

	// Method that runs a test window
	@Override
    public void run(String... args) {

		// Get list of all accounts
		// List<Konto> accounts = kontoController.getAllKonta();
		List<Konto> accounts = getAllAccounts();
		// Print ids to console
		System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
		accounts.forEach(account -> System.out.println(account.getIdKonta()));
		System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");

		// ---------------------------------------------------------------------- //
		// Here some tests for database methods:

		Konto konto = getAccountByLogin("user1");
		System.out.println(konto.getIdKonta());

		List<String> data = getAccountDataForAdminPanel(konto.getIdKonta().toString());

		System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
		System.out.println("Imię: " + data.get(0));
		System.out.println("Nazwisko: " + data.get(1));
		System.out.println("PESEL: " + data.get(2));
		System.out.println("Stanowisko: " + data.get(3));
		System.out.println("Kategoria prawa jazdy: " + data.get(4));
		System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");

		// deleteAccountByLogin("user2"); // Nie działa

		accounts = getAllAccounts();
		System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
		accounts.forEach(account -> System.out.println(account.getIdKonta()));
		System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");

		// ---------------------------------------------------------------------- //
    }

	public List<Konto> getAllAccounts() {
		return this.kontoController.getAllKonta();
	}

	public Konto getAccountByLogin(String login) {
		return this.kontoController.getKontoByLogin(login);
	}

	public void deleteAccountByLogin(String login) {
		this.kontoController.deleteKonto(login);
	}

	/**
	 * Gets worker data into list with order: {name, surname, PESEL, position, dirving license}.
	 * @param accountId - id of the account that the data is collected.
	 * @return - account data in a list.
	 */
	public List<String> getAccountDataForAdminPanel(String accountId) {
		List<String> accountData = new ArrayList<>();
		int accountIdAsInt = 0;
		try {
			accountIdAsInt = Integer.parseInt(accountId);
		} catch (NumberFormatException e) {
            throw new BadRequestException("ID musi być liczbą całkowitą: " + accountId);
        }

		// Get worker name, surname and PESEL
		ResponseEntity<PracownikDTO> pracownik_rpent = pracownikController.getPracownikById(accountId);
		PracownikDTO pracownik = pracownik_rpent.getBody();

		accountData.add(pracownik.getImie());
		accountData.add(pracownik.getNazwisko());
		accountData.add(pracownik.getPesel());

		// Get position
		Stanowisko stanowisko = stanowiskoController.getStanowiskoById(accountIdAsInt);
		accountData.add(stanowisko.getNazwaStanowiska());

		// Get driving license
		ResponseEntity<PrawoJazdyDTO> prawoJazdy_rpent = prawoJazdyController.getPrawoJazdyById(accountId);
		PrawoJazdyDTO prawoJazdy = prawoJazdy_rpent.getBody();

		accountData.add(prawoJazdy.getKategoria());

		return accountData;
	}

	public List<PaczkaDTO> getPaczkiForAccount(String accountId) {
		List<PaczkaDTO> paczki = new ArrayList<>();

		paczki = paczkaController.getAllPaczki();
		paczki.forEach(paczka -> 
			{
				System.out.println(paczka.getWagaPaczki());
				System.out.println(paczka.getIdPaczki());
			}
		);

		return paczki;
	}

	public List<DostawaDTO> getDostawyForAccount(String accountId) {
		List<DostawaDTO> dostawy = new ArrayList<>();

		dostawaController.getAllDostawy().forEach(dostawy::add);

		return dostawy;
	}

	public List<Pojazd> getPojazdyForAccount(String accountId) {
		List<Pojazd> pojazdy = new ArrayList<>();

		pojazdController.getAll().forEach(pojazdy::add);

		return pojazdy;
	}

}
