package com.jumppi.frwk.util;


public class SignalException extends RuntimeException {

	protected int code = 0;
		
	public SignalException (Throwable e) {
		super(e.getMessage(), e);
		this.code = 99;
	}
	
	public SignalException (String msg) {
		super(msg);
		this.code = 99;
	}

	public SignalException (int code, String msg) {
		super(msg);
		this.code = code;
	}

	public SignalException (String msg, Throwable e) {
		super(msg, e);
		this.code = 99;
	}
	
	public SignalException (int code, String msg, Throwable e) {
		super(msg, e);
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}

	public String getJsonTranslatedMessage(String lang) {
		String res = "";
		String msg = "";
		if (Util.nvl(lang).equals("")) {
			msg = this.getMessage();
		} else {
			// Translate
			msg = this.getLocalizedMessage();
		}
		res = msg;

		return res;
	}

	@Override
    public String toString() {
        String msg = super.getMessage() != null ? super.getMessage() : "";
        return msg;
//        Throwable cause = getCause();
//        return msg.replace("\"", "\\\"") + "\"" + ", \"_cause\": \"" + (cause != null ? cause.toString().replace("\"", "\\\"") : "itself");
    }
	
}


