package com.hungrybrothers.alarmforsubscription.exception;

import javax.persistence.EntityExistsException;

public class CustomEntityExistsException extends EntityExistsException {
	private final ErrorCode errorCode;

	public CustomEntityExistsException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
