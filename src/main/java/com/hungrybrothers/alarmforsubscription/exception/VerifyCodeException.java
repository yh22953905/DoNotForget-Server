package com.hungrybrothers.alarmforsubscription.exception;

public class VerifyCodeException extends RuntimeException {
	private final ErrorCode errorCode;

	public VerifyCodeException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
