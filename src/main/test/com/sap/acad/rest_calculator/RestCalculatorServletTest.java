package com.sap.acad.rest_calculator;

import com.sap.acad.calculator.Calculator;
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
            public void write(int b) throws IOException {
                outputJSON += (char) b;
            }
        };
        Mockito.when(response.getOutputStream()).thenReturn(servletOutputStream);
    }

    @AfterEach
    public void clear() {
        outputJSON = "";
    }

    public String removeJSONSpacings(String json) {
        json = json.replaceAll("\n", "")
                .replaceAll("\r", "")
                .replaceAll("\"", "");
        return json;
    }

    public String getAnswerParameterFromJSON() {
        String answerFromServletRequest = removeJSONSpacings(outputJSON.substring(outputJSON.indexOf("answer: ") + "answer: ".length()));
        String answerToExpressionFromServletRequest = answerFromServletRequest.replaceAll(",", "")
                .replaceAll("}", "");
        return answerToExpressionFromServletRequest;
    }

    @Test
    public void returnsJSONToServletRequest() throws IOException {
        String expressionToCalculate = "5+2*3";
        Mockito.when(request.getParameter("expression")).thenReturn(expressionToCalculate);
        restServlet.doGet(request, response);
        outputJSON = removeJSONSpacings(outputJSON);
        boolean correctJSONFormat = outputJSON.charAt(0) == '{' && outputJSON.charAt(outputJSON.length() - 1) == '}';
        Assertions.assertTrue(correctJSONFormat);
    }

    @Test
    public void returnsCorrectAnswerToExpression() throws IOException {
        String expressionToCalculate = "5+2*3";
        Double expectedAnswerToExpression = calculator.calculate(expressionToCalculate);
        Mockito.when(request.getParameter("expression")).thenReturn(expressionToCalculate);
        restServlet.doGet(request, response);
        outputJSON = removeJSONSpacings(outputJSON);
        String answerFromServlet = getAnswerParameterFromJSON();
        boolean correctAnswerToExpression = answerFromServlet.equals(String.valueOf(expectedAnswerToExpression));
        Assertions.assertTrue(correctAnswerToExpression, "Wrong answer!");
    }

    @Test
    public void returnsCorrectAnswerToEmptyExpression() throws IOException {
        String expressionToCalculate = "";
        Mockito.when(request.getParameter("expression")).thenReturn(expressionToCalculate);
        restServlet.doGet(request, response);
        outputJSON = removeJSONSpacings(outputJSON);
        boolean correctAnswerToExpression = outputJSON.equals(String.valueOf("{}"));
        Assertions.assertTrue(correctAnswerToExpression, "Not empty!");
    }
}
