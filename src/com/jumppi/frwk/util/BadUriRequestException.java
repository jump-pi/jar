package com.jumppi.frwk.util;

public class BadUriRequestException extends SignalException {
	public BadUriRequestException(String msg) {
		super(99, msg);
	}
}
