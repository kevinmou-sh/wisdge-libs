package com.wisdge.common.sms;

public class SMSSendFailedException extends Exception {
    private static final long serialVersionUID = 1L;

    public SMSSendFailedException(String message) {
        super(message);
    }
}
