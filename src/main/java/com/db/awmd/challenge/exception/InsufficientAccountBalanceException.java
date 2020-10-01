package com.db.awmd.challenge.exception;

public class InsufficientAccountBalanceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5918793171089455728L;

	public InsufficientAccountBalanceException(String message) {
		super(message);
	}
}
