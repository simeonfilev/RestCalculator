package com.sap.acad.rest_calculator;

import com.sap.acad.calculator.Calculator;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import static org.junit.Assert.assertEquals;

public class RestCalculatorServletTest extends JerseyTest {

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @AfterEach
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Override
    protected Application configure() {
        calculator = new Calculator();
        return new ResourceConfig(RESTCalculator.class);
    }

    private static Calculator calculator;

    private boolean isValidJSON(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }

    @Test
    public void returnCorrectJSONAnswerToExpressionToServletGetRequest() {
        String expression = "5+2*3";
        var req = target("/expressions/").queryParam("expression", expression);
        Response response = req.request().post(Entity.text(""));
        String json = response.readEntity(String.class);
        Assertions.assertTrue(isValidJSON(json), "Does not return valid JSON");
        Double expectedAnswerToExpression = calculator.calculate(expression);
        JSONObject jsonObj = new JSONObject(json);
        Assertions.assertEquals(expectedAnswerToExpression, jsonObj.getDouble("answer"));
        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Content type should be JSON: ", MediaType.APPLICATION_JSON, response.getMediaType().toString());
    }

    @Test
    public void returnsBadRequestToEmptyOrNullExpression() {
        String expression = "";
        var req = target("/expressions/").queryParam("expression", expression);
        Response response = req.request().post(Entity.text(""));
        assertEquals("Http Response should be 400: ", Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }
}
