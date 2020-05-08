package com.sap.acad.rest_calculator;

import com.sap.acad.calculator.Calculator;
import com.sap.acad.rest_calculator.models.Expression;
import com.sap.acad.rest_calculator.service.MySQLConnectionImpl;
import com.sap.acad.rest_calculator.service.MySQLDatabase;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import java.sql.SQLException;
import java.sql.Statement;

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
    public void returnCorrectJSONToHistoryRequest() {
        var req = target("/expressions/");
        Response response = req.request().get();
        String json = response.readEntity(String.class);
        Assertions.assertTrue(isValidJSON(json), "Does not return valid JSON");
        assertEquals("Http Response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Content type should be JSON: ", MediaType.APPLICATION_JSON, response.getMediaType().toString());
    }

    @Test
    public void addsCorrectlyExpressionToDatabase() throws SQLException {
        var req = target("/expressions/");
        Response response = req.request().get();
        String json = response.readEntity(String.class);
        JSONObject jsonObject = new JSONObject(json);
        JSONArray arr = (JSONArray) jsonObject.get("expressions");
        int size = arr.toList().size();

        MySQLConnectionImpl mySQLConnection = new MySQLConnectionImpl();
        mySQLConnection.saveExpression(new Expression("5+3",8.0));

        Response newResponse = req.request().get();
        String newJson = newResponse.readEntity(String.class);
        JSONObject newJsonObject = new JSONObject(newJson);
        JSONArray newArr = (JSONArray) newJsonObject.get("expressions");
        int newSize = newArr.toList().size();

        Assertions.assertTrue(size != newSize,"Didn't add expression to db");
        String objForDelete = newArr.toList().get(newSize-1).toString();
        int id = extractIdFromString(objForDelete);
        mySQLConnection.deleteExpressionById(id);

    }
    public int extractIdFromString(String objForDelete){
        int idIndex = objForDelete.indexOf("id=");
        String idStr = objForDelete.substring(idIndex+"id=".length());
        int counter =0;
        while (Character.isDigit(idStr.charAt(counter))) {
            counter++;
        }
        return Integer.parseInt(idStr.substring(0,counter));
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
