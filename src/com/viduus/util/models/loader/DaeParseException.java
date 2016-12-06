package com.viduus.util.models.loader;

import java.io.IOException;

/**
 * This exception is thrown when a field does not exist that should with the given Collada
 * version number.
 * 
 * @author Ethan Toney
 */
public class DaeParseException extends IOException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8230008053326046298L;

	/**
	 * This exception is thrown for parsing errors.
	 * 
	 * @param message - (String) Message for error.
	 */
	public DaeParseException(String message) {
		super(message);
	}

}
