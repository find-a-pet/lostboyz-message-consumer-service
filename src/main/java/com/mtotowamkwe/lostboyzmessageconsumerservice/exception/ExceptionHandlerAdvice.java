package com.mtotowamkwe.lostboyzmessageconsumerservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ResponseBody
    @ExceptionHandler(DequeuedMessageWasNotEmailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String badRestTemplateUrlHandler(DequeuedMessageWasNotEmailedException dmwnex) {
        return dmwnex.getMessage();
    }

}
