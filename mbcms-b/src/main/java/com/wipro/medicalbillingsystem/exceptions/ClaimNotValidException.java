package com.wipro.medicalbillingsystem.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class ClaimNotValidException extends ResponseStatusException {

	
	private static final long serialVersionUID = 1L;

	public ClaimNotValidException(HttpStatusCode status, String reason) {
		super(status, reason);

	}

}
