package com.polsl.firmakurierska.exception;
//Jest używana, gdy klient przesyła nieprawidłowe dane (np. niepoprawny format ID).
public class BadRequestException extends RuntimeException {
	 private static final long serialVersionUID = 1L;
	 
	public BadRequestException(String message) {
        super(message);
    }
}