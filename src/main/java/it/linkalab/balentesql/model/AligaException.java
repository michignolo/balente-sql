package it.linkalab.balentesql.model;

import java.util.List;

/**
 * Exception thrown by BalenteSQL when an error occurs during query
 * parsing/execution.
 * 
 * @author Damiano Casula
 *
 */
public class AligaException extends RuntimeException {

	public AligaException(List<String> expectedTokens, String actualToken) {
		super("Expected one of " + expectedTokens + " but got [" + actualToken + "]");
	}

	public AligaException(String expectedToken, String token) {
		super("Expected [" + expectedToken + "] but got [" + token + "]");
	}

	public AligaException(String message) {
		super(message);
	}

	public AligaException(Exception e) {
		super(e);
	}

}
