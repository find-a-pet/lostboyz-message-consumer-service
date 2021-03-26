package com.mtotowamkwe.lostboyzmessageconsumerservice.api.impl;

import com.mtotowamkwe.lostboyzmessageconsumerservice.api.MessageConsumer;
import com.mtotowamkwe.lostboyzmessageconsumerservice.exception.DequeuedMessageWasNotEmailedException;
import com.mtotowamkwe.lostboyzmessageconsumerservice.model.UserEmailTemplate;
import com.mtotowamkwe.lostboyzmessageconsumerservice.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

import static org.springframework.http.HttpStatus.Series.*;

@Component
public class MessageConsumerImpl implements MessageConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(MessageConsumerImpl.class);

    private RestTemplate template = new RestTemplate();

    @RabbitListener(queues = "account.verification.emails")
    public void receive(String payload) throws
            RestClientException, DequeuedMessageWasNotEmailedException {
        String[] user = payload.split(",");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        UserEmailTemplate userEmailTemplate = new UserEmailTemplate();
        userEmailTemplate.setEmail(user[0]);
        userEmailTemplate.setCode(user[1]);

        try {
            ResponseEntity<UserEmailTemplate> responseEntity = template
                    .postForEntity(getUri(Constants.LOSTBOYZ_EMAIL_SERVICE_URL),
                            new HttpEntity<>(userEmailTemplate, headers),
                            UserEmailTemplate.class);

            HttpStatus status = responseEntity.getStatusCode();

            if (status.equals(SUCCESSFUL)) {
                LOG.info("Verification code " + userEmailTemplate.getCode() + " sent to "
                        + userEmailTemplate.getEmail());
            } else if (status.equals(CLIENT_ERROR) || status.equals(SERVER_ERROR)){
                LOG.error("DequeuedMessageWasNotEmailedException @ receive()", responseEntity);
                throw new DequeuedMessageWasNotEmailedException("Email not sent:");
            }
        } catch (RestClientException rce) {
            LOG.error("RestClientException @ receive():", rce);
            throw rce;
        }

    }

    URI getUri(String emailServiceUrl) throws DequeuedMessageWasNotEmailedException {
        try {
            URI url = new URI(emailServiceUrl);
            return url;
        } catch (URISyntaxException usex) {
            LOG.error("URISyntaxException @ getUri():", usex);
            throw new DequeuedMessageWasNotEmailedException("Email not sent:\n" + usex.getMessage());
        }
    }
}