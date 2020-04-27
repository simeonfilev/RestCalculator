package com.sap.acad.rest_calculator;

import com.sap.acad.calculator.Calculator;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RestCalculatorServletTest {

    private static Calculator calculator;
    private static HttpServletResponse response;
    private static HttpServletRequest request;
    private static RestCalculatorServlet restServlet;
    private static String outputJSON;

    @BeforeAll
    static void init() throws IOException {
        calculator = new Calculator();
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        restServlet = new RestCalculatorServlet();
        outputJSON = "";
        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }

            @Override
            public void write(int b) {
                outputJSON += (char) b;
            }
        };
        Mockito.when(response.getOutputStream()).thenReturn(servletOutputStream);
    }

    @AfterEach
    public void clear() {
        outputJSON = "";
    }

    @Test
    public void returnsJSONToServletGetRequest() throws IOException {
        String expressionToCalculate = "5+2*3";
        Mockito.when(request.getParameter("expression")).thenReturn(expressionToCalculate);
        restServlet.doGet(request, response);
        Assertions.assertTrue(isValidJSON(outputJSON), "Does not return JSON");
    }

    @Test
    public void returnsCorrectAnswerToExpression() throws IOException {
        String expressionToCalculate = "5+2*3";
        Mockito.when(request.getParameter("expression")).thenReturn(expressionToCalculate);
        restServlet.doGet(request, response);
        Double expectedAnswerToExpression = calculator.calculate(expressionToCalculate);
        JSONObject json = new JSONObject(outputJSON);
        Assertions.assertEquals(expectedAnswerToExpression, json.getDouble("answer"));
    }

    @Test
    public void returnsBadRequestToEmptyExpression() throws IOException {
        Mockito.when(request.getParameter("expression")).thenReturn("");
        restServlet.doGet(request, response);
        Assertions.assertFalse(isValidJSON(outputJSON));
    }

    private boolean isValidJSON(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            return false;
        }
        return true;
    }

}
