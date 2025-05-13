package com.polsl.firmakurierska;

import org.springframework.boot.SpringApplication;
import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.polsl.firmakurierska.controller.KontoController;
import com.polsl.firmakurierska.model.Konto;

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

	public static void main(String[] args) {
		// SpringApplication.run(FirmaKurierskaApplication.class, args);
		new SpringApplicationBuilder(FirmaKurierskaApplication.class).headless(false).run(args);
	}

	// Method that runs a test window
	@Override
    public void run(String... args) {

		// Get list of all accounts
		List<Konto> accounts = kontoController.getAllKonto();
		// Print logins to console
		accounts.forEach(account -> System.out.println(account.getLogin()));

		// Window stuff here
        JFrame frame = new JFrame("Spring Boot Swing App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,300);
        JPanel panel = new JPanel(new BorderLayout());
		JTextField text = new JTextField("Some text blah bla blah...");
		panel.add(text, BorderLayout.BEFORE_FIRST_LINE);

		String text_temp = "";
		for (Konto konto : accounts) {
			text_temp += "Login: " + konto.getLogin() + " ||| Haslo: " + konto.getHaslo() + "\n\r";
		}

		JTextArea textField = new JTextArea(text_temp);
		panel.add(textField, BorderLayout.CENTER);
        frame.setContentPane(panel);
        frame.setVisible(true);
    }
}
