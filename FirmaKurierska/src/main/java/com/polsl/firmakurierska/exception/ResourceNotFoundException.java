package com.polsl.firmakurierska.exception;
//Jest używana, gdy nie można znaleźć zasobu, np. pracownika lub pojazdu.

public class ResourceNotFoundException extends RuntimeException {
	//możliwe serializować wyjątki np. do pliku
	private static final long serialVersionUID = 1L;
	
	public ResourceNotFoundException(String message) {
        super(message);
    }
}