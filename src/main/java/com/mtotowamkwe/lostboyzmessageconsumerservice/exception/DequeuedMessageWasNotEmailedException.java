package com.mtotowamkwe.lostboyzmessageconsumerservice.exception;

public class DequeuedMessageWasNotEmailedException extends Throwable {

    public DequeuedMessageWasNotEmailedException(String errorMessage) {
        super(errorMessage);
    }

}
