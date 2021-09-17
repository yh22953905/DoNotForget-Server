package com.hungrybrothers.alarmforsubscription.exception;

public class AccountAlreadyExistsException extends RuntimeException {
	private final ErrorCode errorCode;

	public AccountAlreadyExistsException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
