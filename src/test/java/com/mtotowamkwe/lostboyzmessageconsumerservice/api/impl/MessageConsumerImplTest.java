package com.mtotowamkwe.lostboyzmessageconsumerservice.api.impl;

import com.mtotowamkwe.lostboyzmessageconsumerservice.api.MessageConsumer;
import com.mtotowamkwe.lostboyzmessageconsumerservice.exception.DequeuedMessageWasNotEmailedException;
import com.mtotowamkwe.lostboyzmessageconsumerservice.model.UserEmailTemplate;
import com.mtotowamkwe.lostboyzmessageconsumerservice.util.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringJUnit4ClassRunner.class)
public class MessageConsumerImplTest {

    private String badUrl;

    private String goodUrl;

    private RestTemplate template  = new RestTemplate();

    private MockRestServiceServer server;

    @Mock
    private MessageConsumer consumer;

    @InjectMocks
    private MessageConsumerImpl consumerImpl;

    @Before
    public void setUp() {
        goodUrl = Constants.LOSTBOYZ_EMAIL_SERVICE_URL;
        badUrl = Constants.TEST_BAD_LOSTBOYZ_EMAIL_SERVICE_URL;
        server = MockRestServiceServer.createServer(template);
    }

    @org.junit.Test
    public void testConsumerNotNull() {
        assertNotNull(consumer);
    }

    @org.junit.Test
    public void testConsumerImplNotNull() {
        assertNotNull(consumerImpl);
    }

    @org.junit.Test
    public void testRestTemplateNotNull() {
        assertNotNull(template);
    }

    @org.junit.Test
    public void testBadUriNotNull() {
        assertNotNull(badUrl);
        assertThat(badUrl instanceof String);
    }

    @org.junit.Test
    public void testGoodUriNotNull() {
        assertNotNull(goodUrl);
        assertThat(goodUrl instanceof String);
    }

    @org.junit.Test
    public void getUri() throws DequeuedMessageWasNotEmailedException {
        assertThat(consumerImpl.getUri(goodUrl) instanceof URI);
        assertNotNull(consumerImpl.getUri(goodUrl));
    }

    @org.junit.Test(expected = DequeuedMessageWasNotEmailedException.class)
    public void testBadUrlProvidedToGetUri() throws DequeuedMessageWasNotEmailedException {
        assertThat(consumerImpl.getUri(badUrl) instanceof URI);
        assertNull(consumerImpl.getUri(badUrl));
    }

    @Test
    public void receive() throws RestClientException, URISyntaxException {

        UriTemplateHandler uriTemplate = new DefaultUriBuilderFactory();
        String uriExpanded = uriTemplate.expand(goodUrl).toString();

        server.expect(ExpectedCount.manyTimes(), requestTo(uriExpanded))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string("{\"email\":\"alice@lostboyz.com\",\"code\":\"123456\"}"))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .location(new URI("http://localhost:14000/api/v1/email"))
                        .body("{\n" +
                                "  \"email\": \"alice@lostboyz.com\",\n" +
                                "  \"code\": \"123456\",\n" +
                                "  \"_links\": {\n" +
                                "    \"self\": {\n" +
                                "      \"href\": \"http://localhost:14000/api/v1/email\"\n" +
                                "    }\n" +
                                "  }\n" +
                                "}")
                        .contentType(new MediaType("application", "hal+json")));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);;

        UserEmailTemplate uet = new UserEmailTemplate();
        uet.setEmail("alice@lostboyz.com");
        uet.setCode("123456");

        ResponseEntity<UserEmailTemplate> userEmailTemplate = template.postForEntity(new URI(goodUrl),
                new HttpEntity<>(uet, headers),
                UserEmailTemplate.class);

        assertNotNull(userEmailTemplate);
        assertThat(userEmailTemplate instanceof ResponseEntity);
        assertEquals(userEmailTemplate.getStatusCode(), HttpStatus.CREATED);
        assertEquals(userEmailTemplate.getHeaders().getContentType(), new MediaType("application", "hal+json"));
        assertEquals(userEmailTemplate.getHeaders().getLocation(), new URI("http://localhost:14000/api/v1/email"));

        server.verify();
    }

}