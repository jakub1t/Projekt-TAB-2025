package com.polsl.firmakurierska.exception;
//Jest używana, gdy klient przesyła nieprawidłowe dane (np. niepoprawny format ID).
public class BadRequestException extends RuntimeException {
	 private static final long serialVersionUID = 2L;
	 
	public BadRequestException(String message) {
        super(message);
    }
}