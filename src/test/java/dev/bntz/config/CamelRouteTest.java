package dev.bntz.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import dev.bntz.models.User;
import dev.bntz.rest.json.BooleanDataContainer;

import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class CamelRouteTest {

    
    @Test
    void procesorShuldReturnSuccessIfInformationIsValid() throws Exception {
        // Arrange
        var user = new User();
        user.setEmail("valid@rmail.com");
        user.setWhatsApp("+1 (829) 598-9363");
        var route =  new CamelRoute();

        var processor = route.getUserProcessor();
        var exchange =  ExchangeBuilder
            .anExchange(new DefaultCamelContext())
            .withBody(user)
            .build();

        // Act
        processor.process(exchange);
        var body  = (BooleanDataContainer<User>) exchange.getMessage().getBody();

        // Assert
        assertTrue(body.isSuccess());
    }


    @Test
    void procesorShuldReturnFailureIfEmailIsValid() throws Exception {
        // Arrange
        var route =  new CamelRoute();
        var user = new User();
        user.setEmail("validrmail.com");
        user.setWhatsApp("+1 (829) 598-9363");

        var processor = route.getUserProcessor();
        var exchange =  ExchangeBuilder
            .anExchange(new DefaultCamelContext())
            .withBody(user)
            .build();

        // Act
        processor.process(exchange);
        var body  = (BooleanDataContainer<String>) exchange.getMessage().getBody();

        // Assert
        assertFalse(body.isSuccess());
        assertEquals("Invalid email", body.getData());

    }

    @Test
    void procesorShuldReturnFailureIfPhoneIsValid() throws Exception {
        // Arrange
        var route =  new CamelRoute();
        var user = new User();
        user.setEmail("valid@rmail.com");
        user.setWhatsApp("18295989363");

        var processor = route.getUserProcessor();
        var exchange =  ExchangeBuilder
            .anExchange(new DefaultCamelContext())
            .withBody(user)
            .build();

        // Act
        processor.process(exchange);
        var body  = (BooleanDataContainer<String>) exchange.getMessage().getBody();

        // Assert
        assertFalse(body.isSuccess());
        assertEquals("Invalid Whastapp", body.getData());
    }
}
