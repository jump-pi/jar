package com.jumppi.frwk.jump;

import com.jumppi.frwk.util.SignalException;

public class InvalidApiKeyException extends SignalException {

	public InvalidApiKeyException() {
		super (400, "Invalid API key");
	}
	
	public InvalidApiKeyException(String msg) {
		super (400, msg);
	}
	
}

